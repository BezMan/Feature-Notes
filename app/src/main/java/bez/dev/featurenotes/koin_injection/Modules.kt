package bez.dev.featurenotes.koin_injection

import bez.dev.featurenotes.misc.NotificationManager
import org.koin.dsl.module


var appModule = module {

    single { NotificationManager(get()) }

}