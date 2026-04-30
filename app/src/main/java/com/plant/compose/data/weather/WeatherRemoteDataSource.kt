package com.plant.compose.data.weather

import com.plant.compose.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class WeatherRemoteDataSource(
    private val apiService: WeatherApiService
) {
    fun getCurrentWeather(): Flow<WeatherInfo> = flow {
        val city = apiService
            .searchCity(
                url = GEOCODING_URL,
                name = DEFAULT_CITY,
                count = 1
            )
            .results
            ?.firstOrNull()
            ?: throw IOException("No weather location found for Dhaka.")

        val latitude = city.latitude ?: throw IOException("Weather location is missing latitude.")
        val longitude = city.longitude ?: throw IOException("Weather location is missing longitude.")

        val current = apiService
            .getCurrentWeather(
                latitude = latitude,
                longitude = longitude,
                current = CURRENT_WEATHER_FIELDS
            )
            .current
            ?: throw IOException("Current weather is unavailable.")

        val weatherCode = current.weatherCode ?: throw IOException("Weather condition is unavailable.")

        emit(
            WeatherInfo(
                cityName = DEFAULT_CITY,
                temperature = current.temperature ?: throw IOException("Temperature is unavailable."),
                humidity = current.humidity ?: throw IOException("Humidity is unavailable."),
                windSpeed = current.windSpeed ?: throw IOException("Wind speed is unavailable."),
                condition = weatherCode.toWeatherCondition(),
                weatherCode = weatherCode
            )
        )
    }

    private fun Int.toWeatherCondition(): String {
        return when (this) {
            0 -> "Clear sky"
            1, 2, 3 -> "Partly cloudy"
            45, 48 -> "Fog"
            51, 53, 55 -> "Drizzle"
            61, 63, 65 -> "Rain"
            71, 73, 75 -> "Snow"
            95 -> "Thunderstorm"
            else -> "Unknown"
        }
    }

    private companion object {
        const val DEFAULT_CITY = "Dhaka"
        const val GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search"
        const val CURRENT_WEATHER_FIELDS =
            "temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m"
    }
}
