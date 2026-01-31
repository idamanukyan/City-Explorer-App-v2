package com.example.homework4

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val repository: WeatherRepository = WeatherRepositoryImpl(
        WeatherApiService(BuildConfig.WEATHER_API_KEY)
    )

    var temperatureUnit by mutableStateOf(TemperatureUnit.Celsius)
        private set

    var locationWeatherState: WeatherUiState by mutableStateOf(WeatherUiState.Idle)
        private set

    var cityWeatherState: WeatherUiState by mutableStateOf(WeatherUiState.Idle)
        private set

    private var cachedLocationCity: String? = null
    private var cachedLocationWeather: WeatherResponse? = null
    private var cachedCityWeather: WeatherResponse? = null

    fun toggleTemperatureUnit() {
        temperatureUnit = if (temperatureUnit == TemperatureUnit.Celsius) {
            TemperatureUnit.Fahrenheit
        } else {
            TemperatureUnit.Celsius
        }
        refreshDisplayStrings()
    }

    fun fetchLocationWeather(city: String) {
        viewModelScope.launch {
            locationWeatherState = WeatherUiState.Loading
            try {
                val weather = repository.getWeatherForCity(city)
                cachedLocationCity = city
                cachedLocationWeather = weather
                locationWeatherState = WeatherUiState.Success(formatLocationTemp(city, weather))
            } catch (e: Exception) {
                locationWeatherState = WeatherUiState.Error("Failed to fetch weather")
            }
        }
    }

    fun fetchCityWeather(cityName: String) {
        viewModelScope.launch {
            cityWeatherState = WeatherUiState.Loading
            try {
                val weather = repository.getWeatherForCity(cityName)
                cachedCityWeather = weather
                cityWeatherState = WeatherUiState.Success(formatCityTemp(weather))
            } catch (e: Exception) {
                cityWeatherState = WeatherUiState.Error("Failed to fetch weather")
            }
        }
    }

    fun setLocationError(message: String) {
        locationWeatherState = WeatherUiState.Error(message)
    }

    private fun refreshDisplayStrings() {
        cachedLocationWeather?.let { weather ->
            cachedLocationCity?.let { city ->
                locationWeatherState = WeatherUiState.Success(formatLocationTemp(city, weather))
            }
        }
        cachedCityWeather?.let { weather ->
            cityWeatherState = WeatherUiState.Success(formatCityTemp(weather))
        }
    }

    private fun formatLocationTemp(city: String, weather: WeatherResponse): String {
        return if (temperatureUnit == TemperatureUnit.Celsius) {
            "$city: ${weather.current.temp_c}°C"
        } else {
            "$city: ${weather.current.temp_f}°F"
        }
    }

    private fun formatCityTemp(weather: WeatherResponse): String {
        return if (temperatureUnit == TemperatureUnit.Celsius) {
            "${weather.current.temp_c}°C"
        } else {
            "${weather.current.temp_f}°F"
        }
    }
}
