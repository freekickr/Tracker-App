package com.freekickr.trackerapp.repositories

import androidx.lifecycle.LiveData
import com.freekickr.trackerapp.database.daos.TrackDao
import com.freekickr.trackerapp.database.entities.Track
import com.freekickr.trackerapp.domain.TrackSortingOrder
import com.freekickr.trackerapp.domain.repo.IDatabaseRepo
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val dao: TrackDao
): IDatabaseRepo {

    override suspend fun insertTrack(track: Track) = dao.insert(track)

    override suspend fun deleteTrack(track: Track) = dao.delete(track)

    override fun getAllTracksSortedBy(order: TrackSortingOrder): LiveData<List<Track>> {
        return when(order) {
            TrackSortingOrder.AVG_SPEED -> dao.getAllSortedByAvgSpeed()
            TrackSortingOrder.CALORIES -> dao.getAllSortedByCalories()
            TrackSortingOrder.DATE -> dao.getAllSortedByDate()
            TrackSortingOrder.DISTANCE -> dao.getAllSortedByDistance()
            TrackSortingOrder.ELAPSED_TIME -> dao.getAllSortedByElapsedTime()
        }
    }

    override fun getTotalAvgSpeed() = dao.getTotalAvgSpeed()

    override fun getTotalDistance() = dao.getTotalDistance()

    override fun getTotalCalories() = dao.getTotalCalories()

    override fun getTotalElapsedTime() = dao.getTotalElapsedTime()

}