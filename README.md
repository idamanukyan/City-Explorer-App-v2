# City Explorer App

An Android app built with Kotlin and Jetpack Compose that lets users explore cities and view real-time weather data with support for Celsius/Fahrenheit toggling, offline caching, and device location detection.

## Overview

The app demonstrates:

- Jetpack Compose UI with multi-screen navigation
- MVVM architecture with Hilt dependency injection
- REST API integration with [WeatherAPI](https://www.weatherapi.com/) via Ktor Client
- Room database caching with 30-minute TTL and offline fallback
- DataStore-backed user preferences
- Runtime location permission handling via FusedLocationProviderClient
- Dynamic temperature unit switching (C / F)
- Material 3 design with dynamic color support (Android 12+)

## Features

### Welcome Screen
- Buttons to explore predefined cities (Berlin, Paris, London, Tokyo, New York)
- Displays current location weather with unit conversion
- Requests location permissions at runtime
- Toggle temperature unit inline or via Settings

### City Detail Screen
- Weather icon, temperature, and "feels like" display
- Humidity, wind speed/direction, and UV index cards
- Material 3 ElevatedCard layout with scroll support

### Settings Screen
- Radio button selector for Celsius / Fahrenheit
- Preference persisted via DataStore

### Weather Integration
- Fetches live weather data from WeatherAPI for location and selected cities
- 30-minute Room cache with automatic expiry (injectable Clock for testability)
- Falls back to expired cache on network failure
- Case-insensitive cache keys prevent duplicate entries
- HTTP status code validation with user-friendly error messages (invalid key, rate limit, no internet)

### Location
- FusedLocationProviderClient with `lastLocation` + `getCurrentLocation` fallback
- Async Geocoder on API 33+, sync fallback for older devices
- Graceful handling of permission denial and location unavailability

## Architecture

```
Presentation          Domain              Data
+-----------+    +----------------+    +------------------+
| Compose   |--->| WeatherViewModel|--->| WeatherRepository|
| Screens   |    |                |    |   (interface)    |
| (hoisted  |    | LocationRepo   |    +--------+---------+
|  state)   |    | TempFormatter  |             |
+-----------+    | PreferencesRepo|    +--------+---------+
                 +----------------+    | WeatherRepoImpl  |
                                       |   +-- ApiService  |
                                       |   +-- WeatherDao  |
                                       |   +-- Clock       |
                                       +------------------+
```

- **State hoisting**: Composables receive state values and lambda callbacks, not ViewModels
- **Repository pattern**: Single source of truth with cache-first strategy
- **Dependency injection**: Hilt modules for network, database, repository, and clock

## Project Structure

```
app/src/main/java/com/example/homework4/
├── CityExplorerApp.kt            # @HiltAndroidApp entry point
├── MainActivity.kt               # Navigation host, ViewModel wiring
├── WeatherViewModel.kt           # UI state management, error mapping
├── WelcomeScreen.kt              # Home screen (hoisted state)
├── CityDetailScreen.kt           # Weather detail (hoisted state)
├── SettingsScreen.kt             # Preferences (hoisted state)
├── WeatherApiService.kt          # Ktor HTTP client with URL-safe params
├── WeatherRepository.kt          # Cache-first repository with Clock
├── LocationRepository.kt         # FusedLocationProviderClient + Geocoder
├── WeatherResponse.kt            # API DTOs + error types
├── WeatherDisplayData.kt         # Formatted UI model
├── WeatherUiState.kt             # Sealed class: Idle/Loading/Success/Error
├── CityInfo.kt                   # City list data
├── TemperatureUnit.kt            # Celsius/Fahrenheit enum
├── TemperatureFormatter.kt       # Response -> display data conversion
├── data/
│   ├── AppDatabase.kt            # Room database (v2)
│   ├── WeatherEntity.kt          # Cache entity
│   ├── WeatherDao.kt             # Room DAO
│   └── PreferencesRepository.kt  # DataStore preferences
├── di/
│   ├── NetworkModule.kt          # HttpClient with timeouts, Json, API key
│   ├── DatabaseModule.kt         # Room database provider
│   ├── RepositoryModule.kt       # Repository binding
│   ├── ClockModule.kt            # Injectable Clock
│   └── ApiKey.kt                 # @ApiKey qualifier
└── ui/theme/
    ├── Color.kt
    ├── Theme.kt                  # CityExplorerTheme with dynamic colors
    └── Type.kt
```

## Tech Stack

| Category | Library | Version |
|----------|---------|---------|
| Language | Kotlin | 2.1.0 |
| UI | Jetpack Compose + Material 3 | BOM 2024.12.01 |
| Navigation | Navigation Compose | 2.8.5 |
| DI | Hilt | 2.53.1 |
| HTTP | Ktor Client (OkHttp engine) | 3.0.3 |
| Serialization | kotlinx-serialization | 1.7.3 |
| Database | Room | 2.6.1 |
| Preferences | DataStore | 1.1.1 |
| Location | Play Services Location | 21.3.0 |
| Images | Coil Compose | 2.7.0 |
| Testing | JUnit 4 + MockK + Coroutines Test | - |

## Getting Started

1. Clone the repository:
   ```
   git clone https://github.com/idamanukyan/City-Explorer-App-v2.git
   ```

2. Create `local.properties` in the project root and add your WeatherAPI key:
   ```
   WEATHER_API_KEY=your_api_key_here
   ```

3. Open in Android Studio, sync Gradle, and run on an emulator or device (min SDK 28).

## Testing

The project includes unit tests covering:

- **WeatherRepositoryTest** -- cache hit/miss/expiry, API fallback, key normalization, clock-based TTL
- **WeatherViewModelTest** -- state transitions, error message mapping (401, 429, IOException), location flow
- **TemperatureFormatterTest** -- unit conversion, icon URL handling, field mapping, day/night

Run tests with:
```
./gradlew test
```

## Future Enhancements

- Search for any city (not just the predefined list)
- Weather forecast (3-5 day outlook)
- User-managed favorites list
- Multi-language support
- Pull-to-refresh on detail screen

## Author

Developed by Ida Manukyan
