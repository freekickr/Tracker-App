package com.freekickr.trackerapp.domain.repo

import androidx.lifecycle.LiveData
import com.freekickr.trackerapp.database.entities.Track
import com.freekickr.trackerapp.domain.TrackSortingOrder

interface IDatabaseRepo {

    suspend fun insertTrack(track: Track): Long

    suspend fun deleteTrack(track: Track)

    fun getAllTracksSortedBy(order: TrackSortingOrder): LiveData<List<Track>>

    fun getTotalAvgSpeed(): LiveData<Float>

    fun getTotalDistance(): LiveData<Int>

    fun getTotalCalories(): LiveData<Int>

    fun getTotalElapsedTime(): LiveData<Long>

}