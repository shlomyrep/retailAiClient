import UIKit
import AVFoundation
import Vision

@objc class ScannerViewController: UIViewController, AVCaptureVideoDataOutputSampleBufferDelegate, AVCaptureMetadataOutputObjectsDelegate {
    var captureSession: AVCaptureSession!
    var previewLayer: AVCaptureVideoPreviewLayer!
    var lastScannedBarcodeLabel: UILabel!
    var didFindCode: ((String) -> Void)?
    var isProcessingEnabled = true
    var seenBarcodes = Set<String>()
    var buttonsStackView: UIStackView!
    var numberOccurrencesMap = [String: Int]()
    var textRecognitionRequest: VNRecognizeTextRequest!

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = UIColor.white
        captureSession = AVCaptureSession()
        setupCaptureDevice()
        setupUIComponents()
        setupTextRecognition()
    }

    private func setupCaptureDevice() {
        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized:
            setupScanningSession()
        case .notDetermined:
            AVCaptureDevice.requestAccess(for: .video) { [weak self] granted in
                if granted {
                    DispatchQueue.main.async {
                        self?.setupScanningSession()
                    }
                }
            }
        case .denied, .restricted:
            return
        default:
            return
        }
    }

    private func setupScanningSession() {
        guard let videoCaptureDevice = AVCaptureDevice.default(for: .video),
              let videoInput = try? AVCaptureDeviceInput(device: videoCaptureDevice),
              captureSession.canAddInput(videoInput) else {
            failed()
            return
        }

        captureSession.addInput(videoInput)
        configureMetadataOutput()

        let videoDataOutput = AVCaptureVideoDataOutput()
        videoDataOutput.setSampleBufferDelegate(self, queue: DispatchQueue(label: "videoQueue"))
        captureSession.addOutput(videoDataOutput)
    }

    private func configureMetadataOutput() {
        let metadataOutput = AVCaptureMetadataOutput()
        if captureSession.canAddOutput(metadataOutput) {
            captureSession.addOutput(metadataOutput)
            metadataOutput.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
            metadataOutput.metadataObjectTypes = [.qr, .code128, .ean8, .ean13, .upce]
        } else {
            failed()
        }
    }

    private func setupUIComponents() {
        setupLastScannedBarcodeLabel()
        setupButtonsContainer()
        addPreviewLayer()
    }

    private func setupLastScannedBarcodeLabel() {
        lastScannedBarcodeLabel = UILabel()
        lastScannedBarcodeLabel.translatesAutoresizingMaskIntoConstraints = false
        lastScannedBarcodeLabel.textAlignment = .center
        lastScannedBarcodeLabel.textColor = .black
        lastScannedBarcodeLabel.font = UIFont.systemFont(ofSize: 18)
        lastScannedBarcodeLabel.numberOfLines = 0
        view.addSubview(lastScannedBarcodeLabel)

        NSLayoutConstraint.activate([
            lastScannedBarcodeLabel.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 50 + view.bounds.height * 0.45 + 20),
            lastScannedBarcodeLabel.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 20),
            lastScannedBarcodeLabel.rightAnchor.constraint(equalTo: view.rightAnchor, constant: -20),
            lastScannedBarcodeLabel.heightAnchor.constraint(greaterThanOrEqualToConstant: 50)
        ])
    }

    private func setupButtonsContainer() {
        let scrollView = UIScrollView()
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(scrollView)

        NSLayoutConstraint.activate([
            scrollView.topAnchor.constraint(equalTo: lastScannedBarcodeLabel.bottomAnchor, constant: 20),
            scrollView.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 20),
            scrollView.rightAnchor.constraint(equalTo: view.rightAnchor, constant: -20),
            scrollView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -20)
        ])

        buttonsStackView = UIStackView()
        buttonsStackView.axis = .vertical
        buttonsStackView.spacing = 10
        buttonsStackView.translatesAutoresizingMaskIntoConstraints = false
        scrollView.addSubview(buttonsStackView)

        NSLayoutConstraint.activate([
            buttonsStackView.topAnchor.constraint(equalTo: scrollView.topAnchor),
            buttonsStackView.leftAnchor.constraint(equalTo: scrollView.leftAnchor),
            buttonsStackView.rightAnchor.constraint(equalTo: scrollView.rightAnchor),
            buttonsStackView.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor),
            buttonsStackView.widthAnchor.constraint(equalTo: scrollView.widthAnchor)
        ])
    }

    private func addPreviewLayer() {
        previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
        previewLayer.frame = CGRect(x: view.bounds.width * 0.03, y: 20, width: view.bounds.width * 0.95, height: view.bounds.height * 0.45)
        previewLayer.videoGravity = .resizeAspectFill
        view.layer.insertSublayer(previewLayer, below: lastScannedBarcodeLabel.layer)
    }

    private func setupTextRecognition() {
        textRecognitionRequest = VNRecognizeTextRequest { [weak self] request, error in
            guard let observations = request.results as? [VNRecognizedTextObservation],
                  error == nil else {
                return
            }
            let texts = observations.compactMap { $0.topCandidates(1).first?.string }
            DispatchQueue.main.async {
                self?.handleTextRecognitionResults(texts)
            }
        }
        textRecognitionRequest.recognitionLevel = .accurate
        textRecognitionRequest.usesLanguageCorrection = true
    }

    private func handleTextRecognitionResults(_ texts: [String]) {
        let nineDigitPattern = "^\\d{9}$"
        let regex = try! NSRegularExpression(pattern: nineDigitPattern, options: [])

        for text in texts {
            let range = NSRange(location: 0, length: text.utf16.count)
            if regex.firstMatch(in: text, options: [], range: range) != nil {
                let count = numberOccurrencesMap[text] ?? 0
                numberOccurrencesMap[text] = count + 1

                if numberOccurrencesMap[text]! >= 3 {
                    createBarcodeButton(barcode: text)
                    numberOccurrencesMap[text] = 0
                }
            }
        }
    }


    func captureOutput(_ output: AVCaptureOutput, didOutput sampleBuffer: CMSampleBuffer, from connection: AVCaptureConnection) {
        guard isProcessingEnabled else { return }
        let pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer)!

        let imageRequestHandler = VNImageRequestHandler(cvPixelBuffer: pixelBuffer, orientation: .up, options: [:])
        try? imageRequestHandler.perform([textRecognitionRequest])
    }

    func metadataOutput(_ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {
        DispatchQueue.main.async { [unowned self] in
            for metadataObject in metadataObjects {
                guard let readableObject = metadataObject as? AVMetadataMachineReadableCodeObject,
                      let stringValue = readableObject.stringValue else {
                    continue
                }
                if !seenBarcodes.contains(stringValue) {
                    seenBarcodes.insert(stringValue)
                    createBarcodeButton(barcode: stringValue)
                }
            }
        }
    }

    private func createBarcodeButton(barcode: String) {
        guard !seenBarcodes.contains(barcode) else { return }
        seenBarcodes.insert(barcode)

        let button = UIButton(type: .system)
        button.setTitle("פתח מוצר \(barcode)", for: .normal)
        button.addTarget(self, action: #selector(barcodeButtonTapped(_:)), for: .touchUpInside)
        button.backgroundColor = .white
        button.setTitleColor(.black, for: .normal)
        button.titleLabel?.font = UIFont.systemFont(ofSize: 18)
        button.layer.cornerRadius = 25
        button.layer.borderWidth = 1.5
        button.layer.borderColor = UIColor.lightGray.cgColor
        button.layer.masksToBounds = true
        button.contentEdgeInsets = UIEdgeInsets(top: 15, left: 80, bottom: 15, right: 80)

        buttonsStackView.addArrangedSubview(button)
        button.heightAnchor.constraint(equalToConstant: 50).isActive = true
    }



    @objc func barcodeButtonTapped(_ sender: UIButton) {
        guard let barcode = sender.title(for: .normal) else { return }
        didFindCode?(barcode)
        closeViewController()
    }
    
    private func closeViewController() {
        if let navigationController = navigationController, navigationController.viewControllers.first != self {
            // If part of a navigation stack and not the root, pop the view controller.
            navigationController.popViewController(animated: true)
        } else {
            // If presented modally or is the root of the navigation controller, dismiss it.
            dismiss(animated: true, completion: nil)
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
        if captureSession != nil && !captureSession.isRunning {
            captureSession.startRunning()
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        if captureSession != nil && captureSession.isRunning {
            captureSession.stopRunning()
        }
    }

    override var prefersStatusBarHidden: Bool {
        return true
    }

    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return .portrait
    }
}
