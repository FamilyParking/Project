//
//  CarGroupsUpdate.swift
//  familyparking
//
//  Created by mauro piva on 19/02/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import Foundation
import UIKit
import CoreData

class CarUpdate{
    
    func downloadCar(){
        
            var request = NSMutableURLRequest(URL: NSURL(string: "http://first-vision-798.appspot.com/getAllCars_fromEmail")!)
            var session = NSURLSession.sharedSession()
            request.HTTPMethod = "POST"
            
            
            var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
            let code = prefs.objectForKey("PIN") as String
            let mail = prefs.objectForKey("EMAIL") as String
            var params = ["Code":code,
                "Email":mail] as Dictionary<String, NSObject>
            
            var err: NSError?
            request.HTTPBody = NSJSONSerialization.dataWithJSONObject(params, options: nil, error: &err)
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue("application/json", forHTTPHeaderField: "Accept")
            
            var task = session.dataTaskWithRequest(request, completionHandler: {data, response, error -> Void in
                println("Response: \(response)")
                var strData = NSString(data: data, encoding: NSUTF8StringEncoding)
                println("Body: \(strData)")
                if(strData!.length>3){
                    println("Check 1 Complete")
                    var array = strData!.componentsSeparatedByString("[")
                    if(array.count==2){
                        var fl = array[1].componentsSeparatedByString("]")
                        var test:String = fl[0] as String
                        dispatch_async(dispatch_get_main_queue(), { () -> Void in
                            if(!test.isEmpty){
                                var cars = test.componentsSeparatedByString("\",\"")
                                self.removeAllCar()
                                for name:String in cars{
                                    if(!name.isEmpty){
                                        var name2 = name.componentsSeparatedByString("'")
                                        self.addACarToLocalDatabase(name2[19], name: name2[15], lat: name2[3], long: name2[11])
                                    }
                                }
                            }
                        })
                        
                    }
                }
                var err: NSError?
                var json = NSJSONSerialization.JSONObjectWithData(data, options: .MutableLeaves, error: &err) as? NSDictionary
                
                // Did the JSONObjectWithData constructor return an error? If so, log the error to the console
                if(err != nil) {
                    println(err!.localizedDescription)
                    let jsonStr = NSString(data: data, encoding: NSUTF8StringEncoding)
                    println("Error could not parse JSON: '\(jsonStr)'")
                }
                else {
                    // The JSONObjectWithData constructor didn't return an error. But, we should still
                    // check and make sure that json has a value using optional binding.
                    if let parseJSON = json {
                        // Okay, the parsedJSON is here, let's get the value for 'success' out of it
                        var success = parseJSON["success"] as? Int
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
    
        func addACarToLocalDatabase(code:String,name:String,lat:String,long:String){
            
            var car = code.stringByReplacingOccurrencesOfString("\"", withString: "")
            println("Ora aggiungo l'auto \(code)")
            var id = code.stringByReplacingOccurrencesOfString("\"",withString: "")
            let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
            let managedContext = appDelegate.managedObjectContext!
            let entity =  NSEntityDescription.entityForName("Car",
                inManagedObjectContext:
                managedContext)
            let person = NSManagedObject(entity: entity!,
                insertIntoManagedObjectContext:managedContext)
            
            person.setValue(name, forKey: "name")
            person.setValue(id, forKey:"id")
            person.setValue(lat, forKey:"lat")
            person.setValue(long, forKey:"long")
            
            
            var error: NSError?
            if !managedContext.save(&error) {
                println("Could not save \(error), \(error?.userInfo)")
            }
    }
    
    func removeCarByNSObj(name: NSManagedObject) {
        
        let appDelegate =
        UIApplication.sharedApplication().delegate as AppDelegate
        
        let managedContext = appDelegate.managedObjectContext!
        managedContext.deleteObject(name)
        
        var error: NSError?
        if !managedContext.save(&error) {
            println("Could not save \(error), \(error?.userInfo)")
        }
    }
    
    func removeAllCar() {
        
        let appDelegate =
        UIApplication.sharedApplication().delegate as AppDelegate
        
        let managedContext = appDelegate.managedObjectContext!
        let fetchRequest = NSFetchRequest(entityName:"Car")

        var error: NSError?
        var people = [NSManagedObject]()
        let fetchedResults =
        managedContext.executeFetchRequest(fetchRequest,
            error: &error) as [NSManagedObject]?
        
        if let results = fetchedResults {
            people = results
        } else {
            println("Could not fetch \(error), \(error!.userInfo)")
        }
        
        for man in people {
            removeCarByNSObj(man)
        }
    }
    
    func removeCarByCode(id:String){
        
        //1
        let appDelegate =
        UIApplication.sharedApplication().delegate as AppDelegate
        
        let managedContext = appDelegate.managedObjectContext!
        
        //2
        let fetchRequest = NSFetchRequest(entityName:"Car")
        
        //3
        var error: NSError?
        var people = [NSManagedObject]()
        let fetchedResults =
        managedContext.executeFetchRequest(fetchRequest,
            error: &error) as [NSManagedObject]?
        
        if let results = fetchedResults {
            people = results
        } else {
            println("Could not fetch \(error), \(error!.userInfo)")
        }
        
        for man in people {
            println(man.valueForKey("id")?.description)
            println(id)
            if(man.valueForKey("id")?.description==id){
                removeCarByNSObj(man)
            }
        }
    }


    
}