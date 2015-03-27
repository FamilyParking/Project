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

class MapViewController: UIViewController, GMSMapViewDelegate,CLLocationManagerDelegate {
    
    var gmaps: GMSMapView!
    let locationManager=CLLocationManager()
    var mapLoaded:Bool = false
    var lastProximity: CLProximity?
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // start Google Analitycs
        GAI.sharedInstance().trackerWithTrackingId("UA-58079755-1")
        GAI.sharedInstance().trackUncaughtExceptions = true
        GAI.sharedInstance().defaultTracker.send(GAIDictionaryBuilder.createEventWithCategory("ui_action", action: "app_launched",label:"launch",value:nil).build())
            checkRegistration()

        
      //  self.locationManager.requestWhenInUseAuthorization()
     
        self.locationManager.requestAlwaysAuthorization()
        
        
        
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
        
        //Start ibeacon service
        
        let uuidString = "B9407F30-F5F8-466E-AFF9-25556B57FE6D"
        //let uuidString = "B9507F30-F5F8-466E-AFF9-25556B57FE6D"
        
        let beaconIdentifier = "iBeaconModules.us"
        let beaconUUID:NSUUID = NSUUID(UUIDString: uuidString)!
        let beaconRegion:CLBeaconRegion = CLBeaconRegion(proximityUUID: beaconUUID,
            identifier: beaconIdentifier)
        beaconRegion.notifyEntryStateOnDisplay = true
        beaconRegion.notifyOnEntry = true
        beaconRegion.notifyOnExit = true
        locationManager.delegate = self
        locationManager.pausesLocationUpdatesAutomatically = false
        
