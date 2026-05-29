package io.certta.sdk.example

import ai.certta.Certta
import ai.certta.config.CerttaConfiguration
import ai.certta.liveness.CerttaLiveness
import ai.certta.liveness.LivenessEvent
import ai.certta.liveness.params.LivenessConfiguration
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.caf.sdk.commons.CafEnvironment

@SuppressLint("SetTextI18n")
class LivenessActivity : AppCompatActivity() {
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_liveness)
        resultTextView = findViewById(R.id.resultTextView)

        findViewById<Button>(R.id.openLivenessButton).setOnClickListener {
            openLiveness()
        }

        configureLiveness()
    }

    private fun configureLiveness() {
        val config = CerttaConfiguration(
            mobileToken = "",
            userId = "user-id",
            securityEnabled = false,
            environment = CafEnvironment.PROD
        )

        Certta.instance.configure(this, config)
        Certta.instance.setLogListener { level: String, message: String ->
            resultTextView.text = String.format(
                "%s\n%s-%s",
                resultTextView.text,
                level,
                message
            )
        }
    }

    private fun openLiveness() {
        val livenessConfiguration = LivenessConfiguration(
            maxRetryAttempts = 0,
            faceAuthEnabled = false,
            showLoading = true,
            useFaceLivenessUi = true
        )
        CerttaLiveness.instance.open(
            livenessConfiguration
        ) { livenessEvent: LivenessEvent? ->
            resultTextView.text = String.format("%s\n%s", resultTextView.text, livenessEvent)
        }
    }
}
