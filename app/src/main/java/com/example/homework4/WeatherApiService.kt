package com.example.homework4

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.json.JSONObject

class WeatherApiService(private val apiKey: String) {

    private val client = HttpClient(Android)

    suspend fun getWeatherForCity(city: String): WeatherResponse? {
        return try {
            val response: HttpResponse = client.get(
                "https://api.weatherapi.com/v1/current.json?key=$apiKey&q=$city"
            )
            val body = response.readText()
            val json = JSONObject(body)

            val locationJson = json.getJSONObject("location")
            val currentJson = json.getJSONObject("current")

            WeatherResponse(
                location = Location(name = locationJson.getString("name")),
                current = Current(
                    temp_c = currentJson.getDouble("temp_c").toFloat(),
                    temp_f = currentJson.getDouble("temp_f").toFloat()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
