package os.dtakac.caffeine

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat

class CaffeineKeepAwakeService : Service() {
    companion object {
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

    override fun onCreate() {
        super.onCreate()
        start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getBooleanExtra(STOP_ACTION, false) == true) {
            stop()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        releaseWakelock()
        super.onDestroy()
    }

    private fun start() {
        // keep screen on
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        releaseWakelock()
        @Suppress("DEPRECATION")
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, WAKE_LOCK_TAG)
        @Suppress("WakelockTimeout")
        wakeLock?.acquire()
        // listen for screen off
        registerReceiver(screenOffReceiver)
        // promote to foreground
        startForeground(NOTIFICATION_ID, getNotification())
    }

    private fun stop() {
        releaseWakelock()
        unregisterReceiver(screenOffReceiver)
        stopSelf()
    }

    private fun releaseWakelock() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
    }

    private fun getNotification(): Notification {
        createNotificationChannel()
        val stopIntent = Intent(this, CaffeineKeepAwakeService::class.java)
        stopIntent.putExtra(STOP_ACTION, true)
        val stopAction = PendingIntent.getService(
                this,
                0,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_coffee)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setStyle(NotificationCompat.BigTextStyle().bigText(getString(R.string.notification_big_text)))
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