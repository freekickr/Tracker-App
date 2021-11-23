package com.freekickr.trackerapp.domain.repo

import com.freekickr.trackerapp.domain.User

interface ISharedPreferencesRepo {

    fun putColdStartValue(value: Boolean)

    fun getColdStartValue(): Boolean

    fun putUser(name: String, weight: String)

    fun getUserName(): String

    fun getUserWeight(): Float
}