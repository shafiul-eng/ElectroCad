package com.example.data

import com.example.model.TutorialLesson

object TutorialData {

    val lessons = listOf(
        TutorialLesson(
            id = "lesson_1",
            moduleBn = "মডিউল ১",
            moduleEn = "Module 1",
            titleBn = "ওহমের সূত্র ও মৌলিক রাশিমালা",
            titleEn = "Ohm's Law & Core Fundamentals",
            readTimeMin = 4,
            descriptionBn = "ইলেকট্রনিক্স সার্কিটের মূল ভিত্তি হলো ভোল্টেজ (V), কারেন্ট (I) এবং রোধ (R) এর মধ্যকার সম্পর্ক। জর্জ সাইমন ওহম আবিষ্কার করেন যে স্থির তাপমাত্রায় পরিবাহীর দুই প্রান্তের বিভব পার্থক্য তার মধ্য দিয়ে প্রবাহিত কারেন্টের সমানুপাতিক।",
            descriptionEn = "Ohm's Law is the bedrock of electronics: potential difference across a conductor is directly proportional to current flowing through it at constant temperature.",
            formulas = listOf(
                "V = I × R (ভোল্টেজ = কারেন্ট × রোধ)",
                "I = V / R (কারেন্ট = ভোল্টেজ ÷ রোধ)",
                "R = V / I (রোধ = ভোল্টেজ ÷ কারেন্ট)",
                "P = V × I = I² × R (বৈদ্যুতিক ক্ষমতা বা ওয়াট)"
            ),
            practicalTipBn = "ব্যবহারিক টিপ: যদি সার্কিটে ভোল্টেজ ৯V হয় এবং আপনি ২০mA (০.০২A) কারেন্ট চান, তবে প্রয়োজনীয় রোধ R = ৯ / ০.০২ = ৪৫০ ওহম।",
            practicalTipEn = "Pro Tip: If supply is 9V and desired current is 20mA, needed R = 9 / 0.02 = 450Ω.",
            presetIdToLoad = "preset_led"
        ),
        TutorialLesson(
            id = "lesson_2",
            moduleBn = "মডিউল ২",
            moduleEn = "Module 2",
            titleBn = "সিরিজ বনাম প্যারালাল সার্কিট বিশ্লেষণ",
            titleEn = "Series vs. Parallel Circuit Analysis",
            readTimeMin = 5,
            descriptionBn = "সিরিজ সার্কিটে সকল উপাদানের মধ্য দিয়ে একই কারেন্ট প্রবাহিত হয় এবং মোট ভোল্টেজ প্রতিটি উপাদানে বিভক্ত হয়। প্যারালাল সার্কিটে প্রতিটি শাখার দুই প্রান্তে সমান ভোল্টেজ থাকে কিন্তু মোট কারেন্ট শাখাগুলোতে বিভক্ত হয়।",
            descriptionEn = "In series circuits, current remains constant while voltage divides. In parallel circuits, voltage is identical across branches while current divides.",
            formulas = listOf(
                "সিরিজ সমতুল্য রোধ: R_eq = R1 + R2 + R3 + ...",
                "প্যারালাল সমতুল্য রোধ: 1/R_eq = 1/R1 + 1/R2 + ...",
                "দুটি রোধের প্যারালাল শর্টকাট: R_eq = (R1 × R2) / (R1 + R2)"
            ),
            practicalTipBn = "মনে রাখুন: সিরিজ সার্কিটে একটি উপাদান নষ্ট বা সংযোগ বিচ্ছিন্ন হলে পুরো সার্কিট বন্ধ হয়ে যায়। কিন্তু প্যারালাল সার্কিটে অন্য শাখাগুলো সচল থাকে।",
            practicalTipEn = "Note: In series, a single broken link kills the entire loop. In parallel, other paths stay active.",
            presetIdToLoad = "preset_series"
        ),
        TutorialLesson(
            id = "lesson_3",
            moduleBn = "মডিউল ৩",
            moduleEn = "Module 3",
            titleBn = "সেমিকন্ডাক্টর ডায়োড এবং এলইডি প্রোটেকশন",
            titleEn = "Semiconductor Diodes & LED Protection",
            readTimeMin = 4,
            descriptionBn = "পি-এন জংশন ডায়োড একদিকে কারেন্ট প্রবাহিত হতে দেয় (ফরওয়ার্ড বায়াস) এবং বিপরীত দিকে বাধা দেয় (রিভার্স বায়াস)। এলইডি (Light Emitting Diode) ফরওয়ার্ড বায়াসে ফোটন নিঃসরণ করে আলো উৎপন্ন করে।",
            descriptionEn = "Diodes allow unidirectional current flow. LEDs emit light under forward bias when electrons recombine with electron holes.",
            formulas = listOf(
                "ফরওয়ার্ড ভোল্টেজ ড্রপ: Vf ≈ ২.০V (লাল), ৩.২V (নীল/সাদা)",
                "কারেন্ট লিমিটিং রোধ: R_limit = (V_supply - V_f) / I_led",
                "এলইডি ক্ষমতা: P_led = V_f × I_led"
            ),
            practicalTipBn = "সতর্কতা: এলইডিকে কখনো সরাসরি ব্যাটারির সাথে সংযোগ করবেন না! ডায়োডের নিজস্ব রোধ খুব কম থাকায় কারেন্ট মাত্রাতিরিক্ত বেড়ে এলইডি পুড়ে (Burnout) যাবে।",
            practicalTipEn = "Caution: Never connect an LED directly to a battery without a resistor; excessive current will permanently destroy it.",
            presetIdToLoad = "preset_led"
        ),
        TutorialLesson(
            id = "lesson_4",
            moduleBn = "মডিউল ৪",
            moduleEn = "Module 4",
            titleBn = "ট্রানজিস্টর: ইলেকট্রনিক সুইচ ও অ্যামপ্লিফায়ার",
            titleEn = "Transistors: Electronic Switches & Amplifiers",
            readTimeMin = 6,
            descriptionBn = "বাইপোলার জংশন ট্রানজিস্টর (BJT) একটি ত্রি-টার্মিনাল বিশিষ্ট সেমিকন্ডাক্টর ডিভাইস: বেস (B), কালেক্টর (C) এবং এমিটার (E)। বেসে অতি ক্ষুদ্র কারেন্ট নিয়ন্ত্রণ করে কালেক্টর-এমিটারের বিশাল কারেন্ট সুইচিং করা যায়।",
            descriptionEn = "BJTs are 3-terminal devices (Base, Collector, Emitter). A tiny base current controls a significantly larger collector-to-emitter current flow.",
            formulas = listOf(
                "কালেক্টর কারেন্ট: Ic = β (hFE) × Ib",
                "এমিটার কারেন্ট: Ie = Ib + Ic ≈ Ic",
                "স্যাচুরেশন কন্ডিশন: Vce(sat) ≈ ০.২V (ফুল অন সুইচ হিসেবে)"
            ),
            practicalTipBn = "প্রজেক্ট আইডিয়া: এলডিআর (LDR) এবং ট্রানজিস্টর ব্যবহার করে খুব সহজেই অটোমেটিক স্ট্রিট লাইট বা রাতের বাতি তৈরি করা যায়।",
            practicalTipEn = "Project Idea: Combine an LDR and BJT to build an automatic light-activated night switch.",
            presetIdToLoad = "preset_motor_buzzer"
        ),
        TutorialLesson(
            id = "lesson_5",
            moduleBn = "মডিউল ৫",
            moduleEn = "Module 5",
            titleBn = "ক্যাপাসিটর এবং আরসি (RC) ফিল্টারিং",
            titleEn = "Capacitors & RC Time Constant",
            readTimeMin = 5,
            descriptionBn = "ক্যাপাসিটর দুটি পরিবাহী পাতের মাঝে ডাই-ইলেকট্রিক পদার্থ দিয়ে তৈরি যা বৈদ্যুতিক চার্জ সঞ্চয় করে। রোধ এবং ক্যাপাসিটরের সংযোগে তৈরি হয় টাইমিং ও ফিল্টারিং সার্কিট।",
            descriptionEn = "Capacitors store electrostatic energy across conductive plates separated by a dielectric insulator. RC networks govern timing and frequency filtering.",
            formulas = listOf(
                "চার্জ সঞ্চয়: Q = C × V",
                "টাইম কনস্ট্যান্ট (টাউ): τ = R × C (সেকেন্ডে)",
                "পূর্ণ চার্জিং সময়: ৫τ (প্রায় ৯৯.৩% চার্জ)"
            ),
            practicalTipBn = "ব্যবহার: পাওয়ার সাপ্লাইয়ে ডিসি ভোল্টেজকে নিখুঁত মসৃণ করতে ফিল্টার ক্যাপাসিটর অপরিহার্য।",
            practicalTipEn = "Application: Smoothing filter capacitors are mandatory in AC-to-DC power supplies.",
            presetIdToLoad = "preset_pot"
        )
    )
}
