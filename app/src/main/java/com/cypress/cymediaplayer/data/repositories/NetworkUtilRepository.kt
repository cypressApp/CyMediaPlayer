package com.cypress.cymediaplayer.data.repositories

import android.content.Context
import com.cypress.cymediaplayer.utils.NetworkUtil

interface NetworkUtilRepository {
    fun getWifiIpAddress(): String?
}

class NetworkUtilRepositoryImp(private val context: Context) : NetworkUtilRepository{
    override fun getWifiIpAddress(): String? = NetworkUtil.getWifiIpAddress(context)
}
