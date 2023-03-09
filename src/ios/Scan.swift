import Foundation
import CovveWeScan
import os.log

var com = CDVInvokedUrlCommand()
var uri = "";

@objc(Scan) class Scan : CDVPlugin, ImageScannerControllerDelegate {
    func imageScannerControllerGoToPhotos(_ scanner: CovveWeScan.ImageScannerController) {
    }
    
    @objc(scanDoc:)
    func scanDoc(_ command: CDVInvokedUrlCommand){
        com = command

        let scannerViewController = ImageScannerController(delegate: self)

        scannerViewController.modalPresentationStyle = .fullScreen
        
        if #available(iOS 13.0, *) {
            scannerViewController.navigationBar.tintColor = .label
        } else {
            scannerViewController.navigationBar.tintColor = .black
        }
        
        self.viewController?.present(scannerViewController, animated: true)

    }
    
    
    func imageScannerController(_ scanner: ImageScannerController, didFailWithError error: Error) {
        os_log("Error: %@", log: .default, type: .error, String(describing: error))
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: String(describing: error));
        commandDelegate.send(pluginResult, callbackId:com.callbackId);
        scanner.dismiss(animated: true)
    }

    func imageScannerController(_ scanner: ImageScannerController, didFinishScanningWithResults results: ImageScannerResults) {
         if(saveImage(image: (results.doesUserPreferEnhancedScan ? (results.enhancedScan?.image ?? results.croppedScan.image) : results.croppedScan.image))) {
             let pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: uri);
             commandDelegate.send(pluginResult, callbackId:com.callbackId);
         }
         else {
             let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "File creation failed!");
             commandDelegate.send(pluginResult, callbackId:com.callbackId);
         }
         scanner.dismiss(animated: true)
    }
    
    func saveImage(image: UIImage) -> Bool {
        guard let data = UIImageJPEGRepresentation(image, 0.7) ?? UIImagePNGRepresentation(image) else {
            return false
        }
        guard let directory = try? FileManager.default.url(for: .documentDirectory, in: .userDomainMask, appropriateFor: nil, create: false) as NSURL else {
            return false
        }
        do {
            try data.write(to: directory.appendingPathComponent("scanimg.jpeg")!)
            uri = directory.appendingPathComponent("scanimg.jpeg")?.absoluteString ?? ""
            if (uri == "") {
                return false
            }
            return true
        } catch {
            print(error.localizedDescription)
            return false
        }
    }
    
    func imageScannerControllerDidCancel(_ scanner: ImageScannerController) {
        let pluginResult = CDVPluginResult(status: CDVCommandStatus_ERROR, messageAs: "Cancelled");
        commandDelegate.send(pluginResult, callbackId:com.callbackId);
        scanner.dismiss(animated: true)
    }
    
    override func pluginInitialize() {
        super.pluginInitialize()
    }
}
