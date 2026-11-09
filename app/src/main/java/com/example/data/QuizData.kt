package com.example.data

import com.example.model.QuizQuestion

object QuizData {

    val questions = listOf(
        QuizQuestion(
            id = 1,
            categoryBn = "ওহমের সূত্র",
            questionBn = "একটি ১২ ভোল্টের ব্যাটারিতে ৪ ওহমের একটি রোধ যুক্ত করলে সার্কিট দিয়ে কত কারেন্ট প্রবাহিত হবে?",
            questionEn = "If a 4Ω resistor is connected across a 12V battery, how much current flows?",
            optionsBn = listOf("২ অ্যাম্পিয়ার (2A)", "৩ অ্যাম্পিয়ার (3A)", "৪৮ অ্যাম্পিয়ার (48A)", "০.৩৩ অ্যাম্পিয়ার (0.33A)"),
            optionsEn = listOf("2 Amperes (2A)", "3 Amperes (3A)", "48 Amperes (48A)", "0.33 Amperes (0.33A)"),
            correctIndex = 1,
            explanationBn = "ওহমের সূত্র অনুযায়ী: I = V / R = ১২ / ৪ = ৩ অ্যাম্পিয়ার।",
            explanationEn = "By Ohm's Law: I = V / R = 12V / 4Ω = 3A."
        ),
        QuizQuestion(
            id = 2,
            categoryBn = "রোধ ও কালার কোড",
            questionBn = "৪-ব্যান্ড রেজিস্টরের প্রথম তিনটি ব্যান্ড বাদামী, কালো এবং লাল হলে এর মান কত?",
            questionEn = "If the first three bands of a 4-band resistor are Brown, Black, and Red, what is its value?",
            optionsBn = listOf("১০০ ওহম (100Ω)", "১ কিলো-ওহম (1kΩ)", "১০ কিলো-ওহম (10kΩ)", "১২০ ওহম (120Ω)"),
            optionsEn = listOf("100 Ohms (100Ω)", "1 Kilo-ohm (1kΩ)", "10 Kilo-ohms (10kΩ)", "120 Ohms (120Ω)"),
            correctIndex = 1,
            explanationBn = "বাদামী = ১, কালো = ০, লাল = গুণক ১০^২ (১০০)। সুতরাং মান = ১০ × ১০০ = ১,০০০ ওহম বা ১ কিলো-ওহম (1kΩ)।",
            explanationEn = "Brown = 1, Black = 0, Red = multiplier 100. Value = 10 × 100 = 1000Ω = 1kΩ."
        ),
        QuizQuestion(
            id = 3,
            categoryBn = "এলইডি ও সেমিকন্ডাক্টর",
            questionBn = "৯V পাওয়ার সাপ্লাইয়ে ২V ড্রপের একটি লাল এলইডি ১৫mA কারেন্টে জ্বালাতে কত রোধ লাগবে?",
            questionEn = "To operate a 2V red LED at 15mA from a 9V source, what series resistance is required?",
            optionsBn = listOf("৪৭০ ওহম (470Ω)", "৩৩০ ওহম (330Ω)", "১৫০ ওহম (150Ω)", "১ কিলো-ওহম (1kΩ)"),
            optionsEn = listOf("470 Ohms (470Ω)", "330 Ohms (330Ω)", "150 Ohms (150Ω)", "1 Kilo-ohm (1kΩ)"),
            correctIndex = 0,
            explanationBn = "রোধের দুই প্রান্তে ভোল্টেজ = ৯ - ২ = ৭ ভোল্ট। প্রয়োজনীয় R = ৭ / ০.০১৫ = ৪৬৬.৬৭ ওহম। নিকটতম আদর্শ মান ৪৭০ ওহম।",
            explanationEn = "Voltage across resistor = 9 - 2 = 7V. R = 7 / 0.015 = 466.7Ω. Standard nearest value is 470Ω."
        ),
        QuizQuestion(
            id = 4,
            categoryBn = "প্যারালাল সার্কিট",
            questionBn = "দুটি সমান ৬০ ওহম (60Ω) রোধ প্যারালালে যুক্ত করলে তাদের সমতুল্য রোধ কত হবে?",
            questionEn = "What is the equivalent resistance when two identical 60Ω resistors are connected in parallel?",
            optionsBn = listOf("১২০ ওহম (120Ω)", "৩০ ওহম (30Ω)", "৬০ ওহম (60Ω)", "১৫ ওহম (15Ω)"),
            optionsEn = listOf("120 Ohms (120Ω)", "30 Ohms (30Ω)", "60 Ohms (60Ω)", "15 Ohms (15Ω)"),
            correctIndex = 1,
            explanationBn = "দুটি সমান রোধ প্যারালালে যুক্ত থাকলে সমতুল্য রোধ তাদের অর্ধেক হয়ে যায়: ৬০ / ২ = ৩০ ওহম।",
            explanationEn = "When two identical resistors are in parallel, Req = R / 2 = 60 / 2 = 30Ω."
        ),
        QuizQuestion(
            id = 5,
            categoryBn = "ট্রানজিস্টর ল্যাব",
            questionBn = "একটি NPN বাইপোলার ট্রানজিস্টরের তিনটি টার্মিনালের সঠিক নাম কোনটি?",
            questionEn = "What are the correct names for the three terminals of an NPN bipolar transistor?",
            optionsBn = listOf(
                "অ্যানোড, ক্যাথোড, গেট",
                "সোর্স, ড্রেন, গেট",
                "বেস, কালেক্টর, এমিটার",
                "পজিটিভ, নেগেটিভ, নিউট্রাল"
            ),
            optionsEn = listOf(
                "Anode, Cathode, Gate",
                "Source, Drain, Gate",
                "Base, Collector, Emitter",
                "Positive, Negative, Neutral"
            ),
            correctIndex = 2,
            explanationBn = "BJT ট্রানজিস্টরের তিনটি টার্মিনাল হলো বেস (Base), কালেক্টর (Collector) এবং এমিটার (Emitter)।",
            explanationEn = "The terminals of a BJT are Base, Collector, and Emitter."
        ),
        QuizQuestion(
            id = 6,
            categoryBn = "ক্যাপাসিটর ও টাইমিং",
            questionBn = "১০kΩ রোধ এবং ১০০µF ক্যাপাসিটরের একটি সার্কিটের টাইম কনস্ট্যান্ট (τ) কত সেকেন্ড?",
            questionEn = "What is the time constant (τ) of a circuit with a 10kΩ resistor and a 100µF capacitor?",
            optionsBn = listOf("০.১ সেকেন্ড", "১.০ সেকেন্ড", "১০ সেকেন্ড", "১০০ সেকেন্ড"),
            optionsEn = listOf("0.1 seconds", "1.0 seconds", "10 seconds", "100 seconds"),
            correctIndex = 1,
            explanationBn = "টাইম কনস্ট্যান্ট τ = R × C = ১০,০০০ ওহম × ০.০০০১ ফ্যারাড = ১.০ সেকেন্ড।",
            explanationEn = "Time constant τ = R × C = 10,000Ω × 0.0001F = 1.0 second."
        )
    )
}
