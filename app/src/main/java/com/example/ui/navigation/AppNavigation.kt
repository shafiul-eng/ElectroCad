package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ShowChart
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.OscilloscopeScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.SimulatorScreen
import com.example.ui.screens.TutorialScreen
import com.example.ui.theme.CircuitNavyDark
import com.example.ui.theme.CircuitNavySurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.TechTextMuted
import com.example.ui.theme.TechWhite

enum class AppTab(val titleBn: String, val titleEn: String, val icon: ImageVector, val tag: String) {
    SIMULATOR("সিমুলেটর", "Simulator", Icons.Default.ElectricBolt, "tab_simulator"),
    OSCILLOSCOPE("অসিলোস্কোপ", "Oscilloscope", Icons.Default.ShowChart, "tab_scope"),
    CALCULATOR("ক্যালকুলেটর", "Calculator", Icons.Default.Calculate, "tab_calculator"),
    TUTORIALS("টিউটোরিয়াল", "Tutorials", Icons.Default.MenuBook, "tab_tutorials"),
    QUIZ("কুইজ ল্যাব", "Quiz Lab", Icons.Default.Quiz, "tab_quiz")
}

@Composable
fun AppNavigation() {
    var selectedTab by remember { mutableStateOf(AppTab.SIMULATOR) }
    var presetToLoadInSimulator by remember { mutableStateOf<String?>(null) }
    var currentLanguage by remember { mutableStateOf(com.example.model.AppLanguage.BANGLA) }

    Scaffold(
        containerColor = CircuitNavyDark,
        bottomBar = {
            NavigationBar(
                containerColor = CircuitNavySurface,
                contentColor = TechWhite
            ) {
                AppTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    val label = if (currentLanguage == com.example.model.AppLanguage.BANGLA) tab.titleBn else tab.titleEn
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = label
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CircuitNavyDark,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan,
                            unselectedIconColor = TechTextMuted,
                            unselectedTextColor = TechTextMuted
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppTab.SIMULATOR -> {
                    SimulatorScreen(
                        presetToLoad = presetToLoadInSimulator,
                        onNavigateToTutorials = { selectedTab = AppTab.TUTORIALS },
                        language = currentLanguage,
                        onToggleLanguage = {
                            currentLanguage = if (currentLanguage == com.example.model.AppLanguage.BANGLA) com.example.model.AppLanguage.ENGLISH else com.example.model.AppLanguage.BANGLA
                        }
                    )
                }
                AppTab.OSCILLOSCOPE -> {
                    OscilloscopeScreen(
                        language = currentLanguage
                    )
                }
                AppTab.CALCULATOR -> {
                    CalculatorScreen()
                }
                AppTab.TUTORIALS -> {
                    TutorialScreen(
                        onLoadPresetInSimulator = { presetId ->
                            presetToLoadInSimulator = presetId
                            selectedTab = AppTab.SIMULATOR
                        }
                    )
                }
                AppTab.QUIZ -> {
                    QuizScreen()
                }
            }
        }
    }
}
