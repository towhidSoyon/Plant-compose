package com.plant.compose.data.repository

import com.plant.compose.data.weather.WeatherRemoteDataSource
import com.plant.compose.domain.model.WeatherInfo
import com.plant.compose.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource
) : WeatherRepository {

    override fun getCurrentWeather(): Flow<Result<WeatherInfo>> {
        return remoteDataSource
            .getCurrentWeather()
            .map { Result.success(it) }
            .catch { error ->
                emit(Result.failure(IllegalStateException(error.toWeatherMessage(), error)))
            }
    }

    private fun Throwable.toWeatherMessage(): String {
        return when (this) {
            is IOException -> message ?: "Unable to load Dhaka weather. Please try again."
            else -> "Unable to load Dhaka weather. Please try again."
        }
    }
}
