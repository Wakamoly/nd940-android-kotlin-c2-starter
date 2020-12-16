package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.db.RadarDatabase
import com.udacity.asteroidradar.main.MainRepository
import retrofit2.HttpException

class RefreshDataWork(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWork"
    }

    override suspend fun doWork(): Result {
        val asteroidDao = RadarDatabase(applicationContext).getAsteroidDao()
        val photoOfTheDayDao = RadarDatabase(applicationContext).getPhotoOfTheDayDao()
        val repository = MainRepository(asteroidDao, photoOfTheDayDao)

        return try {
            repository.removeYesterday()
            repository.refresh()
            Result.success()
        } catch (exception: HttpException) {
            Result.retry()
        }
    }

}
