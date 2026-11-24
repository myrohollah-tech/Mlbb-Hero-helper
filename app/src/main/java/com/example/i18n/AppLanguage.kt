package com.example.i18n

import androidx.compose.ui.unit.LayoutDirection
import java.util.Locale

enum class AppLanguage(
    val code: String,
    val badgeCode: String,
    val nativeName: String,
    val englishName: String,
    val aiLanguageName: String,
    val isRtl: Boolean
) {
    INDONESIAN(
        code = "id",
        badgeCode = "ID",
        nativeName = "Bahasa Indonesia",
        englishName = "Indonesian",
        aiLanguageName = "Indonesian (Bahasa Indonesia)",
        isRtl = false
    ),
    PERSIAN(
        code = "fa",
        badgeCode = "FA",
        nativeName = "فارسی",
        englishName = "Persian",
        aiLanguageName = "Persian (Farsi)",
        isRtl = true
    ),
    ENGLISH(
        code = "en",
        badgeCode = "EN",
        nativeName = "English",
        englishName = "English",
        aiLanguageName = "English",
        isRtl = false
    ),
    GERMAN(
        code = "de",
        badgeCode = "DE",
        nativeName = "Deutsch",
        englishName = "German",
        aiLanguageName = "German (Deutsch)",
        isRtl = false
    ),
    CHINESE_SIMPLIFIED(
        code = "zh",
        badgeCode = "ZH",
        nativeName = "简体中文",
        englishName = "Simplified Chinese",
        aiLanguageName = "Simplified Chinese (简体中文)",
        isRtl = false
    ),
    RUSSIAN(
        code = "ru",
        badgeCode = "RU",
        nativeName = "Русский",
        englishName = "Russian",
        aiLanguageName = "Russian (Русский)",
        isRtl = false
    ),
    FILIPINO(
        code = "tl",
        badgeCode = "TL",
        nativeName = "Filipino / Tagalog",
        englishName = "Filipino",
        aiLanguageName = "Filipino / Tagalog",
        isRtl = false
    ),
    MALAY(
        code = "ms",
        badgeCode = "MS",
        nativeName = "Bahasa Melayu",
        englishName = "Malay",
        aiLanguageName = "Malay (Bahasa Melayu)",
        isRtl = false
    ),
    TURKISH(
        code = "tr",
        badgeCode = "TR",
        nativeName = "Türkçe",
        englishName = "Turkish",
        aiLanguageName = "Turkish (Türkçe)",
        isRtl = false
    ),
    ARABIC(
        code = "ar",
        badgeCode = "AR",
        nativeName = "العربية",
        englishName = "Arabic",
        aiLanguageName = "Arabic (العربية)",
        isRtl = true
    ),
    PORTUGUESE_BR(
        code = "pt-BR",
        badgeCode = "PT",
        nativeName = "Português (BR)",
        englishName = "Portuguese (Brazil)",
        aiLanguageName = "Brazilian Portuguese (Português do Brasil)",
        isRtl = false
    ),
    SPANISH(
        code = "es",
        badgeCode = "ES",
        nativeName = "Español",
        englishName = "Spanish",
        aiLanguageName = "Spanish (Español)",
        isRtl = false
    );

    val layoutDirection: LayoutDirection
        get() = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    val javaLocale: Locale
        get() = Locale.forLanguageTag(code)

    fun format(template: String, vararg args: Any): String =
        String.format(javaLocale, template, *args)

    companion object {
        fun fromCode(code: String?): AppLanguage? {
            if (code.isNullOrBlank()) return null
            val normalized = code.trim().lowercase(Locale.ROOT)
            return entries.firstOrNull { it.code.lowercase(Locale.ROOT) == normalized }
                ?: when {
                    normalized.startsWith("in") || normalized.startsWith("id") -> INDONESIAN
                    normalized.startsWith("fa") -> PERSIAN
                    normalized.startsWith("en") -> ENGLISH
                    normalized.startsWith("de") -> GERMAN
                    normalized.startsWith("zh") -> CHINESE_SIMPLIFIED
                    normalized.startsWith("ru") -> RUSSIAN
                    normalized.startsWith("fil") || normalized.startsWith("tl") -> FILIPINO
                    normalized.startsWith("ms") -> MALAY
                    normalized.startsWith("tr") -> TURKISH
                    normalized.startsWith("ar") -> ARABIC
                    normalized.startsWith("pt") -> PORTUGUESE_BR
                    normalized.startsWith("es") -> SPANISH
                    else -> null
                }
        }

        fun detectDeviceLanguage(locale: Locale = Locale.getDefault()): AppLanguage {
            return fromCode(locale.toLanguageTag())
                ?: fromCode(locale.language)
                ?: ENGLISH
        }
    }
}
