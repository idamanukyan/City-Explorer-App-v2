package com.example.homework4

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TemperatureFormatterTest {

    private val formatter = TemperatureFormatter()

    private val sampleResponse = WeatherResponse(
        location = Location(name = "Berlin", country = "Germany", localtime = "2024-01-15 14:30"),
        current = Current(
            temp_c = 20.0f,
            temp_f = 68.0f,
            condition = Condition(text = "Sunny", icon = "//cdn.weatherapi.com/icon.png"),
            humidity = 55,
            wind_kph = 15.0f,
            wind_mph = 9.3f,
            wind_dir = "NW",
            feelslike_c = 18.5f,
            feelslike_f = 65.3f,
            uv = 5.0f,
            is_day = 1
        )
    )

    @Test
    fun formatWeatherData_celsius_showsCelsiusValues() {
        val data = formatter.formatWeatherData(sampleResponse, TemperatureUnit.Celsius)

        assertEquals("20.0\u00B0C", data.temperature)
        assertEquals("18.5\u00B0C", data.feelsLike)
        assertEquals("15.0 kph NW", data.wind)
    }

    @Test
    fun formatWeatherData_fahrenheit_showsFahrenheitValues() {
        val data = formatter.formatWeatherData(sampleResponse, TemperatureUnit.Fahrenheit)

        assertEquals("68.0\u00B0F", data.temperature)
        assertEquals("65.3\u00B0F", data.feelsLike)
        assertEquals("9.3 mph NW", data.wind)
    }

    @Test
    fun formatWeatherData_locationFields() {
        val data = formatter.formatWeatherData(sampleResponse, TemperatureUnit.Celsius)

        assertEquals("Berlin", data.locationName)
        assertEquals("Germany", data.country)
        assertEquals("2024-01-15 14:30", data.localtime)
    }

    @Test
    fun formatWeatherData_weatherDetails() {
        val data = formatter.formatWeatherData(sampleResponse, TemperatureUnit.Celsius)

        assertEquals("Sunny", data.conditionText)
        assertEquals("55%", data.humidity)
        assertEquals("5.0", data.uvIndex)
        assertTrue(data.isDay)
    }

    @Test
    fun formatWeatherData_iconUrl_prependsHttps() {
        val data = formatter.formatWeatherData(sampleResponse, TemperatureUnit.Celsius)
        assertEquals("https://cdn.weatherapi.com/icon.png", data.conditionIconUrl)
    }

    @Test
    fun formatWeatherData_iconUrl_preservesFullUrl() {
        val response = sampleResponse.copy(
            current = sampleResponse.current.copy(
                condition = Condition(text = "Rain", icon = "https://example.com/icon.png")
            )
        )
        val data = formatter.formatWeatherData(response, TemperatureUnit.Celsius)
        assertEquals("https://example.com/icon.png", data.conditionIconUrl)
    }

    @Test
    fun formatWeatherData_nightTime() {
        val response = sampleResponse.copy(
            current = sampleResponse.current.copy(is_day = 0)
        )
        val data = formatter.formatWeatherData(response, TemperatureUnit.Celsius)
        assertEquals(false, data.isDay)
    }
}
