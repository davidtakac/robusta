package os.dtakac.caffeine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class ScreenOffBroadcastReceiver : BroadcastReceiver() {
    var onScreenOff: (() -> Unit)? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_SCREEN_OFF) {
            onScreenOff?.invoke()
        }
    }
}

fun Context.registerReceiver(screenOffBroadcastReceiver: ScreenOffBroadcastReceiver) {
    registerReceiver(screenOffBroadcastReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
}