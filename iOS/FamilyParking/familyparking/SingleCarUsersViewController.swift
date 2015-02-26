//
//  SingleCarUsersViewController.swift
//  familyparking
//
//  Created by mauro piva on 23/02/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import Foundation
import UIKit
import CoreData


class SingleCarUsersViewController: UIViewController, UITextFieldDelegate, UITableViewDelegate, UITableViewDataSource {
    

    var car : NSObject?
    var tableView: UITableView!
    var textField: UITextField!
    var people = [NSManagedObject]()
    var selectedCar:NSObject = ""
        
        override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: NSBundle?) {
            super.init(nibName:nibNameOrNil, bundle: nibBundleOrNil)
        }
        
        required init(coder aDecoder: NSCoder)
        {
            super.init(coder: aDecoder)
        }
        
        func tableView(tableView: UITableView!, canEditRowAtIndexPath indexPath: NSIndexPath!) -> Bool {
            return true
        }
        
        
        override func viewDidLoad() {
            super.viewDidLoad()
            self.title = car?.valueForKey("name")?.description
            
            self.tableView = UITableView(frame: CGRectMake(0,0, self.view.bounds.size.width, self.view.bounds.size.height-0), style: UITableViewStyle.Plain)
            self.tableView.registerClass(MyTableViewCell.self, forCellReuseIdentifier: "myCell")
            self.tableView.delegate = self
            self.tableView.dataSource = self
            self.view.addSubview(self.tableView)
        }
        
        
        func tableView(tableView: UITableView,
            numberOfRowsInSection section: Int) -> Int {
                return people.count
        }
        
        func tableView(tableView: UITableView,      cellForRowAtIndexPath
            indexPath: NSIndexPath) -> UITableViewCell {
                
                let cell =
                tableView.dequeueReusableCellWithIdentifier("myCell")
                    as UITableViewCell
                
                let person = people[indexPath.row]
                cell.textLabel.text = person.valueForKey("username") as String?
                cell.detailTextLabel?.text = person.valueForKey("email") as String?
                return cell
        }
        func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath){
            
            RemoveConfirmation(indexPath)
            
        }
        
        
        func textFieldShouldReturn(textField: UITextField) -> Bool {
            
            // tableViewData.append(textField.text)
            saveName(textField.text)
            textField.text = ""
            self.tableView.reloadData()
            textField.resignFirstResponder()
            return true
        }
        func saveName(email: String) {
            //1
            let appDelegate =
            UIApplication.sharedApplication().delegate as AppDelegate
            
            let managedContext = appDelegate.managedObjectContext!
            
            //2
            let entity =  NSEntityDescription.entityForName("Users",
                inManagedObjectContext:
                managedContext)
            
            
            
            let person = NSManagedObject(entity: entity!,
                insertIntoManagedObjectContext:managedContext)
            
            //3
            person.setValue(email, forKey: "username")
            person.setValue(email, forKey: "email")
            person.setValue(car?.valueForKey("id"), forKey: "carId")
            
            
            //4
            var error: NSError?
            if !managedContext.save(&error) {
                println("Could not save \(error), \(error?.userInfo)")
            }
            //5
            people.append(person)
            
        }
        
        func removeName(name: NSManagedObject) {
            //1
            let appDelegate =
            UIApplication.sharedApplication().delegate as AppDelegate
            
            let managedContext = appDelegate.managedObjectContext!
            
            //2
            managedContext.deleteObject(name)
            
            //4
            var error: NSError?
            if !managedContext.save(&error) {
                println("Could not save \(error), \(error?.userInfo)")
            }
            
            
        }
        
        override func viewWillAppear(animated: Bool) {
            super.viewWillAppear(animated)
            
            //1
            let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
            let managedContext = appDelegate.managedObjectContext!
            
            //2
            let fetchRequest = NSFetchRequest(entityName:"Users")
            
            //3
            var error: NSError?
            
            let fetchedResults =
            managedContext.executeFetchRequest(fetchRequest,
                error: &error) as [NSManagedObject]?
            
            if let results = fetchedResults {
                people = results
            } else {
                println("Could not fetch \(error), \(error!.userInfo)")
            }
            
            var tmp = [NSManagedObject]()
            
            for man in people {
                
                if(man.valueForKey("carId")?.description==car?.valueForKey("id")?.description){
                    tmp.append(man)
                }
            }
            people = tmp
            
        }
        
        // var selectedCar : NSObject
        
        @IBAction func RemoveConfirmation(index: NSIndexPath) {
            
            var mail = self.people[index.item].valueForKey("username")?.description
            
            var alert = UIAlertController(title: "What to do?",
                message: mail,
                preferredStyle: .Alert)
            
            
            let deleteAction = UIAlertAction(title: "Delete",
                style: .Default) { (action: UIAlertAction!) -> Void in
                    
                    let toRem = self.people.removeAtIndex(index.item)
                    self.removeName(toRem)
                    self.tableView.reloadData()
            }
            let cancelAction = UIAlertAction(title: "Cancel",
                style: .Default) { (action: UIAlertAction!) -> Void in
            }
            alert.addAction(deleteAction)
            alert.addAction(cancelAction)
            presentViewController(alert,
                animated: true,
                completion: nil)
        }
    
    @IBAction func AddUser(sender: AnyObject) {
        
        var alert = UIAlertController(title: "One Car",
            message: "Right now, only one car is admitted",
            preferredStyle: .Alert)
        
        let saveAction = UIAlertAction(title: "Save",
            style: .Default) { (action: UIAlertAction!) -> Void in
                
                let textField = alert.textFields![0] as UITextField
                self.saveName(textField.text)
                //self.names.append(textField.text)
                self.tableView.reloadData()
        }
        
        let cancelAction = UIAlertAction(title: "Cancel",
            style: .Default) { (action: UIAlertAction!) -> Void in
        }
        
            alert.addTextFieldWithConfigurationHandler {
               (textField: UITextField!) -> Void in
            }
        
            alert.addAction(saveAction)
        alert.addAction(cancelAction)
        
        presentViewController(alert,
            animated: true,
            completion: nil)
    }
    
    

}
