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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.codeviewer.app.data.ProjectStore
import com.codeviewer.app.ui.screens.IdeScreen
import com.codeviewer.app.ui.screens.WelcomeScreen
import com.codeviewer.app.ui.theme.AppTheme
import com.codeviewer.app.ui.theme.CodeViewerTheme
import com.codeviewer.app.ui.theme.ThemePreference
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContext = applicationContext

        setContent {
            val themePreference = remember { ThemePreference(appContext) }
            val projectStore = remember { ProjectStore(appContext) }
            val scope = rememberCoroutineScope()

            val currentTheme by themePreference.themeFlow.collectAsState(initial = AppTheme.DARK)
            val recentProjects by projectStore.recentProjects.collectAsState(initial = emptyList())

            // Survives activity recreation and process death — fixes the blank
            // screen seen when returning to the app from the recents tray.
            var currentProject by rememberSaveable { mutableStateOf<String?>(null) }

            CodeViewerTheme(appTheme = currentTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val project = currentProject
                    if (project == null || !File(project).isDirectory) {
                        WelcomeScreen(
                            recentProjects = recentProjects,
                            currentTheme = currentTheme,
                            onThemeChange = { scope.launch { themePreference.setTheme(it) } },
                            onRemoveRecent = { scope.launch { projectStore.removeProject(it) } },
                            onProjectChosen = { path ->
                                scope.launch { projectStore.addProject(path) }
                                currentProject = path
                            }
                        )
                    } else {
                        IdeScreen(
                            projectPath = project,
                            currentTheme = currentTheme,
                            onThemeChange = { scope.launch { themePreference.setTheme(it) } },
                            onCloseProject = { currentProject = null }
                        )
                    }
                }
            }
        }
    }
}
