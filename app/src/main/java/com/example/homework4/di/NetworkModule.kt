package com.example.homework4.di

import com.example.homework4.BuildConfig
import com.example.homework4.WeatherApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient = HttpClient(OkHttp)

    @Provides
    @Singleton
    @ApiKey
    fun provideApiKey(): String = BuildConfig.WEATHER_API_KEY

    @Provides
    @Singleton
    fun provideWeatherApiService(
        @ApiKey apiKey: String,
        client: HttpClient,
        json: Json
    ): WeatherApiService {
        return WeatherApiService(apiKey, client, json)
    }
}
