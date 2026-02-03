package com.example.homework4

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemperatureFormatter @Inject constructor() {

    fun formatWeatherData(weather: WeatherResponse, unit: TemperatureUnit): WeatherDisplayData {
        val temperature = when (unit) {
            TemperatureUnit.Celsius -> "${weather.current.temp_c}\u00B0C"
            TemperatureUnit.Fahrenheit -> "${weather.current.temp_f}\u00B0F"
        }
        val feelsLike = when (unit) {
            TemperatureUnit.Celsius -> "${weather.current.feelslike_c}\u00B0C"
            TemperatureUnit.Fahrenheit -> "${weather.current.feelslike_f}\u00B0F"
        }
        val wind = when (unit) {
            TemperatureUnit.Celsius -> "${weather.current.wind_kph} kph ${weather.current.wind_dir}"
            TemperatureUnit.Fahrenheit -> "${weather.current.wind_mph} mph ${weather.current.wind_dir}"
        }
        val iconUrl = weather.current.condition.icon.let { icon ->
            if (icon.startsWith("//")) "https:$icon" else icon
        }

        return WeatherDisplayData(
            locationName = weather.location.name,
            country = weather.location.country,
            localtime = weather.location.localtime,
            temperature = temperature,
            feelsLike = feelsLike,
            conditionText = weather.current.condition.text,
            conditionIconUrl = iconUrl,
            humidity = "${weather.current.humidity}%",
            wind = wind,
            uvIndex = "${weather.current.uv}",
            isDay = weather.current.is_day == 1
        )
    }
}
