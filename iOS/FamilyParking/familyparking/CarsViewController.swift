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
       
    //  self.tableView = UITableView(frame: CGRectMake(0,0, self.view.bounds.size.width, self.view.bounds.size.height-0), style: UITableViewStyle.Plain)
        var systemVersion = NSString(string: UIDevice.currentDevice().systemVersion).doubleValue

        if systemVersion >= 8 {
        
        self.tableView = UITableView(frame: CGRectMake(0,0, self.view.bounds.size.width, self.view.bounds.size.height-0), style: UITableViewStyle.Grouped)
        }
        else{
         self.tableView = UITableView(frame: CGRectMake(0,44, self.view.bounds.size.width, self.view.bounds.size.height-44), style: UITableViewStyle.Grouped)
        }
        self.tableView.registerClass(MyTableViewCell.self, forCellReuseIdentifier: "myCell")
        self.tableView.delegate = self
        self.tableView.dataSource = self
        self.view.addSubview(self.tableView)
       // println(self.tableView.rowHeight.distanceTo(0))
        
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
            cell.textLabel?.text = person.valueForKey("name") as String?
            if((person.valueForKey("isParked") as String) == "true"){
                cell.detailTextLabel?.text = person.valueForKey("lastPark") as String?
            }else{
                
            }
            cell.selectionStyle = UITableViewCellSelectionStyle.Gray;
            println(person.valueForKey("brand") as String)
            cell.imageView?.image=UIImage(named:(person.valueForKey("brand") as String))
            return cell
    }
    
    func tableView(tableView: UITableView, editActionsForRowAtIndexPath indexPath: NSIndexPath) -> [AnyObject]? {
      //  println("TABEL"+self.tableView.rowHeight.description)
        var mail = self.people[indexPath.item].valueForKey("name")?.description
        
        let deleteClosure = { (action: UITableViewRowAction!, indexPath: NSIndexPath!) -> Void in
            println("Delete closure called")
            let toRem = self.people[indexPath.item]
            self.removeCarFromServer(toRem,index: indexPath.item)
        }
        
        let moreClosure = { (action: UITableViewRowAction!, indexPath: NSIndexPath!) -> Void in
            println("More closure called")
            self.selectedCar = self.people[indexPath.row]
            self.performSegueWithIdentifier("user_group", sender: self)
        }
        let addIBeaconClosure = { (action: UITableViewRowAction!, indexPath: NSIndexPath!) -> Void in
            println("More ibeacon called")
            let toRem = self.people[indexPath.item].valueForKey("id")?.description
            self.associateIBeacon(toRem!)
        }
        let deleteAction = UITableViewRowAction(style: .Default, title: "X", handler: deleteClosure)
        let moreAction = UITableViewRowAction(style: .Normal, title: "Sharers", handler: moreClosure)
        let addIBeaconAction = UITableViewRowAction(style: UITableViewRowActionStyle.Normal, title: "iBeacon", handler: addIBeaconClosure)
        
     //   UIGraphicsBeginImageContext(self.view.frame.size)
     //   UIImage(named: "iBeaconIcon")?.drawInRect(CGRectMake(0, 0, 45, 45))
     //   var image: UIImage = UIGraphicsGetImageFromCurrentImageContext()
     //   UIGraphicsEndImageContext()
     //   addIBeaconAction.backgroundColor = UIColor(patternImage: image)
        
        
      //  UIGraphicsBeginImageContext(self.view.frame.size)
      //  UIImage(named: "trashIcon")?.drawInRect(CGRectMake(0, 0, 45, 45))
      //  image = UIGraphicsGetImageFromCurrentImageContext()
      //  UIGraphicsEndImageContext()
     //   deleteAction.backgroundColor = UIColor(patternImage: image)
        return [deleteAction,addIBeaconAction, moreAction]
        
    }
        /*

    
                let associateBeaconAction = UIAlertAction(title: "Associate iBeacon",
        style: .Default) { (action: UIAlertAction!) -> Void in
        
    
        let toRem = self.people[index.item].valueForKey("id")?.description
        self.associateIBeacon(toRem!)
        /
        }
       
    }
    */
    func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        
        // Intentionally blank. Required to use UITableViewRowActions
    }

    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath){
       
        var systemVersion = NSString(string: UIDevice.currentDevice().systemVersion).doubleValue
        println(systemVersion)
        if systemVersion >= 8 {
            self.find(self.people[indexPath.item])
        }
        else{

    //    self.find(self.people[indexPath.item])

        RemoveConfirmation(indexPath)
        }
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
        
        
    func removeCarFromServer(name: NSManagedObject, index:Int) {
        var request = NSMutableURLRequest(URL: NSURL(string: Comments().serverPath + "deleteCar")!)
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
            if(response != nil){
                var json : NSDictionary? = NSJSONSerialization.JSONObjectWithData(data!, options: .MutableContainers, error: &err) as? NSDictionary
                    
                    if((json!["Flag"] as Bool) == true){
                        
                            self.people.removeAtIndex(index)
                            self.removeName(name)
                        
                            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                                self.tableView.reloadData()
                            })
                }
            }
            else{
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    self.noInternetPopUp()
                })
                
            }
        })
        task.resume()
    }
    func noInternetPopUp(){
        var alertView:UIAlertView = UIAlertView()
        alertView.title = "No Internet"
        alertView.message = "We can't remove your car"
        alertView.delegate = self
        alertView.addButtonWithTitle("OK")
        alertView.show()
    }
        
        
        
    
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        println("Carico view auto")
        //1
        let appDelegate = UIApplication.sharedApplication().delegate as AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        let fetchRequest = NSFetchRequest(entityName:"Car")
        var error: NSError?
        let fetchedResults =
        managedContext.executeFetchRequest(fetchRequest,
            error: &error) as [NSManagedObject]?
        
        if let results = fetchedResults {
            people = results
        } else {
            println("Could not fetch \(error), \(error!.userInfo)")
        }
        self.tableView.reloadData()

        for man in people {
            println(man)
        }
    }
    
   // var selectedCar : NSObject
    
    @IBAction func RemoveConfirmation(index: NSIndexPath) {
        
        var mail = self.people[index.item].valueForKey("name")?.description
        
        var alert = UIAlertController(title: "What to do?",
            message: mail,
            preferredStyle: .Alert)
        
        let findAction = UIAlertAction(title: "Find",
            style: .Default) { (action: UIAlertAction!) -> Void in
                self.find(self.people[index.item])
        }
        
        let deleteAction = UIAlertAction(title: "Delete",
            style: .Default) { (action: UIAlertAction!) -> Void in
                
                //let toRem = self.people.removeAtIndex(index.item)
                let toRem = self.people[index.item]
                self.removeCarFromServer(toRem,index: index.item)
                //self.removeName(toRem)
                //self.tableView.reloadData()
        }
        let associateBeaconAction = UIAlertAction(title: "Associate iBeacon",
            style: .Default) { (action: UIAlertAction!) -> Void in
                
                //let toRem = self.people.removeAtIndex(index.item)
                let toRem = self.people[index.item].valueForKey("id")?.description
                self.associateIBeacon(toRem!)
                //self.removeName(toRem)
                //self.tableView.reloadData()
        }

        let cancelAction = UIAlertAction(title: "Cancel",
            style: .Default) { (action: UIAlertAction!) -> Void in
        }
        let editAction = UIAlertAction(title: "Edit Users",
            style: .Default) { (action: UIAlertAction!) -> Void in
                self.selectedCar = self.people[index.row]
                self.performSegueWithIdentifier("user_group", sender: self)
        }
        
        alert.addAction(findAction)
        alert.addAction(editAction)
        alert.addAction(associateBeaconAction)
        alert.addAction(deleteAction)
        alert.addAction(cancelAction)

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
    
    func associateIBeacon(id:String){
        
        var alert = UIAlertController(title: "iBeacon Association",
            message: "Please put only your car iBeacon near your phone",
            preferredStyle: .Alert)
        
        
        let findIBeaconAction = UIAlertAction(title: "Associate",
            style: .Default) { (action: UIAlertAction!) -> Void in
                self.completeIBeaconAssociation(id)
               
        }
        
        let cancelAction = UIAlertAction(title: "Cancel",
            style: .Default) { (action: UIAlertAction!) -> Void in
        }
        
        
        alert.addAction(findIBeaconAction)
        alert.addAction(cancelAction)
        
        presentViewController(alert,
            animated: true,
            completion: nil)
    }
    
    func completeIBeaconAssociation(id:String){
        
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let NumIBeacon = prefs.integerForKey("NUMBEACON")
        
        if(NumIBeacon==0){
            var alertView:UIAlertView = UIAlertView()
            alertView.title = "No iBeacon found"
            alertView.message = "Please check on our site if your iBeacon is compatible"
            alertView.delegate = self
            alertView.addButtonWithTitle("OK")
            alertView.show()
        }
        else if(NumIBeacon>1){
            var alertView:UIAlertView = UIAlertView()
            alertView.title = "Too many iBeacon"
            alertView.message = "Please find a zone with only your iBeacon"
            alertView.delegate = self
            alertView.addButtonWithTitle("OK")
            alertView.show()
        }
        else{
           
            
            
            var UUID = prefs.objectForKey("BUUID") as String
            var Maj =  prefs.objectForKey("BMAJ") as String
            var Min = prefs.objectForKey("BMIN") as String
            
            
            CarUpdate().updateCarIBeacon(id, UUID: prefs.objectForKey("BUUID") as String, Maj: prefs.objectForKey("BMAJ") as String, Min: prefs.objectForKey("BMIN") as String)
           
            var alertView:UIAlertView = UIAlertView()
            alertView.title = "Association Complete!"
            alertView.message = "Just leave your car and the app will park!"
            alertView.delegate = self
            alertView.addButtonWithTitle("OK")
            alertView.show()
        }
       

    }

    
    
    func find(car:NSManagedObject){
        var lat = car.valueForKey("lat")?.description
        var long = car.valueForKey("long")?.description
        if(lat! == "0"){
            noPositionAlert();
            }
        else{
            let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
            prefs.setBool(true, forKey: "findingCar")
            prefs.setValue(lat, forKey: "goLat")
            prefs.setValue(long, forKey: "goLong")
            prefs.synchronize()
            self.tabBarController!.selectedIndex = 0;

        }
        
    }
    func noPositionAlert(){
            var alertView:UIAlertView = UIAlertView()
            alertView.title = "No Parking informations"
            alertView.message = "We don't know where you car has been parked"
            alertView.delegate = self
            alertView.addButtonWithTitle("OK")
            alertView.show()
    }

}