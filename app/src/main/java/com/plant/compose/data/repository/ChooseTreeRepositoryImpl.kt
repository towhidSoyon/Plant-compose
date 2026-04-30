package com.plant.compose.data.repository

import com.plant.compose.data.choosetree.FirebaseChooseTreeDataSource
import com.plant.compose.domain.model.ChooseTreeInput
import com.plant.compose.domain.model.TreeRecommendation
import com.plant.compose.domain.repository.ChooseTreeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class ChooseTreeRepositoryImpl(
    private val dataSource: FirebaseChooseTreeDataSource
) : ChooseTreeRepository {

    override fun getTreeRecommendations(input: ChooseTreeInput): Flow<Result<List<TreeRecommendation>>> {
        return dataSource
            .getTreeRecommendations(input)
            .catch {
                emit(Result.failure(IllegalStateException("Unable to load tree recommendations. Please try again.")))
            }
    }
}
