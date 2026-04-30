package com.plant.compose.data.repository

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.StorageException
import com.plant.compose.data.deforestation.FirebaseDeforestationReportDataSource
import com.plant.compose.domain.model.SubmitDeforestationReportRequest
import com.plant.compose.domain.repository.DeforestationReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DeforestationReportRepositoryImpl(
    private val dataSource: FirebaseDeforestationReportDataSource
) : DeforestationReportRepository {

    override fun submitReport(request: SubmitDeforestationReportRequest): Flow<Result<Unit>> {
        return dataSource
            .submitReport(request)
            .map { Result.success(Unit) }
            .catch { error ->
                emit(Result.failure(IllegalStateException(error.toReportMessage())))
            }
    }

    private fun Throwable.toReportMessage(): String {
        return when (this) {
            is FirebaseNetworkException -> "Network error. Please check your connection."
            is StorageException -> when (errorCode) {
                StorageException.ERROR_NOT_AUTHORIZED -> "You do not have permission to upload this image."
                StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> "Image upload failed. Please try again."
                else -> "Image upload failed. Please try again."
            }
            is FirebaseFirestoreException -> when (code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED -> {
                    "You do not have permission to submit this report."
                }
                FirebaseFirestoreException.Code.UNAVAILABLE -> {
                    "Report service is unavailable. Please try again."
                }
                else -> "Unable to submit report. Please try again."
            }
            is FirebaseException -> "Unable to submit report. Please try again."
            else -> "Something went wrong. Please try again."
        }
    }
}
