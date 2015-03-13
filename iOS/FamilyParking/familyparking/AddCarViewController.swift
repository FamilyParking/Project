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
    
  //  @IBOutlet weak var BackButt: UIButton!
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
            //BackButt.enabled = false
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
        var request = NSMutableURLRequest(URL: NSURL(string: Comments().serverPath + "createCar")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let pin:String = prefs.objectForKey("PIN") as String
        let mail:String = prefs.objectForKey("EMAIL") as String
       
        var otherUsers = ["Code":pin,
            "Email":mail] as Dictionary<String, NSObject>
        
        
        var car = [
        "Bluetooth_MAC":"",
        "Bluetooth_name":"",
        "Brand":"no_brand",
        "Users":"",
        "Name":CarName.text,
        "Register":"",
        "Latitude":"",
            "Name_car":CarName.text] as Dictionary<String, NSObject>
        
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
            var err: NSError?
            var json = NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.AllowFragments, error: &err) as? NSDictionary
            if(err==nil){
                if((json!["Flag"] as Bool) == true){
                            //self.BackButt.enabled = true
                            self.ConfButt.enabled = true
                    CarUpdate().addACarToLocalDatabase((json!["Object"] as NSNumber).description, name: self.CarName.text, lat: "0", long: "0",brand:"",lastPark:"never",isParked:false)
                        dispatch_async(dispatch_get_main_queue(), { () -> Void in
                            //self.dismissViewControllerAnimated(true, completion: nil)
                            println("")
                            self.navigationController!.popViewControllerAnimated(true)
                        })
                    }
                    else{
                        //self.BackButt.enabled = true
                        self.ConfButt.enabled = true
                    }
                }
                else{
                   //self.BackButt.enabled = true
                    self.ConfButt.enabled = true
                }
            
            
            
        })
        
            task.resume()
        
    }
    
    
}

