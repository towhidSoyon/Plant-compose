package com.plant.compose.domain.repository

import com.plant.compose.domain.model.ChooseTreeInput
import com.plant.compose.domain.model.TreeRecommendation
import kotlinx.coroutines.flow.Flow

interface ChooseTreeRepository {
    fun getTreeRecommendations(input: ChooseTreeInput): Flow<Result<List<TreeRecommendation>>>
}
