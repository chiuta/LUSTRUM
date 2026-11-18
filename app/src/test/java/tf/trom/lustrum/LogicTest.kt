package tf.trom.lustrum

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LogicTest {
    private val now = 2_000_000_000_000L
    private val old = now - 3L * 24 * 3600 * 1000

    private fun item(path: String, size: Long = 1000, modified: Long = old, cat: Category? = null): Item {
        val name = path.substringAfterLast('/')
        return Item(name, path, size, modified, cat ?: Scanner.categoryOf(name))
    }

    @Test fun junkRecognisesTemporaryFiles() {
        assertTrue(Scanner.isJunk(item("/s/Download/a.tmp"), now))
        assertTrue(Scanner.isJunk(item("/s/Download/film.mp4.crdownload"), now))
        assertTrue(Scanner.isJunk(item("/s/DCIM/.thumbnails/1.jpg"), now))
        assertTrue(Scanner.isJunk(item("/s/DCIM/.trashed-1700-IMG.jpg"), now))
    }

    @Test fun junkSkipsBackupsAndFreshDownloads() {
        assertFalse(Scanner.isJunk(item("/s/Documents/tax.bak"), now))
        assertFalse(Scanner.isJunk(item("/s/Documents/notes.old"), now))
        assertFalse(Scanner.isJunk(item("/s/Documents/cache/report.pdf"), now))
        assertFalse(Scanner.isJunk(item("/s/Download/film.part", modified = now - 1000), now))
    }

    @Test fun screenshotsOnlyByNameOrFolder() {
        assertTrue(Scanner.isScreenshot(item("/s/Pictures/Screenshots/a.png")))
        assertTrue(Scanner.isScreenshot(item("/s/DCIM/Screenshot_2026.jpg")))
        assertTrue(Scanner.isScreenshot(item("/s/Pictures/Capturi de ecran/b.png")))
        assertFalse(Scanner.isScreenshot(item("/s/DCIM/Camera/Capture_0001.jpg")))
        assertFalse(Scanner.isScreenshot(item("/s/Pictures/Captured wildlife/fox.jpg")))
    }

    @Test fun compressibleNeedsLargeJpeg() {
        assertTrue(Scanner.isCompressible(item("/s/a.JPG", size = 3_000_000)))
        assertFalse(Scanner.isCompressible(item("/s/a.jpg", size = 100_000)))
        assertFalse(Scanner.isCompressible(item("/s/a.png", size = 9_000_000)))
    }

    @Test fun clusterGroupsNearHashesWithoutChaining() {
        val a = 0x0123456789ABCDEFL
        val b = a xor 0b1111L            // distanța 4 față de a
        val c = b xor (0b1111L shl 20)   // 4 față de b, 8 față de a: nu intră în grupul lui a
        val far = a.inv()
        val g = Dupes.cluster(longArrayOf(a, b, c, far))
        assertEquals(1, g.size)
        assertEquals(listOf(0, 1), g[0].sorted())
    }

    @Test fun clusterFindsMatchesAcrossAllSlices() {
        val base = 0x5555AAAA5555AAAAL
        for (bit in 0 until 64) {
            val g = Dupes.cluster(longArrayOf(base, base xor (1L shl bit)))
            assertEquals("bit $bit", 1, g.size)
        }
    }

    @Test fun clusterKeepsInputPriorityAsRepresentative() {
        val g = Dupes.cluster(longArrayOf(7L, 7L, 7L))
        assertEquals(listOf(0, 1, 2), g[0])
    }

    @Test fun sizeFormatting() {
        assertEquals("512 B", fmtSize(512))
        assertTrue(fmtSize(5L * 1024 * 1024).contains("MB"))
    }
}
