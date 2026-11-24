package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MatchHistoryStore
import com.example.data.MlbbAiRepository
import com.example.i18n.AppLanguage
import com.example.i18n.stringsFor
import com.example.model.AsyncUiState
import com.example.model.CoachChatMessage
import com.example.model.MatchAnalysisReport
import com.example.model.MatchHistoryEntry
import com.example.util.BitmapUtils
import com.google.firebase.ai.Chat
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

private const val TAG = "MatchAnalyzerViewModel"

data class MatchAnalyzerUiState(
    val selectedBitmap: Bitmap? = null,
    val isDecodingImage: Boolean = false,
    val imageDecodeError: String? = null,
    val heroPlayedInput: String = "",
    val analysisState: AsyncUiState<MatchAnalysisReport> = AsyncUiState.Idle,
    val chatMessages: List<CoachChatMessage> = emptyList(),
    val isChatResponding: Boolean = false,
    val chatError: String? = null,
    val historyEntries: List<MatchHistoryEntry> = emptyList(),
    val isLoadingHistory: Boolean = false,
    val activeHistoryEntryId: String? = null,
    val activeHistoryTimestamp: Long? = null
)

class MatchAnalyzerViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = MlbbAiRepository()
    private val historyStore = MatchHistoryStore(application)

    private val _uiState = MutableStateFlow(MatchAnalyzerUiState(isLoadingHistory = true))
    val uiState: StateFlow<MatchAnalyzerUiState> = _uiState.asStateFlow()

    private var activeChat: Chat? = null
    private var analyzeJob: Job? = null
    private var chatJob: Job? = null
    private var openHistoryJob: Job? = null

    init {
        viewModelScope.launch {
            val savedEntries = historyStore.loadEntries()
            _uiState.update {
                it.copy(
                    historyEntries = savedEntries,
                    isLoadingHistory = false
                )
            }
        }
    }

    suspend fun loadHistoryThumbnail(filePath: String): Bitmap? =
        historyStore.loadThumbnail(filePath)

    fun onScreenshotPicked(context: Context, uri: Uri, language: AppLanguage) {
        val localizedStrings = stringsFor(language)
        openHistoryJob?.cancel()
        analyzeJob?.cancel()
        chatJob?.cancel()

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDecodingImage = true,
                    imageDecodeError = null,
                    analysisState = AsyncUiState.Idle,
                    chatMessages = emptyList(),
                    chatError = null,
                    activeHistoryEntryId = null,
                    activeHistoryTimestamp = null
                )
            }
            activeChat = null
            val decoded = BitmapUtils.decodeScaledBitmap(context.applicationContext, uri)
            if (decoded != null) {
                _uiState.update {
                    it.copy(
                        selectedBitmap = decoded,
                        isDecodingImage = false,
                        imageDecodeError = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isDecodingImage = false,
                        imageDecodeError = localizedStrings.matchDecodeError
                    )
                }
            }
        }
    }

    fun onHeroPlayedChange(value: String) {
        _uiState.update { it.copy(heroPlayedInput = value) }
    }

    fun syncLanguage(language: AppLanguage) {
        val current = _uiState.value
        val report = (current.analysisState as? AsyncUiState.Success)?.data ?: return
        val hasUserMessages = current.chatMessages.any { it.isUser }
        if (!hasUserMessages && current.chatMessages.size == 1 && !current.isChatResponding) {
            val greeting = language.format(stringsFor(language).matchChatGreeting, report.heroPlayed)
            _uiState.update { state ->
                state.copy(
                    chatMessages = listOf(
                        state.chatMessages.first().copy(text = greeting)
                    )
                )
            }
        }
    }

    fun startNewAnalysis() {
        openHistoryJob?.cancel()
        analyzeJob?.cancel()
        chatJob?.cancel()
        activeChat = null
        _uiState.update {
            it.copy(
                selectedBitmap = null,
                isDecodingImage = false,
                imageDecodeError = null,
                heroPlayedInput = "",
                analysisState = AsyncUiState.Idle,
                chatMessages = emptyList(),
                isChatResponding = false,
                chatError = null,
                activeHistoryEntryId = null,
                activeHistoryTimestamp = null
            )
        }
    }

    fun openHistoryEntry(entry: MatchHistoryEntry, language: AppLanguage) {
        openHistoryJob?.cancel()
        analyzeJob?.cancel()
        chatJob?.cancel()

        val localizedStrings = stringsFor(language)
        val greeting = language.format(localizedStrings.matchChatGreeting, entry.report.heroPlayed)
        activeChat = repository.createMatchFollowUpChat(entry.report, language)

        _uiState.update {
            it.copy(
                selectedBitmap = null,
                isDecodingImage = true,
                imageDecodeError = null,
                heroPlayedInput = entry.report.heroPlayed,
                analysisState = AsyncUiState.Success(entry.report),
                chatMessages = listOf(
                    CoachChatMessage(
                        id = UUID.randomUUID().toString(),
                        isUser = false,
                        text = greeting
                    )
                ),
                isChatResponding = false,
                chatError = null,
                activeHistoryEntryId = entry.id,
                activeHistoryTimestamp = entry.analyzedAtMillis
            )
        }

        openHistoryJob = viewModelScope.launch {
            val bitmap = historyStore.loadFullBitmap(entry.screenshotFilePath)
            _uiState.update { current ->
                if (current.activeHistoryEntryId == entry.id) {
                    current.copy(
                        selectedBitmap = bitmap,
                        isDecodingImage = false
                    )
                } else {
                    current
                }
            }
        }
    }

    fun deleteHistoryEntry(entryId: String) {
        viewModelScope.launch {
            val updated = historyStore.deleteEntry(entryId)
            _uiState.update { current ->
                val clearedActive = current.activeHistoryEntryId == entryId
                current.copy(
                    historyEntries = updated,
                    activeHistoryEntryId = if (clearedActive) null else current.activeHistoryEntryId,
                    activeHistoryTimestamp = if (clearedActive) null else current.activeHistoryTimestamp
                )
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyStore.clearAll()
            _uiState.update { current ->
                current.copy(
                    historyEntries = emptyList(),
                    activeHistoryEntryId = null,
                    activeHistoryTimestamp = null
                )
            }
        }
    }

    fun analyzeMatch(language: AppLanguage) {
        val bitmap = _uiState.value.selectedBitmap ?: return
        val heroInput = _uiState.value.heroPlayedInput
        val localizedStrings = stringsFor(language)

        openHistoryJob?.cancel()
        analyzeJob?.cancel()
        chatJob?.cancel()
        activeChat = null

        analyzeJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    analysisState = AsyncUiState.Loading,
                    chatMessages = emptyList(),
                    chatError = null,
                    activeHistoryEntryId = null,
                    activeHistoryTimestamp = null
                )
            }
            try {
                val report = repository.analyzeMatchScreenshot(bitmap, heroInput, language)
                activeChat = repository.createMatchFollowUpChat(report, language)

                val savedEntry = try {
                    historyStore.saveEntry(bitmap, report, language.code)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to persist match analysis to history", e)
                    null
                }
                val refreshedHistory = if (savedEntry != null) {
                    historyStore.loadEntries()
                } else {
                    _uiState.value.historyEntries
                }

                _uiState.update {
                    it.copy(
                        analysisState = AsyncUiState.Success(report),
                        chatMessages = listOf(
                            CoachChatMessage(
                                id = UUID.randomUUID().toString(),
                                isUser = false,
                                text = language.format(localizedStrings.matchChatGreeting, report.heroPlayed)
                            )
                        ),
                        historyEntries = refreshedHistory,
                        activeHistoryEntryId = savedEntry?.id,
                        activeHistoryTimestamp = savedEntry?.analyzedAtMillis
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Match screenshot analysis failed (${language.code})", e)
                _uiState.update {
                    it.copy(
                        analysisState = AsyncUiState.Error(
                            e.localizedMessage ?: localizedStrings.errorGeneric
                        )
                    )
                }
            }
        }
    }

    fun sendFollowUpQuestion(question: String, language: AppLanguage) {
        val trimmed = question.trim()
        val chat = activeChat ?: return
        if (trimmed.isBlank() || _uiState.value.isChatResponding) return

        val localizedStrings = stringsFor(language)
        val userMsg = CoachChatMessage(
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

        chatJob?.cancel()
        chatJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    chatMessages = it.chatMessages + userMsg + aiPlaceholder,
                    isChatResponding = true,
                    chatError = null
                )
            }

            val accumulated = StringBuilder()
            try {
                repository.sendChatMessageStream(chat, trimmed, language).collect { chunk ->
                    accumulated.append(chunk)
                    val currentText = accumulated.toString()
                    _uiState.update { state ->
                        state.copy(
                            chatMessages = state.chatMessages.map { msg ->
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
                        isChatResponding = false,
                        chatMessages = state.chatMessages.map { msg ->
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
                Log.e(TAG, "Follow-up chat failed (${language.code})", e)
                _uiState.update { state ->
                    state.copy(
                        isChatResponding = false,
                        chatError = e.localizedMessage ?: localizedStrings.errorGeneric,
                        chatMessages = state.chatMessages
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
