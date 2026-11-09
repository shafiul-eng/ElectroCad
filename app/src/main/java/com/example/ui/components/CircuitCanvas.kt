package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.CircuitComponent
import com.example.model.CircuitWire
import com.example.model.ComponentType
import com.example.model.Pin
import com.example.model.SimulationState
import com.example.ui.theme.CircuitBorder
import com.example.ui.theme.CircuitEmerald
import com.example.ui.theme.CircuitNavyDark
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricCyanDark
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechWhite
import com.example.ui.theme.WireActiveColor
import com.example.ui.theme.WireIdleColor
import com.example.ui.theme.WireShortColor
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.math.sin

enum class ElectronVisualMode(val labelBn: String, val labelEn: String) {
    PARTICLES("ইলেকট্রন কণা", "Particles"),
    WAVE("ইলেকট্রন তরঙ্গ (Wave)", "Sine Wave"),
    OFF("অ্যানিমেশন বন্ধ", "Static Mode")
}

@Composable
fun CircuitCanvas(
    components: List<CircuitComponent>,
    wires: List<CircuitWire>,
    simulationState: SimulationState,
    selectedComponentId: String?,
    selectedWireId: String?,
    pendingWireStart: Pair<String, String>?,
    visualMode: ElectronVisualMode,
    simSpeed: Float = 1.0f,
    isGridSnapEnabled: Boolean = true,
    gridStepDp: Float = 24f,
    language: AppLanguage = AppLanguage.BANGLA,
    onSelectComponent: (CircuitComponent) -> Unit,
    onSelectWire: (CircuitWire?) -> Unit,
    onMoveComponent: (String, Float, Float) -> Unit,
    onRotateComponent: (String, Float) -> Unit,
    onScaleComponent: (String, Float) -> Unit,
    onPinClicked: (CircuitComponent, Pin) -> Unit,
    onToggleSwitch: (CircuitComponent) -> Unit,
    onComponentLongPress: (CircuitComponent) -> Unit,
    modifier: Modifier = Modifier
) {
    val durationMs = (800 / simSpeed.coerceIn(0.2f, 5.0f)).toInt()

    val infiniteTransition = rememberInfiniteTransition(label = "circuit_flow")
    val flowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow_phase"
    )

    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (500 / simSpeed.coerceIn(0.2f, 5.0f)).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "motor_spin"
    )

    val textMeasurer = rememberTextMeasurer()
    var targetTouchCompId by remember { mutableStateOf<String?>(null) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            // 1. Multi-Touch Gestures: Drag, Rotate, Scale components
            .pointerInput(components, selectedComponentId, isGridSnapEnabled) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    val activeId = targetTouchCompId ?: selectedComponentId
                    if (activeId != null) {
                        if (pan != Offset.Zero) {
                            var dx = pan.x / density
                            var dy = pan.y / density
                            onMoveComponent(activeId, dx, dy)
                        }
                        if (rotation != 0f) {
                            onRotateComponent(activeId, rotation)
                        }
                        if (zoom != 1f) {
                            onScaleComponent(activeId, zoom)
                        }
                    }
                }
            }
            // 2. Tap & Long-Press Handler: Selection, Wire picking, Pin connecting, Tooltip
            .pointerInput(components, wires, pendingWireStart, isGridSnapEnabled) {
                detectTapGestures(
                    onPress = { pressOffset ->
                        targetTouchCompId = null
                        for (comp in components) {
                            val compCenter = Offset(comp.x * density, comp.y * density)
                            if ((pressOffset - compCenter).getDistance() <= 46f * comp.scale * density) {
                                targetTouchCompId = comp.id
                                onSelectComponent(comp)
                                onSelectWire(null)
                                break
                            }
                        }
                    },
                    onLongPress = { longPressOffset ->
                        for (comp in components) {
                            val compCenter = Offset(comp.x * density, comp.y * density)
                            if ((longPressOffset - compCenter).getDistance() <= 50f * comp.scale * density) {
                                onComponentLongPress(comp)
                                break
                            }
                        }
                    },
                    onTap = { tapOffset ->
                        var handled = false
                        // Check Pin taps
                        for (comp in components) {
                            val compCenter = Offset(comp.x * density, comp.y * density)
                            val pins = comp.getPins()
                            for (pin in pins) {
                                val pinPos = compCenter + Offset(pin.relX * 55f * density, pin.relY * 55f * density)
                                if ((tapOffset - pinPos).getDistance() <= 32f * density) {
                                    onPinClicked(comp, pin)
                                    handled = true
                                    break
                                }
                            }
                            if (handled) break

                            // Component body tap
                            if ((tapOffset - compCenter).getDistance() <= 46f * comp.scale * density) {
                                if (comp.type == ComponentType.SWITCH_SPST || comp.type == ComponentType.PUSH_BUTTON) {
                                    onToggleSwitch(comp)
                                }
                                onSelectComponent(comp)
                                onSelectWire(null)
                                handled = true
                                break
                            }
                        }

                        // Check Wire tap selection
                        if (!handled) {
                            var wireHit: CircuitWire? = null
                            for (wire in wires) {
                                val fromComp = components.find { it.id == wire.fromComponentId }
                                val toComp = components.find { it.id == wire.toComponentId }
                                if (fromComp != null && toComp != null) {
                                    val fromPin = fromComp.getPins().find { it.id == wire.fromPinId }
                                    val toPin = toComp.getPins().find { it.id == wire.toPinId }
                                    if (fromPin != null && toPin != null) {
                                        val p1 = Offset(
                                            (fromComp.x + fromPin.relX * 55f) * density,
                                            (fromComp.y + fromPin.relY * 55f) * density
                                        )
                                        val p2 = Offset(
                                            (toComp.x + toPin.relX * 55f) * density,
                                            (toComp.y + toPin.relY * 55f) * density
                                        )
                                        if (isPointNearOrthogonalWire(tapOffset, p1, p2, 18f * density)) {
                                            wireHit = wire
                                            break
                                        }
                                    }
                                }
                            }
                            if (wireHit != null) {
                                onSelectWire(wireHit)
                            } else {
                                onSelectWire(null)
                            }
                        }
                    }
                )
            }
    ) {
        val w = size.width
        val h = size.height
        val gridSizePx = gridStepDp * density

        // 1. Grid Background
        drawRect(color = CircuitNavyDark)
        val numX = (w / gridSizePx).toInt() + 1
        val numY = (h / gridSizePx).toInt() + 1

        if (isGridSnapEnabled) {
            // Draw coordinate grid lines when snap is enabled
            for (i in 0..numX) {
                drawLine(
                    color = CircuitBorder.copy(alpha = 0.22f),
                    start = Offset(i * gridSizePx, 0f),
                    end = Offset(i * gridSizePx, h),
                    strokeWidth = 1f
                )
            }
            for (j in 0..numY) {
                drawLine(
                    color = CircuitBorder.copy(alpha = 0.22f),
                    start = Offset(0f, j * gridSizePx),
                    end = Offset(w, j * gridSizePx),
                    strokeWidth = 1f
                )
            }
        }

        // Draw grid dots
        for (i in 0..numX) {
            for (j in 0..numY) {
                drawCircle(
                    color = if (isGridSnapEnabled) ElectricCyan.copy(alpha = 0.35f) else CircuitBorder.copy(alpha = 0.3f),
                    radius = if (isGridSnapEnabled) 1.5f else 1.2f,
                    center = Offset(i * gridSizePx, j * gridSizePx)
                )
            }
        }

        // 2. Draw Wires with Wave / Particle Flow & Selection highlight
        val isSimActive = (simulationState == SimulationState.RUNNING_SAFE || simulationState == SimulationState.COMPONENT_OVERLOAD)
        for (wire in wires) {
            val fromComp = components.find { it.id == wire.fromComponentId }
            val toComp = components.find { it.id == wire.toComponentId }
            if (fromComp != null && toComp != null) {
                val fromPin = fromComp.getPins().find { it.id == wire.fromPinId }
                val toPin = toComp.getPins().find { it.id == wire.toPinId }
                if (fromPin != null && toPin != null) {
                    val p1 = Offset(
                        (fromComp.x + fromPin.relX * 55f) * density,
                        (fromComp.y + fromPin.relY * 55f) * density
                    )
                    val p2 = Offset(
                        (toComp.x + toPin.relX * 55f) * density,
                        (toComp.y + toPin.relY * 55f) * density
                    )

                    val isSelected = wire.id == selectedWireId
                    drawDynamicWire(
                        p1 = p1,
                        p2 = p2,
                        isActive = isSimActive && (fromComp.isActive || toComp.isActive),
                        isShort = simulationState == SimulationState.SHORT_CIRCUIT,
                        isSelected = isSelected,
                        visualMode = visualMode,
                        phase = flowPhase
                    )
                }
            }
        }

        // Draw Pending Wire Target
        if (pendingWireStart != null) {
            val startComp = components.find { it.id == pendingWireStart.first }
            val startPin = startComp?.getPins()?.find { it.id == pendingWireStart.second }
            if (startComp != null && startPin != null) {
                val p1 = Offset(
                    (startComp.x + startPin.relX * 55f) * density,
                    (startComp.y + startPin.relY * 55f) * density
                )
                drawCircle(
                    color = ElectricCyan.copy(alpha = 0.35f),
                    radius = 16f * density,
                    center = p1
                )
                drawCircle(
                    color = ElectricCyan,
                    radius = 9f * density,
                    center = p1,
                    style = Stroke(width = 3f * density)
                )
            }
        }

        // 3. Draw All Placed Components
        for (comp in components) {
            val isSelected = comp.id == selectedComponentId
            drawProfessionalComponent(
                comp = comp,
                isSelected = isSelected,
                pendingWireStart = pendingWireStart,
                spinAngle = spinAngle,
                language = language,
                textMeasurer = textMeasurer,
                density = density
            )
        }
    }
}

