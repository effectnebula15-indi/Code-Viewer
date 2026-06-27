package com.codeviewer.app.syntax.languages

import com.codeviewer.app.syntax.LanguageDefinition
import com.codeviewer.app.syntax.TokenRule
import com.codeviewer.app.syntax.TokenType

class JavaLanguage : LanguageDefinition {
    override val name = "Java"
    override val extensions = listOf("java")
    override val rules = listOf(
        TokenRule(Regex("//.*"), TokenType.COMMENT),
        TokenRule(Regex("/\\*[\\s\\S]*?\\*/"), TokenType.COMMENT),
        TokenRule(Regex("\"(?:[^\"\\\\]|\\\\.)*\""), TokenType.STRING),
        TokenRule(Regex("'(?:[^'\\\\]|\\\\.)*'"), TokenType.STRING),
        TokenRule(Regex("@\\w+"), TokenType.ANNOTATION),
        TokenRule(Regex("\\b\\d+\\.?\\d*[fFdDlL]?\\b"), TokenType.NUMBER),
        TokenRule(Regex("\\b0[xX][0-9a-fA-F]+\\b"), TokenType.NUMBER),
        TokenRule(Regex("\\b(?:abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while|true|false|null|var|record|sealed|permits|yield)\\b"), TokenType.KEYWORD),
        TokenRule(Regex("\\b[A-Z][a-zA-Z0-9]*\\b"), TokenType.TYPE),
        TokenRule(Regex("\\b[a-z][a-zA-Z0-9]*(?=\\s*\\()"), TokenType.FUNCTION)
    )
}
