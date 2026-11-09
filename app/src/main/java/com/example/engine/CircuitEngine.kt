package com.example.engine

import com.example.model.CircuitComponent
import com.example.model.CircuitWire
import com.example.model.ComponentType
import com.example.model.SimulationResult
import com.example.model.SimulationState
import kotlin.math.max
import kotlin.math.min

object CircuitEngine {

    fun solve(
        components: List<CircuitComponent>,
        wires: List<CircuitWire>,
        isPaused: Boolean = false
    ): SimulationResult {
        // Reset state on components
        components.forEach { comp ->
            comp.isActive = false
            comp.currentMa = 0f
            comp.voltageDrop = 0f
            comp.powerMw = 0f
            comp.brightness = 0f
            comp.isBurned = false
        }

        if (isPaused) {
            return SimulationResult(
                state = SimulationState.PAUSED,
                totalCurrentMa = 0f,
                totalPowerMw = 0f,
                equivalentResistanceOhm = 0f,
                messageBn = "সিমুলেশন সাময়িকভাবে বন্ধ আছে। চালুর জন্য প্লে বাটনে চাপুন।",
                messageEn = "Simulation paused. Tap play to resume real-time analysis."
            )
        }

        val powerSource = components.find {
            it.type == ComponentType.DC_BATTERY || it.type == ComponentType.SOLAR_PANEL || it.type == ComponentType.AC_SOURCE
        }
        if (powerSource == null) {
            return SimulationResult(
                state = SimulationState.OPEN_CIRCUIT,
                totalCurrentMa = 0f,
                totalPowerMw = 0f,
                equivalentResistanceOhm = 0f,
                messageBn = "সার্কিটে কোনো পাওয়ার সোর্স (ব্যাটারি/সোলার) নেই। ড্রয়ার থেকে পাওয়ার সোর্স যুক্ত করুন।",
                messageEn = "No power source found. Please add a DC Battery or Solar Panel from the drawer."
            )
        }

        if (wires.isEmpty()) {
            return SimulationResult(
                state = SimulationState.OPEN_CIRCUIT,
                totalCurrentMa = 0f,
                totalPowerMw = 0f,
                equivalentResistanceOhm = 0f,
                messageBn = "সার্কিটে কোনো তারের সংযোগ নেই। টার্মিনালগুলোর মধ্যে সংযোগ তৈরি করুন।",
                messageEn = "No wire connections found. Connect terminals to complete the loop."
            )
        }

        // Build adjacency map between (componentId, pinId)
        val adj = mutableMapOf<Pair<String, String>, MutableList<Pair<String, String>>>()
        fun addEdge(c1: String, p1: String, c2: String, p2: String) {
            val key1 = Pair(c1, p1)
            val key2 = Pair(c2, p2)
            adj.getOrPut(key1) { mutableListOf() }.add(key2)
            adj.getOrPut(key2) { mutableListOf() }.add(key1)
        }

        for (w in wires) {
            addEdge(w.fromComponentId, w.fromPinId, w.toComponentId, w.toPinId)
        }

        // Internal connectivity inside components:
        // Note: For bidirectional wires/linear elements, we add bidirectional edges.
        // For polarized diodes/LEDs, we add a directed edge from anode to cathode only!
        fun addDirectedEdge(c1: String, p1: String, c2: String, p2: String) {
            val key1 = Pair(c1, p1)
            val key2 = Pair(c2, p2)
            adj.getOrPut(key1) { mutableListOf() }.add(key2)
        }

        for (c in components) {
            when (c.type) {
                ComponentType.SWITCH_SPST, ComponentType.PUSH_BUTTON -> {
                    if (c.isSwitchClosed) {
                        addEdge(c.id, "p1", c.id, "p2")
                    }
                }
                ComponentType.FUSE -> {
                    if (!c.isBurned) {
                        addEdge(c.id, "p1", c.id, "p2")
                    }
                }
                ComponentType.RESISTOR, ComponentType.CAPACITOR, ComponentType.INDUCTOR,
                ComponentType.LDR, ComponentType.THERMISTOR, ComponentType.INCANDESCENT_BULB,
                ComponentType.CUSTOM_COMPONENT -> {
                    addEdge(c.id, "p1", c.id, "p2")
                }
                ComponentType.LED, ComponentType.DIODE, ComponentType.ZENER_DIODE -> {
                    // Strictly directed: Current can only travel Anode (+) -> Cathode (-)
                    addDirectedEdge(c.id, "anode", c.id, "cathode")
                }
                ComponentType.BUZZER, ComponentType.DC_MOTOR, ComponentType.VOLTMETER, ComponentType.AMMETER -> {
                    addEdge(c.id, "pos", c.id, "neg")
                }
                ComponentType.POTENTIOMETER -> {
                    addEdge(c.id, "t1", c.id, "wiper")
                    addEdge(c.id, "wiper", c.id, "t2")
                }
                ComponentType.TRANSISTOR_NPN, ComponentType.TRANSISTOR_PNP, ComponentType.MOSFET_N -> {
                    addEdge(c.id, "collector", c.id, "emitter")
                }
                ComponentType.RELAY -> {
                    addEdge(c.id, "com", c.id, "no")
                }
                ComponentType.TIMER_555 -> {
                    addEdge(c.id, "vcc", c.id, "out")
                }
                ComponentType.LOGIC_AND, ComponentType.LOGIC_OR -> {
                    addEdge(c.id, "in1", c.id, "out")
                    addEdge(c.id, "in2", c.id, "out")
                }
                ComponentType.LOGIC_NOT -> {
                    addEdge(c.id, "in", c.id, "out")
                }
                ComponentType.DC_BATTERY, ComponentType.AC_SOURCE, ComponentType.SOLAR_PANEL, ComponentType.GROUND, ComponentType.TRANSFORMER -> {
                    // Do not internally short sources
                }
            }
        }

        // Identify source terminals
        val startPinId = when (powerSource.type) {
            ComponentType.AC_SOURCE -> "live"
            else -> "pos"
        }
        val targetPinId = when (powerSource.type) {
            ComponentType.AC_SOURCE -> "neutral"
            else -> "neg"
        }

        val start = Pair(powerSource.id, startPinId)
        val target = Pair(powerSource.id, targetPinId)

        val visited = mutableSetOf<Pair<String, String>>()
        val parent = mutableMapOf<Pair<String, String>, Pair<String, String>>()
        val queue = ArrayDeque<Pair<String, String>>()

        queue.add(start)
        visited.add(start)

        var pathFound = false
        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            if (curr == target) {
                pathFound = true
                break
            }
            val neighbors = adj[curr] ?: emptyList()
            for (nbr in neighbors) {
                if (nbr !in visited) {
                    visited.add(nbr)
                    parent[nbr] = curr
                    queue.add(nbr)
                }
            }
        }

