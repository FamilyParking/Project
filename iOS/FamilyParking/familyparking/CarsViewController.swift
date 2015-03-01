//
//  CarsViewController.swift
//  familyparking
//
//  Created by mauro piva on 19/02/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import Foundation
import UIKit
import CoreData

class CarsViewController: UIViewController, UITextFieldDelegate, UITableViewDelegate, UITableViewDataSource {
    
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
            cell.textLabel.text = person.valueForKey("name") as String?
            cell.detailTextLabel?.text = "hi"
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
    func saveName(name: String) {
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
        person.setValue(name, forKey: "name")
        
        
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
        
        
    func removeCarFromServer(name: NSManagedObject) {
        var request = NSMutableURLRequest(URL: NSURL(string: "http://first-vision-798.appspot.com/deleteCar")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        
        
        var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let code = prefs.objectForKey("PIN") as String
        let mail = prefs.objectForKey("EMAIL") as String
        println(code)
        var user = ["Code":code,
            "Email":mail] as Dictionary<String, NSObject>
        
        var Car = ["ID_car":name.valueForKey("id")!.description as String,
                ] as Dictionary<String, NSObject>
        
        
        var params = ["User":user,
            "Car":Car] as Dictionary<String, NSObject>
        
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
        //   println(json!["flag"] as Bool)
            if((json!["flag"] as Bool) == true){
                println(json!["flag"] as Bool)

                self.removeName(name)
                self.tableView.reloadData()
            }
            
            println(json)
            
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
        
        
        
    
    
    override func viewWillAppear(animated: Bool) {
        
        
        super.viewWillAppear(animated)
        println("Carico view auto")
        //1
        let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        
        //2
        let fetchRequest = NSFetchRequest(entityName:"Car")
        
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
        
        for man in people {
            
            println(man.valueForKey("name")?.description)
            
        }
    }
    
   // var selectedCar : NSObject
    
    @IBAction func RemoveConfirmation(index: NSIndexPath) {
        
        var mail = self.people[index.item].valueForKey("name")?.description
        
        var alert = UIAlertController(title: "What to do?",
            message: mail,
            preferredStyle: .Alert)
        
        let saveAction = UIAlertAction(title: "Find",
            style: .Default) { (action: UIAlertAction!) -> Void in
            }
        
        let deleteAction = UIAlertAction(title: "Delete",
            style: .Default) { (action: UIAlertAction!) -> Void in
                
                let toRem = self.people.removeAtIndex(index.item)
                self.removeCarFromServer(toRem)
                //self.removeName(toRem)
                //self.tableView.reloadData()
        }
        let cancelAction = UIAlertAction(title: "Cancel",
            style: .Default) { (action: UIAlertAction!) -> Void in
        }
        let editAction = UIAlertAction(title: "Edit",
            style: .Default) { (action: UIAlertAction!) -> Void in
                
                self.selectedCar = self.people[index.row]
                
                // Create an instance of PlayerTableViewController and pass the variable
                
                
               // let destinationVC = SingleCarUsersViewController()
               // destinationVC.carID = selectedCar.valueForKey("id")?.description
                
                // Let's assume that the segue name is called playerSegue
                // This will perform the segue and pre-load the variable for you to use
              //  destinationVC.performSegueWithIdentifier("user_group", sender: self)
                self.performSegueWithIdentifier("user_group", sender: self)
        }
        
        alert.addAction(saveAction)
        alert.addAction(cancelAction)
        alert.addAction(deleteAction)
        alert.addAction(editAction)
        presentViewController(alert,
            animated: true,
            completion: nil)
    }
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if segue.identifier == "user_group"
        {
            if let destinationVC = segue.destinationViewController as? SingleCarUsersViewController{
                destinationVC.car = selectedCar
            }
        }
    }

}