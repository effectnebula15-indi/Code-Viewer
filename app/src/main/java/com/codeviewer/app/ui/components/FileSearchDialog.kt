package com.codeviewer.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.codeviewer.app.ui.theme.LocalIdeColors
import com.codeviewer.app.util.FileUtils
import java.io.File

private data class FileHit(val name: String, val path: String, val relative: String)

private fun collectFiles(root: String, limit: Int = 6000): List<FileHit> {
    val rootFile = File(root)
    val result = ArrayList<FileHit>()
    val queue = ArrayDeque<File>()
    queue.add(rootFile)
    while (queue.isNotEmpty() && result.size < limit) {
        val dir = queue.removeFirst()
        val children = dir.listFiles() ?: continue
        for (child in children) {
            if (child.name.startsWith(".")) continue
            if (child.isDirectory) {
                queue.add(child)
            } else if (FileUtils.isTextFile(child)) {
                val rel = child.absolutePath.removePrefix(rootFile.absolutePath).trimStart('/')
                result.add(FileHit(child.name, child.absolutePath, rel))
                if (result.size >= limit) break
            }
        }
    }
    return result
}

@Composable
fun FileSearchDialog(
    rootPath: String,
    onOpenFile: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val ide = LocalIdeColors.current
    val allFiles = remember(rootPath) { collectFiles(rootPath) }
    var query by remember { mutableStateOf("") }
    val results = remember(query, allFiles) {
        if (query.isBlank()) allFiles.take(60)
        else allFiles.filter { it.name.contains(query, ignoreCase = true) }
            .sortedBy { it.name.length }
            .take(120)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search files by name…") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true
                )
                Spacer(Modifier.padding(top = 6.dp))
                Text(
                    text = "${results.size} of ${allFiles.size} files",
                    style = MaterialTheme.typography.labelSmall,
                    color = ide.mutedText,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(results, key = { it.path }) { hit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenFile(hit.path); onDismiss() }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FileTypeBadge(hit.name, size = 18.dp)
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = hit.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = hit.relative,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ide.mutedText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
