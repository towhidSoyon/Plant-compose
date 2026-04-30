package com.plant.compose.di

import com.plant.compose.data.PreferenceManager
import com.plant.compose.data.agriculturist.FirebaseAgriculturistDataSource
import com.plant.compose.data.auth.FirebaseAuthDataSource
import com.plant.compose.data.blog.FirebaseBlogDataSource
import com.plant.compose.data.choosetree.FirebaseChooseTreeDataSource
import com.plant.compose.data.deforestation.FirebaseDeforestationReportDataSource
import com.plant.compose.data.profile.FirebaseUserProfileDataSource
import com.plant.compose.data.suggestedtree.FirebaseSuggestedTreeDataSource
import com.plant.compose.data.slider.FirebaseSliderDataSource
import com.plant.compose.data.weather.WeatherApiService
import com.plant.compose.data.weather.WeatherRemoteDataSource
import com.plant.compose.data.repository.AgriculturistRepositoryImpl
import com.plant.compose.data.repository.AuthRepositoryImpl
import com.plant.compose.data.repository.BlogRepositoryImpl
import com.plant.compose.data.repository.ChooseTreeRepositoryImpl
import com.plant.compose.data.repository.DeforestationReportRepositoryImpl
import com.plant.compose.data.repository.SuggestedTreeRepositoryImpl
import com.plant.compose.data.repository.SliderRepositoryImpl
import com.plant.compose.data.repository.UserProfileRepositoryImpl
import com.plant.compose.data.repository.WeatherRepositoryImpl
import com.plant.compose.domain.repository.AgriculturistRepository
import com.plant.compose.domain.repository.AuthRepository
import com.plant.compose.domain.repository.BlogRepository
import com.plant.compose.domain.repository.ChooseTreeRepository
import com.plant.compose.domain.repository.DeforestationReportRepository
import com.plant.compose.domain.repository.SuggestedTreeRepository
import com.plant.compose.domain.repository.SliderRepository
import com.plant.compose.domain.repository.UserProfileRepository
import com.plant.compose.domain.repository.WeatherRepository
import com.plant.compose.presentation.splash.SplashViewModel
import com.plant.compose.presentation.onboarding.OnboardingViewModel
import com.plant.compose.presentation.home.HomeViewModel
import com.plant.compose.presentation.login.LoginViewModel
import com.plant.compose.presentation.signup.SignupViewModel
import com.plant.compose.presentation.blog.BlogViewModel
import com.plant.compose.presentation.blog.BlogDetailsViewModel
import com.plant.compose.presentation.choosetree.ChooseTreeViewModel
import com.plant.compose.presentation.choosetree.ChooseTreeResultViewModel
import com.plant.compose.presentation.suggesttree.SuggestTreeResultViewModel
import com.plant.compose.presentation.claimdeforestation.ClaimDeforestationViewModel
import com.plant.compose.presentation.contact.ContactViewModel
import com.plant.compose.presentation.profile.ProfileViewModel
import com.plant.compose.presentation.profile.EditProfileViewModel
import com.plant.compose.presentation.about.AboutViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { PreferenceManager(get()) }
    single { FirebaseAuth.getInstance() }
    single { FirebaseDatabase.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }
    single<HttpClient> {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
        }
    }
    single { WeatherApiService(get()) }
    single { FirebaseAgriculturistDataSource(get()) }
    single { FirebaseAuthDataSource(get(), get()) }
    single { FirebaseBlogDataSource(get()) }
    single { FirebaseChooseTreeDataSource(get(), get()) }
    single { FirebaseDeforestationReportDataSource(get(), get()) }
    single { FirebaseUserProfileDataSource(get(), get(), get()) }
    single { FirebaseSuggestedTreeDataSource(get()) }
    single { FirebaseSliderDataSource(get()) }
    single { WeatherRemoteDataSource(get()) }
    single<AgriculturistRepository> { AgriculturistRepositoryImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<BlogRepository> { BlogRepositoryImpl(get(), get()) }
    single<ChooseTreeRepository> { ChooseTreeRepositoryImpl(get()) }
    single<DeforestationReportRepository> { DeforestationReportRepositoryImpl(get()) }
    single<SuggestedTreeRepository> { SuggestedTreeRepositoryImpl(get()) }
    single<SliderRepository> { SliderRepositoryImpl(get()) }
    single<UserProfileRepository> { UserProfileRepositoryImpl(get()) }
    single<WeatherRepository> { WeatherRepositoryImpl(get()) }

    viewModel { SplashViewModel(get(), get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { SignupViewModel(get()) }
    viewModel { BlogViewModel(get()) }
    viewModel { BlogDetailsViewModel(get()) }
    viewModel { ChooseTreeViewModel() }
    viewModel { ChooseTreeResultViewModel(get()) }
    viewModel { SuggestTreeResultViewModel(get()) }
    viewModel { ClaimDeforestationViewModel(get(), get()) }
    viewModel { ContactViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { EditProfileViewModel(get()) }
    viewModel { AboutViewModel() }
}
