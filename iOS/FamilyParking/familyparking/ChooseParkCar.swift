//
//  ChooseParkCar.swift
//  familyparking
//
//  Created by mauro piva on 22/02/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import Foundation
import UIKit
import CoreData


class ChooseParkCar: UIViewController, UITextFieldDelegate, UITableViewDelegate, UITableViewDataSource {

    @IBAction func Back(sender: AnyObject) {
        self.dismissViewControllerAnimated(true, completion: nil)
    }
        
        var tableView: UITableView!
        var textField: UITextField!
        var people = [NSManagedObject]()
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
            
            ParkCar(indexPath)
            
        }
    
    
        override func viewWillAppear(animated: Bool) {
            super.viewWillAppear(animated)
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
        
        func ParkCar(index: NSIndexPath) {
            
            var carN = self.people[index.item]
            let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
            let code = prefs.objectForKey("PIN") as String
            let mail = prefs.objectForKey("EMAIL") as String
            let lat = prefs.objectForKey("LAT") as String
            let lon = prefs.objectForKey("LON") as String
            let idCar : String = carN.valueForKey("id")!.description
            var request = NSMutableURLRequest(URL: NSURL(string: Comments().serverPath + "updatePosition")!)
            var session = NSURLSession.sharedSession()
                    request.HTTPMethod = "POST"
                    
                    var user = ["Code":code,
                        "Email":mail] as Dictionary<String, NSObject>
                    /*
                    var car = [ "Id_car":carN.valueForKey("id")?.description,
                                "latitude":lat,
                                "longiude":lon]  as Dictionary<String, NSObject>
                    */
                    var car = ["longitude":lon,
                        "latitude":lat,
                        "Longitude":lon,
                        "Latitude":lat,
                        "ID_car":idCar] as Dictionary<String,NSObject>
            
                    var params = ["User":user,
                        "Car":car] as Dictionary<String, NSObject>
                    
                    var err: NSError?
                    request.HTTPBody = NSJSONSerialization.dataWithJSONObject(params, options: nil, error: &err)
                    request.addValue("application/json", forHTTPHeaderField: "Content-Type")
                    request.addValue("application/json", forHTTPHeaderField: "Accept")
                    
                    var task = session.dataTaskWithRequest(request, completionHandler: {data, response, error -> Void in
                        println("Response: \(response)")
                        var strData = NSString(data: data, encoding: NSUTF8StringEncoding)
                        println("Body: \(strData)")
                        var err: NSError?
                        var json = NSJSONSerialization.JSONObjectWithData(data, options: .MutableLeaves, error: &err) as? NSDictionary
                        
                        if(err == nil&&(!(response==nil))){
                        
                            if((json!["flag"] as Bool) == true){
                                 dispatch_async(dispatch_get_main_queue(),{() -> Void in
                                   println("")
                                    self.navigationController!.popViewControllerAnimated(true)
                                })
                            }
                            else {
                                self.noInternetPopUp()
                            }
                        }else
                        {
                            self.noInternetPopUp()
                        }
                        
                    })
                    
                    task.resume()
                   // self.dismissViewControllerAnimated(true, completion: nil)
                }
                
    func noInternetPopUp(){
        var alertView:UIAlertView = UIAlertView()
        alertView.title = "No internet"
        alertView.message = "Please check your internet connection"
        alertView.delegate = self
        alertView.addButtonWithTitle("OK")
        alertView.show()
    }
    
    
    }


