package com.cypress.cymediaplayer.repositories

import com.cypress.cymediaplayer.common.RemoteControlResources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

interface RemoteControlRepository {

    fun setCommunicationProtocol(protocol: Int)
    suspend fun connect(ipAddress : String?) : Flow<RemoteControlResources<String>>
    fun connectBle() : Flow<RemoteControlResources<String>>
    suspend fun connectTcp(ipAddress : String?) : Flow<RemoteControlResources<String>>
    fun sendCommand(command : String)
    fun sendCommandBle(command: String)
    fun sendCommandTcp(command: String)
    fun scanDevice()
    fun scanDeviceBle()
    fun scanDeviceUdp()

}

class RemoteControlRepositoryImp : RemoteControlRepository{

    companion object{
        const val WIFI = 0
        const val BLE = 1
    }
    var client : TcpClientRepository? = null
    var currentProtocol = WIFI


    override fun setCommunicationProtocol(protocol: Int) {
        when(protocol){
            BLE -> currentProtocol = BLE
            WIFI -> currentProtocol = WIFI
        }
    }

    override suspend fun connect(ipAddress : String?) : Flow<RemoteControlResources<String>> = flow {
        when(currentProtocol){
            BLE-> emitAll(connectBle())
            WIFI-> emitAll(connectTcp(ipAddress!!))
        }
    }

    override fun connectBle() : Flow<RemoteControlResources<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun connectTcp(ipAddress : String?) : Flow<RemoteControlResources<String>> = flow {
        client = TcpClientRepository(ipAddress!!, 1234)

//            withContext(Dispatchers.IO) {
                if (client?.connect() == true) {
                    emit(RemoteControlResources.Connected())
//                    while (true){
//                        client?.sendMessage("Hello Server2")
//                        delay(1000)
//                    }

//                    val response = client.readMessage()
//                    withContext(Dispatchers.Main) {
//                        serverResponse = response ?: "No response from server"
//                    }
//                    client.disconnect()
                } else {
//                    withContext(Dispatchers.Main) {
//                        serverResponse = "Failed to connect"
//                    }
                }
//            }
//        }
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