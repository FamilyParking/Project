//
//  Login.swift
//  MapDemo
//
//  Created by mauro piva on 15/01/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import UIKit
import Foundation

class Login: UIViewController, UITextFieldDelegate {

//@IBOutlet weak var NickNameTextField: UITextField!
//@IBOutlet weak var EmailTextField: UITextField!
//@IBOutlet weak var PinTextField: UITextField!
    
    
    @IBOutlet weak var NickNameTextField: UITextField!
    @IBOutlet weak var EmailTextField: UITextField!
    @IBOutlet weak var PinTextField: UITextField!
    
    
    @IBOutlet weak var CreateAccount: UILabel!
//@IBOutlet weak var CreateAccountLabel: UILabel!
    @IBOutlet weak var CheckMailLabel: UILabel!
    @IBOutlet weak var MailVerifiedLabel: UILabel!
    
    @IBOutlet weak var VerifyButton: UIButton!
    @IBOutlet weak var ConfirmButton: UIButton!
    
    var insertPin=false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        NickNameTextField.delegate = self
        EmailTextField.delegate = self
        PinTextField.delegate = self
        
        PinTextField.hidden = true
        CheckMailLabel.hidden = true
        
        
        ConfirmButton.enabled = false
        ConfirmButton.hidden = true
        
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func VerifyClick() {
        
        if(insertPin){goBack()
            return}
        if ( NickNameTextField.text == ""  ||
            !isValidEmail(EmailTextField.text)) {
            
            var alertView:UIAlertView = UIAlertView()
            alertView.title = "Sign Up Failed!"
            alertView.message = "Please enter Username and Password"
            alertView.delegate = self
            alertView.addButtonWithTitle("OK")
            alertView.show()
        }
        else{
            sendMailForPin()
          //  VerifyButton.enabled=false
            //ConfirmButton.enabled=false
        }
       

    }
    
     func isValidEmail(testStr:String) -> Bool {
        println("validate calendar: \(testStr)")
        let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"
        
        var emailTest = NSPredicate(format:"SELF MATCHES %@", emailRegEx)
        let result = emailTest!.evaluateWithObject(testStr)
        return result
    }

    
    @IBAction func VerifyCode() {
        
        if ( PinTextField.text == ""  ||
            !isValidEmail(EmailTextField.text)) {
                
                var alertView:UIAlertView = UIAlertView()
                alertView.title = "Empty Pin"
                alertView.message = "Please enter the pin received by mail"
                alertView.delegate = self
                alertView.addButtonWithTitle("OK")
                alertView.show()
        }
        else{
            ConfirmButton.enabled = false
            pinValidation()
            
        }
 
        
    }
    
    func textFieldShouldReturn(textField: UITextField!) -> Bool // called when 'return' key pressed. return NO to ignore.
    {
        textField.resignFirstResponder()
        return true;
    }
    
     override func touchesBegan(touches: NSSet, withEvent event: UIEvent) {
        PinTextField.resignFirstResponder()
    }
    
    
    
    
    
    func sendMailForPin()
    {
        var request = NSMutableURLRequest(URL: NSURL(string: "http://first-vision-798.appspot.com/registration")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        
        var params = ["ID":UIDevice.currentDevice().identifierForVendor.UUIDString,
           // "Code":PinTextField.text,
            "Nickname":NickNameTextField.text,
            "Email":EmailTextField.text] as Dictionary<String, NSObject>
        
        var err: NSError?
        request.HTTPBody = NSJSONSerialization.dataWithJSONObject(params, options: nil, error: &err)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        var task = session.dataTaskWithRequest(request, completionHandler: {data, response, error -> Void in
            println("Response: \(response)")
            
            
            var strData = NSString(data: data, encoding: NSUTF8StringEncoding)
            
            println("Body: \(strData!)")
            var err: NSError?
            var json = NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.AllowFragments, error: &err) as? NSDictionary
            
            // Did the JSONObjectWithData constructor return an error? If so, log the error to the console
            if(err != nil) {
                println(err!.localizedDescription)
                let jsonStr = NSString(data: data!, encoding: NSUTF8StringEncoding)
                println("Error could not parse JSON because there is an error: '\(jsonStr)'")
                var stringa:NSString = "" + jsonStr!
                
                if(stringa.length>3){
                var array = stringa.componentsSeparatedByString(",")
                    if(array.count==3){
                var fl = array[0].componentsSeparatedByString(":")
                var fla = fl[1].componentsSeparatedByString("'")
                var flag = fla[0].stringByReplacingOccurrencesOfString(" ", withString: "")
                println(flag)
                
                var desc = array[2].componentsSeparatedByString(":")
                var descri = desc[1].componentsSeparatedByString("'")
                var description: AnyObject=descri[1]
                    println(description)
                if (flag == "True"){
                    println("activating pin")
                    self.VerifyButton.enabled=true
                    self.activatePin()
                    }
                else{
                    self.VerifyButton.enabled=true
                        }
                        
                    }
                    else{
                        self.VerifyButton.enabled=true
                        
                        self.noInternetPopup()

                    }
                }
                else{
                    self.VerifyButton.enabled=true

                    self.noInternetPopup()
                }
                
                self.VerifyButton.enabled=true
                
            }
            
            else {
                // The JSONObjectWithData constructor didn't return an error. But, we should still
                // check and make sure that json has a value using optional binding.
                if let parseJSON = json {
                    // Okay, the parsedJSON is here, let's get the value for 'success' out of it
                    var success = parseJSON["description"] as? String
                    println("Succes: \(success)")
                }
                else {
                    // Woa, okay the json object was nil, something went worng. Maybe the server isn't running?
                    let jsonStr = NSString(data: data, encoding: NSUTF8StringEncoding)
                    println("Error could not parse JSON: \(jsonStr)")
                }
            }
        })
        task.resume()
        
    }
    
