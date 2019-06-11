package bez.dev.featurenotes.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import bez.dev.featurenotes.misc.NotificationManager


class OnClearFromRecentService : Service() {


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }


    override fun onTaskRemoved(rootIntent: Intent) {

        val notificationManager = NotificationManager(this)
        notificationManager.cancelNotifications()

        stopSelf()
    }
}