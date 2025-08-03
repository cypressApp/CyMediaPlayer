package com.cypress.cymediaplayer.data.local
import java.io.*
import java.net.Socket
import java.net.InetSocketAddress

class TcpClientApi(
    private val timeout: Int = 5000
) {
    private var socket: Socket? = null
    private var writer: BufferedWriter? = null
    private var reader: BufferedReader? = null

    fun connect(serverIp : String , serverPort: Int): Boolean {
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