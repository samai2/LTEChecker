package com.ale.ltechecker.domain.phonestate

import android.content.Context
import android.net.ConnectivityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhoneStateInfo(context: Context) {

    private val observeNetworkChanged = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    private val networkChangedCallback = NetworkChangedCallback()

    private val wifiChecker = WiFiStateChecker(context)
    private val lteChecker = LTEStateChecker(context)

    val wifiStatus = MutableStateFlow(wifiChecker.getWiFiStatus())
    val lteTestStatus = MutableStateFlow(LTEState.UNKNOWN)

    init {

        connectivityManager.registerDefaultNetworkCallback(networkChangedCallback)
        observeNetworkChanged.launch {
            networkChangedCallback.networkChangeEvents.collect {
                refreshPhoneStateInfo()
            }
        }
        //Durty work around in case when we deal with bug,
        // what we cannot get LTE connection via Connectivity Manager
        observeNetworkChanged.launch {
            while (true) {
                launch { lteTestStatus.emit(lteChecker.checkLTEState()) }
                delay(2000)
            }
        }
    }

    suspend fun refreshPhoneStateInfo() = withContext(Dispatchers.IO) {
        launch { wifiStatus.emit(wifiChecker.getWiFiStatus()) }
        launch { lteTestStatus.emit(lteChecker.checkLTEState()) }
    }
}