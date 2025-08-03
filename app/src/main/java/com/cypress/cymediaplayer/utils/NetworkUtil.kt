package com.cypress.cymediaplayer.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.Inet4Address

object NetworkUtil {

    fun getWifiIpAddress(context: Context): String? {
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
}