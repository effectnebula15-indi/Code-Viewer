package com.codeviewer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeviewer.app.syntax.SyntaxHighlighter
import com.codeviewer.app.ui.theme.Inter
import com.codeviewer.app.ui.theme.JetBrainsMono
import com.codeviewer.app.ui.theme.LocalIdeColors
import com.codeviewer.app.ui.theme.LocalSyntaxColors
import com.codeviewer.app.util.withDetectedLinks

// ---------------------------------------------------------------------------
// Block model + parser
// ---------------------------------------------------------------------------
private sealed interface MdBlock {
    data class Heading(val level: Int, val text: String) : MdBlock
    data class Paragraph(val text: String) : MdBlock
    data class Bullet(val text: String, val number: Int?) : MdBlock
    data class Quote(val text: String) : MdBlock
    data class CodeBlock(val lang: String, val code: String) : MdBlock
    data object Rule : MdBlock
}

private val headingRegex = Regex("^(#{1,6})\\s+(.*)")
private val unorderedRegex = Regex("^[-*+]\\s+(.*)")
private val orderedRegex = Regex("^(\\d+)[.)]\\s+(.*)")

private fun parseMarkdown(src: String): List<MdBlock> {
    val out = mutableListOf<MdBlock>()
    val lines = src.replace("\r\n", "\n").split("\n")
    var i = 0
    val para = StringBuilder()

    fun flushPara() {
        if (para.isNotBlank()) out.add(MdBlock.Paragraph(para.trim().toString()))
        para.setLength(0)
    }

    while (i < lines.size) {
        val raw = lines[i]
        val trimmed = raw.trim()

        if (trimmed.startsWith("```")) {
            flushPara()
            val lang = trimmed.removePrefix("```").trim()
            val sb = StringBuilder()
            i++
            while (i < lines.size && !lines[i].trim().startsWith("```")) {
                sb.append(lines[i]).append('\n')
                i++
            }
            i++ // skip closing fence
            out.add(MdBlock.CodeBlock(lang, sb.toString().trimEnd('\n')))
            continue
        }

        if (trimmed.isEmpty()) {
            flushPara(); i++; continue
        }

        val heading = headingRegex.find(trimmed)
        if (heading != null) {
            flushPara()
            out.add(MdBlock.Heading(heading.groupValues[1].length, heading.groupValues[2].trim()))
            i++; continue
        }

        if (trimmed == "---" || trimmed == "***" || trimmed == "___") {
            flushPara(); out.add(MdBlock.Rule); i++; continue
        }

        if (trimmed.startsWith(">")) {
            flushPara(); out.add(MdBlock.Quote(trimmed.removePrefix(">").trim())); i++; continue
        }

        val unordered = unorderedRegex.find(trimmed)
        if (unordered != null) {
            flushPara(); out.add(MdBlock.Bullet(unordered.groupValues[1], null)); i++; continue
        }

        val ordered = orderedRegex.find(trimmed)
        if (ordered != null) {
            flushPara()
            out.add(MdBlock.Bullet(ordered.groupValues[2], ordered.groupValues[1].toIntOrNull() ?: 1))
            i++; continue
        }

        if (para.isNotEmpty()) para.append(' ')
        para.append(trimmed)
        i++
    }
    flushPara()
    return out
}

// ---------------------------------------------------------------------------
// Inline parser (bold / italic / code / links)
// ---------------------------------------------------------------------------
private fun parseInline(
    text: String,
    codeBg: Color,
    linkColor: Color,
    baseColor: Color
): AnnotatedString {
    val b = AnnotatedString.Builder()
    var i = 0
    val n = text.length
    while (i < n) {
        val c = text[i]

        // inline code `...`
        if (c == '`') {
            val end = text.indexOf('`', i + 1)
            if (end > i) {
                val start = b.length
                b.append(text.substring(i + 1, end))
                b.addStyle(
                    SpanStyle(fontFamily = JetBrainsMono, background = codeBg, fontSize = 13.sp),
                    start, b.length
                )
                i = end + 1; continue
            }
        }

        // bold **...** or __...__
        if ((c == '*' && i + 1 < n && text[i + 1] == '*') ||
            (c == '_' && i + 1 < n && text[i + 1] == '_')
        ) {
            val marker = text.substring(i, i + 2)
            val end = text.indexOf(marker, i + 2)
            if (end > i) {
                val start = b.length
                b.append(text.substring(i + 2, end))
                b.addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, b.length)
                i = end + 2; continue
            }
        }

        // italic *...* or _..._
        if (c == '*' || c == '_') {
            val end = text.indexOf(c, i + 1)
            if (end > i) {
                val start = b.length
                b.append(text.substring(i + 1, end))
                b.addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, b.length)
                i = end + 1; continue
            }
        }

        // link [label](url)
        if (c == '[') {
            val close = text.indexOf(']', i + 1)
            if (close > i && close + 1 < n && text[close + 1] == '(') {
                val parenClose = text.indexOf(')', close + 2)
                if (parenClose > close) {
                    val label = text.substring(i + 1, close)
                    val url = text.substring(close + 2, parenClose)
                    val start = b.length
                    b.append(label)
                    b.addStyle(
                        SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
                        start, b.length
                    )
                    b.addLink(LinkAnnotation.Url(url), start, b.length)
                    i = parenClose + 1; continue
                }
            }
        }

        b.append(c)
        i++
    }
    b.addStyle(SpanStyle(color = baseColor), 0, b.length)
    return b.toAnnotatedString().withDetectedLinks(linkColor)
}

