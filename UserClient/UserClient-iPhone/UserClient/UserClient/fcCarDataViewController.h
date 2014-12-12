//
//  fcCarDataViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "fcAppDelegate.h"
#import "fcCarDataValuePickerViewController.h"
#import "fcCarDataLicenseViewController.h"
#import "MBProgressHUD.h"
#import "CarMake.h"
#import "CarDataIndexItem.h"
#import "UICheckbox.h"

#import "Constants.h"
#import "XMLHelper.h"
@protocol  ItemListChildFinished;

@interface fcCarDataViewController : UIViewController <NSXMLParserDelegate,MBProgressHUDDelegate, CarDataValuePickerDelegate,CarLicenseViewProtocol>
{

@private
MBProgressHUD *HUD;
NSMutableData *responseData;
NSMutableArray *ListIndex;
int MakeID;
int ModelID;
int ColorID;
NSString *m_UserTag;
BOOL CarDataSetResultOK; // This gets set by the parser when the car data is set correctly and we can exit
BOOL UpdatingCarData;

}
//@property (nonatomic, weak) IBOutlet UILabel *WelcomeUserLabel;
@property (nonatomic, strong) IBOutlet UITableView *ItemsList;
@property (nonatomic, strong) IBOutlet UIView *checkView;

@property (nonatomic, weak) id<ItemListChildFinished> delegate;
@property (nonatomic, strong) NSMutableArray *ListIndex;

@property (nonatomic, strong) NSString *m_UserTag;
@property (nonatomic, weak) IBOutlet UICheckbox *walkupCheck;
@property (nonatomic,assign) BOOL signonError;
@property (nonatomic,strong) NSURLConnection *conRequest;
@property (nonatomic, weak) IBOutlet UIButton *actionButton;
@property (nonatomic,weak) IBOutlet UIView *tableFooter;

//@property (nonatomic, assign) UITableViewCell *ItemListTableCell;


- (void) AddFooter;
- (void) CreateListIndex;
- (IBAction) doBack: (id) sender;
- (IBAction) doUpdateCarData: (id) sender;
- (void) doDownloadCarData;
- (CarMake*) parseCarMake: (NSDictionary *)attributes;
- (void) parseCarModel: (NSDictionary *)attributes;
- (void) parseCarColor: (NSDictionary *)attributes;
- (void) updateCell:(UITableViewCell*)cell withItem: (CarDataIndexItem*)item andIndex:(int)index;
- (void) setMakeID:(int)makeID;
- (void) setModelID:(int)modelID;
- (void) setColorID:(int)colorID;
- (void) setLicenseText:(NSString*)License;
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView;
- (IBAction)walkupChecked:(id)sender;
@end
