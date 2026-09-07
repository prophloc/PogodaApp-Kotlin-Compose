package com.example.pogodaapp

data class WeatherData(
    val city: String,
    val country: String,
    val temperature: Double,
    val apparentTemperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCode: Int
)