        if (!pathFound) {
            // Check if loop failed because of reverse-biased diode/LED
            val testAdj = mutableMapOf<Pair<String, String>, MutableList<Pair<String, String>>>()
            for ((k, list) in adj) {
                testAdj.getOrPut(k) { mutableListOf() }.addAll(list)
            }
            for (c in components) {
                if (c.type == ComponentType.LED || c.type == ComponentType.DIODE || c.type == ComponentType.ZENER_DIODE) {
                    testAdj.getOrPut(Pair(c.id, "cathode")) { mutableListOf() }.add(Pair(c.id, "anode"))
                }
            }
            val testVisited = mutableSetOf<Pair<String, String>>()
            val testQueue = ArrayDeque<Pair<String, String>>()
            testQueue.add(start)
            testVisited.add(start)
            var reverseBiasedLoop = false
            while (testQueue.isNotEmpty()) {
                val curr = testQueue.removeFirst()
                if (curr == target) {
                    reverseBiasedLoop = true
                    break
                }
                for (nbr in testAdj[curr] ?: emptyList()) {
                    if (nbr !in testVisited) {
                        testVisited.add(nbr)
                        testQueue.add(nbr)
                    }
                }
            }

            if (reverseBiasedLoop) {
                return SimulationResult(
                    state = SimulationState.OPEN_CIRCUIT,
                    totalCurrentMa = 0f,
                    totalPowerMw = 0f,
                    equivalentResistanceOhm = Float.POSITIVE_INFINITY,
                    messageBn = "ডায়োড বা এলইডি বিপরীত পোলারিটিতে (Reverse Bias) যুক্ত! কারেন্ট প্রবাহ বন্ধ।",
                    messageEn = "Diode or LED is connected in Reverse Bias polarity! Current is blocked.",
                    warningBn = "পোলারিটি ঠিক করতে অ্যানোড (+) পজিটিভ এবং ক্যাথোড (-) নেগেটিভ লাইনে যুক্ত করুন।",
                    warningEn = "Connect Anode (+) to positive rail and Cathode (-) to ground/negative."
                )
            }

            val openSwitch = components.find { (it.type == ComponentType.SWITCH_SPST || it.type == ComponentType.PUSH_BUTTON) && !it.isSwitchClosed }
            val switchMsgBn = if (openSwitch != null) " (সুইচ '${openSwitch.label}' বন্ধ আছে)" else ""
            val switchMsgEn = if (openSwitch != null) " (Switch '${openSwitch.label}' is OFF)" else ""

            return SimulationResult(
                state = SimulationState.OPEN_CIRCUIT,
                totalCurrentMa = 0f,
                totalPowerMw = 0f,
                equivalentResistanceOhm = Float.POSITIVE_INFINITY,
                messageBn = "সার্কিটটি উন্মুক্ত (Open Circuit)। কারেন্ট প্রবাহিত হচ্ছে না$switchMsgBn।",
                messageEn = "Open Circuit detected. No current is flowing$switchMsgEn."
            )
        }

