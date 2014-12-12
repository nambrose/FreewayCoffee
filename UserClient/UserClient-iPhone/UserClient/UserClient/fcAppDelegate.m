//
//  fcAppDelegate.m
//  UserClient
//
//  Created by Nick Ambrose on 9/5/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>
#import "fcAppDelegate.h"
#import "fcRootViewController.h"
#import "fcMenu.h"
#import "Reachability.h"
#import "fcUserItemTable.h"
#import "fcAppSettingsTable.h"
#import "fcUserTipTable.h"
#import "fcLocation.h"
#import "fcLocationTable.h"
#import "fcUserInfo.h"
#import "fcMenuTable.h"
#import "fcItemOrderList.h"
#import "fcItemOrderListTable.h"
#import "MFSideMenu.h"
#import "fcLeftMenuViewController.h"
#import "fcItemsListViewController.h"
#import "fcLastOrder.h"

@implementation fcAppDelegate
@synthesize navController=_navController;
@synthesize window=_window;
@synthesize UserInfo=_userInfo;
@synthesize locationData=_locationData;
@synthesize UserCarInfo=_userCarInfo;

@synthesize UserCreditCardInfo=_userCreditCardInfo;
@synthesize CarData=_carData;
@synthesize menus=_menus;
@synthesize userItems=_userItems;
@synthesize lastOrder=_lastOrder;
@synthesize lastError=_lastError;
@synthesize networkUp=_networkUp;
@synthesize topController=_topController;
@synthesize appSettings=_appSettings;
@synthesize userTips=_userTips;
@synthesize orderLists=_orderLists;


-(id)init
{
    {
        self = [super init];
        if (self)
        {
            _menus = [[fcMenuTable alloc] init];
            _userItems = [[fcUserItemTable alloc]init];
            _lastOrder = nil;
            _networkUp=TRUE;
            _appSettings = [[fcAppSettingsTable alloc]init];
            _userTips = [[fcUserTipTable alloc]init];
            _locationData = [[fcLocationTable alloc]init];
            _orderLists = [[fcItemOrderListTable alloc] init];
        }
        return self;
    }
}

- (fcMenu*) getMenuForCurrentLocation
{
    fcLocation *location = [self getCurrentLocation];
    if(location==nil)
    {
        return nil;
    }
    return [self.menus getMenu:[location LocationMenuID]];
    
}

- (void) ClearCurrentMenu
{
    fcLocation *location = [self getCurrentLocation];
    if(location==nil)
    {
        return;
    }
    [self.menus deleteMenu:[location LocationMenuID]];
}
     

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    // Override point for customization after application launch.
    
    
    if([myCommonAppDelegate isRetina4Display])
    {
        self.topController = [[fcRootViewController alloc] initWithNibName:@"fcRootViewController_ret4" bundle:nil];
    }
    else
    {
        self.topController = [[fcRootViewController alloc] initWithNibName:@"fcRootViewController" bundle:nil];
    }
