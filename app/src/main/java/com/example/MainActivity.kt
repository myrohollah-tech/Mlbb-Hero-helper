package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.firebase.configureAppCheck
import com.example.i18n.AppLanguage
import com.example.i18n.LanguagePreferenceStore
import com.example.i18n.LocalAppLanguage
import com.example.i18n.LocalAppStrings
import com.example.ui.components.CoachTab
import com.example.ui.components.LanguagePickerModal
import com.example.ui.components.LanguageSelectorButton
import com.example.ui.components.MlbbBottomNavigationBar
import com.example.ui.components.MlbbNavigationRail
import com.example.ui.screens.CoachChatOverlayScreen
import com.example.ui.screens.DraftCounterScreen
import com.example.ui.screens.HeroGuideScreen
import com.example.ui.screens.ImageStudioOverlayScreen
import com.example.ui.screens.MatchAnalyzerScreen
import com.example.ui.theme.MlbbCoachTheme
import com.example.ui.theme.mlbbColors
import com.example.viewmodel.CoachChatViewModel
import com.example.viewmodel.DraftCounterViewModel
import com.example.viewmodel.HeroGuideViewModel
import com.example.viewmodel.HeroMetaViewModel
import com.example.viewmodel.ImageStudioViewModel
import com.example.viewmodel.MatchAnalyzerViewModel

private enum class ActiveToolOverlay {
    NONE,
    COACH_CHAT,
    IMAGE_STUDIO
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure Firebase App Check with launch intent before any UI or ViewModel creation
        configureAppCheck(this, intent)

