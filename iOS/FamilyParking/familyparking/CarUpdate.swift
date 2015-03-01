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
    
    func downloadCar(mvc:MapViewController){
        
            var request = NSMutableURLRequest(URL: NSURL(string: "http://first-vision-798.appspot.com/getAllCars")!)
            var session = NSURLSession.sharedSession()
            request.HTTPMethod = "POST"
            
            
            var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
            let code = prefs.objectForKey("PIN") as String
            let mail = prefs.objectForKey("EMAIL") as String
            println(code)
            var user = ["Code":code,
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
                println("Body: \(strData)")
               
                var err: NSError?
                
                var json : NSDictionary? = NSJSONSerialization.JSONObjectWithData(data!, options: .MutableContainers, error: &err) as? NSDictionary
            
                if var cars:NSArray = json?["object"]! as? NSArray{
                    self.removeAllCar()
                    self.removeAllUsers()
                    for carz in cars{
                        self.addACarToLocalDatabase(carz["ID_car"] as String, name: carz["Name"] as String, lat: carz["Latitude"] as String, long: carz["Longitude"] as String, brand: carz["Brand"] as String)
                        
                            let users = carz["Users"] as NSArray
                            for user in users{
                                self.addUserToLocalDatabase(user["Email"]as String, name: user["Nome"] as String, car: carz["ID_car"] as String)
                                println(user)
                            }
                        
                    }
                }
        //        MapViewController().updateCars()
                mvc.updateCars()
                //[0] as NSDictionary
                //println(car["Brand"])
                // Did the JSONObjectWithData constructor return an error? If so, log the error to the console
                if(err != nil) {
                    println(err!.localizedDescription)
                    let jsonStr = NSString(data: data, encoding: NSUTF8StringEncoding)
                    println("Error could not parse JSON: '\(jsonStr)'")
                }
                
            })
            
            task.resume()
            
        }
    
    func addACarToLocalDatabase(code:String,name:String,lat:String,long:String,brand:String){
        
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
            person.setValue(brand, forKey:"brand")
            
            var error: NSError?
            if !managedContext.save(&error) {
                println("Could not save \(error), \(error?.userInfo)")
            }
    }
    
    func addUserToLocalDatabase(email:String,name:String,car:String){
        
        let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        let entity =  NSEntityDescription.entityForName("Users",
            inManagedObjectContext:
            managedContext)
        let person = NSManagedObject(entity: entity!,
            insertIntoManagedObjectContext:managedContext)
        
        person.setValue(car, forKey: "carId")
        person.setValue(email, forKey:"email")
        person.setValue(name, forKey:"username")
        
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
            managedContext.deleteObject(man)
        }
    }
    
    func removeAllUsers() {
        
        let appDelegate =
        UIApplication.sharedApplication().delegate as AppDelegate
        
        let managedContext = appDelegate.managedObjectContext!
        let fetchRequest = NSFetchRequest(entityName:"Users")
        
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
            managedContext.deleteObject(man)
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