package com.codeviewer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.codeviewer.app.ui.navigation.AppNavigation
import com.codeviewer.app.ui.theme.AppTheme
import com.codeviewer.app.ui.theme.CodeViewerTheme
import com.codeviewer.app.ui.theme.ThemePreference
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themePreference = remember { ThemePreference(this) }
            val currentTheme by themePreference.themeFlow
                .collectAsState(initial = AppTheme.DARK)

            CodeViewerTheme(appTheme = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        currentTheme = currentTheme,
                        onThemeChange = { theme ->
                            lifecycleScope.launch {
                                themePreference.setTheme(theme)
                            }
                        }
                    )
                }
            }
        }
    }
}
