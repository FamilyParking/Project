//
//  Registration2ViewController.swift
//  familyparking
//
//  Created by mauro piva on 19/02/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import Foundation
import UIKit

class Registration2ViewController: UIViewController {
    
    @IBOutlet weak var PinBox: UITextField!
    @IBOutlet weak var BackButton: UIButton!
    @IBOutlet weak var ConfirmButton: UIButton!
    
    @IBAction func Confirm(sender: AnyObject) {
        self.BackButton.enabled = false
        self.ConfirmButton.enabled = false
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let mail:String = prefs.objectForKey("EMAIL") as String
        
        var request = NSMutableURLRequest(URL: NSURL(string: Comments().serverPath + "confirmCode")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        
        
        var user = ["Code":PinBox.text,
            "Email":mail] as Dictionary<String, NSObject>
        var params = ["User":user,
            ] as Dictionary<String, NSObject>
        
        
        var err: NSError?
        request.HTTPBody = NSJSONSerialization.dataWithJSONObject(params, options: nil, error: &err)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        var task = session.dataTaskWithRequest(request, completionHandler: {data, response, error -> Void in
            println("Response: \(response)")
            var strData = NSString(data: data, encoding: NSUTF8StringEncoding)
            println("Body: \(strData!)")
            if(strData!.containsString("Code right")){
                println("Code Sent")
                let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
                prefs.setObject(self.PinBox.text, forKey: "PIN")
                prefs.setInteger(1, forKey:"ISLOGGEDIN")
                prefs.synchronize()
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    self.presentingViewController!.presentingViewController!.dismissViewControllerAnimated(true, completion: nil)
                })
            }
            else if(strData!.containsString("false")){
                println("Wrong Code")
                self.wrongPinPopUp()
                self.BackButton.enabled = true
                self.ConfirmButton.enabled = true
            }
            else{
                self.noInternetPopUp()
                self.BackButton.enabled = true
                self.ConfirmButton.enabled = true
            }
        })
        task.resume()
    }
    
    @IBAction func Back(sender: AnyObject) {
        dispatch_async(dispatch_get_main_queue(), { () -> Void in
        self.dismissViewControllerAnimated(true, completion: nil)
        })
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func noInternetPopUp(){
        var alertView:UIAlertView = UIAlertView()
        alertView.title = "No internet"
        alertView.message = "Please check your internet connection"
        alertView.delegate = self
        alertView.addButtonWithTitle("OK")
        alertView.show()
    }
    
    func wrongPinPopUp(){
        var alertView:UIAlertView = UIAlertView()
        alertView.title = "Wrong pin"
        alertView.message = "Your pin is wrong"
        alertView.delegate = self
        alertView.addButtonWithTitle("OK")
        alertView.show()
    }

    
    
}