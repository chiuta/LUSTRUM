@file:OptIn(ExperimentalMaterial3Api::class)

package tf.trom.lustrum

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

class Actions(
    val requestMedia: () -> Unit,
    val openAllFiles: () -> Unit,
    val openUsage: () -> Unit,
    val delete: (List<Item>) -> Unit,
    val appSettings: (String) -> Unit,
    val uninstall: (String) -> Unit,
)

@Composable
fun LustrumTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) darkColorScheme(primary = Color(0xFF4DD0B8))
    else lightColorScheme(primary = Color(0xFF00796B))
    MaterialTheme(colorScheme = colors, content = content)
}

@Composable
fun LustrumApp(vm: LustrumVm, a: Actions) {
    var tab by rememberSaveable { mutableStateOf(0) }
    val snack = remember { SnackbarHostState() }

    LaunchedEffect(vm.message) {
        vm.message?.let {
            snack.showSnackbar(it)
            vm.message = null
        }
    }
    // Prima scanare pornește singură imediat ce există o permisiune.
    LaunchedEffect(vm.perms) {
        if ((vm.perms.media || vm.perms.allFiles) && !vm.scanned && !vm.scanning) vm.scan()
    }

    val tabs = listOf("Acasă" to "🏠", "Fișiere" to "📦", "Duplicate" to "👯", "Aplicații" to "📱")
    Scaffold(
        snackbarHost = { SnackbarHost(snack) },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { i, (label, emoji) ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Text(emoji) },
                        label = { Text(label) },
                    )
                }
            }
        },
    ) { pad ->
        Box(Modifier.padding(pad).statusBarsPadding().fillMaxSize()) {
            when (tab) {
                0 -> HomeScreen(vm, a) { vm.filter = Filter.RESTURI; tab = 1 }
                1 -> FilesScreen(vm, a)
                2 -> DupScreen(vm, a)
                else -> AppsScreen(vm, a)
            }
        }
    }
}

// ───────────────────────── Acasă ─────────────────────────

@Composable
fun HomeScreen(vm: LustrumVm, a: Actions, openJunk: () -> Unit) {
    val p = vm.perms
    val junk = remember(vm.items) { vm.items.filter(Scanner::isJunk) }
    val byCat = remember(vm.items) { vm.items.groupBy { it.category }.mapValues { e -> e.value.sumOf { it.size } } }
    val used = vm.totalBytes - vm.freeBytes

    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("LUSTRUM", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 16.dp))
            Text(
                "Curățare fără reclame, fără internet, fără urmărire.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Stocare", style = MaterialTheme.typography.titleMedium)
                    LinearProgressIndicator(
                        progress = { if (vm.totalBytes > 0) used.toFloat() / vm.totalBytes else 0f },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text("Folosit ${fmtSize(used)} din ${fmtSize(vm.totalBytes)} · liber ${fmtSize(vm.freeBytes)}")
                    if (byCat.isNotEmpty()) {
                        Category.entries.forEach { c ->
                            val s = byCat[c] ?: 0L
                            if (s > 0) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(c.label)
                                    Text(fmtSize(s))
                                }
                                LinearProgressIndicator(
                                    progress = { if (vm.totalBytes > 0) s.toFloat() / vm.totalBytes else 0f },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                        if (!p.allFiles) {
                            Text(
                                "Se văd doar imagini, video și audio. Pentru documente, descărcări și resturi acordă „Acces la toate fișierele”.",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
        }
        item {
            Button(
                onClick = { if (p.media || p.allFiles) vm.scan() else a.requestMedia() },
                enabled = !vm.scanning,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    when {
                        vm.scanning -> vm.progress
                        !(p.media || p.allFiles) -> "Acordă acces și scanează"
                        vm.scanned -> "Scanează din nou"
                        else -> "Scanează telefonul"
                    }
                )
            }
        }
        if (vm.scanned && p.allFiles) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Resturi", style = MaterialTheme.typography.titleMedium)
                        Text("${junk.size} fișiere · ${fmtSize(junk.sumOf { it.size })} (temporare, miniaturi, descărcări întrerupte)")
                        OutlinedButton(onClick = openJunk, enabled = junk.isNotEmpty()) { Text("Vezi și curăță") }
                    }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Permisiuni", style = MaterialTheme.typography.titleMedium)
                    PermRow("Acces la media", p.media, "Acordă", a.requestMedia)
                    PermRow("Acces la toate fișierele", p.allFiles, "Setări", a.openAllFiles)
                    PermRow("Acces la utilizare aplicații", p.usage, "Setări", a.openUsage)
                    Text(
                        "Aplicația nu are permisiunea INTERNET: nimic nu pleacă de pe telefon.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        item {
            OutlinedButton(onClick = vm::clearOwnCache, modifier = Modifier.fillMaxWidth()) {
                Text("Golește cache-ul propriu")
            }
            Box(Modifier.padding(bottom = 16.dp))
        }
    }
}

