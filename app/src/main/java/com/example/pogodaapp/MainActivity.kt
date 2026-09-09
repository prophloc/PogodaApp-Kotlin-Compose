package com.example.pogodaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pogodaapp.ui.theme.PogodaAppTheme
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

private enum class WeatherTab {
    CURRENT,
    LONG_RANGE
}

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
    val longRangeState = viewModel.longRangeState

    var selectedTab by rememberSaveable {
        mutableStateOf(WeatherTab.CURRENT)
    }

    val focusManager = LocalFocusManager.current
    val keyboardController =
        LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.search()
    }

    val background =
        if (selectedTab == WeatherTab.LONG_RANGE) {
            Brush.verticalGradient(
                listOf(
                    Color(0xFF243B55),
                    Color(0xFF6387A6)
                )
            )
        } else {
            currentBackground(state.weather?.weatherCode)
        }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected =
                        selectedTab == WeatherTab.CURRENT,
                    onClick = {
                        selectedTab = WeatherTab.CURRENT
                        dismissKeyboard(
                            focusManager = focusManager,
                            hideKeyboard = {
                                keyboardController?.hide()
                            }
                        )
                    },
                    icon = {
                        Text(
                            text = "☀️",
                            fontSize = 20.sp
                        )
                    },
                    label = {
                        Text("Pogoda")
                    }
                )

                NavigationBarItem(
                    selected =
                        selectedTab == WeatherTab.LONG_RANGE,
                    onClick = {
                        selectedTab = WeatherTab.LONG_RANGE
                        dismissKeyboard(
                            focusManager = focusManager,
                            hideKeyboard = {
                                keyboardController?.hide()
                            }
                        )
                        viewModel.searchLongRange()
                    },
                    icon = {
                        Text(
                            text = "📅",
                            fontSize = 20.sp
                        )
                    },
                    label = {
                        Text("Długoterminowa")
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(background)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    )
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 22.dp,
                        end = 22.dp,
                        top = 18.dp,
                        bottom = 12.dp
                    ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (
                        selectedTab == WeatherTab.CURRENT
                    ) {
                        "Pogoda"
                    } else {
                        "Prognoza długoterminowa"
                    },
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                OutlinedTextField(
                    value = state.cityText,
                    onValueChange = viewModel::changeCity,
                    placeholder = {
                        Text(
                            text = "Wpisz miasto",
                            color = Color.Gray
                        )
                    },
                    leadingIcon = {
                        Text("📍")
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors =
                        OutlinedTextFieldDefaults.colors(
                            focusedContainerColor =
                                Color.White.copy(
                                    alpha = 0.96f
                                ),
                            unfocusedContainerColor =
                                Color.White.copy(
                                    alpha = 0.92f
                                ),
                            focusedBorderColor =
                                Color.White,
                            unfocusedBorderColor =
                                Color.White.copy(
                                    alpha = 0.75f
                                ),
                            focusedTextColor =
                                Color.Black,
                            unfocusedTextColor =
                                Color.Black,
                            cursorColor =
                                Color(0xFF355C7D)
                        )
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()

                        if (
                            selectedTab ==
                            WeatherTab.CURRENT
                        ) {
                            viewModel.search()
                        } else {
                            viewModel.searchLongRange()
                        }
                    },
                    enabled =
                        if (
                            selectedTab ==
                            WeatherTab.CURRENT
                        ) {
                            !state.loading
                        } else {
                            !longRangeState.loading
                        },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text =
                            if (
                                selectedTab ==
                                WeatherTab.CURRENT
                            ) {
                                if (state.loading) {
                                    "Pobieranie..."
                                } else {
                                    "Sprawdź pogodę"
                                }
                            } else {
                                if (
                                    longRangeState.loading
                                ) {
                                    "Pobieranie..."
                                } else {
                                    "Sprawdź długoterminową"
                                }
                            }
                    )
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                if (
                    selectedTab ==
                    WeatherTab.CURRENT
                ) {
                    CurrentTabContent(
                        state = state,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LongRangeTabContent(
                        state = longRangeState,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentTabContent(
    state: WeatherUiState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {
        if (state.loading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                }
            }
        }

        state.error?.let { error ->
            item {
                Text(
                    text = error,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        state.weather?.let { weather ->
            item {
                CurrentWeatherCard(weather)
            }

            item {
                Text(
                    text = "Prognoza na 5 dni",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(
                        weather.forecast
                    ) { index, day ->
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
private fun LongRangeTabContent(
    state: LongRangeUiState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        if (state.loading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                }
            }
        }

        state.error?.let { error ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            Color.White.copy(
                                alpha = 0.94f
                            )
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(18.dp),
                        color = Color(0xFFB00020),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        state.weather?.let { weather ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            Color.White.copy(
                                alpha = 0.94f
                            )
                    )
                ) {
                    Column(
                        modifier =
                            Modifier.padding(18.dp)
                    ) {
                        Text(
                            text =
                                "${weather.city}, ${weather.country}",
                            fontSize = 21.sp,
                            fontWeight =
                                FontWeight.Bold
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Prognoza sezonowa pokazuje trend dla kolejnych miesięcy, a nie dokładną pogodę na konkretny dzień.",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            items(weather.months) { month ->
                SeasonalMonthCard(month)
            }
        }
    }
}

@Composable
private fun SeasonalMonthCard(
    month: SeasonalMonth
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                Color.White.copy(alpha = 0.94f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = formatMonth(month.date),
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            month.temperatureMean?.let {
                Text(
                    text =
                        "🌡️ Średnia modelowa: ${formatOneDecimal(it)}°C",
                    fontSize = 15.sp
                )
            }

            month.temperatureAnomaly?.let {
                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        temperatureTrendText(it),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            month.precipitationAnomaly?.let {
                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        precipitationTrendText(it),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            month.precipitationMean?.let {
                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        "💧 Modelowa wartość opadów: ${formatOneDecimal(it)} mm",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun CurrentWeatherCard(
    weather: WeatherData
) {
    val description =
        weatherDescription(weather.weatherCode)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                Color.White.copy(alpha = 0.94f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Text(
                text =
                    "${weather.city}, ${weather.country}",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = description.first,
                fontSize = 50.sp
            )

            Text(
                text = description.second,
                fontSize = 17.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    "${weather.temperature.toInt()}°",
                fontSize = 58.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text =
                    "Odczuwalna: ${weather.apparentTemperature}°C",
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                WeatherInfoBox(
                    title = "Wilgotność",
                    value =
                        "${weather.humidity}%",
                    emoji = "💧",
                    modifier =
                        Modifier.weight(1f)
                )

                WeatherInfoBox(
                    title = "Wiatr",
                    value =
                        "${weather.windSpeed} km/h",
                    emoji = "💨",
                    modifier =
                        Modifier.weight(1f)
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
    val description =
        weatherDescription(day.weatherCode)

    Card(
        modifier = Modifier.width(132.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                Color.White.copy(alpha = 0.92f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Text(
                text =
                    if (isToday) {
                        "Dziś"
                    } else {
                        formatDay(day.date)
                    },
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = description.first,
                fontSize = 30.sp
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text =
                    "${day.maxTemperature.toInt()}° / ${day.minTemperature.toInt()}°",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text =
                    "💧 ${day.precipitationProbability}%",
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
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

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

private fun currentBackground(
    weatherCode: Int?
): Brush {
    return when (weatherCode) {
        0, 1 -> Brush.verticalGradient(
            listOf(
                Color(0xFF4A90E2),
                Color(0xFF9DD6FF)
            )
        )

        2, 3 -> Brush.verticalGradient(
            listOf(
                Color(0xFF607D8B),
                Color(0xFFB0BEC5)
            )
        )

        51, 53, 55,
        61, 63, 65,
        80, 81, 82 ->
            Brush.verticalGradient(
                listOf(
                    Color(0xFF37474F),
                    Color(0xFF78909C)
                )
            )

        71, 73, 75 ->
            Brush.verticalGradient(
                listOf(
                    Color(0xFF78909C),
                    Color(0xFFECEFF1)
                )
            )

        95, 96, 99 ->
            Brush.verticalGradient(
                listOf(
                    Color(0xFF263238),
                    Color(0xFF546E7A)
                )
            )

        else -> Brush.verticalGradient(
            listOf(
                Color(0xFF4A90E2),
                Color(0xFFB3E5FC)
            )
        )
    }
}

fun weatherDescription(
    code: Int
): Pair<String, String> {
    return when (code) {
        0 -> "☀️" to "Bezchmurnie"
        1, 2 ->
            "🌤️" to
                "Częściowe zachmurzenie"
        3 -> "☁️" to "Pochmurno"
        45, 48 -> "🌫️" to "Mgła"
        51, 53, 55 ->
            "🌦️" to "Mżawka"
        61, 63, 65 ->
            "🌧️" to "Deszcz"
        71, 73, 75 ->
            "🌨️" to "Śnieg"
        80, 81, 82 ->
            "🌧️" to "Przelotne opady"
        95, 96, 99 ->
            "⛈️" to "Burza"
        else ->
            "🌡️" to "Aktualna pogoda"
    }
}

fun formatDay(
    date: String
): String {
    return try {
        val input =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            )

        val output =
            SimpleDateFormat(
                "EEE",
                Locale("pl", "PL")
            )

        val parsed = input.parse(date)

        if (parsed != null) {
            output.format(parsed)
                .replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(
                            Locale("pl", "PL")
                        )
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

fun formatMonth(
    date: String
): String {
    val formats = listOf(
        "yyyy-MM-dd",
        "yyyy-MM"
    )

    for (format in formats) {
        try {
            val input =
                SimpleDateFormat(
                    format,
                    Locale.US
                )

            val parsed = input.parse(date)

            if (parsed != null) {
                val output =
                    SimpleDateFormat(
                        "LLLL yyyy",
                        Locale("pl", "PL")
                    )

                return output
                    .format(parsed)
                    .replaceFirstChar {
                        if (it.isLowerCase()) {
                            it.titlecase(
                                Locale("pl", "PL")
                            )
                        } else {
                            it.toString()
                        }
                    }
            }
        } catch (_: Exception) {
        }
    }

    return date
}

private fun formatOneDecimal(
    value: Double
): String {
    return String.format(
        Locale("pl", "PL"),
        "%.1f",
        value
    )
}

private fun temperatureTrendText(
    anomaly: Double
): String {
    val value = formatOneDecimal(abs(anomaly))

    return when {
        anomaly >= 0.5 ->
            "🔥 Około $value°C cieplej niż zwykle"

        anomaly <= -0.5 ->
            "❄️ Około $value°C chłodniej niż zwykle"

        else ->
            "🌡️ Temperatura zbliżona do normy"
    }
}

private fun precipitationTrendText(
    anomaly: Double
): String {
    val value = formatOneDecimal(abs(anomaly))

    return when {
        anomaly > 1.0 ->
            "🌧️ Bardziej mokro niż zwykle (+$value mm)"

        anomaly < -1.0 ->
            "☀️ Bardziej sucho niż zwykle (-$value mm)"

        else ->
            "💧 Opady zbliżone do normy"
    }
}

private fun dismissKeyboard(
    focusManager: FocusManager,
    hideKeyboard: () -> Unit
) {
    focusManager.clearFocus()
    hideKeyboard()
}
