package com.udacity.asteroidradar.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.db.AsteroidEntity
import com.udacity.asteroidradar.db.PhotoOfTheDayEntity


// TODO: 12/14/20 Add comments about each class

@JsonClass(generateAdapter = true)
data class NetworkAsteroidContainer(val asteroids: List<NetworkAsteroid>)

@JsonClass(generateAdapter = true)
data class NetworkAsteroid(
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean)

fun NetworkAsteroidContainer.asDomainModel() : List<Asteroid> {
    return asteroids.map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

fun NetworkAsteroidContainer.asDatabaseModel() : Array<AsteroidEntity> {
    return asteroids.map {
        AsteroidEntity(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}



@JsonClass(generateAdapter = true)
data class NetworkPhotoOTDContainer(val photoOTD: NetworkPhotoOTD)

@JsonClass(generateAdapter = true)
data class NetworkPhotoOTD(
    val media_type: String,
    val title: String,
    val url: String)

fun NetworkPhotoOTDContainer.asDomainModel() : PictureOfDay {
    return PictureOfDay(
        photoOTD.media_type,
        photoOTD.title,
        photoOTD.url
    )
}


/**
 * PhotoOfTheDay as Database Model, ID of 1 so we only replace the last POTD with the most current always.
 */
fun NetworkPhotoOTD.asDatabaseModel() : PhotoOfTheDayEntity {
    return PhotoOfTheDayEntity(
        0,
        media_type,
        title,
        url
    )
}