/*
    self.topController = [[fcRootViewController alloc]
                          initWithNibName:@"fcRootViewController" bundle:nil];

  */  
    [self ClearAllDownloadedData:TRUE];
    
    //UINavigationController *controller = [[UINavigationController alloc] initWithRootViewController:self.topController];
    
    // MFSide Menu junk
    
   // self.leftMenuViewController = [[fcLeftMenuViewController alloc]init];
    
 //MFSideMenuOptions options = MFSideMenuOptionMenuButtonEnabled|MFSideMenuOptionBackButtonEnabled
   //|MFSideMenuOptionShadowEnabled;
   
    //MFSideMenuOptions options = MFSideMenuOptionMenuButtonEnabled |MFSideMenuOptionShadowEnabled;

    
   // MFSideMenuOptions options = MFSideMenuOptionShadowEnabled;
    //MFSideMenuPanMode panMode = MFSideMenuPanModeNavigationBar;
    
   // MFSideMenuPanMode panMode = 0;//MFSideMenuPanModeNavigationBar;
    
    //MFSideMenuPanMode panMode = 0;
    /*
    MFSideMenu *sideMenu = [MFSideMenu menuWithNavigationController:controller
                                                 sideMenuController:self.leftMenuViewController
                                                           location:MFSideMenuLocationLeft
                                                            options:options
                                                            panMode:panMode];
    
    self.leftMenuViewController.sideMenu = sideMenu;
    */
    
    //self.navController= sideMenu.navigationController;
    
    self.navController=[[UINavigationController alloc] initWithRootViewController:self.topController];
    
    [self.navController setNavigationBarHidden:TRUE];
    
   // self.window.rootViewController=sideMenu.navigationController;
    self.window.rootViewController=self.navController;
    
    // allocate a reachability object
    Reachability* reach = [Reachability reachabilityWithHostname:HOST_NAME];
    
    // set the blocks
    reach.reachableBlock = ^(Reachability*reach)
    {
        [myCommonAppDelegate setNetworkUp:TRUE];
        //FC_Log(@"REACHABLE!");
    };
    
    reach.unreachableBlock = ^(Reachability*reach)
    {
        [myCommonAppDelegate setNetworkUp:TRUE];
        //[myCommonAppDelegate setNetworkUp:FALSE];
        //FC_Log(@"UNREACHABLE!");
    };
    
    // start the notifier which will cause the reachability object to retain itself!
    [reach startNotifier];
    //self.window.backgroundColor = [UIColor whiteColor];
    [self.window makeKeyAndVisible];
    return YES;
}
- (void) reachabilityChanged:(Reachability*)reach
{
    self.networkUp=[reach isReachable];
}
- (void) isReachable:(Reachability*)reach
{
    self.networkUp=[reach isReachable];
}
- (void) isUnreachable:(Reachability*)reach
{
    self.networkUp=[reach isReachable];
}
- (void) ClearAllDownloadedData:(BOOL)andOrderLists;
{
    self.UserInfo=nil;
    self.UserInfo= [[fcUserInfo alloc] init];
    
    [self.locationData clear];
    
    self.UserCarInfo=nil;
    self.UserCarInfo=[[NSMutableDictionary alloc] init];
    
    self.userItems=nil;
    self.userItems=[[fcUserItemTable alloc] init];
    
    self.UserCreditCardInfo=nil;
    self.UserCreditCardInfo=[[NSMutableDictionary alloc] init];
    
    self.CarData =nil;
    self.CarData = [[CarMakeModelColorData alloc] init];
    
    [self.menus clearAllDownloadedData];
    [self.appSettings clear];
    [self.userTips clear];
    
    //[self.orderLists clearAllDownloadedData];
    
    if(andOrderLists==TRUE)
    {
        [self.orderLists clearAllDownloadedData];
    }
    else
    {
        [self.orderLists reconcile]; // Keep the "current" order but remove anything that doesnt exist anymore
    }
}
/* deprecated
// Add a User Drink
- (void) addDrinkWithID:(NSString *)DrinkID andDrinkData:(NSDictionary*)DrinkData
{
    [UserDrinks setValue:DrinkData forKey:DrinkID];
}
*/
- (NSString*) makeLastErrorText
{
    if([self.lastError count]==0)
    {
        return NONE_TEXT;
    }
    
    // Android Has this too: "\n" + getString(R.string.error_code_minor) + LastError.get(FreewayCoffeeXMLHelper.ERROR_CODE_MINOR) +
    NSString *result = [NSString stringWithFormat:@"Error Code:[%@]\n\n%@",
                        [self.lastError valueForKey:ERROR_CODE_MAJOR],
                        [self.lastError valueForKey:ERROR_LONG_TEXT]];
    return result;

}
- (void)storeUsername:(NSString*)userName
{
    // TODO error check
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    // saving an NSString
    [prefs setObject:userName forKey:FC_USERNAME_PREF_KEY];
    [prefs synchronize];
}
- (void)storeName:(NSString*)name
{
    // TODO Error check
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    // saving an NSString
    [prefs setObject:name forKey:FC_NAME_PREF_KEY];
    [prefs synchronize];
}

- (void)storePassword:(NSString*)password
{
    // TODO Error check
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    // saving an NSString
    [prefs setObject:password forKey:FC_PASSWORD_PREF_KEY];
    [prefs synchronize];
    
}

- (NSString*) getUsername
{
    // TODO Error Check
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    // getting an NSString
    return [prefs stringForKey:FC_USERNAME_PREF_KEY];
    
    
}

- (NSString*) getName
{
    return [self.UserInfo userName];
}

- (NSString*) getPassword
{
    // TODO Error Check
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    // getting an NSString
    return [prefs stringForKey:FC_PASSWORD_PREF_KEY];
}

- (BOOL) areUsernameAndPasswordSet
{
    NSString *userName = [self getUsername];
    if( (nil==userName) || ( [userName length]==0))
    {
        return FALSE;
    }
    NSString *password = [self getPassword];
    if( (nil==password) || ( [password length]==0))
    {
        return FALSE;
    }
    
    return TRUE;
}
- (BOOL) isCreditCardPresent
{
    return [self.UserCreditCardInfo count]>0;
    
}
- (BOOL) IsCarDataDownloaded
{
    return [self.CarData IsDataPopulated];
}

