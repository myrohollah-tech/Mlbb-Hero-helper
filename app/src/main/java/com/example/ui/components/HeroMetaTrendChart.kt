package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppLanguage
import com.example.i18n.TrendChartStrings
import com.example.i18n.trendChartStrings
import com.example.model.HeroTrendPoint
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
import java.util.Locale
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

private enum class TrendChartMode {
    WIN_RATE,
    PICK_BAN
}

@Composable
fun HeroMetaTrendChartCard(
    points: List<HeroTrendPoint>,
    modifier: Modifier = Modifier
) {
    if (points.size < 2) return

    val language = LocalAppLanguage.current
    val chartStrings = remember(language) { language.trendChartStrings() }
    var chartMode by remember { mutableStateOf(TrendChartMode.WIN_RATE) }
    var selectedIndex by remember(points) { mutableIntStateOf(points.lastIndex) }

    // Smooth entry/mode-change reveal animation
    var revealTarget by remember(points, chartMode) { mutableStateOf(0f) }
    LaunchedEffect(points, chartMode) {
        revealTarget = 1f
    }
    val revealProgress by animateFloatAsState(
        targetValue = revealTarget,
        animationSpec = tween(durationMillis = 520, easing = FastOutSlowInEasing),
        label = "trend_chart_reveal"
    )

    val safeIndex = selectedIndex.coerceIn(0, points.lastIndex)
    val firstPoint = points.first()
    val latestPoint = points.last()
    val inspectedPoint = points[safeIndex]

    val winDelta = latestPoint.winRate - firstPoint.winRate
    val pickDelta = latestPoint.pickRate - firstPoint.pickRate
    val banDelta = latestPoint.banRate - firstPoint.banRate

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hero_meta_trend_chart_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy800),
        border = BorderStroke(1.dp, BorderCyan.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(
                title = chartStrings.chartTitle,
                subtitle = chartStrings.chartSubtitle,
                icon = Icons.AutoMirrored.Rounded.ShowChart,
                accentColor = ElectricCyan
            )

            // 3 KPI Tiles showing latest 30d metrics + net change
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TrendKpiTile(
                    label = chartStrings.metricWinRate,
                    value = formatPercent(latestPoint.winRate),
                    delta = winDelta,
                    accentColor = EmeraldAccent,
                    selected = chartMode == TrendChartMode.WIN_RATE,
                    onClick = { chartMode = TrendChartMode.WIN_RATE },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("trend_kpi_win_rate")
                )
                TrendKpiTile(
                    label = chartStrings.metricPickRate,
                    value = formatPercent(latestPoint.pickRate),
                    delta = pickDelta,
                    accentColor = ElectricCyan,
                    selected = chartMode == TrendChartMode.PICK_BAN,
                    onClick = { chartMode = TrendChartMode.PICK_BAN },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("trend_kpi_pick_rate")
                )
                TrendKpiTile(
                    label = chartStrings.metricBanRate,
                    value = formatPercent(latestPoint.banRate),
                    delta = banDelta,
                    accentColor = CrimsonAccent,
                    selected = chartMode == TrendChartMode.PICK_BAN,
                    onClick = { chartMode = TrendChartMode.PICK_BAN },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("trend_kpi_ban_rate")
                )
            }

            // Segmented Mode Toggle: Win Rate vs Pick/Ban Rate
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DeepNavy900,
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TrendModeTab(
                        label = chartStrings.tabWinRate,
                        selected = chartMode == TrendChartMode.WIN_RATE,
                        accentColor = EmeraldAccent,
                        selectedContainer = EmeraldContainer,
                        onClick = { chartMode = TrendChartMode.WIN_RATE },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("trend_tab_win_rate")
                    )
                    TrendModeTab(
                        label = chartStrings.tabPickBan,
                        selected = chartMode == TrendChartMode.PICK_BAN,
                        accentColor = ElectricCyan,
                        selectedContainer = ElectricCyanContainer,
                        onClick = { chartMode = TrendChartMode.PICK_BAN },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("trend_tab_pick_ban")
                    )
                }
            }

            // Interactive Inspector Strip for the selected checkpoint
            TrendInspectorBar(
                point = inspectedPoint,
                chartMode = chartMode,
                language = language,
                strings = chartStrings
            )

            // Native Jetpack Compose Canvas Area/Line Chart (LTR time axis for standard chronological readability)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                TrendLineCanvas(
                    points = points,
                    chartMode = chartMode,
                    selectedIndex = safeIndex,
                    revealProgress = revealProgress,
                    language = language,
                    strings = chartStrings,
                    onSelectIndex = { selectedIndex = it }
                )
            }

            // Legend + Scrubbing Tip Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (chartMode == TrendChartMode.WIN_RATE) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LegendDot(color = EmeraldAccent, label = chartStrings.metricWinRate)
                        LegendDot(color = RoyalGold.copy(alpha = 0.75f), label = "50%")
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LegendDot(color = ElectricCyan, label = chartStrings.metricPickRate)
                        LegendDot(color = CrimsonAccent, label = chartStrings.metricBanRate)
                    }
                }
                Text(
                    text = chartStrings.scrubHint,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun TrendKpiTile(
    label: String,
    value: String,
    delta: Float,
    accentColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPositive = delta >= 0f
    val deltaColor = if (isPositive) EmeraldAccent else CrimsonAccent
    val deltaPrefix = if (isPositive) "+" else ""

    Surface(
        modifier = modifier
            .heightIn(min = 66.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) DeepNavy900 else DeepNavy950.copy(alpha = 0.7f),
        border = BorderStroke(
            width = if (selected) 1.dp else 1.dp,
            color = if (selected) accentColor.copy(alpha = 0.7f) else BorderSubtle
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = if (isPositive) {
                        Icons.AutoMirrored.Rounded.TrendingUp
                    } else {
                        Icons.AutoMirrored.Rounded.TrendingDown
                    },
                    contentDescription = null,
                    tint = deltaColor,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "$deltaPrefix${String.format(Locale.US, "%.1f%%", delta)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = deltaColor,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun TrendModeTab(
    label: String,
    selected: Boolean,
    accentColor: Color,
    selectedContainer: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) selectedContainer else Color.Transparent,
        border = if (selected) BorderStroke(1.dp, accentColor.copy(alpha = 0.65f)) else null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) Color.White else TextSecondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TrendInspectorBar(
    point: HeroTrendPoint,
    chartMode: TrendChartMode,
    language: AppLanguage,
    strings: TrendChartStrings
) {
    val dayTitle = if (point.dayOffsetFromEnd <= 0) {
        strings.todayLabel
    } else {
        language.format(strings.dayAgoFull, point.dayOffsetFromEnd)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DeepNavy900,
        border = BorderStroke(1.dp, BorderGold.copy(alpha = 0.45f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("trend_inspector_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = RoyalGoldContainer
            ) {
                Text(
                    text = dayTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = RoyalGold,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            if (chartMode == TrendChartMode.WIN_RATE) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = strings.metricWinRate,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Text(
                        text = formatPercent(point.winRate),
                        style = MaterialTheme.typography.titleSmall,
                        color = EmeraldAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ElectricCyan)
                        )
                        Text(
                            text = formatPercent(point.pickRate),
                            style = MaterialTheme.typography.labelLarge,
                            color = ElectricCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CrimsonAccent)
                        )
                        Text(
                            text = formatPercent(point.banRate),
                            style = MaterialTheme.typography.labelLarge,
                            color = CrimsonAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendLineCanvas(
    points: List<HeroTrendPoint>,
    chartMode: TrendChartMode,
    selectedIndex: Int,
    revealProgress: Float,
    language: AppLanguage,
    strings: TrendChartStrings,
    onSelectIndex: (Int) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val axisTextStyle = remember {
        TextStyle(
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }

    val yBounds = remember(points, chartMode) {
        if (chartMode == TrendChartMode.WIN_RATE) {
            val values = points.map { it.winRate }
            val rawMin = minOf(values.min(), 49.5f)
            val rawMax = maxOf(values.max(), 50.5f)
            val pad = maxOf((rawMax - rawMin) * 0.25f, 1.5f)
            floor((rawMin - pad).coerceAtLeast(30f)) to ceil((rawMax + pad).coerceAtMost(75f))
        } else {
            val values = points.flatMap { listOf(it.pickRate, it.banRate) }
            val rawMin = values.min()
            val rawMax = values.max()
            val pad = maxOf((rawMax - rawMin) * 0.18f, 2f)
            floor((rawMin - pad).coerceAtLeast(0f)) to ceil((rawMax + pad).coerceAtMost(100f))
        }
    }

    val minVal = yBounds.first
    val maxVal = (yBounds.second).coerceAtLeast(minVal + 2f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DeepNavy900)
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .semantics { contentDescription = strings.chartCd }
                .testTag("hero_trend_canvas")
                .pointerInput(points) {
                    val leftPadPx = 38.dp.toPx()
                    val rightPadPx = 12.dp.toPx()
                    fun pickNearestIndex(x: Float) {
                        val chartW = (size.width - leftPadPx - rightPadPx).coerceAtLeast(1f)
                        val fraction = ((x - leftPadPx) / chartW).coerceIn(0f, 1f)
                        val idx = (fraction * points.lastIndex).roundToInt().coerceIn(0, points.lastIndex)
                        onSelectIndex(idx)
                    }
                    detectTapGestures { offset ->
                        pickNearestIndex(offset.x)
                    }
                }
                .pointerInput(points) {
                    val leftPadPx = 38.dp.toPx()
                    val rightPadPx = 12.dp.toPx()
                    detectHorizontalDragGestures { change, _ ->
                        val chartW = (size.width - leftPadPx - rightPadPx).coerceAtLeast(1f)
                        val fraction = ((change.position.x - leftPadPx) / chartW).coerceIn(0f, 1f)
                        val idx = (fraction * points.lastIndex).roundToInt().coerceIn(0, points.lastIndex)
                        onSelectIndex(idx)
                    }
                }
        ) {
            val leftPad = 38.dp.toPx()
            val rightPad = 12.dp.toPx()
            val topPad = 10.dp.toPx()
            val bottomPad = 24.dp.toPx()

            val chartWidth = (size.width - leftPad - rightPad).coerceAtLeast(1f)
            val chartHeight = (size.height - topPad - bottomPad).coerceAtLeast(1f)
            val bottomY = topPad + chartHeight

            fun xForIndex(index: Int): Float {
                val ratio = index.toFloat() / points.lastIndex.coerceAtLeast(1).toFloat()
                return leftPad + ratio * chartWidth
            }

            fun yForValue(value: Float): Float {
                val normalized = ((value - minVal) / (maxVal - minVal)).coerceIn(0f, 1f)
                return bottomY - normalized * chartHeight
            }

            // Horizontal Gridlines + Y-axis labels (4 levels)
            val gridSteps = 3
            for (step in 0..gridSteps) {
                val fraction = step.toFloat() / gridSteps.toFloat()
                val y = bottomY - fraction * chartHeight
                val labelValue = minVal + fraction * (maxVal - minVal)

                drawLine(
                    color = DeepNavy700.copy(alpha = 0.7f),
                    start = Offset(leftPad, y),
                    end = Offset(leftPad + chartWidth, y),
                    strokeWidth = 1.dp.toPx()
                )

                val label = "${labelValue.roundToInt()}%"
                val measured = textMeasurer.measure(label, axisTextStyle)
                drawText(
                    textLayoutResult = measured,
                    topLeft = Offset(
                        x = (leftPad - measured.size.width - 6.dp.toPx()).coerceAtLeast(0f),
                        y = y - measured.size.height / 2f
                    )
                )
            }

            // 50% Equilibrium reference dashed line in Win Rate mode
            if (chartMode == TrendChartMode.WIN_RATE && 50f in minVal..maxVal) {
                val y50 = yForValue(50f)
                drawLine(
                    color = RoyalGold.copy(alpha = 0.55f),
                    start = Offset(leftPad, y50),
                    end = Offset(leftPad + chartWidth, y50),
                    strokeWidth = 1.2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                )
            }

            // Selected vertical crosshair
            val selectedX = xForIndex(selectedIndex)
            drawLine(
                color = RoyalGold.copy(alpha = 0.45f),
                start = Offset(selectedX, topPad),
                end = Offset(selectedX, bottomY),
                strokeWidth = 1.2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            // Draw Smooth Curves + Gradient Area Fills
            if (chartMode == TrendChartMode.WIN_RATE) {
                val coords = points.mapIndexed { idx, p ->
                    Offset(xForIndex(idx), yForValue(p.winRate))
                }
                drawSmoothSeries(
                    coords = coords,
                    bottomY = bottomY,
                    lineColor = EmeraldAccent,
                    revealProgress = revealProgress,
                    selectedIndex = selectedIndex
                )
            } else {
                val banCoords = points.mapIndexed { idx, p ->
                    Offset(xForIndex(idx), yForValue(p.banRate))
                }
                val pickCoords = points.mapIndexed { idx, p ->
                    Offset(xForIndex(idx), yForValue(p.pickRate))
                }
                drawSmoothSeries(
                    coords = banCoords,
                    bottomY = bottomY,
                    lineColor = CrimsonAccent,
                    revealProgress = revealProgress,
                    selectedIndex = selectedIndex
                )
                drawSmoothSeries(
                    coords = pickCoords,
                    bottomY = bottomY,
                    lineColor = ElectricCyan,
                    revealProgress = revealProgress,
                    selectedIndex = selectedIndex
                )
            }

            // X-axis tick labels (Start, Middle, End)
            val tickIndices = linkedSetOf(0, points.lastIndex / 2, points.lastIndex)
            tickIndices.forEach { idx ->
                val pt = points[idx]
                val label = if (pt.dayOffsetFromEnd <= 0) {
                    strings.todayLabel
                } else {
                    language.format(strings.dayAgoAxis, pt.dayOffsetFromEnd)
                }
                val measured = textMeasurer.measure(label, axisTextStyle)
                val xCenter = xForIndex(idx)
                val xPos = (xCenter - measured.size.width / 2f)
                    .coerceIn(leftPad, size.width - measured.size.width)
                drawText(
                    textLayoutResult = measured,
                    topLeft = Offset(x = xPos, y = bottomY + 6.dp.toPx())
                )
            }
        }
    }
}

private fun DrawScope.drawSmoothSeries(
    coords: List<Offset>,
    bottomY: Float,
    lineColor: Color,
    revealProgress: Float,
    selectedIndex: Int
) {
    if (coords.size < 2) return

    // Interpolate vertical animation smoothly upward from baseline
    val animatedCoords = coords.map { pt ->
        val animatedY = bottomY - (bottomY - pt.y) * revealProgress
        Offset(pt.x, animatedY)
    }

    val linePath = Path().apply {
        moveTo(animatedCoords.first().x, animatedCoords.first().y)
        for (i in 0 until animatedCoords.lastIndex) {
            val current = animatedCoords[i]
            val next = animatedCoords[i + 1]
            val controlX = (current.x + next.x) / 2f
            cubicTo(
                controlX, current.y,
                controlX, next.y,
                next.x, next.y
            )
        }
    }

    val areaPath = Path().apply {
        addPath(linePath)
        lineTo(animatedCoords.last().x, bottomY)
        lineTo(animatedCoords.first().x, bottomY)
        close()
    }

    drawPath(
        path = areaPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                lineColor.copy(alpha = 0.28f),
                lineColor.copy(alpha = 0.02f)
            ),
            startY = animatedCoords.minOf { it.y },
            endY = bottomY
        )
    )

    drawPath(
        path = linePath,
        color = lineColor,
        style = Stroke(
            width = 2.6.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )

    // Data point dots + glowing halo on the active scrubbed node
    animatedCoords.forEachIndexed { idx, pt ->
        val isSelected = idx == selectedIndex
        if (isSelected) {
            drawCircle(
                color = lineColor.copy(alpha = 0.28f),
                radius = 9.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = DeepNavy950,
                radius = 5.5.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = lineColor,
                radius = 4.dp.toPx(),
                center = pt
            )
        } else {
            drawCircle(
                color = DeepNavy900,
                radius = 3.5.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = lineColor.copy(alpha = 0.85f),
                radius = 2.3.dp.toPx(),
                center = pt
            )
        }
    }
}

@Composable
private fun LegendDot(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatPercent(value: Float): String {
    val clean = if (abs(value) < 0.05f) 0f else value
    return String.format(Locale.US, "%.1f%%", clean)
}
