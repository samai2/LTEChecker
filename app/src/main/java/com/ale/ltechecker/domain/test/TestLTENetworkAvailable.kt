package com.ale.ltechecker.domain.test

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.CompletableFuture

class TestLTENetworkAvailable(context: Context) {
    companion object {
        const val TEST_DURATION = 2000
        const val STOP_TEST_DELAY = 1000L
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var connectivityManager: ConnectivityManager? = null
    private val request = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build()

    private val lock = Mutex()
    private var feature: CompletableFuture<LTETestStatus>? = null

    init {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            feature?.complete(LTETestStatus.AVAILABLE)
            lock.unlock()
            unregisterNetworkCallback()
        }

        override fun onUnavailable() {
            feature?.complete(LTETestStatus.UNAVAILABLE)
            lock.unlock()
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

    suspend fun startTestNetwork(): LTETestStatus? {
        lock.lock()
        feature = CompletableFuture<LTETestStatus>()

        startTestWatchDog()

        connectivityManager?.requestNetwork(request, networkCallback, TEST_DURATION)
        return feature?.await()
    }

    private fun startTestWatchDog() {
        coroutineScope.launch {
            delay(TEST_DURATION + STOP_TEST_DELAY)
            feature?.let {
                if (!it.isDone) {
                    it.complete(LTETestStatus.UNAVAILABLE)
                }
            }
        }
    }

}


enum class LTETestStatus {
    AVAILABLE, UNAVAILABLE,
}