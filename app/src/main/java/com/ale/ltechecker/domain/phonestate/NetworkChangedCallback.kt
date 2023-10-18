package com.ale.ltechecker.domain.phonestate

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class NetworkChangedCallback : ConnectivityManager.NetworkCallback() {
    private val eventCoroutineScope = CoroutineScope(Dispatchers.Default)
    private val _networkChangetEvents = MutableSharedFlow<Unit>()
    val networkChangeEvents: SharedFlow<Unit>
        get() = _networkChangetEvents.asSharedFlow()


    private fun notifyNetworkChanged(){
        eventCoroutineScope.launch {
            _networkChangetEvents.emit(Unit)
        }
    }

    override fun onAvailable(network: Network) {

        super.onAvailable(network)
    }

    override fun onLosing(network: Network, maxMsToLive: Int) {
        notifyNetworkChanged()
        super.onLosing(network, maxMsToLive)
    }

    override fun onLost(network: Network) {
        notifyNetworkChanged()
        super.onLost(network)
    }

    override fun onUnavailable() {
        notifyNetworkChanged()
        super.onUnavailable()
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        notifyNetworkChanged()
        super.onCapabilitiesChanged(network, networkCapabilities)
    }

    override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
        notifyNetworkChanged()
        super.onLinkPropertiesChanged(network, linkProperties)
    }

    override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
        notifyNetworkChanged()
        super.onBlockedStatusChanged(network, blocked)
    }
}