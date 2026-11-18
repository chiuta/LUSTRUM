package tf.trom.lustrum

import android.app.AppOpsManager
import android.app.usage.StorageStatsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Process
import android.os.storage.StorageManager

object Apps {

    fun hasUsageAccess(ctx: Context): Boolean {
        val ops = ctx.getSystemService(AppOpsManager::class.java)
        val mode = ops.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), ctx.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /** Mărime cache/date și ultima folosire pentru fiecare aplicație (cere „Acces la utilizare”). */
    fun load(ctx: Context): List<AppInfo> {
        val pm = ctx.packageManager
        val ssm = ctx.getSystemService(StorageStatsManager::class.java)
        val usm = ctx.getSystemService(UsageStatsManager::class.java)
        val now = System.currentTimeMillis()
        val lastUsed: Map<String, Long> =
            usm.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, now - 365L * 24 * 3600 * 1000, now)
                .orEmpty()
                .groupBy { it.packageName }
                .mapValues { e -> e.value.maxOf { it.lastTimeUsed } }
        val user = Process.myUserHandle()
        return pm.getInstalledApplications(0).mapNotNull { ai ->
            val st = try {
                ssm.queryStatsForPackage(StorageManager.UUID_DEFAULT, ai.packageName, user)
            } catch (e: Exception) {
                return@mapNotNull null
            }
            AppInfo(
                pkg = ai.packageName,
                label = pm.getApplicationLabel(ai).toString(),
                cache = st.cacheBytes,
                total = st.appBytes + st.dataBytes,
                lastUsed = lastUsed[ai.packageName] ?: 0L,
                system = (ai.flags and ApplicationInfo.FLAG_SYSTEM) != 0,
            )
        }
    }
}
