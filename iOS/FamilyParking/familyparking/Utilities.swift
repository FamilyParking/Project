//
//  Utilities.swift
//  familyparking
//
//  Created by Mauro on 16/04/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import Foundation

class Utilities {

func isValidEmail(testStr:String) -> Bool {
    println("validate calendar: \(testStr)")
    let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"
    
    var emailTest = NSPredicate(format:"SELF MATCHES %@", emailRegEx)
    let result = emailTest.evaluateWithObject(testStr)
    return result
}
    
    func internetFailAler(){
        dispatch_async(dispatch_get_main_queue(), { () -> Void in
            var alertView:UIAlertView = UIAlertView()
            alertView.title = "No internet connection"
            alertView.message = "Please, check your internet connection."
            alertView.delegate = self
            alertView.addButtonWithTitle("OK")
            alertView.show()
        })

    }
    func savingErrorPopUp(){
        var alertView:UIAlertView = UIAlertView()
        alertView.title = "Server Error"
        alertView.message = "Please try later"
        alertView.delegate = self
        alertView.addButtonWithTitle("OK")
        alertView.show()
    }
}