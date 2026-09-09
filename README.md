# PogodaApp

Natywna aplikacja pogodowa na Androida napisana w **Kotlinie** z wykorzystaniem **Jetpack Compose**.

Aplikacja pozwala wyszukać miasto i pobrać aktualne dane pogodowe z API Open-Meteo.

## Funkcje

- wyszukiwanie pogody po nazwie miasta
- aktualna temperatura
- temperatura odczuwalna
- wilgotność
- prędkość wiatru
- opis warunków pogodowych
- dynamiczne tło zależne od pogody
- obsługa ładowania i błędów

## Technologie

- Kotlin
- Android SDK
- Jetpack Compose
- ViewModel
- Kotlin Coroutines
- REST API
- JSON
- Open-Meteo API

## Architektura

Projekt został podzielony na proste warstwy:

- `MainActivity.kt` – interfejs użytkownika w Jetpack Compose
- `WeatherViewModel.kt` – stan ekranu i logika aplikacji
- `WeatherRepository.kt` – komunikacja z API
- `WeatherData.kt` – model danych pogodowych

Przepływ danych:

```text
Jetpack Compose
      ↓
WeatherViewModel
      ↓
WeatherRepository
      ↓
Open-Meteo API
      ↓
WeatherData
      ↓
UI
```

## Screenshot



```text
screenshots/pogoda.png
```


![PogodaApp](screenshots/pogoda.png)



## Autor

Mateusz Sokołowski
