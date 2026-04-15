package io.certta.sdk.example;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.caf.sdk.certta_document_detector.config.CerttaDocumentSmartCapture;
import io.caf.sdk.certta_document_detector.config.models.DocumentSmartCaptureConfig;
import io.certta.Certta;
import io.certta.config.CerttaConfiguration;
import io.certta.document.CerttaDocumentDetector;
import io.certta.document.DocumentDetectorConfiguration;
import io.certta.liveness.CerttaLiveness;
import io.certta.liveness.params.LivenessConfiguration;

public class MainActivity extends AppCompatActivity {

    public String mobileToken = "";
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        resultTextView = findViewById(R.id.resultTextView);

        var config = new CerttaConfiguration(
                mobileToken,     // mobileToken
                "user-id",      // userId
                false            // securityEnabled
        );

        Certta.getInstance().configure(this, config);

        var button1 = findViewById(R.id.button1);
        var button2 = findViewById(R.id.button2);
        var button3 = findViewById(R.id.button3);

        button1.setOnClickListener(v -> onButton1Click());
        button2.setOnClickListener(v -> onButton2Click());
        button3.setOnClickListener(v -> onButton3Click());

        Certta.getInstance().addLogListener((s, s1) -> {
            this.runOnUiThread(() -> resultTextView.setText(String.format("%s\n%s-%s", resultTextView.getText(), s, s1)));
        });
    }

    @SuppressLint("RestrictedApi")
    private void onButton3Click() {
        var config = new DocumentSmartCaptureConfig();
        CerttaDocumentSmartCapture.getInstance().open(config, documentDetectorEvent -> {
            resultTextView.setText(String.format("%s\n%s", resultTextView.getText(), documentDetectorEvent));
        });
    }

    private void onButton1Click() {
        var livenessConfiguration = new LivenessConfiguration(
                3,      // maxRetryAttempts
                false,  // faceAuthEnabled
                true,   // showLoading
                true);    // useFaceLivenessUi
        CerttaLiveness.getInstance().open(livenessConfiguration, livenessEvent -> {
            resultTextView.setText(String.format("%s\n%s", resultTextView.getText(), livenessEvent));
        });

    }

    @SuppressLint("RestrictedApi")
    private void onButton2Click() {
        var config = new DocumentDetectorConfiguration();
        CerttaDocumentDetector.getInstance().open(config, documentDetectorEvent -> {
            resultTextView.setText(String.format("%s\n%s", resultTextView.getText(), documentDetectorEvent));
        });
    }
}
