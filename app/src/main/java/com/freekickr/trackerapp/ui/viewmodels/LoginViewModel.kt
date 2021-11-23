package com.freekickr.trackerapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.freekickr.trackerapp.domain.repo.ISharedPreferencesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sharedPreferencesRepo: ISharedPreferencesRepo
    ) : ViewModel() {

    fun saveUser(name: String, weight: String) {
        sharedPreferencesRepo.putUser(name, weight)
    }

    fun saveColdStartValue(value: Boolean) {
        sharedPreferencesRepo.putColdStartValue(value)
    }

}