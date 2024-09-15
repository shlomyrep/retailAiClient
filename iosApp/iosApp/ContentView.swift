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
    NSLog("TAMIR --> fetchDeviceData --> Starting data collection")
    
    let uuid = UIDevice.current.identifierForVendor?.uuidString ?? ""
    let name = UIDevice.current.model
    let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "Unknown"
    let deviceType = "iOS"
    let modelName = getDeviceModelName()
    
    NSLog("TAMIR --> fetchDeviceData --> Collected Data - UUID: \(uuid), Name: \(name), Version: \(version), Device Type: \(deviceType), Model Name: \(modelName)")

    let platformData = PlatformData(
        uuid: uuid,
        name: name,
        version: version,
        deviceType: deviceType,
        modelName: modelName
    )
    
    // Confirm the callback is being set and invoked
    if let handleDeviceDataResult = DeviceDataBridge.shared.handleDeviceDataResult {
        NSLog("TAMIR --> fetchDeviceData --> Sending data to Kotlin")
        handleDeviceDataResult(platformData)
    } else {
        NSLog("TAMIR --> fetchDeviceData --> handleDeviceDataResult callback is not set")
    }
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

func getDeviceModelIdentifier() -> String {
    var systemInfo = utsname()
    uname(&systemInfo)
    let machineMirror = Mirror(reflecting: systemInfo.machine)
    let identifier = machineMirror.children.reduce("") { identifier, element in
        guard let value = element.value as? Int8, value != 0 else { return identifier }
        return identifier + String(UnicodeScalar(UInt8(value)))
    }
    return identifier
}

