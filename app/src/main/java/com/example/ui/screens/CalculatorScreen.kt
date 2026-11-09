package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.CalculatorEngine
import com.example.ui.theme.CircuitBorder
import com.example.ui.theme.CircuitEmerald
import com.example.ui.theme.CircuitNavyDark
import com.example.ui.theme.CircuitNavySurface
import com.example.ui.theme.CircuitNavySurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechTextSecondary
import com.example.ui.theme.TechWhite

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("কালার কোড", "ওহমের সূত্র", "এলইডি রোধ")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CircuitNavyDark)
    ) {
        // Header
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFBBF24).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = "Calculators",
                    tint = Color(0xFFFBBF24)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "ইলেকট্রনিক্স ক্যালকুলেটর স্যুট",
                    color = TechWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "দ্রুত ও নিখুঁত সার্কিট প্যারামিটার হিসাব",
                    color = TechTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CircuitNavySurface,
            contentColor = ElectricCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = ElectricCyan
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) ElectricCyan else TechTextSecondary,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.testTag("calculator_tab_$index")
                )
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> ResistorColorTab()
                1 -> OhmsLawTab()
                2 -> LedResistorTab()
            }
        }
    }
}

@Composable
private fun ResistorColorTab() {
    var b1Idx by remember { mutableIntStateOf(1) } // Brown (1)
    var b2Idx by remember { mutableIntStateOf(0) } // Black (0)
    var multIdx by remember { mutableIntStateOf(2) } // Red (x100) -> 1kΩ
    var tolIdx by remember { mutableIntStateOf(10) } // Gold (±5%)

    val (resistanceStr, toleranceStr) = CalculatorEngine.calculate4Band(b1Idx, b2Idx, multIdx, tolIdx)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Visual Resistor Graphic Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CircuitNavySurface),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(CircuitBorder)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ফলাফল: $resistanceStr $toleranceStr",
                    color = Color(0xFFFBBF24),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Painted Resistor Component Representation
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Left Lead
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(4.dp)
                            .background(Color(0xFFCBD5E1))
                    )
                    // Resistor Ceramic Body
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFD7BA89))
                            .border(1.dp, Color(0xFFB59360), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ColorBandStripe(color = CalculatorEngine.BandColors[b1Idx].color)
                            ColorBandStripe(color = CalculatorEngine.BandColors[b2Idx].color)
                            ColorBandStripe(color = CalculatorEngine.BandColors[multIdx].color)
                            Spacer(modifier = Modifier.width(8.dp))
                            ColorBandStripe(color = CalculatorEngine.BandColors[tolIdx].color)
                        }
                    }
                    // Right Lead
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(4.dp)
                            .background(Color(0xFFCBD5E1))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Band 1 Selection
        BandPickerRow(
            title = "ব্যান্ড ১ (প্রথম অংক)",
            selectedIndex = b1Idx,
            validColors = CalculatorEngine.BandColors.filter { it.digit != null && it.digit > 0 },
            onSelect = { idx -> b1Idx = idx }
        )

        // Band 2 Selection
        BandPickerRow(
            title = "ব্যান্ড ২ (দ্বিতীয় অংক)",
            selectedIndex = b2Idx,
            validColors = CalculatorEngine.BandColors.filter { it.digit != null },
            onSelect = { idx -> b2Idx = idx }
        )

        // Multiplier Selection
        BandPickerRow(
            title = "ব্যান্ড ৩ (গুণক / Multiplier)",
            selectedIndex = multIdx,
            validColors = CalculatorEngine.BandColors.filter { it.multiplier != null },
            onSelect = { idx -> multIdx = idx }
        )

        // Tolerance Selection
        BandPickerRow(
            title = "ব্যান্ড ৪ (সহনশীলতা / Tolerance)",
            selectedIndex = tolIdx,
            validColors = CalculatorEngine.BandColors.filter { it.tolerance != null },
            onSelect = { idx -> tolIdx = idx }
        )
    }
}

@Composable
private fun ColorBandStripe(color: Color) {
    Box(
        modifier = Modifier
            .width(12.dp)
            .height(48.dp)
            .background(color)
    )
}

