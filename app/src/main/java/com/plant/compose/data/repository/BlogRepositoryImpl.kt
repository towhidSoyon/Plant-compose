package com.plant.compose.data.repository

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.plant.compose.data.blog.BlogDto
import com.plant.compose.data.blog.FirebaseBlogDataSource
import com.plant.compose.domain.model.Blog
import com.plant.compose.domain.repository.BlogRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class BlogRepositoryImpl(
    private val blogDataSource: FirebaseBlogDataSource,
    private val firebaseStorage: FirebaseStorage
) : BlogRepository {

    override fun getBlogs(): Flow<Result<List<Blog>>> {
        return blogDataSource
            .getBlogs()
            .map { blogs ->
                Result.success(blogs.toDomainBlogs())
            }
            .catch { error ->
                emit(Result.failure(IllegalStateException(error.toBlogMessage())))
            }
    }

    override fun getBlogById(blogId: String): Flow<Result<Blog>> {
        return blogDataSource
            .getBlogById(blogId)
            .map { blog ->
                Result.success(blog.toDomain())
            }
            .catch { error ->
                emit(Result.failure(IllegalStateException(error.toBlogMessage())))
            }
    }

    private suspend fun List<BlogDto>.toDomainBlogs(): List<Blog> = coroutineScope {
        map { blog ->
            async { blog.toDomain() }
        }.awaitAll()
    }

    private suspend fun BlogDto.toDomain(): Blog {
        return Blog(
            id = id,
            title = title,
            description = description,
            imageUrl = imageUrl.toCoilImageUrl(),
            authorName = authorName,
            createdAt = createdAt
        )
    }

    private suspend fun String?.toCoilImageUrl(): String? {
        val value = this?.trim().orEmpty()

        return when {
            value.isBlank() -> null
            value.startsWith("https://") -> value
            value.startsWith("http://") -> value
            value.startsWith("//") -> "https:$value"
            value.startsWith("gs://") -> firebaseStorage
                .getReferenceFromUrl(value)
                .downloadUrl
                .await()
                .toString()
            else -> firebaseStorage
                .reference
                .child(value.trimStart('/'))
                .downloadUrl
                .await()
                .toString()
        }
    }

    private fun Throwable.toBlogMessage(): String {
        return when (this) {
            is NoSuchElementException -> message ?: "Blog not found."
            is FirebaseNetworkException -> "Network error. Please check your connection."
            is FirebaseFirestoreException -> when (code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> {
                    "You do not have permission to view blogs."
                }
                FirebaseFirestoreException.Code.UNAVAILABLE -> {
                    "Blog service is unavailable. Please try again."
                }
                else -> "Unable to load blogs. Please try again."
            }
            is FirebaseException -> "Unable to load blogs. Please try again."
            else -> "Something went wrong. Please try again."
        }
    }
}
