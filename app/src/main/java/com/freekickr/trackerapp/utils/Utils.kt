package com.freekickr.trackerapp.utils

import android.widget.EditText

object Utils {

    fun checkEditTextFields(views: List<EditText>): Boolean {
        var result = true
        views.forEach {
            when {
                it.text.toString().trim().isEmpty() -> {
                    it.error = "Поле не может быть пустым"
                    result = false
                }
                it.text.toString().length > 20 -> {
                    it.error = "Поле не может быть больше 20 символов"
                    result = false
                }
                else -> {
                    it.error = null
                    result = true
                }
            }
        }
        return result
    }

}