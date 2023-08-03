package bez.dev.featurenotes.di

import android.app.Application
import androidx.lifecycle.ViewModel
import bez.dev.featurenotes.data.IRepository
import bez.dev.featurenotes.data.NoteRepository
import bez.dev.featurenotes.data.SharedPrefs
import bez.dev.featurenotes.misc.NotificationManager
import bez.dev.featurenotes.presenters.RepoViewModel
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
    @Singleton
    fun provideNotesRepo(sharedPrefs: SharedPrefs): IRepository {
        return NoteRepository(sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideSP(context: Application): SharedPrefs {
        return SharedPrefs(context)
    }

    @Provides
    @Singleton
    fun provideNotificationManager(context: Application): NotificationManager {
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
