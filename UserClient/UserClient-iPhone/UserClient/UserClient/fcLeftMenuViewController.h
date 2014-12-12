//
//  fcLeftMenuViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 2/9/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <UIKit/UIKit.h>

@class WEPopoverController;

@interface fcLeftMenuViewController : UITableViewController

@property (nonatomic,weak) WEPopoverController *controller;
@property (nonatomic,strong) NSMutableArray *ItemsList;
//@property (nonatomic, weak) MFSideMenu *sideMenu;
-(void) openLastOrderController;
-(void) openAboutController;
@end
