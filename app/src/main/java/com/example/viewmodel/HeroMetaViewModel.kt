package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MlbbAiRepository
import com.example.i18n.AppLanguage
import com.example.i18n.stringsFor
import com.example.model.HeroMetaGuide
import com.example.model.HeroQuickCheckSummary
import com.example.model.MlbbHero
import com.example.model.MlbbLane
import com.example.model.MlbbRole
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

private const val TAG = "HeroMetaViewModel"
private const val META_TIMEOUT_MS = 60_000L
private const val CACHE_TTL_MS = 15 * 60 * 1000L

sealed interface HeroMetaUiState {
    data object Idle : HeroMetaUiState
    data class Loading(
        val hero: MlbbHero,
        val language: AppLanguage
    ) : HeroMetaUiState

    data class Success(
        val hero: MlbbHero,
        val guide: HeroMetaGuide,
        val language: AppLanguage,
        val isFromCache: Boolean = false
    ) : HeroMetaUiState

    data class Error(
        val hero: MlbbHero,
        val message: String,
        val language: AppLanguage
    ) : HeroMetaUiState
}

sealed interface HeroQuickCheckUiState {
    data object Idle : HeroQuickCheckUiState
    data class Loading(
        val hero: MlbbHero,
        val language: AppLanguage
    ) : HeroQuickCheckUiState

    data class Success(
        val hero: MlbbHero,
        val summary: HeroQuickCheckSummary,
        val language: AppLanguage
    ) : HeroQuickCheckUiState

    data class Error(
        val hero: MlbbHero,
        val message: String,
        val language: AppLanguage
    ) : HeroQuickCheckUiState
}

private data class CachedHeroMeta(
    val guide: HeroMetaGuide,
    val fetchedAtMillis: Long
)

private data class CachedQuickCheck(
    val summary: HeroQuickCheckSummary,
    val fetchedAtMillis: Long
)

/**
 * Dedicated MVVM ViewModel for securely fetching live Mobile Legends: Bang Bang hero meta data
 * from Gemini via Firebase AI Logic SDK, protected by Firebase App Check and backed by
 * Google Search Grounding. Cache keys include active [AppLanguage] so switching languages
 * immediately fetches or serves meta data in the newly selected language.
 */
