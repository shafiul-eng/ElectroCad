package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CircuitPreset
import com.example.data.DefaultCircuits
import com.example.data.OnlineComponentDatabase
import com.example.data.OnlineComponentItem
import com.example.data.local.AppDatabase
import com.example.data.local.CircuitDesignEntity
import com.example.engine.CircuitEngine
import com.example.engine.CircuitHistoryManager
import com.example.model.AppLanguage
import com.example.model.CircuitComponent
import com.example.model.CircuitWire
import com.example.model.ComponentCategory
import com.example.model.ComponentType
import com.example.model.SimulationResult
import com.example.model.SimulationState
import com.example.model.StringsHelper
import com.example.repository.CircuitRepository
import com.example.ui.components.CircuitCanvas
import com.example.ui.components.ElectronVisualMode
import com.example.ui.theme.AmberWarning
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
import com.example.util.CircuitImageExporter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulatorScreen(
    presetToLoad: String? = null,
    onNavigateToTutorials: () -> Unit,
    language: AppLanguage = AppLanguage.BANGLA,
    onToggleLanguage: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Room Database Repository
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { CircuitRepository(database.circuitDesignDao()) }
    val savedDesigns by repository.allDesigns.collectAsState(initial = emptyList())

    // Undo/Redo History Manager
    val historyManager = remember { CircuitHistoryManager() }

    val components = remember { mutableStateListOf<CircuitComponent>() }
    val wires = remember { mutableStateListOf<CircuitWire>() }

    var currentLanguage by remember(language) { mutableStateOf(language) }
    var simSpeed by remember { mutableFloatStateOf(1.0f) }
    var isGridSnapEnabled by remember { mutableStateOf(true) }
    var showSpeedModal by remember { mutableStateOf(false) }

    var isPaused by remember { mutableStateOf(false) }
    var selectedComponentId by remember { mutableStateOf<String?>(null) }
    var selectedWireId by remember { mutableStateOf<String?>(null) }
    var pendingWireStart by remember { mutableStateOf<Pair<String, String>?>(null) }
    var visualMode by remember { mutableStateOf(ElectronVisualMode.PARTICLES) }

    // Dialog & Sheet States
    var showEditDialog by remember { mutableStateOf(false) }
    var showPresetSheet by remember { mutableStateOf(false) }
    var showTooltipComponent by remember { mutableStateOf<CircuitComponent?>(null) }
    var showSaveDesignDialog by remember { mutableStateOf(false) }
    var showSavedDesignsDialog by remember { mutableStateOf(false) }
    var newDesignName by remember { mutableStateOf("") }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var simulationResult by remember {
        mutableStateOf(
            SimulationResult(
                state = SimulationState.OPEN_CIRCUIT,
                totalCurrentMa = 0f,
                totalPowerMw = 0f,
                equivalentResistanceOhm = 0f,
                messageBn = "সার্কিট শুরু করতে সাইড ড্রয়ার থেকে উপাদান যুক্ত করুন।",
                messageEn = "Add components from the side drawer to begin simulation."
            )
        )
    }

    // Helper to push history
    fun recordHistory() {
        historyManager.pushState(components, wires)
    }

    fun runEngine() {
        simulationResult = CircuitEngine.solve(components, wires, isPaused)
    }

    LaunchedEffect(presetToLoad) {
        val presets = DefaultCircuits.getPresets()
        val target = if (!presetToLoad.isNullOrEmpty()) {
            presets.find { it.id == presetToLoad } ?: presets.first()
        } else if (components.isEmpty()) {
            presets.first()
        } else null

        if (target != null) {
            components.clear()
            wires.clear()
            components.addAll(target.components.map { it.copy() })
            wires.addAll(target.wires.map { it.copy() })
            selectedComponentId = null
            selectedWireId = null
            pendingWireStart = null
            recordHistory()
        }
    }

    LaunchedEffect(components.size, wires.size, isPaused) {
        runEngine()
    }

    val selectedComponent = components.find { it.id == selectedComponentId }

    // Smart Wire Cutting / Splice helper
    fun cutAndSpliceWire(wireToCut: CircuitWire, newComponentType: ComponentType) {
        recordHistory()
        val fromC = components.find { it.id == wireToCut.fromComponentId } ?: return
        val toC = components.find { it.id == wireToCut.toComponentId } ?: return
        val rawMidX = (fromC.x + toC.x) / 2f
        val rawMidY = (fromC.y + toC.y) / 2f
        val midX = if (isGridSnapEnabled) (round(rawMidX / 24f) * 24f).coerceIn(36f, 336f) else rawMidX
        val midY = if (isGridSnapEnabled) (round(rawMidY / 24f) * 24f).coerceIn(48f, 480f) else rawMidY

        val newComp = CircuitComponent(
            type = newComponentType,
            label = if (currentLanguage == AppLanguage.BANGLA) newComponentType.nameBn else newComponentType.nameEn,
            value = newComponentType.defaultVal,
            x = midX,
            y = midY
        )
        components.add(newComp)

        val pins = newComp.getPins()
        val p1 = pins.firstOrNull()?.id ?: "p1"
        val p2 = pins.lastOrNull()?.id ?: "p2"

        wires.remove(wireToCut)
        wires.add(CircuitWire(fromComponentId = wireToCut.fromComponentId, fromPinId = wireToCut.fromPinId, toComponentId = newComp.id, toPinId = p1))
        wires.add(CircuitWire(fromComponentId = newComp.id, fromPinId = p2, toComponentId = wireToCut.toComponentId, toPinId = wireToCut.toPinId))
        selectedWireId = null
        selectedComponentId = newComp.id
        runEngine()
        val msg = StringsHelper.get(currentLanguage, "তার কেটে ${newComp.label} ইন-লাইন যুক্ত করা হয়েছে!", "Cut wire and inserted ${newComp.label} in-line!")
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    // MODAL NAVIGATION DRAWER WITH SEARCH & DRAG-AND-DROP CATALOG
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = CircuitNavySurface,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(330.dp)
            ) {
                ComponentDrawerContent(
                    language = currentLanguage,
                    onAddComponent = { type, customName, companyName ->
                        recordHistory()
                        // If a wire was selected, smart splice it in-line!
                        val wireToSplice = wires.find { it.id == selectedWireId }
                        if (wireToSplice != null) {
                            cutAndSpliceWire(wireToSplice, type)
                        } else {
                            val defaultLabel = if (currentLanguage == AppLanguage.BANGLA) type.nameBn else type.nameEn
                            val rawX = 168f
                            val rawY = 216f
                            val startX = if (isGridSnapEnabled) (round(rawX / 24f) * 24f).coerceIn(36f, 336f) else rawX
                            val startY = if (isGridSnapEnabled) (round(rawY / 24f) * 24f).coerceIn(48f, 480f) else rawY
                            val newComp = CircuitComponent(
                                type = type,
                                label = customName ?: defaultLabel,
                                value = type.defaultVal,
                                x = startX,
                                y = startY,
                                customCompanyName = companyName
                            )
                            components.add(newComp)
                            selectedComponentId = newComp.id
                            runEngine()
                        }
                        coroutineScope.launch { drawerState.close() }
                    },
                    onCloseDrawer = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(CircuitNavyDark)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. TOP HEADER & TELEMETRY
                ElectroCADHeader(
                    result = simulationResult,
                    isPaused = isPaused,
                    visualMode = visualMode,
                    simSpeed = simSpeed,
                    isGridSnapEnabled = isGridSnapEnabled,
                    language = currentLanguage,
                    canUndo = historyManager.canUndo(),
                    canRedo = historyManager.canRedo(),
                    onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                    onTogglePause = {
                        isPaused = !isPaused
                        runEngine()
                    },
                    onToggleVisualMode = {
                        visualMode = when (visualMode) {
                            ElectronVisualMode.PARTICLES -> ElectronVisualMode.WAVE
                            ElectronVisualMode.WAVE -> ElectronVisualMode.OFF
                            ElectronVisualMode.OFF -> ElectronVisualMode.PARTICLES
                        }
                    },
                    onToggleGridSnap = {
                        isGridSnapEnabled = !isGridSnapEnabled
                        val toastMsg = if (isGridSnapEnabled) {
                            StringsHelper.get(currentLanguage, "গ্রিড স্ন্যাপিং চালু (২৪dp)", "Grid Snapping ON (24dp)")
                        } else {
                            StringsHelper.get(currentLanguage, "গ্রিড স্ন্যাপিং বন্ধ (ফ্রি মুভ)", "Grid Snapping OFF (Free)")
                        }
                        Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                    },
                    onOpenSpeedModal = { showSpeedModal = true },
                    onToggleLanguage = {
                        currentLanguage = if (currentLanguage == AppLanguage.BANGLA) AppLanguage.ENGLISH else AppLanguage.BANGLA
                        onToggleLanguage?.invoke()
                    },
                    onUndo = {
                        val prev = historyManager.undo(components, wires)
                        if (prev != null) {
                            components.clear()
                            wires.clear()
                            components.addAll(prev.components)
                            wires.addAll(prev.wires)
                            selectedComponentId = null
                            selectedWireId = null
                            runEngine()
                        }
                    },
                    onRedo = {
                        val next = historyManager.redo(components, wires)
                        if (next != null) {
                            components.clear()
                            wires.clear()
                            components.addAll(next.components)
                            wires.addAll(next.wires)
                            selectedComponentId = null
                            selectedWireId = null
                            runEngine()
                        }
                    },
                    onOpenSavedDesigns = { showSavedDesignsDialog = true },
                    onSaveDesign = { showSaveDesignDialog = true },
                    onExportImage = {
                        CircuitImageExporter.exportAndShareCircuit(
                            context = context,
                            circuitTitle = "ElectroCAD Circuit Design",
                            components = components,
                            wires = wires
                        )
                    },
                    onOpenPresets = { showPresetSheet = true },
                    onReset = {
                        recordHistory()
                        val firstPreset = DefaultCircuits.getPresets().first()
                        components.clear()
                        wires.clear()
                        components.addAll(firstPreset.components.map { it.copy() })
                        wires.addAll(firstPreset.wires.map { it.copy() })
                        selectedComponentId = null
                        selectedWireId = null
                        pendingWireStart = null
                        runEngine()
                    }
                )

                // Wire Selection / Mode Contextual Banner
                AnimatedVisibility(visible = selectedWireId != null || pendingWireStart != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (selectedWireId != null) AmberWarning.copy(alpha = 0.2f) else ElectricCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (selectedWireId != null) StringsHelper.get(currentLanguage, "✂️ তার নির্বাচিত হয়েছে। ডিলিট বা ইন-লাইন কম্পোনেন্ট কাটুন।", "✂️ Wire selected. Tap Delete or add part to splice in-line.")
                            else StringsHelper.get(currentLanguage, "⚡ সংযোগের জন্য পরবর্তী পিনে ট্যাপ করুন।", "⚡ Tap destination pin to connect wire."),
                            color = if (selectedWireId != null) AmberWarning else ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (selectedWireId != null) {
                                Button(
                                    onClick = {
                                        recordHistory()
                                        wires.removeAll { it.id == selectedWireId }
                                        selectedWireId = null
                                        runEngine()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp).testTag("delete_selected_wire_btn")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Wire", tint = TechWhite, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = StringsHelper.get(currentLanguage, "তার মুছুন", "Delete Wire"), color = TechWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            IconButton(
                                onClick = {
                                    selectedWireId = null
                                    pendingWireStart = null
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TechWhite)
                            }
                        }
                    }
                }

                // 2. CANVAS WITH MULTI-TOUCH GESTURES & ELECTRON WAVES
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    CircuitCanvas(
                        components = components,
                        wires = wires,
                        simulationState = simulationResult.state,
                        selectedComponentId = selectedComponentId,
                        selectedWireId = selectedWireId,
                        pendingWireStart = pendingWireStart,
                        visualMode = visualMode,
                        simSpeed = simSpeed,
                        isGridSnapEnabled = isGridSnapEnabled,
                        gridStepDp = 24f,
                        language = currentLanguage,
                        onSelectComponent = { comp ->
                            selectedComponentId = comp.id
                            selectedWireId = null
                        },
                        onSelectWire = { wire ->
                            selectedWireId = wire?.id
                            if (wire != null) selectedComponentId = null
                        },
                        onMoveComponent = { id, dx, dy ->
                            val idx = components.indexOfFirst { it.id == id }
                            if (idx != -1) {
                                val c = components[idx]
                                val rawX = (c.x + dx).coerceIn(36f, 336f)
                                val rawY = (c.y + dy).coerceIn(48f, 480f)
                                val finalX = if (isGridSnapEnabled) (round(rawX / 24f) * 24f).coerceIn(36f, 336f) else rawX
                                val finalY = if (isGridSnapEnabled) (round(rawY / 24f) * 24f).coerceIn(48f, 480f) else rawY
                                components[idx] = c.copy(
                                    x = finalX,
                                    y = finalY
                                )
                            }
                        },
                        onRotateComponent = { id, deltaAngle ->
                            val idx = components.indexOfFirst { it.id == id }
                            if (idx != -1) {
                                val c = components[idx]
                                components[idx] = c.copy(rotationAngle = (c.rotationAngle + deltaAngle) % 360f)
                            }
                        },
                        onScaleComponent = { id, zoomDelta ->
                            val idx = components.indexOfFirst { it.id == id }
                            if (idx != -1) {
                                val c = components[idx]
                                components[idx] = c.copy(scale = (c.scale * zoomDelta).coerceIn(0.6f, 2.5f))
                            }
                        },
                        onPinClicked = { comp, pin ->
                            val currentPending = pendingWireStart
                            if (currentPending == null) {
                                pendingWireStart = Pair(comp.id, pin.id)
                            } else {
                                if (currentPending.first != comp.id || currentPending.second != pin.id) {
                                    recordHistory()
                                    val newWire = CircuitWire(
                                        fromComponentId = currentPending.first,
                                        fromPinId = currentPending.second,
                                        toComponentId = comp.id,
                                        toPinId = pin.id
                                    )
                                    wires.add(newWire)
                                    runEngine()
                                }
                                pendingWireStart = null
                            }
                        },
                        onToggleSwitch = { comp ->
                            recordHistory()
                            val idx = components.indexOfFirst { it.id == comp.id }
                            if (idx != -1) {
                                components[idx] = comp.copy(isSwitchClosed = !comp.isSwitchClosed)
                                runEngine()
                            }
                        },
                        onComponentLongPress = { comp ->
                            showTooltipComponent = comp
                        }
                    )

                    // Contextual Floating Toolbar
                    FloatingContextualBar(
                        selectedComponent = selectedComponent,
                        selectedWireId = selectedWireId,
                        language = currentLanguage,
                        onOpenDrawer = { coroutineScope.launch { drawerState.open() } },
                        onEditComponent = { showEditDialog = true },
                        onRotateComponent = {
                            selectedComponentId?.let { id ->
                                recordHistory()
                                val idx = components.indexOfFirst { it.id == id }
                                if (idx != -1) {
                                    val c = components[idx]
                                    components[idx] = c.copy(rotationAngle = (c.rotationAngle + 90f) % 360f)
                                    runEngine()
                                }
                            }
                        },
                        onDeleteSelected = {
                            recordHistory()
                            if (selectedComponentId != null) {
                                val id = selectedComponentId!!
                                components.removeAll { it.id == id }
                                wires.removeAll { it.fromComponentId == id || it.toComponentId == id }
                                selectedComponentId = null
                                runEngine()
                            } else if (selectedWireId != null) {
                                wires.removeAll { it.id == selectedWireId }
                                selectedWireId = null
                                runEngine()
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp)
                    )
                }
            }

            // 3. EDIT COMPONENT VALUES SHEET
            if (showEditDialog && selectedComponent != null) {
                ModalBottomSheet(
                    onDismissRequest = { showEditDialog = false },
                    containerColor = CircuitNavySurface,
                    sheetState = rememberModalBottomSheetState()
                ) {
                    EditComponentSheet(
                        component = selectedComponent,
                        language = currentLanguage,
                        onUpdate = { updated ->
                            recordHistory()
                            val idx = components.indexOfFirst { it.id == updated.id }
                            if (idx != -1) {
                                components[idx] = updated
                                runEngine()
                            }
                            showEditDialog = false
                        }
                    )
                }
            }

            // 4. COMPONENT TECHNICAL SPECIFICATIONS & LIVE TELEMETRY TOOLTIP POPOVER
            if (showTooltipComponent != null) {
                ComponentSpecsDialog(
                    component = showTooltipComponent!!,
                    language = currentLanguage,
                    onDismiss = { showTooltipComponent = null }
                )
            }

            // 5. SAVE DESIGN DIALOG (Room DB)
            if (showSaveDesignDialog) {
                AlertDialog(
                    onDismissRequest = { showSaveDesignDialog = false },
                    containerColor = CircuitNavySurface,
                    title = { Text(text = StringsHelper.get(currentLanguage, "সার্কিট ডিজাইন সংরক্ষণ (Save to Room)", "Save Circuit Design (Room DB)"), color = TechWhite, fontWeight = FontWeight.Bold) },
                    text = {
                        Column {
                            Text(text = StringsHelper.get(currentLanguage, "আপনার বর্তমান সার্কিটের একটি নাম দিন:", "Enter a name for your circuit design:"), color = TechTextSecondary, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = newDesignName,
                                onValueChange = { newDesignName = it },
                                placeholder = { Text(StringsHelper.get(currentLanguage, "যেমন: LED Timer Circuit", "e.g. 555 LED Flasher")) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TechWhite,
                                    unfocusedTextColor = TechWhite,
                                    focusedBorderColor = ElectricCyan
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newDesignName.isNotBlank()) {
                                    coroutineScope.launch {
                                        repository.saveDesign(
                                            name = newDesignName.trim(),
                                            description = "ElectroCAD circuit with ${components.size} components",
                                            components = components,
                                            wires = wires
                                        )
                                        val saveMsg = StringsHelper.get(currentLanguage, "ডিজাইন সফলভাবে সংরক্ষিত হয়েছে!", "Design saved successfully!")
                                        Toast.makeText(context, saveMsg, Toast.LENGTH_SHORT).show()
                                        newDesignName = ""
                                        showSaveDesignDialog = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                        ) {
                            Text(text = StringsHelper.get(currentLanguage, "সংরক্ষণ করুন", "Save"), color = CircuitNavyDark, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSaveDesignDialog = false }) {
                            Text(text = StringsHelper.get(currentLanguage, "বাতিল", "Cancel"), color = TechTextMuted)
                        }
                    }
                )
            }

            // 6. SAVED DESIGNS BROWSER (Room DB)
            if (showSavedDesignsDialog) {
                SavedDesignsDialog(
                    designs = savedDesigns,
                    language = currentLanguage,
                    onLoadDesign = { entity ->
                        recordHistory()
                        val (loadedComps, loadedWires) = CircuitRepository.deserializeDesign(entity)
                        components.clear()
                        wires.clear()
                        components.addAll(loadedComps)
                        wires.addAll(loadedWires)
                        selectedComponentId = null
                        selectedWireId = null
                        runEngine()
                        showSavedDesignsDialog = false
                        val loadMsg = StringsHelper.get(currentLanguage, "'${entity.name}' লোড করা হয়েছে!", "Loaded '${entity.name}'!")
                        Toast.makeText(context, loadMsg, Toast.LENGTH_SHORT).show()
                    },
                    onDeleteDesign = { id ->
                        coroutineScope.launch { repository.deleteDesign(id) }
                    },
                    onDismiss = { showSavedDesignsDialog = false }
                )
            }

            // 7. PRESETS BOTTOM SHEET
            if (showPresetSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showPresetSheet = false },
                    containerColor = CircuitNavySurface,
                    sheetState = rememberModalBottomSheetState()
                ) {
                    PresetsSheet(
                        language = currentLanguage,
                        onSelectPreset = { preset ->
                            recordHistory()
                            components.clear()
                            wires.clear()
                            components.addAll(preset.components.map { it.copy() })
                            wires.addAll(preset.wires.map { it.copy() })
                            selectedComponentId = null
                            selectedWireId = null
                            pendingWireStart = null
                            showPresetSheet = false
                            runEngine()
                        }
                    )
                }
            }

            // 8. SIMULATION SPEED CONTROLLER BOTTOM SHEET
            if (showSpeedModal) {
                ModalBottomSheet(
                    onDismissRequest = { showSpeedModal = false },
                    containerColor = CircuitNavySurface,
                    sheetState = rememberModalBottomSheetState()
                ) {
                    SpeedControllerSheet(
                        currentSpeed = simSpeed,
                        visualMode = visualMode,
                        language = currentLanguage,
                        onSelectSpeed = { newSpeed ->
                            simSpeed = newSpeed
                        },
                        onToggleVisualMode = {
                            visualMode = when (visualMode) {
                                ElectronVisualMode.PARTICLES -> ElectronVisualMode.WAVE
                                ElectronVisualMode.WAVE -> ElectronVisualMode.OFF
                                ElectronVisualMode.OFF -> ElectronVisualMode.PARTICLES
                            }
                        },
                        onClose = { showSpeedModal = false }
                    )
                }
            }
        }
    }
}

