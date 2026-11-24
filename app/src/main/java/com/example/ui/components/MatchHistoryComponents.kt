package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppLanguage
import com.example.i18n.matchHistoryStrings
import com.example.model.MatchHistoryEntry
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
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldContainer
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import java.text.DateFormat
import java.util.Date

private val VICTORY_KEYWORDS = listOf(
    "victory", "win", "won", "برد", "پیروزی", "فوز", "انتصار",
    "menang", "sieg", "gewonnen", "胜", "побед", "panalo",
    "vitória", "vitoria", "victoria", "zafer", "galibiyet"
)

internal fun isMatchVictoryResult(resultText: String): Boolean {
    val normalized = resultText.lowercase()
    return VICTORY_KEYWORDS.any { normalized.contains(it) }
}

internal fun formatMatchTimestamp(timestampMillis: Long, language: AppLanguage): String {
    if (timestampMillis <= 0L) return ""
    return try {
        val formatter = DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM,
            DateFormat.SHORT,
            language.javaLocale
        )
        formatter.format(Date(timestampMillis))
    } catch (_: Exception) {
        ""
    }
}

@Composable
fun MatchAnalyzerModeSwitcher(
    showingHistory: Boolean,
    historyCount: Int,
    onSelectNewAnalysis: () -> Unit,
    onSelectHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language = LocalAppLanguage.current
    val historyStrings = remember(language) { language.matchHistoryStrings() }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = DeepNavy900,
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModeSwitchSegment(
                label = historyStrings.modeNewAnalysis,
                icon = Icons.Rounded.AutoAwesome,
                selected = !showingHistory,
                accentColor = RoyalGold,
                selectedContainer = RoyalGoldContainer,
                badgeCount = null,
                onClick = onSelectNewAnalysis,
                modifier = Modifier
                    .weight(1f)
                    .testTag("match_mode_new")
            )
            ModeSwitchSegment(
                label = historyStrings.modeHistory,
                icon = Icons.Rounded.History,
                selected = showingHistory,
                accentColor = ElectricCyan,
                selectedContainer = ElectricCyanContainer,
                badgeCount = historyCount.takeIf { it > 0 },
                onClick = onSelectHistory,
                modifier = Modifier
                    .weight(1f)
                    .testTag("match_mode_history")
            )
        }
    }
}

