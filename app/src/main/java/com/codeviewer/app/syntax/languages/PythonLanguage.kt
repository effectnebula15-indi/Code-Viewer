package com.codeviewer.app.syntax.languages

import com.codeviewer.app.syntax.LanguageDefinition
import com.codeviewer.app.syntax.TokenRule
import com.codeviewer.app.syntax.TokenType

class PythonLanguage : LanguageDefinition {
    override val name = "Python"
    override val extensions = listOf("py")
    override val rules = listOf(
        TokenRule(Regex("#.*"), TokenType.COMMENT),
        TokenRule(Regex("\"\"\"[\\s\\S]*?\"\"\""), TokenType.STRING),
        TokenRule(Regex("'''[\\s\\S]*?'''"), TokenType.STRING),
        TokenRule(Regex("\"(?:[^\"\\\\]|\\\\.)*\""), TokenType.STRING),
        TokenRule(Regex("'(?:[^'\\\\]|\\\\.)*'"), TokenType.STRING),
        TokenRule(Regex("@\\w+"), TokenType.ANNOTATION),
        TokenRule(Regex("\\b\\d+\\.?\\d*[jJ]?\\b"), TokenType.NUMBER),
        TokenRule(Regex("\\b0[xXoObB][0-9a-fA-F]+\\b"), TokenType.NUMBER),
        TokenRule(Regex("\\b(?:False|None|True|and|as|assert|async|await|break|class|continue|def|del|elif|else|except|finally|for|from|global|if|import|in|is|lambda|nonlocal|not|or|pass|raise|return|try|while|with|yield)\\b"), TokenType.KEYWORD),
        TokenRule(Regex("\\b[A-Z][a-zA-Z0-9_]*\\b"), TokenType.TYPE),
        TokenRule(Regex("\\b[a-z_][a-zA-Z0-9_]*(?=\\s*\\()"), TokenType.FUNCTION)
    )
}
