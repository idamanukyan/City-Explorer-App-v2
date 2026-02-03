package com.example.homework4

data class WeatherDisplayData(
    val locationName: String,
    val country: String,
    val localtime: String,
    val temperature: String,
    val feelsLike: String,
    val conditionText: String,
    val conditionIconUrl: String,
    val humidity: String,
    val wind: String,
    val uvIndex: String,
    val isDay: Boolean
)
