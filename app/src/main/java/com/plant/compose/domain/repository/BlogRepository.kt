package com.plant.compose.domain.repository

import com.plant.compose.domain.model.Blog
import kotlinx.coroutines.flow.Flow

interface BlogRepository {
    fun getBlogs(): Flow<Result<List<Blog>>>
    fun getBlogById(blogId: String): Flow<Result<Blog>>
}
