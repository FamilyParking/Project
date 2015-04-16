//
//  Utilities.swift
//  
//
//  Created by Mauro on 16/04/15.
//
//

import Foundation

func isValidEmail(testStr:String) -> Bool {
    println("validate calendar: \(testStr)")
    let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"
    
    var emailTest = NSPredicate(format:"SELF MATCHES %@", emailRegEx)
    let result = emailTest.evaluateWithObject(testStr)
    return result
}