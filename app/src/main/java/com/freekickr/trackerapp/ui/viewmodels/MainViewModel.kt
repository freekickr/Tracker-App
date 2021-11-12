package com.freekickr.trackerapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.freekickr.trackerapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val repository: MainRepository) : ViewModel() {



}