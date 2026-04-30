package com.plant.compose.presentation.claimdeforestation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.plant.compose.core.ui.observeAsEvents
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ClaimDeforestationScreen(
    navController: androidx.navigation.NavController
) {
    val viewModel = koinViewModel<ClaimDeforestationViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    viewModel.event.observeAsEvents { event ->
        when (event) {
            is ClaimDeforestationEffect.PopBackStack -> navController.popBackStack()
            is ClaimDeforestationEffect.ShowMessage -> {
                scope.launch { snackbarHostState.showSnackbar(event.message) }
            }
            is ClaimDeforestationEffect.SubmitSuccess -> {
                scope.launch {
                    snackbarHostState.showSnackbar("Report submitted successfully.")
                    navController.popBackStack()
                }
            }
        }
    }

    ClaimDeforestationScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
        onBack = { viewModel.onAction(ClaimDeforestationAction.OnBackClick) },
        onSubmit = { viewModel.onAction(ClaimDeforestationAction.OnSubmitClick) }
    )
}

@Composable
fun ClaimDeforestationScreenContent(
    state: ClaimDeforestationState,
    snackbarHostState: SnackbarHostState,
    onAction: (ClaimDeforestationAction) -> Unit,
    onBack: () -> Unit,
    onSubmit: () -> Unit
){
    val scrollState = rememberScrollState()
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onAction(ClaimDeforestationAction.ImageSelected(it)) }
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
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 72.dp)
        )

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
                        .clickable { onBack() }
                )

                Text(
                    text = "Report Deforestation",
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
                    .verticalScroll(scrollState)
            ) {

                OutlinedTextField(
                    value = state.description,
                    onValueChange = { onAction(ClaimDeforestationAction.DescriptionChanged(it)) },
                    placeholder = { Text("Describe your problems..", color = Color(0xFF8fa38f)) },
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1A3322),
                        unfocusedContainerColor = Color(0xFF1A3322),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))


                OutlinedTextField(
                    value = state.location,
                    onValueChange = { onAction(ClaimDeforestationAction.LocationChanged(it)) },
                    placeholder = { Text("Location", color = Color(0xFF8fa38f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1A3322),
                        unfocusedContainerColor = Color(0xFF1A3322),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Add Image Dashed Area
                val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .drawBehind {
                            drawRoundRect(
                                color = Color(0xFF4FD12B).copy(alpha = 0.5f),
                                style = stroke,
                                cornerRadius = CornerRadius(24.dp.toPx())
                            )
                        }
                        .clip(RoundedCornerShape(24.dp))
                        .clickable(enabled = !state.isSubmitting) {
                            imagePickerLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.selectedImageUri != null) {
                        AsyncImage(
                            model = state.selectedImageUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Add Image",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Upload an image of the deforestation",
                                color = Color(0xFF8fa38f),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                state.submitErrorMessage?.let { message ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = message,
                        color = Color(0xFFFF8A80),
                        fontSize = 13.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Submit Button
            Button(
                onClick = onSubmit,
                enabled = !state.isSubmitting,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8CD178)), // Matching design light green
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 16.dp)
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = "Submit Report",
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
fun SmallActionButton(text: String, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFF1A3322),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.clickable { }
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(vertical = 12.dp),
            textAlign = TextAlign.Center
        )
    }
}
