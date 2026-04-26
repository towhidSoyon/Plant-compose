package com.plant.compose.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plant.compose.R

@Composable
fun LoginScreen() {
    LoginScreenContent()
}

@Composable
fun LoginScreenContent() {

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
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Spacer(Modifier.height(40.dp)) // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.plant_logo),
                    contentDescription = null,
                )

                Spacer(
                    Modifier.weight(1f)
                )

                Text(
                    text = "Plant",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(40.dp))

            // Email
            Text(
                "Email",
                color = Color(0xFFA1C398),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            GreenTextField(
                placeholder =
                    "Enter your email"
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "Password",
                color = Color(0xFFA1C398),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            GreenTextField(
                placeholder =
                    "Enter your password", isPassword = true
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {},
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FD12B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    "Login",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account? ", color = Color(0xFFA1C398), fontSize = 13.sp)
                // In a real app, this would be a clickable text or button to navigate back
                Text(
                    "Signup",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(Modifier.height(14.dp))
            Text("Forgot Password?", color = Color(0xFFA1C398), fontSize = 13.sp)
            Spacer(Modifier.height(18.dp))
            SocialButton("Sign in with Facebook")
            Spacer(Modifier.height(12.dp))
            SocialButton("Sign in with X")
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GreenTextField(placeholder: String, isPassword: Boolean = false) {
    var value by remember { mutableStateOf("") }
    OutlinedTextField(
        value =
            value,
        onValueChange = { value = it },
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

@Composable
private fun SocialButton(text: String) {
    Button(
        onClick = {},
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A442A)),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) { Text(text, color = Color.White) }
}

