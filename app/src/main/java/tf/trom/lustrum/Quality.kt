package tf.trom.lustrum

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * Detectează fotografiile probabil neclare sau prea întunecate.
 * Claritatea = varianța laplacianului pe o copie mică în tonuri de gri; luminozitatea = media.
 * Este o euristică: rezultatele se verifică de om înainte de ștergere.
 */
object Quality {
    const val SHARP_MIN = 25.0
    const val DARK_MAX = 22.0

    data class Score(val sharpness: Double, val luma: Double) {
        val bad: Boolean get() = sharpness < SHARP_MIN || luma < DARK_MAX
    }

    fun laplacianVariance(g: IntArray, w: Int, h: Int): Double {
        if (w < 3 || h < 3) return Double.MAX_VALUE
        var sum = 0.0
        var sq = 0.0
        var n = 0
        for (y in 1 until h - 1) for (x in 1 until w - 1) {
            val i = y * w + x
            val v = (4 * g[i] - g[i - 1] - g[i + 1] - g[i - w] - g[i + w]).toDouble()
            sum += v
            sq += v * v
            n++
        }
        val mean = sum / n
        return sq / n - mean * mean
    }

    fun meanLuma(g: IntArray): Double = if (g.isEmpty()) 0.0 else g.sumOf { it.toLong() }.toDouble() / g.size

    fun score(g: IntArray, w: Int, h: Int) = Score(laplacianVariance(g, w, h), meanLuma(g))

    fun analyze(ctx: Context, item: Item): Score? = try {
        val b = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        item.open(ctx)?.use { BitmapFactory.decodeStream(it, null, b) }
        var s = 1
        while (maxOf(b.outWidth, b.outHeight) / (s * 2) >= 256) s *= 2
        val o = BitmapFactory.Options().apply { inSampleSize = s; inPreferredConfig = Bitmap.Config.ARGB_8888 }
        val bmp = item.open(ctx)?.use { BitmapFactory.decodeStream(it, null, o) }
        if (bmp == null) null else {
            val w = bmp.width
            val h = bmp.height
            val px = IntArray(w * h)
            bmp.getPixels(px, 0, w, 0, 0, w, h)
            bmp.recycle()
            val g = IntArray(px.size) { i ->
                val p = px[i]
                (0.299 * ((p shr 16) and 255) + 0.587 * ((p shr 8) and 255) + 0.114 * (p and 255)).toInt()
            }
            score(g, w, h)
        }
    } catch (t: Throwable) {
        null
    }
}
