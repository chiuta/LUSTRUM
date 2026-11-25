package tf.trom.lustrum

import android.app.Application
import android.media.MediaScannerConnection
import android.os.Environment
import android.os.StatFs
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

enum class Filter { MARI, VECHI, RESTURI, CAPTURI, NECLARE, INSTALATOARE, COMPRIMARE, ORFANE, GOALE }

class LustrumVm(private val app: Application) : AndroidViewModel(app) {

    var perms by mutableStateOf(Perms())

    private val prefs = app.getSharedPreferences("lustrum", android.content.Context.MODE_PRIVATE)
    var useTrash by mutableStateOf(prefs.getBoolean("trash", false))
    var lifetimeFreed by mutableStateOf(prefs.getLong("freed", 0L))
    var trashFiles by mutableStateOf(0)
    var trashBytes by mutableStateOf(0L)

    // poze neclare / întunecate
    var blurry by mutableStateOf<List<Item>>(emptyList())
    var blurryDone by mutableStateOf(false)
    var blurBusy by mutableStateOf(false)
    var blurProgress by mutableStateOf("")

    // scanare
    var items by mutableStateOf<List<Item>>(emptyList())
    var scanning by mutableStateOf(false)
    var scanned by mutableStateOf(false)
    var scannedWithAll by mutableStateOf(false)
    var progress by mutableStateOf("")
    var totalBytes by mutableStateOf(0L)
    var freeBytes by mutableStateOf(0L)

    // fișiere mari / vechi / resturi
    var filter by mutableStateOf(Filter.MARI)

    var orphans by mutableStateOf<List<Item>>(emptyList())
    var emptyDirs by mutableStateOf<List<Item>>(emptyList())
    var compressing by mutableStateOf(false)

    // duplicate
    var dupMode by mutableStateOf(0) // 0 = identice, 1 = asemănătoare
    var exactGroups by mutableStateOf<List<List<Item>>>(emptyList())
    var similarGroups by mutableStateOf<List<List<Item>>>(emptyList())
    var exactDone by mutableStateOf(false)
    var similarDone by mutableStateOf(false)
    var dupBusy by mutableStateOf(false)
    var dupProgress by mutableStateOf("")

    // aplicații
    var apps by mutableStateOf<List<AppInfo>>(emptyList())
    var appsBusy by mutableStateOf(false)

    // selecție comună (cheie = cale)
    var selected by mutableStateOf<Set<String>>(emptySet())
    var message by mutableStateOf<String?>(null)

    var videoGroups by mutableStateOf<List<List<Item>>>(emptyList())
    var videoDone by mutableStateOf(false)

    val groups: List<List<Item>> get() = when (dupMode) { 0 -> exactGroups; 1 -> similarGroups; else -> videoGroups }
    val groupsDone: Boolean get() = when (dupMode) { 0 -> exactDone; 1 -> similarDone; else -> videoDone }

    // programare + Swiper
    var autoDays by mutableStateOf(prefs.getInt("auto_days", 0))
    var autoClean by mutableStateOf(prefs.getBoolean("auto_clean", false))
    var swipeQueue by mutableStateOf<List<Item>>(emptyList())
    var swipeIndex by mutableStateOf(0)
    var swipeMarked by mutableStateOf<List<Item>>(emptyList())

    fun setAuto(days: Int) {
        autoDays = days
        prefs.edit().putInt("auto_days", days).apply()
        Scheduler.apply(app, days)
    }

    fun updateAutoClean(on: Boolean) {
        autoClean = on
        prefs.edit().putBoolean("auto_clean", on).apply()
    }

    fun startSwipe() {
        swipeQueue = items.filter { it.category == Category.IMAGINI || it.category == Category.VIDEO }
            .sortedBy { it.modified }.take(500)
        swipeIndex = 0
        swipeMarked = emptyList()
    }

    fun swipe(delete: Boolean) {
        val cur = swipeQueue.getOrNull(swipeIndex) ?: return
        if (delete) swipeMarked = swipeMarked + cur
        swipeIndex += 1
    }

