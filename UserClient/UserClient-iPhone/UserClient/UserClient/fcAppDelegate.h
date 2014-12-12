//
//  fcAppDelegate.h
//  UserClient
//
//  Created by Nick Ambrose on 9/5/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>

#import <UIKit/UIKit.h>
#import "CarMakeModelColorData.h"

@class fcUserItemTable;
@class fcTestViewController;
@class fcLastOrder;
@class fcRootViewController;
@class fcLocation;
@class fcLocationTable;
@class fcItemOrderList;

// OBject borders
/*
 // Create colored border using CALayer property
 [[webView layer] setBorderColor:
 [[UIColor colorWithRed:0.52 green:0.09 blue:0.07 alpha:1] CGColor]];
 [[webView layer] setBorderWidth:2.75];
 
 [[self view] addSubview:webView];
 */

#define myCommonAppDelegate (fcAppDelegate*)[[UIApplication sharedApplication] delegate]

@class fcMenu;
@class fcMenuTable;
@class Reachability;
@class fcAppSettingsTable;
@class fcUserTipTable;
@class fcUserInfo;
@class fcItemOrderListTable;

@interface fcAppDelegate : UIResponder <UIApplicationDelegate>
{
    NSMutableDictionary *UserInfo;
    //NSMutableDictionary *UserLocationInfo;
    NSMutableDictionary *UserCarInfo;
    NSMutableDictionary *UserDrinks;
    NSMutableDictionary *UserCreditCardInfo;
    
    CarMakeModelColorData *CarData;
    //fcAppSettingsTable *appSettings;
    

}
@property (nonatomic, strong) UIWindow *window;

@property (nonatomic, strong) fcUserInfo *UserInfo;

@property (nonatomic,strong) fcLocationTable *locationData;

@property (nonatomic, strong) NSMutableDictionary *UserCarInfo;
@property (nonatomic, strong) NSMutableDictionary *UserCreditCardInfo;
@property (nonatomic, strong) CarMakeModelColorData *CarData;
@property (nonatomic, strong) UINavigationController *navController;
@property (nonatomic, strong) fcMenuTable* menus;
@property (nonatomic, strong) fcUserItemTable *userItems;
@property (nonatomic, strong) fcLastOrder *lastOrder;
@property (nonatomic, strong) NSMutableDictionary *lastError;
@property (nonatomic, assign) BOOL networkUp;

@property (nonatomic,strong) fcRootViewController *topController;


@property (nonatomic,strong) fcAppSettingsTable *appSettings;
@property (nonatomic,strong) fcUserTipTable *userTips;
@property (nonatomic,strong) fcItemOrderListTable *orderLists;

- (id)init;
- (void)storeUsername:(NSString*)userName;
- (void)storeName:(NSString*)name;
- (void)storePassword:(NSString*)password;
- (NSString*) getUsername;
- (NSString*) getName;
- (NSString*) getPassword;
- (BOOL) areUsernameAndPasswordSet;
// if andOrderLists is true, they get cleared. If not, they get reconciled
- (void) ClearAllDownloadedData:(BOOL)andOrderLists;
- (void) ClearCurrentMenu;
- (BOOL) IsCarDataDownloaded;
- (BOOL) IsItemDataDownloaded;
- (BOOL) IsUserWalkup;
- (BOOL) isCreditCardPresent;
// Not named set just in case of KVO KVO OVK
- (void) applyUserArriveMode:(NSString *)mode;
- (NSString*) makeLastErrorText;
- (BOOL)validateEmail:(NSString *)inputText;
- (void) openFeedbackEmailClient;
- (void) reachabilityChanged:(Reachability*)reach;
- (void) isReachable:(Reachability*)reach;
- (void) isUnreachable:(Reachability*)reach;
- (CAGradientLayer*) makeGreenGradient;
- (CAGradientLayer*) makeGreyGradient;
- (CAGradientLayer*) makeRedGradient;
- (BOOL) isRetina4Display;
- (NSString*) getBackgroundImageName; // Based on retina4 etc
- (void) updateLastOrderStatus:(fcLastOrder*)lastOrder;
-(void) goToTopPage;
-(void) goToTopPageComitted; // Do ChildFinishedCommit too

- (NSNumberFormatter*) getCurrencyFormatter;


- (void)mergeLocationData:(fcLocationTable*)locations;

- (fcLocation*) getCurrentLocation; // nil if nothing set.
- (fcItemOrderList*)getCurrentOrder; // nil if nothing set.
- (void)generateCurrentDefaultOrderIfEmpty;

- (UIColor*) getNotPopulatedItemColor; // i.e. something mandatory is not filled
- (UIColor*) getNormalItemColor; // Usually Black
- (UIColor*) getGoodItemColor; // Usually Green
- (void) prepareButtonForGradient:(UIButton*)button; // Gross hack to allow gradient to display ?
// Add a User Drink
//- (void) addDrinkWithID:(NSString *)DrinkID andDrinkData:(NSDictionary*)DrinkData;

- (NSInteger) getRequestShortTimeout;
- (NSInteger) getRequestLongTimeout;
- (fcMenu*) getMenuForCurrentLocation; // Gets the menu associated with users current location (or nil)

// Gone it from the entire universe
- (void) deleteItemByID:(NSNumber*)itemID fromMenuID:(NSNumber*)menuID;
- (NSString*)getRet4ControllerName:(NSString*)controllerName;;
@end
