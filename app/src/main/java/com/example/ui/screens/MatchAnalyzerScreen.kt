package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppLanguage
import com.example.i18n.LocalAppStrings
import com.example.model.AsyncUiState
import com.example.model.CoachChatMessage
import com.example.model.MatchAnalysisReport
import com.example.ui.components.ClearHistoryConfirmDialog
import com.example.ui.components.GamingErrorCard
import com.example.ui.components.HeroPickerBottomSheet
import com.example.ui.components.MatchAnalyzerModeSwitcher
import com.example.ui.components.MatchAnalyzerSkeleton
import com.example.ui.components.MatchHistoryCard
import com.example.ui.components.MatchHistoryEmptyStateCard
import com.example.ui.components.MatchHistoryHeaderBar
import com.example.ui.components.SectionHeader
import com.example.ui.components.ViewingHistoryBanner
import com.example.ui.components.isMatchVictoryResult
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
import com.example.viewmodel.MatchAnalyzerViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MatchAnalyzerScreen(
    viewModel: MatchAnalyzerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val language = LocalAppLanguage.current
    val isPersian = language == AppLanguage.PERSIAN
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showHeroPicker by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }
    var showHistoryTab by rememberSaveable { mutableStateOf(false) }
    var openedFromHistory by rememberSaveable { mutableStateOf(false) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    val isReviewingSavedHistory = !showHistoryTab &&
        openedFromHistory &&
        uiState.activeHistoryEntryId != null &&
        uiState.analysisState is AsyncUiState.Success

    BackHandler(enabled = isReviewingSavedHistory) {
        showHistoryTab = true
    }

    BackHandler(enabled = showHistoryTab) {
        showHistoryTab = false
    }

    LaunchedEffect(language) {
        viewModel.syncLanguage(language)
    }

    // Zero-permission Android Photo Picker (Google Play Policy compliant)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            openedFromHistory = false
            showHistoryTab = false
            viewModel.onScreenshotPicked(context, uri, language)
        }
    }

    if (showHeroPicker) {
        HeroPickerBottomSheet(
            title = strings.matchHeroPlayedLabel,
            onHeroSelected = { hero ->
                val formatted = if (isPersian) "${hero.nameEn} (${hero.nameFa})" else hero.nameEn
                viewModel.onHeroPlayedChange(formatted)
                showHeroPicker = false
            },
            onDismiss = { showHeroPicker = false }
        )
    }

    if (showClearAllDialog) {
        ClearHistoryConfirmDialog(
            onConfirm = {
                showClearAllDialog = false
                openedFromHistory = false
                viewModel.clearAllHistory()
            },
            onDismiss = { showClearAllDialog = false }
        )
    }

    val quickPrompts = listOf(
        strings.chatChipLateGame,
        strings.chatChipDefenseItem,
        strings.chatChipTeamfight
    )

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item(key = "match_header") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = strings.matchTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = strings.matchSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Mode Switcher: New Analysis | History (N)
        item(key = "match_mode_switcher") {
            MatchAnalyzerModeSwitcher(
                showingHistory = showHistoryTab,
                historyCount = uiState.historyEntries.size,
                onSelectNewAnalysis = {
                    if (showHistoryTab || openedFromHistory) {
                        if (openedFromHistory) {
                            viewModel.startNewAnalysis()
                            chatInput = ""
                        }
                        openedFromHistory = false
                        showHistoryTab = false
                    }
                },
                onSelectHistory = {
                    showHistoryTab = true
                }
            )
        }

        if (showHistoryTab) {
            if (uiState.isLoadingHistory) {
                item(key = "history_loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ElectricCyan)
                    }
                }
            } else if (uiState.historyEntries.isEmpty()) {
                item(key = "history_empty_state") {
                    MatchHistoryEmptyStateCard(
                        onStartAnalysisClick = {
                            openedFromHistory = false
                            showHistoryTab = false
                        }
                    )
                }
            } else {
                item(key = "history_header_bar") {
                    MatchHistoryHeaderBar(
                        totalCount = uiState.historyEntries.size,
                        onClearAllClick = { showClearAllDialog = true }
                    )
                }

                items(
                    items = uiState.historyEntries,
                    key = { it.id }
                ) { entry ->
                    MatchHistoryCard(
                        entry = entry,
                        isActive = uiState.activeHistoryEntryId == entry.id,
                        loadThumbnail = viewModel::loadHistoryThumbnail,
                        onOpen = {
                            chatInput = ""
                            openedFromHistory = true
                            showHistoryTab = false
                            viewModel.openHistoryEntry(entry, language)
                        },
                        onDelete = {
                            viewModel.deleteHistoryEntry(entry.id)
                        }
                    )
                }
            }
        } else {
            if (isReviewingSavedHistory) {
                item(key = "viewing_history_banner") {
                    ViewingHistoryBanner(
                        timestampMillis = uiState.activeHistoryTimestamp,
                        showBackToHistory = true,
                        onBackToHistory = { showHistoryTab = true },
                        onStartNewAnalysis = {
                            openedFromHistory = false
                            chatInput = ""
                            viewModel.startNewAnalysis()
                        }
                    )
                }
            }

            // Screenshot Upload / Preview Card
            if (!isReviewingSavedHistory || uiState.selectedBitmap != null || uiState.isDecodingImage) {
                item(key = "screenshot_uploader") {
                    val bitmap = uiState.selectedBitmap
                    if (bitmap == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.5.dp, BorderCyan, RoundedCornerShape(20.dp))
                                .clickable(enabled = !isReviewingSavedHistory) {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .testTag("match_pick_image_card")
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_match_analyzer_art_1795130938825),
                                contentDescription = strings.cdBannerArt,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(DeepNavy950.copy(alpha = 0.72f))
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (uiState.isDecodingImage) {
                                    CircularProgressIndicator(color = ElectricCyan)
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(ElectricCyan.copy(alpha = 0.2f))
                                            .border(1.5.dp, ElectricCyan, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AddPhotoAlternate,
                                            contentDescription = null,
                                            tint = ElectricCyan,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = strings.matchPickImageCta,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = strings.matchSupportedFormats,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = DeepNavy800),
                            border = BorderStroke(1.dp, BorderGold)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(205.dp)
                                ) {
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = strings.cdSelectedScreenshot,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    if (!isReviewingSavedHistory) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = DeepNavy950.copy(alpha = 0.86f),
                                            border = BorderStroke(1.dp, BorderCyan),
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(10.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable {
                                                    photoPickerLauncher.launch(
                                                        PickVisualMediaRequest(
                                                            ActivityResultContracts.PickVisualMedia.ImageOnly
                                                        )
                                                    )
                                                }
                                                .testTag("match_change_image_button")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.PhotoLibrary,
                                                    contentDescription = null,
                                                    tint = ElectricCyan,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = strings.matchChangeImageCta,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = ElectricCyan
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

        if (!uiState.imageDecodeError.isNullOrBlank()) {
            item(key = "decode_error") {
                Text(
                    text = uiState.imageDecodeError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = CrimsonAccent
                )
            }
        }

        // Hero Played Input + Quick Picker Button + Analyze CTA (hidden while reviewing saved history)
        if (!isReviewingSavedHistory) {
            item(key = "hero_played_input") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = strings.matchHeroPlayedLabel,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.heroPlayedInput,
                            onValueChange = viewModel::onHeroPlayedChange,
                            placeholder = {
                                Text(
                                    text = strings.matchHeroPlayedHint,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = null,
                                    tint = RoyalGold
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalGold,
                                unfocusedBorderColor = BorderSubtle,
                                focusedContainerColor = DeepNavy800,
                                unfocusedContainerColor = DeepNavy800
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("match_hero_input")
                        )
                        OutlinedButton(
                            onClick = { showHeroPicker = true },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, BorderCyan),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("match_hero_picker_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ViewList,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strings.matchSelectFromList,
                                style = MaterialTheme.typography.labelMedium,
                                color = ElectricCyan
                            )
                        }
                    }
                }
            }

            // Analyze Match Button
            item(key = "match_analyze_cta") {
                Button(
                    onClick = {
                        openedFromHistory = false
                        viewModel.analyzeMatch(language)
                    },
                    enabled = uiState.selectedBitmap != null && uiState.analysisState !is AsyncUiState.Loading,
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
                        .testTag("match_analyze_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.matchAnalyzeCta,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Analysis Results
        when (val analysisState = uiState.analysisState) {
            is AsyncUiState.Idle -> Unit

            is AsyncUiState.Loading -> {
                item(key = "match_loading") {
                    MatchAnalyzerSkeleton()
                }
            }

            is AsyncUiState.Error -> {
                item(key = "match_error") {
                    GamingErrorCard(
                        message = analysisState.message,
                        onRetry = { viewModel.analyzeMatch(language) }
                    )
                }
            }

            is AsyncUiState.Success -> {
                val report = analysisState.data

                item(key = "match_stats_card") {
                    ExtractedStatsCard(report = report)
                }

                item(key = "match_strengths_card") {
                    CoachingBulletCard(
                        title = strings.matchStrengthsTitle,
                        icon = Icons.Rounded.CheckCircle,
                        accentColor = EmeraldAccent,
                        containerColor = EmeraldContainer.copy(alpha = 0.55f),
                        points = report.strengths
                    )
                }

                item(key = "match_mistakes_card") {
                    CoachingBulletCard(
                        title = strings.matchMistakesTitle,
                        icon = Icons.Rounded.WarningAmber,
                        accentColor = CrimsonAccent,
                        containerColor = CrimsonContainer.copy(alpha = 0.55f),
                        points = report.mistakes
                    )
                }

                item(key = "match_goal_card") {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = RoyalGoldContainer),
                        border = BorderStroke(1.5.dp, RoyalGold)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SectionHeader(
                                title = strings.matchNextGoalTitle,
                                icon = Icons.Rounded.Flag,
                                accentColor = RoyalGold
                            )
                            Text(
                                text = report.nextMatchGoal,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                item(key = "followup_chat_section") {
                    FollowUpCoachChatSection(
                        messages = uiState.chatMessages,
                        isResponding = uiState.isChatResponding,
                        chatError = uiState.chatError,
                        quickPrompts = quickPrompts,
                        inputText = chatInput,
                        onInputChange = { chatInput = it },
                        onSendMessage = { msg ->
                            viewModel.sendFollowUpQuestion(msg, language)
                            chatInput = ""
                        }
                    )
                }
            }
        }
        }

        item(key = "match_bottom_spacer") {
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExtractedStatsCard(report: MatchAnalysisReport) {
    val strings = LocalAppStrings.current

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
            SectionHeader(
                title = strings.matchStatsTitle,
                icon = Icons.Rounded.Analytics,
                accentColor = ElectricCyan,
                subtitle = "${report.heroPlayed} • ${report.medalFa}"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatTile(
                    label = strings.matchResultLabel,
                    value = report.matchResultFa,
                    valueColor = if (isMatchVictoryResult(report.matchResultFa)) EmeraldAccent else CrimsonAccent,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = strings.matchKdaLabel,
                    value = report.kda,
                    valueColor = RoyalGold,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = strings.matchGoldLabel,
                    value = report.gold,
                    valueColor = ElectricCyan,
                    modifier = Modifier.weight(1f)
                )
            }

            if (report.detectedItems.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = strings.matchYourItemsLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        report.detectedItems.forEach { item ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DeepNavy700,
                                border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RoyalGold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (report.enemyHeroes.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = strings.matchEnemyLineupLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        report.enemyHeroes.forEach { enemy ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CrimsonContainer,
                                border = BorderStroke(1.dp, CrimsonAccent.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = enemy,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CrimsonAccent,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = DeepNavy900,
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = valueColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CoachingBulletCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    containerColor: Color,
    points: List<String>
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionHeader(title = title, icon = icon, accentColor = accentColor)
            points.forEachIndexed { idx, point ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${idx + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = point,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FollowUpCoachChatSection(
    messages: List<CoachChatMessage>,
    isResponding: Boolean,
    chatError: String?,
    quickPrompts: List<String>,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendMessage: (String) -> Unit
) {
    val strings = LocalAppStrings.current

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy800),
        border = BorderStroke(1.dp, BorderCyan)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(
                title = strings.chatSectionTitle,
                subtitle = strings.chatSectionSubtitle,
                icon = Icons.Rounded.Forum,
                accentColor = ElectricCyan
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(quickPrompts) { prompt ->
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = DeepNavy900,
                        border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable(enabled = !isResponding) { onSendMessage(prompt) }
                            .testTag("chat_quick_chip_${quickPrompts.indexOf(prompt)}")
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall,
                            color = RoyalGold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                messages.forEach { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (msg.isUser) 16.dp else 4.dp,
                                bottomEnd = if (msg.isUser) 4.dp else 16.dp
                            ),
                            color = if (msg.isUser) ElectricCyanContainer else DeepNavy900,
                            border = BorderStroke(
                                1.dp,
                                if (msg.isUser) BorderCyan else BorderSubtle
                            ),
                            modifier = Modifier.fillMaxWidth(0.92f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (msg.isUser) strings.chatUserLabel else strings.chatCoachLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (msg.isUser) ElectricCyan else RoyalGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.text.ifBlank { strings.chatTypingPlaceholder },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            if (!chatError.isNullOrBlank()) {
                Text(
                    text = chatError,
                    style = MaterialTheme.typography.bodySmall,
                    color = CrimsonAccent
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    placeholder = {
                        Text(
                            text = strings.chatInputPlaceholder,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    },
                    maxLines = 3,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedContainerColor = DeepNavy900,
                        unfocusedContainerColor = DeepNavy900
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("match_chat_input")
                )
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                        }
                    },
                    enabled = inputText.isNotBlank() && !isResponding,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = RoyalGold,
                        contentColor = DeepNavy950,
                        disabledContainerColor = DeepNavy700,
                        disabledContentColor = TextMuted
                    ),
                    modifier = Modifier
                        .size(50.dp)
                        .testTag("match_chat_send_button")
                ) {
                    if (isResponding) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = DeepNavy950,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Send,
                            contentDescription = strings.chatSendCd
                        )
                    }
                }
            }
        }
    }
}
