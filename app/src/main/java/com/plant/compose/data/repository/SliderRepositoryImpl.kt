package com.plant.compose.data.repository

import com.plant.compose.data.slider.FirebaseSliderDataSource
import com.plant.compose.domain.model.SliderImage
import com.plant.compose.domain.repository.SliderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class SliderRepositoryImpl(
    private val dataSource: FirebaseSliderDataSource
) : SliderRepository {

    override fun getSliderImages(): Flow<Result<List<SliderImage>>> {
        return dataSource
            .getSliderImages()
            .catch {
                emit(Result.failure(IllegalStateException("Unable to load slider images. Please try again.")))
            }
    }
}
