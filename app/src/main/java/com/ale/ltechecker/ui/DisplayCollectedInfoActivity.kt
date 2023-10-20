package com.ale.ltechecker.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.ale.ltechecker.BuildConfig
import com.ale.ltechecker.R
import com.ale.ltechecker.databinding.ActivityDisplayCollectedInfoBinding
import com.ale.ltechecker.domain.phoneInfo.PhoneInfo
import java.io.File
import java.net.URI
import kotlin.io.path.Path
import kotlin.io.path.writeText


class DisplayCollectedInfoActivity : AppCompatActivity() {
    companion object {
        private const val TEXT_RESULT_EXTRA = "test_result"
        fun getIntent(context: Context, isTestPassed: Boolean): Intent {
            return Intent(context, DisplayCollectedInfoActivity::class.java).apply {
                setPackage(BuildConfig.APPLICATION_ID)
                putExtra(TEXT_RESULT_EXTRA, isTestPassed)
            }
        }
    }

    private var binding: ActivityDisplayCollectedInfoBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val infoBinding = ActivityDisplayCollectedInfoBinding.inflate(layoutInflater)
        binding = infoBinding
        val isTestPassed = intent.getBooleanExtra(TEXT_RESULT_EXTRA, false)
        displayInfoToSend(isTestPassed)
        setContentView(infoBinding.root)
        binding?.sendInfo?.setOnClickListener {
            val uriToImage1 = sentInfo(binding?.displayInfo?.text.toString()) ?: return@setOnClickListener
            val uriToImage = FileProvider.getUriForFile(
                this, "${BuildConfig.APPLICATION_ID}.provider", File(uriToImage1)
            )
            with(Intent(Intent.ACTION_SEND_MULTIPLE)) {
                setDataAndType(uriToImage, "*/*")
                putExtra(Intent.EXTRA_SUBJECT, "Phone info")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(data))
                this@DisplayCollectedInfoActivity.startActivity(
                    Intent.createChooser(
                        this, this@DisplayCollectedInfoActivity.getString(R.string.choose_email_client)
                    )
                )
            }
        }
    }


    private fun displayInfoToSend(isTestPassed: Boolean) {
        val phoneInfo = PhoneInfo()
        val infoToSend = """
            ${phoneInfo.collectPhoneInfo(isTestPassed)}
        """.trimIndent()
        binding?.displayInfo?.text = infoToSend
    }

    fun sentInfo(jsonText: String): URI? {
        val pathToCacheFile = externalCacheDir?.absolutePath ?: return null
        val infoFile = Path(pathToCacheFile, "report.json")
        infoFile.writeText(jsonText)
        return infoFile.toUri()
    }
}