@Composable
private fun PermRow(label: String, ok: Boolean, action: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(if (ok) "✅" else "⚠️")
        Text(label, Modifier.weight(1f).padding(start = 8.dp))
        if (!ok) TextButton(onClick = onClick) { Text(action) }
    }
}

// ───────────────────────── componente comune ─────────────────────────

@Composable
private fun ItemRow(item: Item, checked: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onToggle() }.padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = { onToggle() })
        Column(Modifier.weight(1f)) {
            Text(item.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                item.folder,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(fmtSize(item.size))
            Text(fmtDate(item.modified), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun DeleteBar(chosen: List<Item>, enabled: Boolean = true, onClick: () -> Unit) {
    Surface(tonalElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth().padding(12.dp),
        ) { Text("Șterge ${chosen.size} selectate · ${fmtSize(chosen.sumOf { it.size })}") }
    }
}

@Composable
private fun ConfirmDelete(list: List<Item>, onDismiss: () -> Unit, onOk: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ștergi ${list.size} fișiere?") },
        text = { Text("Se eliberează ${fmtSize(list.sumOf { it.size })}. Acțiunea nu se poate anula.") },
        confirmButton = { TextButton(onClick = onOk) { Text("Șterge") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anulează") } },
    )
}

// ───────────────────────── Fișiere mari / vechi / resturi ─────────────────────────

@Composable
fun FilesScreen(vm: LustrumVm, a: Actions) {
    val list = remember(vm.items, vm.filter) { vm.list(vm.filter) }
    var confirm by remember { mutableStateOf<List<Item>?>(null) }
    val chosen = list.filter { it.path in vm.selected }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(vm.filter == Filter.MARI, { vm.filter = Filter.MARI }, { Text("Mari >50 MB") })
            FilterChip(vm.filter == Filter.VECHI, { vm.filter = Filter.VECHI }, { Text("Vechi >1 an") })
            FilterChip(vm.filter == Filter.RESTURI, { vm.filter = Filter.RESTURI }, { Text("Resturi") })
        }
        when {
            !vm.scanned -> Text("Scanează mai întâi din Acasă.", Modifier.padding(16.dp))
            list.isEmpty() -> Text("Nimic de arătat aici.", Modifier.padding(16.dp))
            else -> Text(
                "${list.size} fișiere · ${fmtSize(list.sumOf { it.size })}",
                Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        LazyColumn(Modifier.weight(1f)) {
            items(list, key = { it.path }) { f ->
                ItemRow(f, f.path in vm.selected) { vm.toggle(f.path) }
            }
        }
        if (chosen.isNotEmpty()) DeleteBar(chosen) { confirm = chosen }
    }
    confirm?.let { l ->
        ConfirmDelete(l, { confirm = null }) {
            a.delete(l)
            confirm = null
        }
    }
}

// ───────────────────────── Duplicate ─────────────────────────

@Composable
fun DupScreen(vm: LustrumVm, a: Actions) {
    val groups = vm.groups
    val sel = vm.selected
    var confirm by remember { mutableStateOf<List<Item>?>(null) }
    val chosen = groups.flatten().filter { it.path in sel }
    val risky = groups.count { g -> g.all { it.path in sel } }
    val recoverable = groups.sumOf { g -> g.drop(1).sumOf { it.size } }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(vm.dupMode == 0, { vm.dupMode = 0 }, { Text("Identice") })
            FilterChip(vm.dupMode == 1, { vm.dupMode = 1 }, { Text("Imagini asemănătoare") })
        }
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { vm.findDupes(vm.dupMode == 1) },
                enabled = vm.scanned && !vm.dupBusy,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    when {
                        vm.dupBusy -> vm.dupProgress
                        !vm.scanned -> "Scanează mai întâi din Acasă"
                        vm.dupMode == 0 -> "Caută fișiere identice"
                        else -> "Caută imagini asemănătoare"
                    }
                )
            }
            if (vm.groupsDone) {
                Text("${groups.size} grupuri · ${fmtSize(recoverable)} recuperabili", style = MaterialTheme.typography.bodySmall)
                if (groups.isNotEmpty()) {
                    OutlinedButton(onClick = vm::autoSelect, modifier = Modifier.fillMaxWidth()) {
                        Text("Bifează copiile (păstrează cel mai vechi)")
                    }
                }
            }
            if (risky > 0) {
                Text(
                    "Ai bifat toate copiile din $risky grupuri. Debifează cel puțin una din fiecare grup.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        LazyColumn(Modifier.weight(1f)) {
            groups.forEachIndexed { gi, g ->
                item(key = "h$gi") {
                    Text(
                        "${g.size} copii · ${fmtSize(g[0].size)} fiecare",
                        Modifier.padding(start = 16.dp, top = 12.dp),
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
                items(g, key = { it.path }) { f ->
                    ItemRow(f, f.path in sel) { vm.toggle(f.path) }
                }
            }
        }
        if (chosen.isNotEmpty()) DeleteBar(chosen, enabled = risky == 0) { confirm = chosen }
    }
    confirm?.let { l ->
        ConfirmDelete(l, { confirm = null }) {
            a.delete(l)
            confirm = null
        }
    }
}

// ───────────────────────── Aplicații ─────────────────────────

@Composable
fun AppsScreen(vm: LustrumVm, a: Actions) {
    var sort by rememberSaveable { mutableStateOf(0) }
    LaunchedEffect(vm.perms.usage) {
        if (vm.perms.usage && vm.apps.isEmpty()) vm.loadApps()
    }
    val apps = remember(vm.apps, sort) {
        val user = vm.apps.filter { !it.system }
        when (sort) {
            0 -> user.sortedByDescending { it.cache }
            1 -> user.sortedBy { it.lastUsed }
            else -> user.sortedByDescending { it.total }
        }
    }
    val now = System.currentTimeMillis()

    Column(Modifier.fillMaxSize()) {
        if (!vm.perms.usage) {
            Card(Modifier.fillMaxWidth().padding(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pentru mărimea cache-ului și ultima folosire, Android cere „Acces la utilizare”.")
                    Button(onClick = a.openUsage) { Text("Deschide setările") }
                }
            }
            return@Column
        }
        Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(sort == 0, { sort = 0 }, { Text("Cache") })
            FilterChip(sort == 1, { sort = 1 }, { Text("Nefolosite") })
            FilterChip(sort == 2, { sort = 2 }, { Text("Mărime") })
        }
        Text(
            if (vm.appsBusy) "Se citesc aplicațiile…"
            else "Cache total: ${fmtSize(apps.sumOf { it.cache })}. Android nu permite unei aplicații să șteargă cache-ul altora; " +
                "„Cache” te duce la ecranul aplicației (Stocare → Golește cache-ul).",
            Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodySmall,
        )
        LazyColumn(Modifier.weight(1f)) {
            items(apps, key = { it.pkg }) { app ->
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(app.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        val last = if (app.lastUsed <= 0) "folosire necunoscută"
                        else "folosită acum ${(now - app.lastUsed) / 86_400_000L} zile"
                        Text(
                            "cache ${fmtSize(app.cache)} · total ${fmtSize(app.total)} · $last",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    TextButton(onClick = { a.appSettings(app.pkg) }) { Text("Cache") }
                    TextButton(onClick = { a.uninstall(app.pkg) }) { Text("Dezinstalează") }
                }
            }
        }
    }
}
