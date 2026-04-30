package com.plant.compose.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.plant.compose.navigation.AppNavigation
import com.plant.compose.navigation.Screen
import com.plant.compose.core.components.BottomNavigationBar
import com.plant.compose.presentation.theme.PlantTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val systemBarColor = android.graphics.Color.rgb(10, 26, 13)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(systemBarColor)
        )
        setContent {
            val appBackground = Color(0xFF0A1A0D)
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val shouldShowBottomBar = shouldShowBottomBar(currentRoute)

            PlantTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(appBackground)
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = appBackground,
                        contentColor = Color.White,
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        bottomBar = {
                            if (shouldShowBottomBar) {
                                BottomNavigationBar(navController)
                            }
                        }
                    ) { innerPadding ->
                        val bottomPadding = if (shouldShowBottomBar) {
                            innerPadding.calculateBottomPadding()
                        } else {
                            0.dp
                        }

                        AppNavigation(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(appBackground)
                                .padding(bottom = bottomPadding)
                        )
                    }
                }
            }
        }
    }
}

private val bottomBarRoutes = setOf(
    Screen.Home.route,
    Screen.Blog.route,
    Screen.Contact.route,
    Screen.Profile.route
)

private fun shouldShowBottomBar(route: String?): Boolean {
    return route in bottomBarRoutes
}
