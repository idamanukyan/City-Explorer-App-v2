package com.example.homework4

data class WeatherResponse(
    val location: Location,
    val current: Current
)

data class Location(
    val name: String
)

data class Current(
    val temp_c: Float,
    val temp_f: Float
)
