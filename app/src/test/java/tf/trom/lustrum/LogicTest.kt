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

class NewFeaturesTest {
    @Test fun flatImageHasZeroSharpnessAndIsBad() {
        val g = IntArray(64 * 64) { 128 }
        val s = Quality.score(g, 64, 64)
        assertEquals(0.0, s.sharpness, 0.0001)
        assertTrue(s.bad)
    }

    @Test fun checkerboardIsSharpAndBrightEnough() {
        val g = IntArray(64 * 64) { i -> if ((i % 64 + i / 64) % 2 == 0) 220 else 40 }
        val s = Quality.score(g, 64, 64)
        assertTrue(s.sharpness > Quality.SHARP_MIN)
        assertFalse(s.bad)
    }

    @Test fun darkImageIsFlagged() {
        val g = IntArray(64 * 64) { i -> if ((i % 64 + i / 64) % 2 == 0) 30 else 0 }
        assertTrue(Quality.score(g, 64, 64).bad)
    }

    @Test fun trashMoveRestoreAndEmpty() {
        val base = java.nio.file.Files.createTempDirectory("lustrum").toFile()
        try {
            val f = java.io.File(base, "DCIM/Camera/a.jpg").apply { parentFile!!.mkdirs(); writeText("x".repeat(100)) }
            val item = Item("a.jpg", f.path, 100, 0, Category.IMAGINI)
            val moved = Trash.move(listOf(item), base, 1000L)
            assertEquals(1, moved.size)
            assertFalse(f.exists())
            assertEquals(1, Trash.info(base).files)
            assertEquals(1, Trash.restoreLast(base).size)
            assertTrue(f.exists())
            assertEquals(0, Trash.info(base).files)
            Trash.move(listOf(item), base, 2000L)
            assertEquals(100L, Trash.empty(base))
            assertEquals(0, Trash.info(base).files)
        } finally {
            base.deleteRecursively()
        }
    }

    @Test fun purgeOldKeepsRecentBatches() {
        val base = java.nio.file.Files.createTempDirectory("lustrum").toFile()
        try {
            val f = java.io.File(base, "a.txt").apply { writeText("abc") }
            val item = Item("a.txt", f.path, 3, 0, Category.DOCUMENTE)
            Trash.move(listOf(item), base, 1000L)
            assertEquals(0L, Trash.purgeOld(base, 1000L + Trash.KEEP_MS - 1))
            assertEquals(3L, Trash.purgeOld(base, 1000L + Trash.KEEP_MS + 1))
        } finally {
            base.deleteRecursively()
        }
    }

    @Test fun restoreNeverOverwrites() {
        val base = java.nio.file.Files.createTempDirectory("lustrum").toFile()
        try {
            val f = java.io.File(base, "a.txt").apply { writeText("old") }
            Trash.move(listOf(Item("a.txt", f.path, 3, 0, Category.DOCUMENTE)), base, 5L)
            f.writeText("new")
            assertEquals(0, Trash.restoreLast(base).size)
            assertEquals("new", f.readText())
        } finally {
            base.deleteRecursively()
        }
    }
}

class ReviewFeaturesTest {
    private fun it(name: String, size: Long) = Item(name, "/s/$name", size, 0, Category.IMAGINI)

    @Test fun bestFirstPrefersSharperThenLarger() {
        val a = it("a.jpg", 5_000_000)   // mare, neclară
        val b = it("b.jpg", 1_000_000)   // mică, clară
        val c = it("c.jpg", 2_000_000)   // clară, mai mare decât b
        val sharp = mapOf(a to 5.0, b to 400.0, c to 410.0)
        val out = Dupes.bestFirst(listOf(a, b, c)) { sharp[it] }
        assertEquals(listOf("c.jpg", "b.jpg", "a.jpg"), out.map { x -> x.name })
    }

    @Test fun durationsMatchWithinThreePercentOrOneSecond() {
        assertTrue(Dupes.sameDuration(100_000, 102_500))
        assertFalse(Dupes.sameDuration(100_000, 110_000))
        assertTrue(Dupes.sameDuration(2_000, 2_900))
    }

    @Test fun clusterWithLooserDistanceForVideos() {
        val a = 0x0F0F0F0F0F0F0F0FL
        val b = a xor 0b111111L // distanța 6
        assertEquals(1, Dupes.cluster(longArrayOf(a, b), 6).size)
        assertEquals(0, Dupes.cluster(longArrayOf(a, b), 4).size)
    }
}
