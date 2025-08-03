package com.cypress.cymediaplayer.common

sealed class TcpClientResources<T>(val data : T? = null, val message : String? = null)  {

    class Connecting<T>(data: T? = null) : TcpClientResources<T>(data)
    class Disconnecting<T>(data: T? = null) : TcpClientResources<T>(data)
    class Connected<T>(data: T? = null) : TcpClientResources<T>(data)
    class Disconnected<T>(data: T? = null) : TcpClientResources<T>(data)
    class DataReceived<T>(data: T? = null , message: String) : TcpClientResources<T>(data , message)

}