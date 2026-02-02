package com.example.homework4

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.navigation.NavHostController

@Composable
fun WelcomeScreen(
    viewModel: WeatherViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    var hasRequestedPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.fetchLocationWeatherFromDevice()
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
            viewModel.fetchLocationWeatherFromDevice()
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
                    onClick = { viewModel.fetchLocationWeatherFromDevice() },
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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(onClick = { navController.navigate("settings") }) {
            Text(text = "Settings")
        }
    }
}
