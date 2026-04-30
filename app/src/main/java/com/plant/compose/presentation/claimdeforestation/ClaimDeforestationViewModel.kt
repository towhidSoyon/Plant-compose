package com.plant.compose.presentation.claimdeforestation

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.plant.compose.domain.model.SubmitDeforestationReportRequest
import com.plant.compose.domain.repository.AuthRepository
import com.plant.compose.domain.repository.DeforestationReportRepository
import com.plant.compose.domain.util.AuthResult
import com.plant.compose.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class ClaimDeforestationViewModel(
    private val deforestationReportRepository: DeforestationReportRepository,
    private val authRepository: AuthRepository
) : BaseViewModel<ClaimDeforestationState, ClaimDeforestationAction, ClaimDeforestationEffect>(
    ClaimDeforestationState()
) {

    override fun handleAction(action: ClaimDeforestationAction) {
        when (action) {
            is ClaimDeforestationAction.DescriptionChanged -> updateState {
                copy(description = action.value, submitErrorMessage = null)
            }
            is ClaimDeforestationAction.LocationChanged -> updateState {
                copy(location = action.value, submitErrorMessage = null)
            }
            is ClaimDeforestationAction.ImageSelected -> updateState {
                copy(selectedImageUri = action.uri, submitErrorMessage = null)
            }
            is ClaimDeforestationAction.OnBackClick -> sendEvent(ClaimDeforestationEffect.PopBackStack)
            is ClaimDeforestationAction.OnSubmitClick -> submitReport()
        }
    }

    private fun submitReport() {
        val currentState = state.value
        val imageUri = currentState.selectedImageUri

        when {
            currentState.description.isBlank() -> {
                sendEvent(ClaimDeforestationEffect.ShowMessage("Description is required."))
                return
            }

            currentState.location.isBlank() -> {
                sendEvent(ClaimDeforestationEffect.ShowMessage("Location is required."))
                return
            }

            imageUri == null -> {
                sendEvent(ClaimDeforestationEffect.ShowMessage("Please select an image."))
                return
            }
        }

        viewModelScope.launch {
            val currentUser = when (val result = authRepository.getCurrentUser()) {
                is AuthResult.Success -> result.data
                is AuthResult.Error -> null
            }

            if (currentUser == null) {
                sendEvent(ClaimDeforestationEffect.ShowMessage("Please login before submitting a report."))
                return@launch
            }

            val email = currentUser.email.orEmpty()
            val request = SubmitDeforestationReportRequest(
                imageUri = imageUri,
                description = currentState.description,
                location = currentState.location,
                email = email,
                name = currentUser.name?.takeIf { it.isNotBlank() }
                    ?: email.substringBefore("@").takeIf { it.isNotBlank() }
                    ?: "Plant User",
                phoneNumber = currentUser.phoneNumber.orEmpty(),
                userId = currentUser.id
            )

            updateState {
                copy(
                    isSubmitting = true,
                    submitErrorMessage = null
                )
            }

            deforestationReportRepository.submitReport(request).collect { result ->
                result
                    .onSuccess {
                        updateState {
                            copy(
                                description = "",
                                location = "",
                                selectedImageUri = null,
                                isSubmitting = false,
                                submitErrorMessage = null
                            )
                        }
                        sendEvent(ClaimDeforestationEffect.SubmitSuccess)
                    }
                    .onFailure { error ->
                        val message = error.message ?: "Unable to submit report. Please try again."
                        updateState {
                            copy(
                                isSubmitting = false,
                                submitErrorMessage = message
                            )
                        }
                        sendEvent(ClaimDeforestationEffect.ShowMessage(message))
                    }
            }
        }
    }
}

data class ClaimDeforestationState(
    val description: String = "",
    val location: String = "",
    val selectedImageUri: Uri? = null,
    val isSubmitting: Boolean = false,
    val submitErrorMessage: String? = null
)

sealed class ClaimDeforestationAction {
    data class DescriptionChanged(val value: String) : ClaimDeforestationAction()
    data class LocationChanged(val value: String) : ClaimDeforestationAction()
    data class ImageSelected(val uri: Uri) : ClaimDeforestationAction()
    object OnBackClick : ClaimDeforestationAction()
    object OnSubmitClick : ClaimDeforestationAction()
}

sealed class ClaimDeforestationEffect {
    object SubmitSuccess : ClaimDeforestationEffect()
    object PopBackStack : ClaimDeforestationEffect()
    data class ShowMessage(val message: String) : ClaimDeforestationEffect()
}
