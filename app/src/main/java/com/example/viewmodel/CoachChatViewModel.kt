package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CoachPersona
import com.example.data.MlbbAiRepository
import com.example.i18n.AppLanguage
import com.example.i18n.stringsFor
import com.example.model.CoachChatMessage
import com.google.firebase.ai.Chat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

private const val TAG = "CoachChatViewModel"

data class CoachChatUiState(
    val selectedPersona: CoachPersona = CoachPersona.GENERAL_COACH,
    val messages: List<CoachChatMessage> = listOf(
        CoachChatMessage(
            id = "greeting_init",
            isUser = false,
            text = CoachPersona.GENERAL_COACH.greeting(AppLanguage.ENGLISH)
        )
    ),
    val isResponding: Boolean = false,
    val errorMessage: String? = null
)

class CoachChatViewModel(
    private val repository: MlbbAiRepository = MlbbAiRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoachChatUiState())
    val uiState: StateFlow<CoachChatUiState> = _uiState.asStateFlow()

    private var activeChat: Chat? = null
    private var chatPersona: CoachPersona? = null
    private var lastSyncedLanguage: AppLanguage? = null
    private var streamJob: Job? = null

    fun syncLanguage(language: AppLanguage) {
        if (lastSyncedLanguage == language) return
        lastSyncedLanguage = language
        val current = _uiState.value
        val hasUserMessages = current.messages.any { it.isUser }
        if (!hasUserMessages && !current.isResponding) {
            activeChat = null
            chatPersona = null
            _uiState.update { state ->
                state.copy(
                    messages = listOf(
                        CoachChatMessage(
                            id = "greeting_${language.code}_${state.selectedPersona.name}",
                            isUser = false,
                            text = state.selectedPersona.greeting(language)
                        )
                    ),
                    errorMessage = null
                )
            }
        }
    }

    fun selectPersona(persona: CoachPersona, language: AppLanguage) {
        if (_uiState.value.selectedPersona == persona) return
        streamJob?.cancel()
        activeChat = null
        chatPersona = null
        lastSyncedLanguage = language
        _uiState.value = CoachChatUiState(
            selectedPersona = persona,
            messages = listOf(
                CoachChatMessage(
                    id = UUID.randomUUID().toString(),
                    isUser = false,
                    text = persona.greeting(language)
                )
            )
        )
    }

    fun resetConversation(language: AppLanguage) {
        val currentPersona = _uiState.value.selectedPersona
        streamJob?.cancel()
        activeChat = null
        chatPersona = null
        lastSyncedLanguage = language
        _uiState.value = CoachChatUiState(
            selectedPersona = currentPersona,
            messages = listOf(
                CoachChatMessage(
                    id = UUID.randomUUID().toString(),
                    isUser = false,
                    text = currentPersona.greeting(language)
                )
            )
        )
    }

    fun sendMessage(rawText: String, language: AppLanguage) {
        val trimmed = rawText.trim()
        if (trimmed.isBlank() || _uiState.value.isResponding) return

        val localizedStrings = stringsFor(language)
        val persona = _uiState.value.selectedPersona
        val chat = if (activeChat != null && chatPersona == persona) {
            activeChat!!
        } else {
            repository.createPersonaCoachChat(persona, language).also {
                activeChat = it
                chatPersona = persona
            }
        }

        val userMessage = CoachChatMessage(
            id = UUID.randomUUID().toString(),
            isUser = true,
            text = trimmed
        )
        val aiMsgId = UUID.randomUUID().toString()
        val aiPlaceholder = CoachChatMessage(
            id = aiMsgId,
            isUser = false,
            text = "",
            isStreaming = true
        )

        streamJob?.cancel()
        streamJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    messages = it.messages + userMessage + aiPlaceholder,
                    isResponding = true,
                    errorMessage = null
                )
            }

            val accumulated = StringBuilder()
            try {
                repository.sendChatMessageStream(chat, trimmed, language).collect { chunk ->
                    accumulated.append(chunk)
                    val currentText = accumulated.toString()
                    _uiState.update { state ->
                        state.copy(
                            messages = state.messages.map { msg ->
                                if (msg.id == aiMsgId) {
                                    msg.copy(text = currentText, isStreaming = true)
                                } else {
                                    msg
                                }
                            }
                        )
                    }
                }
                val finalText = accumulated.toString().ifBlank {
                    localizedStrings.errorEmptyResponse
                }
                _uiState.update { state ->
                    state.copy(
                        isResponding = false,
                        messages = state.messages.map { msg ->
                            if (msg.id == aiMsgId) {
                                msg.copy(text = finalText, isStreaming = false)
                            } else {
                                msg
                            }
                        }
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Coach chat failed (${language.code})", e)
                _uiState.update { state ->
                    state.copy(
                        isResponding = false,
                        errorMessage = e.localizedMessage ?: localizedStrings.errorGeneric,
                        messages = state.messages
                            .filterNot { it.id == aiMsgId && it.text.isBlank() }
                            .map { msg ->
                                if (msg.id == aiMsgId) msg.copy(isStreaming = false) else msg
                            }
                    )
                }
            }
        }
    }
}
