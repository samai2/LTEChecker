package com.ale.ltechecker.domain.phonestate

import android.content.Context
import android.net.ConnectivityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PhoneStateInfo(context: Context) {

    val observNetworkCnahged = CoroutineScope(Dispatchers.IO)
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    private val networkChangedCallback = NetworkChangedCallback()

    private val wifiChecker = WiFiStateChecker(context)
    private val lteChecker = LTEStateChecker(context)

    val wifiStatus = MutableStateFlow(wifiChecker.getWiFiStatus())
    val lteTestStatus = MutableStateFlow(LTEState.UNKNOWN)


    init {
        connectivityManager.registerDefaultNetworkCallback(networkChangedCallback)
        observNetworkCnahged.launch {
            networkChangedCallback.networkChangeEvents.collect {
                launch { wifiStatus.emit(wifiChecker.getWiFiStatus()) }
                launch { lteTestStatus.emit(lteChecker.checkLTEState()) }
            }
        }
    }


}