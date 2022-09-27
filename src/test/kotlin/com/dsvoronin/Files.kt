package com.dsvoronin

import java.io.File

internal fun File.file(name: String, content: String? = null): File {
    val file: File = if (name.contains('/')) {
        val directory = File(this, name.substringBeforeLast('/'))
        directory.mkdirs()
        File(directory, name.substringAfterLast('/'))
    } else {
        File(this, name)
    }
    if (!file.exists()) {
        file.createNewFile()
    }
    if (content != null) {
        file.writeText(content.trimIndent())
    }
    return file
}
