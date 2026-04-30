package com.plant.compose.navigation

import android.net.Uri
import com.plant.compose.domain.model.ChooseTreeInput

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object ClaimDeforestation : Screen("claim_deforestation")
    object Blog : Screen("blog")
    object BlogDetails : Screen("blog_details/{blogId}") {
        const val BLOG_ID_ARG = "blogId"

        fun createRoute(blogId: String): String {
            return "blog_details/$blogId"
        }
    }
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Contact : Screen("contact")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object ChooseTree : Screen("choose_tree")
    object ChooseTreeResult : Screen(
        "choose_tree_result" +
            "?soilType={soilType}" +
            "&temperature={temperature}" +
            "&location={location}" +
            "&waterSupply={waterSupply}" +
            "&sunlight={sunlight}" +
            "&typeOfPlant={typeOfPlant}"
    ) {
        const val SOIL_TYPE_ARG = "soilType"
        const val TEMPERATURE_ARG = "temperature"
        const val LOCATION_ARG = "location"
        const val WATER_SUPPLY_ARG = "waterSupply"
        const val SUNLIGHT_ARG = "sunlight"
        const val TYPE_OF_PLANT_ARG = "typeOfPlant"

        fun createRoute(input: ChooseTreeInput): String {
            return "choose_tree_result" +
                "?soilType=${Uri.encode(input.soilType)}" +
                "&temperature=${Uri.encode(input.temperature)}" +
                "&location=${Uri.encode(input.location)}" +
                "&waterSupply=${Uri.encode(input.waterSupply)}" +
                "&sunlight=${Uri.encode(input.sunlight)}" +
                "&typeOfPlant=${Uri.encode(input.typeOfPlant.orEmpty())}"
        }
    }
    object SuggestTreeResult : Screen("suggest_tree_result")
    object About : Screen("about")
}
