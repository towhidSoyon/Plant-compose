package com.plant.compose.presentation.blog

import androidx.lifecycle.viewModelScope
import com.plant.compose.domain.model.Blog
import com.plant.compose.domain.repository.BlogRepository
import com.plant.compose.navigation.NavigationEvent
import com.plant.compose.navigation.Screen
import com.plant.compose.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BlogViewModel(
    private val blogRepository: BlogRepository
) : BaseViewModel<BlogState, BlogAction, NavigationEvent>(BlogState()) {

    private var loadBlogsJob: Job? = null

    init {
        onAction(BlogAction.LoadBlogs)
    }

    override fun handleAction(action: BlogAction) {
        when (action) {
            is BlogAction.OnBlogClick -> {
                sendEvent(NavigationEvent.Navigate(Screen.BlogDetails.createRoute(action.blogId)))
            }
            is BlogAction.OnBackClick -> sendEvent(NavigationEvent.PopBackStack)
            is BlogAction.LoadBlogs -> loadBlogs()
            is BlogAction.Retry -> loadBlogs()
        }
    }

    private fun loadBlogs() {
        loadBlogsJob?.cancel()
        loadBlogsJob = viewModelScope.launch {
            updateState {
                copy(
                    isLoading = blogs.isEmpty(),
                    errorMessage = null
                )
            }

            blogRepository.getBlogs().collect { result ->
                result
                    .onSuccess { blogs ->
                        updateState {
                            copy(
                                isLoading = false,
                                blogs = blogs,
                                errorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        updateState {
                            copy(
                                isLoading = false,
                                errorMessage = error.message ?: "Unable to load blogs. Please try again."
                            )
                        }
                    }
            }
        }
    }
}

data class BlogState(
    val isLoading: Boolean = false,
    val blogs: List<Blog> = emptyList(),
    val errorMessage: String? = null
)

sealed class BlogAction {
    object LoadBlogs : BlogAction()
    object Retry : BlogAction()
    data class OnBlogClick(val blogId: String) : BlogAction()
    object OnBackClick : BlogAction()
}
