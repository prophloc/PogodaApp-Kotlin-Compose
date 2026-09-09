package com.example.pogodaapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class WeatherUiState(
    val cityText: String = "Toruń",
    val weather: WeatherData? = null,
    val loading: Boolean = false,
    val error: String? = null
)

data class LongRangeUiState(
    val weather: LongRangeWeatherData? = null,
    val loading: Boolean = false,
    val error: String? = null
)

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    var state by mutableStateOf(WeatherUiState())
        private set

    var longRangeState by mutableStateOf(LongRangeUiState())
        private set

    private var longRangeCity: String? = null

    fun changeCity(city: String) {
        state = state.copy(
            cityText = city,
            error = null
        )

        if (city.trim() != longRangeCity) {
            longRangeState = LongRangeUiState()
        }
    }

    fun search() {
        val city = state.cityText.trim()

        if (city.isEmpty()) {
            state = state.copy(
                error = "Wpisz miasto"
            )
            return
        }

        viewModelScope.launch {
            state = state.copy(
                loading = true,
                error = null
            )

            try {
                val weather = repository.getWeather(city)

                state = state.copy(
                    weather = weather,
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                state = state.copy(
                    weather = null,
                    loading = false,
                    error = e.message ?: "Wystąpił błąd"
                )
            }
        }
    }

    fun searchLongRange() {
        val city = state.cityText.trim()

        if (city.isEmpty()) {
            longRangeState = LongRangeUiState(
                error = "Wpisz miasto"
            )
            return
        }

        if (
            longRangeCity == city &&
            longRangeState.weather != null
        ) {
            return
        }

        viewModelScope.launch {
            longRangeState = LongRangeUiState(
                loading = true
            )

            try {
                val weather =
                    repository.getLongRangeWeather(city)

                longRangeCity = city

                longRangeState = LongRangeUiState(
                    weather = weather
                )
            } catch (e: Exception) {
                longRangeState = LongRangeUiState(
                    error = e.message
                        ?: "Nie udało się pobrać prognozy długoterminowej"
                )
            }
        }
    }
}
