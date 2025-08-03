package com.cypress.cymediaplayer.repositories
import java.io.*
import java.net.Socket
import java.net.InetSocketAddress

class TcpClientRepository(
    private val serverIp: String,
    private val serverPort: Int,
    private val timeout: Int = 5000 // optional timeout
) {
    private var socket: Socket? = null
    private var writer: BufferedWriter? = null
    private var reader: BufferedReader? = null

    fun connect(): Boolean {
        return try {
            socket = Socket()
            socket?.connect(InetSocketAddress(serverIp, serverPort), timeout)
            writer = BufferedWriter(OutputStreamWriter(socket?.getOutputStream()))
            reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun sendMessage(message: String) {
        try {
            writer?.write(message)
            writer?.newLine()
            writer?.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readMessage(): String? {
        return try {
            reader?.readLine()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun disconnect() {
        try {
            reader?.close()
            writer?.close()
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}