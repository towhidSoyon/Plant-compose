package com.plant.compose.domain.model

import android.net.Uri

data class UpdateUserProfileRequest(
    val name: String,
    val phoneNumber: String?,
    val location: String?,
    val imageUri: Uri? = null
)
