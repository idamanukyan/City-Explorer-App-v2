package com.example.homework4

import com.example.homework4.data.WeatherDao
import com.example.homework4.data.WeatherEntity
import javax.inject.Inject

interface WeatherRepository {
    suspend fun getWeatherForCity(city: String): WeatherResponse
}

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: WeatherApiService,
    private val weatherDao: WeatherDao
) : WeatherRepository {

    companion object {
        private const val CACHE_DURATION_MS = 30 * 60 * 1000L // 30 minutes
    }

    override suspend fun getWeatherForCity(city: String): WeatherResponse {
        val cached = weatherDao.getWeatherForCity(city)
        if (cached != null && !isCacheExpired(cached)) {
            return cached.toWeatherResponse()
        }

        return try {
            val response = apiService.getWeatherForCity(city)
            weatherDao.insertWeather(response.toEntity(city))
            response
        } catch (e: Exception) {
            if (cached != null) {
                cached.toWeatherResponse()
            } else {
                throw e
            }
        }
    }

    private fun isCacheExpired(entity: WeatherEntity): Boolean {
        return System.currentTimeMillis() - entity.lastUpdated > CACHE_DURATION_MS
    }

    private fun WeatherEntity.toWeatherResponse(): WeatherResponse {
        return WeatherResponse(
            location = Location(name = locationName),
            current = Current(temp_c = tempCelsius, temp_f = tempFahrenheit)
        )
    }

    private fun WeatherResponse.toEntity(cityName: String): WeatherEntity {
        return WeatherEntity(
            cityName = cityName,
            locationName = location.name,
            tempCelsius = current.temp_c,
            tempFahrenheit = current.temp_f,
            lastUpdated = System.currentTimeMillis()
        )
    }
}
