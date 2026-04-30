package com.plant.compose.domain.repository

import com.plant.compose.domain.model.Agriculturist
import kotlinx.coroutines.flow.Flow

interface AgriculturistRepository {
    fun getAgriculturists(): Flow<Result<List<Agriculturist>>>
}
