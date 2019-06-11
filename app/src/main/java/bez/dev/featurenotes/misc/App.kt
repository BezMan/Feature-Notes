package bez.dev.featurenotes.misc

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import bez.dev.featurenotes.data.NoteDatabase
import bez.dev.featurenotes.services.OnClearFromRecentService

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        database = NoteDatabase.getInstance(appContext)
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE)

        startService(Intent(baseContext, OnClearFromRecentService::class.java))
    }

    companion object {
        private const val PREFS = "PREFS"
        lateinit var prefs: SharedPreferences
        lateinit var appContext: Context
        lateinit var database: NoteDatabase
    }


}
