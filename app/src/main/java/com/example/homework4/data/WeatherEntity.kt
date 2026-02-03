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
    val country: String = "",
    val localtime: String = "",
    val conditionText: String = "",
    val conditionIcon: String = "",
    val humidity: Int = 0,
    val windKph: Float = 0f,
    val windMph: Float = 0f,
    val windDir: String = "",
    val feelslikeCelsius: Float = 0f,
    val feelslikeFahrenheit: Float = 0f,
    val uv: Float = 0f,
    val isDay: Int = 1,
    val lastUpdated: Long = System.currentTimeMillis()
)
