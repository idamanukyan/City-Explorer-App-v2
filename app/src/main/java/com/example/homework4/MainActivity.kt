package com.example.homework4

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.homework4.ui.theme.Homework4Theme
import java.util.Locale

enum class TemperatureUnit {
    Celsius,
    Fahrenheit
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Homework4Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val weatherApiService = remember { WeatherApiService("your_api_key_here") }
                    val temperatureUnit = remember { mutableStateOf(TemperatureUnit.Celsius) }

                    NavHost(navController = navController, startDestination = "welcome_screen") {
                        composable("welcome_screen") {
                            WelcomeScreen(
                                navController = navController,
                                weatherApiService = weatherApiService,
                                temperatureUnit = temperatureUnit.value,
                                onTemperatureUnitChanged = { selectedUnit ->
                                    temperatureUnit.value = selectedUnit
                                }
                            )
                        }
                        composable("second_screen/{cityName}") { backStackEntry ->
                            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
                            val cityInfo = citiesInfo.find { it.cityName == cityName }
                            cityInfo?.let {
                                SecondScreen(
                                    cityInfo = it,
                                    navController = navController,
                                    weatherApiService = weatherApiService,
                                    temperatureUnit = temperatureUnit.value
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    private fun getCurrentCity(): String? {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location != null) {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            return addresses?.firstOrNull()?.locality
        }
        return null
    }

    @Composable
    fun WelcomeScreen(
        navController: NavHostController,
        weatherApiService: WeatherApiService,
        temperatureUnit: TemperatureUnit,
        onTemperatureUnitChanged: (TemperatureUnit) -> Unit
    ) {
        var locationWeather by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(temperatureUnit) {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                try {
                    val city = getCurrentCity()
                    if (city != null) {
                        val weather = weatherApiService.getWeatherForCity(city)
                        weather?.let {
                            locationWeather = if (temperatureUnit == TemperatureUnit.Celsius) {
                                "$city: ${it.current.temp_c}°C"
                            } else {
                                "$city: ${it.current.temp_f}°F"
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "City Explorer",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            locationWeather?.let {
                Text(
                    text = "Current Location: $it",
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            citiesInfo.forEach { city ->
                Button(
                    onClick = { navController.navigate("second_screen/${city.cityName}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = "Explore ${city.cityName}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val newUnit = if (temperatureUnit == TemperatureUnit.Celsius) {
                    TemperatureUnit.Fahrenheit
                } else {
                    TemperatureUnit.Celsius
                }
                onTemperatureUnitChanged(newUnit)
            }) {
                Text(
                    text = "Switch to ${
                        if (temperatureUnit == TemperatureUnit.Celsius) "Fahrenheit" else "Celsius"
                    }"
                )
            }
        }
    }

    @Composable
    fun SecondScreen(
        cityInfo: CityInfo,
        navController: NavHostController,
        weatherApiService: WeatherApiService,
        temperatureUnit: TemperatureUnit
    ) {
        var weatherText by remember { mutableStateOf("Loading...") }

        LaunchedEffect(cityInfo, temperatureUnit) {
            try {
                val weatherResponse = weatherApiService.getWeatherForCity(cityInfo.cityName)
                if (weatherResponse != null) {
                    weatherText = if (temperatureUnit == TemperatureUnit.Celsius) {
                        "${weatherResponse.current.temp_c}°C"
                    } else {
                        "${weatherResponse.current.temp_f}°F"
                    }
                } else {
                    weatherText = "Unable to load weather data"
                }
            } catch (e: Exception) {
                weatherText = "Error: ${e.message}"
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = cityInfo.cityName,
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Temperature: $weatherText",
                style = TextStyle(fontSize = 18.sp),
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Back")
            }
        }
    }

    class LocationPermissionRequester(
        private val activity: ComponentActivity,
        private val viewModel: LocationPermissionViewModel
    ) {
        fun requestLocationPermission() {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                viewModel.onPermissionRequested()
            }
        }
    }

    @Composable
    fun SettingsPage(
        temperatureUnit: TemperatureUnit,
        onTemperatureUnitChanged: (TemperatureUnit) -> Unit
    ) {
    }

    class LocationPermissionViewModel : androidx.lifecycle.ViewModel() {
        var isPermissionRequested by mutableStateOf(false)
            private set

        fun onPermissionRequested() {
            isPermissionRequested = true
        }
    }
}