        // Trace active path
        val activeComponentIds = mutableSetOf<String>()
        var curr = target
        while (curr != start) {
            activeComponentIds.add(curr.first)
            val p = parent[curr] ?: break
            curr = p
        }
        activeComponentIds.add(powerSource.id)

        val loopComponents = components.filter { it.id in activeComponentIds && it.id != powerSource.id }

        var totalResistance = 0.5f // internal resistance
        var forwardVoltageDrop = 0f
        var hasLoad = false

        for (comp in loopComponents) {
            when (comp.type) {
                ComponentType.RESISTOR -> {
                    totalResistance += comp.value
                    hasLoad = true
                }
                ComponentType.POTENTIOMETER -> {
                    totalResistance += comp.value * 1000f * 0.5f
                    hasLoad = true
                }
                ComponentType.LDR -> {
                    totalResistance += comp.value
                    hasLoad = true
                }
                ComponentType.THERMISTOR -> {
                    totalResistance += comp.value * 1000f
                    hasLoad = true
                }
                ComponentType.LED -> {
                    forwardVoltageDrop += comp.value
                    hasLoad = true
                }
                ComponentType.DIODE, ComponentType.ZENER_DIODE -> {
                    forwardVoltageDrop += comp.value
                }
                ComponentType.BUZZER -> {
                    totalResistance += 100f
                    hasLoad = true
                }
                ComponentType.DC_MOTOR -> {
                    totalResistance += 20f
                    hasLoad = true
                }
                ComponentType.INCANDESCENT_BULB -> {
                    totalResistance += 30f
                    hasLoad = true
                }
                ComponentType.FUSE -> {
                    totalResistance += 0.2f
                }
                ComponentType.AMMETER -> {
                    totalResistance += 0.05f
                }
                ComponentType.VOLTMETER -> {
                    totalResistance += 100000f // 100k
                }
                ComponentType.CUSTOM_COMPONENT -> {
                    totalResistance += 50f
                    hasLoad = true
                }
                else -> {
                    totalResistance += 2f
                }
            }
        }

        val sourceV = powerSource.value
        val effectiveV = max(0f, sourceV - forwardVoltageDrop)

        // Short Circuit check
        if (totalResistance < 2f && !hasLoad) {
            powerSource.currentMa = 10000f
            powerSource.isBurned = true
            return SimulationResult(
                state = SimulationState.SHORT_CIRCUIT,
                totalCurrentMa = powerSource.currentMa,
                totalPowerMw = (sourceV * sourceV) / 0.5f * 1000f,
                equivalentResistanceOhm = totalResistance,
                messageBn = "বিপদ: শর্ট সার্কিট ধরা পড়েছে! পাওয়ার সোর্স সরাসরি শর্ট সংযোগে আছে।",
                messageEn = "Danger: Short Circuit detected! Direct link across power source.",
                warningBn = "শর্ট সার্কিটের ফলে প্রচুর কারেন্ট প্রবাহিত হয়ে উপাদান উত্তপ্ত হচ্ছে!",
                warningEn = "High current surge may damage power source or heat wiring!"
            )
        }

        val loopCurrentMa = (effectiveV / totalResistance) * 1000f
        val totalPowerMw = sourceV * loopCurrentMa

        powerSource.currentMa = loopCurrentMa
        powerSource.voltageDrop = sourceV
        powerSource.isActive = true

        var hasBurnout = false
        var burnoutReasonBn = ""
        var burnoutReasonEn = ""

