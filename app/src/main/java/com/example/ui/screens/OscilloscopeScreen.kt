package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.StringsHelper
import com.example.ui.theme.CircuitBorder
import com.example.ui.theme.CircuitEmerald
import com.example.ui.theme.CircuitNavyDark
import com.example.ui.theme.CircuitNavySurface
import com.example.ui.theme.CircuitNavySurfaceVariant
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechTextSecondary
import com.example.ui.theme.TechWhite
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt

enum class SignalSource(val nameBn: String, val nameEn: String, val baseFreqHz: Float, val defaultType: String) {
    SINE_50HZ("এসি ৫০Hz সাইন তরঙ্গ", "AC 50Hz Mains Sine Wave", 50f, "SINE"),
    TIMER_555("NE555 পালস স্কয়ার ওয়েভ", "NE555 1kHz Clock Pulse", 1000f, "SQUARE"),
    RECTIFIED("ফুল-ওয়েভ রেক্টিফাইড ডিসি", "Full-Wave Rectified DC", 100f, "RECTIFIED"),
    TRIANGLE_AUDIO("১kHz ট্রায়াঙ্গেল অডিও সিগন্যাল", "1kHz Triangle Audio", 1000f, "TRIANGLE")
}

@Composable
fun OscilloscopeScreen(
    language: AppLanguage = AppLanguage.BANGLA,
    modifier: Modifier = Modifier
) {
    var selectedSignal by remember { mutableStateOf(SignalSource.TIMER_555) }
    var isRunning by remember { mutableStateOf(true) }

    // Channel 1 controls
    var ch1Enabled by remember { mutableStateOf(true) }
    var ch1VoltsPerDiv by remember { mutableFloatStateOf(1.0f) } // 0.1, 0.5, 1.0, 2.0, 5.0
    var ch1Offset by remember { mutableFloatStateOf(0f) }

    // Channel 2 controls
    var ch2Enabled by remember { mutableStateOf(true) }
    var ch2VoltsPerDiv by remember { mutableFloatStateOf(2.0f) }
    var ch2Offset by remember { mutableFloatStateOf(0f) }

    // Timebase (Time / Div in milliseconds)
    var timePerDivMs by remember { mutableFloatStateOf(0.5f) } // 0.1ms to 5ms

    // Trigger
    var triggerLevel by remember { mutableFloatStateOf(0.0f) }
    var triggerSlopeRising by remember { mutableStateOf(true) }

    // Animation phase
    val infiniteTransition = rememberInfiniteTransition(label = "OscilloscopeSweep")
    val sweepPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isRunning) 1200 else Int.MAX_VALUE, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .verticalScroll(scrollState)
            .padding(12.dp)
    ) {
        // 1. INSTRUMENT BEZEL HEADER
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CircuitNavySurface),
            shape = RoundedCornerShape(14.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CircuitBorder))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan.copy(alpha = 0.15f))
                            .border(1.dp, ElectricCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ShowChart, contentDescription = "DSO", tint = ElectricCyan, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = StringsHelper.get(language, "ডুয়েল-চ্যানেল ডিজিটাল অসিলোস্কোপ", "Dual-Channel Digital Storage Scope"),
                            color = TechWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Model: DSO-8000 Pro • 100MSa/s • Bandwidth: 50MHz",
                            color = TechTextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Run / Freeze Frame button
                Button(
                    onClick = { isRunning = !isRunning },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) CircuitEmerald else DangerRed
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp).testTag("scope_run_freeze_btn")
                ) {
                    Icon(
                        if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Freeze",
                        tint = CircuitNavyDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isRunning) StringsHelper.get(language, "চলমান (RUN)", "RUN") else StringsHelper.get(language, "স্থির (HOLD)", "HOLD"),
                        color = CircuitNavyDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. SIGNAL PROBE PRESET SELECTION
        Text(
            text = StringsHelper.get(language, "প্রোব সিগন্যাল উৎস (Input Signal Probe):", "Input Signal Probe Source:"),
            color = TechTextSecondary,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(SignalSource.values()) { src ->
                val isSelected = selectedSignal == src
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedSignal = src },
                    label = {
                        Text(
                            text = if (language == AppLanguage.BANGLA) src.nameBn else src.nameEn,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricCyan,
                        selectedLabelColor = CircuitNavyDark,
                        containerColor = CircuitNavySurfaceVariant,
                        labelColor = TechWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. CRT / LCD PHOSPHOR SCREEN (8x10 DIVISIONS GRATICULE)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF040711)),
            shape = RoundedCornerShape(12.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E293B)))
        ) {
            val textMeasurer = rememberTextMeasurer()
            Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                val w = size.width
                val h = size.height
                val numDivX = 10
                val numDivY = 8
                val stepX = w / numDivX
                val stepY = h / numDivY

                // Draw Background Reticle Grid
                val gridColor = Color(0xFF0D1B2A)
                val axisColor = Color(0xFF1E3A5F)

                for (i in 0..numDivX) {
                    val x = i * stepX
                    val isCenter = i == numDivX / 2
                    drawLine(
                        color = if (isCenter) axisColor else gridColor,
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = if (isCenter) 1.5f else 0.8f,
                        pathEffect = if (isCenter) null else PathEffect.dashPathEffect(floatArrayOf(2f, 4f))
                    )
                }

                for (j in 0..numDivY) {
                    val y = j * stepY
                    val isCenter = j == numDivY / 2
                    drawLine(
                        color = if (isCenter) axisColor else gridColor,
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = if (isCenter) 1.5f else 0.8f,
                        pathEffect = if (isCenter) null else PathEffect.dashPathEffect(floatArrayOf(2f, 4f))
                    )
                }

                // Sub-ticks on center axes
                val subTickColor = Color(0xFF254B7A)
                val centerX = w / 2f
                val centerY = h / 2f
                for (k in -20..20) {
                    val tx = centerX + k * (stepX / 5f)
                    if (tx in 0f..w) {
                        drawLine(subTickColor, Offset(tx, centerY - 3f), Offset(tx, centerY + 3f), 1f)
                    }
                    val ty = centerY + k * (stepY / 5f)
                    if (ty in 0f..h) {
                        drawLine(subTickColor, Offset(centerX - 3f, ty), Offset(centerX + 3f, ty), 1f)
                    }
                }

                // Trigger Level Line
                val triggerY = centerY - (triggerLevel / ch1VoltsPerDiv) * stepY
                if (triggerY in 0f..h) {
                    drawLine(
                        color = Color(0xFFFBBF24).copy(alpha = 0.6f),
                        start = Offset(0f, triggerY),
                        end = Offset(w, triggerY),
                        strokeWidth = 1f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "T▶",
                        topLeft = Offset(4f, triggerY - 8f),
                        style = TextStyle(color = Color(0xFFFBBF24), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    )
                }

                // Render Channel 1 Trace (Phosphor Cyan #00E5FF)
                if (ch1Enabled) {
                    val path1 = Path()
                    val totalTimeWindowMs = timePerDivMs * numDivX
                    val freq = selectedSignal.baseFreqHz
                    val phase = if (isRunning) sweepPhase else 0f
                    val ampVolts = when (selectedSignal) {
                        SignalSource.SINE_50HZ -> 9.0f
                        SignalSource.TIMER_555 -> 5.0f
                        SignalSource.RECTIFIED -> 8.5f
                        SignalSource.TRIANGLE_AUDIO -> 4.0f
                    }

                    var isFirst = true
                    val numPoints = 180
                    for (pt in 0..numPoints) {
                        val frac = pt.toFloat() / numPoints
                        val x = frac * w
                        val tMs = frac * totalTimeWindowMs
                        val tSec = tMs / 1000f

                        val vInstant: Float = when (selectedSignal) {
                            SignalSource.SINE_50HZ -> {
                                ampVolts * sin(2f * PI.toFloat() * freq * tSec + phase)
                            }
                            SignalSource.TIMER_555 -> {
                                val s = sin(2f * PI.toFloat() * freq * tSec + phase)
                                if (s >= 0f) ampVolts else 0f
                            }
                            SignalSource.RECTIFIED -> {
                                ampVolts * abs(sin(2f * PI.toFloat() * freq * tSec + phase))
                            }
                            SignalSource.TRIANGLE_AUDIO -> {
                                val period = 1f / freq
                                val norm = (tSec % period) / period
                                val tri = if (norm < 0.5f) (norm * 4f - 1f) else ((1f - norm) * 4f - 1f)
                                ampVolts * tri
                            }
                        }

                        val yNorm = (vInstant + ch1Offset) / ch1VoltsPerDiv
                        val y = centerY - (yNorm * stepY)

                        if (isFirst) {
                            path1.moveTo(x, y.coerceIn(0f, h))
                            isFirst = false
                        } else {
                            path1.lineTo(x, y.coerceIn(0f, h))
                        }
                    }

                    // Phosphor outer glow
                    drawPath(
                        path = path1,
                        color = ElectricCyan.copy(alpha = 0.35f),
                        style = Stroke(width = 4.5f, cap = StrokeCap.Round)
                    )
                    // Sharp trace core
                    drawPath(
                        path = path1,
                        color = ElectricCyan,
                        style = Stroke(width = 2.0f, cap = StrokeCap.Round)
                    )
                }

                // Render Channel 2 Trace (Golden Amber #FBBF24 - Inverted or Filtered Response)
                if (ch2Enabled) {
                    val path2 = Path()
                    val totalTimeWindowMs = timePerDivMs * numDivX
                    val freq = selectedSignal.baseFreqHz
                    val phase = if (isRunning) sweepPhase else 0f
                    val amp2Volts = when (selectedSignal) {
                        SignalSource.SINE_50HZ -> 4.5f
                        SignalSource.TIMER_555 -> 2.5f
                        SignalSource.RECTIFIED -> 6.0f
                        SignalSource.TRIANGLE_AUDIO -> 2.0f
                    }

                    var isFirst = true
                    val numPoints = 180
                    for (pt in 0..numPoints) {
                        val frac = pt.toFloat() / numPoints
                        val x = frac * w
                        val tMs = frac * totalTimeWindowMs
                        val tSec = tMs / 1000f

                        // Phase-shifted response (e.g. 90-degree lag or filtered)
                        val vInstant2 = amp2Volts * sin(2f * PI.toFloat() * freq * tSec + phase - (PI.toFloat() / 2f))
                        val yNorm2 = (vInstant2 + ch2Offset) / ch2VoltsPerDiv
                        val y2 = centerY - (yNorm2 * stepY)

                        if (isFirst) {
                            path2.moveTo(x, y2.coerceIn(0f, h))
                            isFirst = false
                        } else {
                            path2.lineTo(x, y2.coerceIn(0f, h))
                        }
                    }

                    drawPath(
                        path = path2,
                        color = Color(0xFFFBBF24).copy(alpha = 0.3f),
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )
                    drawPath(
                        path = path2,
                        color = Color(0xFFFBBF24),
                        style = Stroke(width = 1.8f, cap = StrokeCap.Round)
                    )
                }

                // On-Screen Legend & Channel Badges
                drawText(
                    textMeasurer = textMeasurer,
                    text = "CH1: ${String.format(Locale.US, "%.1f", ch1VoltsPerDiv)}V/div  |  CH2: ${String.format(Locale.US, "%.1f", ch2VoltsPerDiv)}V/div  |  TB: ${String.format(Locale.US, "%.1f", timePerDivMs)}ms/div",
                    topLeft = Offset(10f, 8f),
                    style = TextStyle(color = TechTextMuted, fontSize = 9.5.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. LIVE INSTRUMENT TELEMETRY TABLE (Vpp, Vrms, Freq, Period)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CircuitNavySurface),
            shape = RoundedCornerShape(12.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CircuitBorder))
        ) {
            val vPeak = when (selectedSignal) {
                SignalSource.SINE_50HZ -> 9.0f
                SignalSource.TIMER_555 -> 5.0f
                SignalSource.RECTIFIED -> 8.5f
                SignalSource.TRIANGLE_AUDIO -> 4.0f
            }
            val vpp = vPeak * 2f
            val vrms = when (selectedSignal) {
                SignalSource.SINE_50HZ -> vPeak / sqrt(2f)
                SignalSource.TIMER_555 -> vPeak * sqrt(0.5f)
                SignalSource.RECTIFIED -> vPeak / sqrt(2f)
                SignalSource.TRIANGLE_AUDIO -> vPeak / sqrt(3f)
            }
            val freq = selectedSignal.baseFreqHz
            val periodMs = (1000f / freq)

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = StringsHelper.get(language, "স্বয়ংক্রিয় পরিমাপ ও রিডআউট (Automated Measurements):", "Automated Signal Readouts:"),
                    color = TechWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MetricBox(label = "Vpp (Peak-to-Peak)", value = "${String.format(Locale.US, "%.2f", vpp)} V", color = ElectricCyan)
                    MetricBox(label = "Vrms (True RMS)", value = "${String.format(Locale.US, "%.2f", vrms)} V", color = Color(0xFFFBBF24))
                    MetricBox(label = "Frequency (f)", value = if (freq >= 1000f) "${String.format(Locale.US, "%.1f", freq / 1000f)} kHz" else "${freq.toInt()} Hz", color = CircuitEmerald)
                    MetricBox(label = "Period (T)", value = "${String.format(Locale.US, "%.2f", periodMs)} ms", color = Color(0xFFA855F7))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. HARDWARE KNOBS: TIMEBASE & VOLTS/DIV CONTROLS
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Timebase Dial
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = CircuitNavySurfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = StringsHelper.get(language, "টাইমবেস (Time/Div):", "Timebase (Time/Div):"),
                        color = TechTextMuted,
                        fontSize = 10.5.sp
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.2f", timePerDivMs)} ms",
                        color = ElectricCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(0.1f, 0.5f, 1.0f, 2.0f).forEach { t ->
                            Button(
                                onClick = { timePerDivMs = t },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (timePerDivMs == t) ElectricCyan else CircuitNavyDark
                                ),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text(
                                    text = "${t}ms",
                                    fontSize = 9.5.sp,
                                    color = if (timePerDivMs == t) CircuitNavyDark else TechWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // CH1 Volts/Div Dial
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = CircuitNavySurfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "CH1 Scale (Volts/Div):",
                        color = TechTextMuted,
                        fontSize = 10.5.sp
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.1f", ch1VoltsPerDiv)} V",
                        color = Color(0xFF00E5FF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(0.5f, 1.0f, 2.0f, 5.0f).forEach { v ->
                            Button(
                                onClick = { ch1VoltsPerDiv = v },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (ch1VoltsPerDiv == v) Color(0xFF00E5FF) else CircuitNavyDark
                                ),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text(
                                    text = "${v}V",
                                    fontSize = 9.5.sp,
                                    color = if (ch1VoltsPerDiv == v) CircuitNavyDark else TechWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 6. CHANNEL TOGGLES & TRIGGER CONTROLS
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CircuitNavySurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // CH1 Enable
                Button(
                    onClick = { ch1Enabled = !ch1Enabled },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (ch1Enabled) ElectricCyan.copy(alpha = 0.2f) else CircuitNavySurfaceVariant
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(if (ch1Enabled) ElectricCyan else Color.Transparent)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = if (ch1Enabled) "CH1 ● ON" else "CH1 ○ OFF",
                        color = if (ch1Enabled) ElectricCyan else TechTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // CH2 Enable
                Button(
                    onClick = { ch2Enabled = !ch2Enabled },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (ch2Enabled) Color(0xFFFBBF24).copy(alpha = 0.2f) else CircuitNavySurfaceVariant
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(if (ch2Enabled) Color(0xFFFBBF24) else Color.Transparent)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = if (ch2Enabled) "CH2 ● ON" else "CH2 ○ OFF",
                        color = if (ch2Enabled) Color(0xFFFBBF24) else TechTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Trigger Slope
                Button(
                    onClick = { triggerSlopeRising = !triggerSlopeRising },
                    colors = ButtonDefaults.buttonColors(containerColor = CircuitNavySurfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = if (triggerSlopeRising) "TRIG: ↗ Rising" else "TRIG: ↘ Falling",
                        color = TechWhite,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricBox(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFF040711), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(text = label, color = TechTextMuted, fontSize = 9.sp)
        Text(
            text = value,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
