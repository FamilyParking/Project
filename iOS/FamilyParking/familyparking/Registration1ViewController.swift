//
//  Registration1ViewController.swift
//  familyparking
//
//  Created by mauro piva on 19/02/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import Foundation
import UIKit

class Registration1ViewController: UIViewController {
    
    @IBOutlet weak var Name: UITextField!
    @IBOutlet weak var Email: UITextField!
    @IBOutlet weak var NextButton: UIButton!
    
    func textFieldShouldReturn(textField: UITextField!) -> Bool // called when 'return' key pressed. return NO to ignore.
    {
        textField.resignFirstResponder()
        return true;
    }
    
   

    
    
    @IBAction func Next() {
        if(isValidEmail(Email.text)){
        NextButton.enabled = false
        requestPin()
        }
        else{
        wrongMailPopUp()
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func requestPin(){
            var request = NSMutableURLRequest(URL: NSURL(string: Comments().serverPath + "registration")!)
            var session = NSURLSession.sharedSession()
            request.HTTPMethod = "POST"
            var params = ["ID":UIDevice.currentDevice().identifierForVendor.UUIDString,
                "Name":Name.text,
                "Email":Email.text.lowercaseString] as Dictionary<String, NSObject>
            var err: NSError?
            request.HTTPBody = NSJSONSerialization.dataWithJSONObject(params, options: nil, error: &err)
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue("application/json", forHTTPHeaderField: "Accept")
            
            var task = session.dataTaskWithRequest(request, completionHandler: {data, response, error -> Void in
                println("Response: \(response)")
               
                
                if(response == nil){
                    dispatch_async(dispatch_get_main_queue(), { () -> Void in
                        var alertView:UIAlertView = UIAlertView()
                        alertView.title = "No internet connection"
                        alertView.message = "Please, check your internet connection."
                        alertView.delegate = self
                        alertView.addButtonWithTitle("OK")
                        alertView.show()
                    })
                    
                }
                
                
                var strData = NSString(data: data, encoding: NSUTF8StringEncoding)
                println("Body: \(strData!)")
                if(strData!.containsString("Code sent")){
                    println("Code Sent")
                    let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
                    prefs.setObject(self.Name.text, forKey: "USERNAME")
                    prefs.setObject(self.Email.text.lowercaseString, forKey: "EMAIL")
                    prefs.synchronize()
                    self.NextButton.enabled = true
                    dispatch_async(dispatch_get_main_queue(), { () -> Void in
                        self.performSegueWithIdentifier("registration_2", sender: self)
                    })
                }
                else{
                    dispatch_async(dispatch_get_main_queue(), { () -> Void in
                        self.NextButton.enabled = true
                        self.noInternetPopUp()
                    })
                }
            })
            task.resume()
    }
    
    func noInternetPopUp(){
        var alertView:UIAlertView = UIAlertView()
        alertView.title = "No internet"
        alertView.message = "Please check your internet connection"
        alertView.delegate = self
        alertView.addButtonWithTitle("OK")
        alertView.show()
    }
    func wrongMailPopUp(){
        var alertView:UIAlertView = UIAlertView()
        alertView.title = "Wrong Mail"
        alertView.message = "Please check your Email"
        alertView.delegate = self
        alertView.addButtonWithTitle("OK")
        alertView.show()
    }
    func isValidEmail(testStr:String) -> Bool {
        println("validate calendar: \(testStr)")
        let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"
        
        var emailTest = NSPredicate(format:"SELF MATCHES %@", emailRegEx)
        let result = emailTest!.evaluateWithObject(testStr)
        return result
    }
    
}