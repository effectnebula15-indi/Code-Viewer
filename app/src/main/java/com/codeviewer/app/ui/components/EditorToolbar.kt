package com.codeviewer.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorToolbar(
    fileName: String,
    isEditMode: Boolean,
    isModified: Boolean,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onUndoClick: () -> Unit,
    onToggleEditMode: () -> Unit,
    onSearchClick: () -> Unit,
    onThemeClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = fileName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Filled.Search, contentDescription = "Search")
            }
            if (isEditMode && isModified) {
                IconButton(onClick = onUndoClick) {
                    Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")
                }
                IconButton(onClick = onSaveClick) {
                    Icon(Icons.Filled.Save, contentDescription = "Save")
                }
            }
            IconButton(onClick = onToggleEditMode) {
                Icon(
                    imageVector = if (isEditMode) Icons.Filled.Visibility else Icons.Filled.Edit,
                    contentDescription = if (isEditMode) "View mode" else "Edit mode"
                )
            }
            IconButton(onClick = onThemeClick) {
                Icon(Icons.Filled.Palette, contentDescription = "Theme")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
