package com.ale.ltechecker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.ale.ltechecker.domain.test.LTETestStatus
import com.ale.ltechecker.domain.test.TestLTENetworkAvailable
import com.ale.ltechecker.domain.phonestate.LTEState
import com.ale.ltechecker.domain.phonestate.PhoneStateInfo
import com.ale.ltechecker.domain.phonestate.WIFIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext


class MainViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return MainViewModel(application) as T
            }
        }
    }

    private val phoneConnectionsInfo = PhoneStateInfo(app)

    val wifiState: MutableStateFlow<WIFIState>
        get() = phoneConnectionsInfo.wifiStatus

    val lteState: MutableStateFlow<LTEState>
        get() = phoneConnectionsInfo.lteTestStatus

    private val test = TestLTENetworkAvailable(app)

    suspend fun checkLTENetworkAvailable() = withContext(Dispatchers.IO) {
        test.startTestNetwork() == LTETestStatus.AVAILABLE
        return@withContext false
    }
}