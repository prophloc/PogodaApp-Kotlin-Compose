package com.example.pogodaapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class WeatherRepository {

    private data class LocationData(
        val latitude: Double,
        val longitude: Double,
        val city: String,
        val country: String
    )

    suspend fun getWeather(city: String): WeatherData {
        return withContext(Dispatchers.IO) {
            val location = getLocation(city)

            val weatherUrl =
                "https://api.open-meteo.com/v1/forecast" +
                    "?latitude=${location.latitude}" +
                    "&longitude=${location.longitude}" +
                    "&current=" +
                    "temperature_2m," +
                    "relative_humidity_2m," +
                    "apparent_temperature," +
                    "weather_code," +
                    "wind_speed_10m" +
                    "&daily=" +
                    "weather_code," +
                    "temperature_2m_max," +
                    "temperature_2m_min," +
                    "precipitation_probability_max" +
                    "&forecast_days=5" +
                    "&wind_speed_unit=kmh" +
                    "&timezone=auto"

            val weatherJson = downloadJson(weatherUrl)
            val current = weatherJson.getJSONObject("current")
            val daily = weatherJson.getJSONObject("daily")

            val dates = daily.getJSONArray("time")
            val maxTemperatures = daily.getJSONArray("temperature_2m_max")
            val minTemperatures = daily.getJSONArray("temperature_2m_min")
            val weatherCodes = daily.getJSONArray("weather_code")
            val precipitation = daily.getJSONArray("precipitation_probability_max")

            val forecast = buildList {
                for (i in 0 until dates.length()) {
                    add(
                        ForecastDay(
                            date = dates.getString(i),
                            maxTemperature = maxTemperatures.getDouble(i),
                            minTemperature = minTemperatures.getDouble(i),
                            weatherCode = weatherCodes.getInt(i),
                            precipitationProbability = precipitation.optInt(i, 0)
                        )
                    )
                }
            }

            WeatherData(
                city = location.city,
                country = location.country,
                temperature = current.getDouble("temperature_2m"),
                apparentTemperature = current.getDouble("apparent_temperature"),
                humidity = current.getInt("relative_humidity_2m"),
                windSpeed = current.getDouble("wind_speed_10m"),
                weatherCode = current.getInt("weather_code"),
                forecast = forecast
            )
        }
    }

    suspend fun getLongRangeWeather(city: String): LongRangeWeatherData {
        return withContext(Dispatchers.IO) {
            val location = getLocation(city)

            val seasonalUrl =
                "https://seasonal-api.open-meteo.com/v1/seasonal" +
                    "?latitude=${location.latitude}" +
                    "&longitude=${location.longitude}" +
                    "&monthly=" +
                    "temperature_2m_mean," +
                    "temperature_2m_anomaly," +
                    "precipitation_mean," +
                    "precipitation_anomaly" +
                    "&models=ecmwf_seas5_ensemble_mean" +
                    "&forecast_days=217" +
                    "&timezone=auto"

            val seasonalJson = downloadJson(seasonalUrl)
            val monthly = seasonalJson.optJSONObject("monthly")
                ?: throw Exception("Brak danych długoterminowych")

            val dates = monthly.optJSONArray("time")
                ?: throw Exception("Brak miesięcy w prognozie")

            val temperatureMean = monthly.optJSONArray("temperature_2m_mean")
            val temperatureAnomaly = monthly.optJSONArray("temperature_2m_anomaly")
            val precipitationMean = monthly.optJSONArray("precipitation_mean")
            val precipitationAnomaly = monthly.optJSONArray("precipitation_anomaly")

            val months = buildList {
                for (i in 0 until dates.length()) {
                    add(
                        SeasonalMonth(
                            date = dates.optString(i),
                            temperatureMean = valueOrNull(temperatureMean, i),
                            temperatureAnomaly = valueOrNull(temperatureAnomaly, i),
                            precipitationMean = valueOrNull(precipitationMean, i),
                            precipitationAnomaly = valueOrNull(precipitationAnomaly, i)
                        )
                    )
                }
            }

            if (months.isEmpty()) {
                throw Exception("Brak danych długoterminowych")
            }

            LongRangeWeatherData(
                city = location.city,
                country = location.country,
                months = months
            )
        }
    }

    private fun getLocation(city: String): LocationData {
        val encodedCity = URLEncoder.encode(city, "UTF-8")

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

        return LocationData(
            latitude = location.getDouble("latitude"),
            longitude = location.getDouble("longitude"),
            city = location.getString("name"),
            country = location.optString("country", "")
        )
    }

    private fun valueOrNull(
        array: JSONArray?,
        index: Int
    ): Double? {
        if (array == null || index >= array.length() || array.isNull(index)) {
            return null
        }

        val value = array.optDouble(index, Double.NaN)

        return if (value.isNaN()) {
            null
        } else {
            value
        }
    }

    private fun downloadJson(urlString: String): JSONObject {
        val connection =
            URL(urlString).openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 12_000
        connection.readTimeout = 12_000

        try {
            if (connection.responseCode !in 200..299) {
                throw Exception(
                    "Błąd serwera: ${connection.responseCode}"
                )
            }

            val response =
                connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

            return JSONObject(response)
        } finally {
            connection.disconnect()
        }
    }
}
