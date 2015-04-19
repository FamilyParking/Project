//
//  EditCarViewController.swift
//  familyparking
//
//  Created by Mauro on 16/04/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import Foundation
import UIKit
import CoreData
import AddressBook
import AddressBookUI

class EditCarViewController: UIViewController,UITableViewDelegate,UITableViewDataSource,ABPeoplePickerNavigationControllerDelegate {
    
    @IBOutlet weak var CarName: UILabel!
    @IBOutlet weak var CarTarga: UILabel!
    @IBOutlet weak var CarBrand: UILabel!
    @IBOutlet weak var CarLastPark: UILabel!
    @IBOutlet weak var CarIbeaconStatus: UISwitch!
   
    
    @IBOutlet weak var CarBrandImage: UIImageView!
    var car : NSObject?
    var people = [NSManagedObject]()
    @IBOutlet weak var SharerTableView: UITableView!
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = car?.valueForKey("name")?.description
        CarName.text = car?.valueForKey("name")?.description
        CarTarga.text = car?.valueForKey("register")?.description
        var brand:String? = car?.valueForKey("brand")?.description
        if(brand=="add_car"){
        CarBrand.text = ""
        }else{
            CarBrand.text = brand
        }
        CarBrandImage.image=UIImage(named:(car?.valueForKey("brand") as! String))
        var parkTime:String = car?.valueForKey("lastPark") as! String!
        
        if(!(parkTime=="never")){
        
        var dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss.SSSSSS" //format style. Browse online to get a format that fits your needs.
        var date = dateFormatter.dateFromString(parkTime)
        var calendar: NSCalendar = NSCalendar.currentCalendar()
        
        var flags = NSCalendarUnit.DayCalendarUnit
        var days = calendar.components(flags, fromDate: date!, toDate: NSDate(), options: nil).day
        
        if(!(days==0)){
            CarLastPark.text = "Parked "+days.description + " days ago"
        }
        else{
            flags = NSCalendarUnit.HourCalendarUnit
            days = calendar.components(flags, fromDate: date!, toDate: NSDate(), options: nil).hour
            CarLastPark.text = "Parked "+days.description + " hours ago"
            
        }
        }
         var iBeacon:String = car?.valueForKey("buuid") as! String!
        
        if(!(iBeacon == "")){
        CarIbeaconStatus.on = true
        }
        
        SharerTableView.delegate = self
        SharerTableView.dataSource = self
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(tableView: UITableView,
        numberOfRowsInSection section: Int) -> Int {
            return people.count
    }
    
