package io.certta.sdk.example;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.caf.sdk.commons.CafUnifiedEvent;

public class MainActivity extends AppCompatActivity implements SdkExampleResultListener {

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

        SdkExampleConfig config = new SdkExampleConfig("", this);

        Button button = findViewById(R.id.startSdkButton);

        button.setOnClickListener(view -> {
            config.buildLiveness().start(this.getApplicationContext());
        });
    }

    @Override
    public void onResult(CafUnifiedEvent event) {
        TextView logTextView = findViewById(R.id.resultButton);
        logTextView.setText(event.toString());
    }
}