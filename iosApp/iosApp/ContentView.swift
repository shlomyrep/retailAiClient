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
        // Obtain the root view controller from the key window.
        let rootViewController = UIApplication.shared.windows.first { $0.isKeyWindow }?.rootViewController
        // Instantiate and present the ScannerViewController.
        let scannerViewController = ScannerViewController()
        rootViewController?.present(scannerViewController, animated: true)
    }
}
