//
//  fcLocationListViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 1/17/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>
#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"

@class fcXMLParserLocations;
@class fcLocation;

@interface fcLocationListViewController : UIViewController <UITableViewDelegate,UITableViewDataSource,MBProgressHUDDelegate>
{
@private
    MBProgressHUD *HUD;
}

@property (nonatomic,weak) IBOutlet UITableView *table;
@property (nonatomic,weak) IBOutlet UIButton *refreshButton;
@property (nonatomic,weak) IBOutlet UIView *tableBGView;

@property (nonatomic,weak) IBOutlet UITableViewCell *tableCell;
@property (nonatomic,strong) CAGradientLayer *refreshButtonGradient;

@property (nonatomic,strong) NSMutableData *responseData;

@property (nonatomic,strong) NSURLConnection *conRequest;
@property (nonatomic,strong) fcXMLParserLocations *locationsXMLParser;

@property (nonatomic, strong) NSMutableArray *ListIndex;

- (IBAction) doBack;
- (void) doGetLocationsList;
- (void) createListIndex;
- (IBAction) doRefresh:(id)sender;
- (void) doSetUserLocation:(fcLocation*)location;
- (void) goToRootViewController;
@end
