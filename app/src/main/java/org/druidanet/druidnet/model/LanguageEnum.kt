package org.druidanet.druidnet.model

import java.util.Locale

enum class LanguageEnum (val abbr: String, val displayLanguage: String, val locale: Locale) {
    CASTELLANO(
        abbr = "CAST",
        displayLanguage = "Castellano",
        locale = Locale.Builder().setLanguage("es").setRegion("ES").build()
    ),
    CATALAN (
        abbr = "CAT",
        displayLanguage = "Català",
        locale = Locale.Builder().setLanguage("ca").build()
    ),
    EUSKERA (
        abbr = "EUS",
        displayLanguage = "Euskera",
        locale = Locale.Builder().setLanguage("eu").build()
    ),
    GALLEGO (
        abbr = "GAL",
        displayLanguage = "Galego",
        locale = Locale.Builder().setLanguage("gl").build()
    ),
    ASTURIANO (
        abbr = "AST",
        displayLanguage = "Asturianu",
        locale = Locale.Builder().setLanguage("ast").build()
    ),
    LATIN (
        abbr = "LAT",
        displayLanguage = "Nombre Científico",
        locale = Locale.Builder().setLanguage("la").build()
    ),
}
