@file:OptIn(ExperimentalMaterial3Api::class)

package tf.trom.lustrum

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.runtime.produceState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.graphics.Bitmap
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import android.text.format.DateUtils
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

class Actions(
    val requestMedia: () -> Unit,
    val openAllFiles: () -> Unit,
    val openUsage: () -> Unit,
    val delete: (List<Item>) -> Unit,
    val appSettings: (String) -> Unit,
    val uninstall: (String) -> Unit,
    val requestNotif: () -> Unit,
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

    // După ce se acordă „Acces la toate fișierele”, scanarea din MediaStore e incompletă: se reia.
    LaunchedEffect(vm.perms.allFiles) {
        if (vm.perms.allFiles && vm.scanned && !vm.scannedWithAll && !vm.scanning) vm.scan()
    }

    val tabs = listOf(R.string.tab_home to "🏠", R.string.tab_files to "📦", R.string.tab_dupes to "👯", R.string.tab_swipe to "👆", R.string.tab_apps to "📱")
    Scaffold(
        snackbarHost = { SnackbarHost(snack) },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { i, (label, emoji) ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { if (tab != i) vm.clearSelection(); tab = i },
                        icon = { Text(emoji) },
                        label = { Text(stringResource(label)) },
                    )
                }
            }
        },
    ) { pad ->
        Box(Modifier.padding(pad).statusBarsPadding().fillMaxSize()) {
            when (tab) {
                0 -> HomeScreen(vm, a) { vm.clearSelection(); vm.filter = Filter.RESTURI; tab = 1 }
                1 -> FilesScreen(vm, a)
                2 -> DupScreen(vm, a)
                3 -> SwipeScreen(vm, a)
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
                stringResource(R.string.tagline),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.storage), style = MaterialTheme.typography.titleMedium)
                    LinearProgressIndicator(
                        progress = { if (vm.totalBytes > 0) used.toFloat() / vm.totalBytes else 0f },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(stringResource(R.string.storage_line, fmtSize(used), fmtSize(vm.totalBytes), fmtSize(vm.freeBytes)))
                    if (byCat.isNotEmpty()) {
                        Category.entries.forEach { c ->
                            val s = byCat[c] ?: 0L
                            if (s > 0) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(stringResource(c.labelRes))
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
                                stringResource(R.string.media_only_note),
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
                        !(p.media || p.allFiles) -> stringResource(R.string.scan_grant)
                        vm.scanned -> stringResource(R.string.scan_again)
                        else -> stringResource(R.string.scan_phone)
                    }
                )
            }
        }
        if (vm.scanned && p.allFiles) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.junk_title), style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.junk_line, junk.size, fmtSize(junk.sumOf { it.size })))
                        OutlinedButton(onClick = openJunk, enabled = junk.isNotEmpty()) { Text(stringResource(R.string.junk_btn)) }
                    }
                }
            }
        }
        item {
            val ctx = LocalContext.current
            val h = remember(vm.scanning, vm.scanned) { HealthReader.read(ctx) }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.health_title), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.health_ram, fmtSize(h.ramTotal - h.ramFree), fmtSize(h.ramTotal)))
                    val pct = if (h.batteryPct >= 0) "${h.batteryPct}%" else "?"
                    Text(stringResource(if (h.charging) R.string.health_batt_chg else R.string.health_batt, pct, "%.1f".format(h.batteryTemp)))
                    Text(
                        stringResource(R.string.health_note),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        if (vm.scanned && vm.items.isNotEmpty()) {
            item {
                val root = remember { Environment.getExternalStorageDirectory().path + "/" }
                val top = remember(vm.items) {
                    vm.items.groupBy { it.folder.removePrefix(root).split('/').take(2).joinToString("/") }
                        .map { (k, v) -> k to v.sumOf { it.size } }
                        .sortedByDescending { it.second }
                        .take(6)
                }
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(stringResource(R.string.folders_title), style = MaterialTheme.typography.titleMedium)
                        top.forEach { (f, s) ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(f.ifEmpty { "/" }, Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(fmtSize(s))
                            }
                        }
                    }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.auto_title), style = MaterialTheme.typography.titleMedium)
                    if (!p.allFiles) {
                        Text(stringResource(R.string.need_all), style = MaterialTheme.typography.bodySmall)
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(0 to R.string.auto_off, 7 to R.string.auto_weekly, 30 to R.string.auto_monthly).forEach { (d, label) ->
                                FilterChip(vm.autoDays == d, { vm.setAuto(d); if (d > 0) a.requestNotif() }, { Text(stringResource(label)) })
                            }
                        }
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.auto_clean), Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                            Switch(checked = vm.autoClean, onCheckedChange = { vm.updateAutoClean(it) })
                        }
                        Text(stringResource(R.string.auto_note), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = vm::trimCaches, enabled = !vm.trimBusy, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.trim_btn))
                    }
                    Text(stringResource(R.string.trim_note), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.safe_delete), style = MaterialTheme.typography.titleMedium)
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.safe_delete_note), Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                        Switch(checked = vm.useTrash, onCheckedChange = { vm.setTrash(it) })
                    }
                    if (p.allFiles && vm.trashFiles > 0) {
                        Text(stringResource(R.string.trash_line, vm.trashFiles, fmtSize(vm.trashBytes)))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = vm::restoreTrash) { Text(stringResource(R.string.trash_restore)) }
                            OutlinedButton(onClick = vm::emptyTrash) { Text(stringResource(R.string.trash_empty)) }
                        }
                    }
                    if (vm.lifetimeFreed > 0) {
                        Text(stringResource(R.string.freed_total, fmtSize(vm.lifetimeFreed)), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.perm_title), style = MaterialTheme.typography.titleMedium)
                    PermRow(stringResource(R.string.perm_media), p.media, stringResource(R.string.perm_grant), a.requestMedia)
                    PermRow(stringResource(R.string.perm_all), p.allFiles, stringResource(R.string.perm_settings), a.openAllFiles)
                    PermRow(stringResource(R.string.perm_usage), p.usage, stringResource(R.string.perm_settings), a.openUsage)
                    Text(
                        stringResource(R.string.no_internet),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        item {
            OutlinedButton(onClick = vm::clearOwnCache, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.clear_own_cache))
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
        ItemThumb(item)
        Column(Modifier.weight(1f).padding(horizontal = 8.dp)) {
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
private fun ItemThumb(item: Item) {
    val ctx = LocalContext.current
    val bmp by produceState<Bitmap?>(null, item.path) { value = withContext(Dispatchers.IO) { Thumbs.load(ctx, item) } }
    Box(
        Modifier.size(44.dp).clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        val b = bmp
        if (b != null) {
            Image(b.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            Text(
                if (item.dir) "📁" else when (item.category) {
                    Category.IMAGINI -> "🖼️"
                    Category.VIDEO -> "🎞️"
                    Category.AUDIO -> "🎵"
                    Category.DOCUMENTE -> "📄"
                    Category.ARHIVE -> "🗜️"
                    else -> "📎"
                }
            )
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
        ) { Text(stringResource(R.string.del_bar, chosen.size, fmtSize(chosen.sumOf { it.size }))) }
    }
}

@Composable
internal fun ConfirmDelete(list: List<Item>, onDismiss: () -> Unit, onOk: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.confirm_del_title, list.size)) },
        text = { Text(stringResource(R.string.confirm_del_text, fmtSize(list.sumOf { it.size }))) },
        confirmButton = { TextButton(onClick = onOk) { Text(stringResource(R.string.delete)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}

// ───────────────────────── Fișiere mari / vechi / resturi ─────────────────────────

@Composable
private fun filterLabel(f: Filter): String = when (f) {
    Filter.MARI -> stringResource(R.string.f_big)
    Filter.VECHI -> stringResource(R.string.f_old)
    Filter.RESTURI -> stringResource(R.string.f_junk)
    Filter.CAPTURI -> stringResource(R.string.f_shots)
    Filter.NECLARE -> stringResource(R.string.f_blur)
    Filter.INSTALATOARE -> stringResource(R.string.f_apk)
    Filter.COMPRIMARE -> stringResource(R.string.f_compress)
    Filter.ORFANE -> stringResource(R.string.f_orphans)
    Filter.GOALE -> stringResource(R.string.f_empty)
}

@Composable
fun FilesScreen(vm: LustrumVm, a: Actions) {
    val list = remember(vm.items, vm.filter, vm.orphans, vm.emptyDirs) { vm.list(vm.filter) }
    var confirm by remember { mutableStateOf<List<Item>?>(null) }
    val chosen = list.filter { it.path in vm.selected }
    val needsAll = vm.filter == Filter.ORFANE || vm.filter == Filter.GOALE || vm.filter == Filter.COMPRIMARE
    val compress = vm.filter == Filter.COMPRIMARE

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Filter.entries.forEach { f ->
                FilterChip(vm.filter == f, { vm.clearSelection(); vm.filter = f }, { Text(filterLabel(f)) })
            }
        }
        if (vm.filter == Filter.NECLARE) {
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = vm::findBlurry, enabled = vm.scanned && !vm.blurBusy, modifier = Modifier.fillMaxWidth()) {
                    Text(if (vm.blurBusy) vm.blurProgress else stringResource(R.string.blur_find))
                }
                Text(stringResource(R.string.blur_note), style = MaterialTheme.typography.bodySmall)
            }
        }
        if (compress) {
            Text(
                stringResource(R.string.compress_note),
                Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        when {
            needsAll && !vm.perms.allFiles -> Text(stringResource(R.string.need_all), Modifier.padding(16.dp))
            !vm.scanned -> Text(stringResource(R.string.scan_first), Modifier.padding(16.dp))
            list.isEmpty() -> Text(stringResource(R.string.nothing), Modifier.padding(16.dp))
            else -> Text(
                stringResource(R.string.items_line, list.size, fmtSize(list.sumOf { it.size })),
                Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        LazyColumn(Modifier.weight(1f)) {
            items(list, key = { it.path }) { f ->
                ItemRow(f, f.path in vm.selected) { vm.toggle(f.path) }
            }
        }
        if (chosen.isNotEmpty()) {
            if (compress) {
                Surface(tonalElevation = 3.dp, modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { confirm = chosen },
                        enabled = !vm.compressing,
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                    ) { Text(if (vm.compressing) vm.progress else stringResource(R.string.compress_btn, chosen.size)) }
                }
            } else {
                DeleteBar(chosen) { confirm = chosen }
            }
        }
    }
    confirm?.let { l ->
        if (compress) {
            AlertDialog(
                onDismissRequest = { confirm = null },
                title = { Text(stringResource(R.string.confirm_cmp_title, l.size)) },
                text = { Text(stringResource(R.string.confirm_cmp_text)) },
                confirmButton = { TextButton(onClick = { vm.compress(l); confirm = null }) { Text(stringResource(R.string.compress)) } },
                dismissButton = { TextButton(onClick = { confirm = null }) { Text(stringResource(R.string.cancel)) } },
            )
        } else {
            ConfirmDelete(l, { confirm = null }) {
                a.delete(l)
                confirm = null
            }
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
            FilterChip(vm.dupMode == 0, { vm.clearSelection(); vm.dupMode = 0 }, { Text(stringResource(R.string.d_identical)) })
            FilterChip(vm.dupMode == 1, { vm.clearSelection(); vm.dupMode = 1 }, { Text(stringResource(R.string.d_similar)) })
            FilterChip(vm.dupMode == 2, { vm.clearSelection(); vm.dupMode = 2 }, { Text(stringResource(R.string.d_video)) })
        }
        Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { vm.findDupes(vm.dupMode) },
                enabled = vm.scanned && !vm.dupBusy,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    when {
                        vm.dupBusy -> vm.dupProgress
                        !vm.scanned -> stringResource(R.string.scan_first)
                        vm.dupMode == 0 -> stringResource(R.string.d_find_exact)
                        vm.dupMode == 2 -> stringResource(R.string.d_find_video)
                        else -> stringResource(R.string.d_find_similar)
                    }
                )
            }
            if (vm.groupsDone) {
                Text(stringResource(R.string.d_groups, groups.size, fmtSize(recoverable)), style = MaterialTheme.typography.bodySmall)
                if (groups.isNotEmpty() && vm.dupMode != 0) {
                    Text(stringResource(if (vm.dupMode == 1) R.string.d_similar_note else R.string.d_video_note), style = MaterialTheme.typography.bodySmall)
                }
                if (groups.isNotEmpty()) {
                    OutlinedButton(onClick = vm::autoSelect, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.d_autoselect))
                    }
                }
            }
            if (risky > 0) {
                Text(
                    stringResource(R.string.d_risky, risky),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        LazyColumn(Modifier.weight(1f)) {
            groups.forEachIndexed { gi, g ->
                item(key = "h$gi") {
                    Text(
                        stringResource(R.string.d_group_head, g.size, fmtSize(g[0].size)),
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
                    Text(stringResource(R.string.a_need_usage))
                    Button(onClick = a.openUsage) { Text(stringResource(R.string.a_open_settings)) }
                }
            }
            return@Column
        }
        Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(sort == 0, { sort = 0 }, { Text(stringResource(R.string.a_cache)) })
            FilterChip(sort == 1, { sort = 1 }, { Text(stringResource(R.string.a_unused)) })
            FilterChip(sort == 2, { sort = 2 }, { Text(stringResource(R.string.a_size)) })
        }
        Text(
            if (vm.appsBusy) stringResource(R.string.a_reading)
            else stringResource(R.string.a_cache_note, fmtSize(apps.sumOf { it.cache })),
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
                        val last = if (app.lastUsed <= 0) stringResource(R.string.a_last_old)
                        else stringResource(R.string.a_last, DateUtils.getRelativeTimeSpanString(app.lastUsed, now, DateUtils.DAY_IN_MILLIS).toString())
                        Text(
                            stringResource(R.string.a_line, fmtSize(app.cache), fmtSize(app.total), last),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    TextButton(onClick = { a.appSettings(app.pkg) }) { Text(stringResource(R.string.a_cache)) }
                    TextButton(onClick = { a.uninstall(app.pkg) }) { Text(stringResource(R.string.a_uninstall)) }
                }
            }
        }
    }
}
