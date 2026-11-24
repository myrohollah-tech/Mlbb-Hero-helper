package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.ViewList
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FilterAltOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.Forest
import androidx.compose.material.icons.rounded.GpsFixed
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.SportsMma
import androidx.compose.material.icons.rounded.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.MlbbHeroCatalog
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppLanguage
import com.example.i18n.LocalAppStrings
import com.example.i18n.favoriteStrings
import com.example.i18n.localizedName
import com.example.model.MlbbHero
import com.example.model.MlbbLane
import com.example.model.MlbbRole
import com.example.ui.components.FavoriteHeartButton
import com.example.ui.components.FavoritesHeroSection
import com.example.ui.components.HeroEmblemBadge
import com.example.ui.components.HeroQuickCheckDialog
import com.example.ui.components.HeroShareButton
import com.example.ui.theme.MlbbCoachTheme
import com.example.ui.theme.mlbbColors
import com.example.util.HeroShareHelper
import com.example.viewmodel.HeroGuideViewModel
import com.example.viewmodel.HeroMetaViewModel

private fun MlbbRole.iconVector(): ImageVector = when (this) {
    MlbbRole.TANK -> Icons.Rounded.Shield
    MlbbRole.FIGHTER -> Icons.Rounded.SportsMma
    MlbbRole.ASSASSIN -> Icons.Rounded.FlashOn
    MlbbRole.MAGE -> Icons.Rounded.AutoFixHigh
    MlbbRole.MARKSMAN -> Icons.Rounded.GpsFixed
    MlbbRole.SUPPORT -> Icons.Rounded.Favorite
}

private fun MlbbLane.iconVector(): ImageVector = when (this) {
    MlbbLane.EXP -> Icons.Rounded.Terrain
    MlbbLane.GOLD -> Icons.Rounded.MonetizationOn
    MlbbLane.MID -> Icons.Rounded.AutoAwesome
    MlbbLane.JUNGLE -> Icons.Rounded.Forest
    MlbbLane.ROAM -> Icons.Rounded.Explore
}

@Composable
fun HeroGuideScreen(
    viewModel: HeroGuideViewModel,
    metaViewModel: HeroMetaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language = LocalAppLanguage.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeHero by metaViewModel.activeHero.collectAsStateWithLifecycle()
    val metaState by metaViewModel.uiState.collectAsStateWithLifecycle()
    val sharingHeroId by metaViewModel.sharingHeroId.collectAsStateWithLifecycle()
    val quickCheckHero by metaViewModel.quickCheckHero.collectAsStateWithLifecycle()
    val quickCheckState by metaViewModel.quickCheckState.collectAsStateWithLifecycle()

    LaunchedEffect(language, activeHero) {
        if (activeHero != null) {
            metaViewModel.onLanguageChanged(language)
        }
    }

    if (activeHero != null) {
        val currentHero = activeHero!!
        val isCatalogHero = MlbbHeroCatalog.heroes.any { it.id == currentHero.id }
        BackHandler {
            metaViewModel.clearActiveHero()
        }
        HeroMetaDetailView(
            hero = currentHero,
            metaState = metaState,
            onBack = metaViewModel::clearActiveHero,
            onRefresh = { metaViewModel.refreshActiveHero(language) },
            isFavorite = currentHero.id in uiState.favoriteHeroIds,
            onToggleFavorite = if (isCatalogHero) {
                { viewModel.toggleFavorite(currentHero.id) }
            } else {
                null
            },
            modifier = modifier
        )
        return
    }

    val shareHeroAction: (MlbbHero) -> Unit = { hero ->
        metaViewModel.prepareHeroGuideForShare(hero, language) { guide ->
            HeroShareHelper.launchShareSheet(
                context = context,
                hero = hero,
                guide = guide,
                language = language
            )
        }
    }

    quickCheckHero?.let { dialogHero ->
        HeroQuickCheckDialog(
            hero = dialogHero,
            quickCheckState = quickCheckState,
            isFavorite = dialogHero.id in uiState.favoriteHeroIds,
            isSharing = sharingHeroId == dialogHero.id,
            onToggleFavorite = { viewModel.toggleFavorite(dialogHero.id) },
            onShare = { shareHeroAction(dialogHero) },
            onOpenFullGuide = { metaViewModel.openFullGuideFromQuickCheck(dialogHero, language) },
            onRetry = { metaViewModel.openQuickCheck(dialogHero, language, forceRefresh = true) },
            onDismiss = metaViewModel::dismissQuickCheck
        )
    }

    HeroSearchAndRosterContent(
        searchQuery = uiState.searchQuery,
        selectedRole = uiState.selectedRole,
        selectedLane = uiState.selectedLane,
        isGridView = uiState.isGridView,
        hasActiveFilters = uiState.hasActiveFilters,
        heroes = uiState.heroes,
        favoriteHeroIds = uiState.favoriteHeroIds,
        favoriteHeroes = uiState.favoriteHeroes,
        sharingHeroId = sharingHeroId,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onRoleSelected = viewModel::onRoleSelected,
        onLaneSelected = viewModel::onLaneSelected,
        onClearFilters = viewModel::clearFilters,
        onSetGridView = viewModel::setGridView,
        onHeroClick = { hero -> metaViewModel.openQuickCheck(hero, language) },
        onToggleFavorite = viewModel::toggleFavorite,
        onShareHero = shareHeroAction,
        onCustomHeroSearch = { query -> metaViewModel.loadCustomHeroMeta(query, language) },
        modifier = modifier
    )
}

