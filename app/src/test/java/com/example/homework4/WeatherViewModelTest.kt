package com.example.homework4

import com.example.homework4.data.PreferencesRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val mockRepository = mockk<WeatherRepository>()
    private val mockLocationRepository = mockk<LocationRepository>()
    private val temperatureFormatter = TemperatureFormatter()
    private val mockPreferencesRepository = mockk<PreferencesRepository>()

    private lateinit var viewModel: WeatherViewModel

    private val sampleResponse = WeatherResponse(
        location = Location(name = "Berlin", country = "Germany"),
        current = Current(temp_c = 15.0f, temp_f = 59.0f)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { mockPreferencesRepository.temperatureUnitFlow } returns flowOf(TemperatureUnit.Celsius)
        viewModel = WeatherViewModel(
            mockRepository,
            mockLocationRepository,
            temperatureFormatter,
            mockPreferencesRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isIdle() {
        assertEquals(WeatherUiState.Idle, viewModel.locationWeatherState)
        assertEquals(WeatherUiState.Idle, viewModel.cityWeatherState)
    }

    @Test
    fun fetchCityWeather_success_setsSuccessState() = runTest {
        coEvery { mockRepository.getWeatherForCity("Berlin") } returns sampleResponse

        viewModel.fetchCityWeather("Berlin")

        val state = viewModel.cityWeatherState
        assertTrue(state is WeatherUiState.Success)
        assertEquals("Berlin", (state as WeatherUiState.Success).data.locationName)
        assertEquals("15.0\u00B0C", state.data.temperature)
    }

    @Test
    fun fetchCityWeather_ioException_showsNoInternetMessage() = runTest {
        coEvery { mockRepository.getWeatherForCity("Berlin") } throws IOException("timeout")

        viewModel.fetchCityWeather("Berlin")

        val state = viewModel.cityWeatherState
        assertTrue(state is WeatherUiState.Error)
        assertEquals("No internet connection", (state as WeatherUiState.Error).message)
    }

    @Test
    fun fetchCityWeather_apiException401_showsInvalidKeyMessage() = runTest {
        coEvery { mockRepository.getWeatherForCity("Berlin") } throws WeatherApiException(401, "Unauthorized")

        viewModel.fetchCityWeather("Berlin")

        val state = viewModel.cityWeatherState
        assertTrue(state is WeatherUiState.Error)
        assertEquals("Invalid API key", (state as WeatherUiState.Error).message)
    }

    @Test
    fun fetchCityWeather_apiException429_showsRateLimitMessage() = runTest {
        coEvery { mockRepository.getWeatherForCity("Berlin") } throws WeatherApiException(429, "Rate limited")

        viewModel.fetchCityWeather("Berlin")

        val state = viewModel.cityWeatherState
        assertTrue(state is WeatherUiState.Error)
        assertEquals("Too many requests \u2014 please try again later", (state as WeatherUiState.Error).message)
    }

    @Test
    fun fetchCityWeather_genericException_showsFallbackMessage() = runTest {
        coEvery { mockRepository.getWeatherForCity("Berlin") } throws RuntimeException("unexpected")

        viewModel.fetchCityWeather("Berlin")

        val state = viewModel.cityWeatherState
        assertTrue(state is WeatherUiState.Error)
        assertEquals("Failed to fetch weather", (state as WeatherUiState.Error).message)
    }

    @Test
    fun fetchLocationWeatherFromDevice_noCity_setsError() = runTest {
        coEvery { mockLocationRepository.getCurrentCity() } returns null

        viewModel.fetchLocationWeatherFromDevice()

        val state = viewModel.locationWeatherState
        assertTrue(state is WeatherUiState.Error)
        assertEquals("Could not determine your city", (state as WeatherUiState.Error).message)
    }

    @Test
    fun fetchLocationWeatherFromDevice_success_setsSuccessState() = runTest {
        coEvery { mockLocationRepository.getCurrentCity() } returns "Berlin"
        coEvery { mockRepository.getWeatherForCity("Berlin") } returns sampleResponse

        viewModel.fetchLocationWeatherFromDevice()

        val state = viewModel.locationWeatherState
        assertTrue(state is WeatherUiState.Success)
        assertEquals("Berlin", (state as WeatherUiState.Success).data.locationName)
    }

    @Test
    fun setLocationError_setsErrorState() {
        viewModel.setLocationError("Permission denied")

        val state = viewModel.locationWeatherState
        assertTrue(state is WeatherUiState.Error)
        assertEquals("Permission denied", (state as WeatherUiState.Error).message)
    }

    @Test
    fun initialTemperatureUnit_isCelsius() {
        assertEquals(TemperatureUnit.Celsius, viewModel.temperatureUnit)
    }
}
