package com.ale.ltechecker.domain.phoneInfo

import android.os.Build
import org.json.JSONObject

class PhoneInfo {

    fun collectPhoneInfo(testResult: Boolean): String {
        val phoneInfoJSON = JSONObject()
        phoneInfoJSON.put("TEST", testResult)
        phoneInfoJSON.put("BOARD", Build.BOARD)
        phoneInfoJSON.put("BRAND", Build.BRAND)
        phoneInfoJSON.put("MODEL", Build.MODEL)
        phoneInfoJSON.put("DEVICE", Build.DEVICE)
        phoneInfoJSON.put("PRODUCT", Build.PRODUCT)
        phoneInfoJSON.put("ID", Build.ID)
        phoneInfoJSON.put("HARDWARE", Build.HARDWARE)
        phoneInfoJSON.put("BOOTLOADER", Build.BOOTLOADER)
        phoneInfoJSON.put("DISPLAY", Build.DISPLAY)
        phoneInfoJSON.put("MANUFACTURER", Build.MANUFACTURER)
        phoneInfoJSON.put("HOST", Build.HOST)
        phoneInfoJSON.put("VERSION.BASE_OS", Build.VERSION.BASE_OS)
        phoneInfoJSON.put("VERSION.CODENAME", Build.VERSION.CODENAME)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            phoneInfoJSON.put("VERSION.RELEASE_OR_CODENAME", Build.VERSION.RELEASE_OR_CODENAME)
        }

        phoneInfoJSON.put("SUPPORTED_ABIS", Build.SUPPORTED_ABIS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            phoneInfoJSON.put("ODM_SKU", Build.ODM_SKU)
        }
        phoneInfoJSON.put("RADIO", Build.getRadioVersion() ?: "")
        phoneInfoJSON.put("SUPPORTED_ABIS", Build.SUPPORTED_ABIS)
        phoneInfoJSON.put("TAGS", Build.TAGS)

        phoneInfoJSON.put("VERSION.CODENAME", Build.VERSION.CODENAME)
        phoneInfoJSON.put("VERSION.SDK_INT", Build.VERSION.SDK_INT)
        phoneInfoJSON.put("VERSION.RELEASE", Build.VERSION.RELEASE)
        phoneInfoJSON.put("VERSION.INCREMENTAL", Build.VERSION.INCREMENTAL)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            phoneInfoJSON.put("VERSION.MEDIA_PERFORMANCE_CLASS", Build.VERSION.MEDIA_PERFORMANCE_CLASS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            phoneInfoJSON.put("VERSION.RELEASE_OR_CODENAMET", Build.VERSION.RELEASE_OR_CODENAME)
        }
        phoneInfoJSON.put("VERSION.PREVIEW_SDK_INT", Build.VERSION.PREVIEW_SDK_INT)
        phoneInfoJSON.put("VERSION.SECURITY_PATCH", Build.VERSION.SECURITY_PATCH)
        return phoneInfoJSON.toString()
    }

}