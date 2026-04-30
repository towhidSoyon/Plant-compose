package com.plant.compose.domain.model

data class SuggestedTree(
    val id: String,
    val title: String,
    val suggestedImage: String?,
    val description: String?,
    val category: String
)
