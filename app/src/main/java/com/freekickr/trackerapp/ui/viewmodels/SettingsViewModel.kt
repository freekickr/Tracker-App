package com.freekickr.trackerapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.freekickr.trackerapp.domain.User
import com.freekickr.trackerapp.domain.repo.ISharedPreferencesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val sharedPreferencesRepo: ISharedPreferencesRepo): ViewModel() {

    fun getUser(): User {
        return User(
            name = sharedPreferencesRepo.getUserName(),
            weight = sharedPreferencesRepo.getUserWeight()
        )
    }

    fun updateUser(name: String, weight: String) {
        sharedPreferencesRepo.putUser(name, weight)
    }
}