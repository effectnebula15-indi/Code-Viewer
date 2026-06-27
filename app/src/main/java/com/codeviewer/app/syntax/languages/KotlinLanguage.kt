package com.codeviewer.app.syntax.languages

import com.codeviewer.app.syntax.LanguageDefinition
import com.codeviewer.app.syntax.TokenRule
import com.codeviewer.app.syntax.TokenType

class KotlinLanguage : LanguageDefinition {
    override val name = "Kotlin"
    override val extensions = listOf("kt", "kts")
    override val rules = listOf(
        TokenRule(Regex("//.*"), TokenType.COMMENT),
        TokenRule(Regex("/\\*[\\s\\S]*?\\*/"), TokenType.COMMENT),
        TokenRule(Regex("\"\"\"[\\s\\S]*?\"\"\""), TokenType.STRING),
        TokenRule(Regex("\"(?:[^\"\\\\]|\\\\.)*\""), TokenType.STRING),
        TokenRule(Regex("'(?:[^'\\\\]|\\\\.)*'"), TokenType.STRING),
        TokenRule(Regex("@\\w+"), TokenType.ANNOTATION),
        TokenRule(Regex("\\b\\d+\\.?\\d*[fFdDlL]?\\b"), TokenType.NUMBER),
        TokenRule(Regex("\\b0[xX][0-9a-fA-F]+\\b"), TokenType.NUMBER),
        TokenRule(Regex("\\b(?:fun|val|var|class|interface|object|package|import|return|if|else|when|for|while|do|break|continue|throw|try|catch|finally|is|as|in|out|by|constructor|init|companion|data|sealed|enum|abstract|open|override|private|protected|public|internal|suspend|inline|crossinline|noinline|reified|typealias|where|true|false|null|this|super|it|lateinit|const|tailrec|operator|infix|annotation|vararg|actual|expect)\\b"), TokenType.KEYWORD),
        TokenRule(Regex("\\b[A-Z][a-zA-Z0-9]*\\b"), TokenType.TYPE),
        TokenRule(Regex("\\b[a-z][a-zA-Z0-9]*(?=\\s*\\()"), TokenType.FUNCTION)
    )
}
