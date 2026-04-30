package com.plant.compose.data.agriculturist

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.plant.compose.domain.model.Agriculturist
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseAgriculturistDataSource(
    private val firestore: FirebaseFirestore
) {
    fun getAgriculturists(): Flow<Result<List<Agriculturist>>> = callbackFlow {
        val registration = firestore
            .collection(AGRICULTURISTS_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(IllegalStateException(error.toAgriculturistMessage())))
                    return@addSnapshotListener
                }

                val agriculturists = snapshot
                    ?.documents
                    .orEmpty()
                    .map { it.toAgriculturist() }
                    .sortedBy { it.name.lowercase() }

                trySend(Result.success(agriculturists))
            }

        awaitClose { registration.remove() }
    }

    private fun DocumentSnapshot.toAgriculturist(): Agriculturist {
        return Agriculturist(
            id = id,
            name = getString("name").orEmpty(),
            phone = getString("phoneNumber"),
            email = getString("email"),
            district = getString("district"),
            imageUrl = getString("imageUrl")
        )
    }

    private fun Throwable.toAgriculturistMessage(): String {
        return when (this) {
            is FirebaseNetworkException -> "Network error. Please check your connection."
            is FirebaseFirestoreException -> when (code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> {
                    "You do not have permission to view agriculturists."
                }

                FirebaseFirestoreException.Code.UNAVAILABLE -> {
                    "Contact service is unavailable. Please try again."
                }

                else -> "Unable to load agriculturists. Please try again."
            }

            is FirebaseException -> "Unable to load agriculturists. Please try again."
            else -> "Something went wrong. Please try again."
        }
    }

    private companion object {
        const val AGRICULTURISTS_COLLECTION = "agriculturist"
    }
}
