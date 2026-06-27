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
}
