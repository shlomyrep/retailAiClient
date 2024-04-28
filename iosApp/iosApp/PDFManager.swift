import Foundation
import UIKit

class PDFManager: NSObject, UIDocumentInteractionControllerDelegate {
    static let shared = PDFManager()

    func openPdf(url: String) {
        guard let pdfURL = URL(string: url) else {
            print("Invalid URL: \(url)")
            return
        }

        let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
        let localPath = documentsDirectory.appendingPathComponent(pdfURL.lastPathComponent)

        if FileManager.default.fileExists(atPath: localPath.path) {
            print("PDF file exists at \(localPath)")
            DispatchQueue.main.async {
                self.presentDocument(at: localPath)
            }
        } else {
            print("Downloading PDF...")
            downloadPDF(from: pdfURL, to: localPath) { success in
                if success {
                    DispatchQueue.main.async {
                        self.presentDocument(at: localPath)
                    }
                }
            }
        }
    }

    private func downloadPDF(from url: URL, to destinationURL: URL, completion: @escaping (Bool) -> Void) {
        let urlSession = URLSession(configuration: .default, delegate: nil, delegateQueue: OperationQueue())
        let downloadTask = urlSession.downloadTask(with: url) { location, response, error in
            guard let location = location, error == nil else {
                print("Error downloading file: \(error?.localizedDescription ?? "Unknown error")")
                completion(false)
                return
            }
            do {
                try FileManager.default.moveItem(at: location, to: destinationURL)
                print("PDF downloaded to \(destinationURL.path)")
                completion(true)
            } catch {
                print("Error saving file: \(error.localizedDescription)")
                completion(false)
            }
        }
        downloadTask.resume()
    }

    private func presentDocument(at filePath: URL) {
        let documentInteractionController = UIDocumentInteractionController(url: filePath)
        documentInteractionController.delegate = self
        DispatchQueue.main.async {
            documentInteractionController.presentPreview(animated: true)
        }
    }

    // UIDocumentInteractionControllerDelegate methods
    func documentInteractionControllerViewControllerForPreview(_ controller: UIDocumentInteractionController) -> UIViewController {
        return UIApplication.shared.windows.first { $0.isKeyWindow }?.rootViewController ?? UIViewController()
    }
}
