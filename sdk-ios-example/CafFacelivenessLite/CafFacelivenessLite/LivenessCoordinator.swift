import UIKit
import CafFaceLivenessLite

struct LogEntry: Identifiable {
    let id = UUID()
    let message: String
    let kind: Kind

    enum Kind { case info, success, failure }
}

// Bridges the UIKit/delegate-based SDK into SwiftUI via ObservableObject.
final class LivenessCoordinator: NSObject, ObservableObject {
    @Published var logs: [LogEntry] = []
    @Published var enableSecurity: Bool = true

    // SDK is recreated on each start() to pick up the current enableSecurity value.
    private var sdk: CafFaceLivenessLiteSDK!

    override init() {
        super.init()
    }

    func start(mobileToken: String, personId: String) {
        guard let vc = UIApplication.topViewController() else { return }
        sdk = CafFaceLivenessLiteSDK(enableSecurity: enableSecurity)
        sdk.delegate = self
        append("Starting... (security: \(enableSecurity))", kind: .info)
        sdk.startSDK(
            viewController: vc,
            mobileToken: mobileToken,
            personId: personId,
            customLocalization: nil,
            environment: .prod,
            loading: true,
            executeFaceAuth: false
        )
    }

    func clearLogs() { logs.removeAll() }

    private func append(_ message: String, kind: LogEntry.Kind) {
        logs.append(LogEntry(message: message, kind: kind))
    }
}

extension LivenessCoordinator: CafFaceLivenessLiteDelegate {
    func didFinishWithSuccess(livenessResult: CafFaceLivenessLiteResult) {
        DispatchQueue.main.async {
            let preview = livenessResult.signedResponse.map { String($0.prefix(40)) } ?? "-"
            self.append("Success: \(preview)", kind: .success)
        }
    }

    func didFinishWithFailure(type: CafFailureType, description: String?, signedResponse: String?) {
        DispatchQueue.main.async {
            self.append("Failure [\(type)]: \(description ?? "-")", kind: .failure)
        }
    }

    func didFinishWithError(type: CafErrorType, description: String) {
        DispatchQueue.main.async {
            self.append("Error [\(type)]: \(description)", kind: .failure)
        }
    }

    func cancelled() { DispatchQueue.main.async { self.append("Cancelled", kind: .info) } }
    func loading()   { DispatchQueue.main.async { self.append("Loading SDK...", kind: .info) } }
    func loaded()    { DispatchQueue.main.async { self.append("SDK Loaded", kind: .info) } }
}

private extension UIApplication {
    static func topViewController(
        base: UIViewController? = UIApplication.shared.connectedScenes
            .compactMap { ($0 as? UIWindowScene)?.keyWindow }
            .first?.rootViewController
    ) -> UIViewController? {
        if let nav = base as? UINavigationController {
            return topViewController(base: nav.visibleViewController)
        }
        if let tab = base as? UITabBarController {
            return topViewController(base: tab.selectedViewController)
        }
        if let presented = base?.presentedViewController {
            return topViewController(base: presented)
        }
        return base
    }
}
