package com.example.i18n

import androidx.compose.runtime.Immutable

@Immutable
data class MatchHistoryStrings(
    val modeNewAnalysis: String,
    val modeHistory: String,
    val historyCountBadge: String, // %d
    val historyTitle: String,
    val historySubtitle: String,
    val historyEmptyTitle: String,
    val historyEmptySubtitle: String,
    val historyEmptyCta: String,
    val historyClearAll: String,
    val historyClearConfirmTitle: String,
    val historyClearConfirmBody: String,
    val historyConfirmDelete: String,
    val historyCancel: String,
    val historyDeleteCd: String,
    val historyOpenCd: String,
    val historyThumbnailCd: String,
    val historyViewingBanner: String, // %s (formatted date/time)
    val historyBackToList: String,
    val historyStartNewCta: String
)

fun AppLanguage.matchHistoryStrings(): MatchHistoryStrings = when (this) {
    AppLanguage.PERSIAN -> MatchHistoryStrings(
        modeNewAnalysis = "تحلیل جدید",
        modeHistory = "تاریخچه",
        historyCountBadge = "%d تحلیل ذخیره شده",
        historyTitle = "تاریخچه تحلیل‌های بازی",
        historySubtitle = "روی هر مسابقه بزنید تا اسکرین‌شات و گزارش کامل مربی را مرور کنید",
        historyEmptyTitle = "هنوز تحلیلی ثبت نشده است",
        historyEmptySubtitle = "هر اسکرین‌شات بازی که تحلیل کنید به‌صورت خودکار اینجا ذخیره می‌شود تا هر زمان بخواهید مرور کنید.",
        historyEmptyCta = "شروع اولین تحلیل",
        historyClearAll = "پاک کردن همه",
        historyClearConfirmTitle = "پاک کردن کل تاریخچه؟",
        historyClearConfirmBody = "تمام اسکرین‌شات‌ها و گزارش‌های ذخیره‌شده از دستگاه حذف می‌شوند و قابل بازگشت نیستند.",
        historyConfirmDelete = "حذف همه",
        historyCancel = "انصراف",
        historyDeleteCd = "حذف این تحلیل از تاریخچه",
        historyOpenCd = "باز کردن گزارش تحلیل",
        historyThumbnailCd = "پیش‌نمایش اسکرین‌شات ذخیره‌شده",
        historyViewingBanner = "گزارش ذخیره‌شده • %s",
        historyBackToList = "تاریخچه",
        historyStartNewCta = "تحلیل جدید"
    )

    AppLanguage.ENGLISH -> MatchHistoryStrings(
        modeNewAnalysis = "New Analysis",
        modeHistory = "History",
        historyCountBadge = "%d saved",
        historyTitle = "Match Analysis History",
        historySubtitle = "Tap any match to review its screenshot, coaching report, and continue chatting",
        historyEmptyTitle = "No analyzed matches yet",
        historyEmptySubtitle = "Every match screenshot you analyze is automatically saved here so you can review your progress anytime.",
        historyEmptyCta = "Analyze a Match",
        historyClearAll = "Clear All",
        historyClearConfirmTitle = "Clear all history?",
        historyClearConfirmBody = "All saved screenshots and coaching reports will be permanently removed from this device.",
        historyConfirmDelete = "Delete All",
        historyCancel = "Cancel",
        historyDeleteCd = "Delete match from history",
        historyOpenCd = "Open saved match report",
        historyThumbnailCd = "Saved match screenshot preview",
        historyViewingBanner = "Saved report • %s",
        historyBackToList = "History",
        historyStartNewCta = "New"
    )

    AppLanguage.INDONESIAN -> MatchHistoryStrings(
        modeNewAnalysis = "Analisis Baru",
        modeHistory = "Riwayat",
        historyCountBadge = "%d tersimpan",
        historyTitle = "Riwayat Analisis Pertandingan",
        historySubtitle = "Ketuk pertandingan untuk melihat screenshot dan laporan pelatih lengkap",
        historyEmptyTitle = "Belum ada riwayat analisis",
        historyEmptySubtitle = "Setiap screenshot pertandingan yang dianalisis akan otomatis tersimpan di sini.",
        historyEmptyCta = "Mulai Analisis",
        historyClearAll = "Hapus Semua",
        historyClearConfirmTitle = "Hapus semua riwayat?",
        historyClearConfirmBody = "Semua screenshot dan laporan analisis yang tersimpan akan dihapus permanen dari perangkat ini.",
        historyConfirmDelete = "Hapus Semua",
        historyCancel = "Batal",
        historyDeleteCd = "Hapus dari riwayat",
        historyOpenCd = "Buka laporan pertandingan",
        historyThumbnailCd = "Pratinjau screenshot tersimpan",
        historyViewingBanner = "Laporan tersimpan • %s",
        historyBackToList = "Riwayat",
        historyStartNewCta = "Baru"
    )

    AppLanguage.ARABIC -> MatchHistoryStrings(
        modeNewAnalysis = "تحليل جديد",
        modeHistory = "السجل",
        historyCountBadge = "%d محفوظ",
        historyTitle = "سجل تحليلات المباريات",
        historySubtitle = "اضغط على أي مباراة لمراجعة لقطة الشاشة وتقرير المدرب الكامل",
        historyEmptyTitle = "لا توجد تحليلات محفوظة بعد",
        historyEmptySubtitle = "يتم حفظ كل لقطة شاشة تقوم بتحليلها تلقائياً هنا لتتمكن من مراجعتها لاحقاً.",
        historyEmptyCta = "ابدأ التحليل الآن",
        historyClearAll = "مسح الكل",
        historyClearConfirmTitle = "مسح السجل بالكامل؟",
        historyClearConfirmBody = "سيتم حذف جميع لقطات الشاشة وتقارير التدريب المحفوظة نهائياً من هذا الجهاز.",
        historyConfirmDelete = "حذف الكل",
        historyCancel = "إلغاء",
        historyDeleteCd = "حذف المباراة من السجل",
        historyOpenCd = "فتح تقرير المباراة",
        historyThumbnailCd = "معاينة لقطة الشاشة المحفوظة",
        historyViewingBanner = "تقرير محفوظ • %s",
        historyBackToList = "السجل",
        historyStartNewCta = "جديد"
    )

    AppLanguage.TURKISH -> MatchHistoryStrings(
        modeNewAnalysis = "Yeni Analiz",
        modeHistory = "Geçmiş",
        historyCountBadge = "%d kayıtlı",
        historyTitle = "Maç Analiz Geçmişi",
        historySubtitle = "Ekran görüntüsünü ve koç raporunu incelemek için bir maça dokun",
        historyEmptyTitle = "Henüz kayıtlı analiz yok",
        historyEmptySubtitle = "Analiz ettiğin her maç ekran görüntüsü burada otomatik olarak saklanır.",
        historyEmptyCta = "Maç Analiz Et",
        historyClearAll = "Tümünü Temizle",
        historyClearConfirmTitle = "Tüm geçmiş temizlensin mi?",
        historyClearConfirmBody = "Kaydedilen tüm ekran görüntüleri ve koç raporları bu cihazdan kalıcı olarak silinecek.",
        historyConfirmDelete = "Tümünü Sil",
        historyCancel = "İptal",
        historyDeleteCd = "Analizi geçmişten sil",
        historyOpenCd = "Kayıtlı maç raporunu aç",
        historyThumbnailCd = "Kayıtlı maç ekran görüntüsü önizlemesi",
        historyViewingBanner = "Kayıtlı rapor • %s",
        historyBackToList = "Geçmiş",
        historyStartNewCta = "Yeni"
    )

    AppLanguage.GERMAN -> MatchHistoryStrings(
        modeNewAnalysis = "Neue Analyse",
        modeHistory = "Verlauf",
        historyCountBadge = "%d gespeichert",
        historyTitle = "Match-Analyse-Verlauf",
        historySubtitle = "Tippe auf ein Match, um Screenshot und Coaching-Bericht erneut anzusehen",
        historyEmptyTitle = "Noch keine Analysen gespeichert",
        historyEmptySubtitle = "Jeder analysierte Match-Screenshot wird hier automatisch für später gespeichert.",
        historyEmptyCta = "Match analysieren",
        historyClearAll = "Alle löschen",
        historyClearConfirmTitle = "Gesamten Verlauf löschen?",
        historyClearConfirmBody = "Alle gespeicherten Screenshots und Berichte werden dauerhaft von diesem Gerät entfernt.",
        historyConfirmDelete = "Alle löschen",
        historyCancel = "Abbrechen",
        historyDeleteCd = "Analyse aus Verlauf löschen",
        historyOpenCd = "Gespeicherten Bericht öffnen",
        historyThumbnailCd = "Vorschau des gespeicherten Screenshots",
        historyViewingBanner = "Gespeicherter Bericht • %s",
        historyBackToList = "Verlauf",
        historyStartNewCta = "Neu"
    )

    AppLanguage.CHINESE_SIMPLIFIED -> MatchHistoryStrings(
        modeNewAnalysis = "新分析",
        modeHistory = "历史记录",
        historyCountBadge = "已保存 %d 条",
        historyTitle = "对局分析历史",
        historySubtitle = "点击任意记录即可回顾对局截图与完整教练报告",
        historyEmptyTitle = "暂无分析记录",
        historyEmptySubtitle = "每次完成对局截图分析后，系统会自动保存在这里，方便随时复盘。",
        historyEmptyCta = "开始分析对局",
        historyClearAll = "清空全部",
        historyClearConfirmTitle = "清空所有历史记录？",
        historyClearConfirmBody = "所有已保存的对局截图和教练报告都将从本设备永久删除。",
        historyConfirmDelete = "全部删除",
        historyCancel = "取消",
        historyDeleteCd = "删除该条记录",
        historyOpenCd = "打开历史分析报告",
        historyThumbnailCd = "已保存对局截图缩略图",
        historyViewingBanner = "已保存报告 • %s",
        historyBackToList = "历史",
        historyStartNewCta = "新建"
    )

    AppLanguage.RUSSIAN -> MatchHistoryStrings(
        modeNewAnalysis = "Новый анализ",
        modeHistory = "История",
        historyCountBadge = "Сохранено: %d",
        historyTitle = "История анализов матчей",
        historySubtitle = "Нажмите на матч, чтобы открыть скриншот и полный отчёт тренера",
        historyEmptyTitle = "История пока пуста",
        historyEmptySubtitle = "Каждый проанализированный скриншот матча автоматически сохраняется здесь.",
        historyEmptyCta = "Разобрать матч",
        historyClearAll = "Очистить всё",
        historyClearConfirmTitle = "Очистить всю историю?",
        historyClearConfirmBody = "Все сохранённые скриншоты и отчёты будут безвозвратно удалены с устройства.",
        historyConfirmDelete = "Удалить всё",
        historyCancel = "Отмена",
        historyDeleteCd = "Удалить матч из истории",
        historyOpenCd = "Открыть сохранённый отчёт",
        historyThumbnailCd = "Миниатюра сохранённого скриншота",
        historyViewingBanner = "Сохранённый отчёт • %s",
        historyBackToList = "История",
        historyStartNewCta = "Новый"
    )

    AppLanguage.FILIPINO -> MatchHistoryStrings(
        modeNewAnalysis = "Bagong Suri",
        modeHistory = "Kasaysayan",
        historyCountBadge = "%d naka-save",
        historyTitle = "Kasaysayan ng Pagsusuri",
        historySubtitle = "I-tap ang kahit anong laban para makita ulit ang screenshot at coach report",
        historyEmptyTitle = "Wala pang naka-save na pagsusuri",
        historyEmptySubtitle = "Awtomatikong ise-save dito ang bawat match screenshot na iyong ipapa-analyze.",
        historyEmptyCta = "Mag-analyze ng Laban",
        historyClearAll = "Burahin Lahat",
        historyClearConfirmTitle = "Burahin ang buong kasaysayan?",
        historyClearConfirmBody = "Permanenteng mabubura sa device na ito ang lahat ng naka-save na screenshot at report.",
        historyConfirmDelete = "Burahin Lahat",
        historyCancel = "Kanselahin",
        historyDeleteCd = "Burahin sa kasaysayan",
        historyOpenCd = "Buksan ang naka-save na report",
        historyThumbnailCd = "Preview ng naka-save na screenshot",
        historyViewingBanner = "Naka-save na report • %s",
        historyBackToList = "Kasaysayan",
        historyStartNewCta = "Bago"
    )

    AppLanguage.MALAY -> MatchHistoryStrings(
        modeNewAnalysis = "Analisis Baharu",
        modeHistory = "Sejarah",
        historyCountBadge = "%d disimpan",
        historyTitle = "Sejarah Analisis Perlawanan",
        historySubtitle = "Ketik mana-mana perlawanan untuk menyemak tangkapan skrin dan laporan penuh",
        historyEmptyTitle = "Tiada sejarah analisis lagi",
        historyEmptySubtitle = "Setiap tangkapan skrin yang dianalisis akan disimpan secara automatik di sini.",
        historyEmptyCta = "Analisis Perlawanan",
        historyClearAll = "Padam Semua",
        historyClearConfirmTitle = "Padam semua sejarah?",
        historyClearConfirmBody = "Semua tangkapan skrin dan laporan jurulatih yang disimpan akan dipadam secara kekal.",
        historyConfirmDelete = "Padam Semua",
        historyCancel = "Batal",
        historyDeleteCd = "Padam daripada sejarah",
        historyOpenCd = "Buka laporan perlawanan",
        historyThumbnailCd = "Pratonton tangkapan skrin disimpan",
        historyViewingBanner = "Laporan disimpan • %s",
        historyBackToList = "Sejarah",
        historyStartNewCta = "Baharu"
    )

    AppLanguage.PORTUGUESE_BR -> MatchHistoryStrings(
        modeNewAnalysis = "Nova Análise",
        modeHistory = "Histórico",
        historyCountBadge = "%d salvas",
        historyTitle = "Histórico de Análises",
        historySubtitle = "Toque em uma partida para rever a captura de tela e o relatório completo",
        historyEmptyTitle = "Nenhuma análise salva ainda",
        historyEmptySubtitle = "Cada captura de tela analisada é salva automaticamente aqui para você revisar quando quiser.",
        historyEmptyCta = "Analisar Partida",
        historyClearAll = "Limpar Tudo",
        historyClearConfirmTitle = "Limpar todo o histórico?",
        historyClearConfirmBody = "Todas as capturas de tela e relatórios salvos serão removidos permanentemente deste dispositivo.",
        historyConfirmDelete = "Apagar Tudo",
        historyCancel = "Cancelar",
        historyDeleteCd = "Excluir partida do histórico",
        historyOpenCd = "Abrir relatório salvo",
        historyThumbnailCd = "Prévia da captura de tela salva",
        historyViewingBanner = "Relatório salvo • %s",
        historyBackToList = "Histórico",
        historyStartNewCta = "Nova"
    )

    AppLanguage.SPANISH -> MatchHistoryStrings(
        modeNewAnalysis = "Nuevo Análisis",
        modeHistory = "Historial",
        historyCountBadge = "%d guardados",
        historyTitle = "Historial de Análisis",
        historySubtitle = "Toca cualquier partida para revisar su captura y el informe completo del entrenador",
        historyEmptyTitle = "Aún no hay análisis guardados",
        historyEmptySubtitle = "Cada captura de partida que analices se guardará aquí automáticamente para consultarla después.",
        historyEmptyCta = "Analizar Partida",
        historyClearAll = "Borrar Todo",
        historyClearConfirmTitle = "¿Borrar todo el historial?",
        historyClearConfirmBody = "Todas las capturas de pantalla e informes guardados se eliminarán permanentemente de este dispositivo.",
        historyConfirmDelete = "Eliminar Todo",
        historyCancel = "Cancelar",
        historyDeleteCd = "Eliminar partida del historial",
        historyOpenCd = "Abrir informe guardado",
        historyThumbnailCd = "Vista previa de la captura guardada",
        historyViewingBanner = "Informe guardado • %s",
        historyBackToList = "Historial",
        historyStartNewCta = "Nuevo"
    )
}
