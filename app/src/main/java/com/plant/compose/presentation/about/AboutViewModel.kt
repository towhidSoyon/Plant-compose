package com.plant.compose.presentation.about

import com.plant.compose.navigation.NavigationEvent
import com.plant.compose.presentation.base.BaseViewModel

class AboutViewModel : BaseViewModel<Unit, AboutAction, NavigationEvent>(Unit) {
    override fun handleAction(action: AboutAction) {
        when (action) {
            is AboutAction.OnBackClick -> sendEvent(NavigationEvent.PopBackStack)
        }
    }
}

sealed class AboutAction {
    object OnBackClick : AboutAction()
}