        for (comp in loopComponents) {
            comp.isActive = true
            comp.currentMa = loopCurrentMa

            when (comp.type) {
                ComponentType.RESISTOR -> {
                    comp.voltageDrop = (comp.value * loopCurrentMa) / 1000f
                    comp.powerMw = comp.voltageDrop * loopCurrentMa
                    if (comp.powerMw > 250f) {
                        comp.isBurned = true
                        hasBurnout = true
                        burnoutReasonBn = "রেজিস্টর '${comp.label}' অতিরিক্ত তাপে নষ্ট হতে পারে (${String.format("%.1f", comp.powerMw)}mW > 250mW)!"
                        burnoutReasonEn = "Resistor '${comp.label}' power exceeds 250mW limit (${String.format("%.1f", comp.powerMw)}mW)!"
                    }
                }
                ComponentType.LED -> {
                    comp.voltageDrop = min(sourceV, comp.value)
                    comp.powerMw = comp.voltageDrop * loopCurrentMa
                    if (loopCurrentMa > 30f) {
                        comp.isBurned = true
                        comp.brightness = 0f
                        hasBurnout = true
                        burnoutReasonBn = "এলইডি '${comp.label}' অতিরিক্ত কারেন্টে জ্বলে গেছে (${String.format("%.1f", loopCurrentMa)}mA > 30mA)! একটি রেজিস্টর যোগ করুন।"
                        burnoutReasonEn = "LED '${comp.label}' burned out due to overcurrent (${String.format("%.1f", loopCurrentMa)}mA > 30mA)!"
                    } else {
                        comp.isBurned = false
                        comp.brightness = (loopCurrentMa / 20f).coerceIn(0.1f, 1f)
                    }
                }
                ComponentType.DC_MOTOR -> {
                    comp.voltageDrop = (20f * loopCurrentMa) / 1000f
                    comp.brightness = (loopCurrentMa / 200f).coerceIn(0.2f, 1f)
                }
                ComponentType.BUZZER -> {
                    comp.voltageDrop = (100f * loopCurrentMa) / 1000f
                    comp.brightness = (loopCurrentMa / 40f).coerceIn(0.3f, 1f)
                }
                ComponentType.INCANDESCENT_BULB -> {
                    comp.voltageDrop = (30f * loopCurrentMa) / 1000f
                    comp.brightness = (loopCurrentMa / 150f).coerceIn(0.2f, 1f)
                }
                ComponentType.FUSE -> {
                    if (loopCurrentMa > comp.value * 1000f) {
                        comp.isBurned = true
                        hasBurnout = true
                        burnoutReasonBn = "নিরাপত্তা ফিউজ গলে গিয়ে সার্কিট রক্ষা করেছে (${String.format("%.1f", loopCurrentMa)}mA > ${comp.value * 1000}mA)!"
                        burnoutReasonEn = "Safety fuse blown protecting circuit from overcurrent!"
                    }
                }
                ComponentType.VOLTMETER -> {
                    comp.voltageDrop = sourceV
                }
                ComponentType.AMMETER -> {
                    comp.currentMa = loopCurrentMa
                }
                else -> {
                    comp.voltageDrop = 0.1f
                }
            }
        }

        return if (hasBurnout) {
            SimulationResult(
                state = SimulationState.COMPONENT_OVERLOAD,
                totalCurrentMa = loopCurrentMa,
                totalPowerMw = totalPowerMw,
                equivalentResistanceOhm = totalResistance,
                messageBn = "সার্কিট সচল কিন্তু ওভারলোড ঝুঁকি রয়েছে!",
                messageEn = "Circuit active with component overload warning!",
                warningBn = burnoutReasonBn,
                warningEn = burnoutReasonEn
            )
        } else {
            val ohmTextBn = String.format("%.1f", totalResistance)
            val currentTextBn = String.format("%.2f", loopCurrentMa)
            val powerTextBn = String.format("%.1f", totalPowerMw)

            SimulationResult(
                state = SimulationState.RUNNING_SAFE,
                totalCurrentMa = loopCurrentMa,
                totalPowerMw = totalPowerMw,
                equivalentResistanceOhm = totalResistance,
                messageBn = "সার্কিট সফলভাবে চলছে! কারেন্ট: $currentTextBn mA, সমতুল্য রোধ: $ohmTextBn Ω, ক্ষমতা: $powerTextBn mW।",
                messageEn = "Circuit running smoothly! Current: $currentTextBn mA, Req: $ohmTextBn Ω, Power: $powerTextBn mW."
            )
        }
    }
}
