package com.freekickr.trackerapp.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.freekickr.trackerapp.R

class AppSettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey)
        setupInputType()
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        return when (preference.key) {
            requireContext().resources.getString(R.string.key_user_name) -> {

                true
            }
            requireContext().resources.getString(R.string.key_user_weight) -> {
                true
            }
            requireContext().resources.getString(R.string.key_language) -> {
                true
            }
            requireContext().resources.getString(R.string.key_clear_tracks) -> {
                true
            }
            else -> {
                super.onPreferenceTreeClick(preference)
            }
        }
    }

    private fun setupInputType() {
        findPreference<EditTextPreference>(requireContext().resources.getString(R.string.key_user_name))?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        }

        findPreference<EditTextPreference>(requireContext().resources.getString(R.string.key_user_weight))?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

        }
    }
}