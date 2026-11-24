package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppLanguage
import com.example.i18n.LocalAppStrings
import com.example.i18n.stringsFor

/**
 * Custom Material 3 Dark ColorScheme tailored for MLBB Hero Coach:
 * Deep Navy surfaces, Royal Gold primary accents, and Electric Cyan secondary highlights.
 */
val MlbbGamingDarkColorScheme = darkColorScheme(
    primary = RoyalGold,
    onPrimary = DeepNavy950,
    primaryContainer = RoyalGoldContainer,
    onPrimaryContainer = RoyalGoldBright,
    inversePrimary = RoyalGoldDim,

    secondary = ElectricCyan,
    onSecondary = DeepNavy950,
    secondaryContainer = ElectricCyanContainer,
    onSecondaryContainer = ElectricCyanBright,

    tertiary = EmeraldAccent,
    onTertiary = DeepNavy950,
    tertiaryContainer = EmeraldContainer,
    onTertiaryContainer = EmeraldAccent,

    background = DeepNavy950,
    onBackground = TextPrimary,

    surface = DeepNavy900,
    onSurface = TextPrimary,
    surfaceVariant = DeepNavy800,
    onSurfaceVariant = TextSecondary,
    surfaceTint = RoyalGold,
    inverseSurface = TextPrimary,
    inverseOnSurface = DeepNavy950,

    surfaceContainerLowest = DeepNavy950,
    surfaceContainerLow = DeepNavy900,
    surfaceContainer = DeepNavy800,
    surfaceContainerHigh = DeepNavy700,
    surfaceContainerHighest = DeepNavy600,

    outline = BorderSubtle,
    outlineVariant = DeepNavy700,
    scrim = Color(0xCC030712),

    error = CrimsonAccent,
    onError = DeepNavy950,
    errorContainer = CrimsonContainer,
    onErrorContainer = CrimsonAccent
)

@Immutable
data class MlbbExtendedColors(
    val goldAccent: Color = RoyalGold,
    val goldBright: Color = RoyalGoldBright,
    val cyanAccent: Color = ElectricCyan,
    val cyanBright: Color = ElectricCyanBright,
    val borderSubtle: Color = BorderSubtle,
    val borderGold: Color = BorderGold,
    val borderCyan: Color = BorderCyan,
    val victoryEmerald: Color = EmeraldAccent,
    val victoryContainer: Color = EmeraldContainer,
    val defeatCrimson: Color = CrimsonAccent,
    val defeatContainer: Color = CrimsonContainer,
    val textMuted: Color = TextMuted,
    val navyCanvasTop: Color = DeepNavy900,
    val navyCanvasBottom: Color = DeepNavy950
) {
    val backgroundBrush: Brush
        get() = Brush.verticalGradient(colors = listOf(navyCanvasTop, navyCanvasBottom))

    val goldCyanAccentBrush: Brush
        get() = Brush.horizontalGradient(
            colors = listOf(
                cyanAccent.copy(alpha = 0.35f),
                goldAccent.copy(alpha = 0.7f),
                cyanAccent.copy(alpha = 0.35f)
            )
        )
}

val LocalMlbbColors = staticCompositionLocalOf { MlbbExtendedColors() }

val MaterialTheme.mlbbColors: MlbbExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalMlbbColors.current

val MlbbShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(26.dp)
)

@Composable
fun MlbbCoachTheme(
    language: AppLanguage = AppLanguage.PERSIAN,
    content: @Composable () -> Unit
) {
    val extendedColors = MlbbExtendedColors()
    val appStrings = stringsFor(language)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides language.layoutDirection,
        LocalAppLanguage provides language,
        LocalAppStrings provides appStrings,
        LocalMlbbColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = MlbbGamingDarkColorScheme,
            typography = AppTypography,
            shapes = MlbbShapes,
            content = content
        )
    }
}
