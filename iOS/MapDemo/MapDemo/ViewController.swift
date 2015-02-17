//
//  ViewController.swift
//  MapDemo
//
//  Created by mauro piva on 08/01/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import UIKit
import CoreData

class ViewController: UIViewController, GMSMapViewDelegate {
    
    //#2
    var gmaps: GMSMapView!
    var people = [NSManagedObject]()

    @IBOutlet weak var Title: UINavigationItem!
    @IBOutlet weak var Running: UIActivityIndicatorView!
    
    @IBOutlet weak var PButton: UIButton!
    @IBAction func PrintPos() {
        println("Mail Sent! :D",self.gmaps.myLocation)
        println("positionfinder", self.gmaps.myLocationEnabled.boolValue)
        println(CLLocationManager.locationServicesEnabled().boolValue)
        if(self.gmaps.myLocation == nil)   {      self.locationManager.requestWhenInUseAuthorization()}
        //getGroups()
        updatePosition()
        
    }
    var firstLocationUpdate: Bool?
    let locationManager=CLLocationManager()

    
    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(true)
        
        
        
        
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let isLoggedIn:Int = prefs.integerForKey("ISLOGGEDIN") as Int
        if (isLoggedIn != 1) {
            self.performSegueWithIdentifier("goto_login", sender: self)
                
        } else {
          getGroups()
        }
       
    }
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        GAI.sharedInstance().trackerWithTrackingId("UA-58079755-1")
        GAI.sharedInstance().trackUncaughtExceptions = true
        
      //  GAI.sharedInstance().dispatchInterval = 10
      //  GAI.sharedInstance().defaultTracker.allowIDFACollection = true
      GAI.sharedInstance().defaultTracker.send(GAIDictionaryBuilder.createEventWithCategory("ui_action", action: "app_launched",label:"launch",value:nil).build())
      //  println("description" + GAI.sharedInstance().defaultTracker.name)
        
        self.locationManager.requestWhenInUseAuthorization()
        
        
        var target: CLLocationCoordinate2D = CLLocationCoordinate2D(latitude: 2.6, longitude: 13.2)
        var camera: GMSCameraPosition = GMSCameraPosition(target: target, zoom: 6, bearing: 0, viewingAngle: 0)
        
      //  gmaps = GMSMapView(frame: CGRectMake(0, 0, self.view.bounds.width, self.view.bounds.height - super.tabBarController!.tabBar.bounds.height))
        
        gmaps = GMSMapView(frame: CGRectMake(0, 0, self.view.bounds.width, self.view.bounds.height))
            
        if let map = gmaps {
            map.myLocationEnabled = true
            map.camera = camera
            map.delegate = self
            gmaps.settings.myLocationButton = true

            gmaps.addObserver(self, forKeyPath: "myLocation", options: .New , context: nil)
            
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                self.gmaps.myLocationEnabled = true
                
            })
            self.gmaps.myLocationEnabled = true
            println("mia posizione",self.gmaps.myLocation)
            
            self.view.insertSubview(gmaps, atIndex: 2)
            self.view.sendSubviewToBack(gmaps)
           //self.view.addSubview(gmaps)
            
       //     var position = CLLocationCoordinate2DMake(51, -0)
       //     var london = GMSMarker(position: position)
       //     london.title = "London"
       //     london.snippet = "Population: 8,174,100"
       //     london.infoWindowAnchor = CGPointMake(0.5, 0.5)
       //     london.userData = "Londra"
           // london.icon = UIImage(named: "house")
       //     london.map = map
          //  map.
        }
        
        
        
    }
    
   
  //  override func mapView:(GMSMapView *)mapView->didTapInfoWindowOfMarker:(id<GMSMarker>)marker {
    
    
   // }
    
    func mapView(mapView: GMSMapView!, didTapInfoWindowOfMarker marker: GMSMarker!) {
        
        println(marker.userData);
        
    }
  
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func observeValueForKeyPath(keyPath: String, ofObject object: AnyObject, change: [NSObject : AnyObject], context: UnsafeMutablePointer<Void>) {
       // println("mia posizione",self.gmaps.myLocation)
        firstLocationUpdate = true
    //    let location = change[NSKeyValueChangeNewKey] as CLLocation
       // gmaps!.camera = GMSCameraPosition.cameraWithTarget(location.coordinate, zoom: 14)
    }
    
    func updatePosition(){
        
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        if (prefs.objectForKey("CARCODE")==nil){
            prefs.setObject("", forKey: "CARCODE")
            prefs.synchronize()
        }
        let activeCar = prefs.objectForKey("CARCODE") as String
        if(activeCar.isEmpty){
            var alert = UIAlertController(title: "No Car",
                message: "Please add a car",
                preferredStyle: .Alert)
            
            let saveAction = UIAlertAction(title: "Add",
                style: .Default) { (action: UIAlertAction!) -> Void in
            
                self.performSegueWithIdentifier("add_car", sender: self)
            
            }
            
            let cancelAction = UIAlertAction(title: "Cancel",
                style: .Default) { (action: UIAlertAction!) -> Void in
            }
            
            alert.addAction(saveAction)
            alert.addAction(cancelAction)
            presentViewController(alert,animated: true,completion: nil)
            return
        }
        println(activeCar)
        
        if(self.gmaps.myLocation==nil)
        {
            var alert = UIAlertController(title: "No Position",
                message: "Please activate your position services",
                preferredStyle: .Alert)
            let saveAction = UIAlertAction(title: "Save",
                style: .Default) { (action: UIAlertAction!) -> Void in
            }
            let cancelAction = UIAlertAction(title: "Cancel",
                style: .Default) { (action: UIAlertAction!) -> Void in
            }
            alert.addAction(cancelAction)
            presentViewController(alert,animated: true,completion: nil)
            return
        }
       // Link richiesta = http://first-vision-798.appspot.com/updatePosition
       // Struttura richiesta = {"Email":"nazzareno.marziale@gmail.com","Code":"163930",”ID”:”756565656”,”Latitude”:”24234234234”,”Longitude”:”3123123231”}
        
        let code = prefs.objectForKey("PIN") as String
        let mail = prefs.objectForKey("EMAIL") as String
        
        
        if(self.gmaps.myLocation != nil){
        var request = NSMutableURLRequest(URL: NSURL(string: "http://first-vision-798.appspot.com/updatePosition")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        
        var params = ["ID_car":activeCar,
                        "Code":code,
                        "Latitude":self.gmaps.myLocation.coordinate.latitude.description,
                        "Longitude":self.gmaps.myLocation.coordinate.longitude.description,
                        "Email":mail] as Dictionary<String, NSObject>
        
        var err: NSError?
        request.HTTPBody = NSJSONSerialization.dataWithJSONObject(params, options: nil, error: &err)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        var task = session.dataTaskWithRequest(request, completionHandler: {data, response, error -> Void in
          
            
            println("Response: \(response)")
            
            self.PButton.hidden = true
            self.Running.startAnimating()
            
            
            var strData = NSString(data: data, encoding: NSUTF8StringEncoding)
            println("Body: \(strData)")
            var err: NSError?
            var json = NSJSONSerialization.JSONObjectWithData(data, options: .MutableLeaves, error: &err) as? NSDictionary
            
            // Did the JSONObjectWithData constructor return an error? If so, log the error to the console
            if(err != nil) {
                println(err!.localizedDescription)
                let jsonStr = NSString(data: data, encoding: NSUTF8StringEncoding)
                println("Error could not parse JSON: '\(jsonStr)'")
            }
            else {
                // The JSONObjectWithData constructor didn't return an error. But, we should still
                // check and make sure that json has a value using optional binding.
                if let parseJSON = json {
                    // Okay, the parsedJSON is here, let's get the value for 'success' out of it
                    var success = parseJSON["success"] as? Int
                    println("Succes: \(success)")
                }
                else {
                    // Woa, okay the json object was nil, something went worng. Maybe the server isn't running?
                    let jsonStr = NSString(data: data, encoding: NSUTF8StringEncoding)
                    println("Error could not parse JSON: \(jsonStr)")
                }
            }
            var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
            
            self.PButton.hidden = false
            self.Running.stopAnimating()
            CarList().removeCode(activeCar)
            self.gmaps.clear()
            self.addACar2(activeCar, name: prefs.objectForKey("ACTIVECAR") as String, lat: self.gmaps.myLocation.coordinate.latitude.description, long: self.gmaps.myLocation.coordinate.longitude.description)
        })
        
        task.resume()
    }
    }
    
    @IBAction func resetAction() {
        var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        prefs.setInteger(0, forKey: "HOWMANYCARS")
        prefs.setInteger(0, forKey: "ISLOGGEDIN")
        prefs.setObject("", forKey: "CARCODE")
        prefs.synchronize()
        CarList().removeAll()
     //   exit(0)
    }
    
    
   
    func getGroups(){
        var request = NSMutableURLRequest(URL: NSURL(string: "http://first-vision-798.appspot.com/getAllCars_fromEmail")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        
        
        var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let code = prefs.objectForKey("PIN") as String
     //   prefs.setObject(self.PinTextField.text, forKey: "PIN")
        let mail = prefs.objectForKey("EMAIL") as String
   //     prefs.setObject(self.EmailTextField.text, forKey: "EMAIL")
        //       {"Email":"nazzareno.marziale@gmail.com","Code":"163930"}
        var params = ["Code":code,
          //  "latitude":self.gmaps.myLocation.coordinate.latitude.description,
         //   "longitude":self.gmaps.myLocation.coordinate.longitude.description,
            "Email":mail] as Dictionary<String, NSObject>
        
        var err: NSError?
        request.HTTPBody = NSJSONSerialization.dataWithJSONObject(params, options: nil, error: &err)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        var task = session.dataTaskWithRequest(request, completionHandler: {data, response, error -> Void in
            
            
            println("Response: \(response)")
            
            var strData = NSString(data: data, encoding: NSUTF8StringEncoding)
            println("Body: \(strData)")
            
            if(strData!.length>3){
                println("Check 1 Complete")
                var array = strData!.componentsSeparatedByString("[")
                if(array.count==2){
                    var fl = array[1].componentsSeparatedByString("]")
                    var test:String = fl[0] as String
                    var cars = test.componentsSeparatedByString("\",\"")
                    self.gmaps.clear()
                    CarList().removeAll()
                    
                    for name:String in cars{
                        if(!name.isEmpty){
                            var name2 = name.componentsSeparatedByString("'")
                            self.addACar2(name2[3], name: name2[11], lat: name2[15], long: name2[19])
                            }
                        }
                    }
                  //  var flag = fla[0].stringByReplacingOccurrencesOfString(" ", withString: "")
                }
            
            var err: NSError?
            var json = NSJSONSerialization.JSONObjectWithData(data, options: .MutableLeaves, error: &err) as? NSDictionary
            
            // Did the JSONObjectWithData constructor return an error? If so, log the error to the console
            if(err != nil) {
                println(err!.localizedDescription)
                let jsonStr = NSString(data: data, encoding: NSUTF8StringEncoding)
                println("Error could not parse JSON: '\(jsonStr)'")
            }
            else {
                // The JSONObjectWithData constructor didn't return an error. But, we should still
                // check and make sure that json has a value using optional binding.
                if let parseJSON = json {
                    // Okay, the parsedJSON is here, let's get the value for 'success' out of it
                    var success = parseJSON["success"] as? Int
                    println("Succes: \(success)")
                }
                else {
                    // Woa, okay the json object was nil, something went worng. Maybe the server isn't running?
                    let jsonStr = NSString(data: data, encoding: NSUTF8StringEncoding)
                    println("Error could not parse JSON: \(jsonStr)")
                }
            }
            
        })
        
        task.resume()
    
    }
    func addACar(code:String){
        
        var car = code.stringByReplacingOccurrencesOfString("\"", withString: "")
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let carsNumber:Int = prefs.integerForKey("HOWMANYCARS") as Int;
        prefs.setInteger(carsNumber + 1, forKey: "HOWMANYCARS")
        prefs.setObject(car, forKey: "ACTIVECAR")
        prefs.synchronize()
        println("Ora aggiungo \(code)")
        
        
       // Link richiesta = http://first-vision-798.appspot.com/getPositionCar
       // Struttura richiesta = {"Email":"nazzareno.marziale@gmail.com","Code":"163930",”ID_car”:”756565656”}
        var request = NSMutableURLRequest(URL: NSURL(string: "http://first-vision-798.appspot.com/getAllCars")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        
        
    //    var prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let code = prefs.objectForKey("PIN") as String
        let mail = prefs.objectForKey("EMAIL") as String
        var params = ["Code":code,
            "ID_car":car,
            "Email":mail] as Dictionary<String, NSObject>
        
        var err: NSError?
        request.HTTPBody = NSJSONSerialization.dataWithJSONObject(params, options: nil, error: &err)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        var task = session.dataTaskWithRequest(request, completionHandler: {data, response, error -> Void in
            
            println("Response: \(response)")
            var strData = NSString(data: data, encoding: NSUTF8StringEncoding)
            println("Body: \(strData)")
            
            if(strData!.length>3){
                println("Check 1 Complete")
                var array = strData!.componentsSeparatedByString("[")
                if(array.count==2){
                    var fl = array[1].componentsSeparatedByString("]")
                    var fla = fl[0].componentsSeparatedByString("'")
                    var carCode = fla[3] as String
                    var model = fla[7] as String
                    var lat = fla[11] as String
                    var long = fla[15] as String
                        println("model\(model)")
                    CarList().removeAll()
                    CarList().saveName(model,id: carCode.stringByReplacingOccurrencesOfString("\"",withString: ""))
                    println("model3\(carCode)")
                    println("model1\(lat)")
                    println("model2\(long)")
                    prefs.setObject(carCode, forKey: "CARCODE")
                    dispatch_async(dispatch_get_main_queue(), { () -> Void in
                        var latDouble = (lat as NSString).doubleValue
                        var longDouble = (long as NSString).doubleValue
                        
                        var position = CLLocationCoordinate2DMake(latDouble, longDouble)
                        var london = GMSMarker(position: position)
                        london.title = model
                        london.snippet = "Click here for more info"
                        london.infoWindowAnchor = CGPointMake(0.5, 0.5)
                        london.userData = "Londra"
                        // london.icon = UIImage(named: "house")
                        london.map = self.gmaps
                        var camera = GMSCameraPosition.cameraWithLatitude(latDouble, longitude: longDouble, zoom: 16)
                        
                        self.gmaps.camera = camera
                        self.Title.title = model
                    })
                    
                }
                
            }
            
        })
        
        task.resume()
        
    }
    func addACar2(code:String,name:String,lat:String,long:String){
        
       
        var car = code.stringByReplacingOccurrencesOfString("\"", withString: "")
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let carsNumber:Int = prefs.integerForKey("HOWMANYCARS") as Int;
        prefs.setInteger(carsNumber + 1, forKey: "HOWMANYCARS")
        prefs.setObject(name, forKey: "ACTIVECAR")
        prefs.synchronize()
        println("Ora aggiungo \(code)")
        
        
                    CarList().saveName(name,id: code.stringByReplacingOccurrencesOfString("\"",withString: ""))
                 //   println("model3\(carCode)")
                    println("model1\(lat)")
                    println("model2\(long)")
                    prefs.setObject(code, forKey: "CARCODE")
        if(lat=="0"||long=="0") {
            return
        }
                    dispatch_async(dispatch_get_main_queue(), { () -> Void in
                        var latDouble = (lat as NSString).doubleValue
                        var longDouble = (long as NSString).doubleValue
                        
                        var position = CLLocationCoordinate2DMake(latDouble, longDouble)
                        var london = GMSMarker(position: position)
                        london.title = name
                        london.snippet = "Click here for more info"
                        london.infoWindowAnchor = CGPointMake(0.5, 0.5)
                        london.userData = "Londra"
                        // london.icon = UIImage(named: "house")
                        london.map = self.gmaps
                        var camera = GMSCameraPosition.cameraWithLatitude(latDouble, longitude: longDouble, zoom: 16)
                        
                        self.gmaps.camera = camera
                        self.Title.title = name
                    })
    }
    
    
    
}