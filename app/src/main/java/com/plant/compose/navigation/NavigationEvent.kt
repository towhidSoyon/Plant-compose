package com.plant.compose.navigation

sealed class NavigationEvent {
    data class Navigate(val route: String, val popUpTo: String? = null, val inclusive: Boolean = false) : NavigationEvent()
    object PopBackStack : NavigationEvent()
}
