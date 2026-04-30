package com.plant.compose.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.plant.compose.R
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plant.compose.core.ui.observeAsEvents
import com.plant.compose.navigation.NavigationEvent
import org.koin.androidx.compose.koinViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    navController: androidx.navigation.NavController
) {
    val viewModel = koinViewModel<HomeViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.event.observeAsEvents { event ->
        when (event) {
            is NavigationEvent.Navigate -> navController.navigate(event.route)
            is NavigationEvent.PopBackStack -> navController.popBackStack()
        }
    }
    HomeScreenContent(
        state = state,
        onAction = {
            viewModel.onAction(it)
        }
    )
}

@Composable
fun HomeScreenContent(
    state: HomeState,
    onAction: (HomeAction) -> Unit
) {
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
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFA1C398)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.plant_logo), // Using the existing logo resource
                            contentDescription = "Logo",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Plant  ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Greeting
            Text(
                text = "Hello, ${state.userName.ifBlank { "there" }}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Discover the perfect tree for your space and help protect our forests.",
                fontSize = 14.sp,
                color = Color(0xFF8fa38f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            WeatherCard(
                state = state,
                onRetryClick = { onAction(HomeAction.RetryWeather) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            HomeImageSlider(
                state = state,
                onRetryClick = { onAction(HomeAction.RetrySliderImages) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ActionCard(
                        title = "Choose Tree",
                        icon = R.drawable.choose,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onAction(HomeAction.OnChooseTreeClick)
                            }
                    )
                    ActionCard(
                        title = "Suggest Tree",
                        icon = R.drawable.suggestion,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onAction(HomeAction.OnSuggestTreeClick)
                            }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ActionCard(
                        title = "Report Deforestation",
                        icon = R.drawable.deforestation,
                        iconTint = Color(0xFFFF6B6B),
                        iconBg = Color(0xFF3F1D1D),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onAction(HomeAction.OnReportClick)
                            }
                    )
                    ActionCard(
                        title = "Get Reminder",
                        icon = R.drawable.notification,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun WeatherCard(
    state: HomeState,
    onRetryClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F1E13))
            .padding(16.dp)
    ) {
        when {
            state.isWeatherLoading -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF4FD12B),
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Loading Dhaka weather...",
                        color = Color(0xFF8fa38f),
                        fontSize = 14.sp
                    )
                }
            }

            state.weatherErrorMessage != null -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = state.weatherErrorMessage,
                        color = Color(0xFFBFCFC4),
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = onRetryClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.dp, Color(0xFF4FD12B)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Retry",
                            color = Color(0xFF4FD12B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            state.weatherInfo != null -> {
                val weather = state.weatherInfo
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = weather.cityName,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = weather.condition,
                                color = Color(0xFF8fa38f),
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            text = "${weather.temperature.toInt()}°C",
                            color = Color(0xFF4FD12B),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        WeatherMetric(
                            label = "Humidity",
                            value = "${weather.humidity}%",
                            modifier = Modifier.weight(1f)
                        )
                        WeatherMetric(
                            label = "Wind",
                            value = "${weather.windSpeed} km/h",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            else -> {
                Text(
                    text = "Dhaka weather is unavailable.",
                    color = Color(0xFF8fa38f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun WeatherMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1A3322))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = Color(0xFF8fa38f),
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun HomeImageSlider(
    state: HomeState,
    onRetryClick: () -> Unit
) {
    when {
        state.isSliderLoading -> SliderPlaceholder(isLoading = true)

        state.sliderErrorMessage != null -> {
            SliderErrorBanner(
                message = state.sliderErrorMessage,
                onRetryClick = onRetryClick
            )
        }

        state.sliderImages.isEmpty() -> SliderPlaceholder(isLoading = false)

        else -> SliderPager(images = state.sliderImages)
    }
}

@Composable
fun SliderPager(images: List<com.plant.compose.domain.model.SliderImage>) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    LaunchedEffect(images.size) {
        if (images.size <= 1) return@LaunchedEffect

        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % images.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp)),
            pageSpacing = 12.dp,
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1A3322))
            ) {
                AsyncImage(
                    model = images[page].imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.plant_logo),
                    error = painterResource(R.drawable.plant_logo),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(images.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(
                            width = if (pagerState.currentPage == index) 18.dp else 8.dp,
                            height = 8.dp
                        )
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) {
                                Color(0xFF4FD12B)
                            } else {
                                Color(0xFF46604A)
                            }
                        )
                )
            }
        }
    }
}

@Composable
fun SliderPlaceholder(isLoading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1A3322)),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF4FD12B))
        } else {
            Image(
                painter = painterResource(R.drawable.plant_logo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(72.dp)
            )
        }
    }
}

@Composable
fun SliderErrorBanner(
    message: String,
    onRetryClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1A3322))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                color = Color(0xFFBFCFC4),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRetryClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, Color(0xFF4FD12B)),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Retry",
                    color = Color(0xFF4FD12B),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    icon: Int,
    modifier: Modifier = Modifier,
    iconTint: Color = Color(0xFFA1C398),
    iconBg: Color = Color(0xFF1A3322)
) {
    Box(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1E4429))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    //tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun TreeCard(name: String, description: String) {
    Column(
        modifier = Modifier.width(160.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White) // Placeholder for image
        ) {
            // In a real app, this would be an Image with contentScale = ContentScale.Crop
            // For now, we simulate the tree image area

            // Using the plant logo or a color as placeholder if no tree image resource
            Image(
                painter = painterResource(R.drawable.plant_logo),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(80.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Leaf/Info icon placeholder
            // Simple version
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF8fa38f)
            )
        }
    }
}