class HeroMetaViewModel(
    private val aiRepository: MlbbAiRepository = MlbbAiRepository()
) : ViewModel() {

    private val _activeHero = MutableStateFlow<MlbbHero?>(null)
    val activeHero: StateFlow<MlbbHero?> = _activeHero.asStateFlow()

    private val _uiState = MutableStateFlow<HeroMetaUiState>(HeroMetaUiState.Idle)
    val uiState: StateFlow<HeroMetaUiState> = _uiState.asStateFlow()

    private val _sharingHeroId = MutableStateFlow<String?>(null)
    val sharingHeroId: StateFlow<String?> = _sharingHeroId.asStateFlow()

    private val _quickCheckHero = MutableStateFlow<MlbbHero?>(null)
    val quickCheckHero: StateFlow<MlbbHero?> = _quickCheckHero.asStateFlow()

    private val _quickCheckState = MutableStateFlow<HeroQuickCheckUiState>(HeroQuickCheckUiState.Idle)
    val quickCheckState: StateFlow<HeroQuickCheckUiState> = _quickCheckState.asStateFlow()

    private val metaCache = mutableMapOf<String, CachedHeroMeta>()
    private val quickCheckCache = mutableMapOf<String, CachedQuickCheck>()
    private var activeFetchJob: Job? = null
    private var shareFetchJob: Job? = null
    private var quickCheckJob: Job? = null

    fun openQuickCheck(
        hero: MlbbHero,
        language: AppLanguage,
        forceRefresh: Boolean = false
    ) {
        _quickCheckHero.value = hero
        val cacheKey = "${hero.id}_${language.code}"
        val now = System.currentTimeMillis()

        // 1. Instant path: synthesize from fresh full meta guide cache if already loaded
        if (!forceRefresh) {
            val cachedMeta = metaCache[cacheKey]
            if (cachedMeta != null && (now - cachedMeta.fetchedAtMillis) < CACHE_TTL_MS) {
                val derived = deriveQuickCheckFromGuide(cachedMeta.guide)
                if (derived != null) {
                    _quickCheckState.value = HeroQuickCheckUiState.Success(
                        hero = hero,
                        summary = derived,
                        language = language
                    )
                    return
                }
            }

            // 2. Instant path: reuse cached quick-check summary
            val cachedQuick = quickCheckCache[cacheKey]
            if (cachedQuick != null && (now - cachedQuick.fetchedAtMillis) < CACHE_TTL_MS) {
                _quickCheckState.value = HeroQuickCheckUiState.Success(
                    hero = hero,
                    summary = cachedQuick.summary,
                    language = language
                )
                return
            }
        }

        // 3. Fast Gemini Flash-Lite fetch
        val localizedStrings = stringsFor(language)
        quickCheckJob?.cancel()
        quickCheckJob = viewModelScope.launch {
            _quickCheckState.value = HeroQuickCheckUiState.Loading(hero, language)
            try {
                val summary = aiRepository.fetchHeroQuickCheck(
                    heroNameEn = hero.nameEn,
                    language = language
                )
                quickCheckCache[cacheKey] = CachedQuickCheck(
                    summary = summary,
                    fetchedAtMillis = System.currentTimeMillis()
                )
                if (_quickCheckHero.value?.id == hero.id) {
                    _quickCheckState.value = HeroQuickCheckUiState.Success(
                        hero = hero,
                        summary = summary,
                        language = language
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Quick-check failed for ${hero.nameEn} (${language.code})", e)
                if (_quickCheckHero.value?.id == hero.id) {
                    _quickCheckState.value = HeroQuickCheckUiState.Error(
                        hero = hero,
                        message = e.localizedMessage ?: localizedStrings.errorGeneric,
                        language = language
                    )
                }
            }
        }
    }

    fun dismissQuickCheck() {
        quickCheckJob?.cancel()
        _quickCheckHero.value = null
        _quickCheckState.value = HeroQuickCheckUiState.Idle
    }

    fun openFullGuideFromQuickCheck(hero: MlbbHero, language: AppLanguage) {
        dismissQuickCheck()
        loadHeroMeta(hero = hero, language = language)
    }

    private fun deriveQuickCheckFromGuide(guide: HeroMetaGuide): HeroQuickCheckSummary? {
        if (guide.rawFallbackFa != null) return null
        val coreItem = guide.coreItems.firstOrNull() ?: return null
        val comboText = guide.skillComboSteps.joinToString(" → ")
            .ifBlank { return null }
        val counterRaw = guide.counters.firstOrNull() ?: return null
        val counterParts = counterRaw.split("|", limit = 2)
        val counterHero = counterParts[0].trim()
        val counterNote = counterParts.getOrNull(1)?.trim().orEmpty()

        return HeroQuickCheckSummary(
            tier = guide.tier,
            coreItemName = coreItem.name,
            coreItemNote = coreItem.reasonFa,
            mainCombo = comboText,
            mainComboNote = guide.comboExplanationFa.lines().firstOrNull()?.trim().orEmpty(),
            primaryCounter = counterHero,
            primaryCounterNote = counterNote
        )
    }

    fun prepareHeroGuideForShare(
        hero: MlbbHero,
        language: AppLanguage,
        onReady: (HeroMetaGuide?) -> Unit
    ) {
        val cacheKey = "${hero.id}_${language.code}"
        val cached = metaCache[cacheKey]
        val isCacheFresh = cached != null &&
            (System.currentTimeMillis() - cached.fetchedAtMillis) < CACHE_TTL_MS

        if (isCacheFresh) {
            onReady(cached!!.guide)
            return
        }

        shareFetchJob?.cancel()
        shareFetchJob = viewModelScope.launch {
            _sharingHeroId.value = hero.id
            val resolvedGuide = try {
                withTimeout(META_TIMEOUT_MS) {
                    aiRepository.fetchHeroMetaGuide(
                        heroNameEn = hero.nameEn,
                        heroNameFa = hero.nameFa,
                        language = language
                    )
                }.also { fetched ->
                    metaCache[cacheKey] = CachedHeroMeta(
                        guide = fetched,
                        fetchedAtMillis = System.currentTimeMillis()
                    )
                }
            } catch (e: CancellationException) {
                _sharingHeroId.value = null
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Share fetch fell back to hero summary for ${hero.nameEn}", e)
                null
            }
            _sharingHeroId.value = null
            onReady(resolvedGuide)
        }
    }

    fun loadHeroMeta(
        hero: MlbbHero,
        language: AppLanguage,
        forceRefresh: Boolean = false
    ) {
        _activeHero.value = hero

        val cacheKey = "${hero.id}_${language.code}"
        val cached = metaCache[cacheKey]
        val isCacheFresh = cached != null &&
            (System.currentTimeMillis() - cached.fetchedAtMillis) < CACHE_TTL_MS

        if (!forceRefresh && isCacheFresh) {
            _uiState.value = HeroMetaUiState.Success(
                hero = hero,
                guide = cached!!.guide,
                language = language,
                isFromCache = true
            )
            return
        }

        val localizedStrings = stringsFor(language)
        activeFetchJob?.cancel()
        activeFetchJob = viewModelScope.launch {
            _uiState.value = HeroMetaUiState.Loading(hero, language)
            try {
                val guide = withTimeout(META_TIMEOUT_MS) {
                    aiRepository.fetchHeroMetaGuide(
                        heroNameEn = hero.nameEn,
                        heroNameFa = hero.nameFa,
                        language = language
                    )
                }
                metaCache[cacheKey] = CachedHeroMeta(
                    guide = guide,
                    fetchedAtMillis = System.currentTimeMillis()
                )
                _uiState.value = HeroMetaUiState.Success(
                    hero = hero,
                    guide = guide,
                    language = language,
                    isFromCache = false
                )
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Hero meta request timed out for ${hero.nameEn} (${language.code})", e)
                _uiState.value = HeroMetaUiState.Error(
                    hero = hero,
                    message = localizedStrings.errorTimeout,
                    language = language
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load hero meta for ${hero.nameEn} (${language.code})", e)
                _uiState.value = HeroMetaUiState.Error(
                    hero = hero,
                    message = e.localizedMessage ?: localizedStrings.errorGeneric,
                    language = language
                )
            }
        }
    }

    fun onLanguageChanged(language: AppLanguage) {
        val hero = _activeHero.value ?: return
        val currentState = _uiState.value
        val alreadyMatchesLanguage = when (currentState) {
            is HeroMetaUiState.Loading -> currentState.language == language
            is HeroMetaUiState.Success -> currentState.language == language
            is HeroMetaUiState.Error -> currentState.language == language
            HeroMetaUiState.Idle -> true
        }
        if (!alreadyMatchesLanguage) {
            loadHeroMeta(hero = hero, language = language, forceRefresh = false)
        }
    }

    fun loadCustomHeroMeta(customName: String, language: AppLanguage) {
        val trimmed = customName.trim()
        if (trimmed.isBlank()) return
        val customHero = MlbbHero(
            id = "custom_${trimmed.lowercase()}",
            nameEn = trimmed,
            nameFa = trimmed,
            primaryRole = MlbbRole.FIGHTER,
            lanes = listOf(MlbbLane.EXP),
            specialtyFa = trimmed
        )
        loadHeroMeta(hero = customHero, language = language, forceRefresh = true)
    }

    fun refreshActiveHero(language: AppLanguage) {
        val hero = _activeHero.value ?: return
        loadHeroMeta(hero = hero, language = language, forceRefresh = true)
    }

    fun clearActiveHero() {
        activeFetchJob?.cancel()
        _activeHero.update { null }
        _uiState.value = HeroMetaUiState.Idle
    }
}
