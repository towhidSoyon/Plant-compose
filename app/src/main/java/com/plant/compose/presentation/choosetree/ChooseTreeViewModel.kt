package com.plant.compose.presentation.choosetree

import com.plant.compose.domain.model.ChooseTreeInput
import com.plant.compose.navigation.NavigationEvent
import com.plant.compose.navigation.Screen
import com.plant.compose.presentation.base.BaseViewModel

class ChooseTreeViewModel : BaseViewModel<Unit, ChooseTreeAction, NavigationEvent>(Unit) {
    override fun handleAction(action: ChooseTreeAction) {
        when (action) {
            is ChooseTreeAction.OnSuggestClick -> {
                sendEvent(NavigationEvent.Navigate(Screen.ChooseTreeResult.createRoute(action.input)))
            }
            is ChooseTreeAction.OnBackClick -> sendEvent(NavigationEvent.PopBackStack)
        }
    }
}

sealed class ChooseTreeAction {
    data class OnSuggestClick(val input: ChooseTreeInput) : ChooseTreeAction()
    object OnBackClick : ChooseTreeAction()
}
