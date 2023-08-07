package bez.dev.featurenotes.di

import android.app.Application
import androidx.lifecycle.ViewModel
import bez.dev.featurenotes.data.domain.IRepository
import bez.dev.featurenotes.data.db.NoteDatabase
import bez.dev.featurenotes.data.NoteRepository
import bez.dev.featurenotes.data.SharedPrefs
import bez.dev.featurenotes.misc.NotificationManager
import bez.dev.featurenotes.views.presenters.RepoViewModel
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
    fun provideNotesRepo(database: NoteDatabase, sharedPrefs: SharedPrefs): IRepository {
        return NoteRepository(database, sharedPrefs)
    }

    @Provides
    @Singleton
    fun provideNotesDatabase(context: Application): NoteDatabase {
        return NoteDatabase.getInstance(context)

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
