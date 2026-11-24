package com.example.data

import android.content.Context

private const val PREFS_NAME = "mlbb_hero_coach_favorites"
private const val KEY_FAVORITE_HERO_IDS = "favorite_hero_ids_ordered"

/**
 * Lightweight persistent store for user-saved favorite MLBB hero IDs.
 * Preserves insertion order so newly favorited heroes appear consistently across app restarts.
 */
class FavoriteHeroesStore(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun loadFavoriteIds(): List<String> {
        val raw = prefs.getString(KEY_FAVORITE_HERO_IDS, null).orEmpty()
        if (raw.isBlank()) return emptyList()
        return raw.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
    }

    fun saveFavoriteIds(ids: List<String>) {
        prefs.edit()
            .putString(KEY_FAVORITE_HERO_IDS, ids.distinct().joinToString(","))
            .apply()
    }
}
