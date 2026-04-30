package com.plant.compose.data.repository

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.plant.compose.data.auth.FirebaseAuthDataSource
import com.plant.compose.domain.model.SignupRequest
import com.plant.compose.domain.model.User
import com.plant.compose.domain.repository.AuthRepository
import com.plant.compose.domain.util.AuthResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val authDataSource: FirebaseAuthDataSource
) : AuthRepository {

    override fun signup(request: SignupRequest): Flow<Result<Unit>> {
        return flow {
            authDataSource.signup(request)
            emit(Result.success(Unit))
        }.catch { error ->
            emit(Result.failure(IllegalStateException(error.toAuthMessage(), error)))
        }
    }

    override fun login(email: String, password: String): Flow<Result<Unit>> {
        return flow {
            authDataSource.login(email, password)
            emit(Result.success(Unit))
        }.catch { error ->
            emit(Result.failure(IllegalStateException(error.toAuthMessage(), error)))
        }
    }

    override fun logout(): Result<Unit> {
        return runCatching { authDataSource.logout() }
            .recoverCatching { throw IllegalStateException(it.toAuthMessage(), it) }
    }

    override suspend fun getCurrentUser(): AuthResult<User?> {
        return runCatching {
            authDataSource.getCurrentUser()?.toDomain()
        }.fold(
            onSuccess = { AuthResult.Success(it) },
            onFailure = { AuthResult.Error(it.toAuthMessage()) }
        )
    }

    private fun FirebaseUser.toDomain(): User {
        return User(
            id = uid,
            email = email,
            name = displayName,
            phoneNumber = phoneNumber
        )
    }

    private fun Throwable.toAuthMessage(): String {
        return when (this) {
            is FirebaseAuthInvalidUserException -> "No account found with this email."
            is FirebaseAuthInvalidCredentialsException -> authMessageFromCode()
            is FirebaseAuthUserCollisionException -> "An account already exists with this email."
            is FirebaseAuthWeakPasswordException -> "Password is too weak."
            is FirebaseNetworkException -> "Network error. Please check your connection."
            is IllegalStateException -> message ?: "Authentication failed. Please try again."
            is FirebaseException -> "Authentication failed. Please try again."
            else -> "Something went wrong. Please try again."
        }
    }

    private fun FirebaseAuthInvalidCredentialsException.authMessageFromCode(): String {
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" -> "Please enter a valid email address."
            "ERROR_WRONG_PASSWORD" -> "Invalid email or password."
            "ERROR_INVALID_CREDENTIAL" -> "Invalid email or password."
            else -> "Invalid email or password."
        }
    }
}
