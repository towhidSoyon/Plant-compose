package com.plant.compose.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plant.compose.R
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.plant.compose.core.ui.observeAsEvents
import com.plant.compose.navigation.NavigationEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen(
    navController: NavController
) {
    val viewModel = koinViewModel<OnboardingViewModel>()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    viewModel.event.observeAsEvents { event ->
        when (event) {
            is NavigationEvent.Navigate -> {
                navController.navigate(event.route) {
                    event.popUpTo?.let {
                        popUpTo(it) { inclusive = event.inclusive }
                    }
                }
            }
            is NavigationEvent.PopBackStack -> navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1A0D))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true
        ) { page ->
            when (page) {
                0 -> PageView(
                    imageRes = R.drawable.onboard_1, // Replace with proper onboarding image
                    title = "Find the Perfect Tree",
                    description = "Get personalized recommendations for trees that thrive in your location.",
                    buttonText = "Get Started",
                    onButtonClick = { scope.launch { pagerState.animateScrollToPage(1) } }
                )
                1 -> PageView(
                    imageRes = R.drawable.onboard_2, // Replace with proper onboarding image
                    title = "Claim Deforestation",
                    description = "Get rid of deforestation by complaining",
                    buttonText = "Next",
                    onButtonClick = { scope.launch { pagerState.animateScrollToPage(2) } }
                )
                2 -> PageView(
                    imageRes = R.drawable.onboard_3, // Replace with proper onboarding image
                    title = "Join the Tree Community",
                    description = "Report deforestation, share your love for trees, and make a difference.",
                    buttonText = "Login",
                    onButtonClick = { viewModel.onAction(OnboardingAction.OnLogin) }
                )
            }
        }
    }
}

@Composable
fun PageView(
    imageRes: Int,
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = Color(0xFF8fa38f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

        }

        Spacer(modifier = Modifier.weight(1f))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Button(
                onClick = onButtonClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8CD178))
            ) {
                Text(buttonText, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
