//
//  MyTableViewCell.swift
//  MapDemo
//
//  Created by mauro piva on 09/01/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import UIKit


class MyTableViewCell: UITableViewCell {

    
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: UITableViewCellStyle.Subtitle, reuseIdentifier: reuseIdentifier)
      //  self.backgroundColor = UIColor.orangeColor()
        self.selectionStyle = UITableViewCellSelectionStyle.None
        
    }
    

    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
