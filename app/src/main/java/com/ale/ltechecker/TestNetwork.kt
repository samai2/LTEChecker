package com.ale.ltechecker

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestNetwork(context: Context) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var connectivityManager: ConnectivityManager? = null
    private val request = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build()
    private val logsBuffer = StringBuilder()

    init {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {

            unregisterNetworkCallback()
        }

        override fun onUnavailable() {

            unregisterNetworkCallback()
        }
    }

    private fun unregisterNetworkCallback() {
        try {
            coroutineScope.launch {
                connectivityManager?.unregisterNetworkCallback(networkCallback)
            }
        } catch (e: Exception) {
            //don't care
        }
    }

}