        locationManager.startMonitoringForRegion(beaconRegion)
        locationManager.startRangingBeaconsInRegion(beaconRegion)
        locationManager.startUpdatingLocation()
    }
    
    func checkRegistration(){
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        var isLoggedIn:Int = prefs.integerForKey("ISLOGGEDIN") as Int
        prefs.setInteger(0, forKey: "NUMBEACON")
        prefs.synchronize()
        println(isLoggedIn)
        if (isLoggedIn != 1) {
            println("Apro la scheda registrazione")
            self.performSegueWithIdentifier("registration_1", sender: self)
        } else {
         //   CarUpdate().downloadCar(self)
            //  GroupUpdate().downloadGroup()
        }
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
                            london.draggable = true
                            london.title = man.valueForKey("name")?.description
                            london.snippet = man.valueForKey("brand")?.description
                            london.infoWindowAnchor = CGPointMake(0.5, 0.5)
                            london.userData = man.valueForKey("id")?.description
                            //  london.icon = UIImage(named: "audi")
                            london.map = self.gmaps
                            var camera = GMSCameraPosition.cameraWithLatitude(latDouble, longitude: longDouble, zoom: 16)
                          //   self.gmaps.camera = camera
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

    func didEndDraggingMarker(marker:GMSMarker){
            println("FINEMOVIMENTO")
            self.staticParkCar(marker.userData.description, nameCar: marker.title, lat: marker.position.latitude.description, lon: marker.position.longitude.description)       //    marker.title!
        
    }
    
    func mapView(mapView: GMSMapView!, didEndDraggingMarker marker:GMSMarker) {
        //addressLabel.lock()
        println(marker.title)
        self.staticParkCar(marker.userData.description, nameCar: marker.title, lat: marker.position.latitude.description, lon: marker.position.longitude.description)
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        println("Showing Map")
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        var finding:Bool = prefs.boolForKey("findingCar")
        if (finding){
            var lat:String = prefs.valueForKey("goLat") as String
            var long:String = prefs.valueForKey("goLong") as String
            var camera = GMSCameraPosition.cameraWithLatitude((lat as NSString).doubleValue, longitude: (long as NSString).doubleValue, zoom: 16)
            self.gmaps.camera = camera
            
        }else{
            CarUpdate().downloadCar(self as MapViewController)
            
        }
        prefs.setBool(false, forKey: "findingCar")
        prefs.synchronize()
        
        //updateCars()
    }

    @IBAction func ParkButtonClick() {
        
        if (CarUpdate().countCar()==0){
            self.performSegueWithIdentifier("create_car", sender: self)
            return;
           //TODO se crei una sola macchina da park, la parki anche
        }
        
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
    
    func staticParkCar(idCar:String,nameCar:String,lat:String,lon:String) {
        
        //     var carN = self.people[index.item]
        let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
        let code = prefs.objectForKey("PIN") as String
        let mail = prefs.objectForKey("EMAIL") as String
        //    let lat = prefs.objectForKey("LAT") as String
        //   let lon = prefs.objectForKey("LON") as String
        let username = prefs.objectForKey("USERNAME") as String
        
        //  let idCar : String = carN.valueForKey("id")!.description
        //   let nameCar : String = carN.valueForKey("name")!.description
        
        var request = NSMutableURLRequest(URL: NSURL(string: Comments().serverPath + "updatePosition")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        
        var user = ["Code":code,
            "Name":username,
            "Email":mail] as Dictionary<String, NSObject>
        /*
        var car = [ "Id_car":carN.valueForKey("id")?.description,
        "latitude":lat,
        "longiude":lon]  as Dictionary<String, NSObject>
        */
        var car = ["Longitude":lon,
            "Latitude":lat,
            "Name":nameCar,
            
            "ID_car":idCar] as Dictionary<String,NSObject>
        
        var params = ["User":user,
            "Car":car] as Dictionary<String, NSObject>
        
        var err: NSError?
        request.HTTPBody = NSJSONSerialization.dataWithJSONObject(params, options: nil, error: &err)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        var task = session.dataTaskWithRequest(request, completionHandler: {data, response, error -> Void in
            println("Response: \(response)")
            
            
            if(response == nil){
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    var alertView:UIAlertView = UIAlertView()
                    alertView.title = "No internet connection"
                    alertView.message = "Please, check your internet connection."
                    alertView.delegate = self
                    alertView.addButtonWithTitle("OK")
                    alertView.show()
                })
                return
            }
            
            var castato:NSHTTPURLResponse = response as NSHTTPURLResponse
            println(castato.statusCode)
            if(castato.statusCode==500){
                
                
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    var alertView:UIAlertView = UIAlertView()
                    alertView.title = "Server Error"
                    alertView.message = "Our Monkeys are working! Try in minutes!"
                    alertView.delegate = self
                    alertView.addButtonWithTitle("OK")
                    alertView.show()
                })
                
                return
            }
            
            var strData = NSString(data: data, encoding: NSUTF8StringEncoding)
            println("Body: \(strData)")
            var err: NSError?
            var json = NSJSONSerialization.JSONObjectWithData(data, options: .MutableLeaves, error: &err) as? NSDictionary
            
            if(err == nil&&(!(response==nil))){
                
                if((json!["Flag"] as Bool) == true){
                    dispatch_async(dispatch_get_main_queue(),{() -> Void in
                        println("")
                        self.navigationController?.popViewControllerAnimated(true)
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
    
    func locationManager(manager:CLLocationManager,  didStartMonitoringForRegion region:CLRegion )
    {
    println(locationManager.requestStateForRegion(region))
      //  println("REGION")
    }
    
    func locationManager(manager: CLLocationManager!,
        didRangeBeacons beacons: [AnyObject]!,
        inRegion region: CLBeaconRegion!) {
            
           NSLog("didRangeBeacons");
            var message:String = ""
            
            
            var playSound = false
            let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
            if(beacons.count > 0) {
                println((beacons[0] as CLBeacon).proximityUUID)
                let nearestBeacon:CLBeacon = beacons[0] as CLBeacon
                if(nearestBeacon.proximity == lastProximity ||
                   nearestBeacon.proximity == CLProximity.Unknown) {
                    prefs.setInteger(1, forKey: "NUMBEACON")
                    prefs.setObject(region.proximityUUID.UUIDString, forKey: "BUUID")
                    prefs.setObject(nearestBeacon.major.description, forKey: "BMAJ")
                    prefs.setObject(nearestBeacon.minor.description, forKey: "BMIN")
                    prefs.synchronize()
                }else{
                    prefs.setInteger(1, forKey: "NUMBEACON")
                    prefs.setObject(region.proximityUUID.UUIDString, forKey: "BUUID")
                    prefs.setObject(nearestBeacon.major.description, forKey: "BMAJ")
                    prefs.setObject(nearestBeacon.minor.description, forKey: "BMIN")
                    prefs.synchronize()
                    
                }
            }
            prefs.synchronize()
            
    }
    
    func locationManager(manager: CLLocationManager!,
        didEnterRegion region: CLRegion!) {
            manager.startRangingBeaconsInRegion(region as CLBeaconRegion)
            manager.startUpdatingLocation()
                        NSLog("You entered the region")
            
    }
    
    func locationManager(manager: CLLocationManager!,
        didExitRegion region: CLRegion!) {
            manager.stopRangingBeaconsInRegion(region as CLBeaconRegion)
            manager.stopUpdatingLocation()
            let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
            prefs.setInteger(0, forKey: "NUMBEACON")
            prefs.synchronize()
            println("You exited the region")
            var car:[String] = CarUpdate().getCarByIBeacon()
            if !(car[0]=="NONE"){
            self.staticParkCar(car[1], nameCar: car[0], lat: self.gmaps.myLocation.coordinate.latitude.description, lon: self.gmaps.myLocation.coordinate.longitude.description)
            }
    }
    
    func locationManager(manager: CLLocationManager!,
        monitoringDidFailForRegion region: CLRegion!,
        withError error: NSError!) {
            NSLog("monitoringDidFailForRegion - error: %@", [error.localizedDescription]);
    }    

    
    func locationManager(manager: CLLocationManager!, didDetermineState state: CLRegionState, forRegion inRegion: CLRegion!) {
        if (state == .Inside) {
            //領域内にはいったときに距離測定を開始
           // manager.startRangingBeaconsInRegion(state)
            println("REGION")
        }
    }

    


}

