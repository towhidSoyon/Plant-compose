package com.plant.compose.domain.repository

import com.plant.compose.domain.model.SuggestedTree
import kotlinx.coroutines.flow.Flow

interface SuggestedTreeRepository {
    fun getSuggestedTrees(): Flow<Result<List<SuggestedTree>>>
}
