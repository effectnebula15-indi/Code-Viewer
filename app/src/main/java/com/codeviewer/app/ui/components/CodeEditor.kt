package com.codeviewer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeviewer.app.syntax.SyntaxHighlighter
import com.codeviewer.app.ui.theme.JetBrainsMono
import com.codeviewer.app.ui.theme.LocalIdeColors
import com.codeviewer.app.ui.theme.LocalSyntaxColors
import com.codeviewer.app.util.withDetectedLinks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private val CodeFontSize = 13.sp
private val CodeLineHeight = 20.sp
private val CodeLineHeightDp = 20.dp

@Composable
fun CodeEditor(
    content: String,
    languageKey: String,
    isEditMode: Boolean,
    searchQuery: String,
    editorValue: TextFieldValue,
    onEditorValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    topInset: Dp = 0.dp,
    bottomInset: Dp = 0.dp
) {
    val syntaxColors = LocalSyntaxColors.current
    val linkColor = LocalIdeColors.current.accent
    val highlighter = remember { SyntaxHighlighter() }

    val codeStyle = remember(syntaxColors) {
        TextStyle(
            fontFamily = JetBrainsMono,
            fontSize = CodeFontSize,
            lineHeight = CodeLineHeight,
            color = syntaxColors.plain
        )
    }

    // Shared vertical scroll keeps the gutter and code aligned at all times.
    val vScroll = rememberScrollState()
    val hScroll = rememberScrollState()

    val effectiveText = if (isEditMode) editorValue.text else content
    val lineCount = remember(effectiveText) { effectiveText.count { it == '\n' } + 1 }
    val gutterWidth = ((lineCount.toString().length).coerceAtLeast(2) * 9 + 20).dp

    if (isEditMode) {
        EditMode(
            value = editorValue,
            onValueChange = onEditorValueChange,
            codeStyle = codeStyle,
            lineCount = lineCount,
            gutterWidth = gutterWidth,
            vScroll = vScroll,
            hScroll = hScroll,
            topInset = topInset,
            bottomInset = bottomInset,
            modifier = modifier
        )
    } else {
        ViewMode(
            content = content,
            languageKey = languageKey,
            searchQuery = searchQuery,
            linkColor = linkColor,
            highlighter = highlighter,
            codeStyle = codeStyle,
            lineCount = lineCount,
            gutterWidth = gutterWidth,
            vScroll = vScroll,
            hScroll = hScroll,
            topInset = topInset,
            bottomInset = bottomInset,
            modifier = modifier
        )
    }
}

@Composable
private fun ViewMode(
    content: String,
    languageKey: String,
    searchQuery: String,
    linkColor: androidx.compose.ui.graphics.Color,
    highlighter: SyntaxHighlighter,
    codeStyle: TextStyle,
    lineCount: Int,
    gutterWidth: Dp,
    vScroll: androidx.compose.foundation.ScrollState,
    hScroll: androidx.compose.foundation.ScrollState,
    topInset: Dp,
    bottomInset: Dp,
    modifier: Modifier
) {
    val syntaxColors = LocalSyntaxColors.current

    val highlightedLines by produceState(
        initialValue = content.lines().map { AnnotatedString(it) },
        content, languageKey, searchQuery, syntaxColors, linkColor
    ) {
        delay(60)
        value = withContext(Dispatchers.Default) {
            var annotated = highlighter.highlight(content, languageKey, syntaxColors)
            if (searchQuery.length >= 2) {
                annotated = applySearchHighlight(annotated, searchQuery, syntaxColors.selectionBg)
            }
            annotated = annotated.withDetectedLinks(linkColor)
            splitAnnotatedStringByLines(annotated)
        }
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(syntaxColors.gutterBg)
            .verticalScroll(vScroll)
    ) {
        Column(
            modifier = Modifier
                .width(gutterWidth)
                .background(syntaxColors.gutterBg)
        ) {
            Spacer(Modifier.height(topInset))
            for (i in 0 until lineCount) {
                LineNumber(i + 1, codeStyle, syntaxColors.lineNumber)
            }
            Spacer(Modifier.height(bottomInset))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(hScroll)
                .padding(start = 6.dp, end = 16.dp)
        ) {
            Spacer(Modifier.height(topInset))
            highlightedLines.forEach { line ->
                Text(
                    text = line,
                    style = codeStyle,
                    softWrap = false,
                    maxLines = 1,
                    modifier = Modifier.height(CodeLineHeightDp)
                )
            }
            Spacer(Modifier.height(bottomInset))
        }
    }
}

@Composable
private fun EditMode(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    codeStyle: TextStyle,
    lineCount: Int,
    gutterWidth: Dp,
    vScroll: androidx.compose.foundation.ScrollState,
    hScroll: androidx.compose.foundation.ScrollState,
    topInset: Dp,
    bottomInset: Dp,
    modifier: Modifier
) {
    val syntaxColors = LocalSyntaxColors.current

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(syntaxColors.gutterBg)
            .verticalScroll(vScroll)
    ) {
        Column(
            modifier = Modifier
                .width(gutterWidth)
                .background(syntaxColors.gutterBg)
        ) {
            Spacer(Modifier.height(topInset))
            for (i in 0 until lineCount) {
                LineNumber(i + 1, codeStyle, syntaxColors.lineNumber)
            }
            Spacer(Modifier.height(bottomInset))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(hScroll)
                .padding(start = 6.dp, end = 16.dp)
        ) {
            Spacer(Modifier.height(topInset))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = codeStyle,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.width(2400.dp)
            )
            Spacer(Modifier.height(bottomInset))
        }
    }
}

@Composable
private fun LineNumber(number: Int, codeStyle: TextStyle, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .height(CodeLineHeightDp)
            .fillMaxWidth()
            .padding(end = 10.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(text = "$number", style = codeStyle.copy(color = color))
    }
}

private fun applySearchHighlight(
    source: AnnotatedString,
    query: String,
    highlightColor: androidx.compose.ui.graphics.Color
): AnnotatedString {
    val text = source.text
    val builder = AnnotatedString.Builder(source)
    var index = text.indexOf(query, ignoreCase = true)
    while (index >= 0) {
        builder.addStyle(
            SpanStyle(background = highlightColor),
            index,
            index + query.length
        )
        index = text.indexOf(query, index + query.length, ignoreCase = true)
    }
    return builder.toAnnotatedString()
}

private fun splitAnnotatedStringByLines(text: AnnotatedString): List<AnnotatedString> {
    val raw = text.text
    val lines = mutableListOf<AnnotatedString>()
    var lineStart = 0
    for (i in raw.indices) {
        if (raw[i] == '\n') {
            lines.add(text.subSequence(lineStart, i))
            lineStart = i + 1
        }
    }
    lines.add(text.subSequence(lineStart, raw.length))
    return lines
}
