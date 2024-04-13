import Foundation
import UIKit
import AVFoundation

@objc class ScannerViewController: UIViewController, AVCaptureMetadataOutputObjectsDelegate {
    var captureSession: AVCaptureSession!
    var previewLayer: AVCaptureVideoPreviewLayer!
    var lastScannedBarcodeLabel: UILabel! // Label to show last scanned barcode
    var didFindCode: ((String) -> Void)?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        view.backgroundColor = UIColor.white
        captureSession = AVCaptureSession()
        
        setupCaptureDevice()
        setupLastScannedBarcodeLabel() // Setup label for last scanned barcode
    }
    
    @objc private func setupCaptureDevice() {
        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized: // The user has previously granted access to the camera.
            setupScanningSession()
        case .notDetermined: // The user has not yet been asked for camera access.
            AVCaptureDevice.requestAccess(for: .video) { granted in
                if granted {
                    DispatchQueue.main.async {
                        self.setupScanningSession()
                    }
                }
            }
        case .denied: // The user has previously denied access.
            return
        case .restricted: // The user can't grant access due to restrictions.
            return
        default:
            return
        }
    }
    
    @objc private func setupScanningSession() {
        guard let videoCaptureDevice = AVCaptureDevice.default(for: .video),
              let videoInput = try? AVCaptureDeviceInput(device: videoCaptureDevice),
              captureSession.canAddInput(videoInput) else {
            failed()
            return
        }
        
        captureSession.addInput(videoInput)
        configureMetadataOutput()
    }
    
    @objc  private func configureMetadataOutput() {
        let metadataOutput = AVCaptureMetadataOutput()
        
        if captureSession.canAddOutput(metadataOutput) {
            captureSession.addOutput(metadataOutput)
            
            metadataOutput.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
            metadataOutput.metadataObjectTypes = [.qr]
            
            addPreviewLayer()
        } else {
            failed()
            return
        }
    }
    
    @objc private func addPreviewLayer() {
        previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
        
        // Adjust this frame to match your desired camera preview size.
        let cameraPreviewFrame = CGRect(x: view.bounds.width * 0.05,
                                        y: 50,
                                        width: view.bounds.width * 0.9,
                                        height: view.bounds.height * 0.45)
        previewLayer.frame = cameraPreviewFrame
        previewLayer.videoGravity = .resizeAspectFill
        view.layer.addSublayer(previewLayer)
        
        // Add the scanning line view
        let scanningLine = UIView(frame: CGRect(x: 0,
                                                y: cameraPreviewFrame.height / 2,
                                                width: cameraPreviewFrame.width,
                                                height: 2))
        scanningLine.backgroundColor = .red  // Set the color of your scanning line here
        view.addSubview(scanningLine)
        
        captureSession.startRunning()
        
        DispatchQueue.main.async {
                  self.captureSession.startRunning()
              }
    }
    
    @objc func failed() {
        let ac = UIAlertController(title: "Scanning not supported", message: "Your device does not support scanning a code from an item. Please use a device with a camera.", preferredStyle: .alert)
        ac.addAction(UIAlertAction(title: "OK", style: .default))
        present(ac, animated: true)
        captureSession = nil
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        if !captureSession.isRunning {
            captureSession.startRunning()
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        if captureSession.isRunning {
            captureSession.stopRunning()
        }
    }
    
    @objc func metadataOutput(_ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {
        if let metadataObject = metadataObjects.first as? AVMetadataMachineReadableCodeObject,
           let stringValue = metadataObject.stringValue {
            AudioServicesPlaySystemSound(SystemSoundID(kSystemSoundID_Vibrate))
            found(code: stringValue)
            
            DispatchQueue.main.async { [unowned self] in
                      if let metadataObject = metadataObjects.first as? AVMetadataMachineReadableCodeObject,
                         let stringValue = metadataObject.stringValue {
                          // Update the last scanned barcode label with the raw value
                          self.lastScannedBarcodeLabel.text = stringValue
                      }
                  }
        }
    }
    
    @objc func found(code: String) {
        didFindCode?(code)
    }
    
    override var prefersStatusBarHidden: Bool {
        return true
    }
    
    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return .portrait
    }
    
    
    private func setupLastScannedBarcodeLabel() {
        lastScannedBarcodeLabel = UILabel()
        lastScannedBarcodeLabel.translatesAutoresizingMaskIntoConstraints = false
        lastScannedBarcodeLabel.textAlignment = .center
        lastScannedBarcodeLabel.textColor = .black
        lastScannedBarcodeLabel.font = UIFont.systemFont(ofSize: 18) // Set the font size as needed
        lastScannedBarcodeLabel.numberOfLines = 0 // Allow for multiple lines if needed
        view.addSubview(lastScannedBarcodeLabel)
        
        // Set up constraints for the label
        NSLayoutConstraint.activate([
            lastScannedBarcodeLabel.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 50 + view.bounds.height * 0.45 + 20),
            lastScannedBarcodeLabel.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 20),
            lastScannedBarcodeLabel.rightAnchor.constraint(equalTo: view.rightAnchor, constant: -20),
            lastScannedBarcodeLabel.heightAnchor.constraint(greaterThanOrEqualToConstant: 50) // Adjust the height as needed
        ])
    }
}
