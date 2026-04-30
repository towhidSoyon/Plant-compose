package com.plant.compose.data.repository

import com.plant.compose.data.agriculturist.FirebaseAgriculturistDataSource
import com.plant.compose.domain.model.Agriculturist
import com.plant.compose.domain.repository.AgriculturistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class AgriculturistRepositoryImpl(
    private val dataSource: FirebaseAgriculturistDataSource
) : AgriculturistRepository {

    override fun getAgriculturists(): Flow<Result<List<Agriculturist>>> {
        return dataSource
            .getAgriculturists()
            .catch {
                emit(Result.failure(IllegalStateException("Unable to load agriculturists. Please try again.")))
            }
    }
}
