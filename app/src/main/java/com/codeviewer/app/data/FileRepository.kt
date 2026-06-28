package com.codeviewer.app.data

import com.codeviewer.app.util.FileUtils
import java.io.File

data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long,
    val extension: String
)

class FileRepository {
    fun listDirectory(path: String): List<FileItem> {
        val dir = File(path)
        if (!dir.exists() || !dir.isDirectory) return emptyList()

        val files = dir.listFiles() ?: return emptyList()
        return files
            .filter { !it.name.startsWith(".") || it.name == ".gitignore" || it.name == ".env" }
            .map { file ->
                FileItem(
                    name = file.name,
                    path = file.absolutePath,
                    isDirectory = file.isDirectory,
                    size = if (file.isFile) file.length() else 0L,
                    lastModified = file.lastModified(),
                    extension = file.extension.lowercase()
                )
            }
            .sortedWith(compareBy<FileItem> { !it.isDirectory }.thenBy { it.name.lowercase() })
    }

    fun readFile(path: String): String {
        val file = File(path)
        if (!file.exists() || !file.isFile) return ""
        return file.readText()
    }

    fun writeFile(path: String, content: String) {
        val file = File(path)
        file.writeText(content)
    }

    fun getParentPath(path: String): String? {
        val file = File(path)
        return file.parent
    }

    fun canRead(path: String): Boolean {
        return File(path).let { it.exists() && it.canRead() }
    }

    fun isReadableTextFile(path: String): Boolean {
        val file = File(path)
        return file.isFile && FileUtils.isTextFile(file) && file.length() < 5 * 1024 * 1024
    }

    fun exists(parentDir: String, name: String): Boolean =
        File(parentDir, name).exists()

    /** Creates an empty file [name] inside [parentDir]. Returns the new path, or null on failure. */
    fun createFile(parentDir: String, name: String): String? {
        return try {
            val file = File(parentDir, name)
            file.parentFile?.mkdirs()
            if (file.exists()) null else if (file.createNewFile()) file.absolutePath else null
        } catch (_: Exception) {
            null
        }
    }

    /** Creates a folder [name] (including intermediate dirs) inside [parentDir]. */
    fun createFolder(parentDir: String, name: String): String? {
        return try {
            val dir = File(parentDir, name)
            if (dir.exists()) null else if (dir.mkdirs()) dir.absolutePath else null
        } catch (_: Exception) {
            null
        }
    }

    /** Renames a file or folder to [newName] (kept in the same parent). Returns the new path, or null on failure. */
    fun rename(path: String, newName: String): String? {
        return try {
            val source = File(path)
            val target = File(source.parentFile, newName)
            if (!source.exists() || target.exists()) null
            else if (source.renameTo(target)) target.absolutePath else null
        } catch (_: Exception) {
            null
        }
    }

    /** Deletes a file or folder (recursively). */
    fun delete(path: String): Boolean {
        return try {
            File(path).deleteRecursively()
        } catch (_: Exception) {
            false
        }
    }
}
