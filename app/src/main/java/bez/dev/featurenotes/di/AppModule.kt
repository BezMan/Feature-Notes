package bez.dev.featurenotes.di

import android.content.Context
import androidx.lifecycle.ViewModel
import bez.dev.featurenotes.data.IRepository
import bez.dev.featurenotes.data.NoteRepository
import bez.dev.featurenotes.misc.App
import bez.dev.featurenotes.misc.NotificationManager
import bez.dev.featurenotes.view_models.RepoViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideAppContext(): Context {
        return App.appContext
    }

    @Provides
    @Singleton
    fun provideNotesRepo(): IRepository {
        return NoteRepository()
    }

    @Provides
    @Singleton
    fun provideNotificationManager(context: Context): NotificationManager {
        return NotificationManager(context)
    }

}

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    @Singleton
    fun provideSharedViewModel(repository: IRepository): ViewModel {
        return RepoViewModel(repository)
    }

}
