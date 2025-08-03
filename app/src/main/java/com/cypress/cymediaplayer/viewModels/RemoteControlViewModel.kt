package com.cypress.cymediaplayer.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cypress.cymediaplayer.common.MainScreenResources
import com.cypress.cymediaplayer.common.RemoteControlResources
import com.cypress.cymediaplayer.repositories.RemoteControlRepository
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
    fun connect(info: String){
        viewModelScope.launch(Dispatchers.IO){
            val tempIp = info.split(",")
            if(tempIp.size >= 2){
                repository.connect(tempIp[0].replace("IP:" , "")).onEach {

                }.collect{ result ->
                    when (result) {
                        is RemoteControlResources.Connected -> {
                            _remoteControlState.value =
                                _remoteControlState.value.copy(isConnected = true)
                            _events.emit(MainScreenResources.VideoList)
                        }
                        is RemoteControlResources.Connecting -> {
                            _remoteControlState.value =
                                _remoteControlState.value.copy(isConnecting = true)
                        }
                        is RemoteControlResources.DataReceived -> {
                            _remoteControlState.value =
                                _remoteControlState.value.copy(
                                    receivedMessage = result.message.toString()
                                )
                        }
                        is RemoteControlResources.Disconnected -> {
                            _remoteControlState.value =
                                _remoteControlState.value.copy(isConnected = false)
                        }
                        is RemoteControlResources.Disconnecting -> {
                            _remoteControlState.value =
                                _remoteControlState.value.copy(isDisconnecting = true)
                        }
                    }

                }
            }
        }
    }

    fun send(message: String){
        viewModelScope.launch(Dispatchers.IO){
            repository.sendCommand(message)
        }
    }


}