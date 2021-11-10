package com.freekickr.trackerapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.freekickr.trackerapp.database.converters.Converters
import com.freekickr.trackerapp.database.daos.TrackDao
import com.freekickr.trackerapp.database.entities.Track

@Database(
    entities = [Track::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class TrackerDatabase: RoomDatabase() {

    abstract fun getTrackDao(): TrackDao

}