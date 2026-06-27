package com.codeviewer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeviewer.app.ui.theme.JetBrainsMono

private data class Badge(val color: Color, val label: String)

private fun badgeFor(name: String): Badge {
    val ext = name.substringAfterLast('.', "").lowercase()
    return when (ext) {
        "kt", "kts" -> Badge(Color(0xFF8052FF), "K")
        "java" -> Badge(Color(0xFFE05A2B), "J")
        "py" -> Badge(Color(0xFF3C78AA), "Py")
        "js", "jsx", "mjs" -> Badge(Color(0xFFE8C547), "JS")
        "ts", "tsx" -> Badge(Color(0xFF3178C6), "TS")
        "html", "htm" -> Badge(Color(0xFFE34F26), "<>")
        "css", "scss", "less" -> Badge(Color(0xFF2965F1), "#")
        "json" -> Badge(Color(0xFFCBCB41), "{}")
        "xml", "svg" -> Badge(Color(0xFF6CA35A), "</>")
        "md" -> Badge(Color(0xFF42A5F5), "M")
        "gradle" -> Badge(Color(0xFF02303A), "G")
        "c", "h" -> Badge(Color(0xFF5C6BC0), "C")
        "cpp", "cc", "hpp" -> Badge(Color(0xFF5C6BC0), "C+")
        "go" -> Badge(Color(0xFF00ADD8), "Go")
        "rs" -> Badge(Color(0xFFCE714F), "Rs")
        "rb" -> Badge(Color(0xFFCC342D), "Rb")
        "swift" -> Badge(Color(0xFFF05138), "Sw")
        "sh", "bash" -> Badge(Color(0xFF4EAA25), "$")
        "sql" -> Badge(Color(0xFF8E9CA3), "DB")
        "yaml", "yml" -> Badge(Color(0xFF7E8B99), "Y")
        else -> Badge(Color(0xFF7E8B99), "·")
    }
}

@Composable
fun FileTypeBadge(name: String, size: Dp = 16.dp) {
    val badge = badgeFor(name)
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(3.dp))
            .background(badge.color),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = badge.label,
            color = Color.White,
            fontFamily = JetBrainsMono,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.42f).sp,
            maxLines = 1
        )
    }
}
