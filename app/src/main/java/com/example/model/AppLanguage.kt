package com.example.model

enum class AppLanguage {
    BANGLA,
    ENGLISH
}

object StringsHelper {
    fun get(language: AppLanguage, bn: String, en: String): String {
        return if (language == AppLanguage.BANGLA) bn else en
    }
}
