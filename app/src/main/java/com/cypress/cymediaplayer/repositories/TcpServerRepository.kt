package com.cypress.cymediaplayer.repositories

import android.util.Log
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class TcpServerRepository(
    private val port: Int,
    private val onClientConnected: ((Socket) -> Unit)? = null,
    private val onMessageReceived: ((String) -> Unit)? = null
) {
    private val serverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var serverSocket: ServerSocket? = null

    fun start() {
        serverScope.launch {
            try {
                serverSocket = ServerSocket(port)
                Log.d("TCP", "Server started on port $port")

                while (true) {
                    val client = serverSocket!!.accept()
                    onClientConnected?.invoke(client)
                    Log.d("TCP", "Client connected: ${client.inetAddress}")

                    // Handle each client in a separate coroutine
                    serverScope.launch {
                        handleClient(client)
                    }
                }
            } catch (e: Exception) {
                Log.e("TCP", "Server error: ${e.message}", e)
            }
        }
    }

    private suspend fun handleClient(client: Socket) {
        withContext(Dispatchers.IO) {
            try {
                val input = BufferedReader(InputStreamReader(client.getInputStream()))
                val output = PrintWriter(client.getOutputStream(), true)

                var message: String?
                while (input.readLine().also { message = it } != null) {
                    Log.d("TCP", "Received: $message")
                    onMessageReceived?.invoke(message!!)
                    output.println("Echo: $message")
                }
            } catch (e: Exception) {
                Log.e("TCP", "Client error: ${e.message}", e)
            } finally {
                client.close()
            }
        }
    }

    fun stop() {
        try {
            serverScope.cancel()
            serverSocket?.close()
            Log.d("TCP", "Server stopped")
        } catch (e: Exception) {
            Log.e("TCP", "Error stopping server: ${e.message}", e)
        }
    }
}