package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Dangerous
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppLanguage
import com.example.i18n.LocalAppStrings
import com.example.i18n.localizedName
import com.example.i18n.quickCheckStrings
import com.example.model.HeroQuickCheckSummary
import com.example.model.MlbbHero
import com.example.ui.theme.BorderCyan
import com.example.ui.theme.BorderGold
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.DeepNavy700
import com.example.ui.theme.DeepNavy800
import com.example.ui.theme.DeepNavy900
import com.example.ui.theme.DeepNavy950
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanContainer
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent
import com.example.viewmodel.HeroQuickCheckUiState

@Composable
fun HeroQuickCheckDialog(
    hero: MlbbHero,
    quickCheckState: HeroQuickCheckUiState,
    isFavorite: Boolean,
    isSharing: Boolean,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit,
    onOpenFullGuide: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    val language = LocalAppLanguage.current
    val strings = LocalAppStrings.current
    val qcStrings = remember(language) { language.quickCheckStrings() }
    val isPersian = language == AppLanguage.PERSIAN
    val accent = hero.primaryRole.accentColor

    val heroTitle = if (isPersian && hero.nameFa != hero.nameEn) {
        "${hero.nameFa} (${hero.nameEn})"
    } else {
        hero.nameEn
    }
    val roleLaneSubtitle = "${hero.localizedRolesText { it.localizedName(strings) }} • ${
        hero.lanes.joinToString(" / ") { it.localizedName(strings) }
    }"
    val tierBadge = (quickCheckState as? HeroQuickCheckUiState.Success)?.summary?.tier

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .widthIn(max = 460.dp)
                .testTag("hero_quick_check_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy800),
            border = BorderStroke(1.2.dp, BorderGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Accent Strip
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(accent, RoyalGold, ElectricCyan)
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header Row: Quick-Check badge + Tier + Favorite/Share/Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = ElectricCyanContainer,
                                border = BorderStroke(1.dp, BorderCyan)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Bolt,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = qcStrings.badgeTitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ElectricCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (!tierBadge.isNullOrBlank()) {
                                val tierColor = when {
                                    tierBadge.startsWith("S", ignoreCase = true) -> RoyalGold
                                    tierBadge.startsWith("A", ignoreCase = true) -> ElectricCyan
                                    tierBadge.startsWith("B", ignoreCase = true) -> EmeraldAccent
                                    else -> TextSecondary
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = tierColor.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, tierColor.copy(alpha = 0.65f))
                                ) {
                                    Text(
                                        text = tierBadge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = tierColor,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            HeroShareButton(
                                onClick = onShare,
                                isLoading = isSharing,
                                testTag = "quick_check_share_button"
                            )
                            FavoriteHeartButton(
                                isFavorite = isFavorite,
                                onToggle = onToggleFavorite,
                                testTag = "quick_check_favorite_button"
                            )
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("quick_check_close_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = qcStrings.closeCd,
                                    tint = TextSecondary
                                )
                            }
                        }
                    }

                    // Hero Identity Banner
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HeroEmblemBadge(
                            nameEn = hero.nameEn,
                            accentColor = accent,
                            size = 50.dp
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = heroTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = roleLaneSubtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = ElectricCyan,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // 3-Point Quick Summary Body
                    when (quickCheckState) {
                        is HeroQuickCheckUiState.Idle,
                        is HeroQuickCheckUiState.Loading -> {
                            QuickCheckLoadingSkeleton(hintText = qcStrings.loadingHint)
                        }

                        is HeroQuickCheckUiState.Error -> {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = CrimsonContainer.copy(alpha = 0.55f),
                                border = BorderStroke(1.dp, CrimsonAccent.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = quickCheckState.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                    OutlinedButton(
                                        onClick = onRetry,
                                        border = BorderStroke(1.dp, CrimsonAccent),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .heightIn(min = 48.dp)
                                            .testTag("quick_check_retry_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Refresh,
                                            contentDescription = null,
                                            tint = CrimsonAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = qcStrings.retryCta,
                                            color = CrimsonAccent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        is HeroQuickCheckUiState.Success -> {
                            QuickCheckThreePoints(
                                summary = quickCheckState.summary,
                                coreItemLabel = qcStrings.coreItemLabel,
                                mainComboLabel = qcStrings.mainComboLabel,
                                primaryCounterLabel = qcStrings.primaryCounterLabel
                            )
                        }
                    }

                    // Primary CTA: Open Full Hero Detail Screen
                    Button(
                        onClick = onOpenFullGuide,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoyalGold,
                            contentColor = DeepNavy950
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp)
                            .testTag("quick_check_full_guide_button")
                    ) {
                        Text(
                            text = qcStrings.openFullGuideCta,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickCheckThreePoints(
    summary: HeroQuickCheckSummary,
    coreItemLabel: String,
    mainComboLabel: String,
    primaryCounterLabel: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        QuickCheckPointCard(
            label = coreItemLabel,
            headline = summary.coreItemName,
            note = summary.coreItemNote,
            icon = Icons.Rounded.Build,
            accentColor = RoyalGold,
            containerColor = RoyalGoldContainer.copy(alpha = 0.45f),
            testTag = "quick_check_point_core_item"
        )
        QuickCheckPointCard(
            label = mainComboLabel,
            headline = summary.mainCombo,
            note = summary.mainComboNote,
            icon = Icons.Rounded.SportsEsports,
            accentColor = VioletAccent,
            containerColor = DeepNavy900,
            testTag = "quick_check_point_combo"
        )
        QuickCheckPointCard(
            label = primaryCounterLabel,
            headline = summary.primaryCounter,
            note = summary.primaryCounterNote,
            icon = Icons.Rounded.Dangerous,
            accentColor = CrimsonAccent,
            containerColor = CrimsonContainer.copy(alpha = 0.48f),
            testTag = "quick_check_point_counter"
        )
    }
}

@Composable
private fun QuickCheckPointCard(
    label: String,
    headline: String,
    note: String,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.18f))
                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = headline,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                if (note.isNotBlank()) {
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickCheckLoadingSkeleton(hintText: String) {
    val transition = rememberInfiniteTransition(label = "quick_check_shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.78f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 750),
            repeatMode = RepeatMode.Reverse
        ),
        label = "quick_check_alpha"
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = hintText,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        repeat(3) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DeepNavy900,
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DeepNavy700.copy(alpha = alpha))
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.36f)
                                .height(10.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(DeepNavy700.copy(alpha = alpha))
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(DeepNavy700.copy(alpha = alpha))
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .height(10.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(DeepNavy700.copy(alpha = alpha * 0.75f))
                        )
                    }
                }
            }
        }
    }
}
