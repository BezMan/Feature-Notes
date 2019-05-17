package bez.dev.featurenotes.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import bez.dev.featurenotes.misc.App


class OnClearFromRecentService : Service() {


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }


    override fun onTaskRemoved(rootIntent: Intent) {

        App.notificationManager.cancelNotifications()

        stopSelf()
    }
}