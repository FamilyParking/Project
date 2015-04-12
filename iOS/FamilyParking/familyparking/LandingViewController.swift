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

class LandingViewController: UIViewController, iCarouselDataSource, iCarouselDelegate {
    
    
    var items: [Int] = []
    
    
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
        carousel.type = .Rotary
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
            (view as UIImageView!).image = UIImage(named: "logo")
            view.contentMode = .Center
            
            label = UILabel(frame:view.bounds)
            label.backgroundColor = UIColor.clearColor()
            label.textAlignment = .Center
            label.font = label.font.fontWithSize(50)
            label.tag = 1
            view.addSubview(label)
        }
        else
        {
            //get a reference to the label in the recycled view
            label = view.viewWithTag(1) as UILabel!
        }
        
        //set item label
        //remember to always set any properties of your carousel item
        //views outside of the `if (view == nil) {...}` check otherwise
        //you'll get weird issues with carousel item content appearing
        //in the wrong place in the carousel
      //  label.text = "\(items[index])"
        
        return view
    }
    
    func carousel(carousel: iCarousel!, valueForOption option: iCarouselOption, withDefault value: CGFloat) -> CGFloat
    {
        if (option == .Spacing)
        {
            return value * 1.1
        }
        return value
    }
    

   
    
}

