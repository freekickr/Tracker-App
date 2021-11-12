package com.freekickr.trackerapp.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.freekickr.trackerapp.R
import com.freekickr.trackerapp.ui.MainActivity
import com.freekickr.trackerapp.utils.Constants.ACTION_PAUSE_SERVICE
import com.freekickr.trackerapp.utils.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.freekickr.trackerapp.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import com.freekickr.trackerapp.utils.Constants.ACTION_STOP_SERVICE
import com.freekickr.trackerapp.utils.Constants.FASTEST_UPDATES_INTERVAL
import com.freekickr.trackerapp.utils.Constants.LOCATION_UPDATES_INTERVAL
import com.freekickr.trackerapp.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.freekickr.trackerapp.utils.Constants.NOTIFICATION_CHANNEL_NAME
import com.freekickr.trackerapp.utils.Constants.NOTIFICATION_ID
import com.freekickr.trackerapp.utils.PermissionsChecker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    private var isFirstRun: Boolean = true

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val _timeRunInSeconds = MutableLiveData<Long>(0L)
    private val timeRunInSeconds: LiveData<Long>
        get() = _timeRunInSeconds

    companion object {
        private val _isTracking = MutableLiveData<Boolean>(false)
        val isTracking: LiveData<Boolean>
            get() = _isTracking

        private val _pathPoints = MutableLiveData<Polylines>(mutableListOf(mutableListOf()))
        val pathPoints: LiveData<Polylines>
            get() = _pathPoints

        private val _timeRunInMillis = MutableLiveData<Long>(0L)
        val timeRunInMillis: LiveData<Long>
            get() = _timeRunInMillis
    }

    override fun onCreate() {
        super.onCreate()
        isTracking.observe(this, {
            updateLocationTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        Timber.d("onStartCommand: start")
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("onStartCommand: resume")
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("onStartCommand: pause")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("onStartCommand: stop")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    private fun startTimer() {
        addEmptyPolyline()
        _isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                lapTime = System.currentTimeMillis() - timeStarted

                _timeRunInMillis.postValue(timeRun + lapTime)

                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    _timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(50L)
            }
            timeRun += lapTime
        }
    }

    private fun pauseService() {
        _isTracking.postValue(false)
        isTimerEnabled = false
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            val request = LocationRequest.create().apply {
                interval = LOCATION_UPDATES_INTERVAL
                fastestInterval = FASTEST_UPDATES_INTERVAL
                priority = PRIORITY_HIGH_ACCURACY
            }
            if (checkPermissions()) {
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun checkPermissions(): Boolean {
        val finePermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarsePermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        return finePermission == PackageManager.PERMISSION_GRANTED && coarsePermission == PackageManager.PERMISSION_GRANTED
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result.locations.let { locations ->
                    locations.forEach {
                        addPathPoint(it)
                        Timber.d("new location ${it.latitude} ${it.longitude}")
                    }
                }
            }
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(it.latitude, it.longitude)
            _pathPoints.value?.apply {
                last().add(pos)
                _pathPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        _pathPoints.postValue(this)
    } ?: _pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForegroundService() {
        startTimer()
        _isTracking.postValue(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
            .setContentTitle("Tracker App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

}