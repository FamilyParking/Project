//
//  Cars.swift
//  MapDemo
//
//  Created by mauro piva on 16/02/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import Foundation
import UIKit
import CoreData
class CarList{
    func saveName(name: String, id:String) {
        let appDelegate =
        UIApplication.sharedApplication().delegate as AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        let entity =  NSEntityDescription.entityForName("Entity",
            inManagedObjectContext:
            managedContext)
        
        let person = NSManagedObject(entity: entity!,
            insertIntoManagedObjectContext:managedContext)
    
        person.setValue(name, forKey: "name")
        person.setValue(id, forKey:"id")
    
        var error: NSError?
        if !managedContext.save(&error) {
            println("Could not save \(error), \(error?.userInfo)")
        }
   }

    func removeName(name: NSManagedObject) {
        
        let appDelegate =
        UIApplication.sharedApplication().delegate as AppDelegate
    
        let managedContext = appDelegate.managedObjectContext!
        managedContext.deleteObject(name)
        
        var error: NSError?
        if !managedContext.save(&error) {
            println("Could not save \(error), \(error?.userInfo)")
        }
    }
    
    func removeAll() {
       
        
        //1
        let appDelegate =
        UIApplication.sharedApplication().delegate as AppDelegate
        
        let managedContext = appDelegate.managedObjectContext!
        
        
        
        //2
        let fetchRequest = NSFetchRequest(entityName:"Entity")
        
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
            
            removeName(man)
            
           // println(man.valueForKey("name")?.description)
            
        }
    }
    
    func removeCode(id:String){
        
        //1
        let appDelegate =
        UIApplication.sharedApplication().delegate as AppDelegate
        
        let managedContext = appDelegate.managedObjectContext!

        //2
        let fetchRequest = NSFetchRequest(entityName:"Entity")
        
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
                removeName(man)
            }
            
        }
    }
    
    
}