// ---------------------------------------------------------------------------
// Rendering
// ---------------------------------------------------------------------------
@Composable
fun MarkdownView(
    content: String,
    modifier: Modifier = Modifier,
    topInset: Dp = 0.dp,
    bottomInset: Dp = 0.dp
) {
    val ide = LocalIdeColors.current
    val syntax = LocalSyntaxColors.current
    val onSurface = MaterialTheme.colorScheme.onSurface
    val codeBg = MaterialTheme.colorScheme.surfaceVariant

    val blocks = remember(content) { parseMarkdown(content) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ide.editorBg),
        contentPadding = PaddingValues(
            start = 20.dp, end = 20.dp, top = topInset + 12.dp, bottom = bottomInset + 24.dp
        )
    ) {
        items(blocks) { block ->
            when (block) {
                is MdBlock.Heading -> {
                    val size = when (block.level) {
                        1 -> 27.sp; 2 -> 22.sp; 3 -> 19.sp; 4 -> 17.sp; 5 -> 15.sp; else -> 14.sp
                    }
                    Text(
                        text = parseInline(block.text, codeBg, ide.accent, onSurface),
                        style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.Bold,
                            fontSize = size,
                            color = onSurface
                        ),
                        modifier = Modifier.padding(top = if (block.level <= 2) 18.dp else 12.dp, bottom = 6.dp)
                    )
                    if (block.level == 1) {
                        HorizontalDivider(color = ide.border, modifier = Modifier.padding(bottom = 6.dp))
                    }
                }

                is MdBlock.Paragraph -> {
                    Text(
                        text = parseInline(block.text, codeBg, ide.accent, onSurface),
                        style = TextStyle(fontFamily = Inter, fontSize = 15.sp, lineHeight = 23.sp, color = onSurface),
                        modifier = Modifier.padding(vertical = 5.dp)
                    )
                }

                is MdBlock.Bullet -> {
                    Row(modifier = Modifier.padding(vertical = 3.dp, horizontal = 4.dp)) {
                        Text(
                            text = if (block.number != null) "${block.number}." else "•",
                            style = TextStyle(fontFamily = Inter, fontSize = 15.sp, color = ide.accent),
                            modifier = Modifier.width(if (block.number != null) 24.dp else 18.dp)
                        )
                        Text(
                            text = parseInline(block.text, codeBg, ide.accent, onSurface),
                            style = TextStyle(fontFamily = Inter, fontSize = 15.sp, lineHeight = 22.sp, color = onSurface)
                        )
                    }
                }

                is MdBlock.Quote -> {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(24.dp)
                                .background(ide.accent)
                        )
                        Text(
                            text = parseInline(block.text, codeBg, ide.accent, ide.mutedText),
                            style = TextStyle(
                                fontFamily = Inter,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                fontStyle = FontStyle.Italic,
                                color = ide.mutedText
                            ),
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }

                is MdBlock.CodeBlock -> {
                    MarkdownCodeBlock(block.lang, block.code, codeBg, syntax)
                }

                MdBlock.Rule -> {
                    HorizontalDivider(color = ide.border, modifier = Modifier.padding(vertical = 12.dp))
                }
            }
        }
    }
}

@Composable
private fun MarkdownCodeBlock(
    lang: String,
    code: String,
    codeBg: Color,
    syntax: com.codeviewer.app.ui.theme.SyntaxColors
) {
    val highlighter = remember { SyntaxHighlighter() }
    val highlighted = remember(code, lang, syntax) {
        if (lang.isNotEmpty()) highlighter.highlight(code, lang, syntax) else AnnotatedString(code)
    }
    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(codeBg)
    ) {
        Column(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(12.dp)
        ) {
            Text(
                text = highlighted,
                style = TextStyle(
                    fontFamily = JetBrainsMono,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = syntax.plain
                ),
                softWrap = false
            )
        }
    }
}
