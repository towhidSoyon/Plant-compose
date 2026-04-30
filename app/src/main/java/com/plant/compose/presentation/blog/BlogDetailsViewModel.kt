package com.plant.compose.presentation.blog

import androidx.lifecycle.viewModelScope
import com.plant.compose.domain.model.Blog
import com.plant.compose.domain.repository.BlogRepository
import com.plant.compose.navigation.NavigationEvent
import com.plant.compose.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BlogDetailsViewModel(
    private val blogRepository: BlogRepository
) : BaseViewModel<BlogDetailsState, BlogDetailsAction, NavigationEvent>(BlogDetailsState()) {

    private var loadBlogJob: Job? = null
    private var currentBlogId: String? = null

    override fun handleAction(action: BlogDetailsAction) {
        when (action) {
            is BlogDetailsAction.LoadBlog -> loadBlog(action.blogId)
            is BlogDetailsAction.Retry -> currentBlogId?.let(::loadBlog)
            is BlogDetailsAction.OnBackClick -> sendEvent(NavigationEvent.PopBackStack)
        }
    }

    private fun loadBlog(blogId: String) {
        if (blogId.isBlank()) {
            updateState {
                copy(
                    isLoading = false,
                    errorMessage = "Blog not found."
                )
            }
            return
        }

        currentBlogId = blogId
        loadBlogJob?.cancel()
        loadBlogJob = viewModelScope.launch {
            updateState {
                copy(
                    isLoading = blog == null,
                    errorMessage = null
                )
            }

            blogRepository.getBlogById(blogId).collect { result ->
                result
                    .onSuccess { blog ->
                        updateState {
                            copy(
                                isLoading = false,
                                blog = blog,
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        updateState {
                            copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Unable to load blog. Please try again."
                            )
                        }
                    }
            }
        }
    }
}

data class BlogDetailsState(
    val isLoading: Boolean = false,
    val blog: Blog? = null,
    val errorMessage: String? = null
)

sealed class BlogDetailsAction {
    data class LoadBlog(val blogId: String) : BlogDetailsAction()
    object Retry : BlogDetailsAction()
    object OnBackClick : BlogDetailsAction()
}
