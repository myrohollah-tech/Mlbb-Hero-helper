package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppLanguage
import com.example.i18n.LocalAppStrings
import com.example.i18n.favoriteStrings
import com.example.i18n.localizedName
import com.example.model.MlbbHero
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.mlbbColors

@Composable
fun FavoriteHeartButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val language = LocalAppLanguage.current
    val favStrings = language.favoriteStrings()
    val mlbbColors = MaterialTheme.mlbbColors

    val iconTint by animateColorAsState(
        targetValue = if (isFavorite) CrimsonAccent else mlbbColors.textMuted,
        label = "favorite_heart_tint"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.12f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "favorite_heart_scale"
    )

    IconButton(
        onClick = onToggle,
        modifier = modifier
            .size(48.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    if (isFavorite) CrimsonContainer.copy(alpha = 0.85f)
                    else MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = if (isFavorite) favStrings.removeFromFavoritesCd else favStrings.addToFavoritesCd,
                tint = iconTint,
                modifier = Modifier
                    .size(19.dp)
                    .scale(iconScale)
            )
        }
    }
}

@Composable
fun FavoritesHeroSection(
    favoriteHeroes: List<MlbbHero>,
    onHeroClick: (MlbbHero) -> Unit,
    onToggleFavorite: (String) -> Unit,
    sharingHeroId: String? = null,
    onShareHero: (MlbbHero) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val language = LocalAppLanguage.current
    val favStrings = language.favoriteStrings()
    val colorScheme = MaterialTheme.colorScheme
    val mlbbColors = MaterialTheme.mlbbColors

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("favorites_section"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainer),
        border = BorderStroke(
            width = 1.dp,
            color = if (favoriteHeroes.isNotEmpty()) CrimsonAccent.copy(alpha = 0.45f) else mlbbColors.borderSubtle
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CrimsonContainer)
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Favorite,
                            contentDescription = null,
                            tint = CrimsonAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = favStrings.sectionTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = favStrings.sectionSubtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (favoriteHeroes.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = CrimsonContainer,
                        border = BorderStroke(1.dp, CrimsonAccent.copy(alpha = 0.45f))
                    ) {
                        Text(
                            text = language.format(favStrings.sectionCountBadge, favoriteHeroes.size),
                            style = MaterialTheme.typography.labelSmall,
                            color = CrimsonAccent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (favoriteHeroes.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = colorScheme.surface,
                    border = BorderStroke(1.dp, mlbbColors.borderSubtle),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .testTag("favorites_empty_state")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FavoriteBorder,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = favStrings.emptyHint,
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.testTag("favorites_carousel")
                ) {
                    items(favoriteHeroes, key = { "fav_${it.id}" }) { hero ->
                        FavoriteHeroQuickCard(
                            hero = hero,
                            isSharing = sharingHeroId == hero.id,
                            onClick = { onHeroClick(hero) },
                            onRemoveFavorite = { onToggleFavorite(hero.id) },
                            onShare = { onShareHero(hero) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteHeroQuickCard(
    hero: MlbbHero,
    isSharing: Boolean,
    onClick: () -> Unit,
    onRemoveFavorite: () -> Unit,
    onShare: () -> Unit
) {
    val strings = LocalAppStrings.current
    val isPersian = LocalAppLanguage.current == AppLanguage.PERSIAN
    val colorScheme = MaterialTheme.colorScheme
    val mlbbColors = MaterialTheme.mlbbColors
    val accent = hero.primaryRole.accentColor
    val primaryName = hero.primaryDisplayName(isPersian)
    val subtitleName = hero.subtitleDisplayName(isPersian)

    Card(
        modifier = Modifier
            .width(164.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("favorite_hero_card_${hero.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        border = BorderStroke(1.dp, mlbbColors.borderGold)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(CrimsonAccent, RoyalGold, accent)
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.14f),
                                colorScheme.surface
                            )
                        )
                    )
                    .padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HeroEmblemBadge(
                        nameEn = hero.nameEn,
                        accentColor = accent,
                        size = 38.dp
                    )
                    FavoriteHeartButton(
                        isFavorite = true,
                        onToggle = onRemoveFavorite,
                        testTag = "favorite_section_remove_${hero.id}"
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = primaryName,
                    style = MaterialTheme.typography.titleSmall,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 6.dp)
                )
                if (subtitleName.isNotEmpty()) {
                    Text(
                        text = subtitleName,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = accent.copy(alpha = 0.16f),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Text(
                            text = hero.primaryRole.localizedName(strings),
                            style = MaterialTheme.typography.labelSmall,
                            color = accent,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    HeroShareButton(
                        onClick = onShare,
                        isLoading = isSharing,
                        testTag = "favorite_share_${hero.id}"
                    )
                }
            }
        }
    }
}
