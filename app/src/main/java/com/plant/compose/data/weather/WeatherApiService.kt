package com.plant.compose.data.weather

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class WeatherApiService(
    private val httpClient: HttpClient
) {
    suspend fun searchCity(
        url: String,
        name: String,
        count: Int
    ): GeocodingResponseDto {
        return httpClient
            .get(url) {
                parameter("name", name)
                parameter("count", count)
            }
            .body()
    }

    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        current: String
    ): WeatherResponseDto {
        return httpClient
            .get("https://api.open-meteo.com/v1/forecast") {
                parameter("latitude", latitude)
                parameter("longitude", longitude)
                parameter("current", current)
            }
            .body()
    }
}
