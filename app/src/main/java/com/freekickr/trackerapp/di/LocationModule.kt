package com.freekickr.trackerapp.di

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.freekickr.trackerapp.ui.MainActivity
import com.freekickr.trackerapp.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Singleton

@Module
@InstallIn(ServiceComponent::class)
object LocationModule {

    @SuppressLint("VisibleForTests")
    @ServiceScoped
    @Provides
    fun provideFusedLocationProvider(@ApplicationContext context: Context) =
        FusedLocationProviderClient(context)

}