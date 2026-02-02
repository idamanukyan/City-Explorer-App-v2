package com.example.homework4

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homework4.data.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationRepository: LocationRepository,
    private val temperatureFormatter: TemperatureFormatter,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    var temperatureUnit by mutableStateOf(TemperatureUnit.Celsius)
        private set

    var locationWeatherState: WeatherUiState by mutableStateOf(WeatherUiState.Idle)
        private set

    var cityWeatherState: WeatherUiState by mutableStateOf(WeatherUiState.Idle)
        private set

    private var cachedLocationCity: String? = null
    private var cachedLocationWeather: WeatherResponse? = null
    private var cachedCityWeather: WeatherResponse? = null

    init {
        viewModelScope.launch {
            preferencesRepository.temperatureUnitFlow.collect { unit ->
                temperatureUnit = unit
                refreshDisplayStrings()
            }
        }
    }

    fun toggleTemperatureUnit() {
        val newUnit = if (temperatureUnit == TemperatureUnit.Celsius) {
            TemperatureUnit.Fahrenheit
        } else {
            TemperatureUnit.Celsius
        }
        viewModelScope.launch {
            preferencesRepository.setTemperatureUnit(newUnit)
        }
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch {
            preferencesRepository.setTemperatureUnit(unit)
        }
    }

    fun fetchLocationWeather(city: String) {
        viewModelScope.launch {
            locationWeatherState = WeatherUiState.Loading
            try {
                val weather = repository.getWeatherForCity(city)
                cachedLocationCity = city
                cachedLocationWeather = weather
                locationWeatherState = WeatherUiState.Success(
                    temperatureFormatter.formatLocationTemp(city, weather, temperatureUnit)
                )
            } catch (e: Exception) {
                locationWeatherState = WeatherUiState.Error("Failed to fetch weather")
            }
        }
    }

    fun fetchLocationWeatherFromDevice() {
        viewModelScope.launch {
            val city = locationRepository.getCurrentCity()
            if (city != null) {
                fetchLocationWeather(city)
            } else {
                locationWeatherState = WeatherUiState.Error("Could not determine your city")
            }
        }
    }

    fun fetchCityWeather(cityName: String) {
        viewModelScope.launch {
            cityWeatherState = WeatherUiState.Loading
            try {
                val weather = repository.getWeatherForCity(cityName)
                cachedCityWeather = weather
                cityWeatherState = WeatherUiState.Success(
                    temperatureFormatter.formatCityTemp(weather, temperatureUnit)
                )
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
                locationWeatherState = WeatherUiState.Success(
                    temperatureFormatter.formatLocationTemp(city, weather, temperatureUnit)
                )
            }
        }
        cachedCityWeather?.let { weather ->
            cityWeatherState = WeatherUiState.Success(
                temperatureFormatter.formatCityTemp(weather, temperatureUnit)
            )
        }
    }
}
