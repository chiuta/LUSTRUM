package tf.trom.lustrum

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.util.LruCache
import android.util.Size
import java.io.File

/** Miniaturi mici pentru liste: din MediaStore când există URI, altfel decodate din fișier. */
object Thumbs {
    private val cache = LruCache<String, Bitmap>(96)
    private const val SIDE = 120

    fun load(ctx: Context, item: Item): Bitmap? {
        if (item.dir || (item.category != Category.IMAGINI && item.category != Category.VIDEO)) return null
        cache.get(item.path)?.let { return it }
        val u = item.uri
        val bmp: Bitmap? = try {
            when {
                u != null -> ctx.contentResolver.loadThumbnail(u, Size(SIDE, SIDE), null)
                item.category == Category.VIDEO -> ThumbnailUtils.createVideoThumbnail(File(item.path), Size(SIDE, SIDE), null)
                else -> decode(item.path)
            }
        } catch (t: Throwable) {
            null
        }
        if (bmp != null) cache.put(item.path, bmp)
        return bmp
    }

    private fun decode(path: String): Bitmap? {
        val b = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, b)
        var s = 1
        while (maxOf(b.outWidth, b.outHeight) / (s * 2) >= SIDE) s *= 2
        return BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = s })
    }
}
