package com.plant.compose.domain.model

import android.net.Uri

data class SubmitDeforestationReportRequest(
    val imageUri: Uri,
    val description: String,
    val location: String,
    val email: String,
    val name: String,
    val phoneNumber: String,
    val userId: String
)
