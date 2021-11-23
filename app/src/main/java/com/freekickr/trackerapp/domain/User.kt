package com.freekickr.trackerapp.domain

data class User(
    val name: String,
    val weight: Float
) {
    companion object {
        fun getDummy() = User(
            "Name",
            100.0f
        )
    }
}