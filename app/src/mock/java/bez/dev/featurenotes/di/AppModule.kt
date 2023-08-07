package bez.dev.featurenotes.di

import bez.dev.featurenotes.data.domain.FlavorConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FlavorModule {

    @Provides
    fun provideInitNotesCount(): FlavorConfig {
        return FlavorConfig(5)
    }

}
