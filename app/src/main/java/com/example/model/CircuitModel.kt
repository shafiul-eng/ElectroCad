package com.example.model

import java.util.UUID

enum class ComponentCategory(val titleBn: String, val titleEn: String) {
    ALL("সকল উপাদান", "All"),
    POWER("পাওয়ার ও শক্তি", "Power Sources"),
    PASSIVE("প্যাসিভ উপাদান", "Passive"),
    SEMICONDUCTOR("সেমিকন্ডাক্টর", "Semiconductors"),
    SENSORS("সেন্সর ও ট্রান্সডিউসার", "Sensors"),
    PROTECTION("রক্ষা ও সুইচিং", "Switches & Protection"),
    ICS("আইসি ও লজিক গেটস", "ICs & Logic"),
    OUTPUT("আউটপুট ও লোড", "Output & Loads"),
    METERS("মিটার ও পরিমাপ", "Meters & Probes"),
    CUSTOM("কাস্টম পার্টস", "Custom Parts")
}

enum class ComponentType(
    val category: ComponentCategory,
    val nameBn: String,
    val nameEn: String,
    val defaultVal: Float,
    val unit: String,
    val iconSymbol: String,
    val searchKeywords: String
) {
    // 1. Power Sources
    DC_BATTERY(ComponentCategory.POWER, "ডিসি ব্যাটারি", "DC Battery", 9.0f, "V", "⚡", "battery dc voltage cell ব্যাটারি ভোল্ট"),
    AC_SOURCE(ComponentCategory.POWER, "এসি সোর্স", "AC Source", 220.0f, "V", "∿", "ac source generator alternater এসি কারেন্ট অল্টারনেটর"),
    SOLAR_PANEL(ComponentCategory.POWER, "সোলার প্যানেল", "Solar Cell", 5.0f, "V", "☀️", "solar panel cell sun সোলার সৌর কোষ"),
    GROUND(ComponentCategory.POWER, "গ্রাউন্ড (GND)", "Ground (GND)", 0.0f, "V", "⏚", "ground gnd earth গ্রাউন্ড আর্থিং"),

    // 2. Passive Components
    RESISTOR(ComponentCategory.PASSIVE, "রেজিস্টর (রোধ)", "Resistor", 330.0f, "Ω", "∿", "resistor ohm resistance রোধ রেজিস্টর ওহম"),
    POTENTIOMETER(ComponentCategory.PASSIVE, "পোটেনশিওমিটার", "Potentiometer", 10.0f, "kΩ", "⎀", "potentiometer pot variable volume ভলিউম পোট"),
    CAPACITOR(ComponentCategory.PASSIVE, "ক্যাপাসিটর", "Capacitor", 100.0f, "µF", "╢", "capacitor cap filter ধারক ক্যাপাসিটর"),
    INDUCTOR(ComponentCategory.PASSIVE, "ইনডাক্টর (কয়েল)", "Inductor", 10.0f, "mH", "ꕤ", "inductor coil choke কয়েল ইনডাক্টর চোক"),
    TRANSFORMER(ComponentCategory.PASSIVE, "ট্রান্সফরমার", "Transformer", 10.0f, "Ratio", "🗲", "transformer step down up ট্রান্সফরমার"),

    // 3. Semiconductors
    LED(ComponentCategory.SEMICONDUCTOR, "এলইডি বাতি", "LED Light", 2.0f, "Vf", "💡", "led light diode বাতি এলইডি লাইট"),
    DIODE(ComponentCategory.SEMICONDUCTOR, "পি-এন ডায়োড", "Diode (1N4007)", 0.7f, "V", "▷|", "diode 1n4007 rectifier ডায়োড রেকটিফায়ার"),
    ZENER_DIODE(ComponentCategory.SEMICONDUCTOR, "জেনার ডায়োড", "Zener Diode", 5.1f, "Vz", "▷|", "zener regulator diode জেনার ভোল্টেজ রেগুলেটর"),
    TRANSISTOR_NPN(ComponentCategory.SEMICONDUCTOR, "NPN ট্রানজিস্টর", "NPN Transistor (BC547)", 100.0f, "hFE", "⎋", "npn transistor bjt bc547 ট্রানজিস্টর এনপিএন"),
    TRANSISTOR_PNP(ComponentCategory.SEMICONDUCTOR, "PNP ট্রানজিস্টর", "PNP Transistor (BC557)", 100.0f, "hFE", "⎋", "pnp transistor bc557 ট্রানজিস্টর পিএনপি"),
    MOSFET_N(ComponentCategory.SEMICONDUCTOR, "N-চ্যানেল মসফেট", "N-Channel MOSFET", 3.3f, "Vth", "⎏", "mosfet fet irf540 মসফেট ট্রানজিস্টর"),

    // 4. Sensors
    LDR(ComponentCategory.SENSORS, "এলডিআর (লাইট সেন্সর)", "LDR Photoresistor", 500.0f, "Ω", "🔆", "ldr photoresistor light sensor আলো সেন্সর"),
    THERMISTOR(ComponentCategory.SENSORS, "থার্মিস্টর (তাপ সেন্সর)", "Thermistor NTC", 10.0f, "kΩ", "🌡️", "thermistor temperature ntc তাপ সেন্সর"),

    // 5. Switches & Protection
    SWITCH_SPST(ComponentCategory.PROTECTION, "টগল সুইচ", "Toggle Switch", 1.0f, "", "⏻", "switch toggle on off সুইচ চাবি"),
    PUSH_BUTTON(ComponentCategory.PROTECTION, "পুশ বাটন", "Push Button", 0.0f, "", "🔘", "button push momentary পুশ বাটন"),
    RELAY(ComponentCategory.PROTECTION, "রিলে মডিউল", "Relay (SPDT)", 5.0f, "V", "🎛️", "relay magnetic switch রিলে"),
    FUSE(ComponentCategory.PROTECTION, "নিরাপত্তা ফিউজ", "Safety Fuse", 1.0f, "A", "⚡", "fuse protection safety ফিউজ নিরাপত্তা"),

    // 6. ICs & Digital Logic
    TIMER_555(ComponentCategory.ICS, "NE555 টাইমার আইসি", "NE555 Timer IC", 5.0f, "V", "⏱️", "555 timer ic astable monostable টাইমার আইসি"),
    LOGIC_AND(ComponentCategory.ICS, "AND গেট", "AND Logic Gate", 5.0f, "V", "⊓", "and gate logic gate গেট এন্ড"),
    LOGIC_OR(ComponentCategory.ICS, "OR গেট", "OR Logic Gate", 5.0f, "V", "⊔", "or gate logic gate গেট অর"),
    LOGIC_NOT(ComponentCategory.ICS, "NOT ইনভার্টার", "NOT Logic Gate", 5.0f, "V", "¬", "not inverter gate ইনভার্টার গেট"),

    // 7. Output & Actuators
    BUZZER(ComponentCategory.OUTPUT, "বাজার (সাউন্ড)", "Buzzer", 5.0f, "V", "🔊", "buzzer speaker beeper সাউন্ড স্পিকার বাজার"),
    DC_MOTOR(ComponentCategory.OUTPUT, "ডিসি মোটর", "DC Motor", 1000.0f, "RPM", "⚙", "motor dc fan rotor ডিসি মোটর ফ্যান"),
    INCANDESCENT_BULB(ComponentCategory.OUTPUT, "ফিলামেন্ট বাল্ব", "Filament Lamp", 12.0f, "V", "🪔", "bulb lamp filament বাল্ব বাতি"),

    // 8. Meters
    VOLTMETER(ComponentCategory.METERS, "ডিজিটাল ভোল্টমিটার", "Voltmeter Probe", 0.0f, "V", "🅅", "voltmeter voltage meter ভোল্টমিটার"),
    AMMETER(ComponentCategory.METERS, "ডিজিটাল অ্যামমিটার", "Ammeter Probe", 0.0f, "mA", "🄰", "ammeter current meter অ্যামমিটার কারেন্ট"),

    // 9. Custom
    CUSTOM_COMPONENT(ComponentCategory.CUSTOM, "কাস্টম পার্টস", "Custom Electronics Part", 1.0f, "", "📦", "custom user part ic module কোম্পানি পার্টস")
}

