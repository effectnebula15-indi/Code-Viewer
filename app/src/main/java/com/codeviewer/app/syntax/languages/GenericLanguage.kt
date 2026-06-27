package com.codeviewer.app.syntax.languages

import com.codeviewer.app.syntax.LanguageDefinition
import com.codeviewer.app.syntax.TokenRule
import com.codeviewer.app.syntax.TokenType

class GenericLanguage : LanguageDefinition {
    override val name = "Generic"
    override val extensions = listOf("txt", "log", "md", "cfg", "conf", "ini", "properties", "env", "gitignore")
    override val rules = listOf(
        TokenRule(Regex("//.*"), TokenType.COMMENT),
        TokenRule(Regex("#.*"), TokenType.COMMENT),
        TokenRule(Regex("\"(?:[^\"\\\\]|\\\\.)*\""), TokenType.STRING),
        TokenRule(Regex("'(?:[^'\\\\]|\\\\.)*'"), TokenType.STRING),
        TokenRule(Regex("\\b\\d+\\.?\\d*\\b"), TokenType.NUMBER),
        TokenRule(Regex("\\b(?:true|false|yes|no|null|none|on|off)\\b", RegexOption.IGNORE_CASE), TokenType.KEYWORD)
    )
}
