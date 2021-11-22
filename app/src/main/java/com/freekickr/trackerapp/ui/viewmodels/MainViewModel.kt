package com.freekickr.trackerapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freekickr.trackerapp.database.entities.Track
import com.freekickr.trackerapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) : ViewModel() {

    fun insertTrack(track: Track) {
        viewModelScope.launch {
            repository.insertTrack(track)
        }
    }

}