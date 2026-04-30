package com.plant.compose.presentation.onboarding

import com.plant.compose.data.PreferenceManager
import com.plant.compose.navigation.NavigationEvent
import com.plant.compose.navigation.Screen
import com.plant.compose.presentation.base.BaseViewModel

class OnboardingViewModel(private val preferenceManager: PreferenceManager) :
    BaseViewModel<Unit, OnboardingAction, NavigationEvent>(Unit) {

    override fun handleAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.OnGetStarted -> {
                preferenceManager.setOnboardingCompleted(true)
                sendEvent(NavigationEvent.Navigate(Screen.Signup.route, Screen.Onboarding.route, true))
            }
            is OnboardingAction.OnLogin -> {
                preferenceManager.setOnboardingCompleted(true)
                sendEvent(NavigationEvent.Navigate(Screen.Login.route, Screen.Onboarding.route, true))
            }
        }
    }
}

sealed class OnboardingAction {
    object OnGetStarted : OnboardingAction()
    object OnLogin : OnboardingAction()
}
