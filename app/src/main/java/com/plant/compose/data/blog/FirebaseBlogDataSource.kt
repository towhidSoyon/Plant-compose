package com.plant.compose.data.blog

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseBlogDataSource(
    private val firestore: FirebaseFirestore
) {
    fun getBlogs(): Flow<List<BlogDto>> = callbackFlow {
        val registration = firestore
            .collection(BLOGS_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val blogs = snapshot
                    ?.documents
                    .orEmpty()
                    .map { it.toBlogDto() }
                    .sortedWith(compareByDescending<BlogDto> { it.createdAt ?: Long.MIN_VALUE })

                trySend(blogs)
            }

        awaitClose { registration.remove() }
    }

    fun getBlogById(blogId: String): Flow<BlogDto> = callbackFlow {
        val registration = firestore
            .collection(BLOGS_COLLECTION)
            .document(blogId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot == null || !snapshot.exists()) {
                    close(NoSuchElementException("Blog not found."))
                    return@addSnapshotListener
                }

                trySend(snapshot.toBlogDto())
            }

        awaitClose { registration.remove() }
    }

    private fun DocumentSnapshot.toBlogDto(): BlogDto {
        return BlogDto(
            id = id,
            title = getString("title").orEmpty(),
            description = getString("description").orEmpty(),
            imageUrl = getString("imageUrl") ?: getString("blogImage"),
            authorName = getString("authorName"),
            createdAt = getTimestampMillis("createdAt")
        )
    }

    private fun DocumentSnapshot.getTimestampMillis(field: String): Long? {
        return when (val value = get(field)) {
            is Timestamp -> value.toDate().time
            is Number -> value.toLong()
            else -> null
        }
    }

    private companion object {
        const val BLOGS_COLLECTION = "blogs"
    }
}
