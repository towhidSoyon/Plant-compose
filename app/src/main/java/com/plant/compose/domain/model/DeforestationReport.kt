package com.plant.compose.domain.model

data class DeforestationReport(
    val complainIdentityImage: String,
    val date: String,
    val description: String,
    val email: String,
    val location: String,
    val name: String,
    val phoneNumber: String,
    val userId: String
)
