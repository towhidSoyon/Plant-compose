package com.plant.compose.presentation.profile

import androidx.lifecycle.viewModelScope
import com.plant.compose.domain.model.UserProfile
import com.plant.compose.domain.repository.UserProfileRepository
import com.plant.compose.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userProfileRepository: UserProfileRepository
) : BaseViewModel<ProfileState, ProfileAction, ProfileEffect>(ProfileState()) {

    private var loadProfileJob: Job? = null

    init {
        onAction(ProfileAction.LoadProfile)
    }

    override fun handleAction(action: ProfileAction) {
        when (action) {
            ProfileAction.LoadProfile -> loadProfile()
            ProfileAction.LogoutClicked -> logout()
        }
    }

    private fun loadProfile() {
        loadProfileJob?.cancel()
        loadProfileJob = viewModelScope.launch {
            updateState {
                copy(
                    isLoading = user == null,
                    errorMessage = null
                )
            }

            userProfileRepository.getCurrentUserProfile().collect { result ->
                result
                    .onSuccess { profile ->
                        updateState {
                            copy(
                                isLoading = false,
                                user = profile,
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        updateState {
                            copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Unable to load profile."
                            )
                        }
                        sendEvent(ProfileEffect.ShowMessage(error.message ?: "Unable to load profile."))
                    }
            }
        }
    }

    private fun logout() {
        userProfileRepository.logout()
            .onSuccess { sendEvent(ProfileEffect.LogoutSuccess) }
            .onFailure { error ->
                val message = error.message ?: "Unable to logout. Please try again."
                updateState { copy(errorMessage = message) }
                sendEvent(ProfileEffect.ShowMessage(message))
            }
    }
}

sealed class ProfileAction {
    object LoadProfile : ProfileAction()
    object LogoutClicked : ProfileAction()
}

data class ProfileState(
    val isLoading: Boolean = false,
    val user: UserProfile? = null,
    val errorMessage: String? = null
)

sealed class ProfileEffect {
    object LogoutSuccess : ProfileEffect()
    data class ShowMessage(val message: String) : ProfileEffect()
}
