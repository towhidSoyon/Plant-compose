package com.plant.compose.data.repository

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.plant.compose.data.profile.FirebaseUserProfileDataSource
import com.plant.compose.domain.model.UpdateUserProfileRequest
import com.plant.compose.domain.model.UserProfile
import com.plant.compose.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserProfileRepositoryImpl(
    private val dataSource: FirebaseUserProfileDataSource
) : UserProfileRepository {

    override fun getCurrentUserProfile(): Flow<Result<UserProfile>> {
        return dataSource
            .getCurrentUserProfile()
            .map { Result.success(it) }
            .catch { emit(Result.failure(IllegalStateException(it.toProfileMessage(), it))) }
    }

    override fun updateCurrentUserProfile(request: UpdateUserProfileRequest): Flow<Result<Unit>> {
        return dataSource
            .updateCurrentUserProfile(request)
            .map { Result.success(Unit) }
            .catch { emit(Result.failure(IllegalStateException(it.toProfileMessage(), it))) }
    }

    override fun logout(): Result<Unit> {
        return runCatching { dataSource.logout() }
            .recoverCatching { throw IllegalStateException(it.toProfileMessage(), it) }
    }

    private fun Throwable.toProfileMessage(): String {
        return when (this) {
            is FirebaseNetworkException -> "Network error. Please check your connection."
            is FirebaseAuthException -> "Your session expired. Please log in again."
            is FirebaseFirestoreException -> when (code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> "You do not have permission to update this profile."
                FirebaseFirestoreException.Code.UNAVAILABLE -> "Profile service is unavailable. Please try again."
                else -> "Unable to load profile. Please try again."
            }
            is FirebaseException -> "Unable to update profile. Please try again."
            else -> message ?: "Something went wrong. Please try again."
        }
    }
}
