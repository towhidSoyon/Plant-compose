package com.plant.compose.presentation.login

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plant.compose.R
import com.plant.compose.core.ui.observeAsEvents
import com.plant.compose.presentation.signup.GreenTextField
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    navController: androidx.navigation.NavController
) {
    val viewModel = koinViewModel<LoginViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    viewModel.event.observeAsEvents { event ->
        when (event) {
            is LoginEvent.Navigate -> {
                navController.navigate(event.route) {
                    event.popUpTo?.let {
                        popUpTo(it) { inclusive = event.inclusive }
                    }
                }
            }

            is LoginEvent.ShowError -> {
                scope.launch { snackbarHostState.showSnackbar(event.message) }
            }
            is LoginEvent.PopBackStack -> navController.popBackStack()
        }
    }
    LoginScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = {
            viewModel.onAction(it)
        }
    )
}

@Composable
fun LoginScreenContent(
    state: LoginState,
    snackbarHostState: SnackbarHostState,
    onAction: (LoginAction) -> Unit
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
                .navigationBarsPadding()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
                "Email",
                color = Color(0xFFA1C398),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            GreenTextField(
                value = state.email,
                onValueChange = { onAction(LoginAction.OnEmailChange(it)) },
                placeholder = "Enter your email"
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
                onValueChange = { onAction(LoginAction.OnPasswordChange(it)) },
                placeholder = "Enter your password",
                isPassword = true
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    onAction(LoginAction.OnLoginClick)
                },
                enabled = !state.isLoading,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FD12B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    if (state.isLoading) "Logging in..." else "Login",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Forgot Password?", color = Color(0xFFA1C398), fontSize = 13.sp)
                Row(
                    modifier = Modifier.clickable {
                        onAction(LoginAction.OnSignupClick)
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Don't have an account? ", color = Color(0xFFA1C398), fontSize = 13.sp)
                    Text(
                        "Signup",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