@Composable
fun HeroSearchAndRosterContent(
    searchQuery: String,
    selectedRole: MlbbRole?,
    selectedLane: MlbbLane?,
    isGridView: Boolean,
    hasActiveFilters: Boolean,
    heroes: List<MlbbHero>,
    favoriteHeroIds: Set<String>,
    favoriteHeroes: List<MlbbHero>,
    sharingHeroId: String?,
    onSearchQueryChange: (String) -> Unit,
    onRoleSelected: (MlbbRole?) -> Unit,
    onLaneSelected: (MlbbLane?) -> Unit,
    onClearFilters: () -> Unit,
    onSetGridView: (Boolean) -> Unit,
    onHeroClick: (MlbbHero) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onShareHero: (MlbbHero) -> Unit,
    onCustomHeroSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val language = LocalAppLanguage.current
    val favStrings = language.favoriteStrings()
    val colorScheme = MaterialTheme.colorScheme
    val mlbbColors = MaterialTheme.mlbbColors
    val heroPairs = heroes.chunked(2)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Atmospheric Hero Banner
        item(key = "hero_banner") {
            HeroGuideBanner()
        }

        // 2. Search Input + Filter Panel Card
        item(key = "search_and_filter_panel") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainer),
                border = BorderStroke(1.dp, mlbbColors.borderSubtle)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Search Field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = {
                            Text(
                                text = strings.heroSearchPlaceholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = mlbbColors.textMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null,
                                tint = colorScheme.secondary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(
                                    onClick = { onSearchQueryChange("") },
                                    modifier = Modifier.testTag("hero_search_clear")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = strings.cdCloseDialog,
                                        tint = colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.secondary,
                            unfocusedBorderColor = mlbbColors.borderSubtle,
                            focusedContainerColor = colorScheme.surface,
                            unfocusedContainerColor = colorScheme.surface,
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hero_search_input")
                    )

                    // Role Filter Row
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = strings.filterRoleLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = selectedRole == null,
                                    onClick = { onRoleSelected(null) },
                                    label = { Text(strings.filterAll) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = colorScheme.surface,
                                        labelColor = colorScheme.onSurfaceVariant,
                                        selectedContainerColor = colorScheme.primary.copy(alpha = 0.2f),
                                        selectedLabelColor = colorScheme.primary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = selectedRole == null,
                                        borderColor = mlbbColors.borderSubtle,
                                        selectedBorderColor = colorScheme.primary
                                    ),
                                    modifier = Modifier.testTag("role_filter_all")
                                )
                            }
                            items(MlbbRole.entries) { role ->
                                val selected = selectedRole == role
                                FilterChip(
                                    selected = selected,
                                    onClick = { onRoleSelected(role) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = role.iconVector(),
                                            contentDescription = null,
                                            tint = if (selected) role.accentColor else colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    label = { Text(role.localizedName(strings)) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = colorScheme.surface,
                                        labelColor = colorScheme.onSurfaceVariant,
                                        selectedContainerColor = role.accentColor.copy(alpha = 0.22f),
                                        selectedLabelColor = role.accentColor
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = selected,
                                        borderColor = mlbbColors.borderSubtle,
                                        selectedBorderColor = role.accentColor
                                    ),
                                    modifier = Modifier.testTag("role_filter_${role.nameEn.lowercase()}")
                                )
                            }
                        }
                    }

                    // Lane Filter Row
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = strings.filterLaneLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = selectedLane == null,
                                    onClick = { onLaneSelected(null) },
                                    label = { Text(strings.filterAll) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = colorScheme.surface,
                                        labelColor = colorScheme.onSurfaceVariant,
                                        selectedContainerColor = colorScheme.secondary.copy(alpha = 0.2f),
                                        selectedLabelColor = colorScheme.secondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = selectedLane == null,
                                        borderColor = mlbbColors.borderSubtle,
                                        selectedBorderColor = colorScheme.secondary
                                    ),
                                    modifier = Modifier.testTag("lane_filter_all")
                                )
                            }
                            items(MlbbLane.entries) { lane ->
                                val selected = selectedLane == lane
                                FilterChip(
                                    selected = selected,
                                    onClick = { onLaneSelected(lane) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = lane.iconVector(),
                                            contentDescription = null,
                                            tint = if (selected) colorScheme.secondary else colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    },
                                    label = { Text(lane.localizedName(strings)) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = colorScheme.surface,
                                        labelColor = colorScheme.onSurfaceVariant,
                                        selectedContainerColor = colorScheme.secondary.copy(alpha = 0.2f),
                                        selectedLabelColor = colorScheme.secondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = selected,
                                        borderColor = mlbbColors.borderSubtle,
                                        selectedBorderColor = colorScheme.secondary
                                    ),
                                    modifier = Modifier.testTag("lane_filter_${lane.nameEn.lowercase()}")
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2.5 Dedicated Favorites Section
        item(key = "favorites_hero_section") {
            FavoritesHeroSection(
                favoriteHeroes = favoriteHeroes,
                onHeroClick = onHeroClick,
                onToggleFavorite = onToggleFavorite,
                sharingHeroId = sharingHeroId,
                onShareHero = onShareHero
            )
        }

        // 3. Result Count, Active Filter Reset & Grid/List Switcher
        item(key = "roster_toolbar") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column {
                        Text(
                            text = favStrings.allHeroesTitle,
                            style = MaterialTheme.typography.titleSmall,
                            color = colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = language.format(strings.heroCountLabel, heroes.size),
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                    if (hasActiveFilters) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = colorScheme.secondaryContainer,
                            border = BorderStroke(1.dp, mlbbColors.borderCyan),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(onClick = onClearFilters)
                                .testTag("hero_clear_filters_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.FilterAltOff,
                                    contentDescription = null,
                                    tint = colorScheme.secondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strings.filterResetAll,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Grid / List View Switcher
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colorScheme.surfaceContainer,
                    border = BorderStroke(1.dp, mlbbColors.borderSubtle)
                ) {
                    Row(modifier = Modifier.padding(2.dp)) {
                        IconButton(
                            onClick = { onSetGridView(true) },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isGridView) colorScheme.primaryContainer else Color.Transparent
                                )
                                .testTag("hero_view_grid_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.GridView,
                                contentDescription = strings.heroViewGridCd,
                                tint = if (isGridView) colorScheme.primary else mlbbColors.textMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = { onSetGridView(false) },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (!isGridView) colorScheme.primaryContainer else Color.Transparent
                                )
                                .testTag("hero_view_list_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ViewList,
                                contentDescription = strings.heroViewListCd,
                                tint = if (!isGridView) colorScheme.primary else mlbbColors.textMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Optional Direct AI Meta Search Chip when user types a hero name
        if (searchQuery.trim().length >= 2) {
            item(key = "custom_search_banner") {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = colorScheme.primaryContainer,
                    border = BorderStroke(1.dp, mlbbColors.borderGold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onCustomHeroSearch(searchQuery) }
                        .testTag("custom_hero_query_chip")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = language.format(strings.heroCustomQueryCta, searchQuery.trim()),
                                style = MaterialTheme.typography.labelLarge,
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = colorScheme.primary
                        )
                    }
                }
            }
        }

        // 4. Hero Cards (Grid or Detailed List) or Empty State
        if (heroes.isEmpty()) {
            item(key = "empty_heroes") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainer),
                    border = BorderStroke(1.dp, mlbbColors.borderSubtle)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.SearchOff,
                            contentDescription = null,
                            tint = colorScheme.secondary,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = strings.heroEmptyTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = strings.heroEmptyHint,
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (hasActiveFilters) {
                                Button(
                                    onClick = onClearFilters,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = colorScheme.secondaryContainer,
                                        contentColor = colorScheme.secondary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.heightIn(min = 48.dp)
                                ) {
                                    Text(strings.filterResetAll)
                                }
                            }
                            if (searchQuery.isNotBlank()) {
                                Button(
                                    onClick = { onCustomHeroSearch(searchQuery) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = colorScheme.primary,
                                        contentColor = colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .testTag("empty_custom_hero_button")
                                ) {
                                    Text(
                                        text = language.format(strings.heroCustomQueryCta, searchQuery.trim()),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else if (isGridView) {
            items(heroPairs, key = { pair -> "grid_${pair.joinToString("_") { it.id }}" }) { rowHeroes ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowHeroes.forEach { hero ->
                        HeroGridCard(
                            hero = hero,
                            isFavorite = hero.id in favoriteHeroIds,
                            isSharing = sharingHeroId == hero.id,
                            onToggleFavorite = { onToggleFavorite(hero.id) },
                            onShare = { onShareHero(hero) },
                            onClick = { onHeroClick(hero) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowHeroes.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        } else {
            items(heroes, key = { "list_${it.id}" }) { hero ->
                HeroListCard(
                    hero = hero,
                    isFavorite = hero.id in favoriteHeroIds,
                    isSharing = sharingHeroId == hero.id,
                    onToggleFavorite = { onToggleFavorite(hero.id) },
                    onShare = { onShareHero(hero) },
                    onClick = { onHeroClick(hero) }
                )
            }
        }

        item(key = "bottom_spacer") {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HeroGuideBanner() {
    val strings = LocalAppStrings.current
    val colorScheme = MaterialTheme.colorScheme
    val mlbbColors = MaterialTheme.mlbbColors
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    // Direction-aware gradient so text starting edge always has rich dark contrast in BOTH RTL & LTR
    val scrimColors = if (isRtl) {
        listOf(
            colorScheme.background.copy(alpha = 0.35f),
            colorScheme.background.copy(alpha = 0.86f),
            colorScheme.background.copy(alpha = 0.96f)
        )
    } else {
        listOf(
            colorScheme.background.copy(alpha = 0.96f),
            colorScheme.background.copy(alpha = 0.86f),
            colorScheme.background.copy(alpha = 0.35f)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(152.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, mlbbColors.borderGold, RoundedCornerShape(22.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_coach_hero_banner_1795130938826),
            contentDescription = strings.cdBannerArt,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(colors = scrimColors))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = colorScheme.surface.copy(alpha = 0.88f),
                border = BorderStroke(1.dp, mlbbColors.borderCyan)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Public,
                        contentDescription = null,
                        tint = colorScheme.secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strings.heroLiveBadge,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.secondary
                    )
                }
            }

            Column {
                Text(
                    text = strings.heroBannerTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = strings.heroBannerSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun HeroGridCard(
    hero: MlbbHero,
    isFavorite: Boolean,
    isSharing: Boolean,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val isPersian = LocalAppLanguage.current == AppLanguage.PERSIAN
    val colorScheme = MaterialTheme.colorScheme
    val mlbbColors = MaterialTheme.mlbbColors
    val accent = hero.primaryRole.accentColor

    val primaryName = hero.primaryDisplayName(isPersian)
    val subtitleName = hero.subtitleDisplayName(isPersian)
    val metaLine = if (isPersian) {
        hero.specialtyFa
    } else {
        hero.localizedRolesText { it.localizedName(strings) }
    }

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("hero_card_${hero.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainer),
        border = BorderStroke(
            width = 1.dp,
            color = if (isFavorite) mlbbColors.borderGold else mlbbColors.borderSubtle
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(accent, accent.copy(alpha = 0.25f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.12f),
                                colorScheme.surfaceContainer
                            )
                        )
                    )
                    .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HeroEmblemBadge(
                        nameEn = hero.nameEn,
                        accentColor = accent,
                        size = 42.dp
                    )
                    FavoriteHeartButton(
                        isFavorite = isFavorite,
                        onToggle = onToggleFavorite,
                        testTag = "favorite_toggle_${hero.id}"
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accent.copy(alpha = 0.16f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = hero.primaryRole.iconVector(),
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = hero.primaryRole.localizedName(strings),
                            style = MaterialTheme.typography.labelSmall,
                            color = accent,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = primaryName,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 6.dp)
                )
                if (subtitleName != null) {
                    Text(
                        text = subtitleName,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = metaLine,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 6.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        hero.lanes.take(2).forEach { lane ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = colorScheme.surfaceContainerHigh
                            ) {
                                Text(
                                    text = lane.localizedName(strings),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.secondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    HeroShareButton(
                        onClick = onShare,
                        isLoading = isSharing,
                        testTag = "hero_share_grid_${hero.id}"
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroListCard(
    hero: MlbbHero,
    isFavorite: Boolean,
    isSharing: Boolean,
    onToggleFavorite: () -> Unit,
    onShare: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val isPersian = LocalAppLanguage.current == AppLanguage.PERSIAN
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val colorScheme = MaterialTheme.colorScheme
    val mlbbColors = MaterialTheme.mlbbColors
    val accent = hero.primaryRole.accentColor

    val primaryName = hero.primaryDisplayName(isPersian)
    val subtitleName = hero.subtitleDisplayName(isPersian)
    val rolesText = hero.localizedRolesText { it.localizedName(strings) }
    val descriptionLine = if (isPersian) "$rolesText • ${hero.specialtyFa}" else rolesText

    val gradientColors = if (isRtl) {
        listOf(colorScheme.surfaceContainer, accent.copy(alpha = 0.12f))
    } else {
        listOf(accent.copy(alpha = 0.12f), colorScheme.surfaceContainer)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("hero_list_card_${hero.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainer),
        border = BorderStroke(
            width = 1.dp,
            color = if (isFavorite) mlbbColors.borderGold else mlbbColors.borderSubtle
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(colors = gradientColors))
                .padding(start = 12.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeroEmblemBadge(
                nameEn = hero.nameEn,
                accentColor = accent,
                size = 48.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = primaryName,
                        style = MaterialTheme.typography.titleMedium,
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subtitleName != null) {
                        Text(
                            text = "($subtitleName)",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Text(
                    text = descriptionLine,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    hero.lanes.forEach { lane ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = colorScheme.surfaceContainerHigh
                        ) {
                            Text(
                                text = lane.localizedName(strings),
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.secondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            HeroShareButton(
                onClick = onShare,
                isLoading = isSharing,
                testTag = "hero_share_list_${hero.id}"
            )
            FavoriteHeartButton(
                isFavorite = isFavorite,
                onToggle = onToggleFavorite,
                testTag = "favorite_toggle_list_${hero.id}"
            )
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = colorScheme.primaryContainer,
                border = BorderStroke(1.dp, mlbbColors.borderGold)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.heroCardOpenGuide,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF060B16)
@Composable
private fun HeroSearchAndRosterPreview() {
    MlbbCoachTheme(language = AppLanguage.ENGLISH) {
        val sampleHeroes = MlbbHeroCatalog.heroes.take(6)
        HeroSearchAndRosterContent(
            searchQuery = "",
            selectedRole = MlbbRole.ASSASSIN,
            selectedLane = MlbbLane.JUNGLE,
            isGridView = true,
            hasActiveFilters = true,
            heroes = sampleHeroes,
            favoriteHeroIds = setOf(sampleHeroes.first().id),
            favoriteHeroes = listOf(sampleHeroes.first()),
            sharingHeroId = null,
            onSearchQueryChange = {},
            onRoleSelected = {},
            onLaneSelected = {},
            onClearFilters = {},
            onSetGridView = {},
            onHeroClick = {},
            onToggleFavorite = {},
            onShareHero = {},
            onCustomHeroSearch = {}
        )
    }
}
