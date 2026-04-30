package com.plant.compose.domain.util

sealed interface AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>
    data class Error(val message: String) : AuthResult<Nothing>
}