    fun swipeUndo() {
        if (swipeIndex == 0) return
        swipeIndex -= 1
        val cur = swipeQueue[swipeIndex]
        swipeMarked = swipeMarked.filter { it.path != cur.path }
    }

    fun resetSwipe() {
        swipeQueue = emptyList(); swipeIndex = 0; swipeMarked = emptyList()
    }

    fun trimCaches() {
        viewModelScope.launch(Dispatchers.IO) {
            val freed = CacheTrim.trim(app, apps.sumOf { it.cache })
            updateStorage()
            message = if (freed > 0) {
                addFreed(freed)
                app.getString(R.string.vm_trimmed, fmtSize(freed))
            } else app.getString(R.string.vm_trim_none)
        }
    }

    init {
        updateStorage()
    }

    private fun updateStorage() {
        val fs = StatFs(Environment.getExternalStorageDirectory().path)
        totalBytes = fs.blockCountLong * fs.blockSizeLong
        freeBytes = fs.availableBytes
    }

    fun list(f: Filter): List<Item> = when (f) {
        Filter.MARI -> items.filter { it.size >= 50L * 1024 * 1024 }.sortedByDescending { it.size }
        Filter.VECHI -> {
            val cut = System.currentTimeMillis() - 365L * 24 * 3600 * 1000
            items.filter { it.modified in 1 until cut && it.size >= 5L * 1024 * 1024 }
                .sortedByDescending { it.size }
        }
        Filter.RESTURI -> items.filter(Scanner::isJunk).sortedByDescending { it.size }
        Filter.CAPTURI -> items.filter(Scanner::isScreenshot).sortedByDescending { it.size }
        Filter.NECLARE -> blurry
        Filter.INSTALATOARE -> items.filter(Scanner::isInstaller).sortedByDescending { it.size }
        Filter.COMPRIMARE -> items.filter(Scanner::isCompressible).sortedByDescending { it.size }
        Filter.ORFANE -> orphans
        Filter.GOALE -> emptyDirs
    }

    fun toggle(path: String) {
        selected = if (path in selected) selected - path else selected + path
    }

    fun clearSelection() {
        selected = emptySet()
    }

    /** În fiecare grup păstrează cel mai vechi fișier și bifează restul. */
    fun autoSelect() {
        selected = selected + groups.flatMap { g -> g.drop(1) }.map { it.path }
    }

