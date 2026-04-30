package com.plant.compose.presentation.home

import androidx.lifecycle.viewModelScope
import com.plant.compose.domain.model.SliderImage
import com.plant.compose.domain.model.WeatherInfo
import com.plant.compose.domain.repository.SliderRepository
import com.plant.compose.domain.repository.UserProfileRepository
import com.plant.compose.domain.repository.WeatherRepository
import com.plant.compose.navigation.NavigationEvent
import com.plant.compose.navigation.Screen
import com.plant.compose.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeViewModel(
    private val sliderRepository: SliderRepository,
    private val userProfileRepository: UserProfileRepository,
    private val weatherRepository: WeatherRepository
) : BaseViewModel<HomeState, HomeAction, NavigationEvent>(HomeState()) {

    private var loadSliderImagesJob: Job? = null
    private var loadUserProfileJob: Job? = null
    private var loadWeatherJob: Job? = null

    init {
        onAction(HomeAction.LoadInitialData)
    }

    override fun handleAction(action: HomeAction) {
        when (action) {
            is HomeAction.LoadInitialData -> {
                loadSliderImages()
                loadUserProfile()
                loadWeather()
            }
            is HomeAction.LoadSliderImages -> loadSliderImages()
            is HomeAction.RetrySliderImages -> loadSliderImages()
            is HomeAction.LoadUserProfile -> loadUserProfile()
            is HomeAction.LoadWeather -> loadWeather()
            is HomeAction.RetryWeather -> loadWeather()
            is HomeAction.OnReportClick -> sendEvent(NavigationEvent.Navigate(Screen.ClaimDeforestation.route))
            is HomeAction.OnChooseTreeClick -> sendEvent(NavigationEvent.Navigate(Screen.ChooseTree.route))
            is HomeAction.OnSuggestTreeClick -> sendEvent(NavigationEvent.Navigate(Screen.SuggestTreeResult.route))
        }
    }

    private fun loadSliderImages() {
        loadSliderImagesJob?.cancel()
        loadSliderImagesJob = viewModelScope.launch {
            updateState {
                copy(
                    isSliderLoading = sliderImages.isEmpty(),
                    sliderErrorMessage = null
                )
            }

            sliderRepository.getSliderImages().collect { result ->
                result
                    .onSuccess { images ->
                        updateState {
                            copy(
                                isSliderLoading = false,
                                sliderImages = images,
                                sliderErrorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        updateState {
                            copy(
                                isSliderLoading = false,
                                sliderErrorMessage = error.message ?: "Unable to load slider images. Please try again."
                            )
                        }
                    }
            }
        }
    }

    private fun loadUserProfile() {
        loadUserProfileJob?.cancel()
        loadUserProfileJob = viewModelScope.launch {
            userProfileRepository.getCurrentUserProfile().collect { result ->
                result
                    .onSuccess { profile ->
                        updateState {
                            copy(
                                userName = profile.name,
                                userProfileErrorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        updateState {
                            copy(userProfileErrorMessage = error.message ?: "Unable to load user profile.")
                        }
                    }
            }
        }
    }

    private fun loadWeather() {
        loadWeatherJob?.cancel()
        loadWeatherJob = viewModelScope.launch {
            updateState {
                copy(
                    isWeatherLoading = weatherInfo == null,
                    weatherErrorMessage = null
                )
            }

            weatherRepository.getCurrentWeather().collect { result ->
                result
                    .onSuccess { weather ->
                        updateState {
                            copy(
                                isWeatherLoading = false,
                                weatherInfo = weather,
                                weatherErrorMessage = null
                            )
                        }
                    }
                    .onFailure { error ->
                        updateState {
                            copy(
                                isWeatherLoading = false,
                                weatherErrorMessage = error.message ?: "Unable to load Dhaka weather."
                            )
                        }
                    }
            }
        }
    }
}

data class HomeState(
    val userName: String = "",
    val userProfileErrorMessage: String? = null,
    val isSliderLoading: Boolean = false,
    val sliderImages: List<SliderImage> = emptyList(),
    val sliderErrorMessage: String? = null,
    val isWeatherLoading: Boolean = false,
    val weatherInfo: WeatherInfo? = null,
    val weatherErrorMessage: String? = null
)

sealed class HomeAction {
    object LoadInitialData : HomeAction()
    object LoadSliderImages : HomeAction()
    object LoadUserProfile : HomeAction()
    object LoadWeather : HomeAction()
    object RetrySliderImages : HomeAction()
    object RetryWeather : HomeAction()
    object OnReportClick : HomeAction()
    object OnChooseTreeClick : HomeAction()
    object OnSuggestTreeClick : HomeAction()
}
