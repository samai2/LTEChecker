package com.ale.ltechecker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
                    viewModel.checkLTENetworkAvailable()
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private suspend fun drawInterface() {
        binding?.title?.text = "${baseContext.getText(R.string.app_title)} ${BuildConfig.VERSION_NAME}"
        displayTestResult(TestResult.NEW)
    }

    private suspend fun displayTestResult(result: TestResult) = withContext(Dispatchers.Main.immediate) {
       val (text, colour) = when (result) {//?attr/textColorTertiary
            TestResult.NEW -> R.string.not_tested to R.color.black
            TestResult.FALIED -> R.string.test_failed to R.color.white
            TestResult.PASSED -> R.string.test_passed to R.color.test_failed
        }
        binding?.testResult?.text = ContextCompat.getString(this@MainActivity, text)
        binding?.testResult?.backgroundTintList = ContextCompat.getColorStateList(this@MainActivity, colour)

        binding?.progressBar?.visibility = View.GONE
    }
}

enum class TestResult {
    NEW, PASSED, FALIED
}
