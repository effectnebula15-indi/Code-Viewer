package com.codeviewer.app.syntax.languages

import com.codeviewer.app.syntax.LanguageDefinition
import com.codeviewer.app.syntax.TokenRule
import com.codeviewer.app.syntax.TokenType

class JsonLanguage : LanguageDefinition {
    override val name = "JSON"
    override val extensions = listOf("json")
    override val rules = listOf(
        TokenRule(Regex("\"(?:[^\"\\\\]|\\\\.)*\"(?=\\s*:)"), TokenType.ATTRIBUTE),
        TokenRule(Regex("\"(?:[^\"\\\\]|\\\\.)*\""), TokenType.STRING),
        TokenRule(Regex("-?\\b\\d+\\.?\\d*(?:[eE][+-]?\\d+)?\\b"), TokenType.NUMBER),
        TokenRule(Regex("\\b(?:true|false|null)\\b"), TokenType.KEYWORD)
    )
}
