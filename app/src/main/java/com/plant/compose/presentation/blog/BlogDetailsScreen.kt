package com.plant.compose.presentation.blog

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plant.compose.R
import com.plant.compose.core.ui.observeAsEvents
import com.plant.compose.navigation.NavigationEvent
import org.koin.androidx.compose.koinViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun BlogDetailsScreen(
    navController: androidx.navigation.NavController,
    blogId: String
) {
    val viewModel = koinViewModel<BlogDetailsViewModel>()

    LaunchedEffect(blogId) {
        viewModel.onAction(BlogDetailsAction.LoadBlog(blogId))
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    viewModel.event.observeAsEvents { event ->
        when (event) {
            is NavigationEvent.PopBackStack -> navController.popBackStack()
            else -> {}
        }
    }
    BlogDetailsScreenContent(
        state = state,
        onBack = { viewModel.onAction(BlogDetailsAction.OnBackClick) }
    )
}

@Composable
fun BlogDetailsScreenContent(
    state: BlogDetailsState,
    onBack: () -> Unit
){
    val scrollState = rememberScrollState()

    val context = LocalContext.current

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
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBack() }
                )

            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Header Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF1A3322))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.plant_logo), // Placeholder
                        contentDescription = null,
                        contentScale = ContentScale.Inside,
                        modifier = Modifier.size(150.dp).align(Alignment.Center)
                    )

                    val imageUrl = state.blog?.imageUrl?.trim().orEmpty()
                    if (imageUrl.isBlank()) {
                        Image(
                            painter = painterResource(R.drawable.plant_logo),
                            contentDescription = null,
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.plant_logo),
                            error = painterResource(R.drawable.plant_logo),
                            onError = { state ->
                                Log.e(
                                    "BlogImage",
                                    "Failed to load blog image: $imageUrl",
                                    state.result.throwable
                                )
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Article Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    // Tag & Date
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFF1E3321),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Forestry",
                                color = Color(0xFF4FD12B),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Oct 24, 2023",
                            color = Color(0xFF8fa38f),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(Color(0xFF8fa38f)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "10 min read",
                            color = Color(0xFF8fa38f),
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = state.blog?.title?:"....",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 36.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Author Info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2A442A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.plant_logo),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "By Towhid Soyon",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Arborist & Researcher",
                                color = Color(0xFF8fa38f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Body Text
                    Text(
                        text = state.blog?.description?:"....",
                        fontSize = 16.sp,
                        color = Color(0xFFBFCFC4),
                        lineHeight = 26.sp,
                        textAlign = TextAlign.Justify
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}
