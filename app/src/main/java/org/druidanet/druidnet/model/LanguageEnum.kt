package org.druidanet.druidnet.model

import java.util.Locale

enum class LanguageEnum (val abbr: String, val displayLanguage: String, val i18n: String, val locale: Locale) {
    CASTELLANO(
        abbr = "CAST",
        displayLanguage = "Castellano",
        i18n = "es",
        locale = Locale.Builder().setLanguage("es").setRegion("ES").build()
    ),
    CATALAN (
        abbr = "CAT",
        displayLanguage = "Català",
        i18n = "ca",
        locale = Locale.Builder().setLanguage("ca").build()
    ),
    EUSKERA (
        abbr = "EUS",
        displayLanguage = "Euskera",
        i18n = "eu",
        locale = Locale.Builder().setLanguage("eu").build()
    ),
    GALLEGO (
        abbr = "GAL",
        displayLanguage = "Galego",
        i18n = "gl",
        locale = Locale.Builder().setLanguage("gl").build()
    ),
    ASTURIANO (
        abbr = "AST",
        displayLanguage = "Asturianu",
        i18n = "",
        locale = Locale.Builder().setLanguage("ast").build()
    ),
    LATIN (
        abbr = "LAT",
        displayLanguage = "Nombre Científico",
        i18n = "la",
        locale = Locale.Builder().setLanguage("la").build()
    ),
}
