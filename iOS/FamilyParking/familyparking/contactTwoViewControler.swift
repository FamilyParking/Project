//
//  contactTwoViewControler.swift
//  familyparking
//
//  Created by mauro piva on 13/03/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//


import Foundation
import UIKit
import CoreData
import UIKit

import AddressBook
import AddressBookUI


struct MyAddressBook {
    let abAddressBook: ABAddressBookRef
    static func createWithOptions(options: [NSObject: AnyObject]?, error: NSErrorPointer) -> MyAddressBook? {
        var cfError: Unmanaged<CFError>?
        let addressBook: ABAddressBook? = ABAddressBookCreateWithOptions(options, &cfError)?.takeRetainedValue()
        if cfError != nil && error != nil {
            error.memory = (cfError!.takeUnretainedValue() as AnyObject as! NSError)
        }
        return addressBook != nil ? MyAddressBook(abAddressBook: addressBook!) : nil
    }
}

class contactTwoViewController: UIViewController, ABPeoplePickerNavigationControllerDelegate {
    
    var addressBook: ABAddressBook!

    @IBAction func WebSiteLinkButton(sender: AnyObject) {
       showPeoplePickerController()
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
}

    
    
}
