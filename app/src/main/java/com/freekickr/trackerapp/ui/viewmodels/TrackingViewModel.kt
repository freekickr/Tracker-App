package com.freekickr.trackerapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freekickr.trackerapp.database.entities.Track
import com.freekickr.trackerapp.domain.User
import com.freekickr.trackerapp.domain.repo.IDatabaseRepo
import com.freekickr.trackerapp.domain.repo.ISharedPreferencesRepo
import com.freekickr.trackerapp.repositories.SharedPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepo,
    private val sharedPreferencesRepository: ISharedPreferencesRepo
): ViewModel() {

    fun insertTrack(track: Track) {
        viewModelScope.launch {
            databaseRepository.insertTrack(track)
        }
    }

    fun getUserWeight(): Float {
        return sharedPreferencesRepository.getUserWeight()
    }

}