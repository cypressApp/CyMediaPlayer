package com.cypress.cymediaplayer.data.repositories

import com.cypress.cymediaplayer.common.TcpClientResources
import com.cypress.cymediaplayer.data.local.TcpClientApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

interface RemoteControlRepository {

    fun setCommunicationProtocol(protocol: Int)
    suspend fun connect(ipAddress : String?) : Flow<TcpClientResources<String>>
    fun connectBle() : Flow<TcpClientResources<String>>
    suspend fun connectTcp(ipAddress : String?) : Flow<TcpClientResources<String>>
    fun sendCommand(command : String)
    fun sendCommandBle(command: String)
    fun sendCommandTcp(command: String)
    fun scanDevice()
    fun scanDeviceBle()
    fun scanDeviceUdp()

}

class RemoteControlRepositoryImp(
    var client : TcpClientApi
) : RemoteControlRepository{

    companion object{
        const val WIFI = 0
        const val BLE = 1
    }
    var currentProtocol = WIFI


    override fun setCommunicationProtocol(protocol: Int) {
        when(protocol){
            BLE -> currentProtocol = BLE
            WIFI -> currentProtocol = WIFI
        }
    }

    override suspend fun connect(ipAddress : String?) : Flow<TcpClientResources<String>> = flow {
        when(currentProtocol){
            BLE-> emitAll(connectBle())
            WIFI-> emitAll(connectTcp(ipAddress!!))
        }
    }

    override fun connectBle() : Flow<TcpClientResources<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun connectTcp(ipAddress : String?) : Flow<TcpClientResources<String>> = flow {

        if (client.connect(ipAddress!!, 1234)) {
            emit(TcpClientResources.Connected())

//            val response = client.readMessage()
//            withContext(Dispatchers.Main) {
//                serverResponse = response ?: "No response from server"
//            }
//            client.disconnect()
        }

    }

    override fun sendCommand(command: String) {
        when(currentProtocol){
            BLE-> sendCommandBle(command)
            WIFI-> sendCommandTcp(command)
        }
    }

    override fun sendCommandBle(command: String) {
        TODO("Not yet implemented")
    }

    override fun sendCommandTcp(command: String) {
        client?.sendMessage(command)
    }

    override fun scanDevice() {
        when(currentProtocol){
            BLE-> scanDeviceBle()
            WIFI-> scanDeviceUdp()
        }
    }

    override fun scanDeviceBle() {
        TODO("Not yet implemented")
    }

    override fun scanDeviceUdp() {
        TODO("Not yet implemented")
    }


}