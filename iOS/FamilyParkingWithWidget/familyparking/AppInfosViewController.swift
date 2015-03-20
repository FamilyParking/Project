//
//  AppInfosViewController.swift
//  familyparking
//
//  Created by mauro piva on 09/03/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import Foundation
import UIKit
import CoreData


class AppInfosViewController: UIViewController {

    @IBAction func WebSiteLinkButton(sender: AnyObject) {
        
        openUrl("http://www.familyparking.it")
    }
    
    func openUrl(url:String!) {
        
        let targetURL=NSURL(string: url)
        
        let application=UIApplication.sharedApplication()
        
        application.openURL(targetURL!);
        
    }

}
