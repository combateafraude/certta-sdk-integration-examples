package io.certta.sdk.example

import ai.certta.Certta
import ai.certta.Certta.configure
import ai.certta.Certta.setLogListener
import ai.certta.CerttaLogListener
import ai.certta.config.CerttaConfiguration
import ai.certta.liveness.CerttaLiveness
import ai.certta.liveness.CerttaLiveness.open
import ai.certta.liveness.CerttaLivenessListener
import ai.certta.liveness.LivenessEvent
import ai.certta.liveness.params.LivenessConfiguration
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.caf.sdk.commons.CafEnvironment

//import com.combateafraude.documentdetector.input.Document;
//import com.combateafraude.documentdetector.input.DocumentDetectorStep;
//import ai.certta.Certta;
//import ai.certta.config.CerttaConfiguration;
//import io.caf.sdk.common.jvmshared.constants.CafStage;
//import ai.certta.document.CerttaDocumentDetector;
//import ai.certta.document.DocumentDetectorConfiguration;
//import ai.certta.liveness.CerttaLiveness;
//import ai.certta.liveness.params.LivenessConfiguration;
//import io.caf.sdk.commons.CafEnvironment;
//import io.caf.sdk.liveness.lite.CafFaceLivenessLite;
//import io.caf.sdk.liveness.lite.api.CafLivenessConfig;
//import io.caf.sdk.liveness.lite.api.CafUnifiedCallback;
//import io.caf.sdk.liveness.lite.models.CafUnifiedEvent;
class LivenessActivity : AppCompatActivity() {
    var mobileToken: String =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI2ODc2MWM2YjYxOWFjYTAwMDIwYmJhMTYifQ.JrrKtYCGsa7MU5S3_PuOeRlF5Wk39HAtEWY03nYxcHY"

    //            public String mobileToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI2OGE0NzE1N2IwMGIyYWM5ZDcyMTdlM2MiLCJleHRyYVNldHRpbmdzIjp7ImZhY2VBdXRoZW50aWNhdGlvbiI6eyJmYWNlRGV0YWlscyI6ZmFsc2V9LCJsaXZlbmVzcyI6eyJzdXBwbGllciI6eyJuYW1lIjoiUGF5RmFjZSIsInByb2R1Y3QiOiJMaXZlbmVzc1BheUZhY2VFbmhhbmNlZCJ9fX0sInRyYW5zYWN0aW9uSWQiOiIzdmY4ZWQ1UnV6aXlRS2FwQjBUZXMzN3JyNUNyQlh6a0JwajMwRlNpU2VIWngiLCJpYXQiOjE3NzYzNDI1NzIsImV4cCI6MTc3NjQyODk3Mn0.4L-AhObh40V1aRaVmkkE9gp3MCmzu4OQAljohiIb944";
    private var resultTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View?>(R.id.main),
            OnApplyWindowInsetsListener { v: View?, insets: WindowInsetsCompat? ->
                val systemBars = insets!!.getInsets(WindowInsetsCompat.Type.systemBars())
                v!!.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            })

        resultTextView = findViewById<TextView>(R.id.resultTextView)

        val config = CerttaConfiguration(
            mobileToken,  // mobileToken
            "user-id",  // userId
            false,  // securityEnabled
            CafEnvironment.PROD // environment
        )

        Certta.getInstance().configure(this, config)

        findViewById<View?>(R.id.button1).setOnClickListener(View.OnClickListener { v: View? ->
            onButton1Click()
        })

        Certta.getInstance().setLogListener(CerttaLogListener { s: String?, s1: String? ->
            this.runOnUiThread(
                Runnable {
                    resultTextView!!.setText(
                        String.format(
                            "%s\n%s-%s",
                            resultTextView!!.getText(),
                            s,
                            s1
                        )
                    )
                })
        })
    }

    private fun onButton1Click() {
        val livenessConfiguration = LivenessConfiguration(
            0,  // maxRetryAttempts
            false,  // faceAuthEnabled
            true,  // showLoading
            true
        ) // useFaceLivenessUi
        CerttaLiveness.getInstance()
            .open(livenessConfiguration, CerttaLivenessListener { livenessEvent: LivenessEvent? ->
                resultTextView!!.setText(
                    String.format("%s\n%s", resultTextView!!.getText(), livenessEvent)
                )
            })
    }
}
