package com.codeviewer.app.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration

private val URL_REGEX = Regex("""https?://[^\s"'`)\]>}]+""")

/** Finds bare http/https URLs in [text]. */
fun findUrls(text: String): Sequence<MatchResult> = URL_REGEX.findAll(text)

/**
 * Returns a copy of this AnnotatedString with every bare URL turned into a
 * clickable link. [Text] handles the click through LocalUriHandler.
 */
fun AnnotatedString.withDetectedLinks(linkColor: Color): AnnotatedString {
    val matches = URL_REGEX.findAll(this.text).toList()
    if (matches.isEmpty()) return this

    val builder = AnnotatedString.Builder(this)
    val linkStyles = TextLinkStyles(
        style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline)
    )
    for (match in matches) {
        builder.addLink(
            LinkAnnotation.Url(match.value, linkStyles),
            match.range.first,
            match.range.last + 1
        )
    }
    return builder.toAnnotatedString()
}
