package com.cypress.cymediaplayer.data.repositories

import com.cypress.cymediaplayer.app.app
import com.cypress.cymediaplayer.common.TcpServerResources
import com.cypress.cymediaplayer.data.local.TcpServerApi
import com.cypress.cymediaplayer.data.local.dto.QrCodeData
import com.cypress.cymediaplayer.utils.EncryptionUtil
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

interface RemoteReceiverRepository {
    fun setCommunicationProtocol(protocol: Int)
    fun startListening(port: Int?) : Flow<TcpServerResources<String>>

    fun startListeningBle(port: Int?) : Flow<TcpServerResources<String>>
    fun startListeningTcp(port: Int?) : Flow<TcpServerResources<String>>
    fun getQrCodeInfo(): Flow<String>
}

class RemoteReceiverRepositoryImp(
    val networkUtilRepository: NetworkUtilRepository,
    val moshi: Moshi,
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

    override fun getQrCodeInfo(): Flow<String> = flow {
        serverTcp.lastVerificationCode = Random.nextInt(100000, 1000000).toString()
        val lastQrCodeInfo = QrCodeData(networkUtilRepository.getWifiIpAddress() ,
            EncryptionUtil.encrypt(serverTcp.lastVerificationCode) , "")
        try {
            emit(moshi.adapter(QrCodeData::class.java).toJson(lastQrCodeInfo))
        }catch (e : Exception){
            app.le("getQrCodeInfo error: ${e.message.toString()}")
        }

    }


}