- (BOOL) IsItemDataDownloaded
{
    return FALSE;
}

- (void) deleteItemByID:(NSNumber*)itemID fromMenuID:(NSNumber*)menuID;
{
    [self.userItems removeUserItem:itemID];
    
    if(menuID!=nil && ![fcMenu isMenuIDNone:menuID])
    {
        
        [self.orderLists removeItemFromOrder:itemID fromMenuID:(NSNumber*)menuID andRemoveAll:TRUE];
        
    }
}


// Default if no data is NO (Car)
- (BOOL) IsUserWalkup
{
    NSNumber *userArriveMode = [self.UserInfo userArriveMode];
    
    if(nil==userArriveMode)
    {
        return FALSE;
    }
    else if([userArriveMode isEqualToNumber:[NSNumber numberWithInt:ARRIVE_MODE_WALKUP_NUM] ])
    {
        return TRUE;
    }
    return FALSE;
}

- (BOOL)validateEmail:(NSString *)inputText {
    NSString *emailRegex = @"[A-Z0-9a-z][A-Z0-9a-z._%+-]*@[A-Za-z0-9][A-Za-z0-9.-]*\\.[A-Za-z]{2,6}";
    NSPredicate *emailTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", emailRegex];
    NSRange aRange;
    if([emailTest evaluateWithObject:inputText]) {
        aRange = [inputText rangeOfString:@"." options:NSBackwardsSearch range:NSMakeRange(0, [inputText length])];
        int indexOfDot = aRange.location;
        //FC_Log(@"aRange.location:%d - %d",aRange.location, indexOfDot);
        if(aRange.location != NSNotFound) {
            NSString *topLevelDomain = [inputText substringFromIndex:indexOfDot];
            topLevelDomain = [topLevelDomain lowercaseString];
            //FC_Log(@"topleveldomains:%@",topLevelDomain);
            NSSet *TLD;
            TLD = [NSSet setWithObjects:@".aero", @".asia", @".biz", @".cat", @".com", @".coop", @".edu", @".gov", @".info", @".int", @".jobs", @".mil", @".mobi", @".museum", @".name", @".net", @".org", @".pro", @".tel", @".travel", @".ac", @".ad", @".ae", @".af", @".ag", @".ai", @".al", @".am", @".an", @".ao", @".aq", @".ar", @".as", @".at", @".au", @".aw", @".ax", @".az", @".ba", @".bb", @".bd", @".be", @".bf", @".bg", @".bh", @".bi", @".bj", @".bm", @".bn", @".bo", @".br", @".bs", @".bt", @".bv", @".bw", @".by", @".bz", @".ca", @".cc", @".cd", @".cf", @".cg", @".ch", @".ci", @".ck", @".cl", @".cm", @".cn", @".co", @".cr", @".cu", @".cv", @".cx", @".cy", @".cz", @".de", @".dj", @".dk", @".dm", @".do", @".dz", @".ec", @".ee", @".eg", @".er", @".es", @".et", @".eu", @".fi", @".fj", @".fk", @".fm", @".fo", @".fr", @".ga", @".gb", @".gd", @".ge", @".gf", @".gg", @".gh", @".gi", @".gl", @".gm", @".gn", @".gp", @".gq", @".gr", @".gs", @".gt", @".gu", @".gw", @".gy", @".hk", @".hm", @".hn", @".hr", @".ht", @".hu", @".id", @".ie", @" No", @".il", @".im", @".in", @".io", @".iq", @".ir", @".is", @".it", @".je", @".jm", @".jo", @".jp", @".ke", @".kg", @".kh", @".ki", @".km", @".kn", @".kp", @".kr", @".kw", @".ky", @".kz", @".la", @".lb", @".lc", @".li", @".lk", @".lr", @".ls", @".lt", @".lu", @".lv", @".ly", @".ma", @".mc", @".md", @".me", @".mg", @".mh", @".mk", @".ml", @".mm", @".mn", @".mo", @".mp", @".mq", @".mr", @".ms", @".mt", @".mu", @".mv", @".mw", @".mx", @".my", @".mz", @".na", @".nc", @".ne", @".nf", @".ng", @".ni", @".nl", @".no", @".np", @".nr", @".nu", @".nz", @".om", @".pa", @".pe", @".pf", @".pg", @".ph", @".pk", @".pl", @".pm", @".pn", @".pr", @".ps", @".pt", @".pw", @".py", @".qa", @".re", @".ro", @".rs", @".ru", @".rw", @".sa", @".sb", @".sc", @".sd", @".se", @".sg", @".sh", @".si", @".sj", @".sk", @".sl", @".sm", @".sn", @".so", @".sr", @".st", @".su", @".sv", @".sy", @".sz", @".tc", @".td", @".tf", @".tg", @".th", @".tj", @".tk", @".tl", @".tm", @".tn", @".to", @".tp", @".tr", @".tt", @".tv", @".tw", @".tz", @".ua", @".ug", @".uk", @".us", @".uy", @".uz", @".va", @".vc", @".ve", @".vg", @".vi", @".vn", @".vu", @".wf", @".ws", @".ye", @".yt", @".za", @".zm", @".zw", nil];
            if(topLevelDomain != nil && ([TLD containsObject:topLevelDomain])) {
                //FC_Log(@"TLD contains topLevelDomain:%@",topLevelDomain);
                return TRUE;
            }
            /*else {
             FC_Log(@"TLD DOEST NOT contains topLevelDomain:%@",topLevelDomain);
             }*/
            
        }
    }
    return FALSE;
}

