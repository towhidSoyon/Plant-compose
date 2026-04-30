package com.plant.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.plant.compose.presentation.about.AboutScreen
import com.plant.compose.presentation.blog.BlogDetailsScreen
import com.plant.compose.presentation.blog.BlogScreen
import com.plant.compose.presentation.choosetree.ChooseTreeScreen
import com.plant.compose.presentation.choosetree.ChooseTreeResultScreen
import com.plant.compose.presentation.suggesttree.SuggestTreeResultScreen
import com.plant.compose.presentation.claimdeforestation.ClaimDeforestationScreen
import com.plant.compose.presentation.contact.ContactScreen
import com.plant.compose.presentation.profile.EditProfileScreen
import com.plant.compose.presentation.home.HomeScreen
import com.plant.compose.presentation.login.LoginScreen
import com.plant.compose.presentation.onboarding.OnboardingScreen
import com.plant.compose.presentation.profile.ProfileScreen
import com.plant.compose.presentation.signup.SignupScreen
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.plant.compose.domain.model.ChooseTreeInput
import com.plant.compose.presentation.splash.SplashScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier){
    NavHost(navController, startDestination = Screen.Splash.route, modifier = modifier) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController)
        }
        composable(Screen.Home.route) { 
            HomeScreen(navController)
        }
        composable(Screen.SuggestTreeResult.route) { SuggestTreeResultScreen(navController) }
        composable(Screen.ChooseTree.route) { 
            ChooseTreeScreen(navController)
        }
        composable(
            route = Screen.ChooseTreeResult.route,
            arguments = listOf(
                navArgument(Screen.ChooseTreeResult.SOIL_TYPE_ARG) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(Screen.ChooseTreeResult.TEMPERATURE_ARG) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(Screen.ChooseTreeResult.LOCATION_ARG) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(Screen.ChooseTreeResult.WATER_SUPPLY_ARG) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(Screen.ChooseTreeResult.SUNLIGHT_ARG) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(Screen.ChooseTreeResult.TYPE_OF_PLANT_ARG) {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val typeOfPlant = backStackEntry.arguments
                ?.getString(Screen.ChooseTreeResult.TYPE_OF_PLANT_ARG)
                .orEmpty()
                .takeIf { it.isNotBlank() }

            ChooseTreeResultScreen(
                navController = navController,
                input = ChooseTreeInput(
                    soilType = backStackEntry.arguments?.getString(Screen.ChooseTreeResult.SOIL_TYPE_ARG).orEmpty(),
                    temperature = backStackEntry.arguments?.getString(Screen.ChooseTreeResult.TEMPERATURE_ARG).orEmpty(),
                    location = backStackEntry.arguments?.getString(Screen.ChooseTreeResult.LOCATION_ARG).orEmpty(),
                    waterSupply = backStackEntry.arguments?.getString(Screen.ChooseTreeResult.WATER_SUPPLY_ARG).orEmpty(),
                    sunlight = backStackEntry.arguments?.getString(Screen.ChooseTreeResult.SUNLIGHT_ARG).orEmpty(),
                    typeOfPlant = typeOfPlant
                )
            )
        }
        composable(Screen.ClaimDeforestation.route) { ClaimDeforestationScreen(navController) }
        composable(Screen.Blog.route) { BlogScreen(navController) }
        composable(
            route = Screen.BlogDetails.route,
            arguments = listOf(navArgument(Screen.BlogDetails.BLOG_ID_ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            BlogDetailsScreen(
                navController = navController,
                blogId = backStackEntry.arguments?.getString(Screen.BlogDetails.BLOG_ID_ARG).orEmpty()
            )
        }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.Signup.route) { SignupScreen(navController) }
        composable(Screen.Contact.route) { ContactScreen(navController) }
        composable(Screen.Profile.route) { 
            ProfileScreen(navController)
        }
        composable(Screen.EditProfile.route) { EditProfileScreen(navController) }
        composable(Screen.About.route) { AboutScreen(navController) }
    }
}
