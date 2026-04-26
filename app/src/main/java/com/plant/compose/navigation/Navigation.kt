package com.plant.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.plant.compose.ui.BlogScreen
import com.plant.compose.ui.HomeScreen
import com.plant.compose.ui.LoginScreen
import com.plant.compose.ui.SignupScreen
import com.plant.compose.ui.SplashScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen {
                navController.navigate("blog") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
        composable("home") { HomeScreen() }
        composable("blog") { BlogScreen() }
        composable("login") { LoginScreen() }
        composable("signup") { SignupScreen() }

        /*
        composable("faceDetection") { FaceDetectionScreen(navController) }
        composable("objectDetection") { ObjectDetectionScreen(navController) }
        composable("smartReply") { SmartReplyScreen(navController) }
        composable("languageId") { LanguageIdScreen(navController) }
        composable("barcodeScanner") { BarcodeScannerScreen(navController) }*/
    }
}