🌍 City Explorer App

An Android app built with Kotlin + Jetpack Compose that allows users to explore cities, view descriptions and images, and fetch real-time weather data with support for Celsius/Fahrenheit toggling.

This version improves on Homework 2 by adding settings, unit switching, and enhanced location-based weather display.

🎯 Overview

The app demonstrates:

Jetpack Compose UI with navigation between screens

REST API integration with WeatherAPI
 (via Ktor Client)

Runtime location permission handling

Dynamic temperature unit switching (°C ↔ °F)

Composable-based architecture for modular UI

✨ Features

🏙️ Welcome Screen

Buttons to explore predefined cities

Displays current location weather (with unit conversion)

Requests location permissions

🌤️ Weather Integration

Fetches live weather data for both current location and selected cities

Displays temperature in Celsius or Fahrenheit

⚙️ Settings Page (planned)

Switch between Celsius and Fahrenheit globally

📷 City Detail Screen

City description text

Image of the city

Real-time temperature

🔒 Location Permission Handling

Requests runtime permissions before fetching current location weather

📂 Project Structure

MainActivity → Entry point, sets up navigation & theme

WelcomeScreen → City list, location permission, weather display

SecondScreen → City details + weather in selected unit

SettingsPage → Placeholder for future temperature unit settings

LocationPermissionRequester → Handles fine location permissions

WeatherApiService → Uses Ktor Client to fetch data from WeatherAPI

TemperatureUnit → Enum to toggle between Celsius/Fahrenheit

🛠️ Tech Stack

Language: Kotlin

UI: Jetpack Compose + Material 3

Navigation: Navigation Compose

API: Ktor Client (WeatherAPI integration)

Permissions: AndroidX Activity + ViewModel

State Management: Compose remember + mutableStateOf

🚀 Getting Started

Clone the repository:

git clone https://github.com/<your-username>/CityExplorerApp-Homework4.git


Open in Android Studio.

Add your WeatherAPI key in WeatherApiService:

val weatherApiService = WeatherApiService("your_api_key_here")


Build & run on an emulator or Android device.

📊 Example User Flow
flowchart TD
    A[Welcome Screen] -->|Select City| B[City Detail Screen]
    A -->|Location Permission Granted| C[Show Current Location Weather]
    A -->|Go to Settings| D[Settings Page]
    D -->|Change Unit °C/°F| A

🔮 Future Enhancements

Full Settings Page for global temperature unit switching

Advanced search for more cities

Multi-language support (German/English)

Weather forecast (next 3–5 days)

👩‍💻 Author

Developed by Ida Manukyan
📧 idamyan01@gmail.com
 | 🌍 GitHub