- (CALayer*) makeGreenGradient
{
    CAGradientLayer *gradient = [CAGradientLayer layer];
    
    // #90EE90
    
    UIColor *lightGreen = [UIColor colorWithRed:0.0 green:0.6 blue:0.0 alpha:1.0];
    
    gradient.colors = [NSArray arrayWithObjects:(id)[lightGreen CGColor], (id)[[UIColor greenColor] CGColor], nil];
    gradient.cornerRadius = DEFAULT_CORNER_RADIUS;

    return gradient;
}

- (CAGradientLayer*) makeRedGradient
{
    CAGradientLayer *gradient = [CAGradientLayer layer];
    
    UIColor *darkRed = [UIColor colorWithRed:1.0 green:0.0 blue:0.0 alpha:1.0];
    UIColor *lightRed = [UIColor colorWithRed:0.5 green:0.0 blue:0.0 alpha:1.0];
    gradient.colors = [NSArray arrayWithObjects:(id)[darkRed CGColor], (id)[lightRed CGColor],nil];
    gradient.cornerRadius = DEFAULT_CORNER_RADIUS;
    return gradient;
}
- (CALayer*) makeGreyGradient
{
    CAGradientLayer *gradient = [CAGradientLayer layer];
    
    gradient.colors = [NSArray arrayWithObjects:(id)[[UIColor grayColor] CGColor], (id)[[UIColor lightGrayColor] CGColor], nil];
    gradient.cornerRadius = DEFAULT_CORNER_RADIUS;
    
    return gradient;
}




- (void) openFeedbackEmailClient
{

    //NSString *recipients = @"mailto:info@freewaycoffee.com?cc=second@example.com,third@example.com&subject=Hello from California!";
    NSString *recipients = [NSString stringWithFormat:@"mailto:info@freewaycoffee.com?&subject=Freeway Coffee - Feedback From (%@) ",[self getName] ];
                                                                                                                
  //  NSString *recipients = @"mailto:info@freewaycoffee.com?&subject=Hello from California!";
    
    NSString *body = [NSString stringWithFormat:@"&body=Feedback from: %@\n", [self getUsername]];
    
    //NSString *body = @"&body=It is raining in sunny California!";
        
    NSString *email = [NSString stringWithFormat:@"%@%@", recipients, body];
    email = [email stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:email]];

}
- (void) applyUserArriveMode:(NSString *)mode;
{
    // Just shove it in. No checking. // TODO CHECK ME FIXME
    if([mode isEqualToString:ARRIVE_MODE_CAR_STR])
    {
        [self.UserInfo setUserArriveMode:[NSNumber numberWithInt:ARRIVE_MODE_CAR_NUM]];
    }
    else if([mode isEqualToString:ARRIVE_MODE_WALKUP_STR])
    {
        [self.UserInfo setUserArriveMode:[NSNumber numberWithInt:ARRIVE_MODE_WALKUP_NUM]];
    }
}



