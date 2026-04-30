package com.plant.compose.presentation.profile

import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.navigation.NavController
import com.plant.compose.R
import com.plant.compose.core.ui.observeAsEvents
import com.plant.compose.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavController
) {
    val viewModel = koinViewModel<ProfileViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    viewModel.event.observeAsEvents { event ->
        when (event) {
            ProfileEffect.LogoutSuccess -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
            is ProfileEffect.ShowMessage -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    ProfileScreenContent(
        state = state,
        onEditProfileClick = { navController.navigate(Screen.EditProfile.route) },
        onAboutClick = { navController.navigate(Screen.About.route) },
        onAction = viewModel::onAction
    )
}

@Composable
fun ProfileScreenContent(
    state: ProfileState,
    onEditProfileClick: () -> Unit,
    onAboutClick: () -> Unit,
    onAction: (ProfileAction) -> Unit
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
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Icon(
                    imageVector = Icons.Default.Edit,
                    tint = Color.Gray,
                    contentDescription = "edit",
                    modifier = Modifier.clickable{
                        onEditProfileClick()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4FD12B))
                }
            }

            // Profile Info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A442A)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = state.user?.image,
                        placeholder = painterResource(id = R.drawable.plant_logo),
                        error = painterResource(id = R.drawable.plant_logo),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = state.user?.name.orEmpty().ifBlank { "Profile" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = state.user?.email.orEmpty(),
                    fontSize = 14.sp,
                    color = Color(0xFF8fa38f)
                )
                Text(
                    text = listOfNotNull(
                        state.user?.phoneNumber?.takeIf { it.isNotBlank() },
                        state.user?.location?.takeIf { it.isNotBlank() }
                    ).joinToString(" - "),
                    fontSize = 14.sp,
                    color = Color(0xFF8fa38f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    value = "12",
                    label = "Trees\nAdded",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "3",
                    label = "Blogs",
                    modifier = Modifier.weight(1f)
                )
            }


            HorizontalDivider(
                thickness =1.dp,
                color = Color(0xFF1E3321),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            ProfileItem(label = "Help"){

            }
            ProfileItem(label = "About"){
                onAboutClick()
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onAction(ProfileAction.LogoutClicked) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4226)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Logout",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
        }
    }
}

@Composable
fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(100.dp)
            .border(1.dp, Color(0xFF1E3321), RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF8fa38f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun ProfileItem(label: String, trailing: @Composable (() -> Unit)? = null, onClick :() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color.White
        )
        if (trailing != null) {
            trailing()
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF8fa38f)
            )
        }
    }
}
