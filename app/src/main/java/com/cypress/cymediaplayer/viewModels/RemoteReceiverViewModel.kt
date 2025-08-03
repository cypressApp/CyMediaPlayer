package com.cypress.cymediaplayer.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cypress.cymediaplayer.common.TcpServerResources
import com.cypress.cymediaplayer.data.repositories.RemoteReceiverRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

class RemoteReceiverViewModel(
    private val remoteReceiverRepository: RemoteReceiverRepository
) : ViewModel() {

    private val _remoteReceiverState = mutableStateOf(RemoteReceiverState())
    val remoteReceiverState: State<RemoteReceiverState> = _remoteReceiverState

    private val _qrCodeInfoState = mutableStateOf("_")
    val qrCodeInfoState: State<String> = _qrCodeInfoState

    fun getQrCodeInfo(){
        viewModelScope.launch(Dispatchers.IO){
            remoteReceiverRepository.getQrCodeInfo().collect { result ->
                _qrCodeInfoState.value = result
            }
        }
    }

    fun start(){
        viewModelScope.launch(Dispatchers.IO){
            remoteReceiverRepository.startListening(1234).collect { result ->
                when(result){
                    is TcpServerResources.ClientConnected -> {
                        _remoteReceiverState.value = _remoteReceiverState.value.copy(
                            isClientConnected = true,
                            receivedMessage = "",
                            isServerStopped = false)
                    }

                    is TcpServerResources.ReceivedData -> {
                        _remoteReceiverState.value = _remoteReceiverState.value.copy(
                            isClientConnected = true,
                            receivedMessage = result.message.toString(),
                            isServerStopped = false)
                    }
                    is TcpServerResources.ServerStopped -> {
                        _remoteReceiverState.value = _remoteReceiverState.value.copy(
                            isServerStopped = true ,
                            receivedMessage = "" ,
                            isClientConnected = false)
                    }

                    is TcpServerResources.ClientVerified -> {
                        _remoteReceiverState.value = _remoteReceiverState.value.copy(
                            isServerStopped = false ,
                            receivedMessage = "" ,
                            isClientConnected = true,
                            isClientVerified = true)
                    }
                }
            }
        }

    }

}