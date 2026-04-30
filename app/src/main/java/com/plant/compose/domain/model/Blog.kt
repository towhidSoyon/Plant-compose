package com.plant.compose.domain.model

data class Blog(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val authorName: String?,
    val createdAt: Long?
)
