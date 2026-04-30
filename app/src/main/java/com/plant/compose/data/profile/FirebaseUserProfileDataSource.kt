package com.plant.compose.data.profile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.plant.compose.domain.model.UpdateUserProfileRequest
import com.plant.compose.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseUserProfileDataSource(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) {
    fun getCurrentUserProfile(): Flow<UserProfile> = flow {
        val user = firebaseAuth.currentUser ?: error("No logged-in user found.")
        val snapshot = findCurrentUserSnapshot() ?: error("User profile not found.")

        emit(
            UserProfile(
                id = snapshot.id,
                name = snapshot.getString(NAME_FIELD).orEmpty(),
                email = snapshot.getString(EMAIL_FIELD).orEmpty().ifBlank { user.email.orEmpty() },
                phoneNumber = snapshot.getString(PHONE_NUMBER_FIELD),
                location = snapshot.getString(LOCATION_FIELD),
                image = snapshot.getString(IMAGE_FIELD)
            )
        )
    }

    fun updateCurrentUserProfile(request: UpdateUserProfileRequest): Flow<Unit> = flow {
        val documentReference = findCurrentUserDocumentReference() ?: error("User profile not found.")
        val imageUrl = request.imageUri?.let { uri ->
            val imageName = "${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg"
            val imageReference = firebaseStorage.reference
                .child(USER_IMAGES_FOLDER)
                .child(imageName)

            imageReference.putFile(uri).await()
            imageReference.downloadUrl.await().toString()
        }

        val updateMap = buildMap<String, Any?> {
            put(NAME_FIELD, request.name.trim())
            put(PHONE_NUMBER_FIELD, request.phoneNumber?.trim().orEmpty())
            put(LOCATION_FIELD, request.location?.trim().orEmpty())
            if (imageUrl != null) put(IMAGE_FIELD, imageUrl)
        }

        documentReference.update(updateMap).await()
        emit(Unit)
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    private suspend fun findCurrentUserSnapshot(): DocumentSnapshot? {
        val user = firebaseAuth.currentUser ?: return null
        val uidSnapshot = firestore
            .collection(USERS_COLLECTION)
            .document(user.uid)
            .get()
            .await()

        if (uidSnapshot.exists()) return uidSnapshot

        val email = user.email?.takeIf { it.isNotBlank() } ?: return null
        return firestore
            .collection(USERS_COLLECTION)
            .whereEqualTo(EMAIL_FIELD, email)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
    }

    private suspend fun findCurrentUserDocumentReference(): DocumentReference? {
        return findCurrentUserSnapshot()?.reference
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val USER_IMAGES_FOLDER = "User Images"
        const val NAME_FIELD = "name"
        const val EMAIL_FIELD = "email"
        const val PHONE_NUMBER_FIELD = "phoneNumber"
        const val LOCATION_FIELD = "location"
        const val IMAGE_FIELD = "image"
    }
}
