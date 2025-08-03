package com.cypress.cymediaplayer.composables.remoteControlComposables

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.cypress.cymediaplayer.app.app
import com.cypress.cymediaplayer.repositories.TcpServerRepository
import com.cypress.cymediaplayer.repositories.VideoItem
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.net.Inet4Address
import kotlin.random.Random


@Composable
fun QrCodeComposable(onNavigation : (VideoItem) -> Unit , onBackPressed: () -> Unit) {

    var lastCommand by remember { mutableStateOf("") }
    val randomCode by remember { mutableIntStateOf(Random.nextInt(100000, 1000000)) }

    app.le("IP:${getWifiIpAddressNew(LocalContext.current)}")
    val tcpServer = TcpServerRepository(
        port = 1234,
        onClientConnected = { socket ->
            Log.d("TCP", "New client: ${socket.inetAddress}")
        },
        onMessageReceived = { message ->
            lastCommand = message
            Log.d("TCP", "Message: $message")
        }
    )
    tcpServer.start()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                bitmap = generateQRCode("IP:${getWifiIpAddressNew(LocalContext.current)},${randomCode}").asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.fillMaxWidth(0.7f).aspectRatio(1f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = lastCommand,fontSize = 20.sp)
        }

    }

    BackHandler(enabled = true) {
        onBackPressed()
    }

}

fun generateQRCode(text: String): Bitmap {
    val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 400, 400)
    val barcodeEncoder = BarcodeEncoder()
    return barcodeEncoder.createBitmap(bitMatrix)
}

fun getWifiIpAddressNew(context: Context): String? {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return null
    val capabilities = cm.getNetworkCapabilities(network) ?: return null
    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
        val linkProperties = cm.getLinkProperties(network) ?: return null
        val inetAddresses = linkProperties.linkAddresses.map { it.address }
        val ipv4 = inetAddresses.firstOrNull { it is Inet4Address }
        return ipv4?.hostAddress
    }
    return null
}

