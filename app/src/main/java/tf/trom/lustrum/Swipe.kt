package tf.trom.lustrum

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/** Triere rapidă: glisează la stânga = șterge, la dreapta = păstrează. Nimic nu se șterge până la confirmare. */
@Composable
fun SwipeScreen(vm: LustrumVm, a: Actions) {
    var confirm by remember { mutableStateOf<List<Item>?>(null) }
    val queue = vm.swipeQueue
    val idx = vm.swipeIndex
    val marked = vm.swipeMarked

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (queue.isEmpty()) {
            Text(stringResource(R.string.swipe_intro), style = MaterialTheme.typography.bodyMedium)
            Button(onClick = vm::startSwipe, enabled = vm.scanned, modifier = Modifier.fillMaxWidth()) {
                Text(if (vm.scanned) stringResource(R.string.swipe_start) else stringResource(R.string.scan_first))
            }
            return@Column
        }
        Text(
            stringResource(R.string.swipe_progress, minOf(idx + 1, queue.size), queue.size, marked.size, fmtSize(marked.sumOf { it.size })),
            style = MaterialTheme.typography.bodySmall,
        )
        val item = queue.getOrNull(idx)
        if (item == null) {
            Text(stringResource(R.string.swipe_done), style = MaterialTheme.typography.titleMedium)
            Box(Modifier.weight(1f))
            OutlinedButton(onClick = vm::startSwipe, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.swipe_start))
            }
        } else {
            val ctx = LocalContext.current
            val bmp by produceState<Bitmap?>(null, item.path) { value = withContext(Dispatchers.IO) { Thumbs.load(ctx, item, 720) } }
            var dx by remember(item.path) { mutableStateOf(0f) }
            Box(
                Modifier.fillMaxWidth().weight(1f)
                    .offset { IntOffset(dx.roundToInt(), 0) }
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .pointerInput(item.path) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                val d = dx
                                dx = 0f
                                if (d > 250f) vm.swipe(false) else if (d < -250f) vm.swipe(true)
                            },
                            onDragCancel = { dx = 0f },
                        ) { _, amount -> dx += amount }
                    },
                contentAlignment = Alignment.Center,
            ) {
                val b = bmp
                if (b != null) Image(b.asImageBitmap(), item.name, Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                else Text(if (item.category == Category.VIDEO) "🎞️" else "🖼️", style = MaterialTheme.typography.displayMedium)
            }
            Text(item.name, maxLines = 1)
            Text("${fmtSize(item.size)} · ${fmtDate(item.modified)}", style = MaterialTheme.typography.bodySmall)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { vm.swipe(true) }, enabled = item != null, modifier = Modifier.weight(1f)) {
                Text("🗑 " + stringResource(R.string.delete))
            }
            OutlinedButton(onClick = vm::swipeUndo, enabled = idx > 0, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.swipe_undo))
            }
            OutlinedButton(onClick = { vm.swipe(false) }, enabled = item != null, modifier = Modifier.weight(1f)) {
                Text("✓ " + stringResource(R.string.swipe_keep))
            }
        }
        Button(
            onClick = { confirm = marked },
            enabled = marked.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(48.dp),
        ) { Text(stringResource(R.string.swipe_review, marked.size)) }
    }
    confirm?.let { l ->
        ConfirmDelete(l, { confirm = null }) {
            a.delete(l)
            confirm = null
        }
    }
}
