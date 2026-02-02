package com.example.homework4

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemperatureFormatter @Inject constructor() {

    fun formatLocationTemp(city: String, weather: WeatherResponse, unit: TemperatureUnit): String {
        return when (unit) {
            TemperatureUnit.Celsius -> "$city: ${weather.current.temp_c}\u00B0C"
            TemperatureUnit.Fahrenheit -> "$city: ${weather.current.temp_f}\u00B0F"
        }
    }

    fun formatCityTemp(weather: WeatherResponse, unit: TemperatureUnit): String {
        return when (unit) {
            TemperatureUnit.Celsius -> "${weather.current.temp_c}\u00B0C"
            TemperatureUnit.Fahrenheit -> "${weather.current.temp_f}\u00B0F"
        }
    }
}
