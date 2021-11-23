package com.freekickr.trackerapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.freekickr.trackerapp.domain.repo.ISharedPreferencesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val sharedPreferencesRepo: ISharedPreferencesRepo
) : ViewModel() {

    fun checkColdStart(): Boolean {
        return sharedPreferencesRepo.getColdStartValue()
    }

}