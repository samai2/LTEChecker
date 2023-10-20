package com.ale.ltechecker.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
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


        setContentView(infoBinding.root)

        displayInfoToSend()

        setupSendInfoButton()
    }

    private fun setupSendInfoButton() {
        binding?.sendInfo?.setOnClickListener {
            val infoFileUri = getUriToSend(writeTextToFile(binding?.displayInfo?.text.toString())) ?: return@setOnClickListener
            with(Intent(Intent.ACTION_SEND_MULTIPLE)) {
                setDataAndType(infoFileUri, "*/*")
                putExtra(Intent.EXTRA_SUBJECT, "Phone info")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(data))
                this@DisplayCollectedInfoActivity.startActivity(
                    Intent.createChooser(
                        this, this@DisplayCollectedInfoActivity.getString(R.string.choose_email_client)
                    )
                )
                finish()
            }
        }
    }


    private fun displayInfoToSend() {
        val isTestPassed = intent.getBooleanExtra(TEXT_RESULT_EXTRA, false)

        val phoneInfo = PhoneInfo()
        val infoToSend = """
            ${phoneInfo.collectPhoneInfo(isTestPassed)}
        """.trimIndent()
        binding?.displayInfo?.text = infoToSend
    }

    private fun writeTextToFile(jsonText: String): URI? {
        val pathToCacheFile = externalCacheDir?.absolutePath ?: return null
        val infoFile = Path(pathToCacheFile, "report.json")
        infoFile.writeText(jsonText)
        return infoFile.toUri()
    }

    private fun getUriToSend(uri: URI?): Uri? {
        uri ?: return null
        return FileProvider.getUriForFile(
            this, "${BuildConfig.APPLICATION_ID}.provider", File(uri)
        )
    }
}

