package com.plant.compose.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel<State, Action, Event>(initialState: State) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _event = Channel<Event>()
    val event: Flow<Event> = _event.receiveAsFlow()

    fun onAction(action: Action) {
        handleAction(action)
    }

    protected abstract fun handleAction(action: Action)

    protected fun updateState(reducer: State.() -> State) {
        _state.update { reducer(it) }
    }

    protected fun sendEvent(event: Event) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
