package os.dtakac.caffeine

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import java.lang.IllegalStateException

class KeepAwakeService : Service() {
    companion object {
        const val START_DIM = "Caffeine::StartDim"
        const val START_BRIGHT = "Caffeine::StartBright"

        private const val NOTIFICATION_CHANNEL_ID = "Caffeine::NotificationChannel"
        private const val NOTIFICATION_ID = 1
        private const val STOP_ACTION = "Caffeine::StopAction"
        private const val WAKE_LOCK_TAG = "Caffeine::ScreenOnWakeLockTag"
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private val screenOffReceiver: ScreenOffBroadcastReceiver by lazy {
        ScreenOffBroadcastReceiver().apply {
            onScreenOff = { stop() }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when {
            intent?.getBooleanExtra(START_BRIGHT, false) == true -> {
                start(allowDim = false)
            }
            intent?.getBooleanExtra(START_DIM, false) == true -> {
                start(allowDim = true)
            }
            intent?.getBooleanExtra(STOP_ACTION, false) == true -> {
                stop()
            }
            else -> throw IllegalStateException("Intent action unknown.")
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        releaseWakelock()
        super.onDestroy()
    }

    private fun start(allowDim: Boolean) {
        releaseWakelock()
        acquireWakeLock(allowDim)
        registerReceiver(screenOffReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
        startForeground(NOTIFICATION_ID, getNotification(allowDim))
    }

    private fun stop() {
        releaseWakelock()
        unregisterReceiver(screenOffReceiver)
        stopSelf()
    }

    private fun acquireWakeLock(allowDim: Boolean) {
        @Suppress("DEPRECATION")
        val wakeLockType = if (allowDim) {
            PowerManager.SCREEN_DIM_WAKE_LOCK
        } else {
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK
        }
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(wakeLockType, WAKE_LOCK_TAG)
        @Suppress("WakelockTimeout")
        wakeLock?.acquire()
    }

    private fun releaseWakelock() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
    }

    private fun getNotification(allowDim: Boolean): Notification {
        createNotificationChannel()
        val stopIntent = Intent(this, KeepAwakeService::class.java)
        stopIntent.putExtra(STOP_ACTION, true)
        val stopAction = PendingIntent.getService(
                this,
                0,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val icon = if (allowDim) R.drawable.ic_coffee else R.drawable.ic_coffee_plus
        val title = if (allowDim) R.string.notification_title_dim else R.string.notification_title_bright
        val text = if (allowDim) R.string.notification_big_text_dim else R.string.notification_big_text_bright

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(getString(title))
                .setContentText(getString(R.string.notification_text))
                .setStyle(NotificationCompat.BigTextStyle().bigText(getString(text)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_stop, getString(R.string.stop_action), stopAction)
                .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            channel.description = descriptionText
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}