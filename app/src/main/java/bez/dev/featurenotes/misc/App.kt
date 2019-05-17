package bez.dev.featurenotes.misc

import android.app.Application
import android.content.Context
import android.content.Intent
import bez.dev.featurenotes.data.NoteDatabase
import bez.dev.featurenotes.services.OnClearFromRecentService

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        database = NoteDatabase.getInstance(appContext)
        notificationManager = NotificationManager(appContext)

        startService(Intent(baseContext, OnClearFromRecentService::class.java))
    }

    companion object {
        lateinit var appContext: Context
        lateinit var database: NoteDatabase
        lateinit var notificationManager: NotificationManager
    }


}
