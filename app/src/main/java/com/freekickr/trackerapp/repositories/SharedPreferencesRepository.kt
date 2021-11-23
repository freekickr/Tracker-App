package com.freekickr.trackerapp.repositories

import android.content.Context
import androidx.preference.PreferenceManager
import com.freekickr.trackerapp.R
import com.freekickr.trackerapp.domain.repo.ISharedPreferencesRepo
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SharedPreferencesRepository @Inject constructor(@ApplicationContext private val app: Context) :
    ISharedPreferencesRepo {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)

    override fun putColdStartValue(value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(app.resources.getString(R.string.key_cold_start), !value)
            .apply()
    }

    override fun getColdStartValue(): Boolean {
        return sharedPreferences.getBoolean(app.resources.getString(R.string.key_cold_start), true)
    }

    override fun putUser(name: String, weight: String) {
        sharedPreferences.edit()
            .putString(app.resources.getString(R.string.key_user_name), name)
            .putString(app.resources.getString(R.string.key_user_weight), weight)
            .apply()
    }

    override fun getUserName(): String {
        return sharedPreferences.getString(app.resources.getString(R.string.key_user_name), "") ?: ""
    }

    override fun getUserWeight(): Float {
        return sharedPreferences.getString(app.resources.getString(R.string.key_user_weight), "0")?.toFloat() ?: "0".toFloat()
    }
}