package tf.trom.lustrum

import android.os.Environment
import java.io.File

/**
 * Coș propriu: fișierele sunt mutate (nu copiate) în `.LUSTRUM-trash/<moment>/<cale relativă>` pe același volum,
 * deci mutarea e instantanee, se poate anula, dar spațiul se eliberează abia la golire sau după 30 de zile.
 */
object Trash {
    const val DIR = ".LUSTRUM-trash"
    const val KEEP_MS = 30L * 24 * 3600 * 1000

    data class Info(val files: Int, val bytes: Long)

    fun base(): File = Environment.getExternalStorageDirectory()

    private fun rel(base: File, f: File): String? {
        val b = base.path + "/"
        return if (f.path.startsWith(b)) f.path.removePrefix(b) else null
    }

    fun move(items: List<Item>, base: File = base(), stamp: Long = System.currentTimeMillis()): List<Item> {
        val root = File(base, DIR)
        root.mkdirs()
        val nomedia = File(root, ".nomedia")
        if (!nomedia.exists()) nomedia.createNewFile()
        val batch = File(root, stamp.toString())
        val moved = ArrayList<Item>()
        for (it in items) {
            val r = rel(base, File(it.path)) ?: continue
            val dest = File(batch, r)
            dest.parentFile?.mkdirs()
            if (File(it.path).renameTo(dest)) moved.add(it)
        }
        return moved
    }

    private fun batches(base: File): List<File> =
        File(base, DIR).listFiles { f -> f.isDirectory }?.sortedBy { it.name.toLongOrNull() ?: 0L } ?: emptyList()

    fun info(base: File = base()): Info {
        var n = 0
        var bytes = 0L
        for (b in batches(base)) b.walkTopDown().filter { it.isFile }.forEach { n++; bytes += it.length() }
        return Info(n, bytes)
    }

    /** Readuce la locul inițial ultima ștergere; fișierele care ar suprascrie ceva rămân în coș. Returnează căile refăcute. */
    fun restoreLast(base: File = base()): List<String> {
        val batch = batches(base).lastOrNull() ?: return emptyList()
        val restored = ArrayList<String>()
        batch.walkTopDown().filter { it.isFile }.toList().forEach { f ->
            val dest = File(base, f.path.removePrefix(batch.path + "/"))
            if (!dest.exists()) {
                dest.parentFile?.mkdirs()
                if (f.renameTo(dest)) restored.add(dest.path)
            }
        }
        batch.walkBottomUp().filter { it.isDirectory && it.list().isNullOrEmpty() }.forEach { it.delete() }
        return restored
    }

    fun empty(base: File = base()): Long {
        val bytes = info(base).bytes
        batches(base).forEach { it.deleteRecursively() }
        return bytes
    }

    fun purgeOld(base: File = base(), now: Long = System.currentTimeMillis()): Long {
        var freed = 0L
        for (b in batches(base)) {
            val t = b.name.toLongOrNull() ?: continue
            if (now - t > KEEP_MS) {
                freed += b.walkTopDown().filter { it.isFile }.sumOf { it.length() }
                b.deleteRecursively()
            }
        }
        return freed
    }
}

/** Reguli comune de siguranță pentru orice ștergere (manuală sau programată). */
object Safety {
    /** Nu atinge nimic în afara stocării partajate, rădăcina, „Android” sau legăturile simbolice. */
    fun ok(it: Item, root: File): Boolean {
        val f = File(it.path)
        val c = try { f.canonicalFile } catch (e: Exception) { return false }
        if (c.path != f.absoluteFile.normalize().path) return false
        if (!c.path.startsWith(root.path + "/")) return false
        return c.path.removePrefix(root.path + "/") != "Android"
    }
}
