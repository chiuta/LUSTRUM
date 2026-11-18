package tf.trom.lustrum

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream
import java.text.DateFormat
import java.util.Date

enum class Category(val label: String) {
    IMAGINI("Imagini"),
    VIDEO("Video"),
    AUDIO("Audio"),
    DOCUMENTE("Documente"),
    ARHIVE("Arhive și instalări"),
    ALTELE("Altele"),
}

/** Un fișier găsit la scanare. `uri` există doar pentru fișierele din MediaStore. */
data class Item(
    val name: String,
    val path: String,
    val size: Long,
    val modified: Long,
    val category: Category,
    val uri: Uri? = null,
) {
    val folder: String get() = path.substringBeforeLast('/', "")

    fun open(ctx: Context): InputStream? =
        if (uri != null) ctx.contentResolver.openInputStream(uri) else File(path).inputStream()
}

data class AppInfo(
    val pkg: String,
    val label: String,
    val cache: Long,
    val total: Long,
    val lastUsed: Long,
    val system: Boolean,
)

data class Perms(
    val media: Boolean = false,
    val allFiles: Boolean = false,
    val usage: Boolean = false,
)

fun fmtSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val units = arrayOf("KB", "MB", "GB", "TB")
    var v = bytes.toDouble()
    var i = -1
    while (v >= 1024 && i < units.size - 1) {
        v /= 1024
        i++
    }
    return "%.1f %s".format(v, units[i])
}

fun fmtDate(ms: Long): String =
    if (ms <= 0) "—" else DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(ms))
