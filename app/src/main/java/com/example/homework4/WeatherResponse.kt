package com.example.homework4

import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val location: Location,
    val current: Current
)

@Serializable
data class Location(
    val name: String
)

@Serializable
data class Current(
    val temp_c: Float,
    val temp_f: Float
)
