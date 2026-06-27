package com.codeviewer.app.syntax.languages

import com.codeviewer.app.syntax.LanguageDefinition
import com.codeviewer.app.syntax.TokenRule
import com.codeviewer.app.syntax.TokenType

class CssLanguage : LanguageDefinition {
    override val name = "CSS"
    override val extensions = listOf("css", "scss", "less")
    override val rules = listOf(
        TokenRule(Regex("/\\*[\\s\\S]*?\\*/"), TokenType.COMMENT),
        TokenRule(Regex("//.*"), TokenType.COMMENT),
        TokenRule(Regex("\"(?:[^\"\\\\]|\\\\.)*\""), TokenType.STRING),
        TokenRule(Regex("'(?:[^'\\\\]|\\\\.)*'"), TokenType.STRING),
        TokenRule(Regex("#[0-9a-fA-F]{3,8}\\b"), TokenType.NUMBER),
        TokenRule(Regex("-?\\b\\d+\\.?\\d*(?:px|em|rem|vh|vw|%|s|ms|deg|fr)?\\b"), TokenType.NUMBER),
        TokenRule(Regex("@\\w[\\w-]*"), TokenType.ANNOTATION),
        TokenRule(Regex("\\b(?:important|inherit|initial|unset|none|auto|flex|grid|block|inline|relative|absolute|fixed|sticky)\\b"), TokenType.KEYWORD),
        TokenRule(Regex("[.#]\\w[\\w-]*"), TokenType.TYPE),
        TokenRule(Regex("\\b[a-z][a-z-]+(?=\\s*:)"), TokenType.ATTRIBUTE),
        TokenRule(Regex("\\b[a-z][a-z-]*(?=\\s*\\()"), TokenType.FUNCTION)
    )
}
