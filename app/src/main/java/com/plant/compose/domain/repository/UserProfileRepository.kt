package com.plant.compose.domain.repository

import com.plant.compose.domain.model.UpdateUserProfileRequest
import com.plant.compose.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun getCurrentUserProfile(): Flow<Result<UserProfile>>
    fun updateCurrentUserProfile(request: UpdateUserProfileRequest): Flow<Result<Unit>>
    fun logout(): Result<Unit>
}
