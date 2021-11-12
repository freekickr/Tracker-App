package com.freekickr.trackerapp.repositories

import androidx.lifecycle.LiveData
import com.freekickr.trackerapp.database.daos.TrackDao
import com.freekickr.trackerapp.database.entities.Track
import com.freekickr.trackerapp.domain.TrackSortingOrder
import javax.inject.Inject

class MainRepository @Inject constructor(
    val dao: TrackDao
) {

    suspend fun insertTrack(track: Track) = dao.insert(track)

    suspend fun deleteTrack(track: Track) = dao.delete(track)

    fun getAllTracksSortedBy(order: TrackSortingOrder): LiveData<List<Track>> {
        return when(order) {
            TrackSortingOrder.AVG_SPEED -> dao.getAllSortedByAvgSpeed()
            TrackSortingOrder.CALORIES -> dao.getAllSortedByCalories()
            TrackSortingOrder.DATE -> dao.getAllSortedByDate()
            TrackSortingOrder.DISTANCE -> dao.getAllSortedByDistance()
            TrackSortingOrder.ELAPSED_TIME -> dao.getAllSortedByElapsedTime()
        }
    }

    fun getTotalAvgSpeed() = dao.getTotalAvgSpeed()

    fun getTotalDistance() = dao.getTotalDistance()

    fun getTotalCalories() = dao.getTotalCalories()

    fun getTotalElapsedTime() = dao.getTotalElapsedTime()

}