package tf.trom.lustrum

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

data class Health(
    val ramTotal: Long,
    val ramFree: Long,
    val batteryPct: Int,
    val batteryTemp: Float,
    val charging: Boolean,
)

object HealthReader {
    fun read(ctx: Context): Health {
        val mi = ActivityManager.MemoryInfo()
        ctx.getSystemService(ActivityManager::class.java).getMemoryInfo(mi)
        val b = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = b?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = b?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val status = b?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return Health(
            ramTotal = mi.totalMem,
            ramFree = mi.availMem,
            batteryPct = if (level >= 0 && scale > 0) level * 100 / scale else -1,
            batteryTemp = (b?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0) / 10f,
            charging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL,
        )
    }
}
