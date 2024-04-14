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
                // Assign the Swift function to the Kotlin object's property.
                ScannerOpenerBridge.shared.openScannerScreenFunc = openScannerScreenFromSwift
                
            }
            .ignoresSafeArea(.all, edges: .bottom) // Compose has own keyboard handler
    }
}


func openScannerScreenFromSwift() {
    DispatchQueue.main.async {
        let rootViewController = UIApplication.shared.windows.first { $0.isKeyWindow }?.rootViewController
        let scannerViewController = ScannerViewController()
        
        // Before presenting the scanner, set the callback that Kotlin will use to handle the scan result.
             scannerViewController.didFindCode = { scannedCode in
                 // Pass the result back to Kotlin using the handleScanResult closure
                 ScannerOpenerBridge.shared.handleScanResult?(scannedCode)
                 print("Scanned Code: \(scannedCode)")
             
              NSLog("TAMIR --> openScannerScreenFromSwift --> Scanned Code: \(scannedCode)")
             }
        
        rootViewController?.present(scannerViewController, animated: true)
    }
}
