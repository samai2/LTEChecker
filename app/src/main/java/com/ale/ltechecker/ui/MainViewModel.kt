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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
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

    var isTestPassed: Boolean? = null

    private val phoneConnectionsInfo = PhoneStateInfo(app)

    val wifiState: MutableStateFlow<WIFIState>
        get() = phoneConnectionsInfo.wifiStatus

    val lteState: MutableStateFlow<LTEState>
        get() = phoneConnectionsInfo.lteTestStatus

    val isCheckNetworkButtonEnabled: Flow<Boolean>
        get() = wifiState.combine(lteState) { a: WIFIState, b: LTEState ->
            a == WIFIState.ENABLED && b == LTEState.ENABLED
        }

    suspend fun refreshPhoneStateInfo() {
        phoneConnectionsInfo.refreshPhoneStateInfo()
    }

    private val test = TestLTENetworkAvailable(app)

    suspend fun checkLTENetworkAvailable() = withContext(Dispatchers.IO) {
        isTestPassed = test.startTestNetwork() == LTETestStatus.AVAILABLE
        return@withContext isTestPassed ?: false
    }
}