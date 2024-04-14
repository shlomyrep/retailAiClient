import Foundation
import UIKit
import AVFoundation
import shared
import AudioToolbox


@objc class ScannerViewController: UIViewController, AVCaptureMetadataOutputObjectsDelegate {
    var captureSession: AVCaptureSession!
    var previewLayer: AVCaptureVideoPreviewLayer!
    var lastScannedBarcodeLabel: UILabel!
    var didFindCode: ((String) -> Void)?
    var lastScannedCode: String?
    var isProcessingEnabled = true // to control processing
    var buttonsContainer: UIView!

    
    override func viewDidLoad() {
           super.viewDidLoad()
           
           view.backgroundColor = UIColor.white
           captureSession = AVCaptureSession()
           setupCaptureDevice()
           setupLastScannedBarcodeLabel()
           setupButtonsContainer()
       }
    
    private func setupButtonsContainer() {
           buttonsContainer = UIView()
           buttonsContainer.translatesAutoresizingMaskIntoConstraints = false
           view.addSubview(buttonsContainer)
           
           NSLayoutConstraint.activate([
               buttonsContainer.topAnchor.constraint(equalTo: lastScannedBarcodeLabel.bottomAnchor, constant: 20),
               buttonsContainer.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 20),
               buttonsContainer.rightAnchor.constraint(equalTo: view.rightAnchor, constant: -20),
               buttonsContainer.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -20)
           ])
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
    
    @objc private func configureMetadataOutput() {
        let metadataOutput = AVCaptureMetadataOutput()
        
        if captureSession.canAddOutput(metadataOutput) {
            captureSession.addOutput(metadataOutput)
            
            metadataOutput.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
            // Include .code128 if you expect to scan Code128 barcodes like the one in the image
            metadataOutput.metadataObjectTypes = [.qr, .code128, .ean8, .ean13, .upce]
            
            addPreviewLayer()
        } else {
            failed()
            return
        }
    }


    
    @objc private func addPreviewLayer() {
        previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
        
        // Adjust this frame to match your desired camera preview size.
        let cameraPreviewFrame = CGRect(x: view.bounds.width * 0.03,
                                        y: 20,
                                        width: view.bounds.width * 0.95,
                                        height: view.bounds.height * 0.45)
        previewLayer.frame = cameraPreviewFrame
        previewLayer.videoGravity = .resizeAspectFill
        view.layer.addSublayer(previewLayer)
        
        // Add the scanning line view
        let scanningLine = UIView(frame: CGRect(x: 10,
                                                y: cameraPreviewFrame.height / 2,
                                                width: cameraPreviewFrame.width,
                                                height: 2))
        scanningLine.backgroundColor = .red
        
        view.addSubview(scanningLine)
        
        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
              self?.captureSession.startRunning()
          }
        
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
    
    func metadataOutput(_ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {
         guard let metadataObject = metadataObjects.first as? AVMetadataMachineReadableCodeObject,
               let stringValue = metadataObject.stringValue, isProcessingEnabled else {
             return
         }

         DispatchQueue.main.async { [unowned self] in
             if stringValue != lastScannedCode {
                 lastScannedCode = stringValue
                 createBarcodeButton(barcode: stringValue)
             }
         }
     }

     private func createBarcodeButton(barcode: String) {
         let barcodeButton = UIButton()
         barcodeButton.setTitle(barcode, for: .normal)
         barcodeButton.backgroundColor = .lightGray
         barcodeButton.setTitleColor(.black, for: .normal)
         barcodeButton.layer.cornerRadius = 5
         barcodeButton.clipsToBounds = true
         barcodeButton.translatesAutoresizingMaskIntoConstraints = false
         barcodeButton.addTarget(self, action: #selector(barcodeButtonTapped(_:)), for: .touchUpInside)
         
         buttonsContainer.addSubview(barcodeButton)

         // Set constraints
         NSLayoutConstraint.activate([
             barcodeButton.topAnchor.constraint(equalTo: (buttonsContainer.subviews.last?.bottomAnchor ?? buttonsContainer.topAnchor), constant: 10),
             barcodeButton.leftAnchor.constraint(equalTo: buttonsContainer.leftAnchor),
             barcodeButton.rightAnchor.constraint(equalTo: buttonsContainer.rightAnchor),
             barcodeButton.heightAnchor.constraint(equalToConstant: 50)
         ])
         
         isProcessingEnabled = false // Stop processing further until the button is tapped
     }

     @objc func barcodeButtonTapped(_ sender: UIButton) {
         guard let barcode = sender.title(for: .normal) else { return }
         handleSelectedBarcode(barcode)
         
         sender.removeFromSuperview() // Optionally remove the button or disable it
         isProcessingEnabled = true // Allow barcode scanning again
     }

     private func handleSelectedBarcode(_ barcode: String) {
         print("Barcode Selected: \(barcode)")
         didFindCode?(barcode)
         ScannerOpenerBridge.shared.handleScanResult?(barcode)
         closeViewController()
//         lastScannedBarcodeLabel.text = barcode 
     }
    
    @objc func closeViewController() {
        if let navController = self.navigationController, navController.viewControllers.first != self {
            // The view controller is in a navigation controller and it's not the root view controller
            navController.popViewController(animated: true)
        } else {
            // The view controller was presented modally or it's the root of a navigation controller
            self.dismiss(animated: true, completion: nil)
        }
    }


    
    @objc func found(code: String) {
        // Swift side verification and avoid processing if it's a duplicate
        if code != lastScannedCode {
            print("Barcode Found: \(code)") // Swift side verification
            lastScannedCode = code
            didFindCode?(code)
            showScanResult("Scanned: \(code)")
            
            DispatchQueue.main.async { [unowned self] in
                self.lastScannedBarcodeLabel.text = code
                ScannerOpenerBridge.shared.handleScanResult?(code)
                NSLog("TAMIR --> found --> Scanned Code: \(code)")
                createBarcodeButton(barcode: code)
                self.captureSession?.stopRunning()
            }
        }
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
    
    func showScanResult(_ result: String) {
        let scanResultLabel = UILabel()
        scanResultLabel.backgroundColor = UIColor.black.withAlphaComponent(0.75)
        scanResultLabel.textColor = .white
        scanResultLabel.textAlignment = .center
        scanResultLabel.text = result
        scanResultLabel.numberOfLines = 0
        scanResultLabel.alpha = 0
        scanResultLabel.layer.cornerRadius = 8
        scanResultLabel.clipsToBounds = true
        scanResultLabel.translatesAutoresizingMaskIntoConstraints = false
        
        view.addSubview(scanResultLabel)
        
        // Constraints
        NSLayoutConstraint.activate([
            scanResultLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            scanResultLabel.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -20),
            scanResultLabel.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 20),
            scanResultLabel.rightAnchor.constraint(equalTo: view.rightAnchor, constant: -20)
        ])
        
        UIView.animate(withDuration: 0.5, animations: {
            scanResultLabel.alpha = 1
        }) { _ in
            UIView.animate(withDuration: 0.5, delay: 2.0, options: [], animations: {
                scanResultLabel.alpha = 0
            }, completion: { _ in
                scanResultLabel.removeFromSuperview()
            })
        }
    }

    
}
