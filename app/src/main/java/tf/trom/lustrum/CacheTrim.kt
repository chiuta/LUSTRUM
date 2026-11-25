package tf.trom.lustrum

import android.content.Context
import android.os.StatFs
import android.os.storage.StorageManager
import java.io.File
import java.io.FileOutputStream

/**
 * Golirea cache-ului altor aplicații fără root: cere sistemului spațiu liber (allocateBytes);
 * Android șterge singur datele din cache de care are nevoie ca să-l ofere. Rezultatul diferă între dispozitive.
 */
object CacheTrim {
    /** Returnează octeții câștigați (măsurați), sau 0 dacă sistemul nu a eliberat nimic. */
    fun trim(ctx: Context, expectedCache: Long): Long {
        val sm = ctx.getSystemService(StorageManager::class.java) ?: return 0L
        val dir = ctx.filesDir
        val tmp = File(dir, "trim.tmp")
        return try {
            val uuid = sm.getUuidForPath(dir)
            val before = StatFs(dir.path).availableBytes
            val allocatable = sm.getAllocatableBytes(uuid)
            if (allocatable <= before) return 0L
            val want = minOf(allocatable, before + maxOf(expectedCache, 256L * 1024 * 1024))
            FileOutputStream(tmp).use { sm.allocateBytes(it.fd, want) }
            tmp.delete()
            maxOf(0L, StatFs(dir.path).availableBytes - before)
        } catch (t: Throwable) {
            0L
        } finally {
            tmp.delete()
        }
    }
}
