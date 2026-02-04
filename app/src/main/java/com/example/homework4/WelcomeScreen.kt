package com.example.homework4

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun WelcomeScreen(
    locationWeatherState: WeatherUiState,
    temperatureUnit: TemperatureUnit,
    onFetchLocationWeather: () -> Unit,
    onLocationError: (String) -> Unit,
    onToggleTemperatureUnit: () -> Unit,
    onCityClick: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    var hasRequestedPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onFetchLocationWeather()
        } else {
            onLocationError("Location permission denied")
        }
    }

    LaunchedEffect(Unit) {
        if (locationWeatherState !is WeatherUiState.Idle) return@LaunchedEffect

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            onFetchLocationWeather()
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

        when (val state = locationWeatherState) {
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
                    text = "${state.data.locationName}: ${state.data.temperature}",
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
                    onClick = onFetchLocationWeather,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(text = "Retry")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        citiesInfo.forEach { city ->
            Button(
                onClick = { onCityClick(city.cityName) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(text = "Explore ${city.cityName}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onToggleTemperatureUnit) {
            Text(
                text = "Switch to ${
                    if (temperatureUnit == TemperatureUnit.Celsius) "Fahrenheit" else "Celsius"
                }"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(onClick = onSettingsClick) {
            Text(text = "Settings")
        }
    }
}
