//
//  UserListController.swift
//  MapDemo
//
//  Created by mauro piva on 09/01/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import UIKit
import CoreData

class UserListController: UIViewController, UITextFieldDelegate, UITableViewDelegate, UITableViewDataSource {

    var tableView: UITableView!
    var textField: UITextField!
//    var tableViewData = [String]()
//    var tableViewData = ["mauro993@gmail.com","mauromauro@mauro.mauro"]
    var people = [NSManagedObject]()
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: NSBundle?) {
        super.init(nibName:nibNameOrNil, bundle: nibBundleOrNil)
    }

    required init(coder aDecoder: NSCoder)
    {
        super.init(coder: aDecoder)
    }


    
    @IBAction func TestButton() {
        
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let carsNumber:Int = prefs.integerForKey("HOWMANYCARS") as Int
        if (carsNumber < 1) {
            self.performSegueWithIdentifier("add_car2", sender: self)
        } else {
            //  self.usernameLabel.text = prefs.valueForKey("USERNAME") as NSString
            println("ho \(carsNumber) auto!")
            var alert = UIAlertController(title: "One Car",
                message: "Right now, only one car is admitted",
                preferredStyle: .Alert)
            
            let saveAction = UIAlertAction(title: "Save",
                style: .Default) { (action: UIAlertAction!) -> Void in
                    
                    let textField = alert.textFields![0] as UITextField
                    self.saveName(textField.text)
                    //   self.names.append(textField.text)
                    self.tableView.reloadData()
            }
            
            let cancelAction = UIAlertAction(title: "Cancel",
                style: .Default) { (action: UIAlertAction!) -> Void in
            }
            
        //    alert.addTextFieldWithConfigurationHandler {
        //        (textField: UITextField!) -> Void in
        //    }
            
       //     alert.addAction(saveAction)
           alert.addAction(cancelAction)
            
            presentViewController(alert,
                animated: true,
                completion: nil)
        }
    }
    
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
       //var barHeight:CGFloat = super.navigationController!.navigationBar.frame.height
  //      println(self.navigationController?.navigationBar.frame.height.description)
        self.tableView = UITableView(frame: CGRectMake(0,0, self.view.bounds.size.width, self.view.bounds.size.height-0), style: UITableViewStyle.Plain)
        self.tableView.registerClass(MyTableViewCell.self, forCellReuseIdentifier: "myCell")
       // self.tableView.backgroundColor = UIColor.purpleColor()
        self.tableView.delegate = self
        self.tableView.dataSource = self
        
        self.view.addSubview(self.tableView)
        
        //self.textField = UITextField(frame: CGRectMake(0, 44, self.view.bounds.size.width, 100))
        //self.textField.backgroundColor = UIColor.whiteColor()
        //self.textField.delegate = self
        
     //   self.view.addSubview(self.textField)
        // Do any additional setup after loading the view.
    
        swiftAddressBook?.requestAccessWithCompletion({ (success, error) -> Void in
            if success {
                //do something with swiftAddressBook
            }
            else {
                //no success. Optionally evaluate error
            }
        })
        if let peopleBookList = swiftAddressBook?.allPeople {
            for person in peopleBookList {
                //person.phoneNumbers is a "multivalue" entry
                //so you get an array of MultivalueEntrys
                //see MultivalueEntry in SwiftAddressBook
                println(person.phoneNumbers!.map( {$0.value} ))
            }
        }
        
    
    
    
    
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
            
            return cell
    }
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath){
        
        RemoveConfirmation(indexPath)
        
      //  let toRem = people.removeAtIndex(indexPath.item)
      //  removeName(toRem)
      //  self.tableView.reloadData()
        
        
        
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
        let entity =  NSEntityDescription.entityForName("Entity",
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
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        //1
        let appDelegate =
        UIApplication.sharedApplication().delegate as AppDelegate
        
        let managedContext = appDelegate.managedObjectContext!
        
        
        
        //2
        let fetchRequest = NSFetchRequest(entityName:"Entity")
        
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
    
    @IBAction func RemoveConfirmation(index: NSIndexPath) {
        
        var mail = self.people[index.item].valueForKey("name")?.description
        
        var alert = UIAlertController(title: "What to do?",
            message: mail,
            preferredStyle: .Alert)
        
        let saveAction = UIAlertAction(title: "Choose",
            style: .Default) { (action: UIAlertAction!) -> Void in
                
                
                let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
                
                prefs.setObject(self.people[index.item].valueForKey("name")?.description, forKey: "ACTIVECAR")
                prefs.setObject(self.people[index.item].valueForKey("id")?.description, forKey: "CARCODE")
                prefs.synchronize()
             //
               // let textField = alert.textFields![0] as UITextField
               // self.saveName(textField.text)
                //   self.names.append(textField.text)
               // self.tableView.reloadData()
        }
        
        let deleteAction = UIAlertAction(title: "Delete",
            style: .Default) { (action: UIAlertAction!) -> Void in
                
                let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
                let activeCar = prefs.objectForKey("CARCODE") as String
                
                let code = prefs.objectForKey("PIN") as String
                let mail = prefs.objectForKey("EMAIL") as String
                
                
                
                var request = NSMutableURLRequest(URL: NSURL(string: "http://first-vision-798.appspot.com/deleteCar")!)
                var session = NSURLSession.sharedSession()
                request.HTTPMethod = "POST"
                
                var params = ["ID":self.people[index.item].valueForKey("id")!.description,
                    "Code":code,
                    "Email":mail] as Dictionary<String, NSObject>
                
                var err: NSError?
                request.HTTPBody = NSJSONSerialization.dataWithJSONObject(params, options: nil, error: &err)
                request.addValue("application/json", forHTTPHeaderField: "Content-Type")
                request.addValue("application/json", forHTTPHeaderField: "Accept")
                
                var task = session.dataTaskWithRequest(request, completionHandler: {data, response, error -> Void in
                    
                    
                    println("Response: \(response)")
                    
                    var strData:NSString = NSString(data: data, encoding: NSUTF8StringEncoding)!
                    println("Body: \(strData)")
                    if(strData.containsString("'flag': True")){
                        CarList().removeName(self.people[index.item])
                        let toRem = self.people.removeAtIndex(index.item)
                        self.removeName(toRem)
                        self.tableView.reloadData()
                        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
                            prefs.setObject("", forKey: "CARCODE")
                            prefs.synchronize()
                    }
                    else{
                        var alert = UIAlertController(title: "Oopsss",
                            message: "We can't delete your car right now.",
                            preferredStyle: .Alert)
                        
                        let cancelAction = UIAlertAction(title: "Ok",
                            style: .Default) { (action: UIAlertAction!) -> Void in
                        }
                       
                        alert.addAction(cancelAction)
                        
                        self.presentViewController(alert,
                            animated: true,
                            completion: nil)
                    }

                })
                
                task.resume()
                
        }
        let cancelAction = UIAlertAction(title: "Cancel",
            style: .Default) { (action: UIAlertAction!) -> Void in
        }
        
        alert.addAction(saveAction)
        alert.addAction(cancelAction)
        alert.addAction(deleteAction)
        presentViewController(alert,
            animated: true,
            completion: nil)
    }
    
        

}
