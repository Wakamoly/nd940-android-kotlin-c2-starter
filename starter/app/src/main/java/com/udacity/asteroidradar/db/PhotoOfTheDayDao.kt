package com.udacity.asteroidradar.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoOfTheDayDao {

    @Query("SELECT * FROM photoofthedaytable ORDER BY id DESC LIMIT 1")
    fun getPhotoOfTheDay(): LiveData<PhotoOfTheDayEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: PhotoOfTheDayEntity)

}