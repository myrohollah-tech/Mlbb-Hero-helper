package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.i18n.AppLanguage
import com.example.i18n.AppStrings
import com.example.i18n.LocalAppStrings
import com.example.ui.theme.DeepNavy900
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MlbbCoachTheme
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldContainer
import com.example.ui.theme.TextMuted

enum class CoachTab(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HERO_GUIDE(
        selectedIcon = Icons.Rounded.AutoStories,
        unselectedIcon = Icons.Outlined.AutoStories,
        testTag = "nav_tab_hero_guide"
    ),
    DRAFT_COUNTER(
        selectedIcon = Icons.Rounded.Shield,
        unselectedIcon = Icons.Outlined.Shield,
        testTag = "nav_tab_draft_counter"
    ),
    MATCH_ANALYZER(
        selectedIcon = Icons.Rounded.Analytics,
        unselectedIcon = Icons.Outlined.Analytics,
        testTag = "nav_tab_match_analyzer"
    );

    fun localizedLabel(strings: AppStrings): String = when (this) {
        HERO_GUIDE -> strings.navHeroGuide
        DRAFT_COUNTER -> strings.navDraftCounter
        MATCH_ANALYZER -> strings.navMatchAnalyzer
    }
}

/**
 * Material 3 Dark-Mode Bottom NavigationBar localized dynamically across all 12 languages
 * with automatic RTL/LTR adaptation.
 */
@Composable
fun MlbbBottomNavigationBar(
    currentTab: CoachTab,
    onTabSelected: (CoachTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            ElectricCyan.copy(alpha = 0.25f),
                            RoyalGold.copy(alpha = 0.6f),
                            ElectricCyan.copy(alpha = 0.25f)
                        )
                    )
                )
        )
        NavigationBar(
            containerColor = DeepNavy900,
            contentColor = RoyalGold,
            tonalElevation = 0.dp,
            windowInsets = WindowInsets.navigationBars,
            modifier = Modifier.testTag("mlbb_bottom_navigation_bar")
        ) {
            CoachTab.entries.forEach { tab ->
                val selected = currentTab == tab
                val label = tab.localizedLabel(strings)
                NavigationBarItem(
                    selected = selected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = label,
                                modifier = Modifier.size(23.dp)
                            )
                            if (selected) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(ElectricCyan)
                                )
                            }
                        }
                    },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = RoyalGold,
                        selectedTextColor = RoyalGold,
                        indicatorColor = RoyalGoldContainer,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag(tab.testTag)
                )
            }
        }
    }
}

@Composable
fun MlbbNavigationRail(
    currentTab: CoachTab,
    onTabSelected: (CoachTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    NavigationRail(
        containerColor = DeepNavy900,
        contentColor = RoyalGold,
        modifier = modifier
            .fillMaxHeight()
            .testTag("mlbb_navigation_rail")
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        CoachTab.entries.forEach { tab ->
            val selected = currentTab == tab
            val label = tab.localizedLabel(strings)
            NavigationRailItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = RoyalGold,
                    selectedTextColor = RoyalGold,
                    indicatorColor = RoyalGoldContainer,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag(tab.testTag)
            )
        }
        HorizontalDivider(color = Color.Transparent)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060B16)
@Composable
private fun MlbbBottomNavigationBarPreview() {
    MlbbCoachTheme(language = AppLanguage.ENGLISH) {
        MlbbBottomNavigationBar(
            currentTab = CoachTab.HERO_GUIDE,
            onTabSelected = {}
        )
    }
}
