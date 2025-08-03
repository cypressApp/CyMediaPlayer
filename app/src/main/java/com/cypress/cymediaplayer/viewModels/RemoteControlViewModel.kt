package com.cypress.cymediaplayer.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cypress.cymediaplayer.common.MainScreenResources
import com.cypress.cymediaplayer.common.TcpClientResources
import com.cypress.cymediaplayer.data.local.dto.QrCodeData
import com.cypress.cymediaplayer.data.repositories.RemoteControlRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RemoteControlViewModel(
    private val repository: RemoteControlRepository
) : ViewModel() {

    private val _remoteControlState = mutableStateOf(RemoteControlState())
    val remoteControlState: State<RemoteControlState> = _remoteControlState
    private val _events = MutableSharedFlow<MainScreenResources>()
    val events = _events.asSharedFlow()

    var commandCounter = mutableIntStateOf(0)
    fun connect(info: QrCodeData){
        viewModelScope.launch(Dispatchers.IO){
            // val tempIp = info.ip//.split(",")
            repository.connect(info.ip).onEach {

            }.collect{ result ->
                when (result) {
                    is TcpClientResources.Connected -> {

                        _remoteControlState.value =
                            _remoteControlState.value.copy(isConnected = true)
                        _events.emit(MainScreenResources.VideoList)
                    }
                    is TcpClientResources.Connecting -> {
                        _remoteControlState.value =
                            _remoteControlState.value.copy(isConnecting = true)
                    }
                    is TcpClientResources.DataReceived -> {
                        _remoteControlState.value =
                            _remoteControlState.value.copy(
                                receivedMessage = result.message.toString()
                            )
                    }
                    is TcpClientResources.Disconnected -> {
                        _remoteControlState.value =
                            _remoteControlState.value.copy(isConnected = false)
                    }
                    is TcpClientResources.Disconnecting -> {
                        _remoteControlState.value =
                            _remoteControlState.value.copy(isDisconnecting = true)
                    }
                }

            }
        }
    }

    fun send(message: String , withCounter : Boolean = true){
        viewModelScope.launch(Dispatchers.IO){
            repository.sendCommand(if(withCounter) "$message ${commandCounter.intValue++}"
                                                else message)
        }
    }


}