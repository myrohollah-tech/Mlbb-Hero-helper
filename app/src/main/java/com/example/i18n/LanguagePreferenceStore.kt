package com.example.i18n

import android.content.Context

private const val PREFS_NAME = "mlbb_hero_coach_i18n"
private const val KEY_SELECTED_LANGUAGE = "selected_language_code"

class LanguagePreferenceStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun loadInitialLanguage(): AppLanguage {
        val savedCode = prefs.getString(KEY_SELECTED_LANGUAGE, null)
        return AppLanguage.fromCode(savedCode) ?: AppLanguage.detectDeviceLanguage()
    }

    fun saveLanguage(language: AppLanguage) {
        prefs.edit()
            .putString(KEY_SELECTED_LANGUAGE, language.code)
            .apply()
    }
}
