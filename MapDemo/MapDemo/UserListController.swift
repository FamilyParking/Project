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
            var alert = UIAlertController(title: "New Mail",
                message: "Add a new mail to the family",
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
            
            alert.addTextFieldWithConfigurationHandler {
                (textField: UITextField!) -> Void in
            }
            
            alert.addAction(saveAction)
            alert.addAction(cancelAction)
            
            presentViewController(alert,
                animated: true,
                completion: nil)
    }
    
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
      //  var barHeight :Double = self.navigationController?.navigationBar.frame.height.description
  //      println(self.navigationController?.navigationBar.frame.height.description)
        self.tableView = UITableView(frame: CGRectMake(0, 0, self.view.bounds.size.width, self.view.bounds.size.height-0), style: UITableViewStyle.Plain)
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
        
        var alert = UIAlertController(title: "Remove Mail",
            message: mail,
            preferredStyle: .Alert)
        
        let saveAction = UIAlertAction(title: "Yes",
            style: .Default) { (action: UIAlertAction!) -> Void in
                
                
                let toRem = self.people.removeAtIndex(index.item)
                self.removeName(toRem)
                self.tableView.reloadData()
                
             //
               // let textField = alert.textFields![0] as UITextField
               // self.saveName(textField.text)
                //   self.names.append(textField.text)
               // self.tableView.reloadData()
        }
        
        let cancelAction = UIAlertAction(title: "No",
            style: .Default) { (action: UIAlertAction!) -> Void in
        }
        
        alert.addAction(saveAction)
        alert.addAction(cancelAction)
        
        presentViewController(alert,
            animated: true,
            completion: nil)
    }
    

}
