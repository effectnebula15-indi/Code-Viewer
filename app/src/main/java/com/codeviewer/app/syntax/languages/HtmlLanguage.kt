package com.codeviewer.app.syntax.languages

import com.codeviewer.app.syntax.LanguageDefinition
import com.codeviewer.app.syntax.TokenRule
import com.codeviewer.app.syntax.TokenType

class HtmlLanguage : LanguageDefinition {
    override val name = "HTML"
    override val extensions = listOf("html", "htm")
    override val rules = listOf(
        TokenRule(Regex("<!--[\\s\\S]*?-->"), TokenType.COMMENT),
        TokenRule(Regex("\"(?:[^\"\\\\]|\\\\.)*\""), TokenType.STRING),
        TokenRule(Regex("'(?:[^'\\\\]|\\\\.)*'"), TokenType.STRING),
        TokenRule(Regex("</?\\w+"), TokenType.TAG),
        TokenRule(Regex("/?>"), TokenType.TAG),
        TokenRule(Regex("\\b[a-z][a-z-]*(?=\\s*=)"), TokenType.ATTRIBUTE),
        TokenRule(Regex("&\\w+;"), TokenType.KEYWORD)
    )
}
