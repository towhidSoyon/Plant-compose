package com.plant.compose.presentation.profile

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.plant.compose.domain.model.UpdateUserProfileRequest
import com.plant.compose.domain.repository.UserProfileRepository
import com.plant.compose.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userProfileRepository: UserProfileRepository
) : BaseViewModel<EditProfileState, EditProfileAction, EditProfileEffect>(EditProfileState()) {

    private var loadProfileJob: Job? = null
    private var updateProfileJob: Job? = null

    init {
        onAction(EditProfileAction.LoadProfile)
    }

    override fun handleAction(action: EditProfileAction) {
        when (action) {
            EditProfileAction.LoadProfile -> loadProfile()
            is EditProfileAction.NameChanged -> updateState { copy(name = action.value) }
            is EditProfileAction.PhoneChanged -> updateState { copy(phoneNumber = action.value) }
            is EditProfileAction.LocationChanged -> updateState { copy(location = action.value) }
            is EditProfileAction.ImageSelected -> updateState { copy(selectedImageUri = action.uri) }
            EditProfileAction.SaveClicked -> saveProfile()
        }
    }

    private fun loadProfile() {
        loadProfileJob?.cancel()
        loadProfileJob = viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }

            userProfileRepository.getCurrentUserProfile().collect { result ->
                result
                    .onSuccess { profile ->
                        updateState {
                            copy(
                                isLoading = false,
                                name = profile.name,
                                email = profile.email,
                                phoneNumber = profile.phoneNumber.orEmpty(),
                                location = profile.location.orEmpty(),
                                image = profile.image,
                                selectedImageUri = null,
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        val message = error.message ?: "Unable to load profile."
                        updateState { copy(isLoading = false, errorMessage = message) }
                        sendEvent(EditProfileEffect.ShowMessage(message))
                    }
            }
        }
    }

    private fun saveProfile() {
        val currentState = state.value
        if (currentState.name.isBlank()) {
            sendEvent(EditProfileEffect.ShowMessage("Name is required."))
            return
        }

        updateProfileJob?.cancel()
        updateProfileJob = viewModelScope.launch {
            updateState { copy(isUpdating = true, errorMessage = null) }

            userProfileRepository
                .updateCurrentUserProfile(
                    UpdateUserProfileRequest(
                        name = currentState.name,
                        phoneNumber = currentState.phoneNumber.takeIf { it.isNotBlank() },
                        location = currentState.location.takeIf { it.isNotBlank() },
                        imageUri = currentState.selectedImageUri
                    )
                )
                .collect { result ->
                    result
                        .onSuccess {
                            updateState { copy(isUpdating = false) }
                            sendEvent(EditProfileEffect.UpdateSuccess)
                        }
                        .onFailure { error ->
                            val message = error.message ?: "Unable to update profile."
                            updateState { copy(isUpdating = false, errorMessage = message) }
                            sendEvent(EditProfileEffect.ShowMessage(message))
                        }
                }
        }
    }
}

sealed class EditProfileAction {
    object LoadProfile : EditProfileAction()
    data class NameChanged(val value: String) : EditProfileAction()
    data class PhoneChanged(val value: String) : EditProfileAction()
    data class LocationChanged(val value: String) : EditProfileAction()
    data class ImageSelected(val uri: Uri) : EditProfileAction()
    object SaveClicked : EditProfileAction()
}

data class EditProfileState(
    val isLoading: Boolean = false,
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val location: String = "",
    val image: String? = null,
    val selectedImageUri: Uri? = null,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null
)

sealed class EditProfileEffect {
    object UpdateSuccess : EditProfileEffect()
    data class ShowMessage(val message: String) : EditProfileEffect()
}
