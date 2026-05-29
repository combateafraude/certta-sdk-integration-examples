package io.certta.sdk.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.caf.sdk.common.jvmshared.constants.CafStage
import io.caf.sdk.liveness.lite.CafFaceLivenessLite
import io.caf.sdk.liveness.lite.api.CafLivenessConfig
import io.caf.sdk.liveness.lite.api.CafUnifiedCallback
import io.caf.sdk.liveness.lite.models.CafUnifiedEvent

@SuppressLint("SetTextI18n")
class LivenessLiteActivity : AppCompatActivity() {
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_liveness_lite)
        resultTextView = findViewById(R.id.resultTextView)

        CafFaceLivenessLite.initialize(
            config = CafLivenessConfig(
                mobileToken = "",
                stage = CafStage.PROD,
                personId = "user-123",
                loading = true,
                executeFaceAuth = false,
                enableSecurity = false,
            )
        )

        findViewById<Button>(R.id.openLivenessLiteButton).setOnClickListener {
            openLivenessLite()
        }
    }

    private fun openLivenessLite() {
        CafFaceLivenessLite.cancelLiveness()
        CafFaceLivenessLite.startLiveness(this, object : CafUnifiedCallback {
            override fun onCancel(event: CafUnifiedEvent.Cancelled) {
                Log.d("LivenessLiteActivity", "onCancel: ")
                resultTextView.text = "${resultTextView.text} \nonCancel"
            }

            override fun onError(event: CafUnifiedEvent.Error) {
                Log.d("LivenessLiteActivity", "onError: $event")
                resultTextView.text = "${resultTextView.text} \nonError"
            }

            override fun onFailure(event: CafUnifiedEvent.Failure) {
                Log.d("LivenessLiteActivity", "onFailure: $event")
                resultTextView.text = "${resultTextView.text} \nonFailure"
            }

            override fun onLoaded(event: CafUnifiedEvent.Loaded) {
                Log.d("LivenessLiteActivity", "onLoaded")
                resultTextView.text = "${resultTextView.text} \nonLoaded"
            }

            override fun onLoading(event: CafUnifiedEvent.Loading) {
                Log.d("LivenessLiteActivity", "onLoading")
                resultTextView.text = "${resultTextView.text} \nonLoading"
            }

            override fun onSuccess(event: CafUnifiedEvent.Success) {
                Log.d("LivenessLiteActivity", "onSuccess: $event")
                resultTextView.text = "${resultTextView.text} \nonSuccess"

            }
        })
    }
}