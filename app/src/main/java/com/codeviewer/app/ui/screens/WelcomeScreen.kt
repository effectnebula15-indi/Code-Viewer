package com.codeviewer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codeviewer.app.data.RecentProject
import com.codeviewer.app.ui.components.AppLogo
import com.codeviewer.app.ui.components.ThemePickerDialog
import com.codeviewer.app.ui.theme.AppTheme
import com.codeviewer.app.ui.theme.Inter
import com.codeviewer.app.ui.theme.LocalIdeColors

@Composable
fun WelcomeScreen(
    recentProjects: List<RecentProject>,
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    onRemoveRecent: (String) -> Unit,
    onProjectChosen: (String) -> Unit
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    if (showPicker) {
        FolderPickerScreen(
            onCancel = { showPicker = false },
            onPick = { path ->
                showPicker = false
                onProjectChosen(path)
            }
        )
        return
    }

    if (showThemeDialog) {
        ThemePickerDialog(
            currentTheme = currentTheme,
            onThemeSelected = {
                onThemeChange(it)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    val ide = LocalIdeColors.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
        ) {
            // Top-right theme button
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showThemeDialog = true }) {
                    Icon(
                        Icons.Filled.Palette,
                        contentDescription = "Theme",
                        tint = ide.mutedText
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppLogo(size = 56.dp)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Code-Viewer",
                        fontFamily = Inter,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Browse · Read · Edit",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ide.mutedText
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // Open button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { showPicker = true }
                    .padding(vertical = 14.dp, horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    Icons.Filled.FolderOpen,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Open Folder as Project",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "RECENT PROJECTS",
                style = MaterialTheme.typography.labelMedium,
                color = ide.mutedText
            )
            Spacer(Modifier.height(8.dp))

            if (recentProjects.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recent projects yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ide.mutedText
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(recentProjects, key = { it.path }) { project ->
                        RecentProjectRow(
                            project = project,
                            onOpen = { onProjectChosen(project.path) },
                            onRemove = { onRemoveRecent(project.path) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentProjectRow(
    project: RecentProject,
    onOpen: () -> Unit,
    onRemove: () -> Unit
) {
    val ide = LocalIdeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, ide.border, RoundedCornerShape(8.dp))
            .clickable(onClick = onOpen)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = project.name.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = project.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = project.path,
                style = MaterialTheme.typography.bodySmall,
                color = ide.mutedText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = onRemove) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Remove",
                tint = ide.mutedText,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
