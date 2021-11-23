package com.freekickr.trackerapp.di

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.freekickr.trackerapp.R
import com.freekickr.trackerapp.ui.MainActivity
import com.freekickr.trackerapp.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @SuppressLint("VisibleForTests")
    @ServiceScoped
    @Provides
    fun provideFusedLocationProvider(@ApplicationContext context: Context) =
        FusedLocationProviderClient(context)

    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(@ApplicationContext context: Context): PendingIntent =
        PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).also {
                it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    @ServiceScoped
    @Provides
    fun provideNotificationBuilder(
        @ApplicationContext context: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
        .setContentTitle("Tracker App")
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)

}