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
        val cacheKey = city.lowercase().trim()
        val cached = weatherDao.getWeatherForCity(cacheKey)
        if (cached != null && !isCacheExpired(cached)) {
            return cached.toWeatherResponse()
        }

        return try {
            val response = apiService.getWeatherForCity(city)
            weatherDao.insertWeather(response.toEntity(cacheKey))
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
            location = Location(
                name = locationName,
                country = country,
                localtime = localtime
            ),
            current = Current(
                temp_c = tempCelsius,
                temp_f = tempFahrenheit,
                condition = Condition(
                    text = conditionText,
                    icon = conditionIcon
                ),
                humidity = humidity,
                wind_kph = windKph,
                wind_mph = windMph,
                wind_dir = windDir,
                feelslike_c = feelslikeCelsius,
                feelslike_f = feelslikeFahrenheit,
                uv = uv,
                is_day = isDay
            )
        )
    }

    private fun WeatherResponse.toEntity(cityName: String): WeatherEntity {
        return WeatherEntity(
            cityName = cityName,
            locationName = location.name,
            tempCelsius = current.temp_c,
            tempFahrenheit = current.temp_f,
            country = location.country,
            localtime = location.localtime,
            conditionText = current.condition.text,
            conditionIcon = current.condition.icon,
            humidity = current.humidity,
            windKph = current.wind_kph,
            windMph = current.wind_mph,
            windDir = current.wind_dir,
            feelslikeCelsius = current.feelslike_c,
            feelslikeFahrenheit = current.feelslike_f,
            uv = current.uv,
            isDay = current.is_day,
            lastUpdated = System.currentTimeMillis()
        )
    }
}
