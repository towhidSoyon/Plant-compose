package com.plant.compose.presentation.choosetree

import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.plant.compose.domain.model.ChooseTreeInput
import com.plant.compose.domain.model.TreeRecommendation
import com.plant.compose.navigation.NavigationEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChooseTreeResultScreen(
    navController: androidx.navigation.NavController,
    input: ChooseTreeInput
) {
    val viewModel = koinViewModel<ChooseTreeResultViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(input) {
        viewModel.onAction(ChooseTreeResultAction.LoadRecommendations(input))
    }

    viewModel.event.observeAsEvents { event ->
        when (event) {
            is NavigationEvent.PopBackStack -> navController.popBackStack()
            else -> {}
        }
    }
    ChooseTreeResultScreenContent(
        state = state,
        onAction = viewModel::onAction,
        onBack = { viewModel.onAction(ChooseTreeResultAction.OnBackClick) }
    )
}

@Composable
fun ChooseTreeResultScreenContent(
    state: ChooseTreeResultState,
    onAction: (ChooseTreeResultAction) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val visibleRecommendations =
        if (state.selectedCategory == "All") state.recommendations
        else state.recommendations.filter { it.category == state.selectedCategory }

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
                    text = "TREE RECOMMENDATIONS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                // Filters
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.categories) { filter ->
                        FilterChip(
                            text = filter,
                            isSelected = state.selectedCategory == filter,
                            onClick = { onAction(ChooseTreeResultAction.SelectCategory(filter)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF4FD12B))
                        }
                    }

                    state.errorMessage != null -> {
                        ChooseTreeResultMessageContent(
                            title = state.errorMessage,
                            actionText = "Retry",
                            onActionClick = { onAction(ChooseTreeResultAction.Retry) }
                        )
                    }

                    visibleRecommendations.isEmpty() -> {
                        ChooseTreeResultMessageContent(title = "No recommendations found.")
                    }

                    else -> {
                        MainTreeCard(recommendation = visibleRecommendations.first())

                        Spacer(modifier = Modifier.height(20.dp))

                        visibleRecommendations.drop(1).forEach { recommendation ->
                            SecondaryTreeCard(recommendation = recommendation)
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) Color(0xFF6B4226) else Color(0xFF1A3322),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF8fa38f),
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
    }
}

@Composable
fun MainTreeCard(recommendation: TreeRecommendation) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF0F1E13))
            .padding(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
        ) {
            AsyncImage(
                model = recommendation.imageUrl,
                placeholder = painterResource(id = R.drawable.plant_logo),
                error = painterResource(id = R.drawable.plant_logo),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
                
                // Match percentage badge
                Surface(
                    color = Color(0xFF1E3321).copy(alpha = 0.8f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                ) {
                    Text(
                        text = "${recommendation.matchPercentage}% Match",
                        color = Color(0xFF4FD12B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = recommendation.category.uppercase(),
                    color = Color(0xFF4FD12B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(0xFF1A3322), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recommendation.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recommendation.description.orEmpty(),
                fontSize = 14.sp,
                color = Color(0xFF8fa38f),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun SecondaryTreeCard(recommendation: TreeRecommendation) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0F1E13))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 100.dp, height = 80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = recommendation.imageUrl,
                placeholder = painterResource(id = R.drawable.plant_logo),
                error = painterResource(id = R.drawable.plant_logo),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recommendation.category.uppercase(),
                    color = Color(0xFF4FD12B),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${recommendation.matchPercentage}%",
                    color = Color(0xFF6B4226),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = recommendation.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = recommendation.description.orEmpty(),
                fontSize = 12.sp,
                color = Color(0xFF8fa38f),
                maxLines = 2
            )
        }
    }
}

@Composable
fun ChooseTreeResultMessageContent(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0F1E13))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = Color(0xFF8fa38f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            if (actionText != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onActionClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4226)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = actionText, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ProTipBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF071408))
            .border(1.dp, Color(0xFF1E3321), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.Eco,
                contentDescription = null,
                tint = Color(0xFF4FD12B),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "PRO TIP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8fa38f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Based on your coastal location, salt-tolerant species like Teak will show better growth stability over 10 years.",
                    fontSize = 14.sp,
                    color = Color(0xFFBFCFC4),
                    lineHeight = 22.sp
                )
            }
        }
    }
}
