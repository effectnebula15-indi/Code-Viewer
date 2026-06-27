package com.codeviewer.app.syntax.languages

import com.codeviewer.app.syntax.LanguageDefinition
import com.codeviewer.app.syntax.TokenRule
import com.codeviewer.app.syntax.TokenType

class JavaScriptLanguage : LanguageDefinition {
    override val name = "JavaScript"
    override val extensions = listOf("js", "jsx")
    override val rules = listOf(
        TokenRule(Regex("//.*"), TokenType.COMMENT),
        TokenRule(Regex("/\\*[\\s\\S]*?\\*/"), TokenType.COMMENT),
        TokenRule(Regex("`(?:[^`\\\\]|\\\\.)*`"), TokenType.STRING),
        TokenRule(Regex("\"(?:[^\"\\\\]|\\\\.)*\""), TokenType.STRING),
        TokenRule(Regex("'(?:[^'\\\\]|\\\\.)*'"), TokenType.STRING),
        TokenRule(Regex("\\b\\d+\\.?\\d*(?:[eE][+-]?\\d+)?\\b"), TokenType.NUMBER),
        TokenRule(Regex("\\b0[xX][0-9a-fA-F]+\\b"), TokenType.NUMBER),
        TokenRule(Regex("\\b(?:break|case|catch|class|const|continue|debugger|default|delete|do|else|export|extends|finally|for|from|function|if|import|in|instanceof|let|new|of|return|super|switch|this|throw|try|typeof|var|void|while|with|yield|async|await|static|get|set|true|false|null|undefined|NaN|Infinity)\\b"), TokenType.KEYWORD),
        TokenRule(Regex("\\b[A-Z][a-zA-Z0-9]*\\b"), TokenType.TYPE),
        TokenRule(Regex("\\b[a-z_$][a-zA-Z0-9_$]*(?=\\s*\\()"), TokenType.FUNCTION)
    )
}
