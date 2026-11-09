package com.example.engine

import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import kotlin.math.roundToInt

data class ResistorBandColor(
    val nameBn: String,
    val nameEn: String,
    val digit: Int?,
    val multiplier: Double?,
    val tolerance: Double?,
    val color: Color
)

object CalculatorEngine {

    val BandColors = listOf(
        ResistorBandColor("কালো (Black)", "Black", 0, 1.0, null, Color(0xFF1E293B)),
        ResistorBandColor("বাদামী (Brown)", "Brown", 1, 10.0, 1.0, Color(0xFF8D5B4C)),
        ResistorBandColor("লাল (Red)", "Red", 2, 100.0, 2.0, Color(0xFFEF4444)),
        ResistorBandColor("কমলা (Orange)", "Orange", 3, 1000.0, null, Color(0xFFF97316)),
        ResistorBandColor("হলুদ (Yellow)", "Yellow", 4, 10000.0, null, Color(0xFFFACC15)),
        ResistorBandColor("সবুজ (Green)", "Green", 5, 100000.0, 0.5, Color(0xFF22C55E)),
        ResistorBandColor("নীল (Blue)", "Blue", 6, 1000000.0, 0.25, Color(0xFF3B82F6)),
        ResistorBandColor("বেগুনি (Violet)", "Violet", 7, 10000000.0, 0.1, Color(0xFFA855F7)),
        ResistorBandColor("ধূসর (Gray)", "Gray", 8, null, 0.05, Color(0xFF94A3B8)),
        ResistorBandColor("সাদা (White)", "White", 9, null, null, Color(0xFFFFFFFF)),
        ResistorBandColor("সোনালী (Gold)", "Gold", null, 0.1, 5.0, Color(0xFFD4AF37)),
        ResistorBandColor("রূপালী (Silver)", "Silver", null, 0.01, 10.0, Color(0xFFC0C0C0))
    )

    fun calculate4Band(
        band1Idx: Int,
        band2Idx: Int,
        multiplierIdx: Int,
        toleranceIdx: Int
    ): Pair<String, String> {
        val d1 = BandColors[band1Idx].digit ?: 0
        val d2 = BandColors[band2Idx].digit ?: 0
        val mult = BandColors[multiplierIdx].multiplier ?: 1.0
        val tol = BandColors[toleranceIdx].tolerance ?: 5.0

        val ohms = (d1 * 10 + d2) * mult
        val formattedResistance = formatOhms(ohms)
        val tolText = "±$tol%"
        return Pair(formattedResistance, tolText)
    }

    fun formatOhms(ohms: Double): String {
        return when {
            ohms >= 1_000_000 -> String.format("%.2f MΩ", ohms / 1_000_000)
            ohms >= 1_000 -> String.format("%.2f kΩ", ohms / 1_000)
            else -> String.format("%.1f Ω", ohms)
        }
    }

    fun calculateLedResistor(
        supplyVoltage: Float,
        ledForwardVoltage: Float,
        targetCurrentMa: Float
    ): Triple<Float, Float, Float> {
        val currentAmps = (targetCurrentMa / 1000f).coerceAtLeast(0.001f)
        val vDiff = (supplyVoltage - ledForwardVoltage).coerceAtLeast(0f)
        val calculatedResistance = vDiff / currentAmps
        val powerWatts = currentAmps * currentAmps * calculatedResistance
        val nearestStandard = findNearestE12(calculatedResistance)
        return Triple(calculatedResistance, nearestStandard, powerWatts)
    }

    private val e12Bases = listOf(10f, 12f, 15f, 18f, 22f, 27f, 33f, 39f, 47f, 56f, 68f, 82f)

    private fun findNearestE12(value: Float): Float {
        if (value <= 0) return 10f
        var power = 0
        var v = value
        while (v >= 100f) {
            v /= 10f
            power++
        }
        while (v < 10f) {
            v *= 10f
            power--
        }
        var nearestBase = e12Bases[0]
        var minDiff = Float.MAX_VALUE
        for (b in e12Bases) {
            val diff = kotlin.math.abs(b - v)
            if (diff < minDiff) {
                minDiff = diff
                nearestBase = b
            }
        }
        return (nearestBase * 10.0.pow(power.toDouble())).toFloat()
    }
}
