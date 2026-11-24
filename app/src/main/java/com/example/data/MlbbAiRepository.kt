package com.example.data

import android.graphics.Bitmap
import android.util.Log
import com.example.i18n.AppLanguage
import com.example.i18n.stringsFor
import com.example.model.CounterItemTip
import com.example.model.CounterPickHero
import com.example.model.DraftCounterResult
import com.example.model.GroundingSource
import com.example.model.HeroBuildItem
import com.example.model.HeroMetaGuide
import com.example.model.HeroQuickCheckSummary
import com.example.model.HeroTrendPoint
import com.example.model.MatchAnalysisReport
import com.example.model.MlbbRole
import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject

private const val TAG = "MlbbAiRepository"
private const val MODEL_NAME = "gemini-3.8-flash"
private const val FAST_MODEL_NAME = "gemini-3.5-flash-lite"
private const val IMAGE_MODEL_NAME = "gemini-nano-banana-2.1"
private const val TIMEOUT_MS = 60_000L

enum class CoachPersona(val modelName: String) {
    GENERAL_COACH(MODEL_NAME),
    MACRO_STRATEGIST(MODEL_NAME),
    FAST_META_TIPS(FAST_MODEL_NAME);

    fun greeting(language: AppLanguage): String {
        val strings = stringsFor(language)
        return when (this) {
            GENERAL_COACH -> strings.chatbotGreetingCoach
            MACRO_STRATEGIST -> strings.chatbotGreetingMacro
            FAST_META_TIPS -> strings.chatbotGreetingFast
        }
    }

    fun systemPrompt(language: AppLanguage): String {
        val lang = language.aiLanguageName
        val preserveRule =
            "Always keep official Mobile Legends: Bang Bang hero names, item names, emblem names, and battle spell names in their official English form (e.g., Fanny, Layla, Dominance Ice, Athena's Shield, Flicker), or show them alongside a brief translation. If a later user turn specifies a different active app language, immediately switch to that language."
        return when (this) {
            GENERAL_COACH ->
                "You are an elite Mobile Legends: Bang Bang (MLBB) all-round coach. Answer EVERY question fluently in $lang (unless a newer turn specifies a switched app language). Cover hero mastery, builds, emblems, counters, and solo queue climbing. $preserveRule"
            MACRO_STRATEGIST ->
                "You are a Mythical Immortal Mobile Legends: Bang Bang (MLBB) macro strategist and 5v5 draft analyst. Answer EVERY question fluently in $lang (unless a newer turn specifies a switched app language). Focus on map control, Turtle/Lord timing, lane freezing, cutting lanes, and roamer rotations. $preserveRule"
            FAST_META_TIPS ->
                "You are an ultra-fast MLBB meta assistant. Answer EVERY question in concise bullet points fluently in $lang (unless a newer turn specifies a switched app language) so players can read quickly during loading screens. $preserveRule"
        }
    }
}

class MlbbAiRepository {

