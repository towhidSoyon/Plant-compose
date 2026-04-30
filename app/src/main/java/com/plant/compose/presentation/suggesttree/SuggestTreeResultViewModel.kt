package com.plant.compose.presentation.suggesttree

import androidx.lifecycle.viewModelScope
import com.plant.compose.domain.model.SuggestedTree
import com.plant.compose.domain.repository.SuggestedTreeRepository
import com.plant.compose.navigation.NavigationEvent
import com.plant.compose.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SuggestTreeResultViewModel(
    private val suggestedTreeRepository: SuggestedTreeRepository
) : BaseViewModel<SuggestTreeResultState, SuggestTreeResultAction, NavigationEvent>(SuggestTreeResultState()) {

    private var loadSuggestedTreesJob: Job? = null

    init {
        onAction(SuggestTreeResultAction.LoadSuggestedTrees)
    }

    override fun handleAction(action: SuggestTreeResultAction) {
        when (action) {
            is SuggestTreeResultAction.LoadSuggestedTrees -> loadSuggestedTrees()
            is SuggestTreeResultAction.Retry -> loadSuggestedTrees()
            is SuggestTreeResultAction.SelectCategory -> updateState {
                copy(selectedCategory = action.category)
            }
            is SuggestTreeResultAction.OnBackClick -> sendEvent(NavigationEvent.PopBackStack)
        }
    }

    private fun loadSuggestedTrees() {
        loadSuggestedTreesJob?.cancel()
        loadSuggestedTreesJob = viewModelScope.launch {
            updateState {
                copy(
                    isLoading = treesByCategory.isEmpty(),
                    errorMessage = null
                )
            }

            suggestedTreeRepository.getSuggestedTrees().collect { result ->
                result
                    .onSuccess { trees ->
                        val groupedTrees = trees
                            .groupBy { it.category }
                            .toSortedMap(String.CASE_INSENSITIVE_ORDER)
                        val categories = groupedTrees.keys.toList()
                        val selectedCategory = state.value.selectedCategory
                            ?.takeIf { it in categories }
                            ?: categories.firstOrNull()

                        updateState {
                            copy(
                                isLoading = false,
                                treesByCategory = groupedTrees,
                                categories = categories,
                                selectedCategory = selectedCategory,
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        updateState {
                            copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Unable to load suggested trees. Please try again."
                            )
                        }
                    }
            }
        }
    }
}

data class SuggestTreeResultState(
    val isLoading: Boolean = false,
    val treesByCategory: Map<String, List<SuggestedTree>> = emptyMap(),
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList(),
    val errorMessage: String? = null
)

sealed class SuggestTreeResultAction {
    object LoadSuggestedTrees : SuggestTreeResultAction()
    object Retry : SuggestTreeResultAction()
    data class SelectCategory(val category: String) : SuggestTreeResultAction()
    object OnBackClick : SuggestTreeResultAction()
}
