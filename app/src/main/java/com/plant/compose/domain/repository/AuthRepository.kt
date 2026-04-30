package com.plant.compose.domain.repository

import com.plant.compose.domain.model.SignupRequest
import com.plant.compose.domain.model.User
import com.plant.compose.domain.util.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signup(request: SignupRequest): Flow<Result<Unit>>
    fun login(email: String, password: String): Flow<Result<Unit>>
    fun logout(): Result<Unit>
    suspend fun getCurrentUser(): AuthResult<User?>
}
