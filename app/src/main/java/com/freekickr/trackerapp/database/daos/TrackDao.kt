package com.freekickr.trackerapp.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.freekickr.trackerapp.database.entities.Track

@Dao
interface TrackDao: GenericDao<Track> {

    @Query("SELECT * FROM tracks_table ORDER BY timestamp DESC")
    fun getAllSortedByDate(): LiveData<List<Track>>

    @Query("SELECT * FROM tracks_table ORDER BY avgSpeed DESC")
    fun getAllSortedByAvgSpeed(): LiveData<List<Track>>

    @Query("SELECT * FROM tracks_table ORDER BY distance DESC")
    fun getAllSortedByDistance(): LiveData<List<Track>>

    @Query("SELECT * FROM tracks_table ORDER BY timeInterval DESC")
    fun getAllSortedByTimeInterval(): LiveData<List<Track>>

    @Query("SELECT * FROM tracks_table ORDER BY calories DESC")
    fun getAllSortedByCalories(): LiveData<List<Track>>

    @Query("SELECT SUM(timeInterval) FROM TRACKS_TABLE")
    fun getTotalTimeMillis(): LiveData<Long>

    @Query("SELECT SUM(calories) FROM TRACKS_TABLE")
    fun getTotalCalories(): LiveData<Int>

    @Query("SELECT SUM(distance) FROM TRACKS_TABLE")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT AVG(avgSpeed) FROM TRACKS_TABLE")
    fun getTotalAvgSpeed(): LiveData<Float>
}