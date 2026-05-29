# CafFaceLivenessLite — Complete Documentation

> **Current version:** 1.2.1

---

## Overview

The CafFaceLivenessLite SDK enables live facial verification with a smaller footprint and simplified API. It detects and rejects spoofing attempts (photos/videos) to protect onboarding and authentication flows.

### Terms & Policies

When using our SDKs, ensure you agree with our [Privacy Policy](https://en.caf.io/politicas/politicas-de-privacidade) and our [Terms and Conditions of Use](https://en.caf.io/politicas/termos-e-condicoes-de-uso).

### SDK Dependencies

CafFaceLivenessLite leverages:

- `iProov Biometrics iOS` 13.1.0

### Runtime Permissions

| Permission | Reason                                                    | Required |
| ---------- | --------------------------------------------------------- | -------- |
| CAMERA     | Capturing the selfie in live facial verification policies | Yes      |

### Footprint and App Size

CafFaceLivenessLite dramatically reduces the binary size compared to CafFaceLiveness while preserving security and analytics.

#### Framework footprint

| Scope | Previous | New | Reduction |
| --- | --- | --- | --- |
| CafFaceLiveness framework only | 29.18 MB | ~0.8 MB | ~97% smaller |
| Total frameworks (CafFaceLiveness + iProov) | 39.90 MB | 10.71 MB | ~73% smaller |

#### Sample .ipa size

| Package | Previous | New | Reduction |
| --- | --- | --- | --- |
| Sample app (.ipa) | 3.6 MB | 1.8 MB | ~50% smaller |

Note: Actual sizes vary by Xcode version, architectures included (arm64/simulator), strip settings, and other app content.

---

## Requirements

| Requirement        | Version |
| ------------------ | ------- |
| Minimum iOS Target | 15.0+   |
| Xcode Version      | 26.0+   |
| Swift Version      | 6.3+   |

---

## Installation

To integrate the **CafFaceLivenessLite SDK** into your iOS project, use Swift Package Manager (SPM) or CocoaPods.

### Swift Package Manager (SPM)

1. Open your project in Xcode.
2. Go to `File` > `Add Packages...`.
3. Enter the repository URL: `https://github.com/combateafraude/caf-ios-sdk`
4. Select the `CafFaceLivenessLite` package or your unified SDK if applicable.
5. Complete integration via the prompts.

### CocoaPods

1. Ensure CocoaPods is installed on your system. If not:

```bash
sudo gem install cocoapods
```

2. Navigate to your project directory.
3. Create or update your Podfile:

```ruby
platform :ios, '15.0'
use_frameworks!

target 'YourAppTarget' do
  pod 'CafSDKiOS/CafFaceLivenessLite', '7.0.0'
end
```

4. Install pods:

```bash
pod install
```

5. Open the generated `.xcworkspace`.

---

## Builder / Configuration

### Creating a Liveness SDK instance

Instantiate `CafFaceLivenessLiteSDK` directly and configure behavior via the constructor and `startSDK` parameters.

### Constructor

```swift
CafFaceLivenessLiteSDK(enableSecurity: Bool = true)
```

| Parameter        | Type | Required | Description                                                                                          |
| ---------------- | ---- | -------- | ---------------------------------------------------------------------------------------------------- |
| `enableSecurity` | Bool | No       | Enables security monitoring: jailbreak detection, instrumentation detection, and dylib monitoring. Default: `true`. Disable only in automated testing environments. |

### startSDK Parameters

| Parameter                 | Type | Required | Description                                                                 |
| ------------------------- | ---- | -------- | --------------------------------------------------------------------------- |
| `mobileToken`            | String | Yes      | Mobile authentication token                                                |
| `personId`               | String | Yes      | Person ID for liveness                                                     |
| `environment`            | CafEnvironment (enum) | No       | `.prod` (default), `.beta`, `.dev`                                         |
| `loading`                | Bool | No       | Shows SDK loading screen (default: `true`)                                  |
| `customLocalization`     | String? | No       | Use a custom strings table/bundle prefix (e.g., `"stringsBundle"`)         |
| `executeFaceAuth`        | Bool | No       | Run FaceAuth after successful liveness (default: `false`)                   |

Note: `customLocalization` maps to the iProov strings table name (omit the `.strings` extension). See the iProov localization guide: [iProov Localization](https://github.com/iProov/ios/wiki/Localization).

### Example

```swift
import CafFaceLivenessLite

final class YourViewController: UIViewController {
    private let sdk = CafFaceLivenessLiteSDK(enableSecurity: true)

    override func viewDidLoad() {
        super.viewDidLoad()
        sdk.delegate = self
    }

    private func start() {
        sdk.startSDK(
            viewController: self,
            mobileToken: "your_mobile_token",
            personId: "your_person_id",
            customLocalization: nil,
            environment: .prod,
            loading: true,
            executeFaceAuth: false
        )
    }
}
```

---

## Starting the SDK

Use `CafFaceLivenessLiteSDK.startSDK` with the required parameters from your view controller.

| Parameter                                  | Required | Description                                               |
| ------------------------------------------ | -------- | --------------------------------------------------------- |
| `viewController: UIViewController`         | Yes      | The presenting view controller                            |
| `mobileToken: String`                      | Yes      | Usage token associated with your CAF account              |
| `personId: String`                         | Yes      | User identifier                                           |
| `customLocalization: String?`              | No       | Strings table name for custom iProov localization (e.g., "stringsBundle") |
| `environment: CafEnvironment`              | No       | `.dev`, `.beta`, `.prod` (default: `.prod`)               |
| `loading: Bool`                            | No       | Show SDK loading screen (default: `true`)                 |
| `executeFaceAuth: Bool`                    | No       | Execute FaceAuth flow after liveness (default: `false`)   |

### Example

```swift
import CafFaceLivenessLite

class ViewController: UIViewController {
    private let sdk = CafFaceLivenessLiteSDK(enableSecurity: true)

    override func viewDidLoad() {
        super.viewDidLoad()
        sdk.delegate = self
    }

    func startLiteLiveness() {
        sdk.startSDK(
            viewController: self,
            mobileToken: "your_mobile_token",
            personId: "your_person_id",
            customLocalization: nil,
            environment: .prod,
            loading: true,
            executeFaceAuth: false
        )
    }
}
```

Localization reference: [iProov Localization](https://github.com/iProov/ios/wiki/Localization)

---

## SDK Lifecycle

### CafFaceLivenessLiteDelegate

The `CafFaceLivenessLiteDelegate` protocol handles key events during the liveness process:

| Event                                                                 | Description                                                              |
| --------------------------------------------------------------------- | ------------------------------------------------------------------------ |
| `didFinishWithSuccess(livenessResult: CafFaceLivenessLiteResult)`     | Called upon successful detection, includes `signedResponse`              |
| `didFinishWithFailure(type: CafFailureType, description: String?, signedResponse: String?)` | Called on business failure with optional signed payload |
| `didFinishWithError(type: CafErrorType, description: String)`         | Called on execution error (permission, network, token, etc.)             |
| `cancelled()`                                                         | User cancelled flow                                                       |
| `loading()`                                                           | SDK is initializing                                                       |
| `loaded()`                                                            | SDK is ready to start (when loading screen disabled)                      |

### Example

```swift
import CafFaceLivenessLite

extension YourViewController: CafFaceLivenessLiteDelegate {
    func didFinishWithSuccess(livenessResult: CafFaceLivenessLiteResult) {
        print("Success: \(livenessResult.signedResponse ?? "-")")
    }

    func didFinishWithFailure(type: CafFailureType, description: String?, signedResponse: String?) {
        print("Failure: type=\(type) desc=\(description ?? "-") signed=\(signedResponse ?? "-")")
    }

    func didFinishWithError(type: CafErrorType, description: String) {
        print("Error: type=\(type) desc=\(description)")
    }

    func cancelled() { print("Cancelled") }
    func loading() { print("Loading") }
    func loaded() { print("Loaded") }
}
```

---

## Result Reference

### CafFaceLivenessLiteResult – Success Event

| Field            | Description                                              |
| ---------------- | -------------------------------------------------------- |
| `signedResponse` | Signed response confirming the genuine liveness capture |

### CafErrorType – Error Handling

Common error types returned via `didFinishWithError(type:description:)`:

| Error                           | Description                                                                 |
|---------------------------------|-----------------------------------------------------------------------------|
| `unsupportedDevice`             | Device does not meet minimum HW/SW requirements                             |
| `cameraPermission`              | Camera permission not granted                                               |
| `networkException`              | Network-related error (no internet, timeout, etc.)                          |
| `serverException`               | Server-side processing error                                                |
| `tokenException`                | Invalid or expired token                                                    |
| `captureAlreadyActiveException` | A previous capture is still active                                          |
| `faceAuthentication`            | Face authentication error                                                   |
| `userTimeoutException`          | User did not complete the capture in time                                   |
| `imageNotFoundException`        | Capture image not found                                                     |
| `tooManyRequestsException`      | Rate limiting / too many requests                                           |
| `invalidResponseException`      | Invalid or malformed server response                                        |
| `permissionException`           | Generic OS permission error                                                 |
| `libraryException`              | Underlying library failure                                                  |
| `unexpectedErrorException`      | Unexpected internal error                                                   |
| `unknownException`              | Unspecified error                                                           |
| `cameraException`               | Camera hardware error                                                       |
| `certificateException`          | Certificate validation failed                                               |
| `invalidOptionsException`       | Invalid SDK configuration options provided                                  |
| `securityException`             | Security violation detected (triggered when `enableSecurity: true` and instrumentation or hooking is detected) |

### CafFailureType – Failure References

Available feedback codes returned via `didFinishWithFailure(type:description:signedResponse:)`:

| Enum value         | Localized description (English)                                                                       | GPA | LA |
|--------------------|-------------------------------------------------------------------------------------------------------|-----|----|
| `unknown`          | Try again                                                                                             | ✅  | ❌ |
| `tooMuchMovement`  | Keep still                                                                                            | ✅  | ❌ |
| `tooBright`        | Move somewhere darker                                                                                 | ✅  | ❌ |
| `tooDark`          | Move somewhere brighter                                                                               | ✅  | ❌ |
| `misalignedFace`   | Keep your face in the oval                                                                            | ✅  | ❌ |
| `faceTooFar`       | Move your face closer to the screen                                                                   | ✅  | ❌ |
| `faceTooClose`     | Move your face farther from the screen                                                                | ✅  | ❌ |
| `sunglasses`       | Remove sunglasses                                                                                     | ✅  | ❌ |
| `systemError`      | System Error                                                                                          | ⚠️  | ✅ |
| `rejected`         | Transaction could not be completed                                                                    | ⚠️  | ✅ |
| `faceNotFound`     | Align your face in the oval and then try to keep still                                                | ⚠️  | ✅ |
| `obscuredFace`     | Make sure your whole face is visible and remove any accessories that might cover your face            | ✅  | ✅ |
| `timeout`          | System timeout                                                                                        | ⚠️  | ✅ |
| `eyewear`          | Remove your eyewear                                                                                   | ⚠️  | ✅ |
| `multipleFaces`    | Ensure only one person is visible                                                                     | ✅  | ✅ |
| `eyesClosed`       | Make sure your eyes are open                                                                          | ✅  | ✅ |
| `userNotFound`     | Transaction could not be completed                                                                    | ⚠️  | ✅ |
| `lightingIssues`   | Make sure your face is well lit and free from glare                                                   | ⚠️  | ✅ |
| `framesBlurry`     | Align your face in the oval and then try to keep still                                                | ⚠️  | ✅ |
| `deviceIssue`      | Try a different device                                                                                | ⚠️  | ✅ |
| `motionIssue`      | Align your face in the oval and then try to keep still                                                | ⚠️  | ✅ |
| `backgroundIssue`  | Move to a different location with a neutral background                                                | ⚠️  | ✅ |
| `deviceRestart`    | Please restart your device and try again                                                              | ⚠️  | ✅ |
| `processingFault`  | Please try again                                                                                      | ⚠️  | ✅ |

Key: ✅ = will be returned, ❌ = will not be returned, ⚠️ = may be returned in the future

---

## Complete Source Example

```swift
import UIKit
import CafFaceLivenessLite

final class MainViewController: UIViewController {
    private let sdk = CafFaceLivenessLiteSDK(enableSecurity: true)

    override func viewDidLoad() {
        super.viewDidLoad()
        sdk.delegate = self

        let startButton = UIButton(type: .system)
        startButton.setTitle("Start Liveness Lite", for: .normal)
        startButton.addTarget(self, action: #selector(startLivenessLite), for: .touchUpInside)
        view.addSubview(startButton)
        // Layout omitted
    }

    @objc private func startLivenessLite() {
        sdk.startSDK(
            viewController: self,
            mobileToken: "mobileToken",
            personId: "personId",
            customLocalization: nil,
            environment: .prod,
            loading: true,
            executeFaceAuth: false
        )
    }
}

extension MainViewController: CafFaceLivenessLiteDelegate {
    func didFinishWithSuccess(livenessResult: CafFaceLivenessLiteResult) {
        print("Success: \(livenessResult.signedResponse ?? "-")")
    }

    func didFinishWithFailure(type: CafFailureType, description: String?, signedResponse: String?) {
        print("Failure: type=\(type) desc=\(description ?? "-") signed=\(signedResponse ?? "-")")
    }

    func didFinishWithError(type: CafErrorType, description: String) {
        print("Error: type=\(type) desc=\(description)")
    }

    func cancelled() { print("Cancelled") }
    func loading() { print("Loading") }
    func loaded() { print("Loaded") }
}
```

---

## FAQ

#### **1. What is the CafFaceLivenessLite SDK and what is its main functionality?**

The Lite SDK provides a smaller, streamlined face liveness verification flow. It detects spoofing attempts to secure onboarding and authentication.

---

#### **2. What are the system requirements for the Lite SDK on iOS?**

Supports iOS 15.0+, Xcode 26.0+, and Swift 6.3+.

---

#### **3. What permissions are required to use the Lite SDK?**

- Camera access (`NSCameraUsageDescription` in `Info.plist`)
- Internet access (ATS exceptions if necessary)

---

#### **4. How do I integrate the Lite SDK into my iOS project?**

Use SPM or CocoaPods to add dependencies, then instantiate `CafFaceLivenessLiteSDK`, set its delegate, and call `startSDK`. See the [Installation](#installation) and [Starting the SDK](#starting-the-sdk) sections above.

---

#### **5. How do I start the Face Liveness verification process?**

1. Set the delegate.
2. Call `startSDK` with `viewController`, `mobileToken`, and `personId`.
3. Handle callbacks in `CafFaceLivenessLiteDelegate`.

---

#### **6. What errors and failures should I handle?**

Handle `didFinishWithError(type:description:)` for execution errors (camera, network, token) and `didFinishWithFailure(type:description:signedResponse:)` for business failures (lighting, alignment). See the [Result Reference](#result-reference) section above.

---

#### **7. Can I customize texts or localization?**

Yes. Provide a `customLocalization` strings table name (omit `.strings`, e.g., `"stringsBundle"`) to `startSDK` to load custom iProov strings. See [iProov Localization](https://github.com/iProov/ios/wiki/Localization).

---

#### **8. Can I chain FaceAuth after liveness?**

Yes. Set `executeFaceAuth: true` in `startSDK`.

---

#### **9. What is the `enableSecurity` parameter and when should I disable it?**

`enableSecurity` is a constructor parameter (default: `true`) that activates security monitoring — jailbreak detection, dylib monitoring, and instrumentation detection. When a violation is detected, `didFinishWithError` is called with `CafErrorType.securityException`.

Disable it (`enableSecurity: false`) only in automated UI testing environments where the test runner triggers false positives. **Keep it enabled in production builds.**

```swift
// Production
let sdk = CafFaceLivenessLiteSDK(enableSecurity: true)

// UI Testing only
let sdk = CafFaceLivenessLiteSDK(enableSecurity: false)
```

---

## Release Notes

### v1.2.1

#### New Features

- **Security Monitoring:** Added `enableSecurity: Bool = true` constructor parameter to `CafFaceLivenessLiteSDK`. When enabled, the SDK performs jailbreak detection, dylib monitoring, and instrumentation detection. Disable only in automated testing environments.
- **New Error Types:** Added four new cases to `CafErrorType`:
  - `cameraException`: Camera hardware error
  - `certificateException`: Certificate validation failed
  - `invalidOptionsException`: Invalid SDK configuration options provided
  - `securityException`: Security violation detected (raised when `enableSecurity: true` and instrumentation or hooking is detected)

#### Distribution

- CocoaPods: `pod 'CafSDKiOS/CafFaceLivenessLite', '7.0.0'`

---

### v1.2.0

#### Updates

* Minimum supported iOS version updated to **15.0+**.
* Updated iProov dependency to **13.1.0**.
* **New Failure Options:** Added new failure cases to `CafFailureType`: `eyewear`, `faceNotFound`, `framesBlurry`, `lightingIssues`, `motionIssue`, `backgroundIssue`, `deviceIssue`, `deviceRestart`, `systemError`, `rejected`, `timeout`, `userNotFound`, `processingFault`.

#### Important Notice

* Reverse proxy support will be discontinued in a future release. A necessary iProov-related update is required to keep iProov working and prevent certificate issues. **After March 15, 2026, Faceliveness and Faceauth services could be unavailable.**

---

### v1.1.0

#### New Features

- **Face Authentication Error Handling:** Added a new `faceAuthentication` case to `CafErrorType` to specifically handle backend errors when `executeFaceAuth` is enabled.

---

### v1.0.0

Initial release of the lightweight face liveness SDK.

#### Highlights

- Smaller footprint with streamlined integration
- Delegate-based lifecycle with success, failure, and error callbacks
- Optional FaceAuth execution after liveness
- Basic localization override via `customLocalization`
