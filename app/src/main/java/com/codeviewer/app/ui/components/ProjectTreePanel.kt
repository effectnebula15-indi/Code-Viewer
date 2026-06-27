package com.codeviewer.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.codeviewer.app.data.FileRepository
import com.codeviewer.app.ui.theme.LocalIdeColors
import java.io.File

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjectTreePanel(
    rootPath: String,
    projectName: String,
    expanded: Set<String>,
    activeFilePath: String?,
    refreshKey: Int,
    onToggleFolder: (String) -> Unit,
    onOpenFile: (String) -> Unit,
    onCreateFile: (parentDir: String) -> Unit,
    onCreateFolder: (parentDir: String) -> Unit,
    onDelete: (path: String) -> Unit,
    modifier: Modifier = Modifier,
    contentPaddingBottom: Dp = 0.dp
) {
    val ide = LocalIdeColors.current
    val repo = remember { FileRepository() }
    val nodes = remember(rootPath, expanded, refreshKey) { buildVisibleNodes(rootPath, expanded, repo) }
    var menuPath by remember { mutableStateOf<String?>(null) }
    var headerMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier.background(ide.panelBg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = contentPaddingBottom)
        ) {
            // Project root header with a create menu.
            item {
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .padding(start = 12.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = projectName.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = ide.mutedText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .combinedClickable(onClick = { headerMenu = true }),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.NoteAdd,
                                contentDescription = "New",
                                modifier = Modifier.size(18.dp),
                                tint = ide.mutedText
                            )
                        }
                    }
                    DropdownMenu(expanded = headerMenu, onDismissRequest = { headerMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("New file") },
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.NoteAdd, null) },
                            onClick = { headerMenu = false; onCreateFile(rootPath) }
                        )
                        DropdownMenuItem(
                            text = { Text("New folder") },
                            leadingIcon = { Icon(Icons.Filled.CreateNewFolder, null) },
                            onClick = { headerMenu = false; onCreateFolder(rootPath) }
                        )
                    }
                }
            }

            items(nodes, key = { it.path }) { node ->
                val selected = node.path == activeFilePath
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .then(
                                if (selected) Modifier.background(ide.treeSelectionBg)
                                else Modifier
                            )
                            .combinedClickable(
                                onClick = {
                                    if (node.isDirectory) onToggleFolder(node.path)
                                    else onOpenFile(node.path)
                                },
                                onLongClick = { menuPath = node.path }
                            )
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

                    DropdownMenu(
                        expanded = menuPath == node.path,
                        onDismissRequest = { menuPath = null }
                    ) {
                        if (node.isDirectory) {
                            DropdownMenuItem(
                                text = { Text("New file") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.NoteAdd, null) },
                                onClick = { menuPath = null; onCreateFile(node.path) }
                            )
                            DropdownMenuItem(
                                text = { Text("New folder") },
                                leadingIcon = { Icon(Icons.Filled.CreateNewFolder, null) },
                                onClick = { menuPath = null; onCreateFolder(node.path) }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("New file") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.NoteAdd, null) },
                                onClick = {
                                    menuPath = null
                                    onCreateFile(File(node.path).parent ?: rootPath)
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            leadingIcon = {
                                Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error)
                            },
                            onClick = { menuPath = null; onDelete(node.path) }
                        )
                    }
                }
            }
        }
    }
}
