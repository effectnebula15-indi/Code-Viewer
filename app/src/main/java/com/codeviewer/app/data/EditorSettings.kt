package com.codeviewer.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.editorDataStore: DataStore<Preferences> by preferencesDataStore(name = "editor")

enum class SortMode(val displayName: String) {
    NAME_ASC("Name (A → Z)"),
    NAME_DESC("Name (Z → A)"),
    TYPE("Type"),
    MODIFIED("Last modified"),
    CUSTOM("Custom")
}

const val MIN_FONT_SIZE = 9
const val MAX_FONT_SIZE = 28
const val DEFAULT_FONT_SIZE = 13

/** Editor + project-tree preferences: code font size, sort mode and custom file order. */
class EditorSettings(private val context: Context) {
    private val fontSizeKey = intPreferencesKey("font_size")
    private val sortModeKey = stringPreferencesKey("sort_mode")
    private val customOrderKey = stringPreferencesKey("custom_order")

    val fontSize: Flow<Int> = context.editorDataStore.data.map { prefs ->
        (prefs[fontSizeKey] ?: DEFAULT_FONT_SIZE).coerceIn(MIN_FONT_SIZE, MAX_FONT_SIZE)
    }

    val sortMode: Flow<SortMode> = context.editorDataStore.data.map { prefs ->
        prefs[sortModeKey]?.let { runCatching { SortMode.valueOf(it) }.getOrNull() } ?: SortMode.NAME_ASC
    }

    /** Map of directory path -> ordered child names (used only in Custom sort mode). */
    val customOrder: Flow<Map<String, List<String>>> = context.editorDataStore.data.map { prefs ->
        decodeOrder(prefs[customOrderKey] ?: "")
    }

    suspend fun setFontSize(size: Int) {
        context.editorDataStore.edit { it[fontSizeKey] = size.coerceIn(MIN_FONT_SIZE, MAX_FONT_SIZE) }
    }

    suspend fun setSortMode(mode: SortMode) {
        context.editorDataStore.edit { it[sortModeKey] = mode.name }
    }

    suspend fun setCustomOrderFor(dir: String, names: List<String>) {
        context.editorDataStore.edit { prefs ->
            val map = decodeOrder(prefs[customOrderKey] ?: "").toMutableMap()
            map[dir] = names
            prefs[customOrderKey] = encodeOrder(map)
        }
    }

    companion object {
        private fun encodeOrder(map: Map<String, List<String>>): String {
            val obj = JSONObject()
            map.forEach { (dir, names) -> obj.put(dir, JSONArray(names)) }
            return obj.toString()
        }

        private fun decodeOrder(raw: String): Map<String, List<String>> {
            if (raw.isBlank()) return emptyMap()
            return try {
                val obj = JSONObject(raw)
                buildMap {
                    obj.keys().forEach { key ->
                        val arr = obj.getJSONArray(key)
                        put(key, (0 until arr.length()).map { arr.getString(it) })
                    }
                }
            } catch (_: Exception) {
                emptyMap()
            }
        }
    }
}
