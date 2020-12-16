package com.udacity.asteroidradar.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.api.getEndDateFormatted
import com.udacity.asteroidradar.api.getStartDateFormatted

@Dao
interface AsteroidDao {

    /**
     * Ordering Asteroids in ascending order so we see the most relevant asteroid
     * data to our current date at the top of the list
     */
    @Query("SELECT * FROM asteroidtable ORDER BY date(closeApproachDate) ASC")
    fun getAsteroids(): LiveData<List<AsteroidEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg asteroids: AsteroidEntity)

    @Query("SELECT * FROM asteroidtable WHERE date(closeApproachDate) == date(:today)")
    fun getTodayAsteroids(today: String = getStartDateFormatted()): LiveData<List<AsteroidEntity>>

    @Query("SELECT * FROM asteroidtable WHERE date(:startDate) <= date(closeApproachDate) AND date(closeApproachDate) <= date(:endDate) ORDER BY date(closeApproachDate) ASC")
    fun getWeekAsteroids(startDate: String = getStartDateFormatted(), endDate: String = getEndDateFormatted()): LiveData<List<AsteroidEntity>>

    /**
     * Deleting all rows by yesterday's closeApproachDate
     */
    @Query("DELETE FROM asteroidtable WHERE date(closeApproachDate) <= date(:yesterday)")
    suspend fun removeYesterday(yesterday: String)

}