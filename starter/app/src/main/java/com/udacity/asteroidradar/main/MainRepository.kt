package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.Resource
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.base.BaseRepository
import com.udacity.asteroidradar.db.AsteroidDao
import com.udacity.asteroidradar.db.PhotoOfTheDayDao
import com.udacity.asteroidradar.db.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject

class MainRepository (
    private val asteroidDao: AsteroidDao,
    private val photoOfTheDayDao: PhotoOfTheDayDao
) : BaseRepository() {

    /**
     * Return DB list of asteroid data as LiveData
     */
    val asteroids: LiveData<List<Asteroid>> = Transformations.map(asteroidDao.getAsteroids()) {
        it.asDomainModel()
    }

    /**
     * Return PhotoOTD model as LiveData
     */
    val photoOTD: LiveData<PictureOfDay> = Transformations.map(photoOfTheDayDao.getPhotoOfTheDay()) {
        it.asDomainModel()
    }

    /*suspend fun checkDB() : Boolean {
        var dbOkay = false

        safeApiCall { asteroidDao.getAsteroids() }.apply {
            // TODO: 12/15/20 Both results come back as Success, but also value null?
            if (this is Resource.Success) {
                if (value.value.isNullOrEmpty()) {
                    Log.w("MainRepository", "checkDB: result->$dbOkay getAsteroids() result is null or empty!")
                }else{
                    dbOkay = true
                    Log.d("MainRepository", "checkDB: result->$dbOkay")
                }
            }else{
                Log.w("MainRepository", "checkDB: result->$dbOkay getAsteroids() Failed!!")
            }
        }

        if(dbOkay) {
            safeApiCall { photoOfTheDayDao.getPhotoOfTheDay() }.apply {
                if (this is Resource.Success){
                    if (value.value == null) {
                        dbOkay = false
                        Log.w("MainRepository", "checkDB: result->$dbOkay getPhotoOfTheDay() result is null!")
                    }
                }else{
                    dbOkay = false
                    Log.w("MainRepository", "checkDB: result->$dbOkay getPhotoOfTheDay() Failed!")
                }
            }
        }else{
            Log.w("MainRepository", "checkDB: result->$dbOkay getAsteroids() Failed!")
        }
        Log.d("MainRepository", "checkDB: result->$dbOkay")

        return dbOkay
    }*/

    suspend fun refresh() {
        val retrofit = RetrofitDataSource().buildApi(AsteroidApi::class.java)

        safeApiCall {
            /**
             * startDate refers to day's formatted date, endDate refers to the formatted date of 7 days from now (defined in [Constants.kt]).
             */
            val startDate = getStartDateFormatted()
            val endDate = getEndDateFormatted()
            Log.d("MainRepository", "refresh: Grabbing Feed! $startDate $endDate")
            retrofit.getFeed(startDate, endDate)
        }.apply {
            if (this is Resource.Success){
                /**
                 * if query is successful, parse the response into the bits we need and save the model to our database in the respective format
                 */
                val stringResponse = value.stringSuspending()
                val asteroids = NetworkAsteroidContainer(parseAsteroidsJsonResult(JSONObject(stringResponse)))
                asteroidDao.insertAll(*asteroids.asDatabaseModel())
                Log.d("MainRepository", "refresh: Success on Feed, inserted into DB")
            }else{
                // TODO: 12/14/20 Occasionally gets a SocketTimeoutException in Retrofit
                Log.e("MainRepository", "refresh: feed failed!")
            }
        }

        safeApiCall {
            /**
             * Getting today's Photo Of The Day and placing it into the DB, replacing the last POTD
             * by using the same ID in our asDatabaseModel() every time it's called along with onConflictStrategy.REPLACE
             * in the DAO.
             */
            Log.d("MainRepository", "refresh: Grabbing POTD!")
            retrofit.getPOTD()
        }.apply {
            /**
             * if query was successful, and the media type returned is an image instead of video, insert into DB
             */
            if (this is Resource.Success){
                // TODO: 12/14/20 if type is video, display thumbnail of image with play button in the view
                // TODO: 12/14/20 https://img.youtube.com/vi/NuLuCeawQSo/maxresdefault.jpg
                Log.d("MainRepository", "refresh: Success on POTD, inserting into DB")
                photoOfTheDayDao.insert(value.asDatabaseModel())
                Log.d("MainRepository", "refresh: inserted POTD into DB")
            }else{
                Log.e("MainRepository", "refresh: POTD failed!")
            }
        }

    }

    /**
     * Removing rows from yesterday, used by WorkManager in [work.RefreshDataWork]
     */
    suspend fun removeYesterday() = safeApiCall { asteroidDao.removeYesterday(getYesterdayFormatted()) }


    @Suppress("BlockingMethodInNonBlockingContext")
    // Ensuring our thread blocking is on IO thread, even though it is through SafeApiCall...
    private suspend fun ResponseBody.stringSuspending() =
        withContext(Dispatchers.IO) { string() }


}