    func noInternetPopup(){
        var alertView:UIAlertView = UIAlertView()
        alertView.title = "No internet"
        alertView.message = "Please check your internet connection"
        alertView.delegate = self
        alertView.addButtonWithTitle("OK")
        alertView.show()
    }
    
    func activatePin()
    {
        insertPin=true
        VerifyButton.setTitle("Back", forState: .Normal)
        VerifyButton.enabled = true
        CreateAccount.text = "Check your mail for the pin"
        PinTextField.hidden = false
        CheckMailLabel.hidden = false
        ConfirmButton.enabled = true
        ConfirmButton.hidden = false
        NickNameTextField.hidden = true
        EmailTextField.hidden = true
        MailVerifiedLabel.hidden = true
    }
    
    func goBack(){
        insertPin=false
        VerifyButton.setTitle("Verify", forState: .Normal)
        CreateAccount.text = "Insert your nick and mail"
        PinTextField.hidden = true
        CheckMailLabel.hidden = true
        ConfirmButton.enabled = false
        ConfirmButton.hidden = true
        NickNameTextField.hidden = false
        EmailTextField.hidden = false
        MailVerifiedLabel.hidden = false
    }
    
    func pinValidation(){
        var request = NSMutableURLRequest(URL: NSURL(string: "http://first-vision-798.appspot.com/getIDGroups")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        var params = ["ID":UIDevice.currentDevice().identifierForVendor.UUIDString,
                "Code":PinTextField.text,
               // "Code":"123455",
                "Nickname":NickNameTextField.text,
                "Email":EmailTextField.text] as Dictionary<String, String>
        var err: NSError?
            println(NSJSONSerialization.dataWithJSONObject(params, options: nil, error: &err))
            request.HTTPBody = NSJSONSerialization.dataWithJSONObject(params, options: nil, error: &err)
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue("application/json", forHTTPHeaderField: "Accept")
            
            var task = session.dataTaskWithRequest(request, completionHandler: {data, response, error -> Void in
                println("Response: \(response)")
                
                
                var strData = NSString(data: data, encoding: NSUTF8StringEncoding)
                
                println("Body: \(strData!)")
                var err: NSError?
                var json = NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.AllowFragments, error: &err) as? NSDictionary
                
                // Did the JSONObjectWithData constructor return an error? If so, log the error to the console
                if(err != nil) {
                    println(err!.localizedDescription)
                    let jsonStr = NSString(data: data!, encoding: NSUTF8StringEncoding)
                    println("Error could not parse JSON because there is an error: '\(jsonStr)'")
                    var stringa:NSString = "" + jsonStr!
                    
                    if(stringa.length>3){
                        var array = stringa.componentsSeparatedByString(",")
                        if(array.count==3){
                            var fl = array[0].componentsSeparatedByString(":")
                            var fla = fl[1].componentsSeparatedByString("'")
                            var flag = fla[0].stringByReplacingOccurrencesOfString(" ", withString: "")
                            println(flag)
                            var desc = array[2].componentsSeparatedByString(":")
                            var descri = desc[1].componentsSeparatedByString("'")
                            var description: AnyObject=descri[1]
                            println(description)
                            if (flag == "True"){
                                var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
                                prefs.setObject(self.PinTextField.text, forKey: "USERNAME")
                                prefs.setObject(self.EmailTextField.text, forKey: "EMAIL")
                                prefs.setInteger(1, forKey: "ISLOGGEDIN")
                                prefs.synchronize()
                                self.dismissViewControllerAnimated(true, completion: nil)
                            return
                            }
                            else{
                                var alertView:UIAlertView = UIAlertView()
                                alertView.title = "Wrong pin"
                                alertView.message = "Please check your pin"
                                alertView.delegate = self
                                alertView.addButtonWithTitle("OK")
                                alertView.show()
                                self.ConfirmButton.enabled = true                            }
                            }
                        else{
                            var alertView:UIAlertView = UIAlertView()
                            alertView.title = "Malformed server answer"
                            alertView.message = "Please check your pin"
                            alertView.delegate = self
                            alertView.addButtonWithTitle("OK")
                            alertView.show()
                            self.ConfirmButton.enabled = true
                        }
                        
                    }
                    else{
                        self.ConfirmButton.enabled = true
                        self.noInternetPopup()
                    }
                    self.ConfirmButton.enabled = true
                }
                    
                else {
                    // The JSONObjectWithData constructor didn't return an error. But, we should still
                    // check and make sure that json has a value using optional binding.
                    if let parseJSON = json {
                        // Okay, the parsedJSON is here, let's get the value for 'success' out of it
                        var success = parseJSON["description"] as? String
                        println("Succes: \(success)")
                    }
                    else {
                        // Woa, okay the json object was nil, something went worng. Maybe the server isn't running?
                        let jsonStr = NSString(data: data, encoding: NSUTF8StringEncoding)
                        println("Error could not parse JSON: \(jsonStr)")
                    }
                }
            })
            task.resume()
            
        }

    
}
