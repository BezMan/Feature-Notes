package bez.dev.featurenotes.misc

import android.app.Application
import android.content.Context
import android.content.Intent
import bez.dev.featurenotes.data.NoteDatabase
import bez.dev.featurenotes.views.MainActivity
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        database = NoteDatabase.getInstance(appContext)

        // Set the default uncaught exception handler.
        handleUncaughtExceptions()

    }

    private fun handleUncaughtExceptions() {
        Thread.setDefaultUncaughtExceptionHandler { thread, ex ->
            ex.printStackTrace()
            triggerRestart(appContext)
        }
    }


    private fun triggerRestart(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        Runtime.getRuntime().exit(0)
    }

    companion object {
        lateinit var appContext: Context
        lateinit var database: NoteDatabase
    }


}
