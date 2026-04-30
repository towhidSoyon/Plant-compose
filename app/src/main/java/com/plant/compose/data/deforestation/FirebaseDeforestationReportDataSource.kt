package com.plant.compose.data.deforestation

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.plant.compose.domain.model.DeforestationReport
import com.plant.compose.domain.model.SubmitDeforestationReportRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FirebaseDeforestationReportDataSource(
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) {
    fun submitReport(request: SubmitDeforestationReportRequest): Flow<Unit> = flow {
        val imageUrl = uploadImage(request)
        val report = DeforestationReport(
            complainIdentityImage = imageUrl,
            date = currentFormattedDate(),
            description = request.description.trim(),
            email = request.email.trim(),
            location = request.location.trim(),
            name = request.name.trim(),
            phoneNumber = request.phoneNumber.trim(),
            userId = request.userId.trim()
        )

        firestore
            .collection(COMPLAINTS_COLLECTION)
            .add(report.toFirestoreMap())
            .await()

        emit(Unit)
    }

    private suspend fun uploadImage(request: SubmitDeforestationReportRequest): String {
        val imageName = "${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg"
        val imageReference = firebaseStorage.reference
            .child(COMPLAIN_IMAGE_FOLDER)
            .child(imageName)

        imageReference.putFile(request.imageUri).await()
        return imageReference.downloadUrl.await().toString()
    }

    private fun DeforestationReport.toFirestoreMap(): Map<String, String> {
        return mapOf(
            "complainIdentityImage" to complainIdentityImage,
            "date" to date,
            "description" to description,
            "email" to email,
            "location" to location,
            "name" to name,
            "phoneNumber" to phoneNumber,
            "userId" to userId
        )
    }

    private fun currentFormattedDate(): String {
        return SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.ENGLISH)
            .format(Date())
            .replace("AM", "am")
            .replace("PM", "pm")
    }

    private companion object {
        const val COMPLAINTS_COLLECTION = "complainWithIdentity"
        const val COMPLAIN_IMAGE_FOLDER = "ComplainIdentityImage"
    }
}
