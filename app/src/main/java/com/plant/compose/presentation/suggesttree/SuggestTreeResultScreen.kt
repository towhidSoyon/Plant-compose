package com.plant.compose.presentation.suggesttree

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.plant.compose.R
import com.plant.compose.core.ui.observeAsEvents
import com.plant.compose.domain.model.SuggestedTree
import com.plant.compose.navigation.NavigationEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun SuggestTreeResultScreen(
    navController: androidx.navigation.NavController
) {
    val viewModel = koinViewModel<SuggestTreeResultViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.event.observeAsEvents { event ->
        when (event) {
            is NavigationEvent.PopBackStack -> navController.popBackStack()
            else -> {}
        }
    }
    SuggestTreeResultScreenContent(
        state = state,
        onAction = viewModel::onAction,
        onBack = { viewModel.onAction(SuggestTreeResultAction.OnBackClick) }
    )
}

@Composable
fun SuggestTreeResultScreenContent(
    state: SuggestTreeResultState,
    onAction: (SuggestTreeResultAction) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val selectedTrees = state.treesByCategory[state.selectedCategory].orEmpty()

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
                .verticalScroll(scrollState)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBack() }
                )

                Text(
                    text = "Suggestions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Find Your Perfect Match",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Discover species that resonate with your environment and aspirations.",
                fontSize = 14.sp,
                color = Color(0xFF8fa38f),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4FD12B))
                    }
                }

                state.errorMessage != null -> {
                    SuggestTreeMessageContent(
                        title = state.errorMessage,
                        actionText = "Retry",
                        onActionClick = { onAction(SuggestTreeResultAction.Retry) }
                    )
                }

                state.categories.isEmpty() -> {
                    SuggestTreeMessageContent(title = "No suggested trees found.")
                }

                else -> {
                    CategoryTabs(
                        categories = state.categories,
                        selectedCategory = state.selectedCategory,
                        onCategoryClick = { category ->
                            onAction(SuggestTreeResultAction.SelectCategory(category))
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SectionHeader(state.selectedCategory.orEmpty())
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        selectedTrees.forEach { tree ->
                            SuggestedTreeCard(tree)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White
    )
}

@Composable
fun CategoryTabs(
    categories: List<String>,
    selectedCategory: String?,
    onCategoryClick: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) Color(0xFF4FD12B) else Color(0xFF1E3321))
                    .clickable { onCategoryClick(category) }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = category,
                    color = if (isSelected) Color.Black else Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SuggestedTreeCard(tree: SuggestedTree) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0F1E13))
            .padding(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Gray)
            ) {
                SuggestedTreeImage(
                    imageUrl = tree.suggestedImage,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = tree.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = tree.description.orEmpty(),
                fontSize = 12.sp,
                color = Color(0xFF8fa38f),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun SuggestedTreeImage(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    val painter = rememberAsyncImagePainter(model = imageUrl)

    if (imageUrl.isNullOrBlank()) {
        Image(
            painter = painterResource(id = R.drawable.plant_logo),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(modifier = modifier) {

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.FillBounds
            )

            if (painter.state is AsyncImagePainter.State.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (painter.state is AsyncImagePainter.State.Error) {
                Image(
                    painter = painterResource(id = R.drawable.plant_logo),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun SuggestTreeMessageContent(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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

@Composable
fun MainPlant(title: String, desc: String, zone: String) {
    Box(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0F1E13))
            .padding(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Gray)
            ) {
                // Image placeholder
                Image(
                    painter = painterResource(id = R.drawable.plant_logo),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = desc,
                fontSize = 12.sp,
                color = Color(0xFF8fa38f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun MedicinePlant(name: String, desc: String, price: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F1E13))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Gray)
        ) {
            Image(
                painter = painterResource(id = R.drawable.plant_logo),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = desc, color = Color(0xFF8fa38f), fontSize = 12.sp, maxLines = 1)
        }
    }
}

@Composable
fun IndoorPlant() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0F1E13))
    ) {
        // Image background placeholder
        Image(
            painter = painterResource(id = R.drawable.plant_logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.4f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "Hybrid Poplar",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Reach full maturity in record time with this resilient species.",
                color = Color(0xFFBFCFC4),
                fontSize = 12.sp,
                modifier = Modifier.width(180.dp)
            )


        }
    }
}
