package com.example.homework4

import com.example.homework4.data.WeatherDao
import com.example.homework4.data.WeatherEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {

    private val mockApiService = mockk<WeatherApiService>()
    private val mockDao = mockk<WeatherDao>(relaxed = true)
    private lateinit var repository: WeatherRepositoryImpl

    private val apiResponse = WeatherResponse(
        location = Location(name = "Berlin", country = "Germany", localtime = "2024-01-15 14:00"),
        current = Current(
            temp_c = 10.0f,
            temp_f = 50.0f,
            condition = Condition(text = "Cloudy", icon = "//cdn.example.com/cloudy.png"),
            humidity = 80,
            wind_kph = 20.0f,
            wind_mph = 12.4f,
            wind_dir = "SW"
        )
    )

    private fun freshCacheEntity(cityName: String = "berlin") = WeatherEntity(
        cityName = cityName,
        locationName = "Berlin",
        tempCelsius = 10.0f,
        tempFahrenheit = 50.0f,
        country = "Germany",
        localtime = "2024-01-15 14:00",
        conditionText = "Cloudy",
        conditionIcon = "//cdn.example.com/cloudy.png",
        humidity = 80,
        windKph = 20.0f,
        windMph = 12.4f,
        windDir = "SW",
        lastUpdated = System.currentTimeMillis() // fresh
    )

    private fun expiredCacheEntity(cityName: String = "berlin") = freshCacheEntity(cityName).copy(
        lastUpdated = System.currentTimeMillis() - (31 * 60 * 1000L) // 31 minutes ago
    )

    @Before
    fun setUp() {
        repository = WeatherRepositoryImpl(mockApiService, mockDao)
    }

    @Test
    fun getWeatherForCity_freshCache_returnsCachedData() = runTest {
        val entity = freshCacheEntity()
        coEvery { mockDao.getWeatherForCity("berlin") } returns entity

        val result = repository.getWeatherForCity("Berlin")

        assertEquals("Berlin", result.location.name)
        assertEquals(10.0f, result.current.temp_c, 0.01f)
        coVerify(exactly = 0) { mockApiService.getWeatherForCity(any()) }
    }

    @Test
    fun getWeatherForCity_expiredCache_callsApi() = runTest {
        val entity = expiredCacheEntity()
        coEvery { mockDao.getWeatherForCity("berlin") } returns entity
        coEvery { mockApiService.getWeatherForCity("Berlin") } returns apiResponse

        val result = repository.getWeatherForCity("Berlin")

        assertEquals("Berlin", result.location.name)
        coVerify(exactly = 1) { mockApiService.getWeatherForCity("Berlin") }
        coVerify(exactly = 1) { mockDao.insertWeather(any()) }
    }

    @Test
    fun getWeatherForCity_noCache_callsApi() = runTest {
        coEvery { mockDao.getWeatherForCity("berlin") } returns null
        coEvery { mockApiService.getWeatherForCity("Berlin") } returns apiResponse

        val result = repository.getWeatherForCity("Berlin")

        assertEquals("Berlin", result.location.name)
        coVerify(exactly = 1) { mockApiService.getWeatherForCity("Berlin") }
    }

    @Test
    fun getWeatherForCity_apiFails_fallsBackToExpiredCache() = runTest {
        val entity = expiredCacheEntity()
        coEvery { mockDao.getWeatherForCity("berlin") } returns entity
        coEvery { mockApiService.getWeatherForCity("Berlin") } throws RuntimeException("Network error")

        val result = repository.getWeatherForCity("Berlin")

        assertEquals("Berlin", result.location.name)
    }

    @Test
    fun getWeatherForCity_apiFails_noCache_throws() = runTest {
        coEvery { mockDao.getWeatherForCity("berlin") } returns null
        coEvery { mockApiService.getWeatherForCity("Berlin") } throws RuntimeException("Network error")

        try {
            repository.getWeatherForCity("Berlin")
            fail("Expected exception")
        } catch (e: RuntimeException) {
            assertEquals("Network error", e.message)
        }
    }

    @Test
    fun getWeatherForCity_normalizesCacheKey() = runTest {
        coEvery { mockDao.getWeatherForCity("berlin") } returns null
        coEvery { mockApiService.getWeatherForCity("BERLIN") } returns apiResponse

        repository.getWeatherForCity("BERLIN")

        coVerify { mockDao.getWeatherForCity("berlin") }
        coVerify { mockDao.insertWeather(match { it.cityName == "berlin" }) }
    }

    @Test
    fun getWeatherForCity_trimsCacheKey() = runTest {
        coEvery { mockDao.getWeatherForCity("berlin") } returns null
        coEvery { mockApiService.getWeatherForCity(" Berlin ") } returns apiResponse

        repository.getWeatherForCity(" Berlin ")

        coVerify { mockDao.getWeatherForCity("berlin") }
    }
}
