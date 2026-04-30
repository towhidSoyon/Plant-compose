package com.plant.compose.data.repository

import com.plant.compose.data.suggestedtree.FirebaseSuggestedTreeDataSource
import com.plant.compose.domain.model.SuggestedTree
import com.plant.compose.domain.repository.SuggestedTreeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class SuggestedTreeRepositoryImpl(
    private val dataSource: FirebaseSuggestedTreeDataSource
) : SuggestedTreeRepository {

    override fun getSuggestedTrees(): Flow<Result<List<SuggestedTree>>> {
        return dataSource
            .getSuggestedTrees()
            .catch {
                emit(Result.failure(IllegalStateException("Unable to load suggested trees. Please try again.")))
            }
    }
}
