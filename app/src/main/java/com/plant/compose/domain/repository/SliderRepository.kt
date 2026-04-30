package com.plant.compose.domain.repository

import com.plant.compose.domain.model.SliderImage
import kotlinx.coroutines.flow.Flow

interface SliderRepository {
    fun getSliderImages(): Flow<Result<List<SliderImage>>>
}
