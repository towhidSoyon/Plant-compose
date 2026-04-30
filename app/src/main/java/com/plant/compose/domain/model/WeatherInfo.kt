package com.plant.compose.domain.model

data class WeatherInfo(
    val cityName: String,
    val temperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val condition: String,
    val weatherCode: Int
)
