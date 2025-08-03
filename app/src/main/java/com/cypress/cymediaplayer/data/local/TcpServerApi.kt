package com.cypress.cymediaplayer.data.local

import android.util.Log
import com.cypress.cymediaplayer.common.TcpServerResources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class TcpServerApi {
    private val serverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var serverSocket: ServerSocket? = null
    private var port = 0

    fun start(port: Int): Flow<TcpServerResources<String>> = channelFlow {
        this@TcpServerApi.port = port

        try {
            serverSocket = ServerSocket(port)

            while (true) {
                val client = serverSocket!!.accept()
                trySend(TcpServerResources.ClientConnected())
                Log.d("TCP", "Client connected: ${client.inetAddress}")

                // Handle each client in a separate coroutine
                launch {
                    handleClient(client).collect { resource ->
                        trySend(resource)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TCP", "Server error: ${e.message}", e)
        }
    }

    fun handleClient(client: Socket): Flow<TcpServerResources<String>> =
        flow {
            try {
                val input = BufferedReader(InputStreamReader(client.getInputStream()))
                val output = PrintWriter(client.getOutputStream(), true)

                var message: String?
                while (input.readLine().also { message = it } != null) {
                    Log.d("TCP", "Received: $message")
                    emit(TcpServerResources.ReceivedData<String>(null, message!!))
                    output.println("Echo: $message")
                }
            } catch (e: Exception) {
                Log.e("TCP", "Client error: ${e.message}", e)
            } finally {
                client.close()
            }
        }.flowOn(Dispatchers.IO) // ✅ ensures proper context


    fun stop() : Flow<TcpServerResources<String>> = flow  {
        try {
            serverScope.cancel()
            serverSocket?.close()
            emit(TcpServerResources.ServerStopped())
            Log.d("TCP", "Server stopped")
        } catch (e: Exception) {
            Log.e("TCP", "Error stopping server: ${e.message}", e)
        }
    }
}