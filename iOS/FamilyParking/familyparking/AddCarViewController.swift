//
//  AddCarViewController.swift
//  familyparking
//
//  Created by mauro piva on 19/02/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import Foundation
import UIKit
import CoreData

class AddCarViewController: UIViewController, UITextFieldDelegate {
    
  
    
   // @IBOuRIFAREtlet weak var BackButton: UIButton!
 //   @IBOuRIFAREtlet weak var ConfirmButton: UIButton!
    @IBOutlet weak var CarName: UITextField!
    
    @IBOutlet weak var BackButt: UIButton!
    @IBOutlet weak var ConfButt: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func Back() {
         self.dismissViewControllerAnimated(true, completion: nil)
    }
    
    
    @IBAction func addACar() {
        
        var model = self.CarName.text
        if(!model.isEmpty){
            BackButt.enabled = false
            ConfButt.enabled = false
            addCarToServer()
        }
        else{
            //TODO emtpy text
        }
    }
    
    func textFieldShouldReturn(textField: UITextField!) -> Bool // called when 'return' key pressed. return NO to ignore.
    {
        textField.resignFirstResponder()
        return true;
    }
    
    override func touchesBegan(touches: NSSet, withEvent event: UIEvent) {
        CarName.resignFirstResponder()
    }
    
    func addCarToServer(){
        
        if(CarName.text.isEmpty){
            println("Ciao")
            var alert = UIAlertController(title: "Car Name",message:"No Name, No Car",
                preferredStyle: .Alert)
            let cancelAction = UIAlertAction(title: "Cancel",
                style: .Default) { (action: UIAlertAction!) -> Void in
                return
            }
            alert.addAction(cancelAction)
            presentViewController(alert,
                animated: true,
                completion: nil)
        }
        else{
            println("ServerCAraar")
            
        var request = NSMutableURLRequest(URL: NSURL(string: "http://first-vision-798.appspot.com/createCar")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let pin:String = prefs.objectForKey("PIN") as String
        let mail:String = prefs.objectForKey("EMAIL") as String
       
        var otherUsers = ["Code":pin,
            "Email":mail] as Dictionary<String, NSObject>
        
        var usersMail = [mail]
        
        var car = [
        "Bluetooth_MAC":"",
        "Bluetooth_name":"",
        "Brand":"",
        "Users":usersMail,
        "Name":CarName.text] as Dictionary<String, NSObject>
        
        
        
        var user = ["Code":pin,
                    "Email":mail] as Dictionary<String, NSObject>
        
        var param = [  "User":user,
                        "Car":car] as Dictionary<String, NSObject>
        
        
        var err: NSError?
        request.HTTPBody = NSJSONSerialization.dataWithJSONObject(param, options: nil, error: &err)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        var task = session.dataTaskWithRequest(request, completionHandler: {data, response, error -> Void in
            println("Response: \(response)")
            
            
            var strData = NSString(data: data, encoding: NSUTF8StringEncoding)
            
            println("Body: \(strData!)")
            var err: NSError?
            var json = NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions.AllowFragments, error: &err) as? NSDictionary
            
            println(json);
            
            if((json?["flag"] as Bool)==true){
                self.BackButt.enabled = true
                self.ConfButt.enabled = true
                CarUpdate().addACarToLocalDatabase((json?["object"] as NSNumber).stringValue, name: self.CarName.text, lat: "0", long: "0",brand:"")
                self.dismissViewControllerAnimated(true, completion: nil)
            }
            else{
                        self.BackButt.enabled = true
                        self.ConfButt.enabled = true
            }
        })
        task.resume()
        
    }
    }
    
}

