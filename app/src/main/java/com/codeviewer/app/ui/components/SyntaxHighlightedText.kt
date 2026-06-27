package com.codeviewer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeviewer.app.syntax.SyntaxHighlighter
import com.codeviewer.app.ui.theme.LocalSyntaxColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun SyntaxHighlightedEditor(
    content: String,
    languageKey: String,
    isEditMode: Boolean,
    onContentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val syntaxColors = LocalSyntaxColors.current
    val highlighter = remember { SyntaxHighlighter() }

    var highlightedText by remember(content, languageKey) {
        mutableStateOf<AnnotatedString?>(null)
    }

    LaunchedEffect(content, languageKey, syntaxColors) {
        delay(100)
        withContext(Dispatchers.Default) {
            val result = highlighter.highlight(content, languageKey, syntaxColors)
            highlightedText = result
        }
    }

    val lines = content.lines()
    val lineCount = lines.size
    val gutterWidth = (lineCount.toString().length * 10 + 24).dp
    val listState = rememberLazyListState()
    val horizontalScroll = rememberScrollState()

    val codeTextStyle = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 13.sp,
        color = syntaxColors.plain
    )

    Row(modifier = modifier.fillMaxSize()) {
        // Line number gutter
        LazyColumn(
            state = listState,
            modifier = Modifier
                .width(gutterWidth)
                .fillMaxHeight()
                .background(syntaxColors.gutterBg)
        ) {
            items(lineCount) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 0.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "${index + 1}",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = syntaxColors.lineNumber,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }

        // Code area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .horizontalScroll(horizontalScroll)
                .padding(horizontal = 8.dp)
        ) {
            if (isEditMode) {
                var textFieldValue by remember(content) {
                    mutableStateOf(TextFieldValue(content))
                }
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        textFieldValue = newValue
                        onContentChange(newValue.text)
                    },
                    textStyle = codeTextStyle,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val displayText = highlightedText ?: AnnotatedString(content)
                LazyColumn {
                    val displayLines = splitAnnotatedStringByLines(displayText)
                    itemsIndexed(displayLines) { _, line ->
                        Text(
                            text = line,
                            style = codeTextStyle.copy(lineHeight = 20.sp),
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

private fun splitAnnotatedStringByLines(text: AnnotatedString): List<AnnotatedString> {
    val rawText = text.text
    val lines = mutableListOf<AnnotatedString>()
    var lineStart = 0

    for (i in rawText.indices) {
        if (rawText[i] == '\n') {
            lines.add(text.subSequence(lineStart, i))
            lineStart = i + 1
        }
    }
    if (lineStart <= rawText.length) {
        lines.add(text.subSequence(lineStart, rawText.length))
    }

    return lines
}