private fun isPointNearOrthogonalWire(pt: Offset, p1: Offset, p2: Offset, threshold: Float): Boolean {
    val midX = (p1.x + p2.x) / 2f
    if (isNearHorizontal(pt, p1.x, midX, p1.y, threshold)) return true
    if (isNearVertical(pt, p1.y, p2.y, midX, threshold)) return true
    if (isNearHorizontal(pt, midX, p2.x, p2.y, threshold)) return true
    return false
}

private fun isNearHorizontal(pt: Offset, x1: Float, x2: Float, y: Float, threshold: Float): Boolean {
    val minX = min(x1, x2) - threshold
    val maxX = max(x1, x2) + threshold
    return pt.x in minX..maxX && abs(pt.y - y) <= threshold
}

private fun isNearVertical(pt: Offset, y1: Float, y2: Float, x: Float, threshold: Float): Boolean {
    val minY = min(y1, y2) - threshold
    val maxY = max(y1, y2) + threshold
    return pt.y in minY..maxY && abs(pt.x - x) <= threshold
}

private fun DrawScope.drawDynamicWire(
    p1: Offset,
    p2: Offset,
    isActive: Boolean,
    isShort: Boolean,
    isSelected: Boolean,
    visualMode: ElectronVisualMode,
    phase: Float
) {
    val midX = (p1.x + p2.x) / 2f
    val wirePath = Path().apply {
        moveTo(p1.x, p1.y)
        lineTo(midX, p1.y)
        lineTo(midX, p2.y)
        lineTo(p2.x, p2.y)
    }

    val baseColor = when {
        isShort -> WireShortColor
        isSelected -> Color(0xFFFBBF24)
        isActive -> WireActiveColor
        else -> WireIdleColor
    }

    if (isActive || isShort || isSelected) {
        drawPath(
            path = wirePath,
            color = baseColor.copy(alpha = if (isSelected) 0.5f else 0.35f),
            style = Stroke(width = if (isSelected) 12f else 9f, cap = StrokeCap.Round)
        )
    }

    drawPath(
        path = wirePath,
        color = baseColor,
        style = Stroke(width = if (isSelected) 5f else 3.5f, cap = StrokeCap.Round)
    )

    if (isActive && visualMode != ElectronVisualMode.OFF) {
        when (visualMode) {
            ElectronVisualMode.PARTICLES -> {
                drawPath(
                    path = wirePath,
                    color = TechWhite,
                    style = Stroke(
                        width = 3.5f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 24f), phase)
                    )
                )
                drawPath(
                    path = wirePath,
                    color = Color(0xFFFDE047),
                    style = Stroke(
                        width = 2.5f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 27f), phase + 10f)
                    )
                )
            }
            ElectronVisualMode.WAVE -> {
                val wavePath = Path()
                val segments = listOf(
                    Pair(p1, Offset(midX, p1.y)),
                    Pair(Offset(midX, p1.y), Offset(midX, p2.y)),
                    Pair(Offset(midX, p2.y), p2)
                )

                val waveLength = 26f
                val amplitude = 5.5f
                for ((start, end) in segments) {
                    val dist = (end - start).getDistance()
                    if (dist > 1f) {
                        val dx = (end.x - start.x) / dist
                        val dy = (end.y - start.y) / dist
                        val normX = -dy
                        val normY = dx

                        val steps = (dist / 2f).toInt()
                        for (s in 0..steps) {
                            val t = s * 2f
                            val px = start.x + dx * t
                            val py = start.y + dy * t
                            val waveOffset = sin((t + phase * 2f) / waveLength * (2 * Math.PI).toFloat()) * amplitude
                            val wx = px + normX * waveOffset
                            val wy = py + normY * waveOffset

                            if (s == 0 && wavePath.isEmpty) wavePath.moveTo(wx, wy)
                            else wavePath.lineTo(wx, wy)
                        }
                    }
                }

                drawPath(
                    path = wavePath,
                    color = ElectricCyan,
                    style = Stroke(width = 2.4f, cap = StrokeCap.Round)
                )
            }
            else -> {}
        }
    }
}

