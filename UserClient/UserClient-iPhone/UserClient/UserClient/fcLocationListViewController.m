//
//  fcLocationListViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 1/17/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import "fcLocationListViewController.h"
#import "fcItemsListViewController.h"
#import "fcXMLParserLocations.h"
#import "fcLocation.h"
#import "fcLocationTable.h"
#import "fcLocationsListIndexData.h"

#import "fcUserInfo.h"
#import "fcAppDelegate.h"
#import "fcError.h"

@interface LocationListTableCellHolder : NSObject
{
    UIImageView *LeftImage;
    UILabel *MainText;
    UITextView *SmallText;
    UILabel *DistanceText;
    //fcTableCellButton *RemoveButton;
    
}
@property (nonatomic, strong) UIImageView *LeftImage;
@property (nonatomic, strong) UILabel *MainText;
@property (nonatomic, strong) UITextView *SmallText;
@property (nonatomic, strong) UILabel *DistanceText;
//property (nonatomic, strong) fcTableCellButton *RemoveButton;
@end

@implementation LocationListTableCellHolder
@synthesize LeftImage;
@synthesize MainText;
@synthesize SmallText;
@synthesize DistanceText;
//@synthesize RemoveButton;

@end


@interface fcLocationListViewController ()

@end

@implementation fcLocationListViewController

// UI
@synthesize table=_table;
@synthesize refreshButton=_refreshButton;
@synthesize tableCell=_tableCell;
@synthesize refreshButtonGradient=_refreshButtonGradient;
@synthesize conRequest=_conRequest;
@synthesize tableBGView=_tableBGView;
// Data
@synthesize responseData=_responseData;
@synthesize locationsXMLParser=_locationsXMLParser;
@synthesize ListIndex=_ListIndex;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        _responseData = [[NSMutableData alloc] init ];
        _ListIndex = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    
    UIColor *background = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
    
    // UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@"Order" style:UIBarButtonItemStyleBordered target:self action:nil];
    // [self.navigationItem setBackBarButtonItem: backButton];
    
    NSString *backText = @"Location";
    
    if([[myCommonAppDelegate UserInfo] isLocationSet]!=TRUE)
    {
        backText = @"Order";
    }
    //UIButton *infoButton = [UIButton buttonWithType:UIButtonTypeInfoLight];
    UIBarButtonItem *barButton = [[UIBarButtonItem alloc]
                                  initWithTitle:backText
                                  style:UIBarButtonItemStyleBordered
                                  target:self
                                  action:@selector(doBack)];
    
    [self.navigationItem setLeftBarButtonItem: barButton];
    
    UIBarButtonItem *refreshButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(doGetLocationsList)];
    self.navigationItem.rightBarButtonItem = refreshButton;

    
    
        // Gross ios6 Hack ?
    [myCommonAppDelegate prepareButtonForGradient:self.refreshButton];
    
    
        
    self.refreshButtonGradient =  [myCommonAppDelegate makeGreenGradient];
    self.refreshButtonGradient.frame = self.refreshButton.bounds;
    [self.refreshButton.layer insertSublayer:self.refreshButtonGradient atIndex:0];
    

    self.title = @"Locations";
    /*
    self.ItemsList.backgroundColor = [UIColor clearColor];
    self.ItemsList.opaque = NO;
    self.ItemsList.backgroundView = nil;
     */
    
    [ [myCommonAppDelegate navController] setNavigationBarHidden:FALSE];
    
    //self.ListIndex = [[NSMutableArray alloc] init];
    /*
     if (!ItemsList.tableFooterView)
     {
     [self AddItemsListFooter ];
     }
     */
    self.table.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    self.tableBGView.layer.cornerRadius= DEFAULT_CORNER_RADIUS;
    [self doGetLocationsList];
    
}

- (IBAction) doRefresh:(id)sender
{
     [self doGetLocationsList];
}

- (void) createListIndex
{
    [self.ListIndex removeAllObjects];
    
    for(NSNumber *aKey in [ [myCommonAppDelegate locationData] LocationData] )
    {
        if([fcLocation isLocationIDNone:aKey])
        {
            continue;
        }
        
        fcLocation *location = [[myCommonAppDelegate locationData] getLocation:aKey];
        fcLocationsListIndexData *item = [[fcLocationsListIndexData alloc] init];
        if([location LocationIsActive]==TRUE)
        {
            if( ([location getCountOfArrivalMethods]==0) || ([location getCountOfPaymentMethods]==0))
            {
                continue; // No way to pay or arrive !
            }
            item.locationID = aKey;
            [self.ListIndex addObject:item];
        }
    }
    
    [self.table reloadData];
}

