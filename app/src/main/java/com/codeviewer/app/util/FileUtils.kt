package com.codeviewer.app.util

import java.io.File

object FileUtils {
    private val codeExtensions = setOf(
        "kt", "kts", "java", "py", "js", "ts", "tsx", "jsx",
        "html", "htm", "css", "scss", "less",
        "xml", "json", "yaml", "yml", "toml",
        "c", "cpp", "h", "hpp", "cs",
        "go", "rs", "rb", "swift", "sh", "bash",
        "sql", "md", "txt", "gradle", "pro",
        "php", "dart", "lua", "r", "m", "mm",
        "vue", "svelte", "graphql", "gql",
        "dockerfile", "makefile", "cmake"
    )

    fun isCodeFile(file: File): Boolean {
        val ext = file.extension.lowercase()
        val name = file.name.lowercase()
        return ext in codeExtensions ||
            name == "dockerfile" ||
            name == "makefile" ||
            name == "cmakelists.txt" ||
            name == ".gitignore" ||
            name == ".env"
    }

    fun isTextFile(file: File): Boolean {
        if (isCodeFile(file)) return true
        val ext = file.extension.lowercase()
        return ext in setOf("txt", "log", "csv", "cfg", "conf", "ini", "properties")
    }

    fun getLanguageForExtension(extension: String): String {
        return when (extension.lowercase()) {
            "kt", "kts" -> "kotlin"
            "java" -> "java"
            "py" -> "python"
            "js", "jsx" -> "javascript"
            "ts", "tsx" -> "typescript"
            "html", "htm" -> "html"
            "xml", "svg" -> "xml"
            "json" -> "json"
            "css", "scss", "less" -> "css"
            "c", "h" -> "c"
            "cpp", "hpp", "cc", "cxx" -> "cpp"
            "go" -> "go"
            "rs" -> "rust"
            "rb" -> "ruby"
            "swift" -> "swift"
            "sh", "bash" -> "shell"
            "sql" -> "sql"
            "yaml", "yml" -> "yaml"
            "md" -> "markdown"
            "gradle" -> "kotlin"
            else -> "generic"
        }
    }

    fun formatFileSize(size: Long): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "%.1f KB".format(size / 1024.0)
            size < 1024 * 1024 * 1024 -> "%.1f MB".format(size / (1024.0 * 1024.0))
            else -> "%.1f GB".format(size / (1024.0 * 1024.0 * 1024.0))
        }
    }
}
