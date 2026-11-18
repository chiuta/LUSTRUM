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

enum class Filter { MARI, VECHI, RESTURI, CAPTURI, INSTALATOARE, COMPRIMARE, ORFANE, GOALE }

class LustrumVm(private val app: Application) : AndroidViewModel(app) {

    var perms by mutableStateOf(Perms())

    // scanare
    var items by mutableStateOf<List<Item>>(emptyList())
    var scanning by mutableStateOf(false)
    var scanned by mutableStateOf(false)
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

    val groups: List<List<Item>> get() = if (dupMode == 0) exactGroups else similarGroups
    val groupsDone: Boolean get() = if (dupMode == 0) exactDone else similarDone

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
        progress = "Se scanează…"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val found = if (perms.allFiles) {
                    Scanner.scanFs { n -> progress = "Se scanează… $n fișiere" }
                } else {
                    Scanner.scanMedia(app) { n -> progress = "Se scanează… $n fișiere" }
                }
                items = found
                if (perms.allFiles) {
                    progress = "Caut resturi de la aplicații dezinstalate…"
                    orphans = Scanner.orphans(app)
                    emptyDirs = Scanner.emptyDirs()
                } else {
                    orphans = emptyList(); emptyDirs = emptyList()
                }
                scanned = true
                selected = emptySet()
                exactGroups = emptyList(); similarGroups = emptyList()
                exactDone = false; similarDone = false
                updateStorage()
            } catch (e: Exception) {
                message = "Eroare la scanare: ${e.message}"
            } finally {
                scanning = false
            }
        }
    }

    fun findDupes(similar: Boolean) {
        if (dupBusy) return
        dupBusy = true
        dupProgress = "Se analizează…"
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val progress: (Int, Int) -> Unit = { d, t -> dupProgress = "Se analizează… $d / $t" }
                if (similar) {
                    similarGroups = Dupes.similar(app, items, progress)
                    similarDone = true
                } else {
                    exactGroups = Dupes.exact(app, items, progress)
                    exactDone = true
                }
            } catch (e: Exception) {
                message = "Eroare la analiză: ${e.message}"
            } finally {
                dupBusy = false
            }
        }
    }

    fun loadApps() {
        if (appsBusy) return
        appsBusy = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                apps = Apps.load(app)
            } catch (e: Exception) {
                message = "Nu am putut citi aplicațiile: ${e.message}"
            } finally {
                appsBusy = false
            }
        }
    }

    /** Apelat după ce fișierele au fost șterse (de sistem sau direct). */
    fun removed(done: Collection<Item>) {
        val keys = done.map { it.path }.toSet()
        items = items.filter { it.path !in keys }
        exactGroups = exactGroups.map { g -> g.filter { it.path !in keys } }.filter { it.size > 1 }
        similarGroups = similarGroups.map { g -> g.filter { it.path !in keys } }.filter { it.size > 1 }
        orphans = orphans.filter { it.path !in keys }
        emptyDirs = emptyDirs.filter { it.path !in keys }
        selected = selected - keys
        updateStorage()
        message = "Șterse ${done.size} fișiere · ${fmtSize(done.sumOf { it.size })} eliberați"
    }

    /** Ștergere directă (necesită „Acces la toate fișierele”). */
    fun deleteFiles(list: List<Item>) {
        viewModelScope.launch(Dispatchers.IO) {
            val ok = list.filter { if (it.dir) File(it.path).deleteRecursively() else File(it.path).delete() }
            val files = ok.filter { !it.dir }
            if (files.isNotEmpty()) {
                MediaScannerConnection.scanFile(app, files.map { it.path }.toTypedArray(), null, null)
            }
            removed(ok)
            if (ok.size < list.size) message = "${list.size - ok.size} fișiere nu au putut fi șterse"
        }
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
                    progress = "Comprim ${i + 1} / ${list.size}…"
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
                selected = selected - list.map { it.path }.toSet()
                message = "Comprimate $n din ${list.size} imagini · ${fmtSize(saved)} eliberați"
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
            message = "Cache propriu golit · ${fmtSize(freed)}"
        }
    }
}
