package com.codeviewer.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.codeviewer.app.data.FileItem
import com.codeviewer.app.data.FileRepository
import com.codeviewer.app.data.SortMode
import com.codeviewer.app.ui.theme.LocalIdeColors
import java.io.File
import kotlin.math.roundToInt

private data class TreeNode(
    val path: String,
    val name: String,
    val depth: Int,
    val isDirectory: Boolean,
    val isExpanded: Boolean
)

private val RowHeight = 28.dp

private fun sortChildren(items: List<FileItem>, dirPath: String, mode: SortMode, order: List<String>?): List<FileItem> {
    return when (mode) {
        SortMode.NAME_ASC -> items.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
        SortMode.NAME_DESC -> items.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenByDescending { it.name.lowercase() })
        SortMode.TYPE -> items.sortedWith(compareBy({ !it.isDirectory }, { it.extension }, { it.name.lowercase() }))
        SortMode.MODIFIED -> items.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenByDescending { it.lastModified })
        SortMode.CUSTOM -> {
            if (order == null) items.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
            else {
                val index = order.withIndex().associate { (i, n) -> n to i }
                items.sortedWith(compareBy({ index[it.name] ?: Int.MAX_VALUE }, { it.name.lowercase() }))
            }
        }
    }
}

private fun buildVisibleNodes(
    rootPath: String,
    expanded: Set<String>,
    repo: FileRepository,
    mode: SortMode,
    orderFor: (String) -> List<String>?
): List<TreeNode> {
    val result = mutableListOf<TreeNode>()
    fun walk(path: String, depth: Int) {
        val sorted = sortChildren(repo.listDirectory(path), path, mode, orderFor(path))
        for (item in sorted) {
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
    sortMode: SortMode,
    customOrder: Map<String, List<String>>,
    onToggleFolder: (String) -> Unit,
    onOpenFile: (String) -> Unit,
    onCreateFile: (parentDir: String) -> Unit,
    onCreateFolder: (parentDir: String) -> Unit,
    onRename: (path: String) -> Unit,
    onDelete: (path: String) -> Unit,
    onReorder: (dir: String, names: List<String>) -> Unit,
    modifier: Modifier = Modifier,
    contentPaddingBottom: Dp = 0.dp
) {
    val ide = LocalIdeColors.current
    val repo = remember { FileRepository() }
    val density = LocalDensity.current
    val rowPx = with(density) { RowHeight.toPx() }

    // Local overrides give instant feedback while reordering; persisted via onReorder.
    val overrides = remember(rootPath) { mutableStateMapOf<String, List<String>>() }
    var overrideVersion by remember(rootPath) { mutableIntStateOf(0) }
    var menuPath by remember { mutableStateOf<String?>(null) }
    var draggingPath by remember { mutableStateOf<String?>(null) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    fun orderFor(dir: String): List<String>? = overrides[dir] ?: customOrder[dir]

    val nodes = remember(rootPath, expanded, refreshKey, sortMode, customOrder, overrideVersion) {
        buildVisibleNodes(rootPath, expanded, repo, sortMode) { orderFor(it) }
    }

    fun move(node: TreeNode, delta: Int) {
        val parent = File(node.path).parent ?: return
        val current = sortChildren(repo.listDirectory(parent), parent, SortMode.CUSTOM, orderFor(parent)).map { it.name }
        val idx = current.indexOf(node.name)
        if (idx < 0) return
        val target = (idx + delta).coerceIn(0, current.size - 1)
        if (target == idx) return
        val newList = current.toMutableList().apply { add(target, removeAt(idx)) }
        overrides[parent] = newList
        overrideVersion++
        onReorder(parent, newList)
    }

    Box(modifier = modifier.background(ide.panelBg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = contentPaddingBottom)
        ) {
            stickyHeader {
                // Pinned header: stays at the top while the tree scrolls, and
                // offers direct new-file / new-folder buttons.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ide.panelBg)
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
                            .combinedClickable(onClick = { onCreateFile(rootPath) }),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.NoteAdd,
                            contentDescription = "New file",
                            modifier = Modifier.size(18.dp),
                            tint = ide.mutedText
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .combinedClickable(onClick = { onCreateFolder(rootPath) }),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.CreateNewFolder,
                            contentDescription = "New folder",
                            modifier = Modifier.size(18.dp),
                            tint = ide.mutedText
                        )
                    }
                }
            }

            items(nodes, key = { it.path }) { node ->
                val selected = node.path == activeFilePath
                val dragging = node.path == draggingPath
                Box(
                    modifier = Modifier
                        .zIndex(if (dragging) 1f else 0f)
                        .offset { IntOffset(0, if (dragging) dragOffsetY.roundToInt() else 0) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(RowHeight)
                            .background(
                                when {
                                    dragging -> ide.treeSelectionBg
                                    selected -> ide.treeSelectionBg
                                    else -> ide.panelBg
                                }
                            )
                            .combinedClickable(
                                onClick = {
                                    if (node.isDirectory) onToggleFolder(node.path) else onOpenFile(node.path)
                                },
                                onLongClick = { menuPath = node.path }
                            )
                            .padding(start = (8 + node.depth * 14).dp, end = 4.dp),
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
                                modifier = Modifier.size(16.dp).rotate(rotation),
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
                            color = if (node.isDirectory) MaterialTheme.colorScheme.onSurface
                            else fileTypeColor(node.name),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (sortMode == SortMode.CUSTOM) {
                            Icon(
                                imageVector = Icons.Filled.DragHandle,
                                contentDescription = "Drag to reorder",
                                tint = ide.mutedText,
                                modifier = Modifier
                                    .size(20.dp)
                                    .pointerInput(node.path) {
                                        detectDragGestures(
                                            onDragStart = { draggingPath = node.path; dragOffsetY = 0f },
                                            onDrag = { change, amount ->
                                                change.consume()
                                                dragOffsetY += amount.y
                                                while (dragOffsetY >= rowPx) { move(node, 1); dragOffsetY -= rowPx }
                                                while (dragOffsetY <= -rowPx) { move(node, -1); dragOffsetY += rowPx }
                                            },
                                            onDragEnd = { draggingPath = null; dragOffsetY = 0f },
                                            onDragCancel = { draggingPath = null; dragOffsetY = 0f }
                                        )
                                    }
                            )
                        }
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
                                text = { Text("Rename") },
                                leadingIcon = { Icon(Icons.Filled.DriveFileRenameOutline, null) },
                                onClick = { menuPath = null; onRename(node.path) }
                            )
                        }
                        if (sortMode == SortMode.CUSTOM) {
                            DropdownMenuItem(
                                text = { Text("Move up") },
                                leadingIcon = { Icon(Icons.Filled.ArrowUpward, null) },
                                onClick = { menuPath = null; move(node, -1) }
                            )
                            DropdownMenuItem(
                                text = { Text("Move down") },
                                leadingIcon = { Icon(Icons.Filled.ArrowDownward, null) },
                                onClick = { menuPath = null; move(node, 1) }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            leadingIcon = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) },
                            onClick = { menuPath = null; onDelete(node.path) }
                        )
                    }
                }
            }
        }
    }
}
