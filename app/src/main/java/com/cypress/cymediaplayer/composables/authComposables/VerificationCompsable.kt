package com.cypress.cymediaplayer.composables.authComposables

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.cypress.cymediaplayer.repositories.VideoItem
import com.cypress.cymediaplayer.viewModels.amplify.AuthViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.math.sin

@Composable
fun VerificationComposable(username : String , password: String ,onNavigation : (String , String) -> Unit) {

    var verificationCode by remember { mutableStateOf("") }
    val authViewModel : AuthViewModel = koinViewModel()
    val verificationCodeState by authViewModel.verificationState

    if(verificationCodeState.isSuccess){
        onNavigation(username , password)
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

                if (verificationCodeState.errorMessage.isNotEmpty()) {
                    Text(
                        text = verificationCodeState.errorMessage,
                        color = Color.Red
                    )
                }

                if(verificationCodeState.isLoading){
                    CircularProgressIndicator(
                        color = Color.Blue,
                        strokeWidth = 4.dp
                    )
                }

                Text("59")

                OutlinedTextField(
                    value = verificationCode,
                    onValueChange = { verificationCode = it },
                    label = { Text("Verification Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { authViewModel.sendVerificationCode(verificationCode) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Verify")
                }

                TextButton(onClick = { } ) {
                    Text("Send again")
                }

            }
        }
    }

}