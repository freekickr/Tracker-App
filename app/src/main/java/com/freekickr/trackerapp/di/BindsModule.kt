package com.freekickr.trackerapp.di

import com.freekickr.trackerapp.domain.repo.IDatabaseRepo
import com.freekickr.trackerapp.domain.repo.ISharedPreferencesRepo
import com.freekickr.trackerapp.repositories.DatabaseRepository
import com.freekickr.trackerapp.repositories.SharedPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface BindsModule {

    @Binds
    fun bindDatabaseRepo(impl: DatabaseRepository): IDatabaseRepo

    @Binds
    fun bindSharedPreferencesRepo(impl: SharedPreferencesRepository): ISharedPreferencesRepo

}