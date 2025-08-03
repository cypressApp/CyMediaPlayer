package com.cypress.cymediaplayer.data.repositories

import com.cypress.cymediaplayer.common.TcpServerResources
import com.cypress.cymediaplayer.data.local.TcpServerApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

interface RemoteReceiverRepository {
    fun setCommunicationProtocol(protocol: Int)
    fun startListening(port: Int?) : Flow<TcpServerResources<String>>

    fun startListeningBle(port: Int?) : Flow<TcpServerResources<String>>
    fun startListeningTcp(port: Int?) : Flow<TcpServerResources<String>>

}

class RemoteReceiverRepositoryImp(
    var serverTcp: TcpServerApi
) : RemoteReceiverRepository{

    companion object{
        const val WIFI = 0
        const val BLE = 1
    }

    var currentProtocol = WIFI

    override fun setCommunicationProtocol(protocol: Int) {
        when(protocol){
            BLE -> currentProtocol = RemoteControlRepositoryImp.Companion.BLE
            WIFI -> currentProtocol = RemoteControlRepositoryImp.Companion.WIFI
        }
    }

    override fun startListening(port: Int?) : Flow<TcpServerResources<String>> = flow {

        when(currentProtocol){
            BLE -> emitAll(startListeningBle(port))
            WIFI -> emitAll(startListeningTcp(port))
        }
    }

    override fun startListeningBle(port: Int?) : Flow<TcpServerResources<String>> = flow {

    }

    override fun startListeningTcp(port: Int?) : Flow<TcpServerResources<String>> = channelFlow {
        if(port == null) return@channelFlow
        serverTcp.start(port).collect{ result ->
            trySend(result)
        }
    }


}
