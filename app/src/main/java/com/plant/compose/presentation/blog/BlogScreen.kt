package com.plant.compose.presentation.blog

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.plant.compose.R
import com.plant.compose.core.ui.observeAsEvents
import com.plant.compose.domain.model.Blog
import com.plant.compose.navigation.NavigationEvent
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BlogScreen(
    navController: androidx.navigation.NavController
) {
    val viewModel = koinViewModel<BlogViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.event.observeAsEvents { event ->
        when (event) {
            is NavigationEvent.Navigate -> navController.navigate(event.route)
            is NavigationEvent.PopBackStack -> navController.popBackStack()
        }
    }
    BlogScreenContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun BlogScreenContent(
    state: BlogState,
    onAction: (BlogAction) -> Unit
){
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onAction(BlogAction.OnBackClick) }
                )

                Text(
                    text = "Blog",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // Spacer to balance the back icon
                Spacer(modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4FD12B))
                    }
                }

                state.errorMessage != null -> {
                    BlogMessageContent(
                        title = state.errorMessage,
                        actionText = "Retry",
                        onActionClick = { onAction(BlogAction.Retry) }
                    )
                }

                state.blogs.isEmpty() -> {
                    BlogMessageContent(title = "No blogs found.")
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(
                            items = state.blogs,
                            key = { it.id }
                        ) { blog ->
                            BlogListItem(
                                blog = blog,
                                onClick = { onAction(BlogAction.OnBlogClick(blog.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BlogListItem(blog: Blog, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageUrl = blog.imageUrl?.trim().orEmpty()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Blog Image
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 70.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1A3322)),
            contentAlignment = Alignment.Center
        ) {
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

        Spacer(modifier = Modifier.width(16.dp))

        // Blog Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = blog.title.ifBlank { "Untitled blog" },
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = blog.subtitle(),
                fontSize = 14.sp,
                color = Color(0xFF8fa38f)
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Open",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun BlogMessageContent(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            color = Color(0xFFBFCFC4),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FD12B))
            ) {
                Text(
                    text = actionText,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun Blog.subtitle(): String {
    val author = authorName?.takeIf { it.isNotBlank() }?.let { "By $it" }
    val date = createdAt?.let { millis ->
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(millis))
    }

    return listOfNotNull(author, date).joinToString(" • ").ifBlank {
        description.take(60).ifBlank { "Tap to read more" }
    }
}
