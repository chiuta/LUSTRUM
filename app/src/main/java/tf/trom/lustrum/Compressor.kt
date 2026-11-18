package tf.trom.lustrum

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream

object Compressor {
    private val TAGS = listOf(
        ExifInterface.TAG_ORIENTATION, ExifInterface.TAG_DATETIME, ExifInterface.TAG_DATETIME_ORIGINAL,
        ExifInterface.TAG_GPS_LATITUDE, ExifInterface.TAG_GPS_LATITUDE_REF,
        ExifInterface.TAG_GPS_LONGITUDE, ExifInterface.TAG_GPS_LONGITUDE_REF,
        ExifInterface.TAG_MAKE, ExifInterface.TAG_MODEL,
    )

    /**
     * Recomprimă un JPEG la calitate 80. Înlocuiește originalul doar dacă fișierul nou
     * este cu cel puțin 20% mai mic; păstrează data modificării și etichetele EXIF principale.
     * Returnează noua mărime, sau null dacă fișierul a fost lăsat neatins.
     */
    fun compress(item: Item): Long? {
        val f = File(item.path)
        val before = f.length()
        val tmp = File(f.parentFile, ".lustrum-${f.name}.tmp")
        try {
            if (hasEmbeddedExtras(f)) return null
            val bmp = BitmapFactory.decodeFile(f.path) ?: return null
            try {
                FileOutputStream(tmp).use { bmp.compress(Bitmap.CompressFormat.JPEG, 80, it) }
            } finally {
                bmp.recycle()
            }
            val after = tmp.length()
            if (after <= 0 || after > before * 0.8) return null
            try {
                val src = ExifInterface(f.path)
                val dst = ExifInterface(tmp.path)
                for (t in TAGS) src.getAttribute(t)?.let { dst.setAttribute(t, it) }
                dst.saveAttributes()
            } catch (e: Exception) {
                // fără EXIF: continuăm
            }
            val mod = f.lastModified()
            if (!tmp.renameTo(f)) return null
            f.setLastModified(mod)
            return f.length()
        } catch (e: Throwable) {
            return null
        } finally {
            if (tmp.exists()) tmp.delete()
        }
    }

    private val MARKERS = listOf("MotionPhoto", "MicroVideo", "GainMap", "hdrgm", "Container:Directory")

    /**
     * Fotografiile cu video încorporat (Motion/Live Photo) sau hartă HDR s-ar strica la recomprimare:
     * se caută marcajele XMP în primii 256 KB și fișierul se lasă neatins.
     */
    fun hasEmbeddedExtras(f: File): Boolean = try {
        val head = ByteArray(minOf(f.length(), 262_144L).toInt())
        f.inputStream().use { it.read(head) }
        val text = String(head, Charsets.ISO_8859_1)
        MARKERS.any { it in text }
    } catch (e: Exception) {
        true // la îndoială, nu se atinge
    }
}
