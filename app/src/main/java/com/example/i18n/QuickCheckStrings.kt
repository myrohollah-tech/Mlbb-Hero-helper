package com.example.i18n

import androidx.compose.runtime.Immutable

@Immutable
data class QuickCheckStrings(
    val badgeTitle: String,
    val coreItemLabel: String,
    val mainComboLabel: String,
    val primaryCounterLabel: String,
    val openFullGuideCta: String,
    val loadingHint: String,
    val closeCd: String,
    val retryCta: String
)

fun AppLanguage.quickCheckStrings(): QuickCheckStrings = when (this) {
    AppLanguage.PERSIAN -> QuickCheckStrings(
        badgeTitle = "بررسی سریع",
        coreItemLabel = "آیتم اصلی",
        mainComboLabel = "کمبوی اصلی",
        primaryCounterLabel = "کانتر اصلی",
        openFullGuideCta = "مشاهده راهنمای کامل و نمودار",
        loadingHint = "در حال دریافت خلاصه ۳ نکته‌ای...",
        closeCd = "بستن بررسی سریع",
        retryCta = "تلاش مجدد"
    )

    AppLanguage.ENGLISH -> QuickCheckStrings(
        badgeTitle = "Quick-Check",
        coreItemLabel = "Core Item",
        mainComboLabel = "Main Combo",
        primaryCounterLabel = "Primary Counter",
        openFullGuideCta = "Open Full Hero Guide",
        loadingHint = "Fetching 3-point meta summary...",
        closeCd = "Close Quick-Check",
        retryCta = "Retry"
    )

    AppLanguage.INDONESIAN -> QuickCheckStrings(
        badgeTitle = "Cek Cepat",
        coreItemLabel = "Item Inti",
        mainComboLabel = "Kombo Utama",
        primaryCounterLabel = "Counter Utama",
        openFullGuideCta = "Buka Panduan Lengkap",
        loadingHint = "Memuat ringkasan cepat 3 poin...",
        closeCd = "Tutup Cek Cepat",
        retryCta = "Coba Lagi"
    )

    AppLanguage.ARABIC -> QuickCheckStrings(
        badgeTitle = "فحص سريع",
        coreItemLabel = "الأداة الأساسية",
        mainComboLabel = "الكومبو الرئيسي",
        primaryCounterLabel = "الكونتر الأساسي",
        openFullGuideCta = "فتح الدليل الكامل للبطل",
        loadingHint = "جارٍ تحميل ملخص النقاط الثلاث...",
        closeCd = "إغلاق الفحص السريع",
        retryCta = "إعادة المحاولة"
    )

    AppLanguage.TURKISH -> QuickCheckStrings(
        badgeTitle = "Hızlı Bakış",
        coreItemLabel = "Ana Eşya",
        mainComboLabel = "Ana Kombo",
        primaryCounterLabel = "Başlıca Counter",
        openFullGuideCta = "Tam Rehberi Aç",
        loadingHint = "3 maddelik özet yükleniyor...",
        closeCd = "Hızlı Bakışı Kapat",
        retryCta = "Tekrar Dene"
    )

    AppLanguage.GERMAN -> QuickCheckStrings(
        badgeTitle = "Schnellcheck",
        coreItemLabel = "Kern-Item",
        mainComboLabel = "Haupt-Kombo",
        primaryCounterLabel = "Haupt-Konter",
        openFullGuideCta = "Vollständigen Guide öffnen",
        loadingHint = "Lade 3-Punkte-Zusammenfassung...",
        closeCd = "Schnellcheck schließen",
        retryCta = "Erneut versuchen"
    )

    AppLanguage.CHINESE_SIMPLIFIED -> QuickCheckStrings(
        badgeTitle = "速查卡",
        coreItemLabel = "核心装备",
        mainComboLabel = "核心连招",
        primaryCounterLabel = "主要克星",
        openFullGuideCta = "查看完整英雄攻略",
        loadingHint = "正在加载三点速览...",
        closeCd = "关闭速查卡",
        retryCta = "重试"
    )

    AppLanguage.RUSSIAN -> QuickCheckStrings(
        badgeTitle = "Быстрый обзор",
        coreItemLabel = "Ключевой предмет",
        mainComboLabel = "Основное комбо",
        primaryCounterLabel = "Главный контрпик",
        openFullGuideCta = "Открыть полный гайд",
        loadingHint = "Загрузка краткой сводки...",
        closeCd = "Закрыть быстрый обзор",
        retryCta = "Повторить"
    )

    AppLanguage.FILIPINO -> QuickCheckStrings(
        badgeTitle = "Quick-Check",
        coreItemLabel = "Core Item",
        mainComboLabel = "Main Combo",
        primaryCounterLabel = "Pangunahing Counter",
        openFullGuideCta = "Buksan ang Buong Guide",
        loadingHint = "Kinukuha ang 3-point summary...",
        closeCd = "Isara ang Quick-Check",
        retryCta = "Subukan Ulit"
    )

    AppLanguage.MALAY -> QuickCheckStrings(
        badgeTitle = "Semak Pantas",
        coreItemLabel = "Item Teras",
        mainComboLabel = "Kombo Utama",
        primaryCounterLabel = "Kaunter Utama",
        openFullGuideCta = "Buka Panduan Penuh",
        loadingHint = "Memuatkan ringkasan 3 poin...",
        closeCd = "Tutup Semak Pantas",
        retryCta = "Cuba Lagi"
    )

    AppLanguage.PORTUGUESE_BR -> QuickCheckStrings(
        badgeTitle = "Resumo Rápido",
        coreItemLabel = "Item Principal",
        mainComboLabel = "Combo Principal",
        primaryCounterLabel = "Principal Counter",
        openFullGuideCta = "Abrir Guia Completo",
        loadingHint = "Carregando resumo em 3 pontos...",
        closeCd = "Fechar Resumo Rápido",
        retryCta = "Tentar Novamente"
    )

    AppLanguage.SPANISH -> QuickCheckStrings(
        badgeTitle = "Vista Rápida",
        coreItemLabel = "Objeto Clave",
        mainComboLabel = "Combo Principal",
        primaryCounterLabel = "Counter Principal",
        openFullGuideCta = "Abrir Guía Completa",
        loadingHint = "Cargando resumen en 3 puntos...",
        closeCd = "Cerrar Vista Rápida",
        retryCta = "Reintentar"
    )
}
