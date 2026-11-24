package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.FavoriteHeroesStore
import com.example.data.MlbbHeroCatalog
import com.example.model.MlbbHero
import com.example.model.MlbbLane
import com.example.model.MlbbRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HeroGuideUiState(
    val searchQuery: String = "",
    val selectedRole: MlbbRole? = null,
    val selectedLane: MlbbLane? = null,
    val isGridView: Boolean = true,
    val heroes: List<MlbbHero> = MlbbHeroCatalog.heroes,
    val favoriteHeroIds: Set<String> = emptySet(),
    val favoriteHeroes: List<MlbbHero> = emptyList()
) {
    val hasActiveFilters: Boolean
        get() = searchQuery.isNotBlank() || selectedRole != null || selectedLane != null
}

class HeroGuideViewModel(application: Application) : AndroidViewModel(application) {

    private val favoritesStore = FavoriteHeroesStore(application)
    private var orderedFavoriteIds: List<String> = favoritesStore.loadFavoriteIds()

    private val _uiState = MutableStateFlow(
        HeroGuideUiState(
            favoriteHeroIds = orderedFavoriteIds.toSet(),
            favoriteHeroes = resolveFavoriteHeroes(orderedFavoriteIds)
        )
    )
    val uiState: StateFlow<HeroGuideUiState> = _uiState.asStateFlow()

    fun toggleFavorite(heroId: String) {
        orderedFavoriteIds = if (heroId in orderedFavoriteIds) {
            orderedFavoriteIds.filterNot { it == heroId }
        } else {
            listOf(heroId) + orderedFavoriteIds
        }
        favoritesStore.saveFavoriteIds(orderedFavoriteIds)
        _uiState.update { state ->
            state.copy(
                favoriteHeroIds = orderedFavoriteIds.toSet(),
                favoriteHeroes = resolveFavoriteHeroes(orderedFavoriteIds)
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                heroes = MlbbHeroCatalog.filterHeroes(query, state.selectedRole, state.selectedLane)
            )
        }
    }

    fun onRoleSelected(role: MlbbRole?) {
        _uiState.update { state ->
            val nextRole = if (state.selectedRole == role) null else role
            state.copy(
                selectedRole = nextRole,
                heroes = MlbbHeroCatalog.filterHeroes(state.searchQuery, nextRole, state.selectedLane)
            )
        }
    }

    fun onLaneSelected(lane: MlbbLane?) {
        _uiState.update { state ->
            val nextLane = if (state.selectedLane == lane) null else lane
            state.copy(
                selectedLane = nextLane,
                heroes = MlbbHeroCatalog.filterHeroes(state.searchQuery, state.selectedRole, nextLane)
            )
        }
    }

    fun clearFilters() {
        _uiState.update { state ->
            state.copy(
                searchQuery = "",
                selectedRole = null,
                selectedLane = null,
                heroes = MlbbHeroCatalog.heroes
            )
        }
    }

    fun setGridView(isGrid: Boolean) {
        _uiState.update { it.copy(isGridView = isGrid) }
    }

    private fun resolveFavoriteHeroes(ids: List<String>): List<MlbbHero> {
        val heroById = MlbbHeroCatalog.heroes.associateBy { it.id }
        return ids.mapNotNull { heroById[it] }
    }
}
