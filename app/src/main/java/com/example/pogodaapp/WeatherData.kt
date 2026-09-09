package com.example.pogodaapp

data class ForecastDay(
    val date: String,
    val maxTemperature: Double,
    val minTemperature: Double,
    val weatherCode: Int,
    val precipitationProbability: Int
)

data class WeatherData(
    val city: String,
    val country: String,
    val temperature: Double,
    val apparentTemperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCode: Int,
    val forecast: List<ForecastDay>
)
