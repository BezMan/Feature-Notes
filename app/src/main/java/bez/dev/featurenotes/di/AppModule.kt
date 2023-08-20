package bez.dev.featurenotes.di

import android.app.Application
import bez.dev.featurenotes.data.NoteRepository
import bez.dev.featurenotes.data.SharedPrefs
import bez.dev.featurenotes.data.db.NoteDatabase
import bez.dev.featurenotes.data.domain.FlavorConfig
import bez.dev.featurenotes.data.domain.IRepository
import bez.dev.featurenotes.misc.NotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotesRepo(database: NoteDatabase, sharedPrefs: SharedPrefs, config: FlavorConfig): IRepository {
        return NoteRepository(database, sharedPrefs, config)
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

    @Provides
    fun provideInitNotesCount(): FlavorConfig {
        return FlavorConfig(FlavorData.INIT_NOTES_COUNT)
    }

}

//@Module
//@InstallIn(ViewModelComponent::class)
//object ViewModelModule {
//
//    @Provides
//    @Singleton
//    fun provideSharedViewModel(repository: IRepository): ViewModel {
//        return RepoViewModel(repository)
//    }
//
//}
