package com.example.homework4

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

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
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = cityInfo.cityName,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (val state = viewModel.cityWeatherState) {
            is WeatherUiState.Idle -> {}
            is WeatherUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                Text(
                    text = "Loading weather...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            is WeatherUiState.Success -> {
                WeatherContent(data = state.data)
            }
            is WeatherUiState.Error -> {
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyMedium,
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

@Composable
private fun WeatherContent(data: WeatherDisplayData) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (data.conditionIconUrl.isNotEmpty()) {
                AsyncImage(
                    model = data.conditionIconUrl,
                    contentDescription = data.conditionText,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Text(
                text = data.conditionText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = data.temperature,
                style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = "Feels like ${data.feelsLike}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${data.locationName}, ${data.country}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )

            if (data.localtime.isNotEmpty()) {
                Text(
                    text = data.localtime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WeatherDetailCard(
            title = "Humidity",
            value = data.humidity,
            modifier = Modifier.weight(1f)
        )
        WeatherDetailCard(
            title = "Wind",
            value = data.wind,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WeatherDetailCard(
            title = "UV Index",
            value = data.uvIndex,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun WeatherDetailCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
