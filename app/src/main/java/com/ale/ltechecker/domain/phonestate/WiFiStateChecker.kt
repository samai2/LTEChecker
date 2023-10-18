package com.ale.ltechecker.domain.phonestate

import android.content.Context
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.WIFI_STATE_DISABLED
import android.net.wifi.WifiManager.WIFI_STATE_DISABLING
import android.net.wifi.WifiManager.WIFI_STATE_ENABLED
import android.net.wifi.WifiManager.WIFI_STATE_ENABLING

class WiFiStateChecker(context: Context) {

    private val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun getWiFiStatus(): WIFIState {
        return when (wifiManager.wifiState) {
            WIFI_STATE_DISABLED -> WIFIState.DISABLED
            WIFI_STATE_DISABLING -> WIFIState.DISABLING
            WIFI_STATE_ENABLED -> WIFIState.ENABLED
            WIFI_STATE_ENABLING -> WIFIState.ENABLING
            else -> WIFIState.UNKNOWN
        }
    }
}

//WIFI_STATE_DISABLED, WIFI_STATE_DISABLING, WIFI_STATE_ENABLED, WIFI_STATE_ENABLING, WIFI_STATE_UNKNOWN
enum class WIFIState {
    DISABLED, DISABLING, ENABLED, ENABLING, UNKNOWN
}

