package com.example.homework4

import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    private val mockWeatherApiService = mockk<WeatherApiService>()

    @Test
    fun testWeatherApiService() {
        val cityName = "Berlin"
        val mockWeatherResponse = WeatherResponse(
            location = Location(name = "Berlin"),
            current = Current(temp_c = 15.0f, temp_f = 59.0f)
        )
        coEvery { mockWeatherApiService.getWeatherForCity(cityName) } returns mockWeatherResponse
    }

    @Test
    fun testTemperatureConversion() {
        val tempC = 20.0f
        val tempF = 68.0f
        val response = WeatherResponse(
            location = Location(name = "Berlin"),
            current = Current(temp_c = tempC, temp_f = tempF)
        )

        val celsiusDisplay = "${response.current.temp_c}\u00B0C"
        assertEquals("20.0\u00B0C", celsiusDisplay)

        val fahrenheitDisplay = "${response.current.temp_f}\u00B0F"
        assertEquals("68.0\u00B0F", fahrenheitDisplay)
    }

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
    fun testTemperatureUnitEnum() {
        assertEquals(2, TemperatureUnit.entries.size)
        assertEquals(TemperatureUnit.Celsius, TemperatureUnit.valueOf("Celsius"))
        assertEquals(TemperatureUnit.Fahrenheit, TemperatureUnit.valueOf("Fahrenheit"))
    }

    @Test
    fun testTemperatureFormatter() {
        val formatter = TemperatureFormatter()
        val response = WeatherResponse(
            location = Location(name = "Berlin"),
            current = Current(temp_c = 20.0f, temp_f = 68.0f)
        )

        assertEquals("Berlin: 20.0\u00B0C", formatter.formatLocationTemp("Berlin", response, TemperatureUnit.Celsius))
        assertEquals("Berlin: 68.0\u00B0F", formatter.formatLocationTemp("Berlin", response, TemperatureUnit.Fahrenheit))
        assertEquals("20.0\u00B0C", formatter.formatCityTemp(response, TemperatureUnit.Celsius))
        assertEquals("68.0\u00B0F", formatter.formatCityTemp(response, TemperatureUnit.Fahrenheit))
    }
}
