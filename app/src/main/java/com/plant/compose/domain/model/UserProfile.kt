package com.plant.compose.domain.model

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phoneNumber: String?,
    val location: String?,
    val image: String?
)
