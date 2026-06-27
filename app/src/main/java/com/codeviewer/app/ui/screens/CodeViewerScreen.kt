package com.codeviewer.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.codeviewer.app.data.FileRepository
import com.codeviewer.app.ui.components.CodeSearchBar
import com.codeviewer.app.ui.components.EditorToolbar
import com.codeviewer.app.ui.components.SyntaxHighlightedEditor
import com.codeviewer.app.ui.components.ThemePickerDialog
import com.codeviewer.app.ui.theme.AppTheme
import com.codeviewer.app.util.FileUtils

@Composable
fun CodeViewerScreen(
    filePath: String,
    currentTheme: AppTheme,
    onNavigateBack: () -> Unit,
    onThemeChange: (AppTheme) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { FileRepository() }
    val fileName = remember(filePath) { filePath.substringAfterLast("/") }
    val extension = remember(filePath) { java.io.File(filePath).extension }
    val languageKey = remember(extension) {
        FileUtils.getLanguageForExtension(extension)
    }

    var content by remember { mutableStateOf("") }
    var originalContent by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }
    var isModified by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showThemeDialog by remember { mutableStateOf(false) }

    val matchCount = remember(content, searchQuery) {
        if (searchQuery.length >= 2) {
            searchQuery.toRegex(RegexOption.LITERAL).findAll(content).count()
        } else 0
    }
    var currentMatch by remember { mutableIntStateOf(0) }

    LaunchedEffect(filePath) {
        val text = repository.readFile(filePath)
        content = text
        originalContent = text
    }

    if (showThemeDialog) {
        ThemePickerDialog(
            currentTheme = currentTheme,
            onThemeSelected = { theme ->
                onThemeChange(theme)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    Scaffold(
        topBar = {
            EditorToolbar(
                fileName = fileName,
                isEditMode = isEditMode,
                isModified = isModified,
                onBackClick = onNavigateBack,
                onSaveClick = {
                    repository.writeFile(filePath, content)
                    originalContent = content
                    isModified = false
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                },
                onUndoClick = {
                    content = originalContent
                    isModified = false
                },
                onToggleEditMode = { isEditMode = !isEditMode },
                onSearchClick = { showSearch = !showSearch },
                onThemeClick = { showThemeDialog = true }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CodeSearchBar(
                visible = showSearch,
                query = searchQuery,
                matchCount = matchCount,
                currentMatch = currentMatch,
                onQueryChange = { query ->
                    searchQuery = query
                    currentMatch = 0
                },
                onNextMatch = {
                    if (matchCount > 0) {
                        currentMatch = (currentMatch + 1) % matchCount
                    }
                },
                onPreviousMatch = {
                    if (matchCount > 0) {
                        currentMatch = (currentMatch - 1 + matchCount) % matchCount
                    }
                },
                onClose = {
                    showSearch = false
                    searchQuery = ""
                }
            )

            SyntaxHighlightedEditor(
                content = content,
                languageKey = languageKey,
                isEditMode = isEditMode,
                onContentChange = { newContent ->
                    content = newContent
                    isModified = newContent != originalContent
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