    private val imageStudioModel by lazy {
        Firebase.ai.generativeModel(
            modelName = IMAGE_MODEL_NAME,
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE)
            }
        )
    }

    private fun buildDraftSchema(): Schema = Schema.obj(
        properties = mapOf(
            "enemyCompAnalysis" to Schema.string(
                description = "2-3 sentence tactical breakdown of the enemy composition, damage profile, sustain/CC, and win condition in the requested language"
            ),
            "topPicks" to Schema.array(
                description = "Exactly 3 top counter-pick heroes for the user's preferred role",
                items = Schema.obj(
                    properties = mapOf(
                        "heroNameEn" to Schema.string(description = "Official English MLBB hero name"),
                        "heroNameLocalized" to Schema.string(description = "Hero name in target language or official English name"),
                        "laneLocalized" to Schema.string(description = "Recommended lane in target language"),
                        "matchAdvantage" to Schema.string(description = "Short matchup rating badge in target language"),
                        "reason" to Schema.string(description = "Detailed explanation in target language of why this hero counters the enemy lineup"),
                        "keyTip" to Schema.string(description = "One actionable gameplay tip in target language")
                    )
                )
            ),
            "counterItems" to Schema.array(
                description = "3 to 5 essential counter items (Anti-Heal, Magic Defense, Physical Defense, etc.)",
                items = Schema.obj(
                    properties = mapOf(
                        "itemName" to Schema.string(description = "Official English MLBB item name, e.g., Dominance Ice or Athena's Shield"),
                        "category" to Schema.string(description = "Item category in target language, e.g., Anti-Heal or Magic Defense"),
                        "targetEnemies" to Schema.string(description = "Which enemy hero(es) this item counters"),
                        "reason" to Schema.string(description = "Why buying this item is critical in target language")
                    )
                )
            )
        )
    )

    private fun buildMatchSchema(): Schema = Schema.obj(
        properties = mapOf(
            "matchResult" to Schema.string(description = "Match outcome in target language (Victory / Defeat / Unknown)"),
            "heroPlayed" to Schema.string(description = "Official English hero name played by the user"),
            "kda" to Schema.string(description = "Extracted KDA string, e.g. 7/4/11"),
            "gold" to Schema.string(description = "Extracted gold amount, e.g. 11,450"),
            "medal" to Schema.string(description = "Medal or score extracted, e.g. Gold MVP 9.8"),
            "detectedItems" to Schema.array(
                items = Schema.string(),
                description = "List of official English MLBB item names detected in the user's build"
            ),
            "enemyHeroes" to Schema.array(
                items = Schema.string(),
                description = "List of official English MLBB enemy hero names seen on the scoreboard"
            ),
            "strengths" to Schema.array(
                items = Schema.string(),
                description = "Exactly 3 key strengths in the target language backed by the scoreboard stats"
            ),
            "mistakes" to Schema.array(
                items = Schema.string(),
                description = "Exactly 3 biggest mistakes in the target language focusing heavily on itemization errors vs the enemy lineup"
            ),
            "nextMatchGoal" to Schema.string(
                description = "Exactly 1 concrete, actionable goal for the next match in the target language"
            )
        )
    )

    private fun buildQuickCheckSchema(): Schema = Schema.obj(
        properties = mapOf(
            "tier" to Schema.string(description = "Current MLBB meta tier, e.g. S+, S, A, B, or C"),
            "coreItemName" to Schema.string(description = "Single most important official English core item name for this hero"),
            "coreItemNote" to Schema.string(description = "1 concise sentence in the target language explaining why this core item is mandatory"),
            "mainCombo" to Schema.string(description = "Primary skill combo order using arrows, e.g. Skill 2 -> Skill 1 -> Ultimate"),
            "mainComboNote" to Schema.string(description = "1 concise execution tip in the target language"),
            "primaryCounter" to Schema.string(description = "Official English name of the #1 counter hero to watch out for"),
            "primaryCounterNote" to Schema.string(description = "1 concise sentence in the target language on why this hero counters them or how to play around it")
        )
    )

    suspend fun fetchHeroQuickCheck(
        heroNameEn: String,
        language: AppLanguage
    ): HeroQuickCheckSummary {
        val strings = stringsFor(language)
        val langName = language.aiLanguageName

        val quickModel = Firebase.ai.generativeModel(
            modelName = FAST_MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = buildQuickCheckSchema()
            },
            systemInstruction = content {
                text(
                    """
                    You are an ultra-fast Mobile Legends: Bang Bang (MLBB) coach providing a 3-point Quick-Check card during hero draft/loading screens.
                    Write ALL note/explanation fields strictly in fluent $langName.
                    ALWAYS preserve official in-game English names for heroes, items, and skills (e.g., Fanny, Dominance Ice, Skill 2 -> Ultimate).
                    Keep each note to 1 short, high-impact sentence.
                    """.trimIndent()
                )
            }
        )

        val prompt = """
            Provide a 3-point MLBB Quick-Check summary for the hero "$heroNameEn" in $langName:
            1. Most essential Core Item (official English item name + 1 short sentence why).
            2. Main Skill Combo (compact arrow sequence + 1 short timing tip).
            3. Primary Counter hero (official English hero name + 1 short tip).
            Also include their current meta Tier (S+, S, A, B, or C).
        """.trimIndent()

        try {
            val response = withTimeout(30_000L) {
                quickModel.generateContent(prompt)
            }
            val jsonText = response.text?.trim()
                ?: throw IllegalStateException(strings.errorEmptyResponse)
            val json = JSONObject(jsonText)
            return HeroQuickCheckSummary(
                tier = json.optString("tier", "META").take(6).ifBlank { "META" },
                coreItemName = json.optString("coreItemName", "-").ifBlank { "-" },
                coreItemNote = json.optString("coreItemNote", ""),
                mainCombo = json.optString("mainCombo", "-").ifBlank { "-" },
                mainComboNote = json.optString("mainComboNote", ""),
                primaryCounter = json.optString("primaryCounter", "-").ifBlank { "-" },
                primaryCounterNote = json.optString("primaryCounterNote", "")
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Quick-check timed out for $heroNameEn", e)
            throw IllegalStateException(strings.errorTimeout)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Quick-check failed for $heroNameEn", e)
            throw e
        }
    }

    suspend fun fetchHeroMetaGuide(
        heroNameEn: String,
        heroNameFa: String,
        language: AppLanguage
    ): HeroMetaGuide {
        val strings = stringsFor(language)
        val langName = language.aiLanguageName

        val groundedModel = Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            tools = listOf(Tool.googleSearch()),
            systemInstruction = content {
                text(
                    """
                    You are a professional Mobile Legends: Bang Bang (MLBB) meta coach.
                    Write ALL explanations, summaries, tips, and coaching commentary fluently in $langName.
                    ALWAYS preserve official in-game English names for MLBB heroes, items, emblems, talents, and battle spells (e.g., Fanny, Layla, Dominance Ice, Blade of Despair, Flicker) to avoid player confusion.
                    Use live Google Search grounding for the latest patch and current meta.
                    """.trimIndent()
                )
            }
        )

        val prompt = """
            Use live Google Search to find the latest current-patch meta in Mobile Legends: Bang Bang (MLBB) for the hero "$heroNameEn".
            Respond 100% in fluent $langName using ONLY the exact delimiter tags below (do not translate the English tag names in brackets):

            [TIER]
            Exactly one of: S+, S, A, B, or C
            [/TIER]

            [TIER_SUMMARY]
            2 concise sentences in $langName explaining this hero's current meta standing.
            [/TIER_SUMMARY]

            [CORE_ITEMS]
            6 lines (1 item per line in buy order) with format: Official English Item Name | Short explanation in $langName
            [/CORE_ITEMS]

            [SITUATIONAL_ITEMS]
            3 to 4 lines with format: Official English Item Name | When and against whom to buy in $langName
            [/SITUATIONAL_ITEMS]

            [EMBLEM]
            Line 1: Official English main Emblem name (e.g., Assassin Emblem)
            Next 3 lines: Official English Talent Name | Short explanation in $langName
            [/EMBLEM]

            [BATTLE_SPELL]
            Line 1: Official English Battle Spell name (and optional alternative)
            Line 2: Short explanation in $langName of why this spell is best
            [/BATTLE_SPELL]

            [SKILL_COMBO]
            Line 1: Skill combo order separated by -> (e.g., Skill 2 -> Skill 1 -> Basic Attack -> Ultimate)
            Line 2: Clear timing and execution guide in $langName
            [/SKILL_COMBO]

            [EARLY_GAME]
            Key early game (0-5 min) tips in $langName.
            [/EARLY_GAME]

            [MID_GAME]
            Key mid game (5-12 min, rotations, Turtle, skirmishes) tips in $langName.
            [/MID_GAME]

            [LATE_GAME]
            Key late game (12+ min, Lord, teamfight positioning, closing out) tips in $langName.
            [/LATE_GAME]

            [COUNTERS]
            3 to 4 lines with format: Official English Hero Name | Short explanation in $langName of why they counter $heroNameEn
            [/COUNTERS]

            [SYNERGIES]
            3 to 4 lines with format: Official English Hero Name | Short explanation in $langName of why they synergize well
            [/SYNERGIES]

            [TRENDS_30D]
            Exactly 7 lines representing this hero's ranked/Mythic Win Rate, Pick Rate, and Ban Rate trajectory over the last 30 days grounded in current MLBB stats & patch changes. Use ONLY ASCII digits and format: daysAgo | winRate | pickRate | banRate
            30 | 51.2 | 1.8 | 22.4
            25 | 51.6 | 1.9 | 24.0
            20 | 52.0 | 2.0 | 26.5
            15 | 51.8 | 2.1 | 28.0
            10 | 52.4 | 2.2 | 31.2
            5 | 52.9 | 2.3 | 34.0
            0 | 53.2 | 2.4 | 36.5
            (Replace the example numbers above with the hero's real grounded 30-day percentages.)
            [/TRENDS_30D]
        """.trimIndent()

        try {
            val response = withTimeout(TIMEOUT_MS) {
                groundedModel.generateContent(prompt)
            }
            val rawText = response.text?.trim().orEmpty()
            if (rawText.isBlank()) {
                throw IllegalStateException(strings.errorEmptyResponse)
            }

            val sources = response.candidates
                .firstOrNull()
                ?.groundingMetadata
                ?.groundingChunks
                ?.mapNotNull { chunk ->
                    val web = chunk.web
                    val uri = web?.uri
                    val title = web?.title
                    if (!uri.isNullOrBlank()) {
                        GroundingSource(
                            title = title?.takeIf { it.isNotBlank() } ?: uri,
                            uri = uri
                        )
                    } else {
                        null
                    }
                }
                ?.distinctBy { it.uri }
                ?.take(6)
                .orEmpty()

            return parseHeroMetaGuide(
                heroNameEn = heroNameEn,
                heroNameFa = heroNameFa,
                rawText = rawText,
                sources = sources
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Hero guide request timed out for $heroNameEn", e)
            throw IllegalStateException(strings.errorTimeout)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch hero meta guide for $heroNameEn", e)
            throw e
        }
    }

    suspend fun fetchDraftCounters(
        enemyHeroes: List<String>,
        preferredRole: MlbbRole,
        language: AppLanguage
    ): DraftCounterResult {
        val strings = stringsFor(language)
        val langName = language.aiLanguageName
        val enemyListText = enemyHeroes.joinToString(", ")

        val draftModel = Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = buildDraftSchema()
            },
            systemInstruction = content {
                text(
                    """
                    You are a professional Mobile Legends: Bang Bang (MLBB) draft & counter-pick coach.
                    Write ALL descriptive fields strictly and fluently in $langName.
                    Preserve official English MLBB hero names and item names (e.g., Fanny, Dominance Ice, Athena's Shield).
                    """.trimIndent()
                )
            }
        )

        val prompt = """
            The enemy team in Mobile Legends: Bang Bang drafted: $enemyListText.
            The user wants to play the ${preferredRole.nameEn} role.
            Respond strictly in $langName:
            1. Analyze the enemy team's win condition, damage type, healing, and CC.
            2. Recommend the top 3 ${preferredRole.nameEn} counter-pick heroes with concise reasons why each counters the lineup plus a key execution tip.
            3. Recommend essential counter items (like Anti-Heal: Dominance Ice / Sea Halberd / Glowing Wand, or Magic Defense: Athena's Shield / Radiant Armor) specifying which enemy heroes they counter.
        """.trimIndent()

        try {
            val response = withTimeout(TIMEOUT_MS) {
                draftModel.generateContent(prompt)
            }
            val jsonText = response.text?.trim()
                ?: throw IllegalStateException(strings.errorEmptyResponse)

            val json = JSONObject(jsonText)
            val summary = json.optString("enemyCompAnalysis", "")

            val picksJson = json.optJSONArray("topPicks") ?: JSONArray()
            val topPicks = (0 until picksJson.length()).map { idx ->
                val obj = picksJson.getJSONObject(idx)
                val enName = obj.optString("heroNameEn", "")
                val locName = obj.optString("heroNameLocalized", enName).ifBlank { enName }
                CounterPickHero(
                    heroNameEn = enName,
                    heroNameFa = locName,
                    laneFa = obj.optString("laneLocalized", ""),
                    matchAdvantageFa = obj.optString("matchAdvantage", ""),
                    reasonFa = obj.optString("reason", ""),
                    keyTipFa = obj.optString("keyTip", "")
                )
            }

            val itemsJson = json.optJSONArray("counterItems") ?: JSONArray()
            val counterItems = (0 until itemsJson.length()).map { idx ->
                val obj = itemsJson.getJSONObject(idx)
                CounterItemTip(
                    itemName = obj.optString("itemName", ""),
                    categoryFa = obj.optString("category", ""),
                    targetEnemiesFa = obj.optString("targetEnemies", ""),
                    reasonFa = obj.optString("reason", "")
                )
            }

            return DraftCounterResult(
                enemyCompAnalysisFa = summary,
                topPicks = topPicks,
                counterItems = counterItems
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Draft counter request timed out", e)
            throw IllegalStateException(strings.errorTimeout)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch draft counters", e)
            throw e
        }
    }

    suspend fun analyzeMatchScreenshot(
        bitmap: Bitmap,
        heroPlayedInput: String,
        language: AppLanguage
    ): MatchAnalysisReport {
        val strings = stringsFor(language)
        val langName = language.aiLanguageName
        val heroHint = heroPlayedInput.trim().ifBlank { "Not specified (detect highlighted player row from screenshot)" }

        val visionModel = Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = buildMatchSchema()
            },
            systemInstruction = content {
                text(
                    """
                    You are a senior Mobile Legends: Bang Bang (MLBB) post-match coach analyzing a scoreboard screenshot.
                    Write ALL coaching insights, result labels, strengths, mistakes, and next-match goals strictly in fluent $langName.
                    Keep official MLBB hero names and item names in English (e.g., Beatrix, Dominance Ice, Wind of Nature) for clarity.
                    If any scoreboard field is unclear in the image, honestly state so in $langName and analyze what is visible.
                    """.trimIndent()
                )
            }
        )

        val prompt = """
            Analyze this Mobile Legends: Bang Bang post-match scoreboard screenshot.
            The user indicated they played: "$heroHint".
            Extract the match result, hero played, KDA, gold, medal/rating, built items, and enemy team heroes.
            Then provide a structured coaching report in fluent $langName:
            - Exactly 3 key strengths (strengths)
            - Exactly 3 biggest mistakes (mistakes), focusing especially on itemization errors against the enemy team
            - Exactly 1 concrete, actionable goal for the next match (nextMatchGoal)
        """.trimIndent()

        try {
            val response = withTimeout(TIMEOUT_MS) {
                visionModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
            }
            val jsonText = response.text?.trim()
                ?: throw IllegalStateException(strings.errorEmptyResponse)

            val json = JSONObject(jsonText)
            return MatchAnalysisReport(
                matchResultFa = json.optString("matchResult", "-"),
                heroPlayed = json.optString("heroPlayed", heroHint),
                kda = json.optString("kda", "-"),
                gold = json.optString("gold", "-"),
                medalFa = json.optString("medal", "-"),
                detectedItems = json.optJSONArray("detectedItems").toStringList(),
                enemyHeroes = json.optJSONArray("enemyHeroes").toStringList(),
                strengths = json.optJSONArray("strengths").toStringList(),
                mistakes = json.optJSONArray("mistakes").toStringList(),
                nextMatchGoal = json.optString("nextMatchGoal", "")
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Match screenshot analysis timed out", e)
            throw IllegalStateException(strings.errorTimeout)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to analyze match screenshot", e)
            throw e
        }
    }

    fun createMatchFollowUpChat(
        report: MatchAnalysisReport,
        language: AppLanguage
    ): Chat {
        val langName = language.aiLanguageName
        val chatModel = Firebase.ai.generativeModel(
            modelName = MODEL_NAME,
            systemInstruction = content {
                text(
                    """
                    You are a personal Mobile Legends: Bang Bang (MLBB) match coach.
                    Respond concisely, warmly, and practically in the user's active app language (currently $langName, or switch immediately if a message specifies a new target language).
                    Keep official MLBB hero and item names in English.
                    """.trimIndent()
                )
            }
        )

        val contextSummary = buildString {
            appendLine("Analyzed Mobile Legends match summary:")
            appendLine("- Hero Played: ${report.heroPlayed}")
            appendLine("- Match Result: ${report.matchResultFa}")
            appendLine("- KDA: ${report.kda} | Gold: ${report.gold} | Medal: ${report.medalFa}")
            appendLine("- Built Items: ${report.detectedItems.joinToString(", ")}")
            appendLine("- Enemy Lineup: ${report.enemyHeroes.joinToString(", ")}")
            appendLine("- Strengths: ${report.strengths.joinToString(" | ")}")
            appendLine("- Mistakes: ${report.mistakes.joinToString(" | ")}")
            appendLine("- Next Match Goal: ${report.nextMatchGoal}")
        }

        val greeting = stringsFor(language).matchChatGreeting.format(report.heroPlayed)

        return chatModel.startChat(
            history = listOf(
                content("user") {
                    text("Here is my analyzed match report. Answer my follow-up questions in $langName.\n$contextSummary")
                },
                content("model") {
                    text(greeting)
                }
            )
        )
    }

    fun createPersonaCoachChat(
        persona: CoachPersona,
        language: AppLanguage
    ): Chat {
        val personaModel = Firebase.ai.generativeModel(
            modelName = persona.modelName,
            systemInstruction = content {
                text(persona.systemPrompt(language))
            }
        )
        return personaModel.startChat(
            history = listOf(
                content("model") {
                    text(persona.greeting(language))
                }
            )
        )
    }

    suspend fun generateOrEditImage(
        prompt: String,
        sourceBitmap: Bitmap? = null,
        language: AppLanguage
    ): Bitmap {
        val strings = stringsFor(language)
        try {
            val response = withTimeout(TIMEOUT_MS) {
                if (sourceBitmap != null) {
                    imageStudioModel.generateContent(
                        content {
                            image(sourceBitmap)
                            text(prompt)
                        }
                    )
                } else {
                    imageStudioModel.generateContent(prompt)
                }
            }

            val imagePart = response.candidates.firstOrNull()
                ?.content?.parts?.filterIsInstance<ImagePart>()
                ?.firstOrNull()

            return imagePart?.image
                ?: throw IllegalStateException(strings.errorEmptyResponse)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Image generation timed out", e)
            throw IllegalStateException(strings.errorTimeout)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Image generation/editing failed", e)
            throw e
        }
    }

    fun sendChatMessageStream(
        chat: Chat,
        userMessage: String,
        language: AppLanguage
    ): Flow<String> = flow {
        val strings = stringsFor(language)
        // Always reinforce active UI language so mid-session language switches take effect immediately
        val languageAwareMessage =
            "[Respond strictly in ${language.aiLanguageName}. Keep official MLBB hero & item names in English.]\n$userMessage"
        try {
            withTimeout(TIMEOUT_MS) {
                chat.sendMessageStream(languageAwareMessage).collect { chunk ->
                    val piece = chunk.text.orEmpty()
                    if (piece.isNotEmpty()) {
                        emit(piece)
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Chat timed out", e)
            throw IllegalStateException(strings.errorTimeout)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Chat failed", e)
            throw e
        }
    }

    private fun parseHeroMetaGuide(
        heroNameEn: String,
        heroNameFa: String,
        rawText: String,
        sources: List<GroundingSource>
    ): HeroMetaGuide {
        fun extractSection(tag: String): String {
            val startTag = "[$tag]"
            val endTag = "[/$tag]"
            val startIdx = rawText.indexOf(startTag)
            if (startIdx == -1) return ""
            val contentStart = startIdx + startTag.length
            val endIdx = rawText.indexOf(endTag, contentStart)
            return if (endIdx != -1) {
                rawText.substring(contentStart, endIdx).trim()
            } else {
                rawText.substring(contentStart).trim()
            }
        }

        fun parseItems(block: String): List<HeroBuildItem> {
            return block.lines()
                .map { it.trim().removePrefix("-").removePrefix("•").removePrefix("*").trim() }
                .filter { it.isNotBlank() }
                .map { line ->
                    val normalizedLine = line.replace('｜', '|')
                    val parts = normalizedLine.split("|", limit = 2)
                    if (parts.size == 2) {
                        HeroBuildItem(
                            name = parts[0].trim().replace("**", ""),
                            reasonFa = parts[1].trim()
                        )
                    } else {
                        HeroBuildItem(name = normalizedLine.replace("**", ""), reasonFa = "")
                    }
                }
        }

        fun parseBulletLines(block: String): List<String> {
            return block.lines()
                .map { it.trim().removePrefix("-").removePrefix("•").removePrefix("*").trim() }
                .filter { it.isNotBlank() }
                .map { it.replace("**", "").replace('｜', '|') }
        }

        val tier = extractSection("TIER").lines().firstOrNull()?.trim()?.take(6).orEmpty()
        val tierSummary = extractSection("TIER_SUMMARY")
        val coreItems = parseItems(extractSection("CORE_ITEMS"))
        val situationalItems = parseItems(extractSection("SITUATIONAL_ITEMS"))

        val emblemLines = parseBulletLines(extractSection("EMBLEM"))
        val emblemMain = emblemLines.firstOrNull().orEmpty()
        val emblemTalents = emblemLines.drop(1)

        val spellLines = parseBulletLines(extractSection("BATTLE_SPELL"))
        val battleSpell = spellLines.firstOrNull().orEmpty()
        val battleSpellReason = spellLines.drop(1).joinToString(" ")

        val comboLines = parseBulletLines(extractSection("SKILL_COMBO"))
        val comboRawOrder = comboLines.firstOrNull().orEmpty()
        val comboSteps = comboRawOrder
            .split("->", "←", "→", "➜", "+")
            .map { it.trim() }
            .filter { it.isNotBlank() }
        val comboExplanation = comboLines.drop(1).joinToString("\n")

        fun parsePercent(raw: String): Float? {
            val ascii = buildString(raw.length) {
                raw.forEach { ch ->
                    when (ch) {
                        in '۰'..'۹' -> append(('0'.code + (ch.code - '۰'.code)).toChar())
                        in '٠'..'٩' -> append(('0'.code + (ch.code - '٠'.code)).toChar())
                        ',' -> append('.')
                        else -> append(ch)
                    }
                }
            }
            val cleaned = Regex("""-?\d+(\.\d+)?""").find(ascii)?.value ?: return null
            return cleaned.toFloatOrNull()
        }

        fun parseTrends(block: String): List<HeroTrendPoint> {
            val defaultOffsets = listOf(30, 25, 20, 15, 10, 5, 0)
            val parsed = block.lines()
                .map { it.trim().removePrefix("-").removePrefix("•").removePrefix("*").trim() }
                .filter { it.isNotBlank() && !it.startsWith("(") }
                .mapNotNull { line ->
                    val parts = line.replace('｜', '|').split("|").map { it.trim() }
                    if (parts.size < 4) return@mapNotNull null
                    val dayVal = parsePercent(parts[0])?.toInt()
                    val wr = parsePercent(parts[1]) ?: return@mapNotNull null
                    val pr = parsePercent(parts[2]) ?: return@mapNotNull null
                    val br = parsePercent(parts[3]) ?: return@mapNotNull null
                    HeroTrendPoint(
                        dayOffsetFromEnd = (dayVal ?: 0).coerceIn(0, 30),
                        winRate = wr.coerceIn(30f, 75f),
                        pickRate = pr.coerceIn(0f, 100f),
                        banRate = br.coerceIn(0f, 100f)
                    )
                }

            if (parsed.size < 2) return emptyList()

            // If day offsets look ascending (1..30) instead of daysAgo (30..0), convert cleanly
            val allOffsetsDistinct = parsed.map { it.dayOffsetFromEnd }.distinct().size == parsed.size
            val normalized = if (!allOffsetsDistinct) {
                parsed.take(defaultOffsets.size).mapIndexed { idx, pt ->
                    pt.copy(dayOffsetFromEnd = defaultOffsets.getOrElse(idx) { (30 - idx * 5).coerceAtLeast(0) })
                }
            } else if (parsed.first().dayOffsetFromEnd < parsed.last().dayOffsetFromEnd) {
                val maxDay = parsed.maxOf { it.dayOffsetFromEnd }.coerceAtLeast(1)
                parsed.map { pt -> pt.copy(dayOffsetFromEnd = (maxDay - pt.dayOffsetFromEnd).coerceIn(0, 30)) }
            } else {
                parsed
            }

            return normalized.sortedByDescending { it.dayOffsetFromEnd }
        }

        val earlyTip = extractSection("EARLY_GAME")
        val midTip = extractSection("MID_GAME")
        val lateTip = extractSection("LATE_GAME")
        val counters = parseBulletLines(extractSection("COUNTERS"))
        val synergies = parseBulletLines(extractSection("SYNERGIES"))
        val trend30d = parseTrends(extractSection("TRENDS_30D"))

        val hasStructuredData = tier.isNotBlank() || coreItems.isNotEmpty() || earlyTip.isNotBlank()

        return HeroMetaGuide(
            heroNameEn = heroNameEn,
            heroNameFa = heroNameFa,
            tier = tier.ifBlank { "META" },
            tierSummaryFa = tierSummary,
            coreItems = coreItems,
            situationalItems = situationalItems,
            emblemMain = emblemMain,
            emblemTalents = emblemTalents,
            battleSpell = battleSpell,
            battleSpellReasonFa = battleSpellReason,
            skillComboSteps = comboSteps,
            comboExplanationFa = comboExplanation,
            earlyGameTipFa = earlyTip,
            midGameTipFa = midTip,
            lateGameTipFa = lateTip,
            counters = counters,
            synergies = synergies,
            groundingSources = sources,
            trend30d = trend30d,
            rawFallbackFa = if (hasStructuredData) null else rawText
        )
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return (0 until length())
            .map { optString(it, "").trim() }
            .filter { it.isNotBlank() }
    }
}
