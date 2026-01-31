package com.example.homework4

sealed class WeatherUiState {
    object Idle : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val temperature: String) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