    func tableView(tableView: UITableView,      cellForRowAtIndexPath
        indexPath: NSIndexPath) -> UITableViewCell {
            
            let cell =
            tableView.dequeueReusableCellWithIdentifier("user_cell")
                as! UITableViewCell
            var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
            let pin:String = prefs.objectForKey("PIN")as! String
            let localMail:String = prefs.objectForKey("EMAIL") as! String
            
            let person = people[indexPath.row]
            var mail = person.valueForKey("email") as! String?
            var showingName = person.valueForKey("username") as! String?
            showingName!.replaceRange(showingName!.startIndex...showingName!.startIndex, with: String(showingName![showingName!.startIndex]).capitalizedString)
            if( mail == localMail){
                cell.textLabel?.text = showingName! + " (You)"
            }
            else{
                //cell.textLabel?.text = person.valueForKey("username") as String?
                cell.textLabel?.text = showingName
            }
            cell.detailTextLabel?.text = person.valueForKey("email") as! String?
            return cell
    }
 
    
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        //1
        updateClassUsersList()
    }
    
    
    func updateClassUsersList(){
        let appDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        let managedContext = appDelegate.managedObjectContext!
        
        //2
        let fetchRequest = NSFetchRequest(entityName:"Users")
        
        //3
        var error: NSError?
        
        let fetchedResults =
        managedContext.executeFetchRequest(fetchRequest,
            error: &error) as! [NSManagedObject]?
        
        if let results = fetchedResults {
            people = results
        } else {
            println("Could not fetch \(error), \(error!.userInfo)")
        }
        
        var tmp = [NSManagedObject]()
        if(car?.valueForKey("id")?.description==nil){
            self.navigationController?.popViewControllerAnimated(true)
            //      self.dismissViewControllerAnimated(true, completion: nil)
        }
        
        for man in people {
            
            if(man.valueForKey("carId")?.description==car?.valueForKey("id")?.description){
                tmp.append(man)
            }
        }
        people = tmp
        
    }
    
    func tableView(tableView: UITableView, editActionsForRowAtIndexPath indexPath: NSIndexPath) -> [AnyObject]? {
        //  println("TABEL"+self.tableView.rowHeight.description)
        var mail = self.people[indexPath.item].valueForKey("username")?.description
        
        let deleteClosure = { (action: UITableViewRowAction!, indexPath: NSIndexPath!) -> Void in
            println("Delete closure called")
            
            let email = self.people[indexPath.item].valueForKey("username")?.description
          //  self.removeUserFromServer(email!,posx: indexPath.item)
            self.removeUserFromServer(email!,posx:indexPath.item)
        }
        
        
        let deleteAction = UITableViewRowAction(style: .Default, title: "Delete", handler: deleteClosure)
        
        return [deleteAction]
        
    }
    
    func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        
        // Intentionally blank. Required to use UITableViewRowActions
    }


    @IBAction func iBeaconClick(sender: AnyObject) {
        
        var iBeacon:String = car?.valueForKey("buuid") as! String!
        
        if(!(iBeacon == "")){
            RemoveIBeaconAlert()
        }
        else{
            let toRem = car!.valueForKey("id")?.description
            self.associateIBeacon(toRem!)
        }
        
        if(!CarIbeaconStatus.on){
            RemoveIBeaconAlert()
        }//controllare con CARIBEACONSTATUS.on
    }
    func RemoveIBeaconAlert() {
        
        var alert = UIAlertController(title: "Remove your iBeacon?",
            message: "You can re-add it later",
            preferredStyle: .Alert)
       
        let deleteAction = UIAlertAction(title: "Remove",
            style: .Default) { (action: UIAlertAction!) -> Void in
        
                //REMOVE IBEACON
                  CarUpdate().updateCarIBeacon(self.car?.valueForKey("id") as! String, UUID:"", Maj: "", Min: "")
                self.CarIbeaconStatus.setOn(false, animated: true)
                
        }
        let cancelAction = UIAlertAction(title: "No",
            style: .Default) { (action: UIAlertAction!) -> Void in
                self.CarIbeaconStatus.setOn(true, animated: true)
        }
        alert.addAction(deleteAction)
        alert.addAction(cancelAction)
        presentViewController(alert,
            animated: true,
            completion: nil)
    }
    
    func removeUserFromServer(text:String,posx:Int){
        
        //car?.valueForKey("id"), forKey: "carId"
        
        if(text.isEmpty){
            var alert = UIAlertController(title: "UserName",message:"No Name, No Car",
                preferredStyle: .Alert)
            let cancelAction = UIAlertAction(title: "Cancel",
                style: .Default) { (action: UIAlertAction!) -> Void in
                    return
            }
            alert.addAction(cancelAction)
            presentViewController(alert,
                animated: true,
                completion: nil)
        }
        else{
            println("RemoveUser")
            
            var request = NSMutableURLRequest(URL: NSURL(string: Comments().serverPath + "removeContactCar")!)
            var session = NSURLSession.sharedSession()
            request.HTTPMethod = "POST"
            var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
            let pin:String = prefs.objectForKey("PIN") as!String
            let mail:String = prefs.objectForKey("EMAIL") as! String
            
            var otherUsers = ["Code":pin,
                "Email":mail] as Dictionary<String, NSObject>
            
            //var usersMail = [text]
            var usersMail = [
                "Name":text,
                "Email":text
                ] as Dictionary<String, NSObject>
            // [text]
            var idcar:String = car?.valueForKey("id") as! String
            
            var carToServer = [
                "Users":[usersMail],
                "ID_car":idcar
                ] as Dictionary<String, NSObject>
            
            
            
            var user = ["Code":pin,
                "Email":mail] as Dictionary<String, NSObject>
            
            var param = [  "User":user,
                "Car":carToServer] as Dictionary<String, NSObject>
            
            
            var err: NSError?
            request.HTTPBody = NSJSONSerialization.dataWithJSONObject(param, options: nil, error: &err)
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue("application/json", forHTTPHeaderField: "Accept")
            
            var task = session.dataTaskWithRequest(request, completionHandler: {data, response, error -> Void in
                println("Response: \(response)")
                
                
                var strData = NSString(data: data, encoding: NSUTF8StringEncoding)
                
                println("Body: \(strData!)")
                var err: NSError?
                var json = NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions.AllowFragments, error: &err) as? NSDictionary
                
                println(json);
                if((json!["Flag"]as! Bool)==true){
                    dispatch_async(dispatch_get_main_queue(), { () -> Void in
                        println("")
                        self.people.removeAtIndex(posx)
                        self.SharerTableView.reloadData()
                    })
                }
                
                
            })
            task.resume()
            
        }
        
        
    }

    @IBAction func AddUserCarButton() {
      OldAddUser("")
    }
    
    func OldAddUser(name:String) {
        var alert = UIAlertController(title: "New Car Mate",
            message: "Insert the email of your carmate",
            preferredStyle: .Alert)
        
        let saveAction = UIAlertAction(title: "Done",
            style: .Default) { (action: UIAlertAction!) -> Void in
                
                let textFieldEx = alert.textFields![0] as! UITextField
                if(Utilities().isValidEmail(textFieldEx.text)){
                   self.addUserToServer(textFieldEx.text)
                }else{
                    self.wrongMailPopUp(name)
                }
        }
        
        let cancelAction = UIAlertAction(title: "Cancel",
            style: .Default) { (action: UIAlertAction!) -> Void in
        }
        
        let rubricAction = UIAlertAction(title: "Open Contacts",
            style: .Default) { (action: UIAlertAction!) -> Void in
                self.showPeoplePickerController()
        }
        
        
        alert.addTextFieldWithConfigurationHandler {
            (textField: UITextField!) -> Void in
            textField.text = name
        }
        
        
        alert.addAction(saveAction)
        alert.addAction(rubricAction)
        alert.addAction(cancelAction)
        
        presentViewController(alert,
            animated: true,
            completion: nil)
    }
    
    func wrongMailPopUp(nameX:String){
        var alert = UIAlertController(title: "Wrong Mail",
            message: "Please check the Email",
            preferredStyle: .Alert)
        
        
        let saveAction = UIAlertAction(title: "Done",
            style: .Default) { (action: UIAlertAction!) -> Void in
                
                println()
                self.OldAddUser(nameX)
        }
        
        
        
        alert.addAction(saveAction)
        
        presentViewController(alert,
            animated: true,
            completion: nil)
    }
    
    private func showPeoplePickerController() {
        let picker = ABPeoplePickerNavigationController()
        picker.peoplePickerDelegate = self
        let displayedItems = [Int(kABPersonEmailProperty)]
        picker.displayedProperties = displayedItems
        picker.predicateForEnablingPerson = NSPredicate(format:"emailAddresses.@count > 0")
        
        
        self.presentViewController(picker, animated: true, completion: nil)
    }
    func peoplePickerNavigationController(peoplePicker: ABPeoplePickerNavigationController!,
        didSelectPerson person: ABRecord!,
        property: ABPropertyID,
        identifier: ABMultiValueIdentifier){
            
            var firstName = ABRecordCopyValue(person, kABPersonFirstNameProperty)
            println(firstName.takeRetainedValue())
            let propertyName = ABPersonCopyLocalizedPropertyName(property).takeRetainedValue()
            let unmanagedMail = ABRecordCopyValue(person, kABPersonEmailProperty)
            let mails: ABMultiValueRef =
            Unmanaged.fromOpaque(unmanagedMail.toOpaque()).takeUnretainedValue()
                as NSObject as ABMultiValueRef
            var inx = identifier.description.toInt()
            var email:String = ABMultiValueCopyValueAtIndex(mails, inx!).takeRetainedValue().description
            self.addUserToServer(email)
            
    }
    func saveName(email: String) {
        //1
        let appDelegate =
        UIApplication.sharedApplication().delegate as! AppDelegate
        
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
        
        //updateClassUsersList()
    }

    func addUserToServer(text:String){
        
        //car?.valueForKey("id"), forKey: "carId"
        
        if(text.isEmpty){
            println("Ciao")
            var alert = UIAlertController(title: "UserName",message:"No Name, No Car",
                preferredStyle: .Alert)
            let cancelAction = UIAlertAction(title: "Cancel",
                style: .Default) { (action: UIAlertAction!) -> Void in
                    return
            }
            alert.addAction(cancelAction)
            presentViewController(alert,
                animated: true,
                completion: nil)
        }
        else{
            println("ServerCAraar")
            
            var request = NSMutableURLRequest(URL: NSURL(string: Comments().serverPath + "insertContactCar")!)
            var session = NSURLSession.sharedSession()
            request.HTTPMethod = "POST"
            var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
            let pin:String = prefs.objectForKey("PIN") as! String
            let mail:String = prefs.objectForKey("EMAIL")as! String
            let userName:String = prefs.objectForKey("USERNAME") as!String
            
            var otherUsers = ["Code":pin,
                "Email":mail] as Dictionary<String, NSObject>
            
            var usersMail = [
                "Name":text,
                "Email":text
                ] as Dictionary<String, NSObject>
            
            
            
            // [text]
            var idcar:String = car?.valueForKey("id") as! String
            var carName:String = car?.valueForKey("name") as! String
            
            var carToServer = [
                "Users":[usersMail],
                "ID_car":idcar,
                "Name":carName,
                ] as Dictionary<String, NSObject>
            
            var user = ["Code":pin,
                "Email":mail,
                "Name":userName
                ] as Dictionary<String, NSObject>
            
            var param = [  "User":user,
                "Car":carToServer] as Dictionary<String, NSObject>
            
            
            var err: NSError?
            request.HTTPBody = NSJSONSerialization.dataWithJSONObject(param, options: nil, error: &err)
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            request.addValue("application/json", forHTTPHeaderField: "Accept")
            
            var task = session.dataTaskWithRequest(request, completionHandler: {data, response, error -> Void in
                println("Response: \(response)")
                
                if(response == nil){
                    Utilities().savingErrorPopUp()
                }
                var strData = NSString(data: data, encoding: NSUTF8StringEncoding)
                
                println("Body: \(strData!)")
                var err: NSError?
                var json = NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions.AllowFragments, error: &err) as? NSDictionary
                if(err==nil){
                    println(json);
                    if((json!["Flag"] as! Bool)==true){
                        dispatch_async(dispatch_get_main_queue(), { () -> Void in
                            println("")
                            self.saveName(text)
                            self.SharerTableView.reloadData()
                        })
                        
                    }else{
                        dispatch_async(dispatch_get_main_queue(), { () -> Void in
                            Utilities().savingErrorPopUp()
                        })
                    }
                }else{
                    dispatch_async(dispatch_get_main_queue(), { () -> Void in
                        Utilities().savingErrorPopUp()
                    })
                }
            })
            task.resume()
        }
        }
       
        func textFieldShouldReturn(textField: UITextField) -> Bool {
            
            // tableViewData.append(textField.text)
            saveName(textField.text)
            textField.text = ""
            self.SharerTableView.reloadData()
            textField.resignFirstResponder()
            return true
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
                self.CarIbeaconStatus.setOn(false, animated: true)
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
            CarIbeaconStatus.setOn(false, animated: true)
        }
        else if(NumIBeacon>1){
            var alertView:UIAlertView = UIAlertView()
            alertView.title = "Too many iBeacon"
            alertView.message = "Please find a zone with only your iBeacon"
            alertView.delegate = self
            alertView.addButtonWithTitle("OK")
            alertView.show()
            CarIbeaconStatus.setOn(false, animated: true)
        }
        else{
            
            
            
            var UUID = prefs.objectForKey("BUUID") as! String
            var Maj =  prefs.objectForKey("BMAJ") as! String
            var Min = prefs.objectForKey("BMIN")as!String
            
            
            CarUpdate().updateCarIBeacon(id, UUID: prefs.objectForKey("BUUID") as! String, Maj: prefs.objectForKey("BMAJ") as! String, Min: prefs.objectForKey("BMIN")as! String)
            
            var alertView:UIAlertView = UIAlertView()
            alertView.title = "Association Complete!"
            alertView.message = "Just leave your car and the app will park!"
            alertView.delegate = self
            alertView.addButtonWithTitle("OK")
            alertView.show()
        }
        
        
    }

}

