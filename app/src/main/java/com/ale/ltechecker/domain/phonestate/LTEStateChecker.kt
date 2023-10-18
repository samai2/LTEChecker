package com.ale.ltechecker.domain.phonestate

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager

import androidx.core.app.ActivityCompat


class LTEStateChecker(private val context: Context) {

    private val telecomManager: TelephonyManager = context.getSystemService(TelephonyManager::class.java)

    fun checkLTEState(): LTEState {
        return if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (telecomManager.isDataEnabled) LTEState.ENABLED else LTEState.DISABLED
        } else LTEState.UNKNOWN

    }
}

enum class LTEState {
    ENABLED, DISABLED, UNKNOWN
}


