package tf.trom.lustrum

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.ParcelFileDescriptor
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Random

/** Rulează pe un emulator Android real în CI: scanare, duplicate, comprimare, coș, programare, interfață. */
@RunWith(AndroidJUnit4::class)
class DeviceTest {
    private val ctx: Context = ApplicationProvider.getApplicationContext()
    private val dir = File(Environment.getExternalStorageDirectory(), "LUSTRUM-test")
    private val old = System.currentTimeMillis() - 3L * 24 * 3600 * 1000

    private fun shell(cmd: String) {
        val fd = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(cmd)
        ParcelFileDescriptor.AutoCloseInputStream(fd).use { it.readBytes() }
    }

    @Before fun setUp() {
        shell("appops set --uid ${ctx.packageName} MANAGE_EXTERNAL_STORAGE allow")
        dir.deleteRecursively()
        dir.mkdirs()
    }

    @After fun tearDown() {
        dir.deleteRecursively()
        Trash.empty()
    }

    private fun item(f: File, cat: Category) = Item(f.name, f.path, f.length(), f.lastModified(), cat)

    private fun noiseJpeg(f: File, side: Int = 1800) {
        val r = Random(7)
        val px = IntArray(side * side) { 0xFF000000.toInt() or r.nextInt(0xFFFFFF) }
        val bmp = Bitmap.createBitmap(px, side, side, Bitmap.Config.ARGB_8888)
        f.outputStream().use { bmp.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        bmp.recycle()
    }

    @Test fun allFilesAccessIsGranted() {
        assertTrue(Environment.isExternalStorageManager())
    }

    @Test fun activityLaunchesWithoutCrashing() {
        ActivityScenario.launch(MainActivity::class.java).use {
            assertEquals(Lifecycle.State.RESUMED, it.state)
        }
    }

    @Test fun scannerFindsJunkAndExactDuplicates() {
        val tmp = File(dir, "leftover.tmp").apply { writeBytes(ByteArray(8000) { 1 }); setLastModified(old) }
        val bytes = ByteArray(20_000).also { Random(3).nextBytes(it) }
        File(dir, "one.bin").writeBytes(bytes)
        File(dir, "two.bin").writeBytes(bytes)
        val found = Scanner.scanFs { }.filter { it.path.startsWith(dir.path) }
        assertTrue(found.any { it.path == tmp.path && Scanner.isJunk(it) })
        val groups = Dupes.exact(ctx, found) { _, _ -> }
        assertTrue(groups.any { g -> g.map { it.name }.toSet() == setOf("one.bin", "two.bin") })
    }

    @Test fun compressorShrinksLargeJpegAndKeepsItReadable() {
        val f = File(dir, "big.jpg").also { noiseJpeg(it) }
        val before = f.length()
        assertTrue(Scanner.isCompressible(item(f, Category.IMAGINI)))
        val after = Compressor.compress(item(f, Category.IMAGINI))
        assertNotNull(after)
        assertTrue("expected <80%: $after of $before", after!! < before * 8 / 10)
        assertNotNull(BitmapFactory.decodeFile(f.path))
    }

    @Test fun thumbnailsAndQualityWorkOnRealImages() {
        val sharp = File(dir, "sharp.jpg").also { noiseJpeg(it, 600) }
        val flat = File(dir, "flat.jpg").also {
            val b = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_8888).apply { eraseColor(0xFF808080.toInt()) }
            it.outputStream().use { o -> b.compress(Bitmap.CompressFormat.JPEG, 90, o) }
        }
        assertNotNull(Thumbs.load(ctx, item(sharp, Category.IMAGINI)))
        val s = Quality.analyze(ctx, item(sharp, Category.IMAGINI))
        val fl = Quality.analyze(ctx, item(flat, Category.IMAGINI))
        assertNotNull(s); assertNotNull(fl)
        assertFalse(s!!.bad)
        assertTrue(fl!!.bad)
    }

    @Test fun trashMovesAndRestoresOnRealStorage() {
        val f = File(dir, "keep-me.txt").apply { writeText("hello") }
        val moved = Trash.move(listOf(item(f, Category.DOCUMENTE)))
        assertEquals(1, moved.size)
        assertFalse(f.exists())
        assertEquals(1, Trash.restoreLast().size)
        assertEquals("hello", f.readText())
    }

    @Test fun scheduledRunCleansOnlyOldJunk() {
        val oldTmp = File(dir, "old.tmp").apply { writeBytes(ByteArray(5000)); setLastModified(old) }
        val freshTmp = File(dir, "fresh.tmp").apply { writeBytes(ByteArray(5000)) }
        val report = Scheduler.runOnce(ctx, autoClean = true, useTrash = false)
        assertTrue(report.cleaned)
        assertFalse(oldTmp.exists())
        assertTrue(freshTmp.exists())
    }

    @Test fun cacheTrimNeverThrows() {
        assertTrue(CacheTrim.trim(ctx, 0L) >= 0L)
    }
}
