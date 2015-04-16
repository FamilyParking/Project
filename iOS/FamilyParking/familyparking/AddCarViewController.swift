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
import UIKit

import AddressBook
import AddressBookUI

class AddCarViewController: UIViewController, UITextFieldDelegate,UIPickerViewDataSource,UIPickerViewDelegate {
 
    
  
    
   // @IBOuRIFAREtlet weak var BackButton: UIButton!
 //   @IBOuRIFAREtlet weak var ConfirmButton: UIButton!
    @IBOutlet weak var CarName: UITextField!
    @IBOutlet weak var CarTarga: UITextField!
    
    @IBOutlet weak var BrandPicker: UIPickerView!
  //  @IBOutlet weak var BackButt: UIButton!
    @IBOutlet weak var ConfButt: UIButton!
    var brand:String = "add_car"
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        BrandPicker.delegate = self
        BrandPicker.dataSource = self
    }
    
    func numberOfComponentsInPickerView(pickerView: UIPickerView) -> Int {
        return pickerData.count
    }
    func pickerView(pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return pickerData[component].count
    }
    func pickerView(pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String! {
        return pickerData[component][row]
    }
    func pickerView(pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        updateLabel()
    }
    
    func updateLabel(){
        let size = pickerData[0][BrandPicker.selectedRowInComponent(0)]
        brand = size
        
        println(" + size + " + size)
    }
    let pickerData = [
        ["alfaromeo","audi","bmw","chevrolet","citroen","dacia","ferrari","fiat","ford","honda","hunday","jaguar","jeep","kia","lancia","land_rover","Lexus","mazda","mercedes","mg","mini","mitsubishi","nissan","opel","peugeot","porsche","ranault","seat","skoda","smart","subaru","toyora","volkswagen","volvo"]
    ]
    @IBAction func Back() {
         self.dismissViewControllerAnimated(true, completion: nil)
    }
    
    
    @IBAction func addACar() {
        
        CarName.enabled = false
        var model = self.CarName.text
        if(!model.isEmpty){
            //BackButt.enabled = false
            ConfButt.enabled = false
            addCarToServer()
        }
        else{
            //TODO emtpy text
            println(model)
            CarName.enabled = true
            noCarNameAlert()
        }
    }
    
    func textFieldShouldReturn(textField: UITextField) -> Bool // called when 'return' key pressed. return NO to ignore.
    {
        textField.resignFirstResponder()
        self.view.endEditing(true)
         
        return false;
        
    }
    
    override func touchesBegan(touches: Set<NSObject>, withEvent event: UIEvent) {
        CarName.resignFirstResponder()
    }
    
    func addCarToServer(){
        var request = NSMutableURLRequest(URL: NSURL(string: Comments().serverPath + "createCar")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let pin:String = prefs.objectForKey("PIN") as! String
        let mail:String = prefs.objectForKey("EMAIL") as! String
       
        var otherUsers = ["Code":pin,
            "Email":mail] as Dictionary<String, NSObject>
        
        
        var car = [
        "Bluetooth_MAC":"",
        "Bluetooth_name":"",
        "Brand":brand,
        "Users":"",
        "Name":CarName.text,
        "Register":CarTarga.text,
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
                self.CarName.enabled = true
                self.ConfButt.enabled = true
                
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
                if((json!["Flag"] as! Bool) == true){
                            //self.BackButt.enabled = true
                            self.ConfButt.enabled = true
                    CarUpdate().addACarToLocalDatabase((json!["Object"] as! NSNumber).description, name: self.CarName.text, lat: "0", long: "0",brand:"",lastPark:"never",isParked:false,UUID:"",Bmin:"",Bmaj:"",register:self.CarTarga.text)
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
    
    func noCarNameAlert(){
        var alertView:UIAlertView = UIAlertView()
        alertView.title = "Car Name is empty"
        alertView.message = "Please, insert your car name."
        alertView.delegate = self
        alertView.addButtonWithTitle("OK")
        alertView.show()
    }
    
    
    
}

