package com.ale.ltechecker.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ale.ltechecker.BuildConfig
import com.ale.ltechecker.databinding.ActivityDisplayCollectedInfoBinding
import com.ale.ltechecker.domain.phoneInfo.PhoneInfo

class DisplayCollectedInfoActivity : AppCompatActivity() {
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, DisplayCollectedInfoActivity::class.java).apply {
                setPackage(BuildConfig.APPLICATION_ID)
            }
        }
    }

    private var binding: ActivityDisplayCollectedInfoBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val infoBinding = ActivityDisplayCollectedInfoBinding.inflate(layoutInflater)
        binding = infoBinding
        displayInfoToSend(false)
        setContentView(infoBinding.root)
    }


    private fun displayInfoToSend(isTestPassed: Boolean) {
        val phoneInfo = PhoneInfo()
        val infoToSend = """
            TEST RESULT : $isTestPassed
            ----- PHONE INFO----
            ${phoneInfo.collectPhoneInfo()}
        """.trimIndent()
        binding?.displayInfo?.text = infoToSend
    }
}