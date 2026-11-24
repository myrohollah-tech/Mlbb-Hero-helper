package com.example.i18n

import androidx.compose.runtime.Immutable

@Immutable
data class TrendChartStrings(
    val chartTitle: String,
    val chartSubtitle: String,
    val tabWinRate: String,
    val tabPickBan: String,
    val metricWinRate: String,
    val metricPickRate: String,
    val metricBanRate: String,
    val dayAgoAxis: String, // e.g., "-%dd"
    val dayAgoFull: String, // e.g., "%d days ago"
    val todayLabel: String,
    val scrubHint: String,
    val chartCd: String
)

fun AppLanguage.trendChartStrings(): TrendChartStrings = when (this) {
    AppLanguage.PERSIAN -> TrendChartStrings(
        chartTitle = "روند ۳۰ روزه متا",
        chartSubtitle = "تغییرات نرخ برد، پیک و بن در رنک",
        tabWinRate = "نرخ برد (WR)",
        tabPickBan = "پیک و بن (P/B)",
        metricWinRate = "نرخ برد",
        metricPickRate = "نرخ پیک",
        metricBanRate = "نرخ بن",
        dayAgoAxis = "-%dر",
        dayAgoFull = "%d روز پیش",
        todayLabel = "امروز",
        scrubHint = "برای بررسی دقیق هر روز، روی نمودار لمس کنید یا بکشید",
        chartCd = "نمودار روند ۳۰ روزه نرخ برد، پیک و بن هیرو"
    )

    AppLanguage.ENGLISH -> TrendChartStrings(
        chartTitle = "30-Day Meta Trends",
        chartSubtitle = "Ranked Win Rate, Pick & Ban progression",
        tabWinRate = "Win Rate",
        tabPickBan = "Pick / Ban",
        metricWinRate = "Win Rate",
        metricPickRate = "Pick Rate",
        metricBanRate = "Ban Rate",
        dayAgoAxis = "-%dd",
        dayAgoFull = "%d days ago",
        todayLabel = "Today",
        scrubHint = "Tap or drag across the chart to inspect any day",
        chartCd = "30-day hero win rate, pick rate, and ban rate trend chart"
    )

    AppLanguage.INDONESIAN -> TrendChartStrings(
        chartTitle = "Tren Meta 30 Hari",
        chartSubtitle = "Perkembangan Win Rate, Pick & Ban Ranked",
        tabWinRate = "Win Rate",
        tabPickBan = "Pick / Ban",
        metricWinRate = "Win Rate",
        metricPickRate = "Pick Rate",
        metricBanRate = "Ban Rate",
        dayAgoAxis = "-%dh",
        dayAgoFull = "%d hari lalu",
        todayLabel = "Hari ini",
        scrubHint = "Ketuk atau geser grafik untuk melihat statistik per hari",
        chartCd = "Grafik tren 30 hari win rate, pick rate, dan ban rate hero"
    )

    AppLanguage.ARABIC -> TrendChartStrings(
        chartTitle = "اتجاهات الميتا خلال 30 يوماً",
        chartSubtitle = "تطور نسبة الفوز والاختيار والحظر في الرانك",
        tabWinRate = "نسبة الفوز",
        tabPickBan = "الاختيار والحظر",
        metricWinRate = "نسبة الفوز",
        metricPickRate = "نسبة الاختيار",
        metricBanRate = "نسبة الحظر",
        dayAgoAxis = "-%dي",
        dayAgoFull = "قبل %d يوم",
        todayLabel = "اليوم",
        scrubHint = "اضغط أو اسحب على الرسم البياني لفحص تفاصيل أي يوم",
        chartCd = "مخطط اتجاه نسبة الفوز والاختيار والحظر خلال 30 يوماً"
    )

    AppLanguage.TURKISH -> TrendChartStrings(
        chartTitle = "30 Günlük Meta Trendleri",
        chartSubtitle = "Dereceli Kazanma, Seçilme ve Ban Oranları",
        tabWinRate = "Kazanma Oranı",
        tabPickBan = "Seçim / Ban",
        metricWinRate = "Kazanma",
        metricPickRate = "Seçilme",
        metricBanRate = "Ban Oranı",
        dayAgoAxis = "-%dg",
        dayAgoFull = "%d gün önce",
        todayLabel = "Bugün",
        scrubHint = "Herhangi bir günü incelemek için grafiğe dokun veya kaydır",
        chartCd = "30 günlük kahraman kazanma, seçilme ve ban oranı grafiği"
    )

    AppLanguage.GERMAN -> TrendChartStrings(
        chartTitle = "30-Tage-Meta-Trends",
        chartSubtitle = "Verlauf von Siegrate, Pick- & Ban-Rate",
        tabWinRate = "Siegrate",
        tabPickBan = "Pick / Ban",
        metricWinRate = "Siegrate",
        metricPickRate = "Pick-Rate",
        metricBanRate = "Ban-Rate",
        dayAgoAxis = "-%dT",
        dayAgoFull = "Vor %d Tagen",
        todayLabel = "Heute",
        scrubHint = "Tippe oder ziehe über das Diagramm, um Details zu sehen",
        chartCd = "30-Tage-Trenddiagramm für Siegrate, Pick- und Ban-Rate"
    )

    AppLanguage.CHINESE_SIMPLIFIED -> TrendChartStrings(
        chartTitle = "近30天版本趋势",
        chartSubtitle = "排位胜率、出场率与禁用率变化",
        tabWinRate = "胜率趋势",
        tabPickBan = "出场 / 禁用",
        metricWinRate = "胜率",
        metricPickRate = "出场率",
        metricBanRate = "禁用率",
        dayAgoAxis = "-%d天",
        dayAgoFull = "%d天前",
        todayLabel = "今日",
        scrubHint = "点击或拖动图表查看任意日期数据",
        chartCd = "英雄近30天胜率、出场率与禁用率趋势图"
    )

    AppLanguage.RUSSIAN -> TrendChartStrings(
        chartTitle = "Тренды меты за 30 дней",
        chartSubtitle = "Динамика винрейта, пикрейта и банрейта",
        tabWinRate = "Винрейт",
        tabPickBan = "Пик / Бан",
        metricWinRate = "Винрейт",
        metricPickRate = "Пикрейт",
        metricBanRate = "Банрейт",
        dayAgoAxis = "-%dд",
        dayAgoFull = "%d дн. назад",
        todayLabel = "Сегодня",
        scrubHint = "Нажмите или проведите по графику для просмотра дня",
        chartCd = "График винрейта, пикрейта и банрейта героя за 30 дней"
    )

    AppLanguage.FILIPINO -> TrendChartStrings(
        chartTitle = "30-Araw na Meta Trends",
        chartSubtitle = "Takbo ng Win Rate, Pick at Ban Rate",
        tabWinRate = "Win Rate",
        tabPickBan = "Pick / Ban",
        metricWinRate = "Win Rate",
        metricPickRate = "Pick Rate",
        metricBanRate = "Ban Rate",
        dayAgoAxis = "-%da",
        dayAgoFull = "%d araw ang nakalipas",
        todayLabel = "Ngayon",
        scrubHint = "I-tap o i-drag ang chart para makita ang bawat araw",
        chartCd = "30-araw na chart ng win rate, pick rate, at ban rate ng hero"
    )

    AppLanguage.MALAY -> TrendChartStrings(
        chartTitle = "Trend Meta 30 Hari",
        chartSubtitle = "Pergerakan Kadar Menang, Pilih & Ban",
        tabWinRate = "Kadar Menang",
        tabPickBan = "Pilih / Ban",
        metricWinRate = "Menang",
        metricPickRate = "Pilih",
        metricBanRate = "Ban",
        dayAgoAxis = "-%dh",
        dayAgoFull = "%d hari lalu",
        todayLabel = "Hari ini",
        scrubHint = "Ketik atau seret carta untuk melihat statistik setiap hari",
        chartCd = "Carta trend 30 hari kadar menang, pilih dan ban hero"
    )

    AppLanguage.PORTUGUESE_BR -> TrendChartStrings(
        chartTitle = "Tendências de 30 Dias",
        chartSubtitle = "Evolução de Taxa de Vitória, Escolha e Ban",
        tabWinRate = "Vitória",
        tabPickBan = "Escolha / Ban",
        metricWinRate = "Vitória",
        metricPickRate = "Escolha",
        metricBanRate = "Banimento",
        dayAgoAxis = "-%dd",
        dayAgoFull = "Há %d dias",
        todayLabel = "Hoje",
        scrubHint = "Toque ou arraste no gráfico para inspecionar cada dia",
        chartCd = "Gráfico de 30 dias da taxa de vitória, escolha e banimento"
    )

    AppLanguage.SPANISH -> TrendChartStrings(
        chartTitle = "Tendencias de 30 Días",
        chartSubtitle = "Evolución de Tasa de Victoria, Pick y Ban",
        tabWinRate = "Victoria",
        tabPickBan = "Pick / Ban",
        metricWinRate = "Victoria",
        metricPickRate = "Pick",
        metricBanRate = "Ban",
        dayAgoAxis = "-%dd",
        dayAgoFull = "Hace %d días",
        todayLabel = "Hoy",
        scrubHint = "Toca o arrastra en el gráfico para ver cada día",
        chartCd = "Gráfico de 30 días de tasa de victoria, pick y ban del héroe"
    )
}
