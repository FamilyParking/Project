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

class CarUpdate: UIViewController{
    
    func downloadCar(mvc:MapViewController){
        
            var request = NSMutableURLRequest(URL: NSURL(string: Comments().serverPath + "getAllCars")!)
            var session = NSURLSession.sharedSession()
            request.HTTPMethod = "POST"
            
            
            var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
            let code = prefs.objectForKey("PIN") as String?
        if (code? == nil){
            return
        }
            let mail = prefs.objectForKey("EMAIL") as String
            let logged = prefs.integerForKey("ISLOGGEDIN")
            println(code)
            var user = ["Code":code!,
                        "Email":mail] as Dictionary<String, NSObject>
            var params = ["User":user,
            ] as Dictionary<String, NSObject>
        
            var err: NSError?
            request.HTTPBody = NSJSONSerialization.dataWithJSONObject(params, options: nil, error: &err)
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue("application/json", forHTTPHeaderField: "Accept")
            
            var task = session.dataTaskWithRequest(request, completionHandler: {data, response, error -> Void in
                println("Response: \(response)")
                
                if(response == nil){
                    dispatch_async(dispatch_get_main_queue(), { () -> Void in
                        var alertView:UIAlertView = UIAlertView()
                        alertView.title = NSLocalizedString("No_internet_connection",comment:"No internet connection")
                        alertView.message = NSLocalizedString("Please_check_your_internet_connection",comment:"Please, check your internet connection.")
                        alertView.delegate = self
                        alertView.addButtonWithTitle("OK")
                        alertView.show()
                    })
                    return
                    
                }
                
                var strData = NSString(data: data, encoding: NSUTF8StringEncoding)
                println("Body: \(strData)")
                var castato:NSHTTPURLResponse = response as NSHTTPURLResponse
                println(castato.statusCode)
                if(!(castato.statusCode==200)){
                   
                    
                    dispatch_async(dispatch_get_main_queue(), { () -> Void in
                        var alertView:UIAlertView = UIAlertView()
                        alertView.title = "Server Error"
                        alertView.message = "Our Monkeys are working! Try in minutes!"
                        alertView.delegate = self
                        alertView.addButtonWithTitle("OK")
                        alertView.show()
                        })
                    
                    return
                }
                var err: NSError?
                if var tst = err{
                    println("NO CONNECTION RIGHT")
                }
                else {
                    var json : NSDictionary? = NSJSONSerialization.JSONObjectWithData(data!, options: .MutableContainers, error: &err) as? NSDictionary
                    if var ex = err{
                            println("CANT PARSE")
                    }else if(err==nil&&json?["Flag"] as Bool){
                        if var cars:NSArray = json?["Object"]! as? NSArray{
                            self.removeAllCar()
                            self.removeAllUsers()
                            for carz in cars{
                                self.addACarToLocalDatabase(carz["ID_car"] as String, name: carz["Name"] as String, lat: carz["Latitude"] as String, long: carz["Longitude"] as String, brand: carz["Brand"] as String,lastPark:carz["Timestamp"] as String,isParked:carz["isParked"] as Bool,UUID:carz["UUID"] as String,Bmin:carz["Bmin"] as String,Bmaj:carz["Bmaj"] as String)
                                
                                let users = carz["Users"] as NSArray
                                for user in users{
                                    self.addUserToLocalDatabase(user["Email"]as String, name: user["Name"] as String, car: carz["ID_car"] as String)
                                    println(user)
                                }
                                
                            }
                        }
                        mvc.updateCars()
                    } else {
                        if((json?["Object"] as NSInteger) == 3) {self.resetSystem();}
                        if((json?["Object"] as NSInteger) == 2) {self.resetSystem();}
                       // if((json?["Object"] as NSInteger) == 4) {self.resetSystem();}
                        
                    }
                }
                //else{
                  //  println("Generic Error")
                    
                //}
        
            })
            if(logged==1){
                task.resume()
            }
        }
    
