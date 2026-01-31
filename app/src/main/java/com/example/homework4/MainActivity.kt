package com.example.homework4

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.homework4.ui.theme.Homework4Theme
import java.util.Locale

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
                                CityDetailScreen(
                                    cityInfo = it,
                                    navController = navController,
                                    viewModel = weatherViewModel
                                )
                            }
                        }
                        composable("settings") {
                            SettingsScreen(
                                viewModel = weatherViewModel,
                                navController = navController
                            )
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
