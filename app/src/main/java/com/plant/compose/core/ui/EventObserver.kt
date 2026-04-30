package com.plant.compose.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> Flow<T>.observeAsEvents(
    onEvent: (T) -> Unit
) {
    val currentOnEvent by rememberUpdatedState(onEvent)

    LaunchedEffect(this) {
        collect { event ->
            currentOnEvent(event)
        }
    }
}