        // STEP 2: Full dark edge-to-edge system bars
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )

        val languageStore = LanguagePreferenceStore(applicationContext)

        setContent {
            var selectedLanguage by remember {
                mutableStateOf(languageStore.loadInitialLanguage())
            }
            var showLanguagePicker by rememberSaveable { mutableStateOf(false) }

            MlbbCoachTheme(language = selectedLanguage) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    MlbbHeroCoachApp(
                        onOpenLanguagePicker = { showLanguagePicker = true }
                    )

                    if (showLanguagePicker) {
                        LanguagePickerModal(
                            currentLanguage = selectedLanguage,
                            onLanguageSelected = { newLanguage ->
                                selectedLanguage = newLanguage
                                languageStore.saveLanguage(newLanguage)
                            },
                            onDismiss = { showLanguagePicker = false }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MlbbHeroCoachApp(
    onOpenLanguagePicker: () -> Unit
) {
    var currentTab by rememberSaveable { mutableStateOf(CoachTab.HERO_GUIDE) }
    var activeOverlay by rememberSaveable { mutableStateOf(ActiveToolOverlay.NONE) }

    val heroGuideViewModel: HeroGuideViewModel = viewModel()
    val heroMetaViewModel: HeroMetaViewModel = viewModel()
    val draftCounterViewModel: DraftCounterViewModel = viewModel()
    val matchAnalyzerViewModel: MatchAnalyzerViewModel = viewModel()
    val coachChatViewModel: CoachChatViewModel = viewModel()
    val imageStudioViewModel: ImageStudioViewModel = viewModel()

    if (activeOverlay == ActiveToolOverlay.COACH_CHAT) {
        CoachChatOverlayScreen(
            viewModel = coachChatViewModel,
            onClose = { activeOverlay = ActiveToolOverlay.NONE },
            onOpenLanguagePicker = onOpenLanguagePicker,
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        )
        return
    }

    if (activeOverlay == ActiveToolOverlay.IMAGE_STUDIO) {
        ImageStudioOverlayScreen(
            viewModel = imageStudioViewModel,
            onClose = { activeOverlay = ActiveToolOverlay.NONE },
            onOpenLanguagePicker = onOpenLanguagePicker,
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        )
        return
    }

    BackHandler(enabled = currentTab != CoachTab.HERO_GUIDE) {
        currentTab = CoachTab.HERO_GUIDE
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.mlbbColors.backgroundBrush)
    ) {
        val useNavigationRail = maxWidth >= 600.dp

        if (useNavigationRail) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                MlbbNavigationRail(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CoachBrandTopBar(
                        onOpenChat = { activeOverlay = ActiveToolOverlay.COACH_CHAT },
                        onOpenImageStudio = { activeOverlay = ActiveToolOverlay.IMAGE_STUDIO },
                        onOpenLanguagePicker = onOpenLanguagePicker
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(max = 820.dp)
                            .fillMaxWidth()
                    ) {
                        CoachTabContent(
                            currentTab = currentTab,
                            heroGuideViewModel = heroGuideViewModel,
                            heroMetaViewModel = heroMetaViewModel,
                            draftCounterViewModel = draftCounterViewModel,
                            matchAnalyzerViewModel = matchAnalyzerViewModel
                        )
                    }
                }
            }
        } else {
            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets.safeDrawing,
                topBar = {
                    CoachBrandTopBar(
                        onOpenChat = { activeOverlay = ActiveToolOverlay.COACH_CHAT },
                        onOpenImageStudio = { activeOverlay = ActiveToolOverlay.IMAGE_STUDIO },
                        onOpenLanguagePicker = onOpenLanguagePicker
                    )
                },
                bottomBar = {
                    MlbbBottomNavigationBar(
                        currentTab = currentTab,
                        onTabSelected = { currentTab = it }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .imePadding()
                ) {
                    CoachTabContent(
                        currentTab = currentTab,
                        heroGuideViewModel = heroGuideViewModel,
                        heroMetaViewModel = heroMetaViewModel,
                        draftCounterViewModel = draftCounterViewModel,
                        matchAnalyzerViewModel = matchAnalyzerViewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun CoachBrandTopBar(
    onOpenChat: () -> Unit,
    onOpenImageStudio: () -> Unit,
    onOpenLanguagePicker: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val mlbbColors = MaterialTheme.mlbbColors
    val strings = LocalAppStrings.current
    val currentLanguage: AppLanguage = LocalAppLanguage.current

    Surface(
        color = colorScheme.surface.copy(alpha = 0.96f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                val showToolLabels = maxWidth >= 420.dp

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colorScheme.primaryContainer,
                            border = BorderStroke(1.dp, mlbbColors.borderGold),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.WorkspacePremium,
                                    contentDescription = null,
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.padding(end = 6.dp)) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleSmall,
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = strings.appBrandSubtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.secondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colorScheme.surfaceContainer,
                            border = BorderStroke(1.dp, mlbbColors.borderCyan),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(onClick = onOpenChat)
                                .testTag("top_bar_open_chat_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = if (showToolLabels) 10.dp else 10.dp,
                                    vertical = 8.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Forum,
                                    contentDescription = strings.toolCoachChat,
                                    tint = colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                if (showToolLabels) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = strings.toolCoachChat,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colorScheme.secondary,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colorScheme.primaryContainer,
                            border = BorderStroke(1.dp, mlbbColors.borderGold),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(onClick = onOpenImageStudio)
                                .testTag("top_bar_open_studio_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoAwesome,
                                    contentDescription = strings.toolImageStudio,
                                    tint = colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                if (showToolLabels) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = strings.toolImageStudio,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        // Globe Icon + Active Language Code Badge at Header Corner
                        LanguageSelectorButton(
                            currentLanguage = currentLanguage,
                            onClick = onOpenLanguagePicker
                        )
                    }
                }
            }
            HorizontalDivider(color = mlbbColors.borderSubtle, thickness = 1.dp)
        }
    }
}

@Composable
private fun CoachTabContent(
    currentTab: CoachTab,
    heroGuideViewModel: HeroGuideViewModel,
    heroMetaViewModel: HeroMetaViewModel,
    draftCounterViewModel: DraftCounterViewModel,
    matchAnalyzerViewModel: MatchAnalyzerViewModel
) {
    when (currentTab) {
        CoachTab.HERO_GUIDE -> HeroGuideScreen(
            viewModel = heroGuideViewModel,
            metaViewModel = heroMetaViewModel
        )
        CoachTab.DRAFT_COUNTER -> DraftCounterScreen(viewModel = draftCounterViewModel)
        CoachTab.MATCH_ANALYZER -> MatchAnalyzerScreen(viewModel = matchAnalyzerViewModel)
    }
}