data class Pin(
    val id: String,
    val labelBn: String,
    val labelEn: String,
    val relX: Float, // relative to component center (-1f to 1f)
    val relY: Float,
    val polarity: String = "" // "+", "-", "A", "K", "GND", "VCC", "B", "C", "E", etc.
)

data class CircuitComponent(
    val id: String = UUID.randomUUID().toString(),
    val type: ComponentType,
    var label: String,
    var value: Float,
    var x: Float, // coordinate on canvas in dp
    var y: Float,
    var rotationAngle: Float = 0f, // degrees 0, 90, 180, 270, etc.
    var scale: Float = 1.0f,
    var isSwitchClosed: Boolean = true,
    var ledColor: String = "RED", // RED, GREEN, BLUE, AMBER
    var voltageDrop: Float = 0f,
    var currentMa: Float = 0f,
    var powerMw: Float = 0f,
    var isBurned: Boolean = false,
    var isActive: Boolean = false,
    var brightness: Float = 0f, // 0f to 1f for LED / motor speed
    var customCompanyName: String? = null
) {
    fun getPins(): List<Pin> {
        val basePins = when (type) {
            ComponentType.DC_BATTERY -> listOf(
                Pin("pos", "+ পজিটিভ (Anode)", "+ Positive (+)", 0f, -0.65f, polarity = "+"),
                Pin("neg", "- নেগেটিভ (Cathode)", "- Negative (-)", 0f, 0.65f, polarity = "-")
            )
            ComponentType.GROUND -> listOf(
                Pin("gnd", "GND (গ্রাউন্ড ০V)", "GND Ground (0V)", 0f, -0.65f, polarity = "GND")
            )
            ComponentType.AC_SOURCE -> listOf(
                Pin("live", "L (লাইন ফেজ)", "L (Live Phase)", 0f, -0.65f, polarity = "L"),
                Pin("neutral", "N (নিউট্রাল ০V)", "N (Neutral)", 0f, 0.65f, polarity = "N")
            )
            ComponentType.SOLAR_PANEL -> listOf(
                Pin("pos", "+ সোলার (+)", "+ Solar (+)", -0.7f, 0f, polarity = "+"),
                Pin("neg", "- সোলার (-)", "- Solar (-)", 0.7f, 0f, polarity = "-")
            )
            ComponentType.CAPACITOR -> listOf(
                Pin("p1", "+ অ্যানোড (+)", "+ Anode (+)", -0.75f, 0f, polarity = "+"),
                Pin("p2", "- ক্যাথোড (-)", "- Cathode (-)", 0.75f, 0f, polarity = "-")
            )
            ComponentType.RESISTOR, ComponentType.INDUCTOR, ComponentType.FUSE,
            ComponentType.SWITCH_SPST, ComponentType.PUSH_BUTTON, ComponentType.LDR, ComponentType.THERMISTOR,
            ComponentType.INCANDESCENT_BULB -> listOf(
                Pin("p1", "টার্মিনাল ১ (T1)", "Terminal 1 (T1)", -0.75f, 0f, polarity = ""),
                Pin("p2", "টার্মিনাল ২ (T2)", "Terminal 2 (T2)", 0.75f, 0f, polarity = "")
            )
            ComponentType.CUSTOM_COMPONENT -> listOf(
                Pin("p1", "+ VCC / ইন (+)", "+ VCC / In (+)", -0.75f, 0f, polarity = "+"),
                Pin("p2", "- GND / আউট (-)", "- GND / Out (-)", 0.75f, 0f, polarity = "-")
            )
            ComponentType.LED, ComponentType.DIODE, ComponentType.ZENER_DIODE -> listOf(
                Pin("anode", "+ অ্যানোড (A / +)", "+ Anode (A / +)", -0.75f, 0f, polarity = "+"),
                Pin("cathode", "- ক্যাথোড (K / -)", "- Cathode (K / -)", 0.75f, 0f, polarity = "-")
            )
            ComponentType.TRANSISTOR_NPN, ComponentType.TRANSISTOR_PNP, ComponentType.MOSFET_N -> listOf(
                Pin("base", "B (বেস কন্ট্রোল)", "B (Base)", -0.75f, 0f, polarity = "B"),
                Pin("collector", "C (কালেক্টর +)", "C (Collector +)", 0.35f, -0.65f, polarity = "C"),
                Pin("emitter", "E (এমিটার -)", "E (Emitter -)", 0.35f, 0.65f, polarity = "E")
            )
            ComponentType.BUZZER, ComponentType.DC_MOTOR, ComponentType.VOLTMETER, ComponentType.AMMETER -> listOf(
                Pin("pos", "+ পজিটিভ (+)", "+ Positive (+)", -0.75f, 0f, polarity = "+"),
                Pin("neg", "- নেগেটিভ (-)", "- Negative (-)", 0.75f, 0f, polarity = "-")
            )
            ComponentType.POTENTIOMETER -> listOf(
                Pin("t1", "টার্মিনাল ১ (T1)", "Terminal 1 (T1)", -0.75f, 0.35f, polarity = ""),
                Pin("wiper", "ওয়াইপার (Wiper)", "Wiper Output", 0f, -0.65f, polarity = "W"),
                Pin("t2", "টার্মিনাল ২ (T2)", "Terminal 2 (T2)", 0.75f, 0.35f, polarity = "")
            )
            ComponentType.TRANSFORMER -> listOf(
                Pin("pri1", "প্রাইমারি ১ (P+)", "Primary 1 (P+)", -0.75f, -0.4f, polarity = "+"),
                Pin("pri2", "প্রাইমারি ২ (P-)", "Primary 2 (P-)", -0.75f, 0.4f, polarity = "-"),
                Pin("sec1", "সেকেন্ডারি ১ (S+)", "Secondary 1 (S+)", 0.75f, -0.4f, polarity = "+"),
                Pin("sec2", "সেকেন্ডারি ২ (S-)", "Secondary 2 (S-)", 0.75f, 0.4f, polarity = "-")
            )
            ComponentType.RELAY -> listOf(
                Pin("coil1", "কয়েল ১ (+)", "Coil 1 (+)", -0.75f, -0.4f, polarity = "+"),
                Pin("coil2", "কয়েল ২ (-)", "Coil 2 (-)", -0.75f, 0.4f, polarity = "-"),
                Pin("com", "কমন (COM)", "Common (COM)", 0.75f, 0f, polarity = "COM"),
                Pin("no", "NO (Normally Open)", "Normally Open", 0.75f, -0.5f, polarity = "NO")
            )
            ComponentType.TIMER_555 -> listOf(
                Pin("vcc", "VCC (+ পিন ৮)", "VCC (+ Pin 8)", -0.75f, -0.5f, polarity = "+"),
                Pin("out", "OUT (পিন ৩)", "OUT (Pin 3)", 0.75f, 0f, polarity = "OUT"),
                Pin("gnd", "GND (- পিন ১)", "GND (- Pin 1)", -0.75f, 0.5f, polarity = "-")
            )
            ComponentType.LOGIC_AND, ComponentType.LOGIC_OR -> listOf(
                Pin("in1", "ইনপুট A", "Input A", -0.75f, -0.35f, polarity = "A"),
                Pin("in2", "ইনপুট B", "Input B", -0.75f, 0.35f, polarity = "B"),
                Pin("out", "আউটপুট Y", "Output Y", 0.75f, 0f, polarity = "Y")
            )
            ComponentType.LOGIC_NOT -> listOf(
                Pin("in", "ইনপুট A", "Input A", -0.75f, 0f, polarity = "A"),
                Pin("out", "আউটপুট Y", "Output Y", 0.75f, 0f, polarity = "Y")
            )
        }

        if (rotationAngle == 0f && scale == 1.0f) return basePins

        val rad = Math.toRadians(rotationAngle.toDouble())
        val cosA = kotlin.math.cos(rad).toFloat()
        val sinA = kotlin.math.sin(rad).toFloat()

        return basePins.map { pin ->
            val rx = (pin.relX * cosA - pin.relY * sinA) * scale
            val ry = (pin.relX * sinA + pin.relY * cosA) * scale
            pin.copy(relX = rx, relY = ry)
        }
    }
}