- (IBAction) doBack
{
    
    if([[myCommonAppDelegate UserInfo] isLocationSet]==TRUE)
    {
        [self.navigationController popViewControllerAnimated:NO];
        
    }
    else
    {
        [self goToRootViewController];
    }
}

- (void) goToRootViewController
{
    NSArray *array = [self.navigationController viewControllers];
    fcItemsListViewController *orderPage = (fcItemsListViewController*)[array objectAtIndex:1];
    if([orderPage respondsToSelector:@selector(childFinishedCommit)])
    {
        [orderPage childFinishedCommit];
    }
    [self.navigationController popToViewController:[array objectAtIndex:1] animated:NO];
    
}

- (UITableViewCell*)tableView:(UITableView*)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    static NSString *CellIdentifier = @"LocationListCell"; // MUST Match the NIB !!!
    
    
    fcLocationsListIndexData *Item = [self.ListIndex objectAtIndex:indexPath.row];
    if(nil==Item)
    {
        NSString *Log = [NSString stringWithFormat:@"LocationsList: UpdateTableRowFromHolder Item: %i, Not found In Index (bad)",indexPath.row];
        FC_ALog(@"%@",Log);
        return nil;
    }
    
    fcLocation *location = [[myCommonAppDelegate locationData] getLocation:Item.locationID];
    
    UITableViewCell *Cell = nil;
    Cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if(nil==Cell)
    {
        // Got to load one
        UINib *theCellNib = [UINib nibWithNibName:@"fcLocationListViewTableCell" bundle:nil];
        [theCellNib instantiateWithOwner:self options:nil];
        Cell = [self tableCell];
        
    }
    else
    {
        // Test to make sure reuse happens sometimes at least.
    }
    
    
    LocationListTableCellHolder *Holder = [[LocationListTableCellHolder alloc] init];
    
    Holder.LeftImage = (UIImageView*)[Cell viewWithTag:1];
    Holder.MainText = (UILabel*) [Cell viewWithTag:2];
    Holder.SmallText= (UITextView*)[Cell viewWithTag:3];
    Holder.DistanceText=(UILabel*)[Cell viewWithTag:4];
    //Holder.RemoveButton=(fcTableCellButton*)[Cell viewWithTag:5];
    
    [Holder.LeftImage setImage:[UIImage imageNamed:LOCATION_TABLE_ROW_IMAGE_NAME] ];
    if(location==nil)
    {
        [Holder.MainText setText:NONE_TEXT];
        [Holder.SmallText setText:@""];
        [Holder.DistanceText setText:@""];
        
    }
    else
    {
        [Holder.MainText setText: location.LocationDescription];
        [Holder.SmallText setText:location.LocationAddress];
        [Holder.DistanceText setText:@""];
    }
    
    return Cell;
}

- (void) doSetUserLocation:(fcLocation*)location
{
    if(location==nil)
    {
        return;
    }
    self.locationsXMLParser = [[fcXMLParserLocations alloc] init];
    
    self.locationsXMLParser.parseType = XML_PARSER_LOCATIONS_PARSE_TYPE_SET_USER_LOCATION;
    
    
    NSString *myRequestString = [NSString stringWithFormat:@"%@=%@&%@=%@",
                                 USER_COMMAND_CMD,SET_USER_LOCATION_CMD,
                                 LOCATION_ID_ATTR,location.LocationID
                                 ];
    
    NSData *myRequestData = [ NSData dataWithBytes: [ myRequestString UTF8String ] length: [ myRequestString length ] ];
    
    NSString *URLString = [NSString stringWithFormat:@"%@%@",
                           BASE_URL,LOCATIONS_PAGE];
    
    NSMutableURLRequest *request = [ [ NSMutableURLRequest alloc ] initWithURL: [ NSURL URLWithString:  URLString] ];
    [ request setHTTPMethod: @"POST" ];
    [ request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"content-type"];
    [ request setHTTPBody: myRequestData ];
    [ request setTimeoutInterval:[myCommonAppDelegate getRequestShortTimeout]];
    
    self.conRequest=[[NSURLConnection alloc] initWithRequest:request delegate:self] ;
    
    HUD = [MBProgressHUD showHUDAddedTo:self.view animated:NO] ;
    HUD.dimBackground = YES;
    HUD.labelText=PROGRESS_SET_USER_LOCATION_MESSAGE;
    HUD.delegate=self;

}

