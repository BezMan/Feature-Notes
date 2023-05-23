package bez.dev.featurenotes.misc

import android.app.Application
import android.content.Context
import bez.dev.featurenotes.data.NoteDatabase
import bez.dev.featurenotes.koin_injection.appModule
import bez.dev.featurenotes.koin_injection.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        initKoin(appContext)
        database = NoteDatabase.getInstance(appContext)
    }


    private fun initKoin(context: Context) {
        startKoin {
            androidContext(context)
            modules(listOf(appModule, viewModelModule))
        }
    }


    companion object {
        lateinit var appContext: Context
        lateinit var database: NoteDatabase
    }


}
