package com.plant.compose.presentation.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plant.compose.R
import com.plant.compose.core.ui.observeAsEvents
import com.plant.compose.navigation.NavigationEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun AboutScreen(
    navController: androidx.navigation.NavController
) {
    val viewModel = koinViewModel<AboutViewModel>()

    viewModel.event.observeAsEvents { event ->
        when (event) {
            is NavigationEvent.PopBackStack -> navController.popBackStack()
            else -> {}
        }
    }
    AboutScreenContent(
        onBack = { viewModel.onAction(AboutAction.OnBackClick) }
    )
}

@Composable
fun AboutScreenContent(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1F0F),
                        Color(0xFF0F2A14),
                        Color(0xFF0A1A0D)
                    )
                )
            )
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBack() }
                )

                Text(
                    text = "About Developer",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Developer Info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A442A)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.plant_logo), // Placeholder for developer image
                        contentDescription = "Developer Image",
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Towhidul Islam",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Text(
                    text = "Android Developer",
                    fontSize = 16.sp,
                    color = Color(0xFF4FD12B),
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF0F1E13))
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Passionate Android Developer dedicated to building beautiful, functional, and user-centric mobile experiences using Jetpack Compose. With a focus on clean code and modern design principles, I strive to create impactful applications like Plant-compose to contribute to a greener planet.",
                        fontSize = 15.sp,
                        color = Color(0xFFBFCFC4),
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
