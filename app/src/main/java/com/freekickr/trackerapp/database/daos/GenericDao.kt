package com.freekickr.trackerapp.database.daos

import androidx.room.*

@Dao
interface GenericDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: T): Long

    @Update
    suspend fun update(value: T)

    @Delete
    suspend fun delete(value: T)

}