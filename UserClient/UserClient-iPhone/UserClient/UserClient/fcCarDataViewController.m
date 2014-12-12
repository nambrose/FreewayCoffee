//
//  fcCarDataViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <QuartzCore/QuartzCore.h>

#import "fcCarDataViewController.h"
#import "fcItemsListViewController.h"
#import "CarColor.h"
#import "CarModel.h"
#import "CarMake.h"
#import "CarMakeModelColorData.h"
#import "fcAppDelegate.h"
#import "fcRootViewController.h"
#import "fcUserInfo.h"

#define CAR_DATA_LIST_DEFAULT_ROW_HEIGHT 48
@interface fcCarDataViewController ()

@end

@implementation fcCarDataViewController
//@synthesize WelcomeUserLabel;

@synthesize ListIndex;
//@synthesize TableFooter;
@synthesize m_UserTag;
@synthesize delegate=_delegate;
@synthesize ItemsList=_itemsList;
@synthesize walkupCheck=_walkupCheck;
//@synthesize tableHeader=_tableHeader;
@synthesize checkView=_checkView;
@synthesize signonError=_signonError;
@synthesize conRequest=_conRequest;
@synthesize actionButton = _actionButton;
@synthesize tableFooter=_tableFooter;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        _signonError=FALSE;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    CarDataSetResultOK=FALSE;
    UpdatingCarData=FALSE;
    responseData = [[NSMutableData alloc] init ];
    
    
    UIColor *background = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
    
    
    self.ItemsList.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    
    ListIndex = [[NSMutableArray alloc] init];
    
    /*
    if (!self.ItemsList.tableFooterView)
    {
        [self AddFooter ];
    }
    */
    for(UIView* subView in self.actionButton.subviews)
        if([subView isKindOfClass:NSClassFromString(@"UIGroupTableViewCellBackground")])
            [subView setHidden:YES];
    
    CAGradientLayer *actionGrad =  [myCommonAppDelegate makeGreenGradient];
    actionGrad.frame = self.actionButton.bounds;
    [self.actionButton.layer insertSublayer:actionGrad atIndex:0];

    
    self.checkView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    
    UIBarButtonItem *anotherButton = [[UIBarButtonItem alloc] initWithTitle:@"Update" style:UIBarButtonItemStylePlain
                                                                     target:self action:@selector(doUpdateCarData:)];
    self.navigationItem.rightBarButtonItem = anotherButton;

    self.walkupCheck.text=@"Walkup";
    self.walkupCheck.checked = [myCommonAppDelegate IsUserWalkup];
    
    
    if([myCommonAppDelegate isRetina4Display])
    {
        self.ItemsList.frame = CGRectMake(0, 75, 320, 200);
    }
    else
    {
        self.ItemsList.frame = CGRectMake(0, 100, 320, 180);
    }
    
    NSString *UserCarMakeID = [[myCommonAppDelegate UserCarInfo] valueForKey:USER_CAR_MAKE_ID_ATTR] ;
    NSString *UserCarModelID = [[myCommonAppDelegate UserCarInfo] valueForKey:USER_CAR_MODEL_ID_ATTR] ;
    NSString *UserCarColorID = [[myCommonAppDelegate UserCarInfo] valueForKey:USER_CAR_COLOR_ID_ATTR] ;
    self.m_UserTag = [[myCommonAppDelegate UserInfo] userTag];
    
    
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@"Transport" style:UIBarButtonItemStyleBordered target:self action:nil];
    [self.navigationItem setBackBarButtonItem: backButton];
    
    MakeID = [UserCarMakeID intValue];
    ModelID = [UserCarModelID intValue];
    ColorID = [UserCarColorID intValue];
    if ( ![myCommonAppDelegate IsCarDataDownloaded])
    {
        self.title = @"Loading Cars";
        [self doDownloadCarData];
    }
    else
    {
        // Just display it
        [self CreateListIndex];
        [self.ItemsList reloadData];
    }
    

}
- (void) doDownloadCarData
{
    if([myCommonAppDelegate networkUp]!=TRUE)
    {
        [[myCommonAppDelegate topController] showNetworkDownToast:self.view];
        [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
        return;
    }

    self.signonError=FALSE;
    NSString *URLString = [NSString stringWithFormat:@"%@%@?%@=%@",BASE_URL,CAR_DATA_PAGE,CAR_PAGE_COMMAND_STRING,GET_CAR_DATA_CMD];
    
    NSURLRequest *theRequest=[NSURLRequest requestWithURL:[NSURL URLWithString:URLString]
                              
                                              cachePolicy:NSURLRequestUseProtocolCachePolicy
                              
                                          timeoutInterval:60.0];
    self.conRequest=[[NSURLConnection alloc] initWithRequest:theRequest delegate:self];
    
    HUD = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    HUD.dimBackground = YES;
    HUD.labelText=PROGRESS_DOWNLOAD_CAR_DATA_MESSAGE;
    HUD.delegate=self;
}


- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    FC_Log(@"%@",elementName);
    
    if( [elementName isEqualToString:CAR_MAKE_TAG])
    {
        CarMake *make = [self parseCarMake:attributeDict];
        if(make!=NULL)
        {
            [[myCommonAppDelegate CarData] AddCarMake:make forID:[make MakeID]];
        }
    }
    else if ([elementName isEqualToString:CAR_MODEL_TAG])
    {
        [self parseCarModel:attributeDict];
        
    }
    else if([elementName isEqualToString:CAR_COLOR_TAG])
    {
        [self parseCarColor:attributeDict];
    }
    else if([elementName isEqualToString:USER_CAR_DATA_TAG])
    {
        [myCommonAppDelegate setUserCarInfo:[XMLHelper convertStringsFromWeb:attributeDict]  ];
        CarDataSetResultOK=TRUE;
    }
    else if([elementName isEqualToString:USER_INFO_USER_TAG])
    {
        NSString *Tag = [attributeDict valueForKey:USER_INFO_USER_TAG];
        if(Tag!=nil)
        {
            [[myCommonAppDelegate UserInfo] setUserTag:Tag];
        }
    }
    else if([elementName isEqualToString:SIGNON_RESPONSE_TAG])
    {
        NSString *attrValue = [NSString stringWithString:[attributeDict valueForKey:RESULT_ATTR] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        if([attrValue isEqualToString:OK_ATTR_VALUE]!=TRUE)
        {
            self.signonError=TRUE;
        }
    }

    
}

- (void) parseCarColor: (NSDictionary *)attributes
{
    CarColor *color = [[CarColor alloc] init];
    
    for(NSString *aKey in [ attributes allKeys] )
    {
        // do something
        NSString *attrValue = [NSString stringWithString:[attributes valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"Car Model: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:CAR_COLOR_ID_ATTR])
        {
            [color setCarColorID:[XMLHelper parseStringToInt:attrValue]];
            
        }
        else if([aKey isEqualToString:CAR_COLOR_LONG_DESCR_ATTR])
        {
            [color setCarColorLongDescr:attrValue];
        }
        if([aKey isEqualToString:CAR_COLOR_SHORT_DESCR_ATTR])
        {
            [color setCarColorShortDescr:attrValue];
        }
        
        if([aKey isEqualToString:SORT_ORDER_SHORT_ATTR])
        {
            
            [color setSortOrder:[XMLHelper parseStringToInt:attrValue]];
            
        }
    }
    
    
    [[ myCommonAppDelegate CarData] AddCarColor:color forID: [color CarColorID]];
    
}

- (void) parseCarModel: (NSDictionary *)attributes
{
    CarModel *model = [[CarModel alloc] init];
    
    for(NSString *aKey in [ attributes allKeys] )
    {
        // do something
        NSString *attrValue = [NSString stringWithString:[attributes valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"Car Model: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:CAR_MODEL_ID_ATTR])
        {
            [model setModelID:[XMLHelper parseStringToInt:attrValue]];
            
        }
        else if([aKey isEqualToString:CAR_MODEL_MAKE_ID_ATTR])
        {
            [model setMakeID:[XMLHelper parseStringToInt:attrValue]];
            
        }
        if([aKey isEqualToString:CAR_MODEL_LONG_DESCR])
        {
            [model setModelLongDescr:attrValue];
            
        }
        if([aKey isEqualToString:CAR_MODEL_SHORT_DESCR])
        {
            [model setModelShortDescr:attrValue];
            
        }
        if([aKey isEqualToString:SORT_ORDER_SHORT_ATTR])
        {
            [model setSortOrder:[XMLHelper parseStringToInt:attrValue]];
        }
    }
    
    CarMake *make = [[  myCommonAppDelegate CarData] GetCarMake:[model MakeID]];
    
    if(make!=NULL)
    {
        [make AddModel:model];
    }
    
    
}
- (CarMake*) parseCarMake: (NSDictionary *)attributes
{
    //NSMutableDictionary *newAttrs = [attributes mutableCopy];
    
    CarMake *make = [[CarMake alloc] init];
    
    for(NSString *aKey in [ attributes allKeys] )
    {
        // do something
        NSString *attrValue = [NSString stringWithString:[attributes valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"Car Make: Attr: %@ Value: %@",aKey,attrValue);
        
        if([aKey isEqualToString:CAR_MAKE_ID_ATTR])
        {
            [make setMakeID:[XMLHelper parseStringToInt:attrValue]];
            
        }
        else if([aKey isEqualToString:CAR_MAKE_LONG_DESCR_ATTR])
        {
            [make setMakeLongDescr:attrValue];
        }
        else if([aKey isEqualToString:CAR_MAKE_SHORT_DESCR_ATTR])
        {
            [make setMakeShortDescr:attrValue];
        }
        else if([aKey isEqualToString:CAR_MAKE_CAN_HAVE_MODELS])
        {
            if ([XMLHelper parseBoolFromString:attrValue] == TRUE)
            {
                [make setCanHaveModels:TRUE];
            }
            else
            {
                [make setCanHaveModels:FALSE];
            }
        }
        else if([aKey isEqualToString:SORT_ORDER_SHORT_ATTR])
        {
            [make setSortOrder:[XMLHelper parseStringToInt:attrValue]];
            
        }
        
        
        //[attrValue release];
        
    }
    return make;
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
    // FC_Log(elementName);
}
- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
    
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    [responseData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    [responseData appendData:data];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.ItemsList deselectRowAtIndexPath:[self.ItemsList indexPathForSelectedRow] animated:YES];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    //[[NSAlert alertWithError:error] runModal];
    
    [HUD hide:YES afterDelay:HUD_HIDE_DELAY_DEFAULT];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    //FC_Log(@"Finish");
    
    // Once this method is invoked, "responseData" contains the complete result
    [HUD hide:YES afterDelay:HUD_HIDE_DELAY_DEFAULT];
    
    NSXMLParser *parser = [[NSXMLParser alloc] initWithData:responseData];
    [parser setDelegate: self];
    
    [parser setShouldProcessNamespaces:YES];
    
#ifdef FC_DO_LOG
     NSString* newStr = [[NSString alloc] initWithData:responseData
     encoding:NSUTF8StringEncoding];
     FC_Log(@"%@",newStr);
#endif
    
    if([parser parse]!=YES)
    {
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: NETWORK_ERROR_ALERT_TITLE
                                   message: NETWORK_ERROR_ALERT_MESSAGE
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
        
        return;
    }
    if(self.signonError==TRUE)
    {
        [[myCommonAppDelegate navController] popToRootViewControllerAnimated:NO];
        return;
        
    }
    if(UpdatingCarData==TRUE)
    {
        if(CarDataSetResultOK==TRUE)
        {
            if([self.delegate respondsToSelector:@selector(childFinished)])
            {
                [self.delegate childFinished];
            }
            [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
            return;
        }
        else
        {
            UIAlertView *alert =
            [[UIAlertView alloc] initWithTitle: UPDATE_CAR_DATA_FAILED_ALERT_TITLE
                                       message: UPDATE_CAR_DATA_FAILED_ALERT_MESSAGE
                                      delegate: self
                             cancelButtonTitle: @"OK"
                             otherButtonTitles: nil];
            [alert show];
            
        }
    }
    [self CreateListIndex];
    [self.ItemsList reloadData];
    
}

- (void) AddFooter
{
    
    [[NSBundle mainBundle] loadNibNamed:@"fcCarDataViewControllerFooter" owner:self options:nil];
    
    
     for(UIView* subView in self.actionButton.subviews)
     if([subView isKindOfClass:NSClassFromString(@"UIGroupTableViewCellBackground")])
     [subView setHidden:YES];
     
     CAGradientLayer *actionGrad =  [myCommonAppDelegate makeGreenGradient];
     actionGrad.frame = self.actionButton.bounds;
     [self.actionButton.layer insertSublayer:actionGrad atIndex:0];
     
    
    self.ItemsList.tableFooterView = self.tableFooter;
    self.ItemsList.tableFooterView.userInteractionEnabled = YES;
}


- (NSInteger) numberOfSectionsInTableView:(UITableView*)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView*)tableView numberOfRowsInSection:(NSInteger)section
{
    return [ListIndex count];
}

/*
 -(CGFloat) tableView:(UITableView*)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
 {
 
 ItemListIndexData *Item = [ListIndex objectAtIndex:indexPath.row];
 if(nil==Item)
 {
 NSString *Log = [NSString stringWithFormat:@"ItemsList: heightForRowAtIndexPath Item: %i, Not found In Index (bad)",indexPath.row];
 FC_Log(Log);
 return ITEMS_LIST_DEFAULT_ROW_HEIGHT;
 }
 
 if(Item.ItemType==ITEM_LIST_USER_DRINK_TYPE)
 {
 return ITEMS_LIST_DRINK_ROW_HEIGHT;
 }
 return ITEMS_LIST_DEFAULT_ROW_HEIGHT;
 
 return 20.0;
 }
 */

- (UITableViewCell*)tableView:(UITableView*)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CarCellID = @"CarTableCell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CarCellID];
    if(cell==nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                      reuseIdentifier:CarCellID] ;
    }
    
    cell.textLabel.lineBreakMode = NSLineBreakByWordWrapping;
    cell.textLabel.numberOfLines = 0;

    CarDataIndexItem *listItem = [ListIndex objectAtIndex:indexPath.row];
    
    [self updateCell:cell withItem:listItem andIndex: indexPath.row];
    
    
    return cell;
    
}

- (void) updateCell:(UITableViewCell*)cell withItem: (CarDataIndexItem*)item andIndex:(int)index
{
    if(cell==nil || item==nil)
    {
        return;
    }
    
    BOOL showMoreArrow=FALSE;
    NSString *CellText=nil;
    NSString *MainText=nil;
    CarMake *make=nil;
    CarModel *model=nil;
    switch( [item ItemType])
    {
        case CAR_DATA_ITEM_TYPE_MAKE:
            if (MakeID!=CAR_MAKE_NONE_ID)
            {
                make = [[myCommonAppDelegate CarData] GetCarMake:MakeID];
                if(nil!=make)
                {
                    MainText =[make MakeLongDescr];
                }
                else
                {
                    MainText=NONE_TEXT;
                }
                
            }
            else
            {
                MainText=SELECT_A_MAKE_TEXT;
            }
            CellText = [NSString stringWithFormat:@"%@: %@",CAR_MAKE_TEXT,MainText];
            if( [[myCommonAppDelegate CarData] GetCarMakeCount] >0)
            {
                showMoreArrow=TRUE;
            }
            else
            {
                showMoreArrow=FALSE;
            }
            break;
        case CAR_DATA_ITEM_TYPE_MODEL:
            
            if(MakeID==CAR_MAKE_NONE_ID)
            {
                showMoreArrow=FALSE;
                MainText = FIRST_SELECT_A_MAKE_TEXT;
                CellText = [NSString stringWithFormat:@"%@: %@",CAR_MODEL_TEXT,MainText];
                break;
            }
            
            make = [[myCommonAppDelegate CarData] GetCarMake:MakeID];
            
            if(make==nil)
            {
                MainText = NONE_TEXT;
                showMoreArrow=FALSE;
                CellText = [NSString stringWithFormat:@"%@: %@",CAR_MODEL_TEXT,MainText];
                break;
            }
            
            showMoreArrow = [make CanHaveModels];
            
            if( [make CanHaveModels]!=TRUE)
            {
                showMoreArrow=FALSE;
                MainText = MAKE_CANNOT_HAVE_MODELS;
                CellText = [NSString stringWithFormat:@"%@: %@",CAR_MODEL_TEXT,MainText];
                break;
            }
            
            
            model = [make GetModel:ModelID];
            if(nil!=model)
            {
                MainText = [model ModelLongDescr];
            }
            else
            {
                MainText = NONE_TEXT;
            }
            
            CellText = [NSString stringWithFormat:@"%@: %@",CAR_MODEL_TEXT,MainText];
            
            
            break;
        case CAR_DATA_ITEM_TYPE_COLOR:
            if (ColorID==0)
            {
                MainText = SELECT_A_COLOR_TEXT;
            }
            else
            {
                CarColor *color= [[myCommonAppDelegate CarData] GetCarColor:ColorID];
                if(nil!=color)
                {
                    MainText = [color CarColorLongDescr];
                }
                else
                {
                    MainText = SELECT_A_COLOR_TEXT;
                }
            }
            CellText = [NSString stringWithFormat:@"%@: %@",CAR_COLOR_TEXT,MainText];
            if( [[myCommonAppDelegate CarData] GetCarColorCount] >0)
            {
                showMoreArrow=TRUE;
            }
            else
            {
                showMoreArrow=FALSE;
            }
            
            break;
        case CAR_DATA_ITEM_TYPE_TAG:
            
            
            if(self.m_UserTag!=nil)
            {
                //FC_Log(@"%@",self.m_UserTag);
            }
            MainText = self.m_UserTag;
            if(MainText==nil || ([MainText isEqualToString:@""]))
            {
                MainText = ENTER_A_LICENSE_TEXT;
                
            }
            CellText = [NSString stringWithFormat:@"%@: %@",CAR_LICENSE_TEXT,MainText];
            
            showMoreArrow=FALSE;
            
            break;
        default:
            CellText=NONE_TEXT;
            showMoreArrow=FALSE;
            break;
    }
    cell.textLabel.text=CellText;
    if(showMoreArrow)
    {
        cell.accessoryType=UITableViewCellAccessoryDisclosureIndicator;
    }
    else
    {
        cell.accessoryType=UITableViewCellAccessoryNone;
    }
}

- (void) tableView: (UITableView*)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    CarDataIndexItem *Item = [ListIndex objectAtIndex:indexPath.row];
    if(nil==Item)
    {
        
        NSString *Log = [NSString stringWithFormat:@"CarData: didSelectRowAtIndexPath Item: %i, Not found In Index (bad)",indexPath.row];
        FC_ALog(@"%@",Log);
        return ;
    }
    
    // Pretty Ugly. If we want to select a Model, we need the makeID to narrow it down
    
    
    if( ([Item ItemType]==CAR_DATA_ITEM_TYPE_MAKE) || ([Item ItemType] == CAR_DATA_ITEM_TYPE_MODEL) || ([Item ItemType] == CAR_DATA_ITEM_TYPE_COLOR))
    {
        // Let them pick their car Info !!
        
        if( [Item ItemType] == CAR_DATA_ITEM_TYPE_MODEL)
        {
            
            if(MakeID == CAR_MAKE_NONE_ID)
            {
                return; // Dont let them pick models before a Make is selected!
            }
            // Got to check Can Has Models too
            CarMake *make = [[myCommonAppDelegate CarData] GetCarMake:MakeID];
            if(nil!=make)
            {
                if([make CanHaveModels]!=TRUE)
                {
                    return;
                }
            }
            
        }
        BOOL popTwo=FALSE;
        if([Item ItemType]==CAR_DATA_ITEM_TYPE_MAKE)
        {
            popTwo=TRUE;
        }
        fcCarDataValuePickerViewController *CarView =
        
        [[ fcCarDataValuePickerViewController alloc] initWithNibName:@"fcCarDataValuePickerViewController"
                                                       andPickType:Item.ItemType andMakeID:MakeID andPopTwo:popTwo];
        CarView.delegate=self;
        
        [ [myCommonAppDelegate navController] pushViewController:CarView animated:NO];
    }
    else if([Item ItemType] == CAR_DATA_ITEM_TYPE_TAG)
    {
        fcCarDataLicenseViewController *LicenseView =
        
        [[ fcCarDataLicenseViewController alloc] initWithNibName:@"fcCarDataLicenseViewController" andText:self.m_UserTag];
        LicenseView.delegate=self;
        
        [ [myCommonAppDelegate navController] pushViewController:LicenseView animated:NO];
        
    }
}

- (IBAction)walkupChecked:(id)sender
{
    if(self.walkupCheck.checked==TRUE)
    {
        self.ItemsList.hidden=YES;
        
        [myCommonAppDelegate applyUserArriveMode:ARRIVE_MODE_WALKUP_STR];
    }
    else
    {
        self.ItemsList.hidden=NO;
        [myCommonAppDelegate applyUserArriveMode:ARRIVE_MODE_CAR_STR];
    }
    [self CreateListIndex];
     
}
- (void) CreateListIndex
{
    [ListIndex removeAllObjects];
    if([myCommonAppDelegate IsUserWalkup]==TRUE)
    {
        [self.ItemsList reloadData];
        self.ItemsList.hidden=YES;
        return;
    }
    self.ItemsList.hidden=NO;
    CarDataIndexItem *ListItem  = nil;
    
    
    
    // Make
    ListItem  = [[CarDataIndexItem alloc] init];
    ListItem.ItemType=CAR_DATA_ITEM_TYPE_MAKE;
    //ListItem.ItemID= m_MakeID;
    
    [ListIndex addObject:ListItem];
       
    // Model
    ListItem  = [[CarDataIndexItem alloc] init];
    ListItem.ItemType = CAR_DATA_ITEM_TYPE_MODEL;
    //ListItem.ItemID = m_ModelID;
    
    [ListIndex addObject:ListItem];
   
    
    // Color
    ListItem  = [[CarDataIndexItem alloc] init];
    ListItem.ItemType = CAR_DATA_ITEM_TYPE_COLOR;
    //ListItem.ItemID= m_ColorID;
    
    [ListIndex addObject:ListItem];
    
    
    // Tag (license)
    
    ListItem  = [[CarDataIndexItem alloc] init];
    ListItem.ItemType = CAR_DATA_ITEM_TYPE_TAG;
    //ListItem.ItemID=-1;
    [ListIndex addObject:ListItem];
    
    //NSString *WelcomeString = [NSString stringWithFormat:@"%@%@",[myCommonAppDelegate getName], @", Set Your Vehicle"];
    NSString *WelcomeString = [NSString stringWithFormat:@"%@",@"Arrival Mode"];
    self.title = WelcomeString;
    [self.ItemsList reloadData];
}

- (IBAction) doBack: (id) sender
{
    if([self.delegate respondsToSelector:@selector(childFinished)])
    {
        [self.delegate childFinished];
    }
    
    [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];

}
- (IBAction) doUpdateCarData: (id) sender
{
    if([myCommonAppDelegate networkUp]!=TRUE)
    {
        [[myCommonAppDelegate topController] showNetworkDownToast:self.view];
        
        return;
    }
    self.signonError=FALSE;
    UpdatingCarData=TRUE;
    NSString *arriveMode = nil;
    
    if([self.walkupCheck checked])
    {
        arriveMode = ARRIVE_MODE_WALKUP_STR;
    }
    else
    {
        arriveMode = ARRIVE_MODE_CAR_STR;
    }
    
    NSString *URLString = [NSString stringWithFormat:@"%@%@?%@=%@&%@=%@&%@=%@&%@=%d&%@=%d&%@=%d",
                           BASE_URL,USER_PAGE,
                           USER_COMMAND,UPDATE_TAG_AND_CAR_CMD,
                           USER_ARRIVE_MODE_CMD_ARG,arriveMode,
                           USER_TAG,[XMLHelper URLEncodedString:m_UserTag],
                           USER_CAR_MAKE_ID,MakeID,
                           USER_CAR_MODEL_ID,ModelID,
                           USER_CAR_COLOR_ID,ColorID];
    
    FC_Log(@"%@",URLString);
    
    NSURLRequest *theRequest=[NSURLRequest requestWithURL:[NSURL URLWithString:URLString]
                              
                                              cachePolicy:NSURLRequestUseProtocolCachePolicy
                              
                                          timeoutInterval:60.0];
    self.conRequest = [[NSURLConnection alloc] initWithRequest:theRequest delegate:self];
    
    HUD = [MBProgressHUD showHUDAddedTo:self.view animated:NO];
    HUD.dimBackground = YES;
    HUD.labelText=PROGRESS_UPDATE_CAR_DATA_MESSAGE;
    HUD.delegate=self;
    
}

- (void)dealloc
{
    [HUD removeFromSuperview];
       
}
-(void) setMakeID:(int)makeID
{
    MakeID = makeID;
    [self CreateListIndex];
    [self.ItemsList reloadData];
}

-(void) setModelID:(int)modelID
{
    ModelID = modelID;
    [self CreateListIndex];
    [self.ItemsList reloadData];
}

-(void) setColorID:(int)colorID
{
    ColorID=colorID;
    [self CreateListIndex];
    [self.ItemsList reloadData];
}
-(void) setLicenseText:(NSString*)License
{
    //self.m_UserTag = [NSString stringWithString: License];
    self.m_UserTag = License;
    
    [self CreateListIndex];
    [self.ItemsList reloadData];
}
- (void)hudWasHidden:(MBProgressHUD *)hud
{
    
    // Remove HUD from screen when the HUD was hidded
    [HUD removeFromSuperview];
    
	HUD = nil;
    
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

@end
