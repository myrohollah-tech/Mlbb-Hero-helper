package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.MilitaryTech
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppLanguage
import com.example.i18n.LocalAppStrings
import com.example.i18n.localizedName
import com.example.model.AsyncUiState
import com.example.model.CounterItemTip
import com.example.model.CounterPickHero
import com.example.model.MlbbHero
import com.example.model.MlbbRole
import com.example.ui.components.DraftCounterSkeleton
import com.example.ui.components.GamingErrorCard
import com.example.ui.components.HeroEmblemBadge
import com.example.ui.components.HeroPickerBottomSheet
import com.example.ui.components.SectionHeader
import com.example.ui.theme.BorderCyan
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.DeepNavy700
import com.example.ui.theme.DeepNavy800
import com.example.ui.theme.DeepNavy900
import com.example.ui.theme.DeepNavy950
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanContainer
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.DraftCounterViewModel
import com.example.viewmodel.MAX_ENEMY_HEROES

@Composable
fun DraftCounterScreen(
    viewModel: DraftCounterViewModel,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalAppLanguage.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showEnemyPicker by remember { mutableStateOf(false) }

    LaunchedEffect(language) {
        viewModel.onLanguageChanged(language)
    }

    if (showEnemyPicker) {
        HeroPickerBottomSheet(
            title = strings.draftPickerTitle,
            excludedHeroIds = uiState.enemyHeroes.map { it.id }.toSet(),
            onHeroSelected = { hero ->
                viewModel.addEnemyHero(hero)
                showEnemyPicker = false
            },
            onDismiss = { showEnemyPicker = false }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item(key = "draft_header") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = strings.draftTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = strings.draftSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Enemy 5-slot Draft Board
        item(key = "enemy_draft_board") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavy800),
                border = BorderStroke(1.dp, CrimsonAccent.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = language.format(strings.draftEnemyTeamTitle, uiState.enemyHeroes.size),
                            style = MaterialTheme.typography.titleSmall,
                            color = CrimsonAccent,
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState.enemyHeroes.isNotEmpty()) {
                            TextButton(
                                onClick = viewModel::clearEnemyHeroes,
                                modifier = Modifier.testTag("draft_clear_all_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.DeleteOutline,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strings.draftClearAll,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (slotIndex in 0 until MAX_ENEMY_HEROES) {
                            val enemy = uiState.enemyHeroes.getOrNull(slotIndex)
                            EnemyDraftSlot(
                                hero = enemy,
                                slotNumber = slotIndex + 1,
                                onAddClick = { showEnemyPicker = true },
                                onRemoveClick = { enemy?.let { viewModel.removeEnemyHero(it.id) } },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (uiState.enemyHeroes.isEmpty()) {
                        Text(
                            text = strings.draftEmptyHint,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Preferred Role Grid (3 x 2)
        item(key = "role_selector") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = strings.draftYourRoleTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                val roleRows = MlbbRole.entries.chunked(3)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    roleRows.forEach { rowRoles ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowRoles.forEach { role ->
                                val selected = uiState.preferredRole == role
                                val roleTitle = role.localizedName(strings)
                                val showEnglishSubtitle = !roleTitle.equals(role.nameEn, ignoreCase = true)

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (selected) role.accentColor.copy(alpha = 0.22f) else DeepNavy800,
                                    border = BorderStroke(
                                        width = if (selected) 1.5.dp else 1.dp,
                                        color = if (selected) role.accentColor else BorderSubtle
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 52.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { viewModel.selectPreferredRole(role) }
                                        .testTag("draft_role_${role.nameEn.lowercase()}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = roleTitle,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (selected) role.accentColor else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (showEnglishSubtitle) {
                                            Text(
                                                text = role.nameEn,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextMuted,
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

        // Analyze Button
        item(key = "analyze_cta") {
            Button(
                onClick = { viewModel.analyzeDraft(language) },
                enabled = uiState.enemyHeroes.isNotEmpty() && uiState.resultState !is AsyncUiState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalGold,
                    contentColor = DeepNavy950,
                    disabledContainerColor = DeepNavy700,
                    disabledContentColor = TextMuted
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("draft_analyze_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.draftAnalyzeCta,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Result State
        when (val resultState = uiState.resultState) {
            is AsyncUiState.Idle -> Unit

            is AsyncUiState.Loading -> {
                item(key = "draft_loading") {
                    DraftCounterSkeleton()
                }
            }

            is AsyncUiState.Error -> {
                item(key = "draft_error") {
                    GamingErrorCard(
                        message = resultState.message,
                        onRetry = { viewModel.analyzeDraft(language) }
                    )
                }
            }

            is AsyncUiState.Success -> {
                val result = resultState.data

                if (result.enemyCompAnalysisFa.isNotBlank()) {
                    item(key = "enemy_comp_analysis") {
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
                                SectionHeader(
                                    title = strings.draftEnemySummaryTitle,
                                    icon = Icons.Rounded.Psychology,
                                    accentColor = ElectricCyan
                                )
                                Text(
                                    text = result.enemyCompAnalysisFa,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                if (result.topPicks.isNotEmpty()) {
                    item(key = "top_picks_header") {
                        SectionHeader(
                            title = strings.draftTopPicksTitle,
                            icon = Icons.Rounded.MilitaryTech,
                            accentColor = RoyalGold
                        )
                    }
                    result.topPicks.forEachIndexed { index, pick ->
                        item(key = "counter_pick_$index") {
                            CounterPickCard(rank = index + 1, pick = pick)
                        }
                    }
                }

                if (result.counterItems.isNotEmpty()) {
                    item(key = "counter_items_header") {
                        SectionHeader(
                            title = strings.draftCounterItemsTitle,
                            icon = Icons.Rounded.Shield,
                            accentColor = EmeraldAccent
                        )
                    }
                    result.counterItems.forEachIndexed { index, itemTip ->
                        item(key = "counter_item_$index") {
                            CounterItemCard(item = itemTip)
                        }
                    }
                }
            }
        }

        item(key = "draft_bottom_spacer") {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun EnemyDraftSlot(
    hero: MlbbHero?,
    slotNumber: Int,
    onAddClick: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalAppLanguage.current
    val isPersian = language == AppLanguage.PERSIAN

    if (hero == null) {
        Box(
            modifier = modifier
                .height(92.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(DeepNavy900)
                .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                .clickable(onClick = onAddClick)
                .testTag("enemy_slot_empty_$slotNumber"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(DeepNavy700),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = strings.draftAddEnemy,
                        tint = ElectricCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = language.format(strings.draftEnemySlotLabel, slotNumber),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .height(92.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            hero.primaryRole.accentColor.copy(alpha = 0.2f),
                            DeepNavy900
                        )
                    )
                )
                .border(1.dp, hero.primaryRole.accentColor.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                .clickable(onClick = onRemoveClick)
                .testTag("enemy_slot_filled_${hero.id}")
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(CrimsonAccent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = strings.cdRemoveHero,
                    tint = DeepNavy950,
                    modifier = Modifier.size(12.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                HeroEmblemBadge(
                    nameEn = hero.nameEn,
                    accentColor = hero.primaryRole.accentColor,
                    size = 38.dp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = hero.primaryDisplayName(isPersian),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CounterPickCard(
    rank: Int,
    pick: CounterPickHero
) {
    val rankColor = when (rank) {
        1 -> RoyalGold
        2 -> ElectricCyan
        else -> EmeraldAccent
    }

    val heroDisplayTitle = if (
        pick.heroNameFa.isBlank() ||
        pick.heroNameFa.equals(pick.heroNameEn, ignoreCase = true)
    ) {
        pick.heroNameEn.ifBlank { pick.heroNameFa }
    } else if (pick.heroNameEn.isBlank()) {
        pick.heroNameFa
    } else {
        "${pick.heroNameFa} (${pick.heroNameEn})"
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy800),
        border = BorderStroke(1.dp, rankColor.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            rankColor.copy(alpha = 0.1f),
                            DeepNavy800
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            .clip(CircleShape)
                            .background(rankColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#$rank",
                            style = MaterialTheme.typography.labelLarge,
                            color = DeepNavy950,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = heroDisplayTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = pick.laneFa,
                            style = MaterialTheme.typography.labelSmall,
                            color = rankColor
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = rankColor.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, rankColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = pick.matchAdvantageFa,
                        style = MaterialTheme.typography.labelSmall,
                        color = rankColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = pick.reasonFa,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (pick.keyTipFa.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DeepNavy900,
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.TipsAndUpdates,
                            contentDescription = null,
                            tint = RoyalGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = pick.keyTipFa,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CounterItemCard(item: CounterItemTip) {
    val strings = LocalAppStrings.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy800),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.itemName,
                    style = MaterialTheme.typography.titleSmall,
                    color = RoyalGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ElectricCyanContainer,
                    border = BorderStroke(1.dp, BorderCyan)
                ) {
                    Text(
                        text = item.categoryFa,
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            if (item.targetEnemiesFa.isNotBlank()) {
                Text(
                    text = "${strings.draftCounterItemAgainstPrefix} ${item.targetEnemiesFa}",
                    style = MaterialTheme.typography.labelMedium,
                    color = CrimsonAccent
                )
            }
            Text(
                text = item.reasonFa,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
