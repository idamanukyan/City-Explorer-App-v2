package com.example.homework4

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

class WeatherApiService(private val apiKey: String) {

    private val client = HttpClient(Android)
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getWeatherForCity(city: String): WeatherResponse {
        val response: HttpResponse = client.get(
            "https://api.weatherapi.com/v1/current.json?key=$apiKey&q=$city"
        )
        val body = response.readText()
        return json.decodeFromString(body)
    }
}
