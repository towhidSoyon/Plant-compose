package com.plant.compose.data.slider

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.plant.compose.domain.model.SliderImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseSliderDataSource(
    private val firebaseStorage: FirebaseStorage
) {
    fun getSliderImages(): Flow<Result<List<SliderImage>>> = flow {
        val folderReference = firebaseStorage.reference.child(SLIDER_FOLDER)
        val images = folderReference
            .listAll()
            .await()
            .items
            .map { item ->
                SliderImage(
                    id = item.name,
                    imageUrl = item.downloadUrl.await().toString()
                )
            }

        emit(Result.success(images))
    }.catchAsResult()

    private fun Flow<Result<List<SliderImage>>>.catchAsResult(): Flow<Result<List<SliderImage>>> = flow {
        try {
            collect { emit(it) }
        } catch (error: Throwable) {
            emit(Result.failure(IllegalStateException(error.toSliderMessage())))
        }
    }

    private fun Throwable.toSliderMessage(): String {
        return when (this) {
            is FirebaseNetworkException -> "Network error. Please check your connection."
            is StorageException -> when (errorCode) {
                StorageException.ERROR_OBJECT_NOT_FOUND -> "No slider images found."
                StorageException.ERROR_NOT_AUTHORIZED -> "You do not have permission to view slider images."
                StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> "Unable to load slider images. Please try again."
                else -> "Unable to load slider images. Please try again."
            }

            is FirebaseException -> "Unable to load slider images. Please try again."
            else -> "Something went wrong. Please try again."
        }
    }

    private companion object {
        const val SLIDER_FOLDER = "Slider Image"
    }
}
