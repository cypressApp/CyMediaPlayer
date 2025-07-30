package com.cypress.cymediaplayer.common.amplify

sealed class AuthResource<T>(val data : T? = null, val message : String? = null) {

    class Success<T>(data: T) : AuthResource<T>(data)
    class Error<T>(data: T? = null , message: String) : AuthResource<T>(data , message)
    class Loading<T>(data: T? = null) : AuthResource<T>(data)

}