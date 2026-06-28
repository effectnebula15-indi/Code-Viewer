package com.codeviewer.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.codeviewer.app.data.DEFAULT_FONT_SIZE
import com.codeviewer.app.data.EditorSettings
import com.codeviewer.app.data.FileRepository
import com.codeviewer.app.data.MAX_FONT_SIZE
import com.codeviewer.app.data.MIN_FONT_SIZE
import com.codeviewer.app.data.SortMode
import com.codeviewer.app.ui.components.CodeEditor
import com.codeviewer.app.ui.components.CodeSearchBar
import com.codeviewer.app.ui.components.ConfirmDialog
import com.codeviewer.app.ui.components.EditorTab
import com.codeviewer.app.ui.components.EditorTabBar
import com.codeviewer.app.ui.components.FileSearchDialog
import com.codeviewer.app.ui.components.MarkdownView
import com.codeviewer.app.ui.components.ProjectTreePanel
import com.codeviewer.app.ui.components.TextInputDialog
import com.codeviewer.app.ui.components.ThemePickerDialog
import com.codeviewer.app.ui.theme.AppTheme
import com.codeviewer.app.ui.theme.LocalIdeColors
import com.codeviewer.app.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    val context = LocalContext.current
    val repo = remember { FileRepository() }
    val ide = LocalIdeColors.current
    val scope = rememberCoroutineScope()
    val settings = remember { EditorSettings(context.applicationContext) }
    val projectName = remember(projectPath) { File(projectPath).name.ifEmpty { "Project" } }

    val fontSize by settings.fontSize.collectAsState(initial = DEFAULT_FONT_SIZE)
    val sortMode by settings.sortMode.collectAsState(initial = SortMode.NAME_ASC)
    val customOrder by settings.customOrder.collectAsState(initial = emptyMap())

    var openPathsRaw by rememberSaveable(projectPath) { mutableStateOf("") }
    var expandedRaw by rememberSaveable(projectPath) { mutableStateOf("") }
    var activeIndex by rememberSaveable(projectPath) { mutableIntStateOf(0) }
    var treeVisible by rememberSaveable { mutableStateOf(true) }
    var editMode by rememberSaveable(projectPath) { mutableStateOf(false) }
    var showSearch by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var currentMatch by remember { mutableIntStateOf(0) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showFileSearch by remember { mutableStateOf(false) }

    var createTarget by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    var renameTarget by remember { mutableStateOf<String?>(null) }
    var deleteTarget by remember { mutableStateOf<String?>(null) }
    var showCloseConfirm by remember { mutableStateOf(false) }

    val contents = remember(projectPath) { mutableStateMapOf<String, String>() }
    val originals = remember(projectPath) { mutableStateMapOf<String, String>() }

    val openPaths = remember(openPathsRaw) { openPathsRaw.split("\n").filter { it.isNotBlank() } }
    val expanded = remember(expandedRaw) { expandedRaw.split("\n").filter { it.isNotBlank() }.toSet() }
    val activePath = openPaths.getOrNull(activeIndex)

    var editorValue by remember(activePath) { mutableStateOf(TextFieldValue("")) }
    LaunchedEffect(activePath, editMode) {
        if (editMode && activePath != null) {
            val text = contents[activePath] ?: ""
            // Start at the top so entering edit mode does not auto-scroll to the
            // bottom of the file (which made taps land near the last line).
            editorValue = TextFieldValue(text, TextRange(0))
        }
    }

    fun setExpanded(set: Set<String>) { expandedRaw = set.joinToString("\n") }
    fun setOpenPaths(list: List<String>) { openPathsRaw = list.joinToString("\n") }

    fun openFile(path: String) {
        val list = openPaths.toMutableList()
        if (path !in list) list.add(path)
        setOpenPaths(list)
        activeIndex = list.indexOf(path)
        editMode = false
        showSearch = false
        searchQuery = ""
    }

    fun closeTabsUnder(path: String) {
        val remaining = openPaths.filterNot { it == path || it.startsWith("$path/") }
        openPaths.filter { it == path || it.startsWith("$path/") }.forEach {
            contents.remove(it); originals.remove(it)
        }
        setOpenPaths(remaining)
        if (activeIndex >= remaining.size) activeIndex = (remaining.size - 1).coerceAtLeast(0)
    }

    fun closeTab(index: Int) {
        val list = openPaths.toMutableList()
        if (index in list.indices) {
            val p = list.removeAt(index)
            contents.remove(p); originals.remove(p)
            setOpenPaths(list)
            if (activeIndex >= list.size) activeIndex = (list.size - 1).coerceAtLeast(0)
        }
    }

    fun toggleFolder(path: String) {
        val set = expanded.toMutableSet()
        if (!set.add(path)) set.remove(path)
        setExpanded(set)
    }

    fun performCreate(parentDir: String, isFolder: Boolean, name: String) {
        if (isFolder) {
            repo.createFolder(parentDir, name)
            if (parentDir != projectPath) setExpanded(expanded + parentDir)
            refreshKey++
        } else {
            val created = repo.createFile(parentDir, name)
            if (parentDir != projectPath) setExpanded(expanded + parentDir)
            refreshKey++
            if (created != null) openFile(created)
        }
    }

    fun performDelete(path: String) {
        closeTabsUnder(path)
        if (path in expanded) setExpanded(expanded - path)
        repo.delete(path)
        refreshKey++
    }

    fun performRename(path: String, newName: String) {
        val newPath = repo.rename(path, newName) ?: return
        // Carry any open tab (and its cached/edited content) over to the new path.
        if (path in openPaths) {
            setOpenPaths(openPaths.map { if (it == path) newPath else it })
        }
        contents.remove(path)?.let { contents[newPath] = it }
        originals.remove(path)?.let { originals[newPath] = it }
        refreshKey++
    }

    val anyModified = openPaths.any { contents[it] != null && contents[it] != originals[it] }
    fun requestCloseProject() {
        if (anyModified) showCloseConfirm = true else onCloseProject()
    }
    fun changeFont(delta: Int) {
        scope.launch { settings.setFontSize((fontSize + delta).coerceIn(MIN_FONT_SIZE, MAX_FONT_SIZE)) }
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
            else -> requestCloseProject()
        }
    }

    // ---- Dialogs ----
    if (showThemeDialog) {
        ThemePickerDialog(
            currentTheme = currentTheme,
            onThemeSelected = { onThemeChange(it); showThemeDialog = false },
            onDismiss = { showThemeDialog = false }
        )
    }
    if (showSortDialog) {
        SortDialog(
            current = sortMode,
            onSelect = { scope.launch { settings.setSortMode(it) }; showSortDialog = false },
            onDismiss = { showSortDialog = false }
        )
    }
    if (showFileSearch) {
        FileSearchDialog(
            rootPath = projectPath,
            onOpenFile = { openFile(it) },
            onDismiss = { showFileSearch = false }
        )
    }
    createTarget?.let { (parent, isFolder) ->
        TextInputDialog(
            title = if (isFolder) "New folder" else "New file",
            label = if (isFolder) "Folder name" else "File name (e.g. main.kt)",
            confirmText = "Create",
            onConfirm = { name -> performCreate(parent, isFolder, name); createTarget = null },
            onDismiss = { createTarget = null }
        )
    }
    renameTarget?.let { path ->
        TextInputDialog(
            title = "Rename",
            label = "New name",
            confirmText = "Rename",
            initialValue = File(path).name,
            onConfirm = { name -> performRename(path, name); renameTarget = null },
            onDismiss = { renameTarget = null }
        )
    }
    deleteTarget?.let { path ->
        ConfirmDialog(
            title = "Delete",
            message = "Delete \"${File(path).name}\"? This cannot be undone.",
            confirmText = "Delete",
            destructive = true,
            onConfirm = { performDelete(path); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
    if (showCloseConfirm) {
        ConfirmDialog(
            title = "Close project",
            message = "You have unsaved changes. Close anyway?",
            confirmText = "Close",
            destructive = true,
            onConfirm = { showCloseConfirm = false; onCloseProject() },
            onDismiss = { showCloseConfirm = false }
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

    fun selectAll() {
        editorValue = editorValue.copy(selection = TextRange(0, editorValue.text.length))
    }
    fun deleteSelection() {
        val sel = editorValue.selection
        val text = editorValue.text
        val newValue = when {
            !sel.collapsed -> TextFieldValue(text.removeRange(sel.min, sel.max), TextRange(sel.min))
            sel.start > 0 -> TextFieldValue(text.removeRange(sel.start - 1, sel.start), TextRange(sel.start - 1))
            else -> editorValue
        }
        editorValue = newValue
        activePath?.let { contents[it] = newValue.text }
    }
    fun saveActive() {
        activePath?.let { p ->
            repo.writeFile(p, contents[p] ?: "")
            originals[p] = contents[p] ?: ""
        }
    }
    fun undoActive() {
        activePath?.let { p ->
            val text = originals[p] ?: ""
            contents[p] = text
            editorValue = TextFieldValue(text, TextRange(text.length))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ide.editorBg)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.height(TopReserve))

            // Full-width in-file search (kept above the tree so it is never squeezed).
            CodeSearchBar(
                visible = showSearch && activePath != null && !(isMarkdown && !editMode),
                query = searchQuery,
                matchCount = matchCount,
                currentMatch = currentMatch,
                onQueryChange = { searchQuery = it; currentMatch = 0 },
                onNextMatch = { if (matchCount > 0) currentMatch = (currentMatch + 1) % matchCount },
                onPreviousMatch = { if (matchCount > 0) currentMatch = (currentMatch - 1 + matchCount) % matchCount },
                onClose = { showSearch = false; searchQuery = "" }
            )

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
                        refreshKey = refreshKey,
                        sortMode = sortMode,
                        customOrder = customOrder,
                        onToggleFolder = { toggleFolder(it) },
                        onOpenFile = { openFile(it) },
                        onCreateFile = { parent -> createTarget = parent to false },
                        onCreateFolder = { parent -> createTarget = parent to true },
                        onRename = { renameTarget = it },
                        onDelete = { deleteTarget = it },
                        onReorder = { dir, names -> scope.launch { settings.setCustomOrderFor(dir, names) } },
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

                    if (editMode && activePath != null) {
                        EditActionBar(
                            modified = activeModified,
                            onSelectAll = { selectAll() },
                            onDelete = { deleteSelection() },
                            onUndo = { undoActive() },
                            onSave = { saveActive() }
                        )
                    }

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
                                editorValue = editorValue,
                                onEditorValueChange = { v ->
                                    editorValue = v
                                    activePath.let { contents[it] = v.text }
                                },
                                fontSize = fontSize,
                                onFontSizeChange = { scope.launch { settings.setFontSize(it) } },
                                bottomInset = BottomClearance
                            )
                        }
                    }
                }
            }
        }

        ToolbarBubble(
            modifier = Modifier.align(Alignment.TopCenter),
            projectName = projectName,
            fileName = activePath?.let { File(it).name },
            editMode = editMode,
            modified = activeModified,
            hasFile = activePath != null,
            onToggleTree = { treeVisible = !treeVisible },
            onToggleEdit = { editMode = !editMode },
            onSearch = { showSearch = !showSearch },
            onFindFile = { showFileSearch = true },
            onSort = { showSortDialog = true },
            onTextBigger = { changeFont(1) },
            onTextSmaller = { changeFont(-1) },
            onTheme = { showThemeDialog = true },
            onNewFile = { createTarget = projectPath to false },
            onNewFolder = { createTarget = projectPath to true },
            onCloseProject = { requestCloseProject() }
        )

        StatusBubble(
            modifier = Modifier.align(Alignment.BottomCenter),
            language = if (activePath != null) languageKey else "",
            lineCount = if (activePath != null) activeContent.count { it == '\n' } + 1 else 0,
            modified = activeModified,
            editMode = editMode
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
    onSearch: () -> Unit,
    onFindFile: () -> Unit,
    onSort: () -> Unit,
    onTextBigger: () -> Unit,
    onTextSmaller: () -> Unit,
    onTheme: () -> Unit,
    onNewFile: () -> Unit,
    onNewFolder: () -> Unit,
    onCloseProject: () -> Unit
) {
    val ide = LocalIdeColors.current
    var menu by remember { mutableStateOf(false) }
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
            modifier = Modifier.padding(start = 4.dp, end = 4.dp),
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
                    Icon(Icons.Filled.Search, contentDescription = "Search in file", tint = ide.mutedText)
                }
                IconButton(onClick = onToggleEdit) {
                    Icon(
                        imageVector = if (editMode) Icons.Filled.Visibility else Icons.Filled.Edit,
                        contentDescription = "Toggle edit",
                        tint = if (editMode) ide.accent else ide.mutedText
                    )
                }
            }
            Box {
                IconButton(onClick = { menu = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = ide.mutedText)
                }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(
                        text = { Text("Find file…") },
                        leadingIcon = { Icon(Icons.Filled.Search, null) },
                        onClick = { menu = false; onFindFile() }
                    )
                    DropdownMenuItem(
                        text = { Text("Sort…") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Sort, null) },
                        onClick = { menu = false; onSort() }
                    )
                    DropdownMenuItem(
                        text = { Text("Text bigger") },
                        leadingIcon = { Icon(Icons.Filled.TextIncrease, null) },
                        onClick = { onTextBigger() }
                    )
                    DropdownMenuItem(
                        text = { Text("Text smaller") },
                        leadingIcon = { Icon(Icons.Filled.TextDecrease, null) },
                        onClick = { onTextSmaller() }
                    )
                    DropdownMenuItem(
                        text = { Text("New file") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.NoteAdd, null) },
                        onClick = { menu = false; onNewFile() }
                    )
                    DropdownMenuItem(
                        text = { Text("New folder") },
                        leadingIcon = { Icon(Icons.Filled.CreateNewFolder, null) },
                        onClick = { menu = false; onNewFolder() }
                    )
                    DropdownMenuItem(
                        text = { Text("Theme") },
                        leadingIcon = { Icon(Icons.Filled.Palette, null) },
                        onClick = { menu = false; onTheme() }
                    )
                    DropdownMenuItem(
                        text = { Text("Close project") },
                        leadingIcon = { Icon(Icons.Filled.Close, null) },
                        onClick = { menu = false; onCloseProject() }
                    )
                }
            }
        }
    }
}