@Composable
private fun ModeSwitchSegment(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    accentColor: Color,
    selectedContainer: Color,
    badgeCount: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) selectedContainer else Color.Transparent,
        border = if (selected) BorderStroke(1.dp, accentColor.copy(alpha = 0.65f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) accentColor else TextMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) Color.White else TextSecondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (badgeCount != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = CircleShape,
                    color = if (selected) accentColor else DeepNavy700
                ) {
                    Text(
                        text = badgeCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) DeepNavy950 else ElectricCyan,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ViewingHistoryBanner(
    timestampMillis: Long?,
    showBackToHistory: Boolean,
    onBackToHistory: () -> Unit,
    onStartNewAnalysis: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language = LocalAppLanguage.current
    val historyStrings = remember(language) { language.matchHistoryStrings() }
    val formattedDate = remember(timestampMillis, language) {
        timestampMillis?.let { formatMatchTimestamp(it, language) }.orEmpty()
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ElectricCyanContainer.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, BorderCyan)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.History,
                contentDescription = null,
                tint = ElectricCyan,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = if (formattedDate.isNotBlank()) {
                    language.format(historyStrings.historyViewingBanner, formattedDate)
                } else {
                    historyStrings.modeHistory
                },
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (showBackToHistory) {
                OutlinedButton(
                    onClick = onBackToHistory,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderCyan),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("history_back_to_list_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = historyStrings.historyBackToList,
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Button(
                onClick = onStartNewAnalysis,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalGold,
                    contentColor = DeepNavy950
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("history_start_new_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = historyStrings.historyStartNewCta,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MatchHistoryHeaderBar(
    totalCount: Int,
    onClearAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language = LocalAppLanguage.current
    val historyStrings = remember(language) { language.matchHistoryStrings() }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = historyStrings.historyTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = ElectricCyanContainer,
                    border = BorderStroke(1.dp, BorderCyan.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = language.format(historyStrings.historyCountBadge, totalCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = historyStrings.historySubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        TextButton(
            onClick = onClearAllClick,
            colors = ButtonDefaults.textButtonColors(contentColor = CrimsonAccent),
            modifier = Modifier
                .heightIn(min = 48.dp)
                .testTag("history_clear_all_button")
        ) {
            Icon(
                imageVector = Icons.Rounded.DeleteSweep,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = historyStrings.historyClearAll,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun MatchHistoryEmptyStateCard(
    onStartAnalysisClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language = LocalAppLanguage.current
    val historyStrings = remember(language) { language.matchHistoryStrings() }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy800),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ElectricCyan.copy(alpha = 0.22f),
                                RoyalGold.copy(alpha = 0.18f)
                            )
                        )
                    )
                    .border(1.dp, BorderCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = historyStrings.historyEmptyTitle,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = historyStrings.historyEmptySubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onStartAnalysisClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalGold,
                    contentColor = DeepNavy950
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("history_empty_cta")
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = historyStrings.historyEmptyCta,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MatchHistoryCard(
    entry: MatchHistoryEntry,
    isActive: Boolean,
    loadThumbnail: suspend (String) -> Bitmap?,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language = LocalAppLanguage.current
    val historyStrings = remember(language) { language.matchHistoryStrings() }
    val isVictory = remember(entry.report.matchResultFa) {
        isMatchVictoryResult(entry.report.matchResultFa)
    }
    val formattedDate = remember(entry.analyzedAtMillis, language) {
        formatMatchTimestamp(entry.analyzedAtMillis, language)
    }

    val thumbnailBitmap by produceState<Bitmap?>(
        initialValue = null,
        key1 = entry.screenshotFilePath
    ) {
        value = loadThumbnail(entry.screenshotFilePath)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onOpen)
            .testTag("history_entry_card_${entry.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy800),
        border = BorderStroke(
            width = if (isActive) 1.5.dp else 1.dp,
            color = if (isActive) RoyalGold else BorderSubtle
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Screenshot Thumbnail
            Box(
                modifier = Modifier
                    .size(width = 96.dp, height = 74.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DeepNavy900)
                    .border(
                        1.dp,
                        if (isVictory) EmeraldAccent.copy(alpha = 0.55f) else CrimsonAccent.copy(alpha = 0.55f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                val bmp = thumbnailBitmap
                if (bmp != null && !bmp.isRecycled) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = historyStrings.historyThumbnailCd,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Image,
                        contentDescription = historyStrings.historyThumbnailCd,
                        tint = TextMuted,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Result pill overlay at bottom-start of thumbnail
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isVictory) EmeraldContainer.copy(alpha = 0.92f) else CrimsonContainer.copy(alpha = 0.92f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                ) {
                    Text(
                        text = entry.report.matchResultFa,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isVictory) EmeraldAccent else CrimsonAccent,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Match Metadata
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = entry.report.heroPlayed,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (entry.report.medalFa.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RoyalGoldContainer,
                            border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.45f))
                        ) {
                            Text(
                                text = entry.report.medalFa,
                                style = MaterialTheme.typography.labelSmall,
                                color = RoyalGold,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = entry.report.kda,
                        style = MaterialTheme.typography.labelMedium,
                        color = RoyalGold,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    if (entry.report.gold.isNotBlank()) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextMuted
                        )
                        Text(
                            text = entry.report.gold,
                            style = MaterialTheme.typography.labelMedium,
                            color = ElectricCyan,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (formattedDate.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Schedule,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Delete & Open indicator
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("history_delete_${entry.id}")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = historyStrings.historyDeleteCd,
                        tint = CrimsonAccent.copy(alpha = 0.85f),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = historyStrings.historyOpenCd,
                    tint = ElectricCyan,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun ClearHistoryConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val language = LocalAppLanguage.current
    val historyStrings = remember(language) { language.matchHistoryStrings() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepNavy800,
        titleContentColor = Color.White,
        textContentColor = TextSecondary,
        icon = {
            Icon(
                imageVector = Icons.Rounded.DeleteSweep,
                contentDescription = null,
                tint = CrimsonAccent,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Text(
                text = historyStrings.historyClearConfirmTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = historyStrings.historyClearConfirmBody,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CrimsonAccent,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("history_clear_confirm_button")
            ) {
                Text(
                    text = historyStrings.historyConfirmDelete,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary),
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("history_clear_cancel_button")
            ) {
                Text(text = historyStrings.historyCancel)
            }
        }
    )
}
