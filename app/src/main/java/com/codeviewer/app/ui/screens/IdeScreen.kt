package com.codeviewer.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.codeviewer.app.data.FileRepository
import com.codeviewer.app.ui.components.CodeEditor
import com.codeviewer.app.ui.components.CodeSearchBar
import com.codeviewer.app.ui.components.EditorTab
import com.codeviewer.app.ui.components.EditorTabBar
import com.codeviewer.app.ui.components.MarkdownView
import com.codeviewer.app.ui.components.ProjectTreePanel
import com.codeviewer.app.ui.components.ThemePickerDialog
import com.codeviewer.app.ui.theme.AppTheme
import com.codeviewer.app.ui.theme.LocalIdeColors
import com.codeviewer.app.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private val TopReserve = 64.dp
private val BottomClearance = 56.dp

@Composable
fun IdeScreen(
    projectPath: String,
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    onCloseProject: () -> Unit
) {
    val repo = remember { FileRepository() }
    val ide = LocalIdeColors.current
    val projectName = remember(projectPath) { File(projectPath).name.ifEmpty { "Project" } }

    var openPathsRaw by rememberSaveable(projectPath) { mutableStateOf("") }
    var expandedRaw by rememberSaveable(projectPath) { mutableStateOf("") }
    var activeIndex by rememberSaveable(projectPath) { mutableIntStateOf(0) }
    var treeVisible by rememberSaveable { mutableStateOf(true) }
    var editMode by rememberSaveable(projectPath) { mutableStateOf(false) }
    var showSearch by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var currentMatch by remember { mutableIntStateOf(0) }
    var showThemeDialog by remember { mutableStateOf(false) }

    val contents = remember(projectPath) { mutableStateMapOf<String, String>() }
    val originals = remember(projectPath) { mutableStateMapOf<String, String>() }

    val openPaths = remember(openPathsRaw) {
        openPathsRaw.split("\n").filter { it.isNotBlank() }
    }
    val expanded = remember(expandedRaw) {
        expandedRaw.split("\n").filter { it.isNotBlank() }.toSet()
    }
    val activePath = openPaths.getOrNull(activeIndex)

    fun openFile(path: String) {
        val list = openPaths.toMutableList()
        if (path !in list) list.add(path)
        openPathsRaw = list.joinToString("\n")
        activeIndex = list.indexOf(path)
        editMode = false
        showSearch = false
        searchQuery = ""
    }

    fun closeTab(index: Int) {
        val list = openPaths.toMutableList()
        if (index in list.indices) {
            val p = list.removeAt(index)
            contents.remove(p)
            originals.remove(p)
            openPathsRaw = list.joinToString("\n")
            if (activeIndex >= list.size) activeIndex = (list.size - 1).coerceAtLeast(0)
        }
    }

    fun toggleFolder(path: String) {
        val set = expanded.toMutableSet()
        if (!set.add(path)) set.remove(path)
        expandedRaw = set.joinToString("\n")
    }

    LaunchedEffect(activePath) {
        val p = activePath ?: return@LaunchedEffect
        if (!contents.containsKey(p)) {
            val text = withContext(Dispatchers.IO) { repo.readFile(p) }
            contents[p] = text
            originals[p] = text
        }
    }

    BackHandler {
        when {
            showSearch -> { showSearch = false; searchQuery = "" }
            else -> onCloseProject()
        }
    }

    if (showThemeDialog) {
        ThemePickerDialog(
            currentTheme = currentTheme,
            onThemeSelected = { onThemeChange(it); showThemeDialog = false },
            onDismiss = { showThemeDialog = false }
        )
    }

    val activeContent = activePath?.let { contents[it] } ?: ""
    val activeModified = activePath != null && contents[activePath] != originals[activePath]
    val languageKey = remember(activePath) {
        activePath?.let { FileUtils.getLanguageForExtension(File(it).extension) } ?: "generic"
    }
    val isMarkdown = languageKey == "markdown"
    val matchCount = remember(activeContent, searchQuery) {
        if (searchQuery.length >= 2) {
            var count = 0
            var i = activeContent.indexOf(searchQuery, ignoreCase = true)
            while (i >= 0) { count++; i = activeContent.indexOf(searchQuery, i + searchQuery.length, ignoreCase = true) }
            count
        } else 0
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ide.editorBg)
            .systemBarsPadding()
    ) {
        // ---- Main content (sits below the floating toolbar bubble) ----
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.height(TopReserve))

            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AnimatedVisibility(
                    visible = treeVisible,
                    enter = expandHorizontally() + fadeIn(),
                    exit = shrinkHorizontally() + fadeOut()
                ) {
                    ProjectTreePanel(
                        rootPath = projectPath,
                        projectName = projectName,
                        expanded = expanded,
                        activeFilePath = activePath,
                        onToggleFolder = { toggleFolder(it) },
                        onOpenFile = { openFile(it) },
                        contentPaddingBottom = BottomClearance,
                        modifier = Modifier.width(262.dp).fillMaxSize()
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .background(ide.editorBg)
                ) {
                    if (openPaths.isNotEmpty()) {
                        EditorTabBar(
                            tabs = openPaths.map {
                                EditorTab(it, File(it).name, contents[it] != originals[it])
                            },
                            activeIndex = activeIndex,
                            onSelect = { activeIndex = it; editMode = false },
                            onClose = { closeTab(it) }
                        )
                    }

                    CodeSearchBar(
                        visible = showSearch && !isMarkdown,
                        query = searchQuery,
                        matchCount = matchCount,
                        currentMatch = currentMatch,
                        onQueryChange = { searchQuery = it; currentMatch = 0 },
                        onNextMatch = { if (matchCount > 0) currentMatch = (currentMatch + 1) % matchCount },
                        onPreviousMatch = { if (matchCount > 0) currentMatch = (currentMatch - 1 + matchCount) % matchCount },
                        onClose = { showSearch = false; searchQuery = "" }
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .background(ide.editorBg)
                            .imePadding()
                    ) {
                        when {
                            activePath == null -> EmptyEditor()
                            isMarkdown && !editMode -> MarkdownView(
                                content = activeContent,
                                bottomInset = BottomClearance
                            )
                            else -> CodeEditor(
                                content = activeContent,
                                languageKey = languageKey,
                                isEditMode = editMode,
                                searchQuery = searchQuery,
                                onContentChange = { contents[activePath] = it },
                                bottomInset = BottomClearance
                            )
                        }
                    }
                }
            }
        }

        // ---- Floating toolbar bubble ----
        ToolbarBubble(
            modifier = Modifier.align(Alignment.TopCenter),
            projectName = projectName,
            fileName = activePath?.let { File(it).name },
            editMode = editMode,
            modified = activeModified,
            hasFile = activePath != null,
            onToggleTree = { treeVisible = !treeVisible },
            onToggleEdit = { editMode = !editMode },
            onSave = {
                activePath?.let { p ->
                    repo.writeFile(p, contents[p] ?: "")
                    originals[p] = contents[p] ?: ""
                }
            },
            onUndo = {
                activePath?.let { p -> contents[p] = originals[p] ?: "" }
            },
            onSearch = { showSearch = !showSearch },
            onTheme = { showThemeDialog = true }
        )

        // ---- Floating status bubble ----
        StatusBubble(
            modifier = Modifier.align(Alignment.BottomCenter),
            language = if (activePath != null) languageKey else "",
            lineCount = if (activePath != null) activeContent.count { it == '\n' } + 1 else 0,
            modified = activeModified,
            editMode = editMode && !(isMarkdown && !editMode)
        )
    }
}

