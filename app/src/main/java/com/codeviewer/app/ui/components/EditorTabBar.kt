package com.codeviewer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.codeviewer.app.ui.theme.LocalIdeColors

data class EditorTab(
    val path: String,
    val name: String,
    val isModified: Boolean
)

@Composable
fun EditorTabBar(
    tabs: List<EditorTab>,
    activeIndex: Int,
    onSelect: (Int) -> Unit,
    onClose: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val ide = LocalIdeColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(ide.tabInactiveBg)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, tab ->
            val active = index == activeIndex
            Row(
                modifier = Modifier
                    .height(36.dp)
                    .background(if (active) ide.tabActiveBg else ide.tabInactiveBg)
                    .drawBehind {
                        if (active) {
                            drawRect(
                                color = ide.tabAccent,
                                topLeft = Offset(0f, 0f),
                                size = size.copy(height = 2.dp.toPx())
                            )
                        }
                    }
                    .clickable { onSelect(index) }
                    .padding(start = 12.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FileTypeBadge(tab.name, size = 14.dp)
                Text(
                    text = tab.name,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = if (active) {
                        androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                    } else {
                        ide.mutedText
                    }
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .clickable { onClose(index) },
                    contentAlignment = Alignment.Center
                ) {
                    if (tab.isModified) {
                        Icon(
                            imageVector = Icons.Filled.Circle,
                            contentDescription = "Modified",
                            modifier = Modifier.size(8.dp),
                            tint = ide.accent
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(13.dp),
                            tint = ide.mutedText
                        )
                    }
                }
            }
        }
    }
}
