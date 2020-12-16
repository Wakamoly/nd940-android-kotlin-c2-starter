package com.udacity.asteroidradar.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AsteroidDao {

    /**
     * Ordering Asteroids in ascending order so we see the most relevant asteroid
     * data to our current date at the top of the list
     */
    @Query("SELECT * FROM asteroidtable ORDER BY id ASC")
    fun getAsteroids(): LiveData<List<AsteroidEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg asteroids: AsteroidEntity)

    /**
     * Deleting all rows by yesterday's closeApproachDate
     */
    @Query("DELETE FROM asteroidtable WHERE closeApproachDate = :yesterday")
    suspend fun removeYesterday(yesterday: String)

}