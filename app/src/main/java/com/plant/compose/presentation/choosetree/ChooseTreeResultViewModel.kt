package com.plant.compose.presentation.choosetree

import androidx.lifecycle.viewModelScope
import com.plant.compose.domain.model.ChooseTreeInput
import com.plant.compose.domain.model.TreeRecommendation
import com.plant.compose.domain.repository.ChooseTreeRepository
import com.plant.compose.navigation.NavigationEvent
import com.plant.compose.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChooseTreeResultViewModel(
    private val chooseTreeRepository: ChooseTreeRepository
) : BaseViewModel<ChooseTreeResultState, ChooseTreeResultAction, NavigationEvent>(ChooseTreeResultState()) {

    private var loadRecommendationsJob: Job? = null
    private var lastInput: ChooseTreeInput? = null

    override fun handleAction(action: ChooseTreeResultAction) {
        when (action) {
            is ChooseTreeResultAction.LoadRecommendations -> loadRecommendations(action.input)
            is ChooseTreeResultAction.Retry -> lastInput?.let { loadRecommendations(it) }
            is ChooseTreeResultAction.SelectCategory -> updateState {
                copy(selectedCategory = action.category)
            }
            is ChooseTreeResultAction.OnBackClick -> sendEvent(NavigationEvent.PopBackStack)
        }
    }

    private fun loadRecommendations(input: ChooseTreeInput) {
        lastInput = input
        loadRecommendationsJob?.cancel()
        loadRecommendationsJob = viewModelScope.launch {
            updateState {
                copy(
                    isLoading = recommendations.isEmpty(),
                    errorMessage = null
                )
            }

            chooseTreeRepository.getTreeRecommendations(input).collect { result ->
                result
                    .onSuccess { recommendations ->
                        val categories = listOf(ALL_CATEGORY) +
                            recommendations
                                .map { it.category }
                                .distinct()
                                .sortedWith(String.CASE_INSENSITIVE_ORDER)

                        val selectedCategory = state.value.selectedCategory
                            .takeIf { it in categories }
                            ?: ALL_CATEGORY

                        updateState {
                            copy(
                                isLoading = false,
                                recommendations = recommendations,
                                selectedCategory = selectedCategory,
                                categories = categories,
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        updateState {
                            copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Unable to load tree recommendations. Please try again."
                            )
                        }
                    }
            }
        }
    }

    private companion object {
        const val ALL_CATEGORY = "All"
    }
}

data class ChooseTreeResultState(
    val isLoading: Boolean = false,
    val recommendations: List<TreeRecommendation> = emptyList(),
    val selectedCategory: String = "All",
    val categories: List<String> = listOf("All"),
    val errorMessage: String? = null
)

sealed class ChooseTreeResultAction {
    data class LoadRecommendations(val input: ChooseTreeInput) : ChooseTreeResultAction()
    data class SelectCategory(val category: String) : ChooseTreeResultAction()
    object Retry : ChooseTreeResultAction()
    object OnBackClick : ChooseTreeResultAction()
}
