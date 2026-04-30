package com.plant.compose.domain.model

data class TreeRecommendation(
    val id: String,
    val name: String,
    val category: String,
    val imageUrl: String?,
    val matchPercentage: Int,
    val description: String?
)
