package com.example.vocabulle2

import com.example.vocabulle2.ui.theme.Language

class IsoCodeList {
    companion object {
        val values = listOf<Language>(
            Language("NL", "\u1F1F1", "Dutch")
        )
        fun toFlagEmoji(countryCodeCaps: String): String {
            // 1. It first checks if the string consists of only 2 characters: ISO 3166-1 alpha-2 two-letter country codes (https://en.wikipedia.org/wiki/Regional_Indicator_Symbol).
            val firstLetter = Character.codePointAt(countryCodeCaps, 0) - 0x41 + 0x1F1E6
            val secondLetter = Character.codePointAt(countryCodeCaps, 1) - 0x41 + 0x1F1E6

            // 2. It then checks if both characters are alphabet
            if (!countryCodeCaps[0].isLetter() || !countryCodeCaps[1].isLetter()) {
                return ""
            }
            return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
        }
    }

}