@Composable
private fun EditActionBar(
    modified: Boolean,
    onSelectAll: () -> Unit,
    onDelete: () -> Unit,
    onUndo: () -> Unit,
    onSave: () -> Unit
) {
    val ide = LocalIdeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(ide.toolbarBg)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ActionChip("Select all", Icons.Filled.SelectAll, ide.mutedText, onSelectAll)
        ActionChip("Delete", Icons.AutoMirrored.Filled.Backspace, ide.mutedText, onDelete)
        if (modified) {
            ActionChip("Undo", Icons.AutoMirrored.Filled.Undo, ide.mutedText, onUndo)
            ActionChip("Save", Icons.Filled.Save, ide.accent, onSave)
        }
    }
}

@Composable
private fun ActionChip(label: String, icon: ImageVector, tint: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(16.dp), tint = tint)
        Text(label, style = MaterialTheme.typography.labelLarge, color = tint)
    }
}

@Composable
private fun SortDialog(
    current: SortMode,
    onSelect: (SortMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sort files", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                SortMode.entries.forEach { mode ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(selected = mode == current, onClick = { onSelect(mode) })
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = mode == current, onClick = { onSelect(mode) })
                        Spacer(Modifier.width(8.dp))
                        Text(mode.displayName, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Done") } }
    )
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
            Text("$lineCount ln", style = MaterialTheme.typography.labelSmall, color = ide.mutedText)
            Text(
                text = language.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelSmall,
                color = ide.mutedText
            )
            if (modified) {
                Text("Unsaved", style = MaterialTheme.typography.labelSmall, color = ide.accent)
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