// HEADER COMPONENT WITH TELEMETRY, UNDO/REDO, EXPORT & BRANDING
@Composable
private fun ElectroCADHeader(
    result: SimulationResult,
    isPaused: Boolean,
    visualMode: ElectronVisualMode,
    simSpeed: Float,
    isGridSnapEnabled: Boolean,
    language: AppLanguage,
    canUndo: Boolean,
    canRedo: Boolean,
    onOpenDrawer: () -> Unit,
    onTogglePause: () -> Unit,
    onToggleVisualMode: () -> Unit,
    onToggleGridSnap: () -> Unit,
    onOpenSpeedModal: () -> Unit,
    onToggleLanguage: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onOpenSavedDesigns: () -> Unit,
    onSaveDesign: () -> Unit,
    onExportImage: () -> Unit,
    onOpenPresets: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = CircuitNavySurface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                when (result.state) {
                    SimulationState.RUNNING_SAFE -> CircuitEmerald
                    SimulationState.SHORT_CIRCUIT -> DangerRed
                    SimulationState.COMPONENT_OVERLOAD -> AmberWarning
                    else -> CircuitBorder
                }
            )
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            // Row 1: Brand, Dev Credit & Language/Quick Config Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(CircuitNavySurfaceVariant)
                            .testTag("open_drawer_button")
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Drawer", tint = ElectricCyan, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "ElectroCAD", color = TechWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(text = "Dev: Md Shafiul Islam", color = ElectricCyan, fontSize = 9.5.sp)
                    }
                }

                // Language Toggle & Simulation Control Pills
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Bilingual Switcher
                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onToggleLanguage() }
                            .testTag("language_toggle_btn"),
                        colors = CardDefaults.cardColors(containerColor = CircuitNavySurfaceVariant),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElectricCyan.copy(alpha = 0.6f)))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == AppLanguage.BANGLA) "🌐 বাংলা" else "🌐 EN",
                                color = TechWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Simulation Speed Controller Button
                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onOpenSpeedModal() }
                            .testTag("speed_btn"),
                        colors = CardDefaults.cardColors(containerColor = ElectricCyan.copy(alpha = 0.15f)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElectricCyan))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚡ ${String.format(Locale.US, "%.2f", simSpeed)}x",
                                color = ElectricCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Grid Snap Toggle Button
                    Card(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onToggleGridSnap() }
                            .testTag("grid_snap_toggle_btn"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isGridSnapEnabled) CircuitEmerald.copy(alpha = 0.2f) else CircuitNavySurfaceVariant
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(if (isGridSnapEnabled) CircuitEmerald else CircuitBorder)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isGridSnapEnabled) "⊞ ${StringsHelper.get(language, "গ্রিড", "Grid")}" else "⊟ ${StringsHelper.get(language, "ফ্রি", "Free")}",
                                color = if (isGridSnapEnabled) CircuitEmerald else TechTextMuted,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 2: Circuit Design Action Toolbar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // History & Visualization Mode
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onUndo,
                        enabled = canUndo,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (canUndo) TechWhite else TechTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onRedo,
                        enabled = canRedo,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if (canRedo) TechWhite else TechTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onToggleVisualMode,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (visualMode == ElectronVisualMode.WAVE) ElectricCyan.copy(alpha = 0.25f) else Color.Transparent)
                    ) {
                        Icon(
                            Icons.Default.Waves,
                            contentDescription = "Wave Mode",
                            tint = if (visualMode == ElectronVisualMode.WAVE) ElectricCyan else TechTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onSaveDesign,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save Design", tint = CircuitEmerald, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onOpenSavedDesigns,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Folder, contentDescription = "Saved Designs", tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onExportImage,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Export PNG", tint = ElectricCyan, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onOpenPresets,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.LibraryBooks, contentDescription = "Presets", tint = Color(0xFFA855F7), modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onReset,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = TechTextMuted, modifier = Modifier.size(16.dp))
                    }
                }

                // Play / Pause Simulation
                IconButton(
                    onClick = onTogglePause,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(if (isPaused) DangerRed.copy(alpha = 0.2f) else ElectricCyan.copy(alpha = 0.2f))
                ) {
                    Icon(
                        if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Play/Pause",
                        tint = if (isPaused) DangerRed else ElectricCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Row 3: Live Telemetry Meters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TelemetryPill(
                    label = StringsHelper.get(language, "কারেন্ট (Current)", "Current (I)"),
                    value = "${String.format("%.2f", result.totalCurrentMa)} mA",
                    color = ElectricCyan
                )
                TelemetryPill(
                    label = StringsHelper.get(language, "সমতুল্য রোধ (Req)", "Req (Ohm)"),
                    value = if (result.equivalentResistanceOhm.isInfinite()) StringsHelper.get(language, "অসীম (∞)", "Infinite (∞)") else "${String.format("%.1f", result.equivalentResistanceOhm)} Ω",
                    color = Color(0xFFFBBF24)
                )
                TelemetryPill(
                    label = StringsHelper.get(language, "পাওয়ার (Power)", "Power (P)"),
                    value = "${String.format("%.1f", result.totalPowerMw)} mW",
                    color = CircuitEmerald
                )
            }
        }
    }
}

