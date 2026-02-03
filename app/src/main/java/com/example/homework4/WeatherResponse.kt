package com.example.homework4

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val location: Location,
    val current: Current
)

@Serializable
data class Location(
    val name: String,
    val country: String = "",
    val localtime: String = ""
)

@Serializable
data class Condition(
    val text: String = "",
    val icon: String = ""
)

@Serializable
data class Current(
    val temp_c: Float,
    val temp_f: Float,
    val condition: Condition = Condition(),
    val humidity: Int = 0,
    val wind_kph: Float = 0f,
    val wind_mph: Float = 0f,
    val wind_dir: String = "",
    val feelslike_c: Float = 0f,
    val feelslike_f: Float = 0f,
    val uv: Float = 0f,
    val is_day: Int = 1
)
