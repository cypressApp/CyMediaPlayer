package com.cypress.cymediaplayer.composables.authComposables

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.cypress.cymediaplayer.MainActivity
import com.cypress.cymediaplayer.viewModels.amplify.AuthViewModel
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.sin

@Composable
fun SignInComposable(username: String , password: String, onCreateAccount: () -> Unit,
                 onForgotPassword: () -> Unit,
                 onLogin: () -> Unit) {

    val context = LocalContext.current
    val activity = context as? Activity

    val authViewModel : AuthViewModel = koinViewModel()
    val signInState by authViewModel.signInState
    var usernameState by authViewModel.username

    var password by remember { mutableStateOf("") }

    if(signInState.isSuccess){
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        activity?.finish()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            val wavePath = Path()
            val amplitude = 60f
            val frequency = 0.02f
            val height = size.height

            wavePath.moveTo(0f, height / 2)
            for (x in 0..size.width.toInt()) {
                val y = (height / 2) + amplitude * sin(x * frequency).toFloat()
                wavePath.lineTo(x.toFloat(), y)
            }
            wavePath.lineTo(size.width, height)
            wavePath.lineTo(0f, height)
            wavePath.close()

            drawPath(
                path = wavePath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF64B5F6), Color(0xFF1976D2))
                )
            )
        }

        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.9f),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (signInState.errorMessage.isNotEmpty()) {
                    Text(
                        text = signInState.errorMessage,
                        color = Color.Red
                    )
                }

                if(signInState.isLoading){
                    CircularProgressIndicator(
                        color = Color.Blue,
                        strokeWidth = 4.dp
                    )
                }

                OutlinedTextField(
                    value = usernameState,
                    onValueChange = { authViewModel.updateUsername(it) },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { authViewModel.signIn(usernameState , password) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }

                TextButton(onClick = onCreateAccount) {
                    Text("Create New Account")
                }

                TextButton(onClick = onForgotPassword) {
                    Text("Forgot Password?")
                }
            }
        }
    }
}