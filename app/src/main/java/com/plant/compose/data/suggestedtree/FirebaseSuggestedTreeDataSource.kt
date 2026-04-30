package com.plant.compose.data.suggestedtree

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.plant.compose.domain.model.SuggestedTree
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseSuggestedTreeDataSource(
    private val firestore: FirebaseFirestore
) {
    fun getSuggestedTrees(): Flow<Result<List<SuggestedTree>>> = callbackFlow {
        val registration = firestore
            .collection(SUGGESTED_TREES_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(IllegalStateException(error.toSuggestedTreeMessage())))
                    return@addSnapshotListener
                }

                val trees = snapshot
                    ?.documents
                    .orEmpty()
                    .mapNotNull { it.toSuggestedTreeOrNull() }

                trySend(Result.success(trees))
            }

        awaitClose { registration.remove() }
    }

    private fun DocumentSnapshot.toSuggestedTreeOrNull(): SuggestedTree? {
        val title = getString("title")?.takeIf { it.isNotBlank() } ?: return null
        val category = getString("category")?.takeIf { it.isNotBlank() } ?: return null

        return SuggestedTree(
            id = id,
            title = title,
            suggestedImage = getString("suggestedImage"),
            description = getString("description"),
            category = category
        )
    }

    private fun Throwable.toSuggestedTreeMessage(): String {
        return when (this) {
            is FirebaseNetworkException -> "Network error. Please check your connection."
            is FirebaseFirestoreException -> when (code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> {
                    "You do not have permission to view suggested trees."
                }

                FirebaseFirestoreException.Code.UNAVAILABLE -> {
                    "Suggestion service is unavailable. Please try again."
                }

                else -> "Unable to load suggested trees. Please try again."
            }

            is FirebaseException -> "Unable to load suggested trees. Please try again."
            else -> "Something went wrong. Please try again."
        }
    }

    private companion object {
        const val SUGGESTED_TREES_COLLECTION = "suggestedTrees"
    }
}
