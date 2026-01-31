package com.example.homework4

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun CityDetailScreen(
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
