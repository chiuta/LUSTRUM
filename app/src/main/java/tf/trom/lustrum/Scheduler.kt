package tf.trom.lustrum

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.io.File
import java.util.concurrent.TimeUnit

/** Curățare periodică în fundal: numai resturi evidente (vezi Scanner.isJunk), cu notificare. */
object Scheduler {
    const val NAME = "lustrum-auto"
    const val CHANNEL = "lustrum-clean"

    data class Report(val count: Int, val bytes: Long, val cleaned: Boolean)

    fun prefs(ctx: Context) = ctx.getSharedPreferences("lustrum", Context.MODE_PRIVATE)

    fun apply(ctx: Context, days: Int) {
        val wm = WorkManager.getInstance(ctx)
        if (days <= 0) {
            wm.cancelUniqueWork(NAME)
            return
        }
        val req = PeriodicWorkRequestBuilder<CleanWorker>(days.toLong(), TimeUnit.DAYS)
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .build()
        wm.enqueueUniquePeriodicWork(NAME, ExistingPeriodicWorkPolicy.UPDATE, req)
    }

    /** Scanează și, dacă `autoClean`, mută în coș / șterge resturile. Necesită „Acces la toate fișierele”. */
    fun runOnce(ctx: Context, autoClean: Boolean, useTrash: Boolean): Report {
        if (!Environment.isExternalStorageManager()) return Report(0, 0, false)
        val junk = Scanner.scanFs { }.filter { Scanner.isJunk(it) }
        val bytes = junk.sumOf { it.size }
        if (!autoClean || junk.isEmpty()) return Report(junk.size, bytes, false)
        val root = Environment.getExternalStorageDirectory().canonicalFile
        val safe = junk.filter { Safety.ok(it, root) }
        val done = if (useTrash) Trash.move(safe) else safe.filter { File(it.path).delete() }
        val freed = done.sumOf { it.size }
        if (!useTrash && freed > 0) {
            val p = prefs(ctx)
            p.edit().putLong("freed", p.getLong("freed", 0L) + freed).apply()
        }
        return Report(done.size, freed, true)
    }

    fun notify(ctx: Context, r: Report) {
        if (r.count == 0) return
        val nm = ctx.getSystemService(NotificationManager::class.java) ?: return
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL, ctx.getString(R.string.auto_title), NotificationManager.IMPORTANCE_LOW)
        )
        val open = PendingIntent.getActivity(
            ctx, 0, Intent(ctx, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        val text = ctx.getString(if (r.cleaned) R.string.notif_cleaned else R.string.notif_found, r.count, fmtSize(r.bytes))
        val n = android.app.Notification.Builder(ctx, CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("LUSTRUM")
            .setContentText(text)
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        try { nm.notify(1, n) } catch (e: SecurityException) { }
    }
}

class CleanWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val p = Scheduler.prefs(applicationContext)
        val r = Scheduler.runOnce(applicationContext, p.getBoolean("auto_clean", false), p.getBoolean("trash", false))
        Scheduler.notify(applicationContext, r)
        return Result.success()
    }
}
