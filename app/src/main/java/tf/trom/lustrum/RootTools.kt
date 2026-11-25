package tf.trom.lustrum

import java.util.concurrent.TimeUnit

/**
 * Cale opțională pentru telefoane cu root: golește cache-ul tuturor aplicațiilor cu `pm trim-caches`.
 * Fără root (cazul normal) `available()` dă false și nimic nu se execută.
 */
object RootTools {
    private fun run(cmd: String, timeoutSec: Long): Pair<Int, String>? = try {
        val p = ProcessBuilder("su", "-c", cmd).redirectErrorStream(true).start()
        val out = p.inputStream.bufferedReader().readText()
        if (!p.waitFor(timeoutSec, TimeUnit.SECONDS)) { p.destroyForcibly(); null } else p.exitValue() to out
    } catch (t: Throwable) {
        null
    }

    fun available(): Boolean {
        val r = run("id", 8) ?: return false
        return r.first == 0 && r.second.contains("uid=0")
    }

    /** Cere sistemului să elibereze cache-ul tuturor aplicațiilor; true dacă comanda a reușit. */
    fun trimAllCaches(): Boolean {
        val r = run("pm trim-caches 9999999999999", 90) ?: return false
        return r.first == 0
    }
}
