package com.plant.compose.presentation.contact

import androidx.lifecycle.viewModelScope
import com.plant.compose.domain.model.Agriculturist
import com.plant.compose.domain.repository.AgriculturistRepository
import com.plant.compose.navigation.NavigationEvent
import com.plant.compose.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ContactViewModel(
    private val agriculturistRepository: AgriculturistRepository
) : BaseViewModel<ContactState, ContactAction, NavigationEvent>(ContactState()) {

    private var loadAgriculturistsJob: Job? = null

    init {
        onAction(ContactAction.LoadAgriculturists)
    }

    override fun handleAction(action: ContactAction) {
        when (action) {
            is ContactAction.LoadAgriculturists -> loadAgriculturists()
            is ContactAction.Retry -> loadAgriculturists()
            is ContactAction.OnBackClick -> sendEvent(NavigationEvent.PopBackStack)
        }
    }

    private fun loadAgriculturists() {
        loadAgriculturistsJob?.cancel()
        loadAgriculturistsJob = viewModelScope.launch {
            updateState {
                copy(
                    isLoading = agriculturists.isEmpty(),
                    errorMessage = null
                )
            }

            agriculturistRepository.getAgriculturists().collect { result ->
                result
                    .onSuccess { agriculturists ->
                        updateState {
                            copy(
                                isLoading = false,
                                agriculturists = agriculturists,
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        updateState {
                            copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Unable to load agriculturists. Please try again."
                            )
                        }
                    }
            }
        }
    }
}

data class ContactState(
    val isLoading: Boolean = false,
    val agriculturists: List<Agriculturist> = emptyList(),
    val errorMessage: String? = null
)

sealed class ContactAction {
    object LoadAgriculturists : ContactAction()
    object Retry : ContactAction()
    object OnBackClick : ContactAction()
}
