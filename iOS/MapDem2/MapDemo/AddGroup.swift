//
//  AddGroup.swift
//  MapDemo
//
//  Created by mauro piva on 18/02/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import UIKit
import CoreData

class AddGroup: UIViewController, UITextFieldDelegate {
    
   
    @IBAction func Back() {
        
        self.dismissViewControllerAnimated(true, completion: nil)
        
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func Backold() {
        
        self.dismissViewControllerAnimated(true, completion: nil)
    }
  
    func textFieldShouldReturn(textField: UITextField!) -> Bool // called when 'return' key pressed. return NO to ignore.
    {
        textField.resignFirstResponder()
        return true;
    }
    
    override func touchesBegan(touches: NSSet, withEvent event: UIEvent) {
       
    }
    
    
        
        
    }
    