@Composable
private fun ToolbarBubble(
    modifier: Modifier,
    projectName: String,
    fileName: String?,
    editMode: Boolean,
    modified: Boolean,
    hasFile: Boolean,
    onToggleTree: () -> Unit,
    onToggleEdit: () -> Unit,
    onSave: () -> Unit,
    onUndo: () -> Unit,
    onSearch: () -> Unit,
    onTheme: () -> Unit
) {
    val ide = LocalIdeColors.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        color = ide.toolbarBg,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, ide.border)
    ) {
        Row(
            modifier = Modifier.padding(start = 4.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleTree) {
                Icon(Icons.AutoMirrored.Filled.MenuOpen, contentDescription = "Toggle project tree", tint = ide.mutedText)
            }
            Column(modifier = Modifier.weight(1f).padding(start = 2.dp)) {
                Text(
                    text = projectName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (fileName != null) {
                    Text(
                        text = (if (modified) "• " else "") + fileName,
                        style = MaterialTheme.typography.bodySmall,
                        color = ide.mutedText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (hasFile) {
                IconButton(onClick = onSearch) {
                    Icon(Icons.Filled.Search, contentDescription = "Search", tint = ide.mutedText)
                }
                if (editMode && modified) {
                    IconButton(onClick = onUndo) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = ide.mutedText)
                    }
                    IconButton(onClick = onSave) {
                        Icon(Icons.Filled.Save, contentDescription = "Save", tint = ide.accent)
                    }
                }
                IconButton(onClick = onToggleEdit) {
                    Icon(
                        imageVector = if (editMode) Icons.Filled.Visibility else Icons.Filled.Edit,
                        contentDescription = "Toggle edit",
                        tint = if (editMode) ide.accent else ide.mutedText
                    )
                }
            }
            IconButton(onClick = onTheme) {
                Icon(Icons.Filled.Palette, contentDescription = "Theme", tint = ide.mutedText)
            }
        }
    }
}

@Composable
private fun StatusBubble(
    modifier: Modifier,
    language: String,
    lineCount: Int,
    modified: Boolean,
    editMode: Boolean
) {
    val ide = LocalIdeColors.current
    if (language.isEmpty()) return
    Surface(
        modifier = modifier
            .padding(bottom = 12.dp)
            .height(30.dp),
        shape = RoundedCornerShape(15.dp),
        color = ide.statusBarBg,
        shadowElevation = 4.dp,
        border = BorderStroke(1.dp, ide.border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = if (editMode) "EDIT" else "READ",
                style = MaterialTheme.typography.labelSmall,
                color = if (editMode) ide.accent else ide.mutedText
            )
            Text(
                text = "$lineCount ln",
                style = MaterialTheme.typography.labelSmall,
                color = ide.mutedText
            )
            Text(
                text = language.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelSmall,
                color = ide.mutedText
            )
            if (modified) {
                Text(
                    text = "Unsaved",
                    style = MaterialTheme.typography.labelSmall,
                    color = ide.accent
                )
            }
        }
    }
}

@Composable
private fun EmptyEditor() {
    val ide = LocalIdeColors.current
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.Code,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = ide.mutedText
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Select a file from the project tree",
                style = MaterialTheme.typography.bodyMedium,
                color = ide.mutedText
            )
        }
    }
}
