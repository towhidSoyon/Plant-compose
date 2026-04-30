package com.plant.compose.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import com.plant.compose.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A1A0D))
            .navigationBarsPadding()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem("Home", Icons.Outlined.Home, isSelected = currentRoute == Screen.Home.route) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }
        BottomNavItem("Blog", Icons.AutoMirrored.Outlined.Assignment, isSelected = currentRoute == Screen.Blog.route) {
            navController.navigate(Screen.Blog.route) {
                popUpTo(Screen.Home.route)
            }
        }
        BottomNavItem("Contact", Icons.Outlined.Phone, isSelected = currentRoute == Screen.Contact.route) {
            navController.navigate(Screen.Contact.route) {
                popUpTo(Screen.Home.route)
            }
        }
        BottomNavItem("Profile", Icons.Outlined.Person, isSelected = currentRoute == Screen.Profile.route) {
            navController.navigate(Screen.Profile.route) {
                popUpTo(Screen.Home.route)
            }
        }
    }
}

@Composable
fun BottomNavItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color.White else Color(0xFF8fa38f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else Color(0xFF8fa38f)
        )
    }
}
