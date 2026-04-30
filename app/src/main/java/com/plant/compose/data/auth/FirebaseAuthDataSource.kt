package com.plant.compose.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.plant.compose.domain.model.SignupRequest
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun login(email: String, password: String) {
        firebaseAuth
            .signInWithEmailAndPassword(email.trim(), password)
            .await()
    }

    suspend fun signup(request: SignupRequest) {
        if (request.password != request.confirmPassword) {
            error("Password and confirm password do not match.")
        }

        val user = firebaseAuth
            .createUserWithEmailAndPassword(request.email.trim(), request.password)
            .await()
            .user ?: error("User not found")

        val userMap = mapOf(
            NAME_FIELD to request.name.trim(),
            EMAIL_FIELD to request.email.trim(),
            PHONE_NUMBER_FIELD to request.phoneNumber.trim(),
            LOCATION_FIELD to "",
            IMAGE_FIELD to "",
            CREATED_AT_FIELD to System.currentTimeMillis()
        )

        try {
            firestore
                .collection(USERS_COLLECTION)
                .document(user.uid)
                .set(userMap)
                .await()
        } catch (error: Exception) {
            // TODO: Consider rollback/delete-user strategy if Firestore profile creation fails after Auth signup.
            throw IllegalStateException("Account created, but profile setup failed. Please contact support.", error)
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    private companion object {
        const val USERS_COLLECTION = "users"
        const val NAME_FIELD = "name"
        const val EMAIL_FIELD = "email"
        const val PHONE_NUMBER_FIELD = "phoneNumber"
        const val LOCATION_FIELD = "location"
        const val IMAGE_FIELD = "image"
        const val CREATED_AT_FIELD = "createdAt"
    }
}
