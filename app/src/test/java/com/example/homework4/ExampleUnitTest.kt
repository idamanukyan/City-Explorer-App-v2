package com.example.homework4

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCityInfoList() {
        assertEquals(5, citiesInfo.size)
        assertEquals("Berlin", citiesInfo[0].cityName)
        assertEquals("Paris", citiesInfo[1].cityName)
        assertEquals("London", citiesInfo[2].cityName)
        assertEquals("Tokyo", citiesInfo[3].cityName)
        assertEquals("New York", citiesInfo[4].cityName)
    }

    @Test
    fun testTemperatureUnitEnum() {
        assertEquals(2, TemperatureUnit.entries.size)
        assertEquals(TemperatureUnit.Celsius, TemperatureUnit.valueOf("Celsius"))
        assertEquals(TemperatureUnit.Fahrenheit, TemperatureUnit.valueOf("Fahrenheit"))
    }

    @Test
    fun testWeatherResponseDataClass() {
        val response = WeatherResponse(
            location = Location(name = "Tokyo"),
            current = Current(temp_c = 25.5f, temp_f = 77.9f)
        )
        assertEquals("Tokyo", response.location.name)
        assertEquals(25.5f, response.current.temp_c, 0.01f)
        assertEquals(77.9f, response.current.temp_f, 0.01f)
    }

    @Test
    fun testWeatherApiExceptionCarriesStatusAndMessage() {
        val exception = WeatherApiException(401, "Invalid API key")
        assertEquals(401, exception.statusCode)
        assertEquals("Invalid API key", exception.message)
    }
}
