//
//  TodayViewController.swift
//  FastPark
//
//  Created by mauro piva on 18/03/15.
//  Copyright (c) 2015 familyParking. All rights reserved.
//

import UIKit
import NotificationCenter

class TodayViewController: UITableViewController, NCWidgetProviding {
    
    var items : [RSSItem]?
    
    let dateFormatter :NSDateFormatter = {
        let formatter = NSDateFormatter()
        formatter.dateStyle = .ShortStyle
        return formatter
        }()
    
    let expandButton = UIButton()
    
    let userDefaults = NSUserDefaults.standardUserDefaults()
    
    var expanded : Bool {
        get {
            return userDefaults.boolForKey("expanded")
        }
        set (newExpanded) {
            userDefaults.setBool(newExpanded, forKey: "expanded")
            userDefaults.synchronize()
        }
    }
    
    let defaultNumRows = 3
    let maxNumberOfRows = 6
    
    var cachedItems : [RSSItem]? {
        get {
            return TMCache.sharedCache().objectForKey("feed") as? [RSSItem]
        }
        set (newItems) {
            TMCache.sharedCache().setObject(newItems, forKey: "feed")
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        updateExpandButtonTitle()
        expandButton.addTarget(self, action: "toggleExpand", forControlEvents: .TouchUpInside)
        tableView.sectionFooterHeight = 44
        
        items = cachedItems
        updatePreferredContentSize()
    }
    
    func updatePreferredContentSize() {
        preferredContentSize = CGSizeMake(CGFloat(0), CGFloat(tableView(tableView, numberOfRowsInSection: 0)) * CGFloat(tableView.rowHeight) + tableView.sectionFooterHeight)
    }
    
    override func viewWillTransitionToSize(size: CGSize, withTransitionCoordinator coordinator: UIViewControllerTransitionCoordinator) {
        coordinator.animateAlongsideTransition({ context in
            self.tableView.frame = CGRectMake(0, 0, size.width, size.height)
            }, completion: nil)
    }
    
    func widgetPerformUpdateWithCompletionHandler(completionHandler: ((NCUpdateResult) -> Void)!) {
        loadFeed(completionHandler)
    }
    
    func loadFeed(completionHandler: ((NCUpdateResult) -> Void)!) {
        
        let url = NSURL(string: "http://blog.xebia.com/feed/")
        let req = NSURLRequest(URL: url!)
        
        RSSParser.parseRSSFeedForRequest(req,
            success: { feedItems in
                if self.hasNewData(feedItems as [RSSItem]) {
                    self.items = feedItems as? [RSSItem]
                    self.tableView .reloadData()
                    self.updatePreferredContentSize()
                    self.cachedItems = self.items
                    completionHandler(.NewData)
                } else {
                    completionHandler(.NoData)
                }
            },
            failure: { error in
                println(error)
                completionHandler(.Failed)
                
        })
    }
    
    func hasNewData(feedItems: [RSSItem]) -> Bool {
        return items == nil || items! != feedItems
    }
    
    // MARK: Table view data source
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let items = items {
            return min(items.count, expanded ? maxNumberOfRows : defaultNumRows)
        }
        return 0
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("RSSItem", forIndexPath: indexPath) as RSSItemTableViewCell
        
        if let item = items?[indexPath.row] {
            cell.titleLabel.text = item.title
            cell.authorLabel.text = item.author
            cell.dateLabel.text = dateFormatter.stringFromDate(item.pubDate)
        }
        
        return cell
    }
    
    override func tableView(tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        return expandButton
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        if let item = items?[indexPath.row] {
            if let context = extensionContext {
                context.openURL(item.link, completionHandler: nil)
            }
        }
    }
    
    // MARK: expand
    
    func updateExpandButtonTitle() {
        expandButton.setTitle(expanded ? "Show less" : "Show more", forState: .Normal)
    }
    
    func toggleExpand() {
        expanded = !expanded
        updateExpandButtonTitle()
        updatePreferredContentSize()
        tableView.reloadData()
    }
}