data class CircuitWire(
    val id: String = UUID.randomUUID().toString(),
    val fromComponentId: String,
    val fromPinId: String,
    val toComponentId: String,
    val toPinId: String
)

enum class SimulationState {
    RUNNING_SAFE,
    SHORT_CIRCUIT,
    OPEN_CIRCUIT,
    COMPONENT_OVERLOAD,
    PAUSED
}

data class SimulationResult(
    val state: SimulationState,
    val totalCurrentMa: Float,
    val totalPowerMw: Float,
    val equivalentResistanceOhm: Float,
    val messageBn: String,
    val messageEn: String,
    val warningBn: String? = null,
    val warningEn: String? = null
)

data class TutorialLesson(
    val id: String,
    val moduleBn: String,
    val moduleEn: String,
    val titleBn: String,
    val titleEn: String,
    val readTimeMin: Int,
    val descriptionBn: String,
    val descriptionEn: String,
    val formulas: List<String>,
    val practicalTipBn: String,
    val practicalTipEn: String,
    val presetIdToLoad: String? = null
)

data class QuizQuestion(
    val id: Int,
    val categoryBn: String,
    val questionBn: String,
    val questionEn: String,
    val optionsBn: List<String>,
    val optionsEn: List<String>,
    val correctIndex: Int,
    val explanationBn: String,
    val explanationEn: String
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isNotice: Boolean = false
)
