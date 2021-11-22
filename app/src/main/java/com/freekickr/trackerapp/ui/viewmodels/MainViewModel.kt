package com.freekickr.trackerapp.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freekickr.trackerapp.database.entities.Track
import com.freekickr.trackerapp.domain.TrackSortingOrder
import com.freekickr.trackerapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    private val tracksSortedByDate = repository.getAllTracksSortedBy(TrackSortingOrder.DATE)
    private val tracksSortedByElapsedTime = repository.getAllTracksSortedBy(TrackSortingOrder.ELAPSED_TIME)
    private val tracksSortedByDistance = repository.getAllTracksSortedBy(TrackSortingOrder.DISTANCE)
    private val tracksSortedByCalories = repository.getAllTracksSortedBy(TrackSortingOrder.CALORIES)
    private val tracksSortedByAvgSpeed = repository.getAllTracksSortedBy(TrackSortingOrder.AVG_SPEED)

    val tracks = MediatorLiveData<List<Track>>()

    var sortType = TrackSortingOrder.DATE

    init {
        tracks.addSource(tracksSortedByDate) { result ->
            if (sortType == TrackSortingOrder.DATE) {
                result?.let {
                    tracks.value = it
                }
            }
        }
        tracks.addSource(tracksSortedByElapsedTime) { result ->
            if (sortType == TrackSortingOrder.ELAPSED_TIME) {
                result?.let {
                    tracks.value = it
                }
            }
        }
        tracks.addSource(tracksSortedByDistance) { result ->
            if (sortType == TrackSortingOrder.DISTANCE) {
                result?.let {
                    tracks.value = it
                }
            }
        }
        tracks.addSource(tracksSortedByCalories) { result ->
            if (sortType == TrackSortingOrder.CALORIES) {
                result?.let {
                    tracks.value = it
                }
            }
        }
        tracks.addSource(tracksSortedByAvgSpeed) { result ->
            if (sortType == TrackSortingOrder.AVG_SPEED) {
                result?.let {
                    tracks.value = it
                }
            }
        }
    }

    fun changeSortType(sort: TrackSortingOrder) {
        when(sort) {
            TrackSortingOrder.AVG_SPEED -> tracksSortedByAvgSpeed.value?.let { tracks.value = it }
            TrackSortingOrder.DISTANCE -> tracksSortedByDistance.value?.let { tracks.value = it }
            TrackSortingOrder.DATE -> tracksSortedByDate.value?.let { tracks.value = it }
            TrackSortingOrder.CALORIES -> tracksSortedByCalories.value?.let { tracks.value = it }
            TrackSortingOrder.ELAPSED_TIME -> tracksSortedByElapsedTime.value?.let { tracks.value = it }
        }.also {
            this.sortType = sort
        }
    }

    fun insertTrack(track: Track) {
        viewModelScope.launch {
            repository.insertTrack(track)
        }
    }

}