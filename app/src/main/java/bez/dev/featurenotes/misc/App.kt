package bez.dev.featurenotes.misc

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import bez.dev.featurenotes.data.NoteDatabase
import bez.dev.featurenotes.koin_injection.appModule
import bez.dev.featurenotes.koin_injection.viewModelModule
import bez.dev.featurenotes.services.OnClearFromRecentService
import bez.dev.featurenotes.views.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(OnUncaughtException)
        initAsync()
        initKoin()
    }


    object OnUncaughtException : Thread.UncaughtExceptionHandler {
        override fun uncaughtException(t: Thread, e: Throwable) {
            Log.e("App triggerRebirth", "DefaultUncaughtExceptionHandler")
            val app = App()
            app.triggerRebirth()
        }
    }


    fun triggerRebirth() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        Runtime.getRuntime().exit(0)
    }


    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            modules(listOf(appModule, viewModelModule))
        }
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
