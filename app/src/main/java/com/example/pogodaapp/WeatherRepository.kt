package com.example.pogodaapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class WeatherRepository {

    suspend fun getWeather(city: String): WeatherData {

        return withContext(Dispatchers.IO) {

            val encodedCity = URLEncoder.encode(city, "UTF-8")

            // Najpierw znajdujemy współrzędne miasta
            val geoUrl =
                "https://geocoding-api.open-meteo.com/v1/search" +
                        "?name=$encodedCity" +
                        "&count=1" +
                        "&language=pl" +
                        "&format=json"

            val geoJson = downloadJson(geoUrl)

            val results = geoJson.optJSONArray("results")
                ?: throw Exception("Nie znaleziono miasta")

            if (results.length() == 0) {
                throw Exception("Nie znaleziono miasta")
            }

            val location = results.getJSONObject(0)

            val latitude = location.getDouble("latitude")
            val longitude = location.getDouble("longitude")
            val cityName = location.getString("name")
            val country = location.optString("country", "")

            // Pobieramy pogodę dla tych współrzędnych
            val weatherUrl =
                "https://api.open-meteo.com/v1/forecast" +
                        "?latitude=$latitude" +
                        "&longitude=$longitude" +
                        "&current=" +
                        "temperature_2m," +
                        "relative_humidity_2m," +
                        "apparent_temperature," +
                        "weather_code," +
                        "wind_speed_10m" +
                        "&wind_speed_unit=kmh" +
                        "&timezone=auto"

            val weatherJson = downloadJson(weatherUrl)

            val current =
                weatherJson.getJSONObject("current")

            WeatherData(
                city = cityName,
                country = country,
                temperature =
                    current.getDouble("temperature_2m"),
                apparentTemperature =
                    current.getDouble("apparent_temperature"),
                humidity =
                    current.getInt("relative_humidity_2m"),
                windSpeed =
                    current.getDouble("wind_speed_10m"),
                weatherCode =
                    current.getInt("weather_code")
            )
        }
    }

    private fun downloadJson(
        urlString: String
    ): JSONObject {

        val connection =
            URL(urlString).openConnection()
                    as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        try {

            if (connection.responseCode !in 200..299) {
                throw Exception(
                    "Błąd serwera: ${connection.responseCode}"
                )
            }

            val response =
                connection.inputStream
                    .bufferedReader()
                    .use {
                        it.readText()
                    }

            return JSONObject(response)

        } finally {
            connection.disconnect()
        }
    }
}