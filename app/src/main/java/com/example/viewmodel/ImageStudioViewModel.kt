package com.example.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MlbbAiRepository
import com.example.i18n.AppLanguage
import com.example.i18n.stringsFor
import com.example.model.AsyncUiState
import com.example.util.BitmapUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ImageStudioViewModel"

data class ImageStudioUiState(
    val prompt: String = "",
    val sourceBitmap: Bitmap? = null,
    val isDecodingSource: Boolean = false,
    val generationState: AsyncUiState<Bitmap> = AsyncUiState.Idle
)

class ImageStudioViewModel(
    private val repository: MlbbAiRepository = MlbbAiRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImageStudioUiState())
    val uiState: StateFlow<ImageStudioUiState> = _uiState.asStateFlow()

    private var generateJob: Job? = null

    fun onPromptChange(value: String) {
        _uiState.update { it.copy(prompt = value) }
    }

    fun onSourceImagePicked(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDecodingSource = true) }
            val decoded = BitmapUtils.decodeScaledBitmap(context.applicationContext, uri)
            _uiState.update {
                it.copy(
                    sourceBitmap = decoded,
                    isDecodingSource = false
                )
            }
        }
    }

    fun useGeneratedAsSource(bitmap: Bitmap) {
        _uiState.update {
            it.copy(
                sourceBitmap = bitmap,
                prompt = ""
            )
        }
    }

    fun clearSourceImage() {
        _uiState.update { it.copy(sourceBitmap = null) }
    }

    fun generateOrEdit(language: AppLanguage) {
        val current = _uiState.value
        val trimmedPrompt = current.prompt.trim()
        if (trimmedPrompt.isBlank()) return

        val localizedStrings = stringsFor(language)
        generateJob?.cancel()
        generateJob = viewModelScope.launch {
            _uiState.update { it.copy(generationState = AsyncUiState.Loading) }
            try {
                val bitmap = repository.generateOrEditImage(
                    prompt = trimmedPrompt,
                    sourceBitmap = current.sourceBitmap,
                    language = language
                )
                _uiState.update { it.copy(generationState = AsyncUiState.Success(bitmap)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Image generation failed (${language.code})", e)
                _uiState.update {
                    it.copy(
                        generationState = AsyncUiState.Error(
                            e.localizedMessage ?: localizedStrings.errorGeneric
                        )
                    )
                }
            }
        }
    }
}
