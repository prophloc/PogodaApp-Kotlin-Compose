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

data class SeasonalMonth(
    val date: String,
    val temperatureMean: Double?,
    val temperatureAnomaly: Double?,
    val precipitationMean: Double?,
    val precipitationAnomaly: Double?
)

data class LongRangeWeatherData(
    val city: String,
    val country: String,
    val months: List<SeasonalMonth>
)
