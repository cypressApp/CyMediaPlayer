package com.cypress.cymediaplayer.common

sealed class TcpServerResources<T>(val data : T? = null, val message : String? = null)   {

    class ClientConnected<T>(data: T? = null) : TcpServerResources<T>(data)
    class ReceivedData<T>(data: T? = null, message : String) : TcpServerResources<T>(data , message)
    class ServerStopped<T>(data: T? = null) : TcpServerResources<T>(data)

}