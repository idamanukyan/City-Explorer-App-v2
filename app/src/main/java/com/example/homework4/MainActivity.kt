package com.example.homework4

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
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
                    val weatherViewModel: WeatherViewModel = viewModel()

                    NavHost(navController = navController, startDestination = "welcome_screen") {
                        composable("welcome_screen") {
                            WelcomeScreen(
                                viewModel = weatherViewModel,
                                navController = navController,
                                getCurrentCity = { getCurrentCity() }
                            )
                        }
                        composable("second_screen/{cityName}") { backStackEntry ->
                            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
                            val cityInfo = citiesInfo.find { it.cityName == cityName }
                            cityInfo?.let {
                                SecondScreen(
                                    cityInfo = it,
                                    navController = navController,
                                    viewModel = weatherViewModel
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
}

@Composable
fun WelcomeScreen(
    viewModel: WeatherViewModel,
    navController: NavHostController,
    getCurrentCity: () -> String?
) {
    val context = LocalContext.current
    var hasRequestedPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val city = getCurrentCity()
            if (city != null) {
                viewModel.fetchLocationWeather(city)
            } else {
                viewModel.setLocationError("Could not determine your city")
            }
        } else {
            viewModel.setLocationError("Location permission denied")
        }
    }

    LaunchedEffect(Unit) {
        if (viewModel.locationWeatherState !is WeatherUiState.Idle) return@LaunchedEffect

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            val city = getCurrentCity()
            if (city != null) {
                viewModel.fetchLocationWeather(city)
            } else {
                viewModel.setLocationError("Could not determine your city")
            }
        } else if (!hasRequestedPermission) {
            hasRequestedPermission = true
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
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

        when (val state = viewModel.locationWeatherState) {
            is WeatherUiState.Idle -> {}
            is WeatherUiState.Loading -> {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Fetching location weather...",
                        style = TextStyle(fontSize = 14.sp)
                    )
                }
            }
            is WeatherUiState.Success -> {
                Text(
                    text = "Current Location: ${state.temperature}",
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(8.dp)
                )
            }
            is WeatherUiState.Error -> {
                Text(
                    text = state.message,
                    style = TextStyle(fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
                Button(
                    onClick = {
                        val city = getCurrentCity()
                        if (city != null) {
                            viewModel.fetchLocationWeather(city)
                        }
                    },
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(text = "Retry")
                }
            }
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

        Button(onClick = { viewModel.toggleTemperatureUnit() }) {
            Text(
                text = "Switch to ${
                    if (viewModel.temperatureUnit == TemperatureUnit.Celsius) "Fahrenheit" else "Celsius"
                }"
            )
        }
    }
}

@Composable
fun SecondScreen(
    cityInfo: CityInfo,
    navController: NavHostController,
    viewModel: WeatherViewModel
) {
    LaunchedEffect(cityInfo.cityName) {
        viewModel.fetchCityWeather(cityInfo.cityName)
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

        when (val state = viewModel.cityWeatherState) {
            is WeatherUiState.Idle -> {}
            is WeatherUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                Text(
                    text = "Loading weather...",
                    style = TextStyle(fontSize = 14.sp),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            is WeatherUiState.Success -> {
                Text(
                    text = "Temperature: ${state.temperature}",
                    style = TextStyle(fontSize = 18.sp),
                    modifier = Modifier.padding(8.dp)
                )
            }
            is WeatherUiState.Error -> {
                Text(
                    text = state.message,
                    style = TextStyle(fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
                Button(
                    onClick = { viewModel.fetchCityWeather(cityInfo.cityName) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = "Retry")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Back")
        }
    }
}