@Composable
private fun TelemetryPill(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .background(CircuitNavyDark, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, color = TechTextMuted, fontSize = 9.sp)
        Text(text = value, color = color, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
    }
}

// FLOATING CONTEXTUAL BAR (Rotate, Delete, Drawer, Splicing)
@Composable
private fun FloatingContextualBar(
    selectedComponent: CircuitComponent?,
    selectedWireId: String?,
    language: AppLanguage,
    onOpenDrawer: () -> Unit,
    onEditComponent: () -> Unit,
    onRotateComponent: () -> Unit,
    onDeleteSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = CircuitNavySurface.copy(alpha = 0.95f)),
        shape = RoundedCornerShape(32.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CircuitBorder))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onOpenDrawer,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("floating_drawer_button")
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Drawer", tint = CircuitNavyDark, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = StringsHelper.get(language, "+ পার্টস লাইব্রেরি", "+ Parts Library"),
                    color = CircuitNavyDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            if (selectedComponent != null) {
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = onRotateComponent,
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(CircuitNavySurfaceVariant)
                ) {
                    Icon(Icons.Default.RotateRight, contentDescription = "Rotate 90", tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = onEditComponent,
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(CircuitNavySurfaceVariant)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Value", tint = ElectricCyan, modifier = Modifier.size(17.dp))
                }
            }

            if (selectedComponent != null || selectedWireId != null) {
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = onDeleteSelected,
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(CircuitNavySurfaceVariant).testTag("delete_selected_btn")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// SEARCHABLE ONLINE COMPONENT DATABASE DRAWER
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComponentDrawerContent(
    language: AppLanguage,
    onAddComponent: (ComponentType, String?, String?) -> Unit,
    onCloseDrawer: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ComponentCategory.ALL) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = StringsHelper.get(language, "ElectroCAD পার্টস লাইব্রেরি", "ElectroCAD Parts Library"),
                    color = TechWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = StringsHelper.get(language, "অনলাইন ডাটাবেস ও পার্টস প্লেসমেন্ট", "Online Component Database & Placement"),
                    color = ElectricCyan,
                    fontSize = 11.sp
                )
            }
            IconButton(onClick = onCloseDrawer) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TechWhite)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = StringsHelper.get(language, "পার্টস বা কোম্পানির নাম (যেমন: 555, IRFZ44, Yageo)...", "Search parts or brand (e.g. 555, IRFZ44, Yageo)..."),
                    fontSize = 11.5.sp
                )
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = ElectricCyan, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TechTextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TechWhite,
                unfocusedTextColor = TechWhite,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = CircuitBorder
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(ComponentCategory.values()) { cat ->
                val isSelected = selectedCategory == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = { Text(text = if (language == AppLanguage.BANGLA) cat.titleBn else cat.titleEn, fontSize = 10.5.sp) },
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

        Spacer(modifier = Modifier.height(8.dp))

        val query = searchQuery.trim().lowercase()
        val onlineItems = OnlineComponentDatabase.items.filter { item ->
            val matchCat = selectedCategory == ComponentCategory.ALL || item.category == selectedCategory
            val matchQuery = query.isEmpty() ||
                    item.nameBn.lowercase().contains(query) ||
                    item.nameEn.lowercase().contains(query) ||
                    item.partNumber.lowercase().contains(query) ||
                    item.manufacturer.lowercase().contains(query)
            matchCat && matchQuery
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Option to add as Custom Component
            if (query.isNotEmpty() && onlineItems.none { it.partNumber.lowercase() == query }) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAddComponent(ComponentType.CUSTOM_COMPONENT, searchQuery, "Custom") },
                        colors = CardDefaults.cardColors(containerColor = ElectricCyan.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElectricCyan))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = ElectricCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = StringsHelper.get(language, "+ '$searchQuery' তৈরি করুন", "+ Create '$searchQuery'"),
                                    color = ElectricCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = StringsHelper.get(language, "কাস্টম ইলেকট্রনিক্স পার্টস হিসেবে ক্যানভাসে স্থাপন করুন", "Place on canvas as custom electronic component"),
                                    color = TechTextSecondary,
                                    fontSize = 10.5.sp
                                )
                            }
                        }
                    }
                }
            }

            items(onlineItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAddComponent(item.type, item.partNumber, item.manufacturer) },
                    colors = CardDefaults.cardColors(containerColor = CircuitNavySurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CircuitBorder))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = item.partNumber, color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = item.manufacturer, color = TechTextMuted, fontSize = 10.sp)
                        }
                        Text(
                            text = if (language == AppLanguage.BANGLA) item.nameBn else item.nameEn,
                            color = TechWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(text = item.specs, color = TechTextSecondary, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

// COMPONENT TECHNICAL SPECS & LIVE TELEMETRY TOOLTIP DIALOG
@Composable
private fun ComponentSpecsDialog(
    component: CircuitComponent,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CircuitNavySurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = component.type.iconSymbol, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = component.label,
                        color = TechWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(text = component.type.nameEn, color = ElectricCyan, fontSize = 11.sp)
                }
            }
        },
        text = {
            Column {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CircuitNavySurfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = StringsHelper.get(language, "লাইভ টেলিমেট্রি (Live Telemetry):", "Live Telemetry & Diagnostics:"),
                            color = ElectricCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• ${StringsHelper.get(language, "ভোল্টেজ ড্রপ", "Voltage Drop")}: ${String.format("%.2f", component.voltageDrop)} V",
                            color = TechWhite,
                            fontSize = 11.5.sp
                        )
                        Text(
                            text = "• ${StringsHelper.get(language, "তড়িৎ প্রবাহ (Current)", "Current Flow")}: ${String.format("%.2f", component.currentMa)} mA",
                            color = TechWhite,
                            fontSize = 11.5.sp
                        )
                        Text(
                            text = "• ${StringsHelper.get(language, "ক্ষমতা ক্ষয় (Power)", "Power Dissipation")}: ${String.format("%.2f", component.powerMw)} mW",
                            color = TechWhite,
                            fontSize = 11.5.sp
                        )
                        val statusDesc = if (component.isBurned) StringsHelper.get(language, "নষ্ট (Burned Out!)", "Burned Out!")
                        else if (component.isActive) StringsHelper.get(language, "সক্রিয় (Active)", "Active")
                        else StringsHelper.get(language, "নিষ্ক্রিয় (Idle)", "Idle")
                        Text(
                            text = "• ${StringsHelper.get(language, "উপাদান অবস্থা", "Component Status")}: $statusDesc",
                            color = if (component.isBurned) DangerRed else CircuitEmerald,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = StringsHelper.get(language, "টার্মিনাল পিনআউট:", "Terminal Pinout:"),
                    color = TechWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                component.getPins().forEach { pin ->
                    val pinLabel = if (language == AppLanguage.BANGLA) pin.labelBn else pin.labelEn
                    Text(text = "• ${pin.id}: $pinLabel", color = TechTextSecondary, fontSize = 11.sp)
                }

                if (component.customCompanyName != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${StringsHelper.get(language, "উত্পাদক", "Manufacturer")}: ${component.customCompanyName}",
                        color = Color(0xFFFBBF24),
                        fontSize = 11.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
            ) {
                Text(
                    text = StringsHelper.get(language, "ঠিক আছে", "OK"),
                    color = CircuitNavyDark,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

// SAVED DESIGNS (Room DB) MODAL
@Composable
private fun SavedDesignsDialog(
    designs: List<CircuitDesignEntity>,
    language: AppLanguage,
    onLoadDesign: (CircuitDesignEntity) -> Unit,
    onDeleteDesign: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CircuitNavySurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Folder, contentDescription = "Designs", tint = Color(0xFFFBBF24))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = StringsHelper.get(language, "সংরক্ষিত ডিজাইনসমূহ (Room DB)", "Saved Circuit Designs (Room DB)"),
                    color = TechWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            if (designs.isEmpty()) {
                Text(
                    text = StringsHelper.get(language, "এখনও কোনো ডিজাইন সংরক্ষণ করা হয়নি। উপরের সেভ বাটনে ট্যাপ করে বর্তমান সার্কিটটি সংরক্ষণ করুন।", "No saved circuit designs found. Tap the Save button on the header to save your design."),
                    color = TechTextSecondary,
                    fontSize = 13.sp
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().height(260.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(designs) { design ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CircuitNavySurfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).clickable { onLoadDesign(design) }) {
                                    Text(text = design.name, color = TechWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    val partsWord = StringsHelper.get(language, "উপাদান", "parts")
                                    Text(text = "${design.componentCount} $partsWord • ${dateFormat.format(Date(design.timestamp))}", color = TechTextMuted, fontSize = 10.5.sp)
                                }
                                IconButton(onClick = { onDeleteDesign(design.id) }, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = StringsHelper.get(language, "বন্ধ করুন", "Close"), color = ElectricCyan)
            }
        }
    )
}

@Composable
private fun EditComponentSheet(
    component: CircuitComponent,
    language: AppLanguage,
    onUpdate: (CircuitComponent) -> Unit
) {
    var currentValue by remember { mutableStateOf(component.value) }
    var selectedColor by remember { mutableStateOf(component.ledColor) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = "${component.label} - ${StringsHelper.get(language, "মান পরিবর্তন", "Edit Parameters")}",
            color = TechWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        when (component.type) {
            ComponentType.DC_BATTERY, ComponentType.SOLAR_PANEL, ComponentType.AC_SOURCE -> {
                Text(
                    text = "${StringsHelper.get(language, "ভোল্টেজ", "Voltage")}: ${currentValue.toInt()} V",
                    color = ElectricCyan,
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = currentValue,
                    onValueChange = { currentValue = it },
                    valueRange = 1.5f..220f,
                    colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
                )
            }
            ComponentType.RESISTOR, ComponentType.LDR, ComponentType.THERMISTOR -> {
                Text(
                    text = "${StringsHelper.get(language, "রোধ (Resistance)", "Resistance")}: ${currentValue.toInt()} Ω",
                    color = Color(0xFFFBBF24),
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = currentValue,
                    onValueChange = { currentValue = it },
                    valueRange = 10f..5000f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFFBBF24), activeTrackColor = Color(0xFFFBBF24))
                )
            }
            ComponentType.LED -> {
                Text(
                    text = StringsHelper.get(language, "এলইডির রঙ পরিবর্তন:", "Change LED Emission Color:"),
                    color = TechWhite
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val colorList = if (language == AppLanguage.BANGLA) {
                        listOf("RED" to "লাল", "GREEN" to "সবুজ", "BLUE" to "নীল", "AMBER" to "হলুদ")
                    } else {
                        listOf("RED" to "Red", "GREEN" to "Green", "BLUE" to "Blue", "AMBER" to "Amber")
                    }
                    colorList.forEach { (code, name) ->
                        Button(
                            onClick = { selectedColor = code },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedColor == code) ElectricCyan else CircuitNavySurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(text = name, color = if (selectedColor == code) CircuitNavyDark else TechWhite)
                        }
                    }
                }
            }
            ComponentType.CAPACITOR -> {
                Text(
                    text = "${StringsHelper.get(language, "ক্যাপাসিট্যান্স", "Capacitance")}: ${currentValue.toInt()} µF",
                    color = Color(0xFFA855F7),
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = currentValue,
                    onValueChange = { currentValue = it },
                    valueRange = 10f..1000f,
                    colors = SliderDefaults.colors(thumbColor = Color(0xFFA855F7), activeTrackColor = Color(0xFFA855F7))
                )
            }
            ComponentType.FUSE -> {
                Text(
                    text = "${StringsHelper.get(language, "ফিউজ রেটিং", "Fuse Rating")}: ${currentValue} A",
                    color = DangerRed,
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = currentValue,
                    onValueChange = { currentValue = it },
                    valueRange = 0.5f..10f,
                    colors = SliderDefaults.colors(thumbColor = DangerRed, activeTrackColor = DangerRed)
                )
            }
            else -> {
                Text(
                    text = "${StringsHelper.get(language, "ডিফল্ট মান", "Default Value")}: ${component.value} ${component.type.unit}",
                    color = TechTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                onUpdate(
                    component.copy(
                        value = currentValue,
                        ledColor = selectedColor
                    )
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = StringsHelper.get(language, "সংরক্ষণ করুন (Apply Changes)", "Apply Changes"),
                color = CircuitNavyDark,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PresetsSheet(
    language: AppLanguage,
    onSelectPreset: (CircuitPreset) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = StringsHelper.get(language, "প্রস্তুত প্রিসেট সার্কিট নির্বাচন করুন", "Select Circuit Preset"),
            color = TechWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = StringsHelper.get(language, "শিক্ষার্থী ও শিক্ষকের জন্য তৈরি প্রস্তুত ল্যাব ডেমো:", "Ready-to-use lab demos prepared for students and teachers:"),
            color = TechTextSecondary,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        val presets = DefaultCircuits.getPresets()
        presets.forEach { preset ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSelectPreset(preset) },
                colors = CardDefaults.cardColors(containerColor = CircuitNavySurfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.BANGLA) preset.titleBn else preset.titleEn,
                            color = TechWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = preset.categoryBn,
                            color = ElectricCyan,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == AppLanguage.BANGLA) preset.descriptionBn else preset.descriptionEn,
                        color = TechTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// SIMULATION SPEED CONTROLLER MODAL BOTTOM SHEET
@Composable
private fun SpeedControllerSheet(
    currentSpeed: Float,
    visualMode: ElectronVisualMode,
    language: AppLanguage,
    onSelectSpeed: (Float) -> Unit,
    onToggleVisualMode: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = StringsHelper.get(language, "⚡ সিমুলেশন গতি ও ওয়েভ ফ্রিকোয়েন্সি", "⚡ Simulation Speed & Wave Frequency"),
                    color = TechWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = StringsHelper.get(language, "ইলেকট্রন প্রবাহ ও সাইন তরঙ্গের গতি নিয়ন্ত্রণ করুন", "Control drift velocity of electrons and sine wave frequency"),
                    color = ElectricCyan,
                    fontSize = 11.5.sp
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TechWhite)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Speed Display Pill Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CircuitNavySurfaceVariant),
            shape = RoundedCornerShape(14.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ElectricCyan))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = StringsHelper.get(language, "বর্তমান গতিবেগ (Speed Rate):", "Active Speed Multiplier:"),
                        color = TechTextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = when {
                            currentSpeed < 0.6f -> StringsHelper.get(language, "খুব ধীর (পুঙ্খানুপুঙ্খ ডিবাগিং ও বিশ্লেষণ)", "Very Slow (Detailed Wave Debugging)")
                            currentSpeed < 0.9f -> StringsHelper.get(language, "ধীর গতি (ওয়েভ পরিদর্শন)", "Slow (Wave Inspection Mode)")
                            currentSpeed <= 1.25f -> StringsHelper.get(language, "স্বাভাবিক গতি (রিয়েল-টাইম প্রবাহ)", "Normal Speed (Real-time Flow)")
                            currentSpeed <= 2.5f -> StringsHelper.get(language, "দ্রুত গতি (উচ্চ ফ্রিকোয়েন্সি)", "Fast (High Frequency Response)")
                            else -> StringsHelper.get(language, "সর্বোচ্চ গতি (আল্ট্রা হাই-স্পিড)", "Maximum (Ultra High-Speed)")
                        },
                        color = Color(0xFFFBBF24),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = ElectricCyan.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = "${String.format(Locale.US, "%.2f", currentSpeed)}x",
                        color = ElectricCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Preset Multiplier Chips
        Text(
            text = StringsHelper.get(language, "দ্রুত প্রিসেট গতি:", "Quick Preset Multipliers:"),
            color = TechWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(0.25f, 0.5f, 1.0f, 2.0f, 4.0f).forEach { speedVal ->
                val isSelected = kotlin.math.abs(currentSpeed - speedVal) < 0.05f
                Button(
                    onClick = { onSelectSpeed(speedVal) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) ElectricCyan else CircuitNavySurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = "${speedVal}x",
                        color = if (isSelected) CircuitNavyDark else TechWhite,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fine Speed Slider
        Text(
            text = StringsHelper.get(language, "সূক্ষ্ম গতি নিয়ন্ত্রণ (০.২৫x - ৪.০x):", "Fine Speed Tuning (0.25x - 4.0x):"),
            color = TechWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Slider(
            value = currentSpeed,
            onValueChange = { onSelectSpeed(it) },
            valueRange = 0.25f..4.0f,
            steps = 14,
            colors = SliderDefaults.colors(
                thumbColor = ElectricCyan,
                activeTrackColor = ElectricCyan,
                inactiveTrackColor = CircuitBorder
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Wave Visual Mode Toggle within sheet
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CircuitNavySurfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = StringsHelper.get(language, "ডিসপ্লে মোড:", "Visual Flow Mode:"),
                        color = TechTextMuted,
                        fontSize = 10.5.sp
                    )
                    Text(
                        text = if (language == AppLanguage.BANGLA) visualMode.labelBn else visualMode.labelEn,
                        color = TechWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = onToggleVisualMode,
                    colors = ButtonDefaults.buttonColors(containerColor = CircuitNavyDark),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(Icons.Default.Waves, contentDescription = "Cycle Visual Mode", tint = ElectricCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = StringsHelper.get(language, "পরিবর্তন", "Toggle"),
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm button
        Button(
            onClick = onClose,
            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = StringsHelper.get(language, "নিশ্চিত করুন (Apply)", "Apply & Close"),
                color = CircuitNavyDark,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
