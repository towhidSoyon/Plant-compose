package com.plant.compose.presentation.contact

import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.plant.compose.R
import com.plant.compose.core.ui.observeAsEvents
import com.plant.compose.domain.model.Agriculturist
import com.plant.compose.navigation.NavigationEvent
import org.koin.androidx.compose.koinViewModel


@Composable
fun ContactScreen(
    navController: androidx.navigation.NavController
) {
    val viewModel = koinViewModel<ContactViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    viewModel.event.observeAsEvents { event ->
        when (event) {
            is NavigationEvent.PopBackStack -> navController.popBackStack()
            else -> {}
        }
    }
    ContactScreenContent(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun ContactScreenContent(
    state: ContactState,
    onAction: (ContactAction) -> Unit
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
        ) {
            // Top Bar - Matching HomeScreen structure
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Contact",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // Spacer to balance the back icon (matching the symmetry)
                Spacer(modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                    ContactMessageContent(
                        title = state.errorMessage,
                        actionText = "Retry",
                        onActionClick = { onAction(ContactAction.Retry) }
                    )
                }

                state.agriculturists.isEmpty() -> {
                    ContactMessageContent(title = "No agriculturists found.")
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(
                            items = state.agriculturists,
                            key = { it.id }
                        ) { agriculturist ->
                            ContactListItem(agriculturist)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactListItem(agriculturist: Agriculturist) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Avatar - Styled similar to Logo container in HomeScreen
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A442A)), // Darker green shade
                contentAlignment = Alignment.Center
            ) {
                if (agriculturist.imageUrl.isNullOrBlank()) {
                    Image(
                        painter = painterResource(R.drawable.plant_logo),
                        contentDescription = null,
                        contentScale = ContentScale.Inside,
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    AsyncImage(
                        model = agriculturist.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.plant_logo),
                        error = painterResource(R.drawable.plant_logo),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column {
                Text(
                    text = agriculturist.name.ifBlank { "Unknown agriculturist" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = "Agriculturist",
                    fontSize = 14.sp,
                    color = Color(0xFF8fa38f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ContactButton(
                text = "Email",
                modifier = Modifier.weight(1f)
            )
            ContactButton(
                text = "Call",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ContactMessageContent(
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

@Composable
fun ContactButton(text: String, modifier: Modifier = Modifier) {
    Button(
        onClick = { },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1E3321) // Button background from design
        ),
        modifier = modifier.height(44.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}
