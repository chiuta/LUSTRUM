package tf.trom.lustrum

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import android.os.Bundle

class MainActivity : ComponentActivity() {

    private val vm: LustrumVm by viewModels()
    private var pending: List<Item> = emptyList()

    private val deleteLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { r ->
        if (r.resultCode == RESULT_OK) vm.removed(pending) else vm.message = "Ștergerea a fost anulată"
        pending = emptyList()
    }

    private val permLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { refreshPerms() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val actions = Actions(
            requestMedia = {
                permLauncher.launch(
                    if (Build.VERSION.SDK_INT >= 33) arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO,
                    ) else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                )
            },
            openAllFiles = {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                )
            },
            openUsage = { startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) },
            delete = this::requestDelete,
            appSettings = { pkg ->
                startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", pkg, null))
                )
            },
            uninstall = { pkg ->
                startActivity(Intent(Intent.ACTION_DELETE, Uri.parse("package:$pkg")))
            },
        )
        setContent { LustrumTheme { LustrumApp(vm, actions) } }
    }

    override fun onResume() {
        super.onResume()
        refreshPerms()
    }

    private fun granted(p: String) = checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED

    private fun refreshPerms() {
        val media = if (Build.VERSION.SDK_INT >= 33) granted(Manifest.permission.READ_MEDIA_IMAGES)
        else granted(Manifest.permission.READ_EXTERNAL_STORAGE)
        vm.perms = Perms(
            media = media,
            allFiles = Environment.isExternalStorageManager(),
            usage = Apps.hasUsageAccess(this),
        )
    }

    /** Fișierele din MediaStore se șterg printr-o cerere de sistem; restul direct. */
    private fun requestDelete(items: List<Item>) {
        val (withUri, files) = items.partition { it.uri != null }
        if (files.isNotEmpty()) vm.deleteFiles(files)
        if (withUri.isNotEmpty()) {
            pending = withUri
            val pi = MediaStore.createDeleteRequest(contentResolver, withUri.map { it.uri!! })
            deleteLauncher.launch(IntentSenderRequest.Builder(pi.intentSender).build())
        }
    }
}
