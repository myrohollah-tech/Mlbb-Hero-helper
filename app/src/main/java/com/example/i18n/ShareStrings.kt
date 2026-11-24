package com.example.i18n

import androidx.compose.runtime.Immutable

@Immutable
data class ShareStrings(
    val shareHeroCd: String,
    val shareBuildCta: String,
    val shareChooserTitle: String,
    val shareSubjectTemplate: String, // %s = hero name
    val shareRoleLabel: String,
    val shareLaneLabel: String,
    val shareSpecialtyLabel: String,
    val shareOpenAppHint: String,
    val shareFooter: String
)

fun AppLanguage.shareStrings(): ShareStrings = when (this) {
    AppLanguage.PERSIAN -> ShareStrings(
        shareHeroCd = "اشتراک‌گذاری تحلیل و بیلد هیرو",
        shareBuildCta = "اشتراک‌گذاری تحلیل و بیلد",
        shareChooserTitle = "ارسال بیلد و تحلیل هیرو",
        shareSubjectTemplate = "بیلد و راهنمای %s در Mobile Legends",
        shareRoleLabel = "نقش",
        shareLaneLabel = "مسیر (لاین)",
        shareSpecialtyLabel = "سبک بازی",
        shareOpenAppHint = "برای مشاهده تحلیل کامل متا، کمبوها و کانترها، این هیرو را در MLBB Hero Coach باز کنید.",
        shareFooter = "ارسال‌شده از MLBB Hero Coach"
    )

    AppLanguage.ENGLISH -> ShareStrings(
        shareHeroCd = "Share hero analysis and build",
        shareBuildCta = "Share Build & Analysis",
        shareChooserTitle = "Share Hero Build & Guide",
        shareSubjectTemplate = "MLBB Build & Guide: %s",
        shareRoleLabel = "Role",
        shareLaneLabel = "Lane",
        shareSpecialtyLabel = "Specialty",
        shareOpenAppHint = "Open this hero in MLBB Hero Coach for live meta tips, combos, and counter picks.",
        shareFooter = "Shared via MLBB Hero Coach"
    )

    AppLanguage.INDONESIAN -> ShareStrings(
        shareHeroCd = "Bagikan analisis dan build hero",
        shareBuildCta = "Bagikan Build & Analisis",
        shareChooserTitle = "Bagikan Build & Panduan Hero",
        shareSubjectTemplate = "Build & Panduan MLBB: %s",
        shareRoleLabel = "Role",
        shareLaneLabel = "Lane",
        shareSpecialtyLabel = "Spesialisasi",
        shareOpenAppHint = "Buka hero ini di MLBB Hero Coach untuk tips meta terbaru, kombo, dan counter.",
        shareFooter = "Dibagikan dari MLBB Hero Coach"
    )

    AppLanguage.ARABIC -> ShareStrings(
        shareHeroCd = "مشاركة تحليل وبناء البطل",
        shareBuildCta = "مشاركة التحليل والبناء",
        shareChooserTitle = "مشاركة دليل وبناء البطل",
        shareSubjectTemplate = "بناء ودليل %s في Mobile Legends",
        shareRoleLabel = "الدور",
        shareLaneLabel = "المسار",
        shareSpecialtyLabel = "الأسلوب",
        shareOpenAppHint = "افتح هذا البطل في MLBB Hero Coach لعرض تحليل الميتا والكومبوهات والكونتر.",
        shareFooter = "تمت المشاركة عبر MLBB Hero Coach"
    )

    AppLanguage.TURKISH -> ShareStrings(
        shareHeroCd = "Kahraman analizini ve buildini paylaş",
        shareBuildCta = "Build ve Analizi Paylaş",
        shareChooserTitle = "Kahraman Buildini Paylaş",
        shareSubjectTemplate = "MLBB Build ve Rehber: %s",
        shareRoleLabel = "Rol",
        shareLaneLabel = "Koridor",
        shareSpecialtyLabel = "Uzmanlık",
        shareOpenAppHint = "Canlı meta ipuçları, kombolar ve counterlar için MLBB Hero Coach uygulamasını aç.",
        shareFooter = "MLBB Hero Coach ile paylaşıldı"
    )

    AppLanguage.GERMAN -> ShareStrings(
        shareHeroCd = "Helden-Analyse und Build teilen",
        shareBuildCta = "Build & Analyse teilen",
        shareChooserTitle = "Helden-Build & Guide teilen",
        shareSubjectTemplate = "MLBB Build & Guide: %s",
        shareRoleLabel = "Rolle",
        shareLaneLabel = "Lane",
        shareSpecialtyLabel = "Spezialität",
        shareOpenAppHint = "Öffne diesen Helden in MLBB Hero Coach für aktuelle Meta-Tipps, Kombos und Konter.",
        shareFooter = "Geteilt mit MLBB Hero Coach"
    )

    AppLanguage.CHINESE_SIMPLIFIED -> ShareStrings(
        shareHeroCd = "分享英雄出装与分析",
        shareBuildCta = "分享出装与分析",
        shareChooserTitle = "分享英雄出装攻略",
        shareSubjectTemplate = "MLBB 英雄出装攻略：%s",
        shareRoleLabel = "定位",
        shareLaneLabel = "分路",
        shareSpecialtyLabel = "特长",
        shareOpenAppHint = "在 MLBB Hero Coach 中查看该英雄的实时版本出装、连招与克制关系。",
        shareFooter = "来自 MLBB Hero Coach"
    )

    AppLanguage.RUSSIAN -> ShareStrings(
        shareHeroCd = "Поделиться разбором и сборкой героя",
        shareBuildCta = "Поделиться сборкой",
        shareChooserTitle = "Поделиться гайдом и сборкой",
        shareSubjectTemplate = "Сборка и гайд MLBB: %s",
        shareRoleLabel = "Роль",
        shareLaneLabel = "Линия",
        shareSpecialtyLabel = "Стиль",
        shareOpenAppHint = "Откройте этого героя в MLBB Hero Coach, чтобы увидеть актуальную мету, комбо и контрпики.",
        shareFooter = "Отправлено из MLBB Hero Coach"
    )

    AppLanguage.FILIPINO -> ShareStrings(
        shareHeroCd = "I-share ang analysis at build ng hero",
        shareBuildCta = "I-share ang Build at Analysis",
        shareChooserTitle = "I-share ang Hero Build & Guide",
        shareSubjectTemplate = "MLBB Build & Guide: %s",
        shareRoleLabel = "Role",
        shareLaneLabel = "Lane",
        shareSpecialtyLabel = "Estilo",
        shareOpenAppHint = "Buksan ang hero na ito sa MLBB Hero Coach para sa live meta tips, combos, at counters.",
        shareFooter = "Ibinahagi gamit ang MLBB Hero Coach"
    )

    AppLanguage.MALAY -> ShareStrings(
        shareHeroCd = "Kongsi analisis dan build hero",
        shareBuildCta = "Kongsi Build & Analisis",
        shareChooserTitle = "Kongsi Build & Panduan Hero",
        shareSubjectTemplate = "Build & Panduan MLBB: %s",
        shareRoleLabel = "Peranan",
        shareLaneLabel = "Laluan",
        shareSpecialtyLabel = "Kepakaran",
        shareOpenAppHint = "Buka hero ini di MLBB Hero Coach untuk tip meta terkini, kombo dan kaunter.",
        shareFooter = "Dikongsi melalui MLBB Hero Coach"
    )

    AppLanguage.PORTUGUESE_BR -> ShareStrings(
        shareHeroCd = "Compartilhar análise e build do herói",
        shareBuildCta = "Compartilhar Build e Análise",
        shareChooserTitle = "Compartilhar Build e Guia",
        shareSubjectTemplate = "Build e Guia MLBB: %s",
        shareRoleLabel = "Função",
        shareLaneLabel = "Rota",
        shareSpecialtyLabel = "Especialidade",
        shareOpenAppHint = "Abra este herói no MLBB Hero Coach para dicas do meta atual, combos e counters.",
        shareFooter = "Compartilhado via MLBB Hero Coach"
    )

    AppLanguage.SPANISH -> ShareStrings(
        shareHeroCd = "Compartir análisis y build del héroe",
        shareBuildCta = "Compartir Build y Análisis",
        shareChooserTitle = "Compartir Build y Guía",
        shareSubjectTemplate = "Build y Guía MLBB: %s",
        shareRoleLabel = "Rol",
        shareLaneLabel = "Carril",
        shareSpecialtyLabel = "Especialidad",
        shareOpenAppHint = "Abre este héroe en MLBB Hero Coach para consejos del meta actual, combos y counters.",
        shareFooter = "Compartido desde MLBB Hero Coach"
    )
}
