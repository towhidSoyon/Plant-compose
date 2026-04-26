package com.plant.compose.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import com.plant.compose.navigation.AppNavigation
import com.plant.compose.ui.theme.PlantTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantTheme {
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar()
                    }
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
