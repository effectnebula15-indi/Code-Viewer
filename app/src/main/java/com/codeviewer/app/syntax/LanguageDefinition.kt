package com.codeviewer.app.syntax

data class TokenRule(
    val pattern: Regex,
    val tokenType: TokenType
)

interface LanguageDefinition {
    val name: String
    val extensions: List<String>
    val rules: List<TokenRule>
}
