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

    
    @IBAction func PrintPos() {
        println("Mail Sent! :D",self.gmaps.myLocation)
        println("positionfinder", self.gmaps.myLocationEnabled.boolValue)
        println(CLLocationManager.locationServicesEnabled().boolValue)
        if(self.gmaps.myLocation == nil)   {      self.locationManager.requestWhenInUseAuthorization()}
        sendToServer()
        
    }
    var firstLocationUpdate: Bool?
    let locationManager=CLLocationManager()

    
    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
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
            
        }
        
    }
    
  
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func observeValueForKeyPath(keyPath: String, ofObject object: AnyObject, change: [NSObject : AnyObject], context: UnsafeMutablePointer<Void>) {
        println("mia posizione",self.gmaps.myLocation)
        firstLocationUpdate = true
        let location = change[NSKeyValueChangeNewKey] as CLLocation
        gmaps!.camera = GMSCameraPosition.cameraWithTarget(location.coordinate, zoom: 14)
    }
    
    func sendToServer(){
        
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
        var str = ""
        var firstTime = true
        for man in people {
            
            if (!firstTime){
                str += " "
            }
            else
            {
                firstTime = false
            }
            var appoggio =  man.valueForKey("name")!.description
            str += appoggio
            
        }
        
        println(str)

        
        
        if(self.gmaps.myLocation != nil){
        var request = NSMutableURLRequest(URL: NSURL(string: "http://first-vision-798.appspot.com/sign")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        
        var params = ["ID":UIDevice.currentDevice().identifierForVendor.UUIDString,
                        "latitude":self.gmaps.myLocation.coordinate.latitude.description,
                        "longitude":self.gmaps.myLocation.coordinate.longitude.description,
                        "email":str] as Dictionary<String, NSObject>
        
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
    }
}