@Composable
private fun BandPickerRow(
    title: String,
    selectedIndex: Int,
    validColors: List<com.example.engine.ResistorBandColor>,
    onSelect: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = title,
            color = TechWhite,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(CalculatorEngine.BandColors) { globalIdx, band ->
                if (band in validColors) {
                    val isSelected = globalIdx == selectedIndex
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(band.color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) ElectricCyan else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onSelect(globalIdx) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ElectricCyan)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OhmsLawTab() {
    var voltageInput by remember { mutableStateOf("12") }
    var resistanceInput by remember { mutableStateOf("100") }

    val v = voltageInput.toFloatOrNull() ?: 0f
    val r = resistanceInput.toFloatOrNull() ?: 1f
    val i = if (r > 0) v / r else 0f
    val iMa = i * 1000f
    val pWatts = v * i
    val pMilliwatts = pWatts * 1000f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CircuitNavySurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ওহমের সূত্র ক্যালকুলেশন",
                    color = ElectricCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = voltageInput,
                    onValueChange = { voltageInput = it },
                    label = { Text("ভোল্টেজ (Voltage - V)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TechWhite,
                        unfocusedTextColor = TechWhite,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = CircuitBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("voltage_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = resistanceInput,
                    onValueChange = { resistanceInput = it },
                    label = { Text("রোধ (Resistance - Ω)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TechWhite,
                        unfocusedTextColor = TechWhite,
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = CircuitBorder
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("resistance_input")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CircuitNavySurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "হিসাবকৃত ফলাফল:",
                    color = TechWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "তড়িৎ প্রবাহ (Current I):", color = TechTextSecondary, fontSize = 13.sp)
                    Text(text = "${String.format("%.3f", i)} A (${String.format("%.1f", iMa)} mA)", color = ElectricCyan, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "বৈদ্যুতিক ক্ষমতা (Power P):", color = TechTextSecondary, fontSize = 13.sp)
                    Text(text = "${String.format("%.2f", pWatts)} W (${String.format("%.1f", pMilliwatts)} mW)", color = CircuitEmerald, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "সূত্র: I = V / R = $v / $r = ${String.format("%.3f", i)}A\nP = V × I = $v × ${String.format("%.3f", i)} = ${String.format("%.2f", pWatts)}W",
                    color = TechTextMuted,
                    fontSize = 11.5.sp
                )
            }
        }
    }
}

@Composable
private fun LedResistorTab() {
    var supplyV by remember { mutableStateOf(9f) }
    var ledVf by remember { mutableStateOf(2.0f) }
    var currentMa by remember { mutableStateOf(20f) }

    val (exactR, nearestE12, powerW) = CalculatorEngine.calculateLedResistor(supplyV, ledVf, currentMa)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CircuitNavySurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "এলইডি সিরিজ রেজিস্টর ক্যালকুলেটর",
                    color = ElectricCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "সাপ্লাই ভোল্টেজ: ${supplyV.toInt()} V", color = TechWhite, fontSize = 13.sp)
                Slider(
                    value = supplyV,
                    onValueChange = { supplyV = it },
                    valueRange = 3f..24f,
                    colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "এলইডি ফরওয়ার্ড ড্রপ (Vf): ${String.format("%.1f", ledVf)} V", color = TechWhite, fontSize = 13.sp)
                Slider(
                    value = ledVf,
                    onValueChange = { ledVf = it },
                    valueRange = 1.8f..3.4f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFFBBF24), activeTrackColor = Color(0xFFFBBF24))
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "টার্গেট কারেন্ট (mA): ${currentMa.toInt()} mA", color = TechWhite, fontSize = 13.sp)
                Slider(
                    value = currentMa,
                    onValueChange = { currentMa = it },
                    valueRange = 5f..30f,
                    colors = SliderDefaults.colors(thumbColor = CircuitEmerald, activeTrackColor = CircuitEmerald)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CircuitNavySurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "সুপারিশকৃত রেজিস্টর রেটিং:",
                    color = TechWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "হিসাবকৃত সঠিক মান:", color = TechTextSecondary, fontSize = 13.sp)
                    Text(text = "${String.format("%.1f", exactR)} Ω", color = TechWhite, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "নিকটতম আদর্শ (E12 মান):", color = TechTextSecondary, fontSize = 13.sp)
                    Text(text = "${nearestE12.toInt()} Ω", color = Color(0xFFFBBF24), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "সর্বনিম্ন ওয়াটেজ রেটিং:", color = TechTextSecondary, fontSize = 13.sp)
                    Text(
                        text = if (powerW < 0.25f) "1/4 W (0.25W)" else "${String.format("%.2f", powerW * 1.5f)} W",
                        color = CircuitEmerald,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
