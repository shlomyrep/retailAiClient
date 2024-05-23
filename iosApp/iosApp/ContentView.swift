import UIKit
import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.mainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .onAppear {
                ScannerOpenerBridge.shared.openScannerScreenFunc = { (skuRegex: String) in
                    openScannerScreenFromSwift(skuRegex: skuRegex)
                }
                PdfOpenerBridge.shared.openPdfFunc = { (url: String) in
                    openPdfFromSwift(url: url)
                }
                
                DeviceDataBridge.shared.getDeviceData = {
                    fetchDeviceData()
                }
                
            }
            .ignoresSafeArea(.all, edges: .bottom) // Compose has own keyboard handler
    }
}

func fetchDeviceData() {
//     Collect device data
    let uuid = UIDevice.current.identifierForVendor?.uuidString ?? ""
    let username = "YourUsername" // Replace with actual username if available
    let name = UIDevice.current.model
    let fcm = ""
    let version = UIDevice.current.systemVersion
    let deviceType = "iOS"
    let lastInteractionTime = Date().timeIntervalSince1970
    let versionCode = Bundle.main.infoDictionary?["CFBundleVersion"] as? String ?? "0"

    let deviceData = DeviceData(
        uuid: uuid,
        username: username,
        name: name,
        version: version,
        fcm:fcm,
        deviceType: deviceType,
        lastInteractionTime: Int64(lastInteractionTime),
        versionCode: Int32(versionCode) ?? 0
    )

    DeviceDataBridge.shared.handleDeviceDataResult?(deviceData)
    NSLog("TAMIR --> fetchDeviceData -->  : \(uuid)")
    NSLog("TAMIR --> fetchDeviceData -->  : \(username)")
    NSLog("TAMIR --> fetchDeviceData -->  : \(name)")
    NSLog("TAMIR --> fetchDeviceData -->  : \(fcm)")
    NSLog("TAMIR --> fetchDeviceData -->  : \(version)")
    NSLog("TAMIR --> fetchDeviceData -->  : \(deviceType)")
    NSLog("TAMIR --> fetchDeviceData -->  : \(lastInteractionTime)")
    NSLog("TAMIR --> fetchDeviceData -->  : \(versionCode)")
}

func openScannerScreenFromSwift(skuRegex: String) {
    DispatchQueue.main.async {
        let rootViewController = UIApplication.shared.windows.first { $0.isKeyWindow }?.rootViewController
        let scannerViewController = ScannerViewController()
        
        // Set the skuRegex
        scannerViewController.skuRegex = skuRegex
        
        // Set the callback that Kotlin will use to handle the scan result.
        scannerViewController.didFindCode = { scannedCode in
            // Pass the result back to Kotlin using the handleScanResult closure
            ScannerOpenerBridge.shared.handleScanResult?(scannedCode)
            print("Scanned Code: \(scannedCode)")
            NSLog("TAMIR --> openScannerScreenFromSwift --> Scanned Code: \(scannedCode)")
        }
        
        rootViewController?.present(scannerViewController, animated: true)
    }
}

func openPdfFromSwift(url: String) {
    PDFManager.shared.openPdf(url: url)
}


extension ComposeView {
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    class Coordinator: NSObject, UIDocumentInteractionControllerDelegate {
        var parent: ComposeView
        init(_ parent: ComposeView) {
            self.parent = parent
        }

        func documentInteractionControllerViewControllerForPreview(_ controller: UIDocumentInteractionController) -> UIViewController {
            return UIApplication.shared.windows.first { $0.isKeyWindow }?.rootViewController ?? UIViewController()
        }
    }
}
