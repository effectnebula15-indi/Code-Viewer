package com.codeviewer.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

private val Context.projectsDataStore: DataStore<Preferences> by preferencesDataStore(name = "projects")

data class RecentProject(
    val path: String,
    val name: String
)

/** Persists the list of recently opened project folders, most-recent first. */
class ProjectStore(private val context: Context) {
    private val recentKey = stringPreferencesKey("recent_projects")

    val recentProjects: Flow<List<RecentProject>> = context.projectsDataStore.data.map { prefs ->
        (prefs[recentKey] ?: "")
            .split("\n")
            .filter { it.isNotBlank() }
            .map { path -> RecentProject(path, File(path).name.ifEmpty { path }) }
            .filter { File(it.path).isDirectory }
    }

    suspend fun addProject(path: String) {
        context.projectsDataStore.edit { prefs ->
            val current = (prefs[recentKey] ?: "")
                .split("\n")
                .filter { it.isNotBlank() && it != path }
            val updated = (listOf(path) + current).take(MAX_RECENT)
            prefs[recentKey] = updated.joinToString("\n")
        }
    }

    suspend fun removeProject(path: String) {
        context.projectsDataStore.edit { prefs ->
            val updated = (prefs[recentKey] ?: "")
                .split("\n")
                .filter { it.isNotBlank() && it != path }
            prefs[recentKey] = updated.joinToString("\n")
        }
    }

    companion object {
        private const val MAX_RECENT = 12
    }
}