- (BOOL) isRetina4Display
{
    CGRect bounds = [[UIScreen mainScreen] bounds];
    CGFloat height = bounds.size.height;
    CGFloat scale = [[UIScreen mainScreen] scale];
    
    return (([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) && ((height * scale) >= 1136));
}
- (NSString*) getBackgroundImageName
{
    if([self isRetina4Display])
    {
        return IMG_BACKGROUND_IMAGE_NAME_RET4;
    }
    else
    {
        return IMG_BACKGROUND_IMAGE_NAME;
    }
}

- (fcLocation*) getCurrentLocation
{
    if(self.UserInfo==nil)
    {
        return nil;
    }
    
    NSNumber *userLocationID = [self.UserInfo userLocationID];
    
    if([fcLocation isLocationIDNone:userLocationID])
    {
        return nil;
    }
    
    return [self.locationData getLocation:userLocationID];
}

- (fcItemOrderList*)getCurrentOrder
{
    fcLocation* location = [self getCurrentLocation];
    if(location==nil)
    {
        return nil;
    }
    return [[self orderLists] getOrderListForMenuID:[location LocationMenuID]];
}

- (void) updateLastOrderStatus:(fcLastOrder*)lastOrder
{
    if(lastOrder==nil)
    {
        self.lastOrder=nil;
        return;
    }
    [self setLastOrder:lastOrder];
    
    [[self lastOrder] setOrderStatus:ORDER_SUBMITTED];
    // These need to go away !
    [[self lastOrder] updateImHereStatus];
    [[self lastOrder] updateOrderIDStatus];
    
}
- (void)generateCurrentDefaultOrderIfEmpty
{
    fcLocation* location = [self getCurrentLocation];
    if(location==nil)
    {
        return ;
    }
    [self.orderLists generateDefaultOrderIfEmpty: [location LocationMenuID]];
}

- (void) prepareButtonForGradient:(UIButton*)button
{
    for(UIView* subView in button.subviews)
    {
        if([subView isKindOfClass:NSClassFromString(@"UIGroupTableViewCellBackground")])
        {
            [subView setHidden:YES];
        }
    }
   
}


- (NSNumberFormatter*) getCurrencyFormatter
{
    NSNumberFormatter *currencyFormatter  = [[NSNumberFormatter alloc] init];
    [currencyFormatter setGeneratesDecimalNumbers:YES];
    [currencyFormatter setNumberStyle:NSNumberFormatterCurrencyStyle];
    return currencyFormatter;
}

-(void) goToTopPageComitted
{
    NSArray *array = [self.navController viewControllers];
    
    fcItemsListViewController *orderPage = (fcItemsListViewController*)[array objectAtIndex:1];
    if([orderPage respondsToSelector:@selector(childFinishedCommit)])
    {
        [orderPage childFinishedCommit];
    }
    [self.navController popToViewController:[array objectAtIndex:1] animated:NO];
}

-(void) goToTopPage
{
    NSArray *array = [self.navController viewControllers];
    
    [self.navController popToViewController:[array objectAtIndex:1] animated:NO];
}


- (NSInteger) getRequestShortTimeout
{
    return 30;
}
- (NSInteger) getRequestLongTimeout
{
    return 60;
}

- (void)mergeLocationData:(fcLocationTable*)locations
{
    // Set is basically merge
    [self.locationData setLocationTable:locations];
}
- (UIColor*) getNotPopulatedItemColor
{
    return [UIColor redColor];
}
- (UIColor*) getNormalItemColor
{
    return [UIColor blackColor];
}
- (UIColor*) getGoodItemColor
{
    return [UIColor greenColor];
}

- (NSString*)getRet4ControllerName:(NSString*)controllerName;
{
    if([self isRetina4Display])
    {
        return [NSString stringWithFormat:@"%@_ret4",controllerName];
    }
    else
    {
        return controllerName;
    }
}
/*
public String MakeCurrentOrderURL()  throws UnsupportedEncodingException
{
    String DrinksList = GetCurrentDrinkOrderIDList();
    
    
    String UserArriveMode = FCXMLHelper.ARRIVE_MODE_CAR_STR;
    if(IsUserWalkup())
    {
        UserArriveMode = FCXMLHelper.ARRIVE_MODE_WALKUP_STR;
    }
    String MakeOrderURL = new String(BASE_URL + ORDER_PAGE + "?" + COMMAND_STRING + "=" + MAKE_ORDER_COMMAND  + "&" +
                                     APP_CLIENT_TYPE + "=" + URLEncoder.encode(APP_CLIENT_VALUE ,"utf-8") + "&" +
                                     FCXMLHelper.USER_ARRIVE_MODE_CMD_ARG  +"=" + UserArriveMode +"&" +
                                     ORDER_DRINKS_LIST + "=" + URLEncoder.encode(DrinksList,"utf-8"));
    
    return MakeOrderURL;
}
*/

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
