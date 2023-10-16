package com.ale.ltechecker

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ale.ltechecker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }
    private var binding: ActivityMainBinding? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        drawInterface()
    }

    @SuppressLint("SetTextI18n")
    private fun drawInterface() {
        binding?.title?.text = "${baseContext.getText(R.string.app_title)} ${BuildConfig.VERSION_NAME}"
    }
}

