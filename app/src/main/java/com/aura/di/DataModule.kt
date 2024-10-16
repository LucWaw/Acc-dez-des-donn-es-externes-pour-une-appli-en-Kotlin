package com.openclassrooms.stellarforecast.di

import com.aura.data.network.UserClient
import com.aura.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideWeatherRepository(dataClient: UserClient): UserRepository {
        return UserRepository(dataClient)
    }
}
