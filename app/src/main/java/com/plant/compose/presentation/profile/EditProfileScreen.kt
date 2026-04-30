package com.plant.compose.presentation.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.navigation.NavController
import com.plant.compose.R
import com.plant.compose.core.ui.observeAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditProfileScreen(
    navController: NavController
) {
    val viewModel = koinViewModel<EditProfileViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    viewModel.event.observeAsEvents { event ->
        when (event) {
            EditProfileEffect.UpdateSuccess -> navController.popBackStack()
            is EditProfileEffect.ShowMessage -> {
                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    EditProfileScreenContent(
        state = state,
        onBack = { navController.popBackStack() },
        onAction = viewModel::onAction
    )
}

@Composable
fun EditProfileScreenContent(
    state: EditProfileState,
    onBack: () -> Unit,
    onAction: (EditProfileAction) -> Unit
){
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onAction(EditProfileAction.ImageSelected(it)) }
    }

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
                .navigationBarsPadding()
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
                        .clickable {
                            onBack()
                        }
                )

                Text(
                    text = "Edit Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.size(24.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4FD12B))
                    }
                }

                // Profile Picture Edit
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2A442A)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = state.selectedImageUri ?: state.image,
                            placeholder = painterResource(id = R.drawable.plant_logo),
                            error = painterResource(id = R.drawable.plant_logo),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    // Camera Overlay Icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4FD12B))
                            .align(Alignment.BottomEnd)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change Photo",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Input Fields
                EditProfileField(
                    label = "Full Name",
                    value = state.name,
                    onValueChange = { onAction(EditProfileAction.NameChanged(it)) }
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                EditProfileField(
                    label = "Email Address",
                    value = state.email,
                    onValueChange = { },
                    enabled = false
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                EditProfileField(
                    label = "Location",
                    value = state.location,
                    onValueChange = { onAction(EditProfileAction.LocationChanged(it)) }
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                EditProfileField(
                    label = "Phone Number",
                    value = state.phoneNumber,
                    onValueChange = { onAction(EditProfileAction.PhoneChanged(it)) }
                )

                Spacer(modifier = Modifier.height(40.dp))
            }

            // Save Button
            Button(
                onClick = {
                    onAction(EditProfileAction.SaveClicked)
                },
                enabled = !state.isUpdating,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FD12B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 16.dp)
            ) {
                if (state.isUpdating) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Save Changes",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EditProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    Column {
        Text(
            text = label,
            color = Color(0xFFA1C398),
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            readOnly = !enabled,
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2A442A),
                unfocusedContainerColor = Color(0xFF2A442A),
                focusedBorderColor = Color(0xFF4FD12B),
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
