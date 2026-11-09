package com.example.data

import com.example.model.ComponentCategory
import com.example.model.ComponentType

data class OnlineComponentItem(
    val id: String,
    val type: ComponentType,
    val partNumber: String,
    val manufacturer: String,
    val nameBn: String,
    val nameEn: String,
    val category: ComponentCategory,
    val defaultVal: Float,
    val unit: String,
    val specs: String,
    val datasheetSummary: String,
    val packageType: String
)

object OnlineComponentDatabase {

    val items = listOf(
        // Microcontrollers & ICs
        OnlineComponentItem(
            id = "ic_555",
            type = ComponentType.TIMER_555,
            partNumber = "NE555P",
            manufacturer = "Texas Instruments",
            nameBn = "NE555 প্রিসিশন টাইমার আইসি",
            nameEn = "NE555 Precision Timer IC",
            category = ComponentCategory.ICS,
            defaultVal = 5.0f,
            unit = "V",
            specs = "Vcc: 4.5V–16V • Max Frequency: 500kHz • Output Sink/Source: 200mA",
            datasheetSummary = "অ্যাস্টেবল ও মনোস্টেবল মাল্টিভাইব্রেটর হিসেবে পালস তৈরি ও টাইমিং সার্কিটের জন্য বহুল ব্যবহৃত টাইমার আইসি।",
            packageType = "DIP-8"
        ),
        OnlineComponentItem(
            id = "opamp_741",
            type = ComponentType.CUSTOM_COMPONENT,
            partNumber = "LM741CN",
            manufacturer = "STMicroelectronics",
            nameBn = "LM741 জেনারেল পারপাস অপ-অ্যাম্প",
            nameEn = "LM741 Operational Amplifier",
            category = ComponentCategory.ICS,
            defaultVal = 12.0f,
            unit = "V",
            specs = "Supply: ±15V • Gain: 200V/mV • Input Impedance: 2MΩ",
            datasheetSummary = "অ্যামপ্লিফিকেশন, কম্প্যারেটর ও এনালগ সিগন্যাল প্রসেসিংয়ের জন্য ক্লাসিক অপারেশনাল অ্যামপ্লিফায়ার।",
            packageType = "DIP-8"
        ),
        OnlineComponentItem(
            id = "vreg_7805",
            type = ComponentType.CUSTOM_COMPONENT,
            partNumber = "LM7805CT",
            manufacturer = "Fairchild / onsemi",
            nameBn = "LM7805 ৫V ভোল্টেজ রেগুলেটর",
            nameEn = "LM7805 5V Linear Voltage Regulator",
            category = ComponentCategory.POWER,
            defaultVal = 5.0f,
            unit = "V",
            specs = "Input: 7V–35V • Output: 5.0V ±4% • Max Current: 1.5A",
            datasheetSummary = "উচ্চ ডিসি ভোল্টেজ থেকে স্থির ৫ ভোল্ট আউটপুট সরবরাহ করে মাইক্রোকন্ট্রোলার ও সেন্সরের জন্য।",
            packageType = "TO-220"
        ),

        // Transistors & MOSFETs
        OnlineComponentItem(
            id = "bjt_bc547",
            type = ComponentType.TRANSISTOR_NPN,
            partNumber = "BC547B",
            manufacturer = "NXP Semiconductors",
            nameBn = "BC547 NPN জেনারেল ট্রানজিস্টর",
            nameEn = "BC547 NPN Bipolar Transistor",
            category = ComponentCategory.SEMICONDUCTOR,
            defaultVal = 100.0f,
            unit = "hFE",
            specs = "Vceo: 45V • Ic: 100mA • hFE Gain: 200–450 • Ptot: 500mW",
            datasheetSummary = "ক্ষুদ্র সিগন্যাল অ্যামপ্লিফিকেশন ও ইলেকট্রনিক লোড সুইচিংয়ের জন্য আদর্শ ট্রানজিস্টর।",
            packageType = "TO-92"
        ),
        OnlineComponentItem(
            id = "bjt_2n2222",
            type = ComponentType.TRANSISTOR_NPN,
            partNumber = "2N2222A",
            manufacturer = "Central Semiconductor",
            nameBn = "2N2222A হাই কারেন্ট NPN সুইচ",
            nameEn = "2N2222A High-Current NPN Switch",
            category = ComponentCategory.SEMICONDUCTOR,
            defaultVal = 150.0f,
            unit = "hFE",
            specs = "Vceo: 40V • Ic: 800mA • Transition Freq: 300MHz",
            datasheetSummary = "রিলে, ছোট ডিসি মোটর ও হাই পাওয়ার এলইডি ড্রাইভ করার জন্য হাই কারেন্ট ট্রানজিস্টর।",
            packageType = "TO-18 / TO-92"
        ),
        OnlineComponentItem(
            id = "mosfet_irfz44n",
            type = ComponentType.MOSFET_N,
            partNumber = "IRFZ44N",
            manufacturer = "Infineon Technologies",
            nameBn = "IRFZ44N N-চ্যানেল পাওয়ার মসফেট",
            nameEn = "IRFZ44N N-Channel Power MOSFET",
            category = ComponentCategory.SEMICONDUCTOR,
            defaultVal = 4.0f,
            unit = "Vth",
            specs = "Vds: 55V • Id: 49A • Rds(on): 17.5mΩ • Gate Charge: 63nC",
            datasheetSummary = "হাই পাওয়ার মোটর ড্রাইভার, ডিসি-টু-ডিসি কনভার্টার ও সোলার ইনভার্টারে ব্যবহৃত মসফেট।",
            packageType = "TO-220AB"
        ),

        // Diodes & LEDs
        OnlineComponentItem(
            id = "diode_1n4007",
            type = ComponentType.DIODE,
            partNumber = "1N4007",
            manufacturer = "Vishay",
            nameBn = "1N4007 ১ অ্যাম্পিয়ার রেকটিফায়ার ডায়োড",
            nameEn = "1N4007 1A Standard Recovery Rectifier",
            category = ComponentCategory.SEMICONDUCTOR,
            defaultVal = 0.7f,
            unit = "V",
            specs = "VRRM: 1000V • IF(AV): 1.0A • VF: 1.1V at 1A",
            datasheetSummary = "এসি থেকে ডিসি রেকটিফিকেশন ও রিভার্স পোলারিটি সুরক্ষার জন্য ক্লাসিক ডায়োড।",
            packageType = "DO-41"
        ),
        OnlineComponentItem(
            id = "led_rgb",
            type = ComponentType.LED,
            partNumber = "WS2812B / RGB",
            manufacturer = "Worldsemi / Adafruit",
            nameBn = "৫ মিমি উজ্জ্বল ডিফায়ুজড এলইডি",
            nameEn = "5mm High-Brightness LED",
            category = ComponentCategory.SEMICONDUCTOR,
            defaultVal = 2.0f,
            unit = "Vf",
            specs = "Forward Voltage: 2.0V–3.2V • Max Current: 20mA • Luminosity: 1000mcd",
            datasheetSummary = "ইন্ডিকেটর আলো ও ডিসপ্লের জন্য অপ্টো-ইলেক্ট্রনিক আলোক উৎস।",
            packageType = "Radial 5mm"
        ),

        // Passive Components
        OnlineComponentItem(
            id = "res_metal_film",
            type = ComponentType.RESISTOR,
            partNumber = "MFR-25FTE52-330R",
            manufacturer = "Yageo",
            nameBn = "৩৩০ ওহম ১% প্রিসিশন মেটাল ফিল্ম রোধ",
            nameEn = "330Ω 1/4W Metal Film Resistor",
            category = ComponentCategory.PASSIVE,
            defaultVal = 330.0f,
            unit = "Ω",
            specs = "Resistance: 330Ω • Tolerance: ±1% • Power: 0.25W • Temp Coeff: ±50ppm/°C",
            datasheetSummary = "মাইক্রোকন্ট্রোলার পিন ও এলইডি কারেন্ট লিমিটিংয়ের সবচেয়ে জনপ্রিয় রোধ।",
            packageType = "Axial 0207"
        ),
        OnlineComponentItem(
            id = "cap_electro_100u",
            type = ComponentType.CAPACITOR,
            partNumber = "EEU-FR1V101",
            manufacturer = "Panasonic",
            nameBn = "১০০µF ৩৫V অ্যালুমিনিয়াম ইলেক্ট্রোলাইটিক ধারক",
            nameEn = "100µF 35V Electrolytic Capacitor",
            category = ComponentCategory.PASSIVE,
            defaultVal = 100.0f,
            unit = "µF",
            specs = "Capacitance: 100µF • Rated Voltage: 35V • ESR: 0.08Ω • Temp: 105°C",
            datasheetSummary = "পাওয়ার সাপ্লাই লাইনে ভোল্টেজ রিপল দূর করতে এবং ফিল্টারিংয়ের জন্য অপরিহার্য।",
            packageType = "Radial Can"
        ),

        // Actuators, Protection & Motors
        OnlineComponentItem(
            id = "relay_5v",
            type = ComponentType.RELAY,
            partNumber = "SRD-05VDC-SL-C",
            manufacturer = "Songle",
            nameBn = "৫V মিনি পাওয়ার রিলে (১০A)",
            nameEn = "5V SPDT Power Relay (10A)",
            category = ComponentCategory.PROTECTION,
            defaultVal = 5.0f,
            unit = "V",
            specs = "Coil Voltage: 5V DC • Contact Rating: 10A 250VAC / 10A 30VDC",
            datasheetSummary = "ক্ষুদ্র ডিসি সিগন্যাল দিয়ে ২২০V এসি বা বড় মোটর চালুর ইলেকট্রো-ম্যাগনেটিক সুইচ।",
            packageType = "PCB Mount"
        ),
        OnlineComponentItem(
            id = "fuse_fast_1a",
            type = ComponentType.FUSE,
            partNumber = "0217001.MXP",
            manufacturer = "Littelfuse",
            nameBn = "১ অ্যাম্পিয়ার ফাস্ট-অ্যাক্টিং কাচের ফিউজ",
            nameEn = "1A Fast-Acting Glass Cartridge Fuse",
            category = ComponentCategory.PROTECTION,
            defaultVal = 1.0f,
            unit = "A",
            specs = "Current Rating: 1.0A • Voltage: 250VAC • Breaking Capacity: 35A",
            datasheetSummary = "শর্ট সার্কিট ও অতিরিক্ত কারেন্ট থেকে সংবেদনশীল সার্কিট রক্ষা করতে স্বয়ংক্রিয়ভাবে বিচ্ছিন্ন হয়।",
            packageType = "5x20mm"
        ),
        OnlineComponentItem(
            id = "motor_dc_toy",
            type = ComponentType.DC_MOTOR,
            partNumber = "TT-130",
            manufacturer = "Mabuchi Motor",
            nameBn = "ডিসি হবি গিয়ার ও কুলিং মোটর",
            nameEn = "130-Size Hobby DC Motor",
            category = ComponentCategory.OUTPUT,
            defaultVal = 1200.0f,
            unit = "RPM",
            specs = "Operating Voltage: 3V–9V • No-load Speed: 12000 RPM • Stall Current: 1.2A",
            datasheetSummary = "ইলেকট্রিক্যাল শক্তিকে ঘূর্ণনশীল মেকানিক্যাল শক্তিতে রূপান্তরকারী মোটর।",
            packageType = "Motor Body"
        ),
        OnlineComponentItem(
            id = "solar_cell_5v",
            type = ComponentType.SOLAR_PANEL,
            partNumber = "SC-5050",
            manufacturer = "SunPower / Adafruit",
            nameBn = "৫V ১০০mA মিনি ফটোভোলটাইক সোলার প্যানেল",
            nameEn = "5V 100mA Monocrystalline Solar Panel",
            category = ComponentCategory.POWER,
            defaultVal = 5.0f,
            unit = "V",
            specs = "Voc: 6.0V • Vmp: 5.0V • Imp: 100mA • Efficiency: 19%",
            datasheetSummary = "সূর্যের আলোকে পরিবেশবান্ধব বিদ্যুৎ শক্তিতে রূপান্তরকারী সোলার সেল।",
            packageType = "Panel 50x50mm"
        )
    )
}
