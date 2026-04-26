package com.plant.compose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plant.compose.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navToHome: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000)
        navToHome()
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF064420), Color(0xFF0B6124))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 40.dp)
        ) {
            Spacer(modifier = Modifier.height(1.dp)) // top spacing for status bar

            // Center Icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0x3322FF00)),
                contentAlignment = Alignment.Center
            ) {
                // Replace with your tree drawable
                Image(
                    painter =  painterResource(R.drawable.plant_logo),
                    contentDescription = "Tree Icon",
                    modifier = Modifier.size(60.dp)
                )
            }

            // Title and subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Plant",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "FOR A BETTER ENVIRONMENT",
                    fontSize = 14.sp,
                    color = Color(0xFFBFCFC4),
                    letterSpacing = 1.2.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Bottom progress + label
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress bar
                LinearProgressIndicator(
                    progress = 0.3f, // set your progress
                    color = Color(0xFF00FF00),
                    trackColor = Color(0x2200FF00),
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(4.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "NATURE FOCUSED TECH",
                    fontSize = 12.sp,
                    color = Color(0xFFBFCFC4)
                )
            }
        }
    }
}