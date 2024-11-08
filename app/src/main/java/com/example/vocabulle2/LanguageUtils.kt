package com.example.vocabulle2

class LanguageUtils(iso: String) {
    companion object {
        fun getLanguage(isoCode: String): String {
            if (isoCode == "NL") {
                return "Néerlandais"
            } else {
                return isoCode
            }
        }
    }
}