package com.codeviewer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.codeviewer.app.ui.theme.AppTheme

private data class ThemePreview(
    val theme: AppTheme,
    val bg: Color,
    val primary: Color,
    val accent: Color
)

private val themePreviews = listOf(
    ThemePreview(AppTheme.LIGHT, Color(0xFFFAFAFA), Color(0xFF6200EE), Color(0xFF03DAC6)),
    ThemePreview(AppTheme.DARK, Color(0xFF121212), Color(0xFFBB86FC), Color(0xFF03DAC6)),
    ThemePreview(AppTheme.DARK_PURPLE, Color(0xFF1A1128), Color(0xFFBB86FC), Color(0xFF9C64FF)),
    ThemePreview(AppTheme.ULTRA_DARK, Color(0xFF000000), Color(0xFFB0B0B0), Color(0xFF808080)),
    ThemePreview(AppTheme.DARK_RED, Color(0xFF1A1111), Color(0xFFFF6B6B), Color(0xFFFF4444))
)

@Composable
fun ThemePickerDialog(
    currentTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Choose Theme",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                themePreviews.forEach { preview ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { onThemeSelected(preview.theme) }
                            .padding(vertical = 8.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = currentTheme == preview.theme,
                            onClick = { onThemeSelected(preview.theme) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(preview.bg)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(preview.primary)
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(preview.accent)
                            )
                        }

                        Text(
                            text = preview.theme.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
