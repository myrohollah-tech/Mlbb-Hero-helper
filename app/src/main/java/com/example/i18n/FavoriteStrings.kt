package com.example.i18n

import androidx.compose.runtime.Immutable

@Immutable
data class FavoriteStrings(
    val sectionTitle: String,
    val sectionCountBadge: String, // %d
    val sectionSubtitle: String,
    val emptyHint: String,
    val allHeroesTitle: String,
    val addToFavoritesCd: String,
    val removeFromFavoritesCd: String
)

fun AppLanguage.favoriteStrings(): FavoriteStrings = when (this) {
    AppLanguage.PERSIAN -> FavoriteStrings(
        sectionTitle = "هیروهای محبوب",
        sectionCountBadge = "%d ذخیره شده",
        sectionSubtitle = "دسترسی سریع به هیروهای اصلی شما",
        emptyHint = "برای ذخیره هیروهای اصلی خود، روی آیکون قلب در کارت هر هیرو بزنید.",
        allHeroesTitle = "همه هیروها",
        addToFavoritesCd = "افزودن به علاقه‌مندی‌ها",
        removeFromFavoritesCd = "حذف از علاقه‌مندی‌ها"
    )
    AppLanguage.ENGLISH -> FavoriteStrings(
        sectionTitle = "Favorites",
        sectionCountBadge = "%d saved",
        sectionSubtitle = "Quick access to your most played heroes",
        emptyHint = "Tap the heart icon on any hero card to save your main heroes here.",
        allHeroesTitle = "All Heroes",
        addToFavoritesCd = "Add to favorites",
        removeFromFavoritesCd = "Remove from favorites"
    )
    AppLanguage.INDONESIAN -> FavoriteStrings(
        sectionTitle = "Favorit",
        sectionCountBadge = "%d tersimpan",
        sectionSubtitle = "Akses cepat ke hero andalanmu",
        emptyHint = "Ketuk ikon hati di kartu hero mana saja untuk menyimpannya di sini.",
        allHeroesTitle = "Semua Hero",
        addToFavoritesCd = "Tambahkan ke favorit",
        removeFromFavoritesCd = "Hapus dari favorit"
    )
    AppLanguage.ARABIC -> FavoriteStrings(
        sectionTitle = "المفضلة",
        sectionCountBadge = "%d محفوظ",
        sectionSubtitle = "وصول سريع إلى أبطالك المفضلين",
        emptyHint = "اضغط على أيقونة القلب في بطاقة أي بطل لحفظ أبطالك الأساسيين هنا.",
        allHeroesTitle = "كل الأبطال",
        addToFavoritesCd = "إضافة إلى المفضلة",
        removeFromFavoritesCd = "إزالة من المفضلة"
    )
    AppLanguage.TURKISH -> FavoriteStrings(
        sectionTitle = "Favoriler",
        sectionCountBadge = "%d kayıtlı",
        sectionSubtitle = "En çok oynadığın kahramanlara hızlı erişim",
        emptyHint = "Ana kahramanlarını buraya kaydetmek için kartlardaki kalp simgesine dokun.",
        allHeroesTitle = "Tüm Kahramanlar",
        addToFavoritesCd = "Favorilere ekle",
        removeFromFavoritesCd = "Favorilerden çıkar"
    )
    AppLanguage.GERMAN -> FavoriteStrings(
        sectionTitle = "Favoriten",
        sectionCountBadge = "%d gespeichert",
        sectionSubtitle = "Schneller Zugriff auf deine meistgespielten Helden",
        emptyHint = "Tippe auf das Herzsymbol einer Heldenkarte, um Favoriten zu speichern.",
        allHeroesTitle = "Alle Helden",
        addToFavoritesCd = "Zu Favoriten hinzufügen",
        removeFromFavoritesCd = "Aus Favoriten entfernen"
    )
    AppLanguage.CHINESE_SIMPLIFIED -> FavoriteStrings(
        sectionTitle = "收藏英雄",
        sectionCountBadge = "已收藏 %d",
        sectionSubtitle = "快速查看你的常用本命英雄",
        emptyHint = "点击任意英雄卡片上的爱心图标，即可将常用英雄保存到这里。",
        allHeroesTitle = "全部英雄",
        addToFavoritesCd = "添加到收藏",
        removeFromFavoritesCd = "取消收藏"
    )
    AppLanguage.RUSSIAN -> FavoriteStrings(
        sectionTitle = "Избранное",
        sectionCountBadge = "Сохранено: %d",
        sectionSubtitle = "Быстрый доступ к любимым героям",
        emptyHint = "Нажмите на сердечко на карточке любого героя, чтобы сохранить его здесь.",
        allHeroesTitle = "Все герои",
        addToFavoritesCd = "Добавить в избранное",
        removeFromFavoritesCd = "Убрать из избранного"
    )
    AppLanguage.FILIPINO -> FavoriteStrings(
        sectionTitle = "Mga Paborito",
        sectionCountBadge = "%d naka-save",
        sectionSubtitle = "Mabilis na access sa paborito mong mga hero",
        emptyHint = "I-tap ang heart icon sa kahit anong hero card para i-save ito dito.",
        allHeroesTitle = "Lahat ng Hero",
        addToFavoritesCd = "Idagdag sa paborito",
        removeFromFavoritesCd = "Alisin sa paborito"
    )
    AppLanguage.MALAY -> FavoriteStrings(
        sectionTitle = "Kegemaran",
        sectionCountBadge = "%d disimpan",
        sectionSubtitle = "Akses pantas ke hero utama anda",
        emptyHint = "Ketik ikon hati pada mana-mana kad hero untuk simpan di sini.",
        allHeroesTitle = "Semua Hero",
        addToFavoritesCd = "Tambah ke kegemaran",
        removeFromFavoritesCd = "Buang daripada kegemaran"
    )
    AppLanguage.PORTUGUESE_BR -> FavoriteStrings(
        sectionTitle = "Favoritos",
        sectionCountBadge = "%d salvos",
        sectionSubtitle = "Acesso rápido aos seus heróis mais jogados",
        emptyHint = "Toque no ícone de coração em qualquer card para salvar seus heróis aqui.",
        allHeroesTitle = "Todos os Heróis",
        addToFavoritesCd = "Adicionar aos favoritos",
        removeFromFavoritesCd = "Remover dos favoritos"
    )
    AppLanguage.SPANISH -> FavoriteStrings(
        sectionTitle = "Favoritos",
        sectionCountBadge = "%d guardados",
        sectionSubtitle = "Acceso rápido a tus héroes más jugados",
        emptyHint = "Toca el ícono de corazón en cualquier tarjeta para guardar tus héroes aquí.",
        allHeroesTitle = "Todos los Héroes",
        addToFavoritesCd = "Añadir a favoritos",
        removeFromFavoritesCd = "Quitar de favoritos"
    )
}
