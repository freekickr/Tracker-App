package com.freekickr.trackerapp.database.entities

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks_table")
data class Track(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val img: Bitmap? = null,
    val avgSpeed: Float = 0f,
    val distance: Int = 0,
    val elapsedTime: Long = 0L,
    val calories: Int = 0,
    val timestamp: Long = 0L
)