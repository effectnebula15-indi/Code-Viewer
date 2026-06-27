package com.codeviewer.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.codeviewer.app.data.FileRepository
import com.codeviewer.app.ui.theme.LocalIdeColors

private data class TreeNode(
    val path: String,
    val name: String,
    val depth: Int,
    val isDirectory: Boolean,
    val isExpanded: Boolean
)

private fun buildVisibleNodes(
    rootPath: String,
    expanded: Set<String>,
    repo: FileRepository
): List<TreeNode> {
    val result = mutableListOf<TreeNode>()
    fun walk(path: String, depth: Int) {
        for (item in repo.listDirectory(path)) {
            val isExp = item.isDirectory && item.path in expanded
            result.add(TreeNode(item.path, item.name, depth, item.isDirectory, isExp))
            if (isExp) walk(item.path, depth + 1)
        }
    }
    walk(rootPath, 0)
    return result
}

@Composable
fun ProjectTreePanel(
    rootPath: String,
    projectName: String,
    expanded: Set<String>,
    activeFilePath: String?,
    onToggleFolder: (String) -> Unit,
    onOpenFile: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPaddingBottom: Dp = 0.dp
) {
    val ide = LocalIdeColors.current
    val repo = remember { FileRepository() }
    val nodes = remember(rootPath, expanded) { buildVisibleNodes(rootPath, expanded, repo) }

    Box(modifier = modifier.background(ide.panelBg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = contentPaddingBottom)
        ) {
            // Project root header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = projectName.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = ide.mutedText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            items(nodes, key = { it.path }) { node ->
                val selected = node.path == activeFilePath
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .then(
                            if (selected) Modifier.background(ide.treeSelectionBg)
                            else Modifier
                        )
                        .clickable {
                            if (node.isDirectory) onToggleFolder(node.path)
                            else onOpenFile(node.path)
                        }
                        .padding(start = (8 + node.depth * 14).dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (node.isDirectory) {
                        val rotation by animateFloatAsState(
                            targetValue = if (node.isExpanded) 90f else 0f,
                            label = "chevron"
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .rotate(rotation),
                            tint = ide.mutedText
                        )
                        Spacer(Modifier.width(2.dp))
                        Icon(
                            imageVector = if (node.isExpanded) Icons.Filled.FolderOpen else Icons.Filled.Folder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = ide.accent
                        )
                        Spacer(Modifier.width(8.dp))
                    } else {
                        Spacer(Modifier.width(18.dp))
                        FileTypeBadge(node.name, size = 16.dp)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        text = node.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
