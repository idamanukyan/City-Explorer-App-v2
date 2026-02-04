package com.example.homework4

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class WeatherApiService(
    private val apiKey: String,
    private val client: HttpClient,
    private val json: Json
) {
    suspend fun getWeatherForCity(city: String): WeatherResponse {
        val response: HttpResponse = client.get("https://api.weatherapi.com/v1/current.json") {
            parameter("key", apiKey)
            parameter("q", city)
        }
        val body = response.bodyAsText()
        if (!response.status.isSuccess()) {
            throw WeatherApiException(response.status.value, parseErrorMessage(body))
        }
        return json.decodeFromString(body)
    }

    private fun parseErrorMessage(body: String): String {
        return try {
            val error = json.decodeFromString<WeatherApiError>(body)
            error.error.message
        } catch (_: Exception) {
            "Unknown API error"
        }
    }
}
