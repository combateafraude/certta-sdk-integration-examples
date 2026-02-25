package io.certta.sdk.example;


import static io.caf.sdk.caffaceliveness.CafFaceLivenessBuilderExtKt.setFaceLivenessConfig;

import android.util.Log;

import java.util.List;

import io.caf.sdk.caffaceliveness.domain.CafFaceLivenessConfig;
import io.caf.sdk.commons.CafEnvironment;
import io.caf.sdk.commons.CafErrorType;
import io.caf.sdk.commons.CafFailureType;
import io.caf.sdk.commons.CafLogLevel;
import io.caf.sdk.commons.CafModuleType;
import io.caf.sdk.commons.CafSdkConfiguration;
import io.caf.sdk.commons.CafSdkProvider;
import io.caf.sdk.commons.CafUnifiedEvent;
import io.caf.sdk.commons.CafUnifiedResponse;
import kotlin.Unit;

interface SdkExampleResultListener {
    void onResult(CafUnifiedEvent event);
}

public class SdkExampleConfig {
    private final String TAG = "CerttaSdkEvent";
    String appKey;
    SdkExampleResultListener resultListener;


    SdkExampleConfig(String appKey, SdkExampleResultListener listener) {
        this.appKey = appKey;
        this.resultListener = listener;
    }

    public CafSdkProvider buildLiveness() {
        CafSdkConfiguration sdkConfiguration = new CafSdkConfiguration(
                /*presentationOrder*/ List.of(CafModuleType.FACE_LIVENESS),
                /*colorConfig*/ null,
                /*nextStepContent*/ null,
                /*waitForAllServices*/ true,
                /*enableTransitionScreens*/ true
//                /*enableSecurityModule*/ false
        );

        CafFaceLivenessConfig cafFaceLivenessConfig = new CafFaceLivenessConfig(
                /*loading*/ true,                  // Displays loading screen during processing
                /*reverseProxyConfig*/ null,
                /*screenCaptureEnabled*/  true,    // Allows screen capture if necessary
                /*debugModeEnabled*/  true,        // Enable debug mode for detailed logs
                /*sdkType*/ null,
                /*executeFaceAuth*/  false,        // Sets whether to execute face authentication.
                /*maxRetryAttempts*/  3,           // Sets the number of times the retry screen will appear.
                /*bridgeConfig*/ null
        );
        setFaceLivenessConfig(sdkConfiguration, cafFaceLivenessConfig);

        CafSdkProvider.Builder sdkBuilder = new CafSdkProvider.Builder(
                /*mobileToken*/  appKey,        // Authentication token
                /*personId*/  "person-id",              // User identifier
                /*environment*/  CafEnvironment.PROD,   // Production environment
                /*configuration*/  sdkConfiguration,    // Global configuration with module order and parameters
                event -> {
                    resultListener.onResult(event);
                    handleEvent(event);
                    return Unit.INSTANCE;
                }  // Return of SDK events
        );
        return sdkBuilder.build();
    }

    private void handleEvent(CafUnifiedEvent event) {
        if (event instanceof CafUnifiedEvent.Loading) {
            showLog("Loading...");
            return;
        }
        if (event instanceof CafUnifiedEvent.Loaded) {
            showLog("Loaded");
            return;
        }
        if (event instanceof CafUnifiedEvent.Success) {
            showSuccessMessage((CafUnifiedEvent.Success) event);
            return;
        }
        if (event instanceof CafUnifiedEvent.Failure) {
            showFailureMessage((CafUnifiedEvent.Failure) event);
            return;
        }
        if (event instanceof CafUnifiedEvent.Error) {
            showErrorMessage((CafUnifiedEvent.Error) event);
            return;
        }
        if (event instanceof CafUnifiedEvent.Cancelled) {
            showLog("Cancelled");
            return;
        }
        if (event instanceof CafUnifiedEvent.Log) {
            showLogMessage((CafUnifiedEvent.Log) event);
        }
    }

    private void showSuccessMessage(CafUnifiedEvent.Success event) {
        List<CafUnifiedResponse> responses = event.getResponse();

        List<String> mappedResponses = List.of();
        responses.forEach(item -> {
            try {
                mappedResponses.add(item.getModuleName() + " " + item.getSignedResponse());
            } catch (Exception e) {
                mappedResponses.add(e.getMessage());

            }
        });

        showLog("Success: " + mappedResponses);
    }

    private void showErrorMessage(CafUnifiedEvent.Error event) {
        CafErrorType type = event.getType();
        String error = event.getDescription();

        showLog("Error: " + "type: " + type + " message: " + error);
    }

    private void showFailureMessage(CafUnifiedEvent.Failure event) {
        String response = event.getResponse();
        CafFailureType type = event.getType();
        String description = event.getDescription();
        showLog("Failure: " + "response: " + response + " type: " + type + " description: " + description);
    }

    private void showLogMessage(CafUnifiedEvent.Log event) {
        CafLogLevel level = event.getLevel();
        String message = event.getMessage();
        showLog("Log: " + "level: " + level + " message: " + message);
    }

    private void showLog(String message) {
        Log.d(TAG, "handleEvent: " + message);
    }
}
