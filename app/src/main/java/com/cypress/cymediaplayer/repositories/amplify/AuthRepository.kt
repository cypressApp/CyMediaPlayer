package com.cypress.cymediaplayer.repositories.amplify

import com.cypress.cymediaplayer.common.amplify.AuthResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AuthRepository {

    fun signIn(username : String , password : String) : Flow<AuthResource<String>>
    fun signUp(email : String , username: String , password: String, confirmPass : String): Flow<AuthResource<String>>
    fun sendVerificationCode(code : String): Flow<AuthResource<String>>
}

class AuthRepositoryImp : AuthRepository{

    override fun signIn(username: String, password: String) : Flow<AuthResource<String>> = flow {
        if(username.trim().isEmpty() || password.trim().isEmpty()){
            emit(AuthResource.Error(null, "Please fill all fields"))
            return@flow
        }
        emit(AuthResource.Loading())
        delay(1000)
//        emit(SignInResource.Error(null,"Error"))
        emit(AuthResource.Success(""))
    }



    override fun signUp(email : String , username: String , password: String, confirmPass : String) : Flow<AuthResource<String>> = flow {
        if(email.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty() || confirmPass.trim().isEmpty()){
            emit(AuthResource.Error(null , "Please fill all fields"))
            return@flow
        }
        if(password != confirmPass){
            emit(AuthResource.Error(null , "Password is incorrect"))
            return@flow
        }
        emit(AuthResource.Loading(null))
        delay(1000) // signUp simulation
        emit(AuthResource.Success("Success"))
    }

    override fun sendVerificationCode(code: String): Flow<AuthResource<String>> = flow {

        if(code.trim().isEmpty()){
            emit(AuthResource.Error(null , "Please enter code"))
            return@flow
        }
        emit(AuthResource.Loading())
        delay(1000) // Verification simulation
        emit(AuthResource.Success(""))


    }


}