    func addACarToLocalDatabase(code:String,name:String,lat:String,long:String,brand:String,lastPark:String,isParked:Bool,UUID:String,Bmin:String,Bmaj:String){
        
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
            person.setValue(lastPark, forKey: "lastPark")
            person.setValue(isParked.description, forKey: "isParked")
            person.setValue(UUID,forKey:"buuid")
            person.setValue(Bmaj,forKey:"bmaj")
            person.setValue(Bmin,forKey:"bmin")
        
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
    
    func countCar() ->Int {
        
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
        return people.count
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
   
    
    func  getCarByIBeacon() -> [String]{
        
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
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()

        var UUID = prefs.objectForKey("BUUID") as String
        var Maj =  prefs.objectForKey("BMAJ") as String
        var Min = prefs.objectForKey("BMIN") as String
        
        for man in people {
            println(man.valueForKey("id")?.description)
            
            if((man.valueForKey("buuid")?.description==UUID)&&(man.valueForKey("bmin")?.description==Min)&&(man.valueForKey("bmaj")?.description==Maj)){
                var name : String = man.valueForKey("name")! as String
                var id : String = man.valueForKey("id")! as String
                return [name,id]
            }
            
        }
        
        return ["NONE","NONE"]
    }
    
    
    
    func updateCarIBeacon(Id_car:String,UUID:String,Maj:String,Min:String){
        insertServerIBeacon(Id_car,UUID: UUID,Maj: Maj,Min: Min)
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
       // var carToUpdate:NSManagedObject = NSManagedObject()
        
        
        
        let entity =  NSEntityDescription.entityForName("Car",
            inManagedObjectContext:
            managedContext)
        let person = NSManagedObject(entity: entity!,
            insertIntoManagedObjectContext:managedContext)
       
        
        
        
        for man in people {
            println(man.valueForKey("id")?.description)
            
            if(man.valueForKey("id")?.description==Id_car){
                person.setValue(man.valueForKey("name"), forKey: "name")
                person.setValue(man.valueForKey("id"), forKey:"id")
                person.setValue(man.valueForKey("lat"), forKey:"lat")
                person.setValue(man.valueForKey("long"), forKey:"long")
                person.setValue(man.valueForKey("brand"), forKey:"brand")
                person.setValue(man.valueForKey("lastPark"), forKey:"lastPark")
                person.setValue(man.valueForKey("isParked"), forKey:"isParked")
                person.setValue(UUID, forKey: "buuid")
                person.setValue(Min, forKey: "bmin")
                person.setValue(Maj, forKey: "bmaj")
                removeCarByNSObj(man)
                break
            }
        }
        
        
        //take infos from carToUpdate
    
        
        
        if !managedContext.save(&error) {
            println("Could not save \(error), \(error?.userInfo)")
        }
        
        
        
        
    }

    
    
    func serverErrorPopUp(){
        var alertView:UIAlertView = UIAlertView()
        alertView.title = "Server Error"
        alertView.message = "Our Monkeys are working! Try in minutes!"
        alertView.delegate = self
        alertView.addButtonWithTitle("OK")
        alertView.show()
    }
    
    
    func insertServerIBeacon(Id_car:String,UUID:String,Maj:String,Min:String) {
        
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let code = prefs.objectForKey("PIN") as String
        let mail = prefs.objectForKey("EMAIL") as String
        let username = prefs.objectForKey("USERNAME") as String
        
        var request = NSMutableURLRequest(URL: NSURL(string: Comments().serverPath + "updateUUID")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        
        var user = ["Code":code,
            "Name":username,
            "Email":mail] as Dictionary<String, NSObject>
        
        var car = ["UUID":UUID,
            "Bmin":Min,
            "Bmaj":Maj,
            
            "ID_car":Id_car] as Dictionary<String,NSObject>
        
        var params = ["User":user,
            "Car":car] as Dictionary<String, NSObject>
        
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
                return
            }
            
            var castato:NSHTTPURLResponse = response as NSHTTPURLResponse
            println(castato.statusCode)
            if(castato.statusCode==500){
                
                
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    var alertView:UIAlertView = UIAlertView()
                    alertView.title = "Server Error"
                    alertView.message = "Our Monkeys are working! Try in minutes!"
                    alertView.delegate = self
                    alertView.addButtonWithTitle("OK")
                    alertView.show()
                })
                
                return
            }
            
            var strData = NSString(data: data, encoding: NSUTF8StringEncoding)
            println("Body: \(strData)")
            var err: NSError?
            var json = NSJSONSerialization.JSONObjectWithData(data, options: .MutableLeaves, error: &err) as? NSDictionary
            
            if(err == nil&&(!(response==nil))){
                
                if((json!["Flag"] as Bool) == true){
                    dispatch_async(dispatch_get_main_queue(),{() -> Void in
                        println("")
                        self.navigationController?.popViewControllerAnimated(true)
                    })
                }
                else {
                   // self.noInternetPopUp()
                }
            }else
            {
              //  self.noInternetPopUp()
            }
            
        })
        
        task.resume()
        // self.dismissViewControllerAnimated(true, completion: nil)
    }
    
    
    func resetSystem(){
        println("System Reset")
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        prefs.setInteger(0, forKey:"ISLOGGEDIN")
        prefs.synchronize()
  //      prefs.syncronize()
        
        removeAllCar()
        removeAllUsers()
      
        exit(0)
        
        }
    

   
}