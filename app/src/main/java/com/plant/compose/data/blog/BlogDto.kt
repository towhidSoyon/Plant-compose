package com.plant.compose.data.blog

data class BlogDto(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val authorName: String?,
    val createdAt: Long?
)
