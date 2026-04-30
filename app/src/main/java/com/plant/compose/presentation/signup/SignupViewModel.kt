package com.plant.compose.presentation.signup

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.plant.compose.domain.model.SignupRequest
import com.plant.compose.domain.repository.AuthRepository
import com.plant.compose.navigation.Screen
import com.plant.compose.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class SignupViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<SignupState, SignupAction, SignupEvent>(SignupState()) {

    override fun handleAction(action: SignupAction) {
        when (action) {
            is SignupAction.NameChanged -> updateState { copy(name = action.value) }
            is SignupAction.EmailChanged -> updateState { copy(email = action.value) }
            is SignupAction.PhoneChanged -> updateState { copy(phoneNumber = action.value) }
            is SignupAction.PasswordChanged -> updateState { copy(password = action.value) }
            is SignupAction.ConfirmPasswordChanged -> updateState { copy(confirmPassword = action.value) }
            is SignupAction.SignupClicked -> signup()
            is SignupAction.LoginClicked -> sendEvent(SignupEvent.Navigate(Screen.Login.route))
            is SignupAction.BackClicked -> sendEvent(SignupEvent.PopBackStack)
        }
    }

    private fun signup() {
        val name = state.value.name.trim()
        val email = state.value.email.trim()
        val phoneNumber = state.value.phoneNumber.trim()
        val password = state.value.password
        val confirmPassword = state.value.confirmPassword

        if (name.isBlank()) {
            sendEvent(SignupEvent.ShowError("Name is required."))
            return
        }

        if (email.isBlank()) {
            sendEvent(SignupEvent.ShowError("Email is required."))
            return
        }

        if (phoneNumber.isBlank()) {
            sendEvent(SignupEvent.ShowError("Phone number is required."))
            return
        }

        if (password.isBlank()) {
            sendEvent(SignupEvent.ShowError("Password is required."))
            return
        }

        if (confirmPassword.isBlank()) {
            sendEvent(SignupEvent.ShowError("Confirm password is required."))
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            sendEvent(SignupEvent.ShowError("Please enter a valid email address."))
            return
        }

        if (password != confirmPassword) {
            sendEvent(SignupEvent.ShowError("Password and confirm password must match."))
            return
        }

        if (password.length < 6) {
            sendEvent(SignupEvent.ShowError("Password must be at least 6 characters."))
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            authRepository
                .signup(
                    SignupRequest(
                        name = name,
                        email = email,
                        phoneNumber = phoneNumber,
                        password = password,
                        confirmPassword = confirmPassword
                    )
                )
                .collect { result ->
                    result
                        .onSuccess {
                            updateState { copy(isLoading = false) }
                            sendEvent(SignupEvent.SignupSuccess)
                        }
                        .onFailure { error ->
                            updateState {
                                copy(
                                    isLoading = false,
                                    errorMessage = error.message
                                )
                            }
                            sendEvent(SignupEvent.ShowError(error.message ?: "Signup failed. Please try again."))
                        }
                }
        }
    }
}

data class SignupState(
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class SignupAction {
    data class NameChanged(val value: String) : SignupAction()
    data class EmailChanged(val value: String) : SignupAction()
    data class PhoneChanged(val value: String) : SignupAction()
    data class PasswordChanged(val value: String) : SignupAction()
    data class ConfirmPasswordChanged(val value: String) : SignupAction()
    object SignupClicked : SignupAction()
    object LoginClicked : SignupAction()
    object BackClicked : SignupAction()
}

sealed class SignupEvent {
    data class Navigate(
        val route: String,
        val popUpTo: String? = null,
        val inclusive: Boolean = false
    ) : SignupEvent()

    object SignupSuccess : SignupEvent()
    data class ShowError(val message: String) : SignupEvent()
    object PopBackStack : SignupEvent()
}
