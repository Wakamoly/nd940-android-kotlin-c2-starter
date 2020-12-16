package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Constants
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidApi {

    @GET("neo/rest/v1/feed")
    suspend fun getFeed(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String = Constants.API_KEY
    ) : ResponseBody

    @GET("planetary/apod")
    suspend fun getPOTD(
        @Query("api_key") apiKey: String = Constants.API_KEY
    ) : NetworkPhotoOTD

}