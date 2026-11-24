package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppStrings
import com.example.ui.theme.BorderCyan
import com.example.ui.theme.BorderGold
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.DeepNavy600
import com.example.ui.theme.DeepNavy700
import com.example.ui.theme.DeepNavy800
import com.example.ui.theme.DeepNavy900
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.MlbbCoachTheme
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldContainer
import com.example.ui.theme.TextSecondary

@Composable
fun rememberCoachShimmerBrush(
    highlightTint: Color = ElectricCyan
): Brush {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val transition = rememberInfiniteTransition(label = "coach_skeleton_shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = if (isRtl) 1000f else -400f,
        targetValue = if (isRtl) -400f else 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1250, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    val shimmerColors = listOf(
        DeepNavy700.copy(alpha = 0.85f),
        DeepNavy600.copy(alpha = 0.95f),
        highlightTint.copy(alpha = 0.22f),
        DeepNavy600.copy(alpha = 0.95f),
        DeepNavy700.copy(alpha = 0.85f)
    )

    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim, y = 0f),
        end = Offset(x = translateAnim + 420f, y = 260f)
    )
}

@Composable
private fun SkeletonBar(
    brush: Brush,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

@Composable
private fun SkeletonStatusBanner(
    title: String,
    subtitle: String,
    accentColor: Color = ElectricCyan
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DeepNavy800,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

/**
 * Bidirectional (RTL/LTR) Shimmer Skeleton for Tab 1: Hero Guide & Meta Build Detail
 */
@Composable
fun HeroGuideSkeleton(
    heroDisplayName: String,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val goldBrush = rememberCoachShimmerBrush(RoyalGold)
    val cyanBrush = rememberCoachShimmerBrush(ElectricCyan)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hero_guide_skeleton"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SkeletonStatusBanner(
            title = strings.heroDetailLoadingTitle.format(heroDisplayName),
            subtitle = strings.heroDetailLoadingSubtitle,
            accentColor = ElectricCyan
        )

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy800),
            border = BorderStroke(1.dp, BorderGold)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonBar(
                    brush = goldBrush,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SkeletonBar(
                        brush = goldBrush,
                        modifier = Modifier
                            .fillMaxWidth(0.45f)
                            .height(14.dp)
                    )
                    SkeletonBar(
                        brush = cyanBrush,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                    )
                    SkeletonBar(
                        brush = cyanBrush,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(12.dp)
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy800),
            border = BorderStroke(1.dp, BorderSubtle)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SkeletonBar(
                        brush = goldBrush,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    SkeletonBar(
                        brush = goldBrush,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(16.dp)
                    )
                }
                repeat(4) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SkeletonBar(
                            brush = goldBrush,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SkeletonBar(
                                brush = cyanBrush,
                                modifier = Modifier
                                    .fillMaxWidth(0.55f)
                                    .height(13.dp)
                            )
                            SkeletonBar(
                                brush = cyanBrush,
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(10.dp)
                            )
                        }
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy800),
            border = BorderStroke(1.dp, BorderCyan)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SkeletonBar(
                    brush = cyanBrush,
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(16.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) {
                        SkeletonBar(
                            brush = goldBrush,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                        )
                    }
                }
                SkeletonBar(
                    brush = cyanBrush,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(12.dp)
                )
            }
        }
    }
}

/**
 * Bidirectional (RTL/LTR) Shimmer Skeleton for Tab 2: Draft & Counter-Pick Assistant
 */
