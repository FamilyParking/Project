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
            CarUpdate().downloadCar()
            GroupUpdate().downloadGroup()
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

    
}
