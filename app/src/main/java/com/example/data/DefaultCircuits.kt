package com.example.data

import com.example.model.CircuitComponent
import com.example.model.CircuitWire
import com.example.model.ComponentType

data class CircuitPreset(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val categoryBn: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val components: List<CircuitComponent>,
    val wires: List<CircuitWire>
)

object DefaultCircuits {

    fun getPresets(): List<CircuitPreset> {
        // Preset 1: Simple LED Circuit
        val b1 = CircuitComponent(id = "p1_bat", type = ComponentType.DC_BATTERY, label = "9V ব্যাটারি", value = 9f, x = 60f, y = 140f)
        val s1 = CircuitComponent(id = "p1_sw", type = ComponentType.SWITCH_SPST, label = "টগল সুইচ", value = 1f, x = 160f, y = 80f, isSwitchClosed = true)
        val r1 = CircuitComponent(id = "p1_res", type = ComponentType.RESISTOR, label = "৩৩০Ω রোধ", value = 330f, x = 260f, y = 80f)
        val led1 = CircuitComponent(id = "p1_led", type = ComponentType.LED, label = "লাল এলইডি", value = 2.0f, x = 260f, y = 200f, ledColor = "RED")

        val wires1 = listOf(
            CircuitWire(fromComponentId = "p1_bat", fromPinId = "pos", toComponentId = "p1_sw", toPinId = "p1"),
            CircuitWire(fromComponentId = "p1_sw", fromPinId = "p2", toComponentId = "p1_res", toPinId = "p1"),
            CircuitWire(fromComponentId = "p1_res", fromPinId = "p2", toComponentId = "p1_led", toPinId = "anode"),
            CircuitWire(fromComponentId = "p1_led", fromPinId = "cathode", toComponentId = "p1_bat", toPinId = "neg")
        )

        // Preset 2: Series Resistors
        val b2 = CircuitComponent(id = "p2_bat", type = ComponentType.DC_BATTERY, label = "12V ব্যাটারি", value = 12f, x = 60f, y = 140f)
        val r2_1 = CircuitComponent(id = "p2_r1", type = ComponentType.RESISTOR, label = "R1 (১০০Ω)", value = 100f, x = 170f, y = 80f)
        val r2_2 = CircuitComponent(id = "p2_r2", type = ComponentType.RESISTOR, label = "R2 (২০০Ω)", value = 200f, x = 270f, y = 80f)
        val led2 = CircuitComponent(id = "p2_led", type = ComponentType.LED, label = "সবুজ এলইডি", value = 2.2f, x = 270f, y = 200f, ledColor = "GREEN")

        val wires2 = listOf(
            CircuitWire(fromComponentId = "p2_bat", fromPinId = "pos", toComponentId = "p2_r1", toPinId = "p1"),
            CircuitWire(fromComponentId = "p2_r1", fromPinId = "p2", toComponentId = "p2_r2", toPinId = "p1"),
            CircuitWire(fromComponentId = "p2_r2", fromPinId = "p2", toComponentId = "p2_led", toPinId = "anode"),
            CircuitWire(fromComponentId = "p2_led", fromPinId = "cathode", toComponentId = "p2_bat", toPinId = "neg")
        )

        // Preset 3: Motor & Buzzer Alarm Circuit
        val b3 = CircuitComponent(id = "p3_bat", type = ComponentType.DC_BATTERY, label = "9V ব্যাটারি", value = 9f, x = 60f, y = 140f)
        val sw3 = CircuitComponent(id = "p3_sw", type = ComponentType.SWITCH_SPST, label = "মাস্টার সুইচ", value = 1f, x = 160f, y = 70f, isSwitchClosed = true)
        val motor3 = CircuitComponent(id = "p3_mot", type = ComponentType.DC_MOTOR, label = "ডিসি ফ্যান মোটর", value = 1200f, x = 260f, y = 70f)
        val buzzer3 = CircuitComponent(id = "p3_buz", type = ComponentType.BUZZER, label = "অ্যালার্ম বাজার", value = 5f, x = 260f, y = 210f)

        val wires3 = listOf(
            CircuitWire(fromComponentId = "p3_bat", fromPinId = "pos", toComponentId = "p3_sw", toPinId = "p1"),
            CircuitWire(fromComponentId = "p3_sw", fromPinId = "p2", toComponentId = "p3_mot", toPinId = "pos"),
            CircuitWire(fromComponentId = "p3_mot", fromPinId = "neg", toComponentId = "p3_buz", toPinId = "pos"),
            CircuitWire(fromComponentId = "p3_buz", fromPinId = "neg", toComponentId = "p3_bat", toPinId = "neg")
        )

        // Preset 4: Potentiometer Voltage Divider
        val b4 = CircuitComponent(id = "p4_bat", type = ComponentType.DC_BATTERY, label = "9V ব্যাটারি", value = 9f, x = 60f, y = 140f)
        val pot4 = CircuitComponent(id = "p4_pot", type = ComponentType.POTENTIOMETER, label = "১০kΩ ভলিউম পোট", value = 10f, x = 180f, y = 80f)
        val led4 = CircuitComponent(id = "p4_led", type = ComponentType.LED, label = "নীল এলইডি", value = 3.0f, x = 270f, y = 180f, ledColor = "BLUE")

        val wires4 = listOf(
            CircuitWire(fromComponentId = "p4_bat", fromPinId = "pos", toComponentId = "p4_pot", toPinId = "t1"),
            CircuitWire(fromComponentId = "p4_pot", fromPinId = "wiper", toComponentId = "p4_led", toPinId = "anode"),
            CircuitWire(fromComponentId = "p4_led", fromPinId = "cathode", toComponentId = "p4_bat", toPinId = "neg")
        )

        return listOf(
            CircuitPreset(
                id = "preset_led",
                titleBn = "১. সাধারণ এলইডি ও কারেন্ট লিমিটিং রোধ",
                titleEn = "1. Basic LED with Current Limiting Resistor",
                categoryBn = "বেসিক ল্যাব",
                descriptionBn = "একটি ৯ ভোল্ট ব্যাটারি থেকে ৩৩০ ওহম রোধের মাধ্যমে নিরাপদে এলইডি জ্বালানোর আদর্শ সার্কিট। রোধের মান কমিয়ে বা বাড়িয়ে এলইডির উজ্জ্বলতা পর্যবেক্ষণ করুন।",
                descriptionEn = "Classic LED protection circuit using a 330Ω series resistor to limit forward current below 25mA.",
                components = listOf(b1, s1, r1, led1),
                wires = wires1
            ),
            CircuitPreset(
                id = "preset_series",
                titleBn = "২. সিরিজ রোধ ও ওহমের সূত্র বিশ্লেষণ",
                titleEn = "2. Series Resistors & Ohm's Law",
                categoryBn = "তত্ত্বীয় সার্কিট",
                descriptionBn = "সিরিজে যুক্ত ১০০Ω এবং ২০০Ω রোধের মোট সমতুল্য রোধ ৩০০Ω। ওহমের সূত্র V = I × R এর মাধ্যমে ভোল্টেজ ড্রপ এবং কারেন্ট হিসাব করুন।",
                descriptionEn = "Series connection of R1 and R2 showing additive equivalent resistance and current distribution.",
                components = listOf(b2, r2_1, r2_2, led2),
                wires = wires2
            ),
            CircuitPreset(
                id = "preset_motor_buzzer",
                titleBn = "৩. ডিসি মোটর ও বাজার অ্যাকচুয়েটর",
                titleEn = "3. DC Motor & Buzzer Actuator",
                categoryBn = "আউটপুট ডিভাইস",
                descriptionBn = "মেকানিক্যাল মোটর রোটেশন এবং সাউন্ড অ্যালার্ম সার্কিট। সুইচ প্রেস করে লাইভ আরপিএম ও অডিও ফ্রিকোয়েন্সি টেস্ট করুন।",
                descriptionEn = "Electromechanical system featuring high torque DC motor and acoustic buzzer transducer.",
                components = listOf(b3, sw3, motor3, buzzer3),
                wires = wires3
            ),
            CircuitPreset(
                id = "preset_pot",
                titleBn = "৪. পোটেনশিওমিটার ও পরিবর্তনশীল ভোল্টেজ",
                titleEn = "4. Variable Voltage via Potentiometer",
                categoryBn = "কন্ট্রোল ল্যাব",
                descriptionBn = "পোটেনশিওমিটারকে ভোল্টেজ ডিভাইডার হিসেবে ব্যবহার করে এলইডির ব্রাইটনেস মসৃণভাবে ডিম বা ব্রাইট করা।",
                descriptionEn = "Using a 3-terminal potentiometer to create an adjustable voltage divider for load control.",
                components = listOf(b4, pot4, led4),
                wires = wires4
            )
        )
    }
}
