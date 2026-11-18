package tf.trom.lustrum

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import java.security.MessageDigest
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

object Dupes {
    private const val MIN_SIZE = 4096L

    private fun digest(ctx: Context, item: Item, limit: Long): String? {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val stream = item.open(ctx) ?: return null
            stream.use { s ->
                val buf = ByteArray(65536)
                var left = limit
                while (left > 0) {
                    val n = s.read(buf, 0, minOf(buf.size.toLong(), left).toInt())
                    if (n < 0) break
                    md.update(buf, 0, n)
                    left -= n
                }
            }
            md.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            null
        }
    }

    /** Fișiere identice: grupare după mărime, apoi hash pe primii 64 KB, apoi hash complet. */
    fun exact(ctx: Context, items: List<Item>, progress: (Int, Int) -> Unit): List<List<Item>> {
        val candidates = items.filter { it.size >= MIN_SIZE }
            .groupBy { it.size }.values.filter { it.size > 1 }
        val total = candidates.sumOf { it.size }
        var done = 0
        val out = ArrayList<List<Item>>()
        for (sameSize in candidates) {
            val heads = HashMap<String, MutableList<Item>>()
            for (f in sameSize) {
                digest(ctx, f, 65536)?.let { h -> heads.getOrPut(h) { mutableListOf() }.add(f) }
                done++
                progress(done, total)
            }
            for (h in heads.values) {
                if (h.size < 2) continue
                val full = HashMap<String, MutableList<Item>>()
                for (f in h) {
                    digest(ctx, f, Long.MAX_VALUE)?.let { d -> full.getOrPut(d) { mutableListOf() }.add(f) }
                }
                full.values.filter { it.size > 1 }.forEach { g -> out.add(g.sortedBy { x -> x.modified }) }
            }
        }
        return out.sortedByDescending { g -> g[0].size * (g.size - 1) }
    }

    private fun lum(p: Int): Int = (Color.red(p) * 299 + Color.green(p) * 587 + Color.blue(p) * 114) / 1000

    /** dHash 64 biți: compară luminozitatea pixelilor vecini pe o miniatură 9×8. */
    private fun dhash(ctx: Context, item: Item): Long {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        item.open(ctx)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (bounds.outWidth / (sample * 2) >= 64 && bounds.outHeight / (sample * 2) >= 64) sample *= 2
        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val bmp = item.open(ctx)?.use { BitmapFactory.decodeStream(it, null, opts) }
            ?: throw IllegalStateException("not an image")
        val small = Bitmap.createScaledBitmap(bmp, 9, 8, true)
        var h = 0L
        for (y in 0 until 8) {
            for (x in 0 until 8) {
                if (lum(small.getPixel(x, y)) > lum(small.getPixel(x + 1, y))) h = h or (1L shl (y * 8 + x))
            }
        }
        if (small !== bmp) small.recycle()
        bmp.recycle()
        return h
    }

    /** Imagini asemănătoare (aceeași poză redimensionată, recomprimată, capturi repetate). */
    fun similar(ctx: Context, items: List<Item>, progress: (Int, Int) -> Unit): List<List<Item>> {
        val imgs = items.filter { it.category == Category.IMAGINI && it.size >= 10_000 }
        val hashes = LongArray(imgs.size)
        val ok = BooleanArray(imgs.size)
        val done = AtomicInteger()
        val pool = Executors.newFixedThreadPool(4)
        try {
            imgs.indices.map { i ->
                pool.submit(Runnable {
                    try {
                        val h = dhash(ctx, imgs[i])
                        hashes[i] = h
                        ok[i] = h != 0L // imaginile uniforme ar produce grupuri false
                    } catch (e: Throwable) {
                        // fișier ilizibil sau nu e imagine: se ignoră
                    }
                    progress(done.incrementAndGet(), imgs.size)
                })
            }.forEach { it.get() }
        } finally {
            pool.shutdown()
        }

        val idx = imgs.indices.filter { ok[it] }
        val parent = IntArray(imgs.size) { it }
        fun find(x: Int): Int {
            var r = x
            while (parent[r] != r) r = parent[r]
            var c = x
            while (parent[c] != r) {
                val n = parent[c]
                parent[c] = r
                c = n
            }
            return r
        }
        for (a in idx.indices) {
            for (b in a + 1 until idx.size) {
                if (java.lang.Long.bitCount(hashes[idx[a]] xor hashes[idx[b]]) <= 4) {
                    parent[find(idx[a])] = find(idx[b])
                }
            }
        }
        return idx.groupBy { find(it) }.values
            .filter { it.size > 1 }
            .map { g -> g.map { imgs[it] }.sortedBy { it.modified } }
            .sortedByDescending { g -> g.sumOf { it.size } }
    }
}
