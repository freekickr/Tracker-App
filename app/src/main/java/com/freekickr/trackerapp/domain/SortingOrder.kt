package com.freekickr.trackerapp.domain

enum class TrackSortingOrder(val description: String) {
    DATE("Date"),
    ELAPSED_TIME("Elapsed time"),
    DISTANCE("Distance"),
    AVG_SPEED("Average speed"),
    CALORIES("Calories burned");

    override fun toString(): String {
        return description
    }
}