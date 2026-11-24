package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Dangerous
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppLanguage
import com.example.i18n.LocalAppStrings
import com.example.i18n.localizedName
import com.example.i18n.shareStrings
import com.example.model.HeroBuildItem
import com.example.model.HeroMetaGuide
import com.example.model.MlbbHero
import com.example.ui.components.FavoriteHeartButton
import com.example.ui.components.GamingErrorCard
import com.example.ui.components.HeroEmblemBadge
import com.example.ui.components.HeroGuideSkeleton
import com.example.ui.components.HeroMetaTrendChartCard
import com.example.ui.components.HeroShareButton
import com.example.ui.components.SectionHeader
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
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldContainer
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletAccent
import com.example.util.HeroShareHelper
import com.example.viewmodel.HeroMetaUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HeroMetaDetailView(
    hero: MlbbHero,
    metaState: HeroMetaUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val language = LocalAppLanguage.current
    val shareStrings = remember(language) { language.shareStrings() }
    val isPersian = language == AppLanguage.PERSIAN
    val uriHandler = LocalUriHandler.current
    val loadedGuide = (metaState as? HeroMetaUiState.Success)?.guide

    val heroTitle = if (isPersian && hero.nameFa != hero.nameEn) {
        "${hero.nameFa} (${hero.nameEn})"
    } else {
        hero.nameEn
    }
    val rolesAndLanes = "${hero.localizedRolesText { it.localizedName(strings) }} • ${
        hero.lanes.joinToString(" / ") { it.localizedName(strings) }
    }"

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header Bar
        item(key = "detail_topbar") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("hero_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = strings.heroDetailBack,
                            tint = RoyalGold
                        )
                    }
                    HeroEmblemBadge(
                        nameEn = hero.nameEn,
                        accentColor = hero.primaryRole.accentColor,
                        size = 44.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = heroTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = rolesAndLanes,
                            style = MaterialTheme.typography.bodySmall,
                            color = ElectricCyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HeroShareButton(
                        onClick = {
                            HeroShareHelper.launchShareSheet(
                                context = context,
                                hero = hero,
                                guide = loadedGuide,
                                language = language
                            )
                        },
                        testTag = "hero_detail_share_button"
                    )
                    if (onToggleFavorite != null) {
                        FavoriteHeartButton(
                            isFavorite = isFavorite,
                            onToggle = onToggleFavorite,
                            testTag = "hero_detail_favorite_button"
                        )
                    }
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.testTag("hero_detail_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = strings.heroDetailRefresh,
                            tint = ElectricCyan
                        )
                    }
                }
            }
        }

        when (metaState) {
            is HeroMetaUiState.Idle, is HeroMetaUiState.Loading -> {
                item(key = "loading_guide") {
                    HeroGuideSkeleton(heroDisplayName = hero.primaryDisplayName(isPersian))
                }
            }

            is HeroMetaUiState.Error -> {
                item(key = "error_guide") {
                    GamingErrorCard(
                        message = metaState.message,
                        onRetry = onRefresh
                    )
                }
            }

            is HeroMetaUiState.Success -> {
                val guide = metaState.guide

                if (guide.rawFallbackFa != null) {
                    item(key = "raw_fallback") {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = DeepNavy800),
                            border = BorderStroke(1.dp, BorderGold)
                        ) {
                            Text(
                                text = guide.rawFallbackFa,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    // 1. Tier Spotlight Card
                    item(key = "tier_card") {
                        TierSpotlightCard(tier = guide.tier, summary = guide.tierSummaryFa)
                    }

                    // 1.5 Interactive 30-Day Win Rate & Pick/Ban Trend Chart
                    if (guide.trend30d.size >= 2) {
                        item(key = "meta_trend_30d") {
                            HeroMetaTrendChartCard(points = guide.trend30d)
                        }
                    }

                    // 2. Core Build
                    if (guide.coreItems.isNotEmpty()) {
                        item(key = "core_build") {
                            BuildItemsCard(
                                title = strings.heroSectionCoreBuild,
                                icon = Icons.Rounded.Build,
                                accentColor = RoyalGold,
                                items = guide.coreItems,
                                numbered = true
                            )
                        }
                    }

                    // 3. Situational Build
                    if (guide.situationalItems.isNotEmpty()) {
                        item(key = "situational_build") {
                            BuildItemsCard(
                                title = strings.heroSectionSituationalBuild,
                                icon = Icons.Rounded.Shield,
                                accentColor = ElectricCyan,
                                items = guide.situationalItems,
                                numbered = false
                            )
                        }
                    }

                    // 4. Emblem & Battle Spell
                    item(key = "emblem_spell") {
                        EmblemAndSpellSection(guide = guide)
                    }

                    // 5. Skill Combo Order
                    if (guide.skillComboSteps.isNotEmpty() || guide.comboExplanationFa.isNotBlank()) {
                        item(key = "skill_combo") {
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
                                    SectionHeader(
                                        title = strings.heroSectionCombo,
                                        icon = Icons.Rounded.SportsEsports,
                                        accentColor = VioletAccent
                                    )
                                    if (guide.skillComboSteps.isNotEmpty()) {
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            guide.skillComboSteps.forEachIndexed { idx, step ->
                                                Surface(
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = DeepNavy700,
                                                    border = BorderStroke(1.dp, VioletAccent.copy(alpha = 0.5f))
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(20.dp)
                                                                .clip(CircleShape)
                                                                .background(VioletAccent),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "${idx + 1}",
                                                                style = MaterialTheme.typography.labelSmall,
                                                                color = DeepNavy950,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = step,
                                                            style = MaterialTheme.typography.labelMedium,
                                                            color = Color.White
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (guide.comboExplanationFa.isNotBlank()) {
                                        Text(
                                            text = guide.comboExplanationFa,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 6. Early / Mid / Late Game Strategy Tabs
                    item(key = "game_phases") {
                        GamePhaseTipsCard(
                            earlyTip = guide.earlyGameTipFa,
                            midTip = guide.midGameTipFa,
                            lateTip = guide.lateGameTipFa
                        )
                    }

                    // 7. Counters & Synergies
                    if (guide.counters.isNotEmpty()) {
                        item(key = "counters") {
                            MatchupListCard(
                                title = strings.heroSectionCounters,
                                icon = Icons.Rounded.Dangerous,
                                accentColor = CrimsonAccent,
                                containerColor = CrimsonContainer.copy(alpha = 0.55f),
                                entries = guide.counters
                            )
                        }
                    }

                    if (guide.synergies.isNotEmpty()) {
                        item(key = "synergies") {
                            MatchupListCard(
                                title = strings.heroSectionSynergies,
                                icon = Icons.Rounded.GroupAdd,
                                accentColor = EmeraldAccent,
                                containerColor = EmeraldContainer.copy(alpha = 0.55f),
                                entries = guide.synergies
                            )
                        }
                    }
                }

                // 7.5 Share Full Build & Analysis CTA
                item(key = "share_full_guide_cta") {
                    Button(
                        onClick = {
                            HeroShareHelper.launchShareSheet(
                                context = context,
                                hero = hero,
                                guide = guide,
                                language = language
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoyalGold,
                            contentColor = DeepNavy950
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("hero_detail_share_cta_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = shareStrings.shareBuildCta,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 8. Live Google Search Grounding Sources
                if (guide.groundingSources.isNotEmpty()) {
                    item(key = "grounding_sources") {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = DeepNavy900),
                            border = BorderStroke(1.dp, BorderCyan)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                SectionHeader(
                                    title = strings.heroSectionSources,
                                    icon = Icons.Rounded.Public,
                                    accentColor = ElectricCyan
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    guide.groundingSources.forEach { source ->
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = DeepNavy800,
                                            border = BorderStroke(1.dp, BorderSubtle),
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable {
                                                    runCatching { uriHandler.openUri(source.uri) }
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.OpenInNew,
                                                    contentDescription = null,
                                                    tint = ElectricCyan,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = source.title,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item(key = "detail_bottom_spacer") {
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
private fun TierSpotlightCard(tier: String, summary: String) {
    val strings = LocalAppStrings.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val tierColor = when {
        tier.startsWith("S", ignoreCase = true) -> RoyalGold
        tier.startsWith("A", ignoreCase = true) -> ElectricCyan
        tier.startsWith("B", ignoreCase = true) -> EmeraldAccent
        else -> TextSecondary
    }
    val gradientColors = if (isRtl) {
        listOf(DeepNavy800, tierColor.copy(alpha = 0.14f))
    } else {
        listOf(tierColor.copy(alpha = 0.14f), DeepNavy800)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy800),
        border = BorderStroke(1.dp, tierColor.copy(alpha = 0.55f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(colors = gradientColors))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(tierColor.copy(alpha = 0.2f))
                    .border(1.5.dp, tierColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tier,
                    style = MaterialTheme.typography.headlineMedium,
                    color = tierColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.EmojiEvents,
                        contentDescription = null,
                        tint = tierColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.heroSectionTier,
                        style = MaterialTheme.typography.labelMedium,
                        color = tierColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = summary.ifBlank { strings.heroTierDefaultSummary },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun BuildItemsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    items: List<HeroBuildItem>,
    numbered: Boolean
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy800),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionHeader(title = title, icon = icon, accentColor = accentColor)
            items.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(color = DeepNavy700, thickness = 1.dp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.18f))
                            .border(1.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (numbered) "${index + 1}" else "+",
                            style = MaterialTheme.typography.labelMedium,
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        if (item.reasonFa.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.reasonFa,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmblemAndSpellSection(guide: HeroMetaGuide) {
    val strings = LocalAppStrings.current
    val language = LocalAppLanguage.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy800),
            border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SectionHeader(
                    title = strings.heroSectionEmblem,
                    icon = Icons.Rounded.AutoAwesome,
                    accentColor = RoyalGold
                )
                if (guide.emblemMain.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = RoyalGoldContainer,
                        border = BorderStroke(1.dp, BorderGold)
                    ) {
                        Text(
                            text = guide.emblemMain,
                            style = MaterialTheme.typography.titleSmall,
                            color = RoyalGold,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                guide.emblemTalents.forEachIndexed { idx, talent ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "• ${language.format(strings.heroTalentPrefix, idx + 1)} ",
                            style = MaterialTheme.typography.bodySmall,
                            color = ElectricCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = talent.replace("|", " — "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy800),
            border = BorderStroke(1.dp, BorderCyan.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SectionHeader(
                    title = strings.heroSectionSpell,
                    icon = Icons.Rounded.Bolt,
                    accentColor = ElectricCyan
                )
                if (guide.battleSpell.isNotBlank()) {
                    Text(
                        text = guide.battleSpell,
                        style = MaterialTheme.typography.titleMedium,
                        color = ElectricCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (guide.battleSpellReasonFa.isNotBlank()) {
                    Text(
                        text = guide.battleSpellReasonFa,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun GamePhaseTipsCard(
    earlyTip: String,
    midTip: String,
    lateTip: String
) {
    val strings = LocalAppStrings.current
    var selectedPhase by remember { mutableIntStateOf(0) }
    val phases = listOf(
        strings.phaseEarly to earlyTip,
        strings.phaseMid to midTip,
        strings.phaseLate to lateTip
    )

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
            SectionHeader(
                title = strings.heroSectionGamePhases,
                icon = Icons.Rounded.Timeline,
                accentColor = RoyalGold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DeepNavy900)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                phases.forEachIndexed { index, (label, _) ->
                    val isSelected = selectedPhase == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) RoyalGold else Color.Transparent)
                            .clickable { selectedPhase = index }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                            .testTag("game_phase_tab_$index"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) DeepNavy950 else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Text(
                text = phases[selectedPhase].second.ifBlank { strings.phaseEmptyFallback },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun MatchupListCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    containerColor: Color,
    entries: List<String>
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionHeader(title = title, icon = icon, accentColor = accentColor)
            entries.forEach { entry ->
                val parts = entry.split("|", limit = 2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 7.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    if (parts.size == 2) {
                        Column {
                            Text(
                                text = parts[0].trim(),
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = parts[1].trim(),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    } else {
                        Text(
                            text = entry,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