- (void) tableView: (UITableView*)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    fcLocationsListIndexData *Item = [self.ListIndex objectAtIndex:indexPath.row];
    if(nil==Item)
    {
        NSString *Log = [NSString stringWithFormat:@"LocationsList: UpdateTableRowFromHolder Item: %i, Not found In Index (bad)",indexPath.row];
        FC_ALog(@"%@",Log);
        return;
    }
    
    
    fcLocation *location = [[myCommonAppDelegate locationData] getLocation:Item.locationID];
    if(location!=nil)
    {
        [self doSetUserLocation:location];
    }
    
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.table deselectRowAtIndexPath:[self.table indexPathForSelectedRow] animated:YES];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSInteger) numberOfSectionsInTableView:(UITableView*)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView*)tableView numberOfRowsInSection:(NSInteger)section
{
    
    return [self.ListIndex count];
}
/*
- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    CGFloat optionsHeight = [self getOptionsHeightForRowAtIndexPath:indexPath];
    CGRect f = cell.frame;
    f.size.height = optionsHeight+40.0;
    //cell.frame =f;
    UILabel* optionsLabel = (UILabel*)[cell viewWithTag:3];
    
    if(nil!=optionsLabel)
    {
        CGRect optionsRect = optionsLabel.frame;
        optionsRect.size.height=optionsHeight;
        [cell viewWithTag:3].frame=optionsRect;
        //[cell viewWithTag:3]
    }
}
*/
- (void) doGetLocationsList
{
    
    self.locationsXMLParser = [[fcXMLParserLocations alloc] init];
    
    self.locationsXMLParser.parseType = XML_PARSER_LOCATIONS_PARSE_TYPE_GET_LOCATIONS;
    
    
    NSString *myRequestString = [NSString stringWithFormat:@"%@=%@",
                                 USER_COMMAND_CMD,GET_ALL_LOCATIONS_CMD
                                 ];
    
    NSData *myRequestData = [ NSData dataWithBytes: [ myRequestString UTF8String ] length: [ myRequestString length ] ];
    
    NSString *URLString = [NSString stringWithFormat:@"%@%@",BASE_URL,LOCATIONS_PAGE];
    
    NSMutableURLRequest *request = [ [ NSMutableURLRequest alloc ] initWithURL: [ NSURL URLWithString:  URLString] ];
    [ request setHTTPMethod: @"POST" ];
    [ request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"content-type"];
    [ request setHTTPBody: myRequestData ];
    [ request setTimeoutInterval:[myCommonAppDelegate getRequestShortTimeout]];
    
    self.conRequest=[[NSURLConnection alloc] initWithRequest:request delegate:self] ;
    
    HUD = [MBProgressHUD showHUDAddedTo:self.view animated:NO] ;
    HUD.dimBackground = YES;
    HUD.labelText=PROGRESS_DOWNLOAD_LOCATIONS_MESSAGE;
    HUD.delegate=self;
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    [self.responseData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    [self.responseData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    [HUD hide:YES afterDelay:HUD_HIDE_DELAY_DEFAULT];
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // Once this method is invoked, "responseData" contains the complete result
    [HUD hide:YES afterDelay:HUD_HIDE_DELAY_DEFAULT];

    [self.table deselectRowAtIndexPath:[self.table indexPathForSelectedRow] animated:YES];
    self.conRequest=nil;
    
#ifdef FC_DO_LOG
    NSString* newStr = [[NSString alloc] initWithData:self.responseData
                                    encoding:NSUTF8StringEncoding];
    FC_Log(@"LocationsXML:%@",newStr);
#endif
    
    NSXMLParser *parser = [[NSXMLParser alloc] initWithData:self.responseData];
    [parser setDelegate: self.locationsXMLParser];
    
    [parser setShouldProcessNamespaces:YES];
    
        
    if([parser parse]!=YES)
    {
        NSString *message = [NSString stringWithFormat:@"%@: %@",XML_PARSE_ALERT_MESSAGE,[parser parserError] ];
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: XML_PARSE_ALERT_TITLE
                                   message: message
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
        self.locationsXMLParser=nil;
        return;
    }
    
    if(self.locationsXMLParser.signonError==TRUE)
    {
        [[myCommonAppDelegate navController] popToRootViewControllerAnimated:NO];
		return;
        
    }
    if(self.locationsXMLParser.parseSuccessful==TRUE)
    {
        if(self.locationsXMLParser.parseType==XML_PARSER_LOCATIONS_PARSE_TYPE_GET_LOCATIONS)
        {
            [[myCommonAppDelegate locationData] clear];
            [[myCommonAppDelegate locationData] setLocationTable:self.locationsXMLParser.locations];
            [self createListIndex];
        }
        else if(self.locationsXMLParser.parseType==XML_PARSER_LOCATIONS_PARSE_TYPE_SET_USER_LOCATION)
        {
            if(self.locationsXMLParser.responseSuccess==TRUE)
            {
                if( [fcLocation isLocationIDNone:self.locationsXMLParser.updatedLocationID]!=TRUE)
                {
                    [[myCommonAppDelegate UserInfo] setUserLocationID:self.locationsXMLParser.updatedLocationID];
                    [myCommonAppDelegate generateCurrentDefaultOrderIfEmpty];
                    
                    // Last order is sent each time we change location! Gross
                    [myCommonAppDelegate updateLastOrderStatus:self.locationsXMLParser.lastOrder];
                    
                    [self goToRootViewController];
                }
            }
        }
        
    }
    else
    {
        [self.table deselectRowAtIndexPath:[self.table indexPathForSelectedRow] animated:YES];
        NSString *messageTitle=nil;
        NSString *message=nil;
        if(self.locationsXMLParser.parseType==XML_PARSER_LOCATIONS_PARSE_TYPE_GET_LOCATIONS)
        {
            messageTitle=DOWNLOAD_LOCATIONS_ALERT_TITLE;
            message = [self.locationsXMLParser.error makeErrorText];
        }
        else if(self.locationsXMLParser.parseType==XML_PARSER_LOCATIONS_PARSE_TYPE_SET_USER_LOCATION)
        {
            messageTitle=SET_USER_LOCATION_ALERT_TITLE;
            message = [self.locationsXMLParser.error makeErrorText];
        }
        else
        {
            messageTitle=UNKNOWN_ALERT_TITLE;
            message=UNKNOWN_ALERT_MESSAGE;
        }

        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: messageTitle
                                   message: message
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
        self.locationsXMLParser=nil;
        return;

    }
    
    self.locationsXMLParser=nil;
}


- (CGFloat) getOptionsHeightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 75.0;
    /*
    if([self.ListIndex count]==0)
    {
        return ITEMS_LIST_DEFAULT_ROW_HEIGHT;
    }
    ItemListIndexData *listItem = [self.ListIndex objectAtIndex:indexPath.row];
    
    
    if(nil==listItem)
    {
        return 50.0; // YUCK
    }
    NSNumber *userItemId = [NSNumber numberWithInt:listItem.ItemID];
    fcUserItem *userItem = [[myCommonAppDelegate userItems ] getUserItemForID:userItemId];
    
    if(nil==userItem)
    {
        return 50.0; // YUCK
    }
    
    NSMutableString *itemOptions = [NSMutableString stringWithString:userItem.userItemOptionsText];
    
    
    UIFont *font = [UIFont fontWithName:@"Helvetica" size:10.0];
    
    CGSize optionsSize = [itemOptions sizeWithFont:font constrainedToSize:CGSizeMake(160,4000)];
    if(optionsSize.height>50.0)
    {
        return 50.0;
    }
    return optionsSize.height;
     */
}

-(CGFloat) tableView:(UITableView*)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 75.0;
    /*
    if([self.ListIndex count]==0)
    {
        return ITEMS_LIST_DEFAULT_ROW_HEIGHT;
    }
    
    ItemListIndexData *Item = [self.ListIndex objectAtIndex:indexPath.row];
    if(nil==Item)
    {
        NSString *Log = [NSString stringWithFormat:@"ItemsList: heightForRowAtIndexPath Item: %i, Not found In Index (bad)",indexPath.row];
        FC_Log(@"%@",Log);
        return ITEMS_LIST_DEFAULT_ROW_HEIGHT;
    }
    
    if(Item.ItemType!=ITEM_LIST_USER_DRINK_TYPE)
    {
        return ITEMS_LIST_DEFAULT_ROW_HEIGHT;
    }
    
    
    
    CGFloat optionsHeight = [self getOptionsHeightForRowAtIndexPath:indexPath];
    
    return optionsHeight+40.0;
*/
  
}


@end
