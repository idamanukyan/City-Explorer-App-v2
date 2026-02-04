package com.example.homework4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.homework4.ui.theme.Homework4Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
                    val weatherViewModel: WeatherViewModel = hiltViewModel()

                    NavHost(navController = navController, startDestination = "welcome_screen") {
                        composable("welcome_screen") {
                            WelcomeScreen(
                                viewModel = weatherViewModel,
                                navController = navController
                            )
                        }
                        composable("second_screen/{cityName}") { backStackEntry ->
                            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
                            val cityInfo = citiesInfo.find { it.cityName == cityName }
                            if (cityInfo != null) {
                                CityDetailScreen(
                                    cityInfo = cityInfo,
                                    navController = navController,
                                    viewModel = weatherViewModel
                                )
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "City not found",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Button(
                                        onClick = { navController.popBackStack() },
                                        modifier = Modifier.padding(top = 16.dp)
                                    ) {
                                        Text(text = "Go back")
                                    }
                                }
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
}