    fun scan() {
        if (scanning) return
        scanning = true
        progress = app.getString(R.string.vm_scanning)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (perms.allFiles) Trash.purgeOld()
                val found = if (perms.allFiles) {
                    Scanner.scanFs { n -> progress = app.getString(R.string.vm_scanning_n, n) }
                } else {
                    Scanner.scanMedia(app) { n -> progress = app.getString(R.string.vm_scanning_n, n) }
                }
                items = found
                if (perms.allFiles) {
                    progress = app.getString(R.string.vm_orphans)
                    orphans = Scanner.orphans(app)
                    emptyDirs = Scanner.emptyDirs()
                } else {
                    orphans = emptyList(); emptyDirs = emptyList()
                }
                scanned = true
                scannedWithAll = perms.allFiles
                selected = emptySet()
                exactGroups = emptyList(); similarGroups = emptyList()
                exactDone = false; similarDone = false
                blurry = emptyList(); blurryDone = false
                videoGroups = emptyList(); videoDone = false
                updateStorage()
                refreshTrash()
            } catch (e: Exception) {
                message = app.getString(R.string.vm_err_scan, e.message ?: "")
            } finally {
                scanning = false
            }
        }
    }

    fun findDupes(mode: Int) {
        if (dupBusy) return
        dupBusy = true
        dupProgress = app.getString(R.string.vm_analyzing)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val progress: (Int, Int) -> Unit = { d, t -> dupProgress = app.getString(R.string.vm_analyzing_n, d, t) }
                when (mode) {
                    1 -> {
                        val raw = Dupes.similar(app, items, progress)
                        similarGroups = raw.mapIndexed { gi, g ->
                            dupProgress = app.getString(R.string.vm_analyzing_n, gi + 1, raw.size)
                            val sc = g.associateWith { Quality.analyze(app, it)?.sharpness }
                            Dupes.bestFirst(g) { sc[it] }
                        }
                        similarDone = true
                    }
                    2 -> {
                        videoGroups = Dupes.similarVideos(app, items, progress)
                        videoDone = true
                    }
                    else -> {
                        exactGroups = Dupes.exact(app, items, progress)
                        exactDone = true
                    }
                }
            } catch (e: Exception) {
                message = app.getString(R.string.vm_err_analyze, e.message ?: "")
            } finally {
                dupBusy = false
            }
        }
    }

    fun findBlurry() {
        if (blurBusy) return
        blurBusy = true
        blurProgress = app.getString(R.string.vm_analyzing)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imgs = items.filter { it.category == Category.IMAGINI && it.size >= 30_000 && !Scanner.isScreenshot(it) }
                val scores = arrayOfNulls<Quality.Score>(imgs.size)
                val done = AtomicInteger(0)
                val pool = Executors.newFixedThreadPool(4)
                try {
                    val fs = imgs.indices.map { i ->
                        pool.submit(Runnable {
                            scores[i] = Quality.analyze(app, imgs[i])
                            val d = done.incrementAndGet()
                            if (d % 20 == 0) blurProgress = app.getString(R.string.vm_analyzing_n, d, imgs.size)
                        })
                    }
                    fs.forEach { it.get() }
                } finally {
                    pool.shutdown()
                }
                blurry = imgs.indices.filter { scores[it]?.bad == true }
                    .sortedBy { scores[it]!!.sharpness }
                    .map { imgs[it] }
                blurryDone = true
            } catch (e: Exception) {
                message = app.getString(R.string.vm_err_analyze, e.message ?: "")
            } finally {
                blurBusy = false
            }
        }
    }

    private fun addFreed(n: Long) {
        if (n <= 0) return
        lifetimeFreed += n
        prefs.edit().putLong("freed", lifetimeFreed).apply()
    }

    fun setTrash(on: Boolean) {
        useTrash = on
        prefs.edit().putBoolean("trash", on).apply()
    }

    fun refreshTrash() {
        if (!perms.allFiles) { trashFiles = 0; trashBytes = 0; return }
        viewModelScope.launch(Dispatchers.IO) {
            val i = Trash.info()
            trashFiles = i.files
            trashBytes = i.bytes
        }
    }

    fun restoreTrash() {
        viewModelScope.launch(Dispatchers.IO) {
            val r = Trash.restoreLast()
            if (r.isNotEmpty()) MediaScannerConnection.scanFile(app, r.toTypedArray(), null, null)
            message = app.getString(R.string.vm_restored, r.size)
            refreshTrash()
            if (r.isNotEmpty()) { scanning = false; scan() }
        }
    }

    fun emptyTrash() {
        viewModelScope.launch(Dispatchers.IO) {
            val freed = Trash.empty()
            addFreed(freed)
            updateStorage()
            message = app.getString(R.string.vm_trash_emptied, fmtSize(freed))
            refreshTrash()
        }
    }

    fun loadApps() {
        if (appsBusy) return
        appsBusy = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                apps = Apps.load(app)
            } catch (e: Exception) {
                message = app.getString(R.string.vm_err_apps, e.message ?: "")
            } finally {
                appsBusy = false
            }
        }
    }

    /** Apelat după ce fișierele au fost șterse (de sistem sau direct). */
    fun removed(done: Collection<Item>, trashed: Boolean = false) {
        val keys = done.map { it.path }.toSet()
        items = items.filter { it.path !in keys }
        exactGroups = exactGroups.map { g -> g.filter { it.path !in keys } }.filter { it.size > 1 }
        similarGroups = similarGroups.map { g -> g.filter { it.path !in keys } }.filter { it.size > 1 }
        videoGroups = videoGroups.map { g -> g.filter { it.path !in keys } }.filter { it.size > 1 }
        if (swipeQueue.isNotEmpty()) resetSwipe()
        orphans = orphans.filter { it.path !in keys }
        blurry = blurry.filter { it.path !in keys }
        emptyDirs = emptyDirs.filter { it.path !in keys }
        selected = selected - keys
        updateStorage()
        val bytes = done.sumOf { it.size }
        if (trashed) {
            message = app.getString(R.string.vm_trashed, done.size, fmtSize(bytes))
            refreshTrash()
        } else {
            addFreed(bytes)
            message = app.getString(R.string.vm_deleted, done.size, fmtSize(bytes))
        }
    }

    /** Ștergere directă (necesită „Acces la toate fișierele”). */
    fun deleteFiles(list: List<Item>) {
        viewModelScope.launch(Dispatchers.IO) {
            val root = Environment.getExternalStorageDirectory().canonicalFile
            val safe = list.filter { safeToDelete(it, root) }
            val ok = if (useTrash) Trash.move(safe)
            else safe.filter { if (it.dir) File(it.path).deleteRecursively() else File(it.path).delete() }
            val files = ok.filter { !it.dir }
            if (files.isNotEmpty()) {
                MediaScannerConnection.scanFile(app, files.map { it.path }.toTypedArray(), null, null)
            }
            removed(ok, useTrash)
            if (ok.size < list.size) message = app.getString(R.string.vm_cant_delete, list.size - ok.size)
        }
    }

    /** Nu atinge nimic în afara stocării partajate, rădăcina, „Android” sau legăturile simbolice. */
    private fun safeToDelete(it: Item, root: File): Boolean = Safety.ok(it, root)

    @Suppress("unused")
    private fun safeToDeleteOld(it: Item, root: File): Boolean {
        val f = File(it.path)
        val c = try { f.canonicalFile } catch (e: Exception) { return false }
        if (c.path != f.absoluteFile.normalize().path) return false // legătură simbolică
        if (!c.path.startsWith(root.path + "/")) return false
        val rel = c.path.removePrefix(root.path + "/")
        return rel != "Android"
    }

    /** Recomprimă JPEG-urile selectate (necesită „Acces la toate fișierele”). */
    fun compress(list: List<Item>) {
        if (compressing) return
        compressing = true
        viewModelScope.launch(Dispatchers.IO) {
            var saved = 0L
            var n = 0
            val newSizes = HashMap<String, Long>()
            try {
                for ((i, it) in list.withIndex()) {
                    progress = app.getString(R.string.vm_compressing, i + 1, list.size)
                    val after = Compressor.compress(it) ?: continue
                    saved += it.size - after
                    n++
                    newSizes[it.path] = after
                }
                if (newSizes.isNotEmpty()) {
                    MediaScannerConnection.scanFile(app, newSizes.keys.toTypedArray(), null, null)
                    items = items.map { f -> newSizes[f.path]?.let { s -> f.copy(size = s) } ?: f }
                    updateStorage()
                }
                addFreed(saved)
                selected = selected - list.map { it.path }.toSet()
                message = app.getString(R.string.vm_compressed, n, list.size, fmtSize(saved))
            } finally {
                compressing = false
            }
        }
    }

    /** Golește cache-ul propriu al aplicației. */
    fun clearOwnCache() {
        viewModelScope.launch(Dispatchers.IO) {
            var freed = 0L
            for (d in listOfNotNull(app.cacheDir, app.externalCacheDir)) {
                freed += d.walkTopDown().filter { it.isFile }.sumOf { it.length() }
                d.listFiles()?.forEach { it.deleteRecursively() }
            }
            message = app.getString(R.string.vm_cache_cleared, fmtSize(freed))
        }
    }
}