@Composable
fun DraftCounterSkeleton(
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val goldBrush = rememberCoachShimmerBrush(RoyalGold)
    val cyanBrush = rememberCoachShimmerBrush(ElectricCyan)
    val rankColors = listOf(RoyalGold, ElectricCyan, EmeraldAccent)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("draft_counter_skeleton"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SkeletonStatusBanner(
            title = strings.draftAnalyzingTitle,
            subtitle = strings.draftAnalyzingSubtitle,
            accentColor = RoyalGold
        )

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy800),
            border = BorderStroke(1.dp, BorderCyan)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SkeletonBar(
                    brush = cyanBrush,
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(15.dp)
                )
                SkeletonBar(
                    brush = cyanBrush,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                )
                SkeletonBar(
                    brush = cyanBrush,
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(12.dp)
                )
            }
        }

        rankColors.forEach { accent ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavy800),
                border = BorderStroke(1.dp, accent.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SkeletonBar(
                                brush = goldBrush,
                                shape = CircleShape,
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                SkeletonBar(
                                    brush = goldBrush,
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(14.dp)
                                )
                                SkeletonBar(
                                    brush = cyanBrush,
                                    modifier = Modifier
                                        .width(72.dp)
                                        .height(10.dp)
                                )
                            }
                        }
                        SkeletonBar(
                            brush = cyanBrush,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .width(68.dp)
                                .height(24.dp)
                        )
                    }
                    SkeletonBar(
                        brush = cyanBrush,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                    )
                    SkeletonBar(
                        brush = cyanBrush,
                        modifier = Modifier
                            .fillMaxWidth(0.82f)
                            .height(12.dp)
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy800),
            border = BorderStroke(1.dp, BorderSubtle)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SkeletonBar(
                        brush = goldBrush,
                        modifier = Modifier
                            .width(110.dp)
                            .height(14.dp)
                    )
                    SkeletonBar(
                        brush = cyanBrush,
                        modifier = Modifier
                            .width(76.dp)
                            .height(20.dp)
                    )
                }
                SkeletonBar(
                    brush = cyanBrush,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(12.dp)
                )
            }
        }
    }
}

/**
 * Bidirectional (RTL/LTR) Shimmer Skeleton for Tab 3: Match Screenshot Analyzer Report
 */
@Composable
fun MatchAnalyzerSkeleton(
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val goldBrush = rememberCoachShimmerBrush(RoyalGold)
    val cyanBrush = rememberCoachShimmerBrush(ElectricCyan)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("match_analyzer_skeleton"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SkeletonStatusBanner(
            title = strings.matchAnalyzingTitle,
            subtitle = strings.matchAnalyzingSubtitle,
            accentColor = ElectricCyan
        )

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy800),
            border = BorderStroke(1.dp, BorderCyan)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SkeletonBar(
                    brush = cyanBrush,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(16.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = DeepNavy900,
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                SkeletonBar(
                                    brush = cyanBrush,
                                    modifier = Modifier
                                        .width(36.dp)
                                        .height(10.dp)
                                )
                                SkeletonBar(
                                    brush = goldBrush,
                                    modifier = Modifier
                                        .width(52.dp)
                                        .height(16.dp)
                                )
                            }
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(4) {
                        SkeletonBar(
                            brush = goldBrush,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(24.dp)
                        )
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldContainer.copy(alpha = 0.55f)),
            border = BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SkeletonBar(
                    brush = cyanBrush,
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(16.dp)
                )
                repeat(3) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SkeletonBar(
                            brush = cyanBrush,
                            shape = CircleShape,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        SkeletonBar(
                            brush = cyanBrush,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                        )
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CrimsonContainer.copy(alpha = 0.55f)),
            border = BorderStroke(1.dp, CrimsonAccent.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SkeletonBar(
                    brush = goldBrush,
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .height(16.dp)
                )
                repeat(3) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SkeletonBar(
                            brush = goldBrush,
                            shape = CircleShape,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        SkeletonBar(
                            brush = goldBrush,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                        )
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = RoyalGoldContainer),
            border = BorderStroke(1.5.dp, BorderGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SkeletonBar(
                    brush = goldBrush,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(16.dp)
                )
                SkeletonBar(
                    brush = goldBrush,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060B16)
@Composable
private fun HeroGuideSkeletonPreview() {
    MlbbCoachTheme(language = AppLanguage.PERSIAN) {
        HeroGuideSkeleton(heroDisplayName = "فنی", modifier = Modifier.padding(16.dp))
    }
}
