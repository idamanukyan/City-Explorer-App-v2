package com.example.homework4

interface WeatherRepository {
    suspend fun getWeatherForCity(city: String): WeatherResponse
}

class WeatherRepositoryImpl(
    private val apiService: WeatherApiService
) : WeatherRepository {
    override suspend fun getWeatherForCity(city: String): WeatherResponse {
        return apiService.getWeatherForCity(city)
    }
}
