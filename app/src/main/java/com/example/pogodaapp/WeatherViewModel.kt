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

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    var state by mutableStateOf(WeatherUiState())
        private set

    fun changeCity(city: String) {
        state = state.copy(cityText = city)
    }

    fun search() {
        val city = state.cityText.trim()

        if (city.isEmpty()) {
            state = state.copy(error = "Wpisz miasto")
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
}
