package com.freekickr.trackerapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.freekickr.trackerapp.domain.TrackSortingOrder
import com.freekickr.trackerapp.domain.repo.IDatabaseRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(private val repository: IDatabaseRepo) : ViewModel() {

    val totalTimeRun = repository.getTotalElapsedTime()
    val totalDistance = repository.getTotalDistance()
    val totalCalories = repository.getTotalCalories()
    val avgSpeed = repository.getTotalAvgSpeed()

    val runsSortedByDate = repository.getAllTracksSortedBy(TrackSortingOrder.DATE)

}