// AUTHENTIC IEEE / IEC ELECTRONICS SCHEMATIC SYMBOL RENDERER
private fun DrawScope.drawProfessionalComponent(
    comp: CircuitComponent,
    isSelected: Boolean,
    pendingWireStart: Pair<String, String>?,
    spinAngle: Float,
    language: AppLanguage,
    textMeasurer: TextMeasurer,
    density: Float
) {
    val center = Offset(comp.x * density, comp.y * density)
    val compRadius = 38f * comp.scale * density

    withTransform({
        rotate(comp.rotationAngle, center)
        scale(comp.scale, comp.scale, center)
    }) {
        if (isSelected) {
            drawCircle(
                color = ElectricCyan.copy(alpha = 0.25f),
                radius = 38f * density + 14f * density,
                center = center
            )
            drawCircle(
                color = ElectricCyan,
                radius = 38f * density + 4f * density,
                center = center,
                style = Stroke(width = 2.5f * density)
            )
        }

        // Component Background Disc
        val bgBrush = Brush.radialGradient(
            colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A)),
            center = center,
            radius = 38f * density
        )
        drawCircle(brush = bgBrush, radius = 38f * density, center = center)
        drawCircle(
            color = if (comp.isBurned) DangerRed else CircuitBorder,
            radius = 38f * density,
            center = center,
            style = Stroke(width = 2f * density)
        )

        // DRAW PROFESSIONAL ELECTRONIC SYMBOLS ACCORDING TO STANDARDS
        when (comp.type) {
            // 1. Resistor (IEEE US Zigzag standard with precise peaks)
            ComponentType.RESISTOR -> {
                val sx = center.x - 24f * density
                val sy = center.y
                val path = Path().apply {
                    moveTo(sx, sy)
                    lineTo(sx + 4f * density, sy)
                    lineTo(sx + 8f * density, sy - 11f * density)
                    lineTo(sx + 16f * density, sy + 11f * density)
                    lineTo(sx + 24f * density, sy - 11f * density)
                    lineTo(sx + 32f * density, sy + 11f * density)
                    lineTo(sx + 40f * density, sy - 11f * density)
                    lineTo(sx + 44f * density, sy)
                    lineTo(sx + 48f * density, sy)
                }
                drawPath(
                    path = path,
                    color = if (comp.isBurned) DangerRed else Color(0xFFFBBF24),
                    style = Stroke(width = 3.2f * density, cap = StrokeCap.Round)
                )
            }

            // 2. Potentiometer (Zigzag with central wiper arrow)
            ComponentType.POTENTIOMETER -> {
                val sx = center.x - 22f * density
                val sy = center.y + 6f * density
                val path = Path().apply {
                    moveTo(sx, sy)
                    lineTo(sx + 6f * density, sy - 8f * density)
                    lineTo(sx + 14f * density, sy + 8f * density)
                    lineTo(sx + 22f * density, sy - 8f * density)
                    lineTo(sx + 30f * density, sy + 8f * density)
                    lineTo(sx + 38f * density, sy - 8f * density)
                    lineTo(sx + 44f * density, sy)
                }
                drawPath(path = path, color = Color(0xFFFBBF24), style = Stroke(width = 2.8f * density, cap = StrokeCap.Round))
                // Wiper arrow from top
                drawLine(color = ElectricCyan, start = center + Offset(0f, -22f * density), end = center + Offset(0f, -4f * density), strokeWidth = 3f * density)
                val arrow = Path().apply {
                    moveTo(center.x - 5f * density, center.y - 8f * density)
                    lineTo(center.x + 5f * density, center.y - 8f * density)
                    lineTo(center.x, center.y - 2f * density)
                    close()
                }
                drawPath(path = arrow, color = ElectricCyan)
            }

            // 3. Capacitor (Parallel plate with dielectric gap & electrolytic curvature)
            ComponentType.CAPACITOR -> {
                val plateH = 22f * density
                val gap = 5.5f * density
                // Positive straight plate
                drawLine(
                    color = Color(0xFFA855F7),
                    start = center + Offset(-gap, -plateH),
                    end = center + Offset(-gap, plateH),
                    strokeWidth = 3.5f * density,
                    cap = StrokeCap.Square
                )
                // Negative curved plate (Electrolytic style)
                drawArc(
                    color = Color(0xFFA855F7),
                    startAngle = -70f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = center + Offset(gap - 6f * density, -plateH),
                    size = Size(14f * density, plateH * 2f),
                    style = Stroke(width = 3.5f * density, cap = StrokeCap.Round)
                )
                drawText(textMeasurer, "+", center + Offset(-gap - 10f * density, -plateH - 4f * density), TextStyle(color = Color(0xFFA855F7), fontSize = 11.sp, fontWeight = FontWeight.Bold))
            }

            // 4. Inductor (Continuous coiled loops)
            ComponentType.INDUCTOR -> {
                val startX = center.x - 22f * density
                val y = center.y
                val loopW = 11f * density
                for (k in 0..3) {
                    drawArc(
                        color = Color(0xFF38BDF8),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(startX + k * loopW, y - 8f * density),
                        size = Size(loopW, 16f * density),
                        style = Stroke(width = 3f * density, cap = StrokeCap.Round)
                    )
                }
                // Ferrite core lines above
                drawLine(Color(0xFF64748B), center + Offset(-20f * density, -12f * density), center + Offset(20f * density, -12f * density), 2f * density)
                drawLine(Color(0xFF64748B), center + Offset(-20f * density, -15f * density), center + Offset(20f * density, -15f * density), 2f * density)
            }

            // 5. Diode (1N4007 standard rectifier)
            ComponentType.DIODE -> {
                val tri = Path().apply {
                    moveTo(center.x - 12f * density, center.y - 12f * density)
                    lineTo(center.x - 12f * density, center.y + 12f * density)
                    lineTo(center.x + 8f * density, center.y)
                    close()
                }
                drawPath(path = tri, color = Color(0xFF38BDF8))
                drawLine(color = Color(0xFF38BDF8), start = center + Offset(8f * density, -14f * density), end = center + Offset(8f * density, 14f * density), strokeWidth = 3.5f * density)
            }

            // 6. Zener Diode (with Z-wings)
            ComponentType.ZENER_DIODE -> {
                val tri = Path().apply {
                    moveTo(center.x - 12f * density, center.y - 12f * density)
                    lineTo(center.x - 12f * density, center.y + 12f * density)
                    lineTo(center.x + 8f * density, center.y)
                    close()
                }
                drawPath(path = tri, color = Color(0xFFF97316))
                // Zener bar with top and bottom wings
                drawLine(Color(0xFFF97316), center + Offset(8f * density, -14f * density), center + Offset(8f * density, 14f * density), 3.5f * density)
                drawLine(Color(0xFFF97316), center + Offset(8f * density, -14f * density), center + Offset(3f * density, -14f * density), 3f * density)
                drawLine(Color(0xFFF97316), center + Offset(8f * density, 14f * density), center + Offset(13f * density, 14f * density), 3f * density)
            }

            // 7. LED (Diode + radiating light arrows + bloom)
            ComponentType.LED -> {
                val ledColor = when (comp.ledColor) {
                    "GREEN" -> CircuitEmerald
                    "BLUE" -> Color(0xFF38BDF8)
                    "AMBER" -> Color(0xFFF59E0B)
                    else -> DangerRed
                }
                if (comp.isBurned) {
                    drawLine(DangerRed, center + Offset(-14f * density, -14f * density), center + Offset(14f * density, 14f * density), 4f * density)
                    drawLine(DangerRed, center + Offset(14f * density, -14f * density), center + Offset(-14f * density, 14f * density), 4f * density)
                } else {
                    if (comp.isActive && comp.brightness > 0f) {
                        drawCircle(ledColor.copy(alpha = 0.55f * comp.brightness), 38f * density * (1.1f + 0.35f * comp.brightness), center)
                    }
                    val tri = Path().apply {
                        moveTo(center.x - 12f * density, center.y - 12f * density)
                        lineTo(center.x - 12f * density, center.y + 12f * density)
                        lineTo(center.x + 8f * density, center.y)
                        close()
                    }
                    drawPath(path = tri, color = ledColor)
                    drawLine(ledColor, center + Offset(8f * density, -14f * density), center + Offset(8f * density, 14f * density), 3.5f * density)
                    // Two 45-deg radiating arrows
                    drawLine(ledColor, center + Offset(2f * density, -14f * density), center + Offset(12f * density, -24f * density), 2.2f * density)
                    drawLine(ledColor, center + Offset(8f * density, -8f * density), center + Offset(18f * density, -18f * density), 2.2f * density)
                    // Polarity markings
                    drawText(textMeasurer, "+", center + Offset(-20f * density, -18f * density), TextStyle(color = DangerRed, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold))
                    drawText(textMeasurer, "-", center + Offset(11f * density, -18f * density), TextStyle(color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold))
                }
            }

            // 8. NPN BJT Transistor (Base vertical bar, collector, emitter with OUTWARD arrow)
            ComponentType.TRANSISTOR_NPN -> {
                // Outer transistor circle
                drawCircle(Color(0xFF64748B), 26f * density, center, style = Stroke(width = 2f * density))
                // Vertical Base bar
                drawLine(TechWhite, center + Offset(-10f * density, -16f * density), center + Offset(-10f * density, 16f * density), 4f * density, StrokeCap.Round)
                // Collector lead (top right)
                drawLine(TechWhite, center + Offset(-10f * density, -6f * density), center + Offset(16f * density, -18f * density), 2.8f * density)
                // Emitter lead (bottom right)
                drawLine(TechWhite, center + Offset(-10f * density, 6f * density), center + Offset(16f * density, 18f * density), 2.8f * density)
                // Outward arrow on emitter
                val arrow = Path().apply {
                    moveTo(center.x + 16f * density, center.y + 18f * density)
                    lineTo(center.x + 8f * density, center.y + 16f * density)
                    lineTo(center.x + 13f * density, center.y + 10f * density)
                    close()
                }
                drawPath(path = arrow, color = ElectricCyan)
            }

            // 9. PNP BJT Transistor (Emitter with INWARD arrow toward base)
            ComponentType.TRANSISTOR_PNP -> {
                drawCircle(Color(0xFF64748B), 26f * density, center, style = Stroke(width = 2f * density))
                drawLine(TechWhite, center + Offset(-10f * density, -16f * density), center + Offset(-10f * density, 16f * density), 4f * density, StrokeCap.Round)
                drawLine(TechWhite, center + Offset(-10f * density, -6f * density), center + Offset(16f * density, -18f * density), 2.8f * density)
                drawLine(TechWhite, center + Offset(-10f * density, 6f * density), center + Offset(16f * density, 18f * density), 2.8f * density)
                // Inward arrow toward base
                val arrow = Path().apply {
                    moveTo(center.x - 2f * density, center.y + 10f * density)
                    lineTo(center.x + 6f * density, center.y + 12f * density)
                    lineTo(center.x + 1f * density, center.y + 18f * density)
                    close()
                }
                drawPath(path = arrow, color = Color(0xFFEC4899))
            }

            // 10. MOSFET (N-channel with isolated gate & channel segments)
            ComponentType.MOSFET_N -> {
                drawCircle(Color(0xFF64748B), 26f * density, center, style = Stroke(width = 2f * density))
                // Gate bar
                drawLine(ElectricCyan, center + Offset(-14f * density, -16f * density), center + Offset(-14f * density, 16f * density), 3.5f * density)
                // 3 Channel bars
                val cx = center.x - 6f * density
                drawLine(TechWhite, Offset(cx, center.y - 16f * density), Offset(cx, center.y - 8f * density), 3f * density)
                drawLine(TechWhite, Offset(cx, center.y - 4f * density), Offset(cx, center.y + 4f * density), 3f * density)
                drawLine(TechWhite, Offset(cx, center.y + 8f * density), Offset(cx, center.y + 16f * density), 3f * density)
                // Drain and Source leads
                drawLine(TechWhite, Offset(cx, center.y - 12f * density), center + Offset(16f * density, -16f * density), 2.5f * density)
                drawLine(TechWhite, Offset(cx, center.y + 12f * density), center + Offset(16f * density, 16f * density), 2.5f * density)
            }

            // 11. DC Battery (Dual Cell IEEE Standard with clear Red + / Blue - Polarity)
            ComponentType.DC_BATTERY -> {
                val gap = 6f * density
                // Cell 1: Long thin (+), short thick (-)
                drawLine(DangerRed, center + Offset(-20f * density, -gap * 2), center + Offset(20f * density, -gap * 2), 3.5f * density)
                drawLine(Color(0xFF38BDF8), center + Offset(-11f * density, -gap), center + Offset(11f * density, -gap), 5.5f * density)
                // Cell 2: Long thin (+), short thick (-)
                drawLine(DangerRed, center + Offset(-20f * density, gap), center + Offset(20f * density, gap), 3.5f * density)
                drawLine(Color(0xFF38BDF8), center + Offset(-11f * density, gap * 2), center + Offset(11f * density, gap * 2), 5.5f * density)
                // Red + and Blue - badges
                drawText(textMeasurer, "+", center + Offset(-4f * density, -28f * density), TextStyle(color = DangerRed, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold))
                drawText(textMeasurer, "-", center + Offset(-4f * density, 18f * density), TextStyle(color = Color(0xFF38BDF8), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold))
            }

            // 12. AC Source (Circle with Sine Wave inside)
            ComponentType.AC_SOURCE -> {
                drawCircle(ElectricCyan, 20f * density, center, style = Stroke(width = 3f * density))
                val wave = Path().apply {
                    val sx = center.x - 12f * density
                    moveTo(sx, center.y)
                    cubicTo(sx + 6f * density, center.y - 10f * density, sx + 6f * density, center.y - 10f * density, center.x, center.y)
                    cubicTo(center.x + 6f * density, center.y + 10f * density, center.x + 6f * density, center.y + 10f * density, center.x + 12f * density, center.y)
                }
                drawPath(wave, ElectricCyan, style = Stroke(width = 3f * density, cap = StrokeCap.Round))
            }

            // 13. Ground (Descending horizontal steps)
            ComponentType.GROUND -> {
                drawLine(ElectricCyan, center + Offset(0f, -14f * density), center + Offset(0f, 0f), 3f * density)
                drawLine(ElectricCyan, center + Offset(-18f * density, 0f), center + Offset(18f * density, 0f), 3.5f * density)
                drawLine(ElectricCyan, center + Offset(-11f * density, 6f * density), center + Offset(11f * density, 6f * density), 3.5f * density)
                drawLine(ElectricCyan, center + Offset(-5f * density, 12f * density), center + Offset(5f * density, 12f * density), 3.5f * density)
            }

            // 14. Switch (SPST knife switch with contact terminals)
            ComponentType.SWITCH_SPST, ComponentType.PUSH_BUTTON -> {
                val p1 = center + Offset(-18f * density, 0f)
                val p2 = center + Offset(18f * density, 0f)
                drawCircle(TechWhite, 4f * density, p1)
                drawCircle(TechWhite, 4f * density, p2)
                val leverEnd = if (comp.isSwitchClosed) p2 else center + Offset(14f * density, -20f * density)
                drawLine(if (comp.isSwitchClosed) CircuitEmerald else TechTextMuted, p1, leverEnd, 4f * density, StrokeCap.Round)
            }

            // 15. Fuse (IEC capsule with S-curve link)
            ComponentType.FUSE -> {
                drawRoundRect(Color(0xFF64748B), center + Offset(-20f * density, -8f * density), Size(40f * density, 16f * density), androidx.compose.ui.geometry.CornerRadius(4f * density), Stroke(width = 2.5f * density))
                if (!comp.isBurned) {
                    val sPath = Path().apply {
                        moveTo(center.x - 18f * density, center.y)
                        cubicTo(center.x - 8f * density, center.y - 6f * density, center.x + 8f * density, center.y + 6f * density, center.x + 18f * density, center.y)
                    }
                    drawPath(sPath, Color(0xFFFBBF24), style = Stroke(width = 2.5f * density))
                } else {
                    drawLine(DangerRed, center + Offset(-8f * density, -5f * density), center + Offset(8f * density, 5f * density), 2.5f * density)
                }
            }

            // 16. NE555 Timer IC (DIP Package with Notch & Pins)
            ComponentType.TIMER_555 -> {
                drawRoundRect(Color(0xFF334155), center + Offset(-24f * density, -24f * density), Size(48f * density, 48f * density), androidx.compose.ui.geometry.CornerRadius(6f * density), Stroke(width = 2.5f * density))
                drawArc(
                    color = TechTextMuted,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = center + Offset(-6f * density, -24f * density),
                    size = Size(12f * density, 12f * density),
                    style = Stroke(width = 2f * density)
                )
                drawText(textMeasurer, "555", center + Offset(-12f * density, -8f * density), TextStyle(color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold))
            }

            // 17. Operational Amplifier (LM741 Triangle with +/- inputs)
            ComponentType.CUSTOM_COMPONENT -> {
                val tri = Path().apply {
                    moveTo(center.x - 20f * density, center.y - 22f * density)
                    lineTo(center.x - 20f * density, center.y + 22f * density)
                    lineTo(center.x + 22f * density, center.y)
                    close()
                }
                drawPath(tri, Color(0xFF1E293B))
                drawPath(tri, ElectricCyan, style = Stroke(width = 2.8f * density))
                drawText(textMeasurer, "-", center + Offset(-16f * density, -18f * density), TextStyle(color = TechWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold))
                drawText(textMeasurer, "+", center + Offset(-16f * density, 4f * density), TextStyle(color = TechWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold))
            }

            // 18. Logic AND Gate (D-shape)
            ComponentType.LOGIC_AND -> {
                val p = Path().apply {
                    moveTo(center.x - 18f * density, center.y - 18f * density)
                    lineTo(center.x, center.y - 18f * density)
                    arcTo(rect = androidx.compose.ui.geometry.Rect(center.x - 18f * density, center.y - 18f * density, center.x + 18f * density, center.y + 18f * density), startAngleDegrees = -90f, sweepAngleDegrees = 180f, forceMoveTo = false)
                    lineTo(center.x - 18f * density, center.y + 18f * density)
                    close()
                }
                drawPath(p, ElectricCyan, style = Stroke(width = 2.8f * density))
            }

            // 19. DC Motor
            ComponentType.DC_MOTOR -> {
                drawCircle(Color(0xFF64748B), 20f * density, center, style = Stroke(width = 3f * density))
                val angleRad = Math.toRadians(if (comp.isActive) spinAngle.toDouble() else 0.0)
                val dx = (cos(angleRad) * 14.0 * density).toFloat()
                val dy = (sin(angleRad) * 14.0 * density).toFloat()
                drawLine(if (comp.isActive) ElectricCyan else TechTextMuted, center - Offset(dx, dy), center + Offset(dx, dy), 3.5f * density, StrokeCap.Round)
                drawText(textMeasurer, "M", center + Offset(-6f * density, -9f * density), TextStyle(color = TechWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold))
            }

            // 20. Buzzer / Beeper
            ComponentType.BUZZER -> {
                val cone = Path().apply {
                    moveTo(center.x - 14f * density, center.y - 10f * density)
                    lineTo(center.x, center.y - 16f * density)
                    lineTo(center.x, center.y + 16f * density)
                    lineTo(center.x - 14f * density, center.y + 10f * density)
                    close()
                }
                drawPath(cone, Color(0xFFA855F7))
            }

            else -> {
                drawText(textMeasurer, comp.type.iconSymbol, center + Offset(-9f * density, -12f * density), TextStyle(color = TechWhite, fontSize = 18.sp))
            }
        }
    }

    // Dynamic Spec Label (Respecting Language)
    val displayVal = when (comp.type) {
        ComponentType.DC_BATTERY, ComponentType.SOLAR_PANEL, ComponentType.AC_SOURCE -> "${comp.value.toInt()}V"
        ComponentType.RESISTOR, ComponentType.LDR -> "${comp.value.toInt()}Ω"
        ComponentType.CAPACITOR -> "${comp.value.toInt()}µF"
        ComponentType.LED -> if (comp.isActive) "${String.format("%.1f", comp.currentMa)}mA" else if (language == AppLanguage.BANGLA) comp.label else comp.type.nameEn
        ComponentType.DC_MOTOR -> if (comp.isActive) "${(comp.brightness * 1200).toInt()} RPM" else "Motor"
        ComponentType.SWITCH_SPST -> if (comp.isSwitchClosed) "ON" else "OFF"
        ComponentType.FUSE -> if (comp.isBurned) "BLOWN" else "${comp.value}A"
        else -> if (language == AppLanguage.BANGLA) comp.label else comp.type.nameEn
    }

    drawText(
        textMeasurer = textMeasurer,
        text = displayVal,
        topLeft = center + Offset(-compRadius, compRadius + 6f * density),
        style = TextStyle(color = if (comp.isActive) ElectricCyan else TechWhite, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
    )

    // Interactive Terminals (Pins) with Color-Coded Polarity Badges
    val pins = comp.getPins()
    for (pin in pins) {
        val pinPos = center + Offset(pin.relX * 55f * density, pin.relY * 55f * density)
        val isWireCandidate = pendingWireStart != null && pendingWireStart.first == comp.id && pendingWireStart.second == pin.id

        // Polarity color code:
        // Red for "+", "A", "VCC", "pos", "live"
        // Blue for "-", "K", "neg", "neutral"
        // Green for "GND"
        val (pinColor, badgeText) = when (pin.polarity) {
            "+", "A", "VCC", "pos" -> Pair(DangerRed, "+")
            "-", "K", "neg" -> Pair(Color(0xFF38BDF8), "-")
            "GND" -> Pair(CircuitEmerald, "⏚")
            "L" -> Pair(DangerRed, "L")
            "N" -> Pair(Color(0xFF38BDF8), "N")
            "B" -> Pair(ElectricCyan, "B")
            "C" -> Pair(Color(0xFFF59E0B), "C")
            "E" -> Pair(Color(0xFFEC4899), "E")
            else -> Pair(ElectricCyan, "")
        }

        drawLine(
            color = if (isWireCandidate) ElectricCyan else pinColor.copy(alpha = 0.7f),
            start = center + Offset(pin.relX * 36f * density, pin.relY * 36f * density),
            end = pinPos,
            strokeWidth = 2.5f * density
        )
        // Outer halo
        drawCircle(
            color = if (isWireCandidate) ElectricCyan else pinColor.copy(alpha = 0.25f),
            radius = 8.5f * density,
            center = pinPos
        )
        // Outer pin ring
        drawCircle(
            color = if (isWireCandidate) TechWhite else pinColor,
            radius = 5.5f * density,
            center = pinPos
        )
        // Pin core center
        drawCircle(
            color = if (isWireCandidate) ElectricCyan else CircuitNavyDark,
            radius = 2.5f * density,
            center = pinPos
        )

        // Draw polarity badge character next to the terminal
        if (badgeText.isNotEmpty()) {
            val badgeOffset = Offset(
                if (pin.relX >= 0) 7f * density else -14f * density,
                if (pin.relY > 0) 3f * density else -13f * density
            )
            drawText(
                textMeasurer = textMeasurer,
                text = badgeText,
                topLeft = pinPos + badgeOffset,
                style = TextStyle(
                    color = pinColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }
    }
}
