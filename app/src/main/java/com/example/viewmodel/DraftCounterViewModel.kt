package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MlbbAiRepository
import com.example.i18n.AppLanguage
import com.example.i18n.stringsFor
import com.example.model.AsyncUiState
import com.example.model.DraftCounterResult
import com.example.model.MlbbHero
import com.example.model.MlbbRole
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "DraftCounterViewModel"
const val MAX_ENEMY_HEROES = 5

data class DraftCounterUiState(
    val enemyHeroes: List<MlbbHero> = emptyList(),
    val preferredRole: MlbbRole = MlbbRole.FIGHTER,
    val resultState: AsyncUiState<DraftCounterResult> = AsyncUiState.Idle,
    val resultLanguage: AppLanguage? = null
)

class DraftCounterViewModel(
    private val repository: MlbbAiRepository = MlbbAiRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DraftCounterUiState())
    val uiState: StateFlow<DraftCounterUiState> = _uiState.asStateFlow()

    private var analyzeJob: Job? = null

    fun addEnemyHero(hero: MlbbHero) {
        _uiState.update { state ->
            if (state.enemyHeroes.size >= MAX_ENEMY_HEROES || state.enemyHeroes.any { it.id == hero.id }) {
                state
            } else {
                state.copy(enemyHeroes = state.enemyHeroes + hero)
            }
        }
    }

    fun removeEnemyHero(heroId: String) {
        _uiState.update { state ->
            state.copy(enemyHeroes = state.enemyHeroes.filterNot { it.id == heroId })
        }
    }

    fun clearEnemyHeroes() {
        analyzeJob?.cancel()
        _uiState.update {
            it.copy(
                enemyHeroes = emptyList(),
                resultState = AsyncUiState.Idle,
                resultLanguage = null
            )
        }
    }

    fun selectPreferredRole(role: MlbbRole) {
        _uiState.update { it.copy(preferredRole = role) }
    }

    fun analyzeDraft(language: AppLanguage) {
        val current = _uiState.value
        if (current.enemyHeroes.isEmpty()) return

        val localizedStrings = stringsFor(language)
        analyzeJob?.cancel()
        analyzeJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    resultState = AsyncUiState.Loading,
                    resultLanguage = language
                )
            }
            try {
                val enemyNames = current.enemyHeroes.map { it.nameEn }
                val result = repository.fetchDraftCounters(
                    enemyHeroes = enemyNames,
                    preferredRole = current.preferredRole,
                    language = language
                )
                _uiState.update {
                    it.copy(
                        resultState = AsyncUiState.Success(result),
                        resultLanguage = language
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Draft counter analysis failed (${language.code})", e)
                _uiState.update {
                    it.copy(
                        resultState = AsyncUiState.Error(
                            e.localizedMessage ?: localizedStrings.errorGeneric
                        )
                    )
                }
            }
        }
    }

    fun onLanguageChanged(language: AppLanguage) {
        val current = _uiState.value
        val hasResult = current.resultState is AsyncUiState.Success || current.resultState is AsyncUiState.Error
        if (hasResult && current.enemyHeroes.isNotEmpty() && current.resultLanguage != language) {
            analyzeDraft(language)
        }
    }
}
