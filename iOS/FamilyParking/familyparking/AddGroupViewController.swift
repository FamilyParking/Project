//
//  AddGroupViewController.swift
//  familyparking
//
//  Created by mauro piva on 19/02/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import Foundation
import UIKit
import CoreData

class AddGroupViewController: UIViewController, UITextFieldDelegate {
    
    
  //  @IBOutlet weak var CarModel: UITextField!
  //  @IBOutlet weak var BackButton: UIButton!
  //  @IBOutlet weak var ConfirmButton: UIButton!
    @IBOutlet weak var BackButton: UIButton!
    @IBOutlet weak var ConfirmButton: UIButton!
    @IBOutlet weak var GroupName: UITextField!
    
    @IBAction func Back() {
    self.dismissViewControllerAnimated(true, completion: nil)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func AddGroup() {
        
        let model = self.GroupName.text
        if(!model.isEmpty){
            BackButton.enabled = false
            ConfirmButton.enabled = false
            addGroupToServer()
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
       GroupName.resignFirstResponder()
    }
    
    func addGroupToServer(){
        var request = NSMutableURLRequest(URL: NSURL(string: "http://first-vision-798.appspot.com/createGroup")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let pin:String = prefs.objectForKey("PIN") as String
        let mail:String = prefs.objectForKey("EMAIL") as String
        var params = ["Code":pin,
            "Name":GroupName.text,
            "Email":mail] as Dictionary<String, NSObject>
        
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
                        
                        var id = array[1].componentsSeparatedByString(":")
                        var idCar: String = id[1].stringByReplacingOccurrencesOfString(" ", withString: "") as String;
                        println(idCar)
                        var desc = array[2].componentsSeparatedByString(":")
                        var descri = desc[1].componentsSeparatedByString("'")
                        var description: AnyObject=descri[1]
                        println(description)
                        
                        if(flag=="True"){
                            self.BackButton.enabled = true
                            self.ConfirmButton.enabled = true
                            GroupUpdate().addAGroupToLocalDatabase(idCar, name: self.GroupName.text)
                            self.dismissViewControllerAnimated(true, completion: nil)
                        }
                    }
                }
                else{
                    self.BackButton.enabled = true
                    self.ConfirmButton.enabled = true
                }
            }
            
            
        })
        task.resume()
        
    }
    
    
}
