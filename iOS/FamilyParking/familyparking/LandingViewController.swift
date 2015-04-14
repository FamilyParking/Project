//
//  LandingViewController.swift
//  familyparking
//
//  Created by mauro piva on 12/04/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

//
//  ViewController.swift
//  familyparking
//
//  Created by mauro piva on 19/02/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import UIKit

class LandingViewController: UIViewController, iCarouselDataSource, iCarouselDelegate, FBSDKLoginButtonDelegate {
    
    
    var items: [Int] = []
    var imtapp:Int = 0;
    @IBOutlet weak var fbLoginView: FBSDKLoginButton!
    
    @IBOutlet weak var carousel: iCarousel!
    override func awakeFromNib()
    {
        super.awakeFromNib()
        for i in 0...4
        {
            items.append(i)
        }
    }
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
        carousel.type = .Linear
        self.fbLoginView.delegate = self
        self.fbLoginView.readPermissions = ["public_profile", "email", "user_friends"]
        
            }
    
    func imageTapped(img: AnyObject)
    {
        if (imtapp==4){
            openUrl("http://www.familyparking.it")}
    }
    
    func openUrl(url:String!) {
        
        let targetURL=NSURL(string: url)
        
        let application=UIApplication.sharedApplication()
        
        application.openURL(targetURL!);
    }
    
    func numberOfItemsInCarousel(carousel: iCarousel!) -> Int
    {
        return items.count
    }
    
    func carousel(carousel: iCarousel!, viewForItemAtIndex index: Int, var reusingView view: UIView!) -> UIView!
    {
        var label: UILabel! = nil
        
        //create new view if no view is available for recycling
        if (view == nil)
        {
            //don't do anything specific to the index within
            //this `if (view == nil) {...}` statement because the view will be
            //recycled and used with other index values later
            view = UIImageView(frame:CGRectMake(0, 0, 200, 200))
            (view as UIImageView!).image = UIImage(named: "img\(items[index])")
            view.contentMode = .Center
            
            label = UILabel(frame:view.bounds)
            label.backgroundColor = UIColor.clearColor()
            label.textAlignment = .Center
            label.font = label.font.fontWithSize(50)
            label.tag = 1
            view.addSubview(label)
            
            var tgr = UITapGestureRecognizer(target:self, action:Selector("imageTapped:"))
            
            view.addGestureRecognizer(tgr)
            view.userInteractionEnabled = true
        }
        else
        {
            (view as UIImageView!).image = UIImage(named: "img\(items[index])")
            //get a reference to the label in the recycled view
            label = view.viewWithTag(1) as UILabel!
            println("PROBLEMA")
        }
        
        //set item label
        //remember to always set any properties of your carousel item
        //views outside of the `if (view == nil) {...}` check otherwise
        //you'll get weird issues with carousel item content appearing
        //in the wrong place in the carousel
       // label.text = "\(items[index])"
        imtapp=index
        return view
    }
    
    func carousel(carousel: iCarousel!, valueForOption option: iCarouselOption, withDefault value: CGFloat) -> CGFloat
    {
        if (option == .Spacing)
        {
            return value * 1.5
        }
        return value
    }
    
    func loginButton(loginButton: FBSDKLoginButton!, didCompleteWithResult result: FBSDKLoginManagerLoginResult!, error: NSError!) {
        println("User Logged In")
        
        if ((error) != nil)
        {
            // Process error
        }
        else if result.isCancelled {
            // Handle cancellations
        }
        else {
            // If you ask for multiple permissions at once, you
            // should check if specific permissions missing
            if result.grantedPermissions.containsObject("email")
            {
                returnUserData()
            }
        }
    }
    
    func loginButtonDidLogOut(loginButton: FBSDKLoginButton!) {
        println("User Logged Out")
    }
    
    func returnUserData()
    {
        let graphRequest : FBSDKGraphRequest = FBSDKGraphRequest(graphPath: "me", parameters: nil)
        graphRequest.startWithCompletionHandler({ (connection, result, error) -> Void in
            
            if ((error) != nil)
            {
                // Process error
                println("Error: \(error)")
            }
            else
            {
                println("fetched user: \(result)")
                let userName : NSString = result.valueForKey("name") as NSString
                println("User Name is: \(userName)")
                let userEmail : NSString? = result.valueForKey("email") as NSString?
                if userEmail == nil {return}
                println("User Email is: \(userEmail)")
                self.requestPin(userName, email: userEmail!)
            }
        })
    }
    
    func requestPin(name:String,email:String){
        var request = NSMutableURLRequest(URL: NSURL(string: Comments().serverPath + "register_social")!)
        var session = NSURLSession.sharedSession()
        request.HTTPMethod = "POST"
        var params = ["ID":UIDevice.currentDevice().identifierForVendor.UUIDString,
            "Name":name,
            "Email":email.lowercaseString] as Dictionary<String, NSObject>
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
                
            }
            
            
            var strData:String? = NSString(data: data, encoding: NSUTF8StringEncoding)
            println("Body: \(strData!)")
            if((strData!.rangeOfString("true")?.isEmpty==false)){
                // if(strData!.containsString("Code sent")){
                println("Code Sent")
                var err: NSError?
                var json = NSJSONSerialization.JSONObjectWithData(data, options: .MutableLeaves, error: &err) as? NSDictionary
                var pin:Int = json!["Object"]! as Int
                let prefs:NSUserDefaults = NSUserDefaults.standardUserDefaults()
                prefs.setObject(name, forKey: "USERNAME")
                prefs.setObject(email.lowercaseString, forKey: "EMAIL")
                prefs.setObject((pin.description as String), forKey: "PIN")
                prefs.setInteger(1, forKey:"ISLOGGEDIN")
                prefs.synchronize()
                
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    self.dismissViewControllerAnimated(true, completion: nil)
                })
            }
            else{
                
            }
        })
        task.resume()
    }

    
}

