package com.udacity.asteroidradar.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        AsteroidEntity::class,
        PhotoOfTheDayEntity::class
    ],
    version = 1)
//@TypeConverters(DbConverters::class)
abstract class RadarDatabase : RoomDatabase() {

    abstract fun getAsteroidDao() : AsteroidDao
    abstract fun getPhotoOfTheDayDao() : PhotoOfTheDayDao

    companion object {
        val DATABASE_NAME = "radardatabase"
        @Volatile private var instance : RadarDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        /**
         *
         * fallbackToDestructive since we don't care if we lose data on changed schema
         * otherwise, we could create our own migrations via explaining changes to the DB about changed entities
         *
         * */
        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            RadarDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

    }

}