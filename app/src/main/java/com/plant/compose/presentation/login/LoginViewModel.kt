package com.plant.compose.presentation.login

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.plant.compose.domain.repository.AuthRepository
import com.plant.compose.navigation.Screen
import com.plant.compose.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<LoginState, LoginAction, LoginEvent>(LoginState()) {

    override fun handleAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnEmailChange -> updateState { copy(email = action.value) }
            is LoginAction.OnPasswordChange -> updateState { copy(password = action.value) }
            is LoginAction.OnLoginClick -> login()
            is LoginAction.OnSignupClick -> sendEvent(LoginEvent.Navigate(Screen.Signup.route))
            is LoginAction.OnBackClick -> sendEvent(LoginEvent.PopBackStack)
        }
    }

    private fun login() {
        val email = state.value.email.trim()
        val password = state.value.password

        if (email.isBlank() || password.isBlank()) {
            sendEvent(LoginEvent.ShowError("Email and password are required."))
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            sendEvent(LoginEvent.ShowError("Please enter a valid email address."))
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }

            authRepository.login(email, password).collect { result ->
                result
                    .onSuccess {
                        updateState { copy(isLoading = false) }
                        sendEvent(LoginEvent.Navigate(Screen.Home.route, Screen.Login.route, true))
                    }
                    .onFailure { error ->
                        updateState { copy(isLoading = false) }
                        sendEvent(LoginEvent.ShowError(error.message ?: "Invalid email or password."))
                    }
                }
        }
    }
}

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false
)

sealed class LoginAction {
    data class OnEmailChange(val value: String) : LoginAction()
    data class OnPasswordChange(val value: String) : LoginAction()
    object OnLoginClick : LoginAction()
    object OnSignupClick : LoginAction()
    object OnBackClick : LoginAction()
}

sealed class LoginEvent {
    data class Navigate(
        val route: String,
        val popUpTo: String? = null,
        val inclusive: Boolean = false
    ) : LoginEvent()

    data class ShowError(val message: String) : LoginEvent()
    object PopBackStack : LoginEvent()
}
