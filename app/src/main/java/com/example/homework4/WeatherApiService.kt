package com.example.homework4

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

class WeatherApiService(
    private val apiKey: String,
    private val client: HttpClient,
    private val json: Json
) {
    suspend fun getWeatherForCity(city: String): WeatherResponse {
        val response: HttpResponse = client.get(
            "https://api.weatherapi.com/v1/current.json?key=$apiKey&q=$city"
        )
        val body = response.bodyAsText()
        return json.decodeFromString(body)
    }
}
