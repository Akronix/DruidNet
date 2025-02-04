package org.druidanet.druidnet.model

import java.util.Locale

enum class LanguageEnum (val abbr: String, val displayLanguage: String, val locale: Locale) {
    CASTELLANO( abbr = "CAST", displayLanguage = "Castellano", locale = Locale("es", "ES") ),
    CATALAN ( abbr = "CAT", displayLanguage = "Català", locale = Locale("ca")),
    EUSKERA ( abbr = "EUS", displayLanguage = "Euskera", locale = Locale("eu")),
    GALLEGO (abbr = "GAL", displayLanguage = "Galego", locale = Locale("gl")),
    ASTURIANO (abbr = "AST", displayLanguage = "Asturianu", locale = Locale("ast")),
    LATIN (abbr = "LAT", displayLanguage = "Nombre Científico", locale = Locale("la")),
}