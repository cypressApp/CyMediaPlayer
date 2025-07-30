package com.cypress.cymediaplayer.viewModels.amplify

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cypress.cymediaplayer.common.amplify.AuthResource
import com.cypress.cymediaplayer.repositories.amplify.AuthRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AuthViewModel (
    val authRepository: AuthRepository,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _signInState = mutableStateOf(AuthState())
    val signInState: State<AuthState> = _signInState

    var username = mutableStateOf(savedStateHandle["username"] ?: "")

    private val _signUpState = mutableStateOf(AuthState())
    val signUpState: State<AuthState> = _signUpState

    private val _verificationState = mutableStateOf(AuthState())
    val verificationState : State<AuthState> = _verificationState

    fun signIn(username : String , password : String){
        authRepository.signIn(username , password).onEach { result ->
            when(result){
                is AuthResource.Loading -> {
                    _signInState.value = AuthState(isLoading = true)
                }
                is AuthResource.Success -> {
                    _signInState.value = AuthState(isSuccess = true)
                }
                is AuthResource.Error -> {
                    _signInState.value = AuthState(isSuccess = false , errorMessage = result.message.toString())
                }
            }
        }.launchIn(viewModelScope)
    }

    fun signUp(email : String , username: String , password: String, confirmPass : String){
        authRepository.signUp(email, username , password , confirmPass).onEach { result ->
            when(result){
                is AuthResource.Loading ->{
                    _signUpState.value = AuthState(isLoading = true)
                }
                is AuthResource.Success -> {
                    _signUpState.value = AuthState(isSuccess = true)
                }
                is AuthResource.Error -> {
                    _signUpState.value = AuthState(isSuccess = false, errorMessage = result.message.toString())
                }
            }
        }.launchIn(viewModelScope)
    }

    fun sendVerificationCode(code : String){
        authRepository.sendVerificationCode(code).onEach { result ->

            when (result){
                is AuthResource.Loading ->{
                    _verificationState.value = AuthState(isLoading = true)
                }
                is AuthResource.Success -> {
                    _verificationState.value = AuthState(isSuccess = true)
                }
                is AuthResource.Error -> {
                    _verificationState.value = AuthState(isSuccess = false, errorMessage = result.message.toString())
                }
            }

        }.launchIn(viewModelScope)
    }

    fun updateUsername(username: String){
        this.username.value = username
        savedStateHandle["username"] = username
    }

}