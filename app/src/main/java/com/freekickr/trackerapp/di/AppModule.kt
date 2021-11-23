package com.freekickr.trackerapp.di

import android.content.Context
import androidx.room.Room
import com.freekickr.trackerapp.database.TrackerDatabase
import com.freekickr.trackerapp.domain.repo.IDatabaseRepo
import com.freekickr.trackerapp.domain.repo.ISharedPreferencesRepo
import com.freekickr.trackerapp.repositories.DatabaseRepository
import com.freekickr.trackerapp.repositories.SharedPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext app: Context): TrackerDatabase =
        Room.databaseBuilder(app, TrackerDatabase::class.java, "tracker_db")
            .build()

    @Singleton
    @Provides
    fun provideTrackerDao(database: TrackerDatabase) = database.getTrackDao()

}