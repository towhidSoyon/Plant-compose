package com.plant.compose.presentation.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plant.compose.R
import com.plant.compose.core.ui.observeAsEvents
import com.plant.compose.navigation.Screen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignupScreen(
    navController: androidx.navigation.NavController
) {
    val viewModel = koinViewModel<SignupViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    viewModel.event.observeAsEvents { event ->
        when (event) {
            is SignupEvent.Navigate -> {
                navController.navigate(event.route) {
                    event.popUpTo?.let {
                        popUpTo(it) { inclusive = event.inclusive }
                    }
                }
            }

            SignupEvent.SignupSuccess -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Signup.route) { inclusive = true }
                }
            }

            is SignupEvent.ShowError -> {
                scope.launch { snackbarHostState.showSnackbar(event.message) }
            }
            is SignupEvent.PopBackStack -> navController.popBackStack()
        }
    }
    SignupScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = {
            viewModel.onAction(it)
        }
    )
}

@Composable
fun SignupScreenContent(
    state: SignupState,
    snackbarHostState: SnackbarHostState,
    onAction: (SignupAction) -> Unit
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
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0x3322FF00)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter =  painterResource(R.drawable.plant_logo),
                    contentDescription = "Tree Icon",
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Plant",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )


            Spacer(Modifier.height(40.dp))

            Text(
                "Full Name",
                color = Color(0xFFA1C398),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            GreenTextField(
                value = state.name,
                onValueChange = { onAction(SignupAction.NameChanged(it)) },
                placeholder = "Enter your full name"
            )
            Spacer(Modifier.height(20.dp))

            Text(
                "Email",
                color = Color(0xFFA1C398),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            GreenTextField(
                value = state.email,
                onValueChange = { onAction(SignupAction.EmailChanged(it)) },
                placeholder = "Enter your email"
            )
            Spacer(Modifier.height(20.dp))

            Text(
                "Phone Number",
                color = Color(0xFFA1C398),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            GreenTextField(
                value = state.phoneNumber,
                onValueChange = { onAction(SignupAction.PhoneChanged(it)) },
                placeholder = "Enter your phone number"
            )
            Spacer(Modifier.height(20.dp))

            Text(
                "Password",
                color = Color(0xFFA1C398),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            GreenTextField(
                value = state.password,
                onValueChange = { onAction(SignupAction.PasswordChanged(it)) },
                placeholder = "Enter your password",
                isPassword = true
            )

            Spacer(Modifier.height(20.dp))

            Text(
                "Confirm Password",
                color = Color(0xFFA1C398),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            GreenTextField(
                value = state.confirmPassword,
                onValueChange = { onAction(SignupAction.ConfirmPasswordChanged(it)) },
                placeholder = "Confirm your password",
                isPassword = true
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    onAction(SignupAction.SignupClicked)
                },
                enabled = !state.isLoading,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FD12B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Sign Up",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text("Already have an account? ", color = Color(0xFFA1C398), fontSize = 13.sp)
                Text(
                    "Login",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable {
                        onAction(SignupAction.LoginClicked)
                    }
                )
            }

            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
fun GreenTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                placeholder,
                color = Color(0xFF9DB59B)
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF2A442A),
            unfocusedContainerColor = Color(0xFF2A442A),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}
