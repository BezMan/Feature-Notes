package bez.dev.featurenotes.koin_injection

import bez.dev.featurenotes.data.NoteRepository
import bez.dev.featurenotes.misc.NotificationManager
import bez.dev.featurenotes.view_models.RepoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


var appModule = module {

    single { NotificationManager(get()) }
    single { NoteRepository() }

}

val viewModelModule = module {
    viewModel { RepoViewModel(get()) }
}