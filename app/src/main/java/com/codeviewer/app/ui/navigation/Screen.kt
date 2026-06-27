package com.codeviewer.app.ui.navigation

sealed class Screen(val route: String) {
    data object FileBrowser : Screen("file_browser/{path}") {
        fun createRoute(path: String) = "file_browser/${java.net.URLEncoder.encode(path, "UTF-8")}"
    }
    data object CodeViewer : Screen("code_viewer/{path}") {
        fun createRoute(path: String) = "code_viewer/${java.net.URLEncoder.encode(path, "UTF-8")}"
    }
    data object Settings : Screen("settings")
}
