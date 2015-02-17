//
//  AddCar.swift
//  MapDemo
//
//  Created by mauro piva on 01/02/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import UIKit
import CoreData

class AddCar: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var CarModel: UITextField!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        CarModel.delegate = self
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func AddACar() {
        let model = self.CarModel.text
        if(!model.isEmpty){
         //   var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
         //   let carsNumber:Int = prefs.integerForKey("HOWMANYCARS") as Int
         //   prefs.setInteger(carsNumber + 1, forKey: "HOWMANYCARS")
         //   prefs.synchronize()
         //  self.dismissViewControllerAnimated(true, completion: nil)
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
        CarModel.resignFirstResponder()
    }

    func addCarToServer(){
        var request = NSMutableURLRequest(URL: NSURL(string: "http://first-vision-798.appspot.com/createCar")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let pin:String = prefs.objectForKey("PIN") as String
        let mail:String = prefs.objectForKey("EMAIL") as String
       // prefs.setInteger(carsNumber + 1, forKey: "HOWMANYCARS")
        var params = ["Code":pin,
            // "Code":PinTextField.text,
            "Name":CarModel.text,
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
                      //      self.saveCarLocal(idCar);
                            let carsNumber:Int = prefs.integerForKey("HOWMANYCARS") as Int;
                            prefs.setInteger(carsNumber + 1, forKey: "HOWMANYCARS")
                            prefs.setObject(idCar, forKey: "ACTIVECAR")
                            prefs.setObject(idCar, forKey: "CARCODE")
                            prefs.synchronize()
                            self.dismissViewControllerAnimated(true, completion: nil)
                        }
                    }
                }
                else{
                    //TODO Show general error
                }
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
    
    func addGroupToServer(){
        var request = NSMutableURLRequest(URL: NSURL(string: "http://first-vision-798.appspot.com/createGroup")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let pin:String = prefs.objectForKey("PIN") as String
        let mail:String = prefs.objectForKey("EMAIL") as String
        // prefs.setInteger(carsNumber + 1, forKey: "HOWMANYCARS")
        var params = ["Code":pin,
            // "Code":PinTextField.text,
            "Name":CarModel.text,
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
            }
            
        })
        task.resume()
    }

    
    func saveCarLocal(idCar:String){
        CarList().saveName(CarModel.text,id: idCar)
            //1
            let appDelegate =
            UIApplication.sharedApplication().delegate as AppDelegate
        
            let managedContext = appDelegate.managedObjectContext!
            
            //2
            let entity =  NSEntityDescription.entityForName("Car",
                inManagedObjectContext:
                managedContext)
            
            
            
            let person = NSManagedObject(entity: entity!,
                insertIntoManagedObjectContext:managedContext)
            
            //3
            person.setValue(idCar, forKey: "id")
            person.setValue(0, forKey: "latitude")
            person.setValue(0, forKey: "longitude")
            person.setValue(CarModel.text, forKey: "name")

        
            
            //4
            var error: NSError?
            if !managedContext.save(&error) {
                println("Could not save \(error), \(error?.userInfo)")
            }  

        
    
    }

}
