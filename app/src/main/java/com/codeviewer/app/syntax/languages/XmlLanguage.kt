package com.codeviewer.app.syntax.languages

import com.codeviewer.app.syntax.LanguageDefinition
import com.codeviewer.app.syntax.TokenRule
import com.codeviewer.app.syntax.TokenType

class XmlLanguage : LanguageDefinition {
    override val name = "XML"
    override val extensions = listOf("xml", "svg")
    override val rules = listOf(
        TokenRule(Regex("<!--[\\s\\S]*?-->"), TokenType.COMMENT),
        TokenRule(Regex("<\\?[\\s\\S]*?\\?>"), TokenType.KEYWORD),
        TokenRule(Regex("\"(?:[^\"\\\\]|\\\\.)*\""), TokenType.STRING),
        TokenRule(Regex("'(?:[^'\\\\]|\\\\.)*'"), TokenType.STRING),
        TokenRule(Regex("</?[\\w:.-]+"), TokenType.TAG),
        TokenRule(Regex("/?>"), TokenType.TAG),
        TokenRule(Regex("\\b[\\w:.-]+(?=\\s*=)"), TokenType.ATTRIBUTE)
    )
}
