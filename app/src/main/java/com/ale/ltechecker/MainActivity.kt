package com.ale.ltechecker

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ale.ltechecker.TestResult.*
import com.ale.ltechecker.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }
    private var binding: ActivityMainBinding? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        lifecycleScope.launch {
            drawInterface()
        }

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

        binding?.clearTestData?.setOnClickListener {
            lifecycleScope.launch {
                displayTestResult(NEW)
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun drawInterface() {
        binding?.title?.text = "${baseContext.getText(R.string.app_title)} ${BuildConfig.VERSION_NAME}"
    }

    private suspend fun displayTestResult(result: TestResult) = withContext(Dispatchers.Main.immediate) {
        val (text, color) = when (result) {
            NEW -> R.string.not_tested to com.google.android.material.R.attr.colorSecondary
            FAILED -> R.string.test_failed to R.attr.colorError
            PASSED -> R.string.test_passed to R.attr.colourInfoText
        }
        binding?.testResult?.text = ContextCompat.getString(this@MainActivity, text)

        val colourId = this@MainActivity.getColorByAttr(color)
        binding?.testResult?.backgroundTintList = ColorStateList.valueOf(colourId)

        binding?.progressBar?.visibility = View.GONE
    }
}

enum class TestResult {
    NEW, PASSED, FAILED
}

fun AppCompatActivity.getColorByAttr(attr: Int): Int {
    var colour: TypedArray? = null
    try {
        val attrs = intArrayOf(attr)
        colour = this.obtainStyledAttributes(R.style.Base_Theme_LTEChecker_DayLayt, attrs)
        return colour.getColor(0, androidx.appcompat.R.attr.colorPrimary)
    } finally {
        colour?.recycle()
    }
}