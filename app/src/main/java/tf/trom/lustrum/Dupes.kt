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
            .sortedWith(compareByDescending<Item> { it.size }.thenBy { it.modified })
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
        return cluster(LongArray(idx.size) { hashes[idx[it]] })
            .map { g -> g.map { imgs[idx[it]] } } // fiecare grup: cel mai mare (calitate) primul
            .sortedByDescending { g -> g.sumOf { it.size } }
    }

    /**
     * Grupează hash-uri cu distanța Hamming ≤ maxDist față de un „reprezentant” (fără înlănțuire:
     * A~B și B~C nu înseamnă A~C). Candidații vin din 5 găleți de câte ~13 biți (principiul cutiei:
     * două hash-uri la distanță ≤ 4 au cel puțin o felie identică), deci nu e comparație n².
     * Ordinea de intrare = prioritatea reprezentanților. Returnează liste de indici.
     */
    fun cluster(hashes: LongArray, maxDist: Int = 4): List<List<Int>> {
        val slices = 5
        val buckets = Array(slices) { HashMap<Int, MutableList<Int>>() }
        fun key(h: Long, s: Int): Int {
            val w = if (s == slices - 1) 12 else 13
            return ((h ushr (s * 13)) and ((1L shl w) - 1)).toInt()
        }
        for (i in hashes.indices) for (s in 0 until slices) buckets[s].getOrPut(key(hashes[i], s)) { mutableListOf() }.add(i)
        val used = BooleanArray(hashes.size)
        val out = ArrayList<List<Int>>()
        for (i in hashes.indices) {
            if (used[i]) continue
            used[i] = true
            val g = arrayListOf(i)
            for (s in 0 until slices) {
                for (j in buckets[s][key(hashes[i], s)].orEmpty()) {
                    if (!used[j] && java.lang.Long.bitCount(hashes[i] xor hashes[j]) <= maxDist) {
                        used[j] = true
                        g.add(j)
                    }
                }
            }
            if (g.size > 1) out.add(g)
        }
        return out
    }
}
