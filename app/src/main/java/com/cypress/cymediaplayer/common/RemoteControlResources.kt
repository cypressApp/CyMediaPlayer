package com.cypress.cymediaplayer.common

sealed class RemoteControlResources<T>(val data : T? = null , val message : String? = null)  {

    class Connecting<T>(data: T? = null) : RemoteControlResources<T>(data)
    class Disconnecting<T>(data: T? = null) : RemoteControlResources<T>(data)
    class Connected<T>(data: T? = null) : RemoteControlResources<T>(data)
    class Disconnected<T>(data: T? = null) : RemoteControlResources<T>(data)
    class DataReceived<T>(data: T? = null , message: String) : RemoteControlResources<T>(data , message)

}