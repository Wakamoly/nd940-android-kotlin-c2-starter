package com.udacity.asteroidradar.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.PictureOfDay

@Entity(tableName = "photoofthedaytable")
data class PhotoOfTheDayEntity constructor(
    @PrimaryKey val id: Long,
    val mediaType: String,
    val title: String,
    val url: String
)

fun PhotoOfTheDayEntity.asDomainModel(): PictureOfDay {
    return PictureOfDay(mediaType, title, url)
}