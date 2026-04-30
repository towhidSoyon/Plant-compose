package com.plant.compose.domain.repository

import com.plant.compose.domain.model.SubmitDeforestationReportRequest
import kotlinx.coroutines.flow.Flow

interface DeforestationReportRepository {
    fun submitReport(request: SubmitDeforestationReportRequest): Flow<Result<Unit>>
}
