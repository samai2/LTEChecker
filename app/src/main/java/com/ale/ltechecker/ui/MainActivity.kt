package com.ale.ltechecker.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ale.ltechecker.BuildConfig
import com.ale.ltechecker.R
import com.ale.ltechecker.ui.TestResult.*
import com.ale.ltechecker.databinding.ActivityMainBinding
import com.ale.ltechecker.domain.phonestate.LTEState
import com.ale.ltechecker.domain.phonestate.WIFIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    companion object {
        const val READ_PHONE_STATE_PERMISSION_REQUEST_CODE = 42
    }

    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }
    private var binding: ActivityMainBinding? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setUpAppTitle()

        binding?.checkNetwork?.setOnClickListener {
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    withContext(Dispatchers.Main.immediate) {
                        binding?.progressBar?.visibility = View.VISIBLE
                    }
                    val result = if (viewModel.checkLTENetworkAvailable()) PASSED else FAILED
                    displayTestResult(result)
                }
            }
        }

        binding?.sendStatistic?.setOnClickListener {
            val displayInfoIntent = DisplayCollectedInfoActivity.getIntent(this, true)
            startActivity(displayInfoIntent)
        }
    }


    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        binding?.clearTestData?.setOnClickListener {
            lifecycleScope.launch {
                displayTestResult(NEW)
            }
        }

        observeLTEStatus()

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PERMISSION_GRANTED) {
            observeWiFiStatus()
        } else {
            requestPermission()
        }
        return super.onCreateView(name, context, attrs)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpAppTitle() {
        binding?.title?.text = "${baseContext.getText(R.string.app_title)} ${BuildConfig.VERSION_NAME}"
    }

    private suspend fun displayTestResult(result: TestResult) = withContext(Dispatchers.Main.immediate) {
        val (text, color) = when (result) {
            NEW -> R.string.not_tested to com.google.android.material.R.attr.colorSecondary
            FAILED -> R.string.test_failed to R.attr.colorError
            PASSED -> R.string.test_passed to R.attr.colorSuccess
        }
        binding?.testResult?.text = ContextCompat.getString(this@MainActivity, text)

        binding?.testResult?.backgroundTintList = this@MainActivity.getColorByAttr(color)
        binding?.progressBar?.visibility = View.GONE
    }

    private fun requestPermission() {
        this.requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), READ_PHONE_STATE_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_PHONE_STATE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                observeWiFiStatus()
            }
        }
    }


    private fun observeWiFiStatus() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.wifiState.collect {
                val (text, color) = when (it) {
                    WIFIState.ENABLED -> R.string.enabled to R.attr.colorSuccess
                    WIFIState.UNKNOWN -> R.string.unknown to com.google.android.material.R.attr.colorSecondary
                    else -> R.string.disabled to R.attr.colorError
                }
                withContext(Dispatchers.Main.immediate) {
                    binding?.wifiStatus?.text = this@MainActivity.getString(text)
                    binding?.wifiStatus?.backgroundTintList = this@MainActivity.getColorByAttr(color)
                }
            }
        }
    }

    private fun observeLTEStatus() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.lteState.collect {
                val (text, color) = when (it) {
                    LTEState.ENABLED -> R.string.enabled to R.attr.colorSuccess
                    LTEState.UNKNOWN -> R.string.unknown to com.google.android.material.R.attr.colorSecondary
                    LTEState.DISABLED -> R.string.disabled to R.attr.colorError
                }
                withContext(Dispatchers.Main.immediate) {
                    binding?.mobileDataStatus?.text = this@MainActivity.getString(text)
                    binding?.mobileDataStatus?.backgroundTintList = this@MainActivity.getColorByAttr(color)
                }
            }
        }
    }
}

enum class TestResult {
    NEW, PASSED, FAILED
}

fun AppCompatActivity.getColorByAttr(attr: Int): ColorStateList {
    var colour: TypedArray? = null
    try {
        val attrs = intArrayOf(attr)
        colour = this.obtainStyledAttributes(R.style.Base_Theme_LTEChecker_DayLayt, attrs)
        return ColorStateList.valueOf(colour.getColor(0, androidx.appcompat.R.attr.colorPrimary))
    } finally {
        colour?.recycle()
    }


}