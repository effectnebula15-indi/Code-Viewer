package com.codeviewer.app.ui.navigation

import android.os.Environment
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codeviewer.app.ui.screens.CodeViewerScreen
import com.codeviewer.app.ui.screens.FileBrowserScreen
import com.codeviewer.app.ui.screens.SettingsScreen
import com.codeviewer.app.ui.theme.AppTheme

@Composable
fun AppNavigation(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit
) {
    val navController = rememberNavController()
    val defaultPath = Environment.getExternalStorageDirectory().absolutePath

    NavHost(
        navController = navController,
        startDestination = Screen.FileBrowser.createRoute(defaultPath)
    ) {
        composable(
            route = Screen.FileBrowser.route,
            arguments = listOf(navArgument("path") { type = NavType.StringType }),
            enterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) { backStackEntry ->
            val path = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("path") ?: defaultPath, "UTF-8"
            )
            FileBrowserScreen(
                currentPath = path,
                currentTheme = currentTheme,
                onNavigateToFolder = { folderPath ->
                    navController.navigate(Screen.FileBrowser.createRoute(folderPath))
                },
                onNavigateToFile = { filePath ->
                    navController.navigate(Screen.CodeViewer.createRoute(filePath))
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onThemeChange = onThemeChange
            )
        }

        composable(
            route = Screen.CodeViewer.route,
            arguments = listOf(navArgument("path") { type = NavType.StringType }),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) { backStackEntry ->
            val path = java.net.URLDecoder.decode(
                backStackEntry.arguments?.getString("path") ?: "", "UTF-8"
            )
            CodeViewerScreen(
                filePath = path,
                currentTheme = currentTheme,
                onNavigateBack = { navController.popBackStack() },
                onThemeChange = onThemeChange
            )
        }

        composable(
            route = Screen.Settings.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            SettingsScreen(
                currentTheme = currentTheme,
                onThemeChange = onThemeChange,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
