package com.example.pogodaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pogodaapp.ui.theme.PogodaAppTheme
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PogodaAppTheme {
                WeatherScreen()
            }
        }
    }
}

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = viewModel()
) {
    val state = viewModel.state
    val weather = state.weather

    LaunchedEffect(Unit) {
        viewModel.search()
    }

    val background = when (weather?.weatherCode) {
        0, 1 -> Brush.verticalGradient(
            listOf(Color(0xFF4A90E2), Color(0xFF9DD6FF))
        )
        2, 3 -> Brush.verticalGradient(
            listOf(Color(0xFF607D8B), Color(0xFFB0BEC5))
        )
        51, 53, 55, 61, 63, 65, 80, 81, 82 -> Brush.verticalGradient(
            listOf(Color(0xFF37474F), Color(0xFF78909C))
        )
        71, 73, 75 -> Brush.verticalGradient(
            listOf(Color(0xFF78909C), Color(0xFFECEFF1))
        )
        95, 96, 99 -> Brush.verticalGradient(
            listOf(Color(0xFF263238), Color(0xFF546E7A))
        )
        else -> Brush.verticalGradient(
            listOf(Color(0xFF4A90E2), Color(0xFFB3E5FC))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Pogoda",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = state.cityText,
                onValueChange = viewModel::changeCity,
                label = { Text("Wpisz miasto") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.95f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.90f),
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = viewModel::search,
                enabled = !state.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    if (state.loading) "Pobieranie..." else "Sprawdź pogodę"
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (state.loading) {
                CircularProgressIndicator(color = Color.White)
                Spacer(modifier = Modifier.height(18.dp))
            }

            state.error?.let { error ->
                Text(
                    text = error,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(18.dp))
            }

            weather?.let {
                CurrentWeatherCard(it)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Prognoza na 5 dni",
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(it.forecast) { index, day ->
                        ForecastCard(
                            day = day,
                            isToday = index == 0
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentWeatherCard(
    weather: WeatherData
) {
    val description = weatherDescription(weather.weatherCode)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.94f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${weather.city}, ${weather.country}",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description.first,
                fontSize = 50.sp
            )

            Text(
                text = description.second,
                fontSize = 17.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${weather.temperature.toInt()}°",
                fontSize = 58.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Odczuwalna: ${weather.apparentTemperature}°C",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WeatherInfoBox(
                    title = "Wilgotność",
                    value = "${weather.humidity}%",
                    emoji = "💧",
                    modifier = Modifier.weight(1f)
                )

                WeatherInfoBox(
                    title = "Wiatr",
                    value = "${weather.windSpeed} km/h",
                    emoji = "💨",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ForecastCard(
    day: ForecastDay,
    isToday: Boolean
) {
    val description = weatherDescription(day.weatherCode)

    Card(
        modifier = Modifier.width(132.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.92f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isToday) "Dziś" else formatDay(day.date),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description.first,
                fontSize = 30.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${day.maxTemperature.toInt()}° / ${day.minTemperature.toInt()}°",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "💧 ${day.precipitationProbability}%",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun WeatherInfoBox(
    title: String,
    value: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF3F5F7)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Text(
                text = title,
                fontSize = 12.sp
            )
        }
    }
}

fun weatherDescription(
    code: Int
): Pair<String, String> {
    return when (code) {
        0 -> "☀️" to "Bezchmurnie"
        1, 2 -> "🌤️" to "Częściowe zachmurzenie"
        3 -> "☁️" to "Pochmurno"
        45, 48 -> "🌫️" to "Mgła"
        51, 53, 55 -> "🌦️" to "Mżawka"
        61, 63, 65 -> "🌧️" to "Deszcz"
        71, 73, 75 -> "🌨️" to "Śnieg"
        80, 81, 82 -> "🌧️" to "Przelotne opady"
        95, 96, 99 -> "⛈️" to "Burza"
        else -> "🌡️" to "Aktualna pogoda"
    }
}

fun formatDay(date: String): String {
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val output = SimpleDateFormat("EEE", Locale("pl", "PL"))
        val parsed = input.parse(date)

        if (parsed != null) {
            output.format(parsed).replaceFirstChar {
                if (it.isLowerCase()) {
                    it.titlecase(Locale("pl", "PL"))
                } else {
                    it.toString()
                }
            }
        } else {
            date
        }
    } catch (_: Exception) {
        date
    }
}