func getDeviceModelName() -> String {
    let identifier = getDeviceModelIdentifier()
    switch identifier {
    case "iPhone14,2": return "iPhone 13 Pro"
    case "iPhone14,3": return "iPhone 13 Pro Max"
    case "iPhone14,4": return "iPhone 13 mini"
    case "iPhone14,5": return "iPhone 13"
    case "iPhone13,4": return "iPhone 12 Pro Max"
    case "iPhone13,3": return "iPhone 12 Pro"
    case "iPhone13,2": return "iPhone 12"
    case "iPhone13,1": return "iPhone 12 Mini"
    case "iPhone12,8": return "iPhone SE (2nd generation)"
    case "iPhone12,5": return "iPhone 11 Pro Max"
    case "iPhone12,3": return "iPhone 11 Pro"
    case "iPhone12,1": return "iPhone 11"
    case "iPhone11,8": return "iPhone XR"
    case "iPhone11,6", "iPhone11,4": return "iPhone XS Max"
    case "iPhone11,2": return "iPhone XS"
    case "iPhone10,6", "iPhone10,3": return "iPhone X"
    case "iPhone10,5", "iPhone10,2": return "iPhone 8 Plus"
    case "iPhone10,4", "iPhone10,1": return "iPhone 8"
    case "iPhone9,2", "iPhone9,4": return "iPhone 7 Plus"
    case "iPhone9,1", "iPhone9,3": return "iPhone 7"
    case "iPhone8,4": return "iPhone SE"
    case "iPhone8,2": return "iPhone 6s Plus"
    case "iPhone8,1": return "iPhone 6s"
    case "iPhone7,1": return "iPhone 6 Plus"
    case "iPhone7,2": return "iPhone 6"
    case "iPhone6,2", "iPhone6,1": return "iPhone 5s"
    case "iPhone5,4", "iPhone5,3": return "iPhone 5c"
    case "iPhone5,2", "iPhone5,1": return "iPhone 5"
    case "iPhone4,1": return "iPhone 4S"
    case "iPhone3,3", "iPhone3,2", "iPhone3,1": return "iPhone 4"
    case "iPhone2,1": return "iPhone 3GS"
    case "iPhone1,2": return "iPhone 3G"
    case "iPhone1,1": return "iPhone"
    
    case "iPad13,16", "iPad13,17": return "iPad Air (5th generation)"
    case "iPad13,10", "iPad13,11": return "iPad Pro 11-inch (4th generation)"
    case "iPad13,8", "iPad13,9": return "iPad Pro 12.9-inch (6th generation)"
    case "iPad13,4", "iPad13,5", "iPad13,6", "iPad13,7": return "iPad Pro 11-inch (3rd generation)"
    case "iPad13,1", "iPad13,2": return "iPad Air (4th generation)"
    case "iPad12,1", "iPad12,2": return "iPad (9th generation)"
    case "iPad11,7", "iPad11,6": return "iPad (8th generation)"
    case "iPad11,4", "iPad11,5": return "iPad Air (3rd generation)"
    case "iPad11,3", "iPad11,2": return "iPad mini (5th generation)"
    case "iPad11,1": return "iPad mini (5th generation)"
    case "iPad10,6", "iPad10,5": return "iPad (9th generation)"
    case "iPad8,12", "iPad8,11": return "iPad Pro 12.9-inch (4th generation)"
    case "iPad8,9", "iPad8,8": return "iPad Pro 11-inch (2nd generation)"
    case "iPad8,7", "iPad8,6": return "iPad Pro 12.9-inch (3rd generation)"
    case "iPad8,5", "iPad8,4": return "iPad Pro 11-inch"
    case "iPad8,3", "iPad8,2": return "iPad Pro 11-inch"
    case "iPad8,1": return "iPad Pro 11-inch"
    case "iPad7,6", "iPad7,5": return "iPad (6th generation)"
    case "iPad7,4", "iPad7,3": return "iPad Pro 10.5-inch"
    case "iPad7,2": return "iPad Pro 12.9-inch (2nd generation)"
    case "iPad7,1": return "iPad Pro 12.9-inch (2nd generation)"
    case "iPad6,12", "iPad6,11": return "iPad (5th generation)"
    case "iPad6,8", "iPad6,7": return "iPad Pro 12.9-inch"
    case "iPad6,4", "iPad6,3": return "iPad Pro 9.7-inch"
    case "iPad5,4": return "iPad Air 2"
    case "iPad5,3": return "iPad Air 2"
    case "iPad5,2": return "iPad mini 4"
    case "iPad5,1": return "iPad mini 4"
    case "iPad4,9": return "iPad mini 3"
    case "iPad4,8": return "iPad mini 3"
    case "iPad4,7": return "iPad mini 3"
    case "iPad4,6": return "iPad mini 2"
    case "iPad4,5": return "iPad mini 2"
    case "iPad4,4": return "iPad mini 2"
    case "iPad4,3": return "iPad Air"
    case "iPad4,2": return "iPad Air"
    case "iPad4,1": return "iPad Air"
    case "iPad3,6": return "iPad (4th generation)"
    case "iPad3,5": return "iPad (4th generation)"
    case "iPad3,4": return "iPad (4th generation)"
    case "iPad3,3": return "iPad (3rd generation)"
    case "iPad3,2": return "iPad (3rd generation)"
    case "iPad3,1": return "iPad (3rd generation)"
    case "iPad2,7": return "iPad mini (Wi-Fi + Cellular)"
    case "iPad2,6": return "iPad mini (Wi-Fi + Cellular)"
    case "iPad2,5": return "iPad mini (Wi-Fi)"
    case "iPad2,4": return "iPad 2 (Wi-Fi)"
    case "iPad2,3": return "iPad 2 (Wi-Fi + 3G)"
    case "iPad2,2": return "iPad 2 (Wi-Fi + 3G)"
    case "iPad2,1": return "iPad 2 (Wi-Fi)"
    case "iPad1,1": return "iPad"

    case "iPod9,1": return "iPod touch (7th generation)"
    case "iPod7,1": return "iPod touch (6th generation)"
    case "iPod5,1": return "iPod touch (5th generation)"
    case "iPod4,1": return "iPod touch (4th generation)"
    case "iPod3,1": return "iPod touch (3rd generation)"
    case "iPod2,1": return "iPod touch (2nd generation)"
    case "iPod1,1": return "iPod touch"

    default: return identifier
    }
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
