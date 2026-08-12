package com.example.weatherapp

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<WeatherCondition>,
    val wind: Wind,
    val cod: Int
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class WeatherCondition(
    val main: String,
    val description: String
)

data class Wind(
    val speed: Double
)