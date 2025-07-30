package com.cypress.cymediaplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cypress.cymediaplayer.composables.authComposables.SignInComposable
import com.cypress.cymediaplayer.composables.authComposables.SignUpComposable
import com.cypress.cymediaplayer.composables.authComposables.VerificationComposable

sealed class AuthScreens{
    data class SignInScreen(val username: String , val password: String) : AuthScreens()
    object SignUpScreen : AuthScreens()
    data class VerificationScreen(val username: String , val password: String) : AuthScreens()
}

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf<AuthScreens>(AuthScreens.SignInScreen("" , "")) }

            when(val authScreen = currentScreen){
                is AuthScreens.SignInScreen -> {
                    SignInComposable(authScreen.username , authScreen.password, onCreateAccount = {
                        currentScreen = AuthScreens.SignUpScreen
                    },
                    onForgotPassword = {

                    },
                    onLogin = {
                        currentScreen = AuthScreens.SignInScreen("" , "")
                    })
                }
                is AuthScreens.SignUpScreen -> {
                    SignUpComposable(onVerificationCode = { username , password ->
                        currentScreen = AuthScreens.VerificationScreen(username , password)
                    } , onBack = {
                        currentScreen = AuthScreens.SignInScreen("" , "")
                    })
                }
                is AuthScreens.VerificationScreen -> {
                    VerificationComposable(authScreen.username , authScreen.password,
                        onNavigation = { username , password ->
                        currentScreen = AuthScreens.SignInScreen(username , password)
                    })
                }
            }


        }
    }
}