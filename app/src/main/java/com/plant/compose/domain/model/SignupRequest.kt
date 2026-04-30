package com.plant.compose.domain.model

data class SignupRequest(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val confirmPassword: String
)
