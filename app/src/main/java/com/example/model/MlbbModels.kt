package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.RoleAssassinColor
import com.example.ui.theme.RoleFighterColor
import com.example.ui.theme.RoleMageColor
import com.example.ui.theme.RoleMarksmanColor
import com.example.ui.theme.RoleSupportColor
import com.example.ui.theme.RoleTankColor

enum class MlbbRole(
    val nameEn: String,
    val nameFa: String,
    val accentColor: Color
) {
    TANK("Tank", "تانک", RoleTankColor),
    FIGHTER("Fighter", "فایتر", RoleFighterColor),
    ASSASSIN("Assassin", "اساسین", RoleAssassinColor),
    MAGE("Mage", "میج", RoleMageColor),
    MARKSMAN("Marksman", "مارکسمن", RoleMarksmanColor),
    SUPPORT("Support", "ساپورت", RoleSupportColor)
}

enum class MlbbLane(
    val nameEn: String,
    val nameFa: String
) {
    EXP("EXP", "اکسپی (EXP)"),
    GOLD("Gold", "گلد (Gold)"),
    MID("Mid", "مید (Mid)"),
    JUNGLE("Jungle", "جانگل (Jungle)"),
    ROAM("Roam", "روم (Roam)")
}

data class MlbbHero(
    val id: String,
    val nameEn: String,
    val nameFa: String,
    val primaryRole: MlbbRole,
    val secondaryRole: MlbbRole? = null,
    val lanes: List<MlbbLane>,
    val specialtyFa: String
) {
    val rolesTextFa: String
        get() = if (secondaryRole != null) {
            "${primaryRole.nameFa} / ${secondaryRole.nameFa}"
        } else {
            primaryRole.nameFa
        }

    fun primaryDisplayName(isPersian: Boolean): String = if (isPersian) nameFa else nameEn

    fun subtitleDisplayName(isPersian: Boolean): String = if (isPersian) nameEn else ""

    fun localizedRolesText(roleLabel: (MlbbRole) -> String): String =
        if (secondaryRole != null) {
            "${roleLabel(primaryRole)} / ${roleLabel(secondaryRole)}"
        } else {
            roleLabel(primaryRole)
        }
}

sealed interface AsyncUiState<out T> {
    data object Idle : AsyncUiState<Nothing>
    data object Loading : AsyncUiState<Nothing>
    data class Success<T>(val data: T) : AsyncUiState<T>
    data class Error(val message: String) : AsyncUiState<Nothing>
}

data class HeroBuildItem(
    val name: String,
    val reasonFa: String
)

data class GroundingSource(
    val title: String,
    val uri: String
)

data class HeroTrendPoint(
    val dayOffsetFromEnd: Int, // e.g., 30, 25, 20, 15, 10, 5, 0 (0 = Today)
    val winRate: Float,
    val pickRate: Float,
    val banRate: Float
)

data class HeroMetaGuide(
    val heroNameEn: String,
    val heroNameFa: String,
    val tier: String,
    val tierSummaryFa: String,
    val coreItems: List<HeroBuildItem>,
    val situationalItems: List<HeroBuildItem>,
    val emblemMain: String,
    val emblemTalents: List<String>,
    val battleSpell: String,
    val battleSpellReasonFa: String,
    val skillComboSteps: List<String>,
    val comboExplanationFa: String,
    val earlyGameTipFa: String,
    val midGameTipFa: String,
    val lateGameTipFa: String,
    val counters: List<String>,
    val synergies: List<String>,
    val groundingSources: List<GroundingSource>,
    val trend30d: List<HeroTrendPoint> = emptyList(),
    val rawFallbackFa: String? = null
)

data class HeroQuickCheckSummary(
    val tier: String,
    val coreItemName: String,
    val coreItemNote: String,
    val mainCombo: String,
    val mainComboNote: String,
    val primaryCounter: String,
    val primaryCounterNote: String
)

data class CounterPickHero(
    val heroNameEn: String,
    val heroNameFa: String,
    val laneFa: String,
    val matchAdvantageFa: String,
    val reasonFa: String,
    val keyTipFa: String
)

data class CounterItemTip(
    val itemName: String,
    val categoryFa: String,
    val targetEnemiesFa: String,
    val reasonFa: String
)

data class DraftCounterResult(
    val enemyCompAnalysisFa: String,
    val topPicks: List<CounterPickHero>,
    val counterItems: List<CounterItemTip>
)

data class MatchAnalysisReport(
    val matchResultFa: String,
    val heroPlayed: String,
    val kda: String,
    val gold: String,
    val medalFa: String,
    val detectedItems: List<String>,
    val enemyHeroes: List<String>,
    val strengths: List<String>,
    val mistakes: List<String>,
    val nextMatchGoal: String
)

data class CoachChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val isStreaming: Boolean = false
)

data class MatchHistoryEntry(
    val id: String,
    val analyzedAtMillis: Long,
    val screenshotFilePath: String,
    val languageCode: String,
    val report: MatchAnalysisReport
)
