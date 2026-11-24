package com.example.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.i18n.AppLanguage
import com.example.i18n.localizedName
import com.example.i18n.shareStrings
import com.example.i18n.stringsFor
import com.example.i18n.trendChartStrings
import com.example.model.HeroMetaGuide
import com.example.model.MlbbHero
import java.util.Locale

object HeroShareHelper {

    fun formatHeroShareText(
        hero: MlbbHero,
        guide: HeroMetaGuide?,
        language: AppLanguage
    ): String {
        val strings = stringsFor(language)
        val shareStrings = language.shareStrings()
        val trendStrings = language.trendChartStrings()
        val isPersian = language == AppLanguage.PERSIAN

        val heroTitle = if (isPersian && hero.nameFa != hero.nameEn) {
            "${hero.nameFa} (${hero.nameEn})"
        } else {
            hero.nameEn
        }
        val roles = hero.localizedRolesText { it.localizedName(strings) }
        val lanes = hero.lanes.joinToString(" / ") { it.localizedName(strings) }

        return buildString {
            appendLine("⚔️ $heroTitle — MLBB")
            appendLine("${shareStrings.shareRoleLabel}: $roles")
            appendLine("${shareStrings.shareLaneLabel}: $lanes")

            if (guide == null) {
                if (isPersian && hero.specialtyFa.isNotBlank()) {
                    appendLine("${shareStrings.shareSpecialtyLabel}: ${hero.specialtyFa}")
                }
                appendLine()
                appendLine(shareStrings.shareOpenAppHint)
            } else if (!guide.rawFallbackFa.isNullOrBlank()) {
                appendLine()
                appendLine(guide.rawFallbackFa.trim())
            } else {
                appendLine()
                if (guide.tier.isNotBlank()) {
                    appendLine("🏆 ${strings.heroSectionTier}: ${guide.tier}")
                    if (guide.tierSummaryFa.isNotBlank()) {
                        appendLine(guide.tierSummaryFa.trim())
                    }
                }

                guide.trend30d.lastOrNull()?.let { latest ->
                    appendLine(
                        "📊 ${trendStrings.metricWinRate}: ${String.format(Locale.US, "%.1f%%", latest.winRate)} • " +
                            "${trendStrings.metricPickRate}: ${String.format(Locale.US, "%.1f%%", latest.pickRate)} • " +
                            "${trendStrings.metricBanRate}: ${String.format(Locale.US, "%.1f%%", latest.banRate)}"
                    )
                }

                if (guide.coreItems.isNotEmpty()) {
                    appendLine()
                    appendLine("🛠 ${strings.heroSectionCoreBuild}:")
                    guide.coreItems.forEachIndexed { idx, item ->
                        val reason = item.reasonFa.trim()
                        if (reason.isNotBlank()) {
                            appendLine("${idx + 1}. ${item.name} — $reason")
                        } else {
                            appendLine("${idx + 1}. ${item.name}")
                        }
                    }
                }

                if (guide.situationalItems.isNotEmpty()) {
                    appendLine()
                    appendLine("🛡 ${strings.heroSectionSituationalBuild}:")
                    guide.situationalItems.forEach { item ->
                        val reason = item.reasonFa.trim()
                        if (reason.isNotBlank()) {
                            appendLine("• ${item.name} — $reason")
                        } else {
                            appendLine("• ${item.name}")
                        }
                    }
                }

                if (guide.emblemMain.isNotBlank() || guide.emblemTalents.isNotEmpty()) {
                    appendLine()
                    appendLine("✨ ${strings.heroSectionEmblem}: ${guide.emblemMain}")
                    if (guide.emblemTalents.isNotEmpty()) {
                        appendLine(
                            guide.emblemTalents.joinToString(" • ") {
                                it.replace("|", " - ").trim()
                            }
                        )
                    }
                }

                if (guide.battleSpell.isNotBlank()) {
                    appendLine()
                    appendLine("⚡ ${strings.heroSectionSpell}: ${guide.battleSpell}")
                    if (guide.battleSpellReasonFa.isNotBlank()) {
                        appendLine(guide.battleSpellReasonFa.trim())
                    }
                }

                if (guide.skillComboSteps.isNotEmpty()) {
                    appendLine()
                    appendLine("🎮 ${strings.heroSectionCombo}:")
                    appendLine(guide.skillComboSteps.joinToString(" → "))
                    if (guide.comboExplanationFa.isNotBlank()) {
                        appendLine(guide.comboExplanationFa.trim())
                    }
                }

                val hasPhaseTips = guide.earlyGameTipFa.isNotBlank() ||
                    guide.midGameTipFa.isNotBlank() ||
                    guide.lateGameTipFa.isNotBlank()
                if (hasPhaseTips) {
                    appendLine()
                    appendLine("📈 ${strings.heroSectionGamePhases}:")
                    if (guide.earlyGameTipFa.isNotBlank()) {
                        appendLine("• ${strings.phaseEarly}: ${guide.earlyGameTipFa.trim()}")
                    }
                    if (guide.midGameTipFa.isNotBlank()) {
                        appendLine("• ${strings.phaseMid}: ${guide.midGameTipFa.trim()}")
                    }
                    if (guide.lateGameTipFa.isNotBlank()) {
                        appendLine("• ${strings.phaseLate}: ${guide.lateGameTipFa.trim()}")
                    }
                }

                if (guide.counters.isNotEmpty()) {
                    appendLine()
                    appendLine("⚠️ ${strings.heroSectionCounters}:")
                    guide.counters.take(3).forEach { entry ->
                        appendLine("• ${entry.replace("|", " — ").trim()}")
                    }
                }

                if (guide.synergies.isNotEmpty()) {
                    appendLine()
                    appendLine("🤝 ${strings.heroSectionSynergies}:")
                    guide.synergies.take(3).forEach { entry ->
                        appendLine("• ${entry.replace("|", " — ").trim()}")
                    }
                }
            }

            appendLine()
            append("— ${shareStrings.shareFooter}")
        }
    }

    fun launchShareSheet(
        context: Context,
        hero: MlbbHero,
        guide: HeroMetaGuide?,
        language: AppLanguage
    ) {
        val shareStrings = language.shareStrings()
        val isPersian = language == AppLanguage.PERSIAN
        val heroName = hero.primaryDisplayName(isPersian)
        val subject = language.format(shareStrings.shareSubjectTemplate, heroName)
        val body = formatHeroShareText(hero = hero, guide = guide, language = language)

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        val chooser = Intent.createChooser(sendIntent, shareStrings.shareChooserTitle).apply {
            if (context !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        runCatching {
            context.startActivity(chooser)
        }
    }
}
