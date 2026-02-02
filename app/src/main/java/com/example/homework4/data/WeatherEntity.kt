package com.example.homework4.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherEntity(
    @PrimaryKey
    val cityName: String,
    val locationName: String,
    val tempCelsius: Float,
    val tempFahrenheit: Float,
    val lastUpdated: Long = System.currentTimeMillis()
)
