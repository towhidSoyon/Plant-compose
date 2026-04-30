package com.plant.compose.data.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponseDto(
    @SerialName("results") val results: List<GeocodingResultDto>? = null
)

@Serializable
data class GeocodingResultDto(
    @SerialName("name") val name: String? = null,
    @SerialName("latitude") val latitude: Double? = null,
    @SerialName("longitude") val longitude: Double? = null
)

@Serializable
data class WeatherResponseDto(
    @SerialName("current") val current: CurrentWeatherDto? = null
)

@Serializable
data class CurrentWeatherDto(
    @SerialName("temperature_2m") val temperature: Double? = null,
    @SerialName("relative_humidity_2m") val humidity: Int? = null,
    @SerialName("weather_code") val weatherCode: Int? = null,
    @SerialName("wind_speed_10m") val windSpeed: Double? = null
)
