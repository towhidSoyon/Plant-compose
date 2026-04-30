package com.plant.compose.domain.repository

import com.plant.compose.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather(): Flow<Result<WeatherInfo>>
}
