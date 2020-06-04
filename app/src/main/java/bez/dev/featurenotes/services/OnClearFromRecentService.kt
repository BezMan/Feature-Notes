package bez.dev.featurenotes.services

import android.app.IntentService
import android.content.Intent
import bez.dev.featurenotes.misc.NotificationManager
import org.koin.android.ext.android.get


class OnClearFromRecentService : IntentService("OnClearFromRecentService") {


    override fun onHandleIntent(intent: Intent?) {
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }


    override fun onTaskRemoved(rootIntent: Intent) {

        get<NotificationManager>().cancelNotifications()

        stopSelf()
    }
}