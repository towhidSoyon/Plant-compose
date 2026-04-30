package com.plant.compose.presentation.splash

import androidx.lifecycle.viewModelScope
import com.plant.compose.data.PreferenceManager
import com.plant.compose.domain.repository.AuthRepository
import com.plant.compose.domain.util.AuthResult
import com.plant.compose.navigation.NavigationEvent
import com.plant.compose.navigation.Screen
import com.plant.compose.presentation.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val preferenceManager: PreferenceManager,
    private val authRepository: AuthRepository
) :
    BaseViewModel<Unit, SplashAction, NavigationEvent>(Unit) {

    init {
        viewModelScope.launch {
            delay(3000)
            val destination = getStartDestination()
            sendEvent(NavigationEvent.Navigate(destination, Screen.Splash.route, true))
        }
    }

    override fun handleAction(action: SplashAction) {}

    private suspend fun getStartDestination(): String {
        if (!preferenceManager.isOnboardingCompleted()) {
            return Screen.Onboarding.route
        }

        return when (val result = authRepository.getCurrentUser()) {
            is AuthResult.Success -> {
                if (result.data != null) Screen.Home.route else Screen.Login.route
            }

            is AuthResult.Error -> Screen.Login.route
        }
    }
}

sealed class SplashAction
