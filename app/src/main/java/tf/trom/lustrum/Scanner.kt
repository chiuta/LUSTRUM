package tf.trom.lustrum

import android.content.ContentUris
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import java.io.File

object Scanner {

    fun categoryOf(name: String): Category =
        when (name.substringAfterLast('.', "").lowercase()) {
            "jpg", "jpeg", "png", "gif", "webp", "heic", "heif", "bmp", "dng" -> Category.IMAGINI
            "mp4", "mkv", "webm", "3gp", "avi", "mov", "m4v" -> Category.VIDEO
            "mp3", "m4a", "aac", "ogg", "opus", "flac", "wav", "amr" -> Category.AUDIO
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "epub", "odt", "ods", "csv", "rtf" -> Category.DOCUMENTE
            "zip", "rar", "7z", "tar", "gz", "apk", "xapk", "iso" -> Category.ARHIVE
            else -> Category.ALTELE
        }

    /** Doar media (imagini, video, audio) – funcționează cu permisiunile obișnuite. */
    fun scanMedia(ctx: Context, onProgress: (Int) -> Unit): List<Item> {
        val out = ArrayList<Item>()
        val sources = listOf(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI to Category.IMAGINI,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI to Category.VIDEO,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI to Category.AUDIO,
        )
        val proj = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.DATA,
        )
        for ((base, cat) in sources) {
            ctx.contentResolver.query(base, proj, "${MediaStore.MediaColumns.SIZE} > 0", null, null)?.use { c ->
                val iId = c.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val iName = c.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
                val iSize = c.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                val iMod = c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)
                val iData = c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                while (c.moveToNext()) {
                    val name = c.getString(iName) ?: continue
                    val path = c.getString(iData) ?: continue
                    out.add(
                        Item(
                            name = name,
                            path = path,
                            size = c.getLong(iSize),
                            modified = c.getLong(iMod) * 1000,
                            category = cat,
                            uri = ContentUris.withAppendedId(base, c.getLong(iId)),
                        )
                    )
                    if (out.size % 500 == 0) onProgress(out.size)
                }
            }
        }
        return out
    }

    /** Tot spațiul de stocare partajat – cere „Acces la toate fișierele”. */
    fun scanFs(onProgress: (Int) -> Unit): List<Item> {
        val root = Environment.getExternalStorageDirectory()
        val out = ArrayList<Item>()
        root.walkTopDown()
            .maxDepth(12)
            .onEnter { !(it.parentFile == root && it.name == "Android") }
            .forEach { f ->
                if (f.isFile) {
                    val len = f.length()
                    if (len > 0) {
                        out.add(Item(f.name, f.path, len, f.lastModified(), categoryOf(f.name)))
                        if (out.size % 500 == 0) onProgress(out.size)
                    }
                }
            }
        return out
    }

    private val STD_DIRS = setOf(
        "DCIM", "Download", "Pictures", "Music", "Movies", "Documents", "Alarms",
        "Notifications", "Ringtones", "Podcasts", "Audiobooks", "Recordings",
    )
    private val PKG_NAME = Regex("^[a-z][a-z0-9_]*(\\.[a-z0-9_]+)+$")

    private fun dirSize(d: File): Long = d.walkTopDown().filter { it.isFile }.sumOf { it.length() }

    /** Foldere rămase în spațiul partajat de la aplicații care nu mai sunt instalate. */
    fun orphans(ctx: Context): List<Item> {
        val installed = ctx.packageManager.getInstalledPackages(0).map { it.packageName }.toSet()
        val root = Environment.getExternalStorageDirectory()
        val candidates = ArrayList<File>()
        for (sub in listOf("Android/media", "Android/obb")) {
            File(root, sub).listFiles()?.let { candidates.addAll(it) }
        }
        root.listFiles()?.let { candidates.addAll(it.filter { f -> f.name !in STD_DIRS }) }
        return candidates
            .filter { it.isDirectory && PKG_NAME.matches(it.name) && it.name !in installed }
            .map { Item(it.name, it.path, dirSize(it), it.lastModified(), Category.ALTELE, null, true) }
            .filter { it.size > 0 }
            .sortedByDescending { it.size }
    }

    /** Foldere complet goale (fără cele standard ale sistemului). */
    fun emptyDirs(): List<Item> {
        val root = Environment.getExternalStorageDirectory()
        val out = ArrayList<Item>()
        root.walkTopDown()
            .maxDepth(12)
            .onEnter { !(it.parentFile == root && it.name == "Android") }
            .forEach { d ->
                if (d.isDirectory && d != root &&
                    !(d.parentFile == root && d.name in STD_DIRS) &&
                    d.listFiles()?.isEmpty() == true
                ) out.add(Item(d.name, d.path, 0, d.lastModified(), Category.ALTELE, null, true))
            }
        return out
    }

    fun isScreenshot(i: Item): Boolean {
        if (i.category != Category.IMAGINI) return false
        val p = i.path.lowercase()
        return "screenshot" in p || "captur" in p
    }

    fun isInstaller(i: Item): Boolean =
        i.name.substringAfterLast('.', "").lowercase() in setOf("apk", "xapk", "apks")

    /** Fotografii JPEG mari care pot fi recomprimate. */
    fun isCompressible(i: Item): Boolean =
        i.name.substringAfterLast('.', "").lowercase() in setOf("jpg", "jpeg") && i.size >= 1_500_000

    private val JUNK_EXT = setOf("tmp", "temp", "log", "bak", "old", "thumbdata", "chk", "dmp", "crdownload", "part")
    private val JUNK_DIRS = setOf(".thumbnails", ".cache", "cache", "lost.dir", ".trash")

    /** Resturi evidente: temporare, miniaturi, descărcări întrerupte, coșul Android (.trashed-*). */
    fun isJunk(i: Item): Boolean {
        val lower = i.name.lowercase()
        if (lower.startsWith(".trashed-")) return true
        if (lower.substringAfterLast('.', "") in JUNK_EXT) return true
        return i.folder.lowercase().split('/').any { it in JUNK_DIRS }
    }
}
