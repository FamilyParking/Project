//
//  MapViewController.swift
//  familyparking
//
//  Created by mauro piva on 19/02/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import Foundation
import UIKit
import CoreData

class MapViewController: UIViewController, GMSMapViewDelegate {
    
    var gmaps: GMSMapView!
    let locationManager=CLLocationManager()
    var mapLoaded:Bool = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // start Google Analitycs
        GAI.sharedInstance().trackerWithTrackingId("UA-58079755-1")
        GAI.sharedInstance().trackUncaughtExceptions = true
        GAI.sharedInstance().defaultTracker.send(GAIDictionaryBuilder.createEventWithCategory("ui_action", action: "app_launched",label:"launch",value:nil).build())
        
        // check registration
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        var isLoggedIn:Int = prefs.integerForKey("ISLOGGEDIN") as Int
        println(isLoggedIn)
        if (isLoggedIn != 1) {
            println("Apro la scheda registrazione")
            self.performSegueWithIdentifier("registration_1", sender: self)
        } else {
            CarUpdate().downloadCar(self)
          //  GroupUpdate().downloadGroup()
        }
        
        
        
        
        
        self.locationManager.requestWhenInUseAuthorization()
        
        
        var target: CLLocationCoordinate2D = CLLocationCoordinate2D(latitude: 2.6, longitude: 13.2)
        var camera: GMSCameraPosition = GMSCameraPosition(target: target, zoom: 6, bearing: 0, viewingAngle: 0)
       
        var barHeight:CGFloat = tabBarController!.tabBar.frame.height
        
        gmaps = GMSMapView(frame: CGRectMake(0, 0, self.view.bounds.width, self.view.bounds.height-barHeight))
        
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
            mapLoaded = true
            println("Map Loaded")
            self.updateCars()
        }
        else{
            // TODO MAP NOT LOADED
        }
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    var didFirstLocationUpdate=true
    override func observeValueForKeyPath(keyPath: String, ofObject object: AnyObject, change: [NSObject : AnyObject], context: UnsafeMutablePointer<Void>) {
        // println("mia posizione",self.gmaps.myLocation)
        if(didFirstLocationUpdate==true){
            didFirstLocationUpdate = false
            let location = change[NSKeyValueChangeNewKey] as CLLocation
            gmaps!.camera = GMSCameraPosition.cameraWithTarget(location.coordinate, zoom: 14)
        }
    }
    
    func updateCars(){
        if (mapLoaded||true){
        var people = [NSManagedObject]()
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
        
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                self.gmaps.clear()
                
                for man in people {
                    println("Inserisco")
                    println(man.valueForKey("name")?.description)
                    if(man.valueForKey("name")?.description != nil){
                        var latDouble = (man.valueForKey("lat")!.description as NSString).doubleValue
                        var longDouble = (man.valueForKey("long")!.description as NSString).doubleValue
                        if(latDouble != 0){
                            var position = CLLocationCoordinate2DMake(latDouble, longDouble)
                            var london = GMSMarker(position: position)
                            london.title = man.valueForKey("name")?.description
                            london.snippet = man.valueForKey("brand")?.description
                            london.infoWindowAnchor = CGPointMake(0.5, 0.5)
                            london.userData = man.valueForKey("id")?.description
                            //  london.icon = UIImage(named: "audi")
                            london.map = self.gmaps
                            var camera = GMSCameraPosition.cameraWithLatitude(latDouble, longitude: longDouble, zoom: 16)
                             self.gmaps.camera = camera
                        }
                    }
                }
           //     var markers = //some array;
                //var bounds =  google.maps.LatLngBounds();
                self.gmaps.sizeToFit()
                //for(i=0;i<markers.length;i++) {
                  //  bounds.extend(markers[i].getPosition());
                //}
                
                //map.fitBounds(bounds);

                
            })

        }
        else{
           println(" // TODO mappa non caricata")
        }
    }

    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        println("Showing Map")
        CarUpdate().downloadCar(self as MapViewController)
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        var finding:Bool = prefs.boolForKey("findingCar")
        if (finding){
            var lat:String = prefs.valueForKey("goLat") as String
            var long:String = prefs.valueForKey("goLong") as String
        }
        
        //updateCars()
    }

    @IBAction func ParkButtonClick() {
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        
        if(gmaps.myLocation != nil){
        
            
            prefs.setObject(self.gmaps.myLocation.coordinate.latitude.description, forKey:"LAT")
            
            prefs.setObject(self.gmaps.myLocation.coordinate.longitude.description, forKey:"LON")
           prefs.synchronize()
            self.performSegueWithIdentifier("park_action", sender: self)
        }
        else{
            println("NO GPS")
            //TODO
        }
        
    }
}
