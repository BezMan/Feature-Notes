package bez.dev.featurenotes.misc

import android.app.Application
import android.content.Context
import android.content.Intent
import bez.dev.featurenotes.data.NoteDatabase
import bez.dev.featurenotes.services.OnClearFromRecentService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initAsync()
    }


    private fun initAsync() {
        CoroutineScope(Dispatchers.Default).launch {
            appContext = applicationContext
            database = NoteDatabase.getInstance(appContext)

            startService(Intent(baseContext, OnClearFromRecentService::class.java))
        }
    }


    companion object {
        lateinit var appContext: Context
        lateinit var database: NoteDatabase
    }


}
