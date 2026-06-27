package com.codeviewer.app.syntax

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.codeviewer.app.syntax.languages.*
import com.codeviewer.app.ui.theme.SyntaxColors

class SyntaxHighlighter {
    private val languages: Map<String, LanguageDefinition> = buildMap {
        listOf(
            KotlinLanguage(), JavaLanguage(), PythonLanguage(),
            JavaScriptLanguage(), TypeScriptLanguage(),
            HtmlLanguage(), XmlLanguage(), JsonLanguage(),
            CssLanguage(), GenericLanguage()
        ).forEach { lang ->
            lang.extensions.forEach { ext -> put(ext, lang) }
        }
    }

    // Friendly language names map to the extension keys used above.
    private val aliases = mapOf(
        "kotlin" to "kt",
        "python" to "py",
        "javascript" to "js",
        "typescript" to "ts",
        "markdown" to "md",
        "shell" to "sh",
        "ruby" to "rb",
        "rust" to "rs",
        "cpp" to "c"
    )

    fun highlight(code: String, languageKey: String, colors: SyntaxColors): AnnotatedString {
        val key = languageKey.lowercase()
        val lang = languages[key] ?: languages[aliases[key]] ?: GenericLanguage()
        val builder = AnnotatedString.Builder(code)

        builder.addStyle(SpanStyle(color = colors.plain), 0, code.length)

        val applied = BooleanArray(code.length)

        for (rule in lang.rules) {
            try {
                rule.pattern.findAll(code).forEach { match ->
                    val start = match.range.first
                    val end = match.range.last + 1
                    if (end <= code.length) {
                        var canApply = true
                        for (i in start until end) {
                            if (applied[i]) {
                                canApply = false
                                break
                            }
                        }
                        if (canApply) {
                            val style = spanStyleFor(rule.tokenType, colors)
                            builder.addStyle(style, start, end)
                            for (i in start until end) {
                                applied[i] = true
                            }
                        }
                    }
                }
            } catch (_: Exception) {
                // skip problematic patterns
            }
        }

        return builder.toAnnotatedString()
    }

    private fun spanStyleFor(tokenType: TokenType, colors: SyntaxColors): SpanStyle {
        return when (tokenType) {
            TokenType.KEYWORD -> SpanStyle(color = colors.keyword, fontWeight = FontWeight.Bold)
            TokenType.STRING -> SpanStyle(color = colors.string)
            TokenType.COMMENT -> SpanStyle(color = colors.comment, fontStyle = FontStyle.Italic)
            TokenType.NUMBER -> SpanStyle(color = colors.number)
            TokenType.TYPE -> SpanStyle(color = colors.type)
            TokenType.FUNCTION -> SpanStyle(color = colors.function)
            TokenType.OPERATOR -> SpanStyle(color = colors.operator)
            TokenType.ANNOTATION -> SpanStyle(color = colors.annotation)
            TokenType.TAG -> SpanStyle(color = colors.tag, fontWeight = FontWeight.Bold)
            TokenType.ATTRIBUTE -> SpanStyle(color = colors.attribute)
            TokenType.PUNCTUATION -> SpanStyle(color = colors.operator)
            TokenType.PLAIN -> SpanStyle(color = colors.plain)
        }
    }

    fun getLanguageKey(extension: String): String = extension.lowercase()
}
