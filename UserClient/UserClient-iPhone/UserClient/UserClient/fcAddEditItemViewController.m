//
//  fcAddEditItemViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>
#import "fcAddEditItemViewController.h"
#import "fcAppDelegate.h"
#import "fcMenu.h"
#import "fcItemType.h"
#import "fcAddEditItemListIndexDataItem.h"
#import "fcUserItem.h"
#import "fcItemOptionValuePickerViewController.h"
#import "fcItemNotesViewController.h"
#import "ITToastMessage.h"
#import "XMLHelper.h"
#import "fcXMLParserUserItems.h"
#import "fcUserItemTable.h"
#import "XMLParserMenu.h"
#import "fcRootViewController.h"
#import "fcMenu.h"
#import "fcServerObjectResponse.h"
#import "fcMenuTable.h"
#import "fcItemOrderList.h"
#import "UICheckbox.h"

@interface fcAddEditItemViewController ()

@end

@implementation fcAddEditItemViewController

@synthesize tableCell=_tableCell;
@synthesize TableFooter=_tableFooter;
@synthesize TableHeader=_TableHeader;
@synthesize itemTypeLabel=_itemTypeLabel;
//@synthesize itemNameEdit=_itemNameEdit;
@synthesize itemIncludeDefaultCheck=_itemIncludeDefaultCheck;
@synthesize addEditButton=_addEditButton;
@synthesize itemsList=_itemsList;
@synthesize listIndex=_listIndex;
@synthesize isEdit=_isEdit;
@synthesize itemType=_itemType;
@synthesize itemInProgress=_itemInProgress;
@synthesize itemNotes=_itemNotes;
@synthesize HUD=_hud;
@synthesize responseData=_responseData;
@synthesize delegate=_delegate ;
@synthesize popTop=_popTop;
@synthesize downloadingMenu=_downloadingMenu;
@synthesize conRequest=_conRequest;
@synthesize menu=_menu;

// Vars
@synthesize itemTypeID=_itemTypeID;
@synthesize userItemID=_userItemID; // -1 for Add (yeah, uglified!)

- (id) initWithNibName:(NSString *)nibNameOrNil andItemType: (NSNumber*)type andUserItemId:(NSNumber*) userItemID 
        andIsEdit:(BOOL)isEdit andPopTop: (BOOL) popTop
{
    self = [super initWithNibName:nibNameOrNil bundle:nil];
    if (self)
    {
        _listIndex = [[NSMutableArray alloc]init];
        _itemTypeID = type;
        _userItemID=userItemID;
        _itemType=nil;
        _itemInProgress=nil;
        _isEdit=isEdit;
        _responseData = [[NSMutableData alloc]init];
        _popTop = popTop;
        _downloadingMenu=FALSE;
        _menu = [myCommonAppDelegate getMenuForCurrentLocation];
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    
    UIColor *background = [[UIColor alloc] initWithPatternImage:
                           [UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
    
    if (!self.itemsList.tableFooterView)
    {
        [self AddFooter ];
    }
    
    if (!self.itemsList.tableHeaderView)
    {
        [self AddHeader ];
    }
    
    self.itemsList.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    self.itemIncludeDefaultCheck.text =@"Always Include In Order";
    //self.itemIncludeDefaultCheck.textf
    if(self.isEdit==TRUE)
    {
        // Editing
        NSString *WelcomeString = [NSString stringWithFormat:@"%@",
                                   @"Build your drink"];
        self.title=WelcomeString;
        // Action does not work here. If we need it, go here: http://stackoverflow.com/questions/2796438/uibarbuttonitem-target-action-not-working
        /*
         // Make the info button use the standard icon and hook it up to work
         UIButton *infoButton = [UIButton buttonWithType:UIButtonTypeInfoLight];
         UIBarButtonItem *barButton = [[[UIBarButtonItem alloc]
         initWithImage:infoButton.currentImage
         style:UIBarButtonItemStyleBordered
         target:self
         action:@selector(showInfo:)] autorelease];

         or:
         
         UIButton* infoButton = [UIButton buttonWithType: UIButtonTypeInfoLight];
         [infoButton addTarget:self action:@selector(displayAboutUs) forControlEvents:UIControlEventTouchDown];
         
         UIBarButtonItem* itemAboutUs =[[UIBarButtonItem alloc]initWithCustomView:infoButton];
         …

         
         */
        UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@"Cancel" style:UIBarButtonItemStyleBordered target:self
                                                                      action:nil];
        [self.navigationItem setBackBarButtonItem: backButton];
        UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithTitle:@"Save" style:UIBarButtonItemStylePlain
                                                                       target:self action:@selector(doAddEditDrink:)];
        self.navigationItem.rightBarButtonItem = rightButton;
        
        fcUserItem *itemToCopy = [[myCommonAppDelegate userItems] getUserItemForID:self.userItemID];
        if(nil!=itemToCopy)
        {
            self.itemInProgress = [itemToCopy clone];
        }
        self.itemNotes = [self.itemInProgress.userItemExtra copy];
    }
    else
    {
        // Adding
        NSString *WelcomeString = [NSString stringWithFormat:@"%@",
                                   @"Build your drink"];
        self.title=WelcomeString;
        self.itemInProgress = [self.menu makeDefaultItem:self.itemTypeID ];
        UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@"Cancel" style:UIBarButtonItemStyleBordered target:self
                                                                      action:nil];
        [self.navigationItem setBackBarButtonItem: backButton];
        UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithTitle:@"Add" style:UIBarButtonItemStylePlain
                                                                       target:self action:@selector(doAddEditDrink:)];
        self.navigationItem.rightBarButtonItem = rightButton;

        
    }
    
    self.menu = [myCommonAppDelegate getMenuForCurrentLocation];
    [self setHeaderItems ];
    if(self.menu==nil)
    {
        self.title = CHECK_MENU_MESSAGE;
        [self doDownloadItemData];
    }
    else
    {
        [self createListIndex];
    }
    
    //[self createListIndex];
}

-(void)doDownloadItemData
{

    self.downloadingMenu=TRUE;
    
    if([myCommonAppDelegate networkUp]!=TRUE)
    {
        [[myCommonAppDelegate topController] showNetworkDownToast:self.view];
        [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
        return;
    }
    
    NSNumber *menuVersion=nil;
    fcMenu *menu = [myCommonAppDelegate getMenuForCurrentLocation];
    if(menu!=nil)
    {
        menuVersion = [menu menuVersion];
    }
    
    NSMutableString *myRequestString = [NSMutableString stringWithFormat:@"%@=%@",
                                 USER_COMMAND_CMD,GET_MENU_FOR_USER_CMD
                                 ];
    
    if(menuVersion!=nil)
    {
        [  myRequestString appendFormat:@"&%@=%@",MENU_HAVE_VERSION_CMD_ARG,menuVersion ];
    }
    
    NSData *myRequestData = [ NSData dataWithBytes: [ myRequestString UTF8String ] length: [ myRequestString length ] ];
    
    
    NSString *URLString = [NSString stringWithFormat:@"%@%@",BASE_URL,MENU_PAGE];
    
    NSMutableURLRequest *request = [ [ NSMutableURLRequest alloc ] initWithURL: [ NSURL URLWithString:  URLString] ];
    [ request setHTTPMethod: @"POST" ];
    [ request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"content-type"];
    [ request setHTTPBody: myRequestData ];
    [ request setTimeoutInterval:[myCommonAppDelegate getRequestShortTimeout]];
    
    self.conRequest=[[NSURLConnection alloc] initWithRequest:request delegate:self] ;
    
    self.HUD = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    self.HUD.dimBackground = YES;
    self.HUD.labelText=CHECK_MENU_HUD_MESSAGE;
    self.HUD.delegate=self;
        
}

- (void) setHeaderItems
{
    //self.itemNameEdit.text = self.itemInProgress.userItemName;
    
    self.itemIncludeDefaultCheck.checked = [self.itemInProgress includeDefault];
    
}
- (BOOL)textFieldShouldReturn:(UITextField *)textBoxName {
	[textBoxName resignFirstResponder];
	return YES;
}


- (NSMutableString*)makeAddEditPost
{
    
    
    NSMutableString *result = [[NSMutableString alloc]init];
    
    // Add or edit
    if(self.isEdit!=TRUE)
    {
        // Add
        [result appendFormat:@"%@=%@",USER_COMMAND,ITEM_ADD_EDIT_COMMAND_ADD];
        [result appendFormat:@"&%@=%@",MENU_ID_CMD_ARG,[self.menu menuID]];
        [result appendFormat:@"&%@=%@",ITEM_TYPE_TYPE_ID_CMD_PARAM,self.itemType.itemTypeTypeID];
        
    }
    else
    {
        // It's an edit
        [result appendFormat:@"%@=%@",USER_COMMAND,ITEM_ADD_EDIT_COMMAND_EDIT];
        [result appendFormat:@"&%@=%@",USER_ITEM_ITEM_ID,self.itemInProgress.userItemID];
        
    }
    
    NSNumber *menuVersion=nil;
    fcMenu *menu = [myCommonAppDelegate getMenuForCurrentLocation];
    if(menu!=nil)
    {
        menuVersion = [menu menuVersion];
    }
    if(menuVersion!=nil)
    {
        [  result appendFormat:@"&%@=%@",MENU_HAVE_VERSION_CMD_ARG,menuVersion ];
    }
    
    [result appendFormat:@"&%@=%@",ITEM_ADD_EDIT_SCHEMA_STRING,ITEM_ADD_EDIT_SCHEMA];
    
        
    
    // Item Type
    // TODO Error check might well be nice here.
    [result appendFormat:@"&%@=%@",USER_ITEM_TYPE_ID,self.itemInProgress.ItemTypeID];
    
    // NOTES
    if( (nil!=self.itemInProgress.userItemExtra) && ([self.itemInProgress.userItemExtra length]>0))
    {
        NSString *extra=[XMLHelper URLEncodedString:self.itemInProgress.userItemExtra];
        [result appendFormat:@"&%@=%@",USER_ITEM_EXTRA_OPTIONS_ATTR,extra];
        
    }
    
    // Item Name
    
    if( (nil!=self.itemInProgress.userItemName) && ([self.itemInProgress.userItemName length]>0))
    {
        NSString *itemName=[XMLHelper URLEncodedString:self.itemInProgress.userItemName];
        [result appendFormat:@"&%@=%@",USER_ITEM_NAME_ATTR,itemName];

    }
    
    if(self.itemInProgress.includeDefault==TRUE)
    {
        [result appendFormat:@"&%@=1",USER_ITEM_INCLUDE_DEFAULT_ATTR];
    }
    else
    {
        [result appendFormat:@"&%@=0",USER_ITEM_INCLUDE_DEFAULT_ATTR];
    }
    
    [self.itemInProgress addOptionsListToPost:result];
    
     
    return result;
}

- (void) doAddEditDrink:(id)sender
{
    // Close kbd when doing an add/edit
    //[self.itemNameEdit resignFirstResponder];
    
    if([myCommonAppDelegate networkUp]!=TRUE)
    {
        [[myCommonAppDelegate topController] showNetworkDownToast:self.view];
        return;
    }
    NSInteger OptionToFix=-1;
    self.downloadingMenu=FALSE;
    if([self areAllMandatoryOptionsSelected]!=TRUE)
    {
        // This is silly as we traverse the list twice. Oh well, at least it's a short list.
        OptionToFix = [self getIndexOfFirstOptionToFix];
        
        fcAddEditItemListIndexDataItem *item = [self.listIndex objectAtIndex:OptionToFix];
        if(nil==item)
        {
            // Errk
            ITToastMessage *Toast = [[ITToastMessage alloc]
                                     initWithDuration:ITToastMessageDefaultDuration andText:@"Internal Error Occured"]  ;
            
            [Toast displayInView:[self view]];
            return;
        }
        NSString *errorStr = [NSString stringWithFormat:@"Please select a %@",item.optionDescr];
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: @"Options Missing"
                                   message: errorStr
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
        
        
        //ITToastMessage *Toast = [[ITToastMessage alloc] initWithDuration:ITToastMessageDefaultDuration andText:errorStr]  ;
        
        //[Toast displayInView:[self view]];
        return;
        
    }
    
    
    //NSString *itemName = self.itemNameEdit.text;
    NSString *itemName=@""; // Removed because control was too hard to use
    BOOL alwaysInclude = self.itemIncludeDefaultCheck.checked;
    
    self.itemInProgress.userItemName = itemName;
    self.itemInProgress.includeDefault=alwaysInclude;
    self.itemInProgress.userItemExtra=self.itemNotes;
    
    NSMutableString *requestStr = [self makeAddEditPost];
    
    FC_Log(@"%@",requestStr);
        
    //NSString *myRequestStringEscaped = [requestStr stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    
    NSData *myRequestData = [ NSData dataWithBytes: [ requestStr UTF8String ] length: [ requestStr length ] ];
    
    NSString *URLString = [NSString stringWithFormat:@"%@%@",BASE_URL,DRINK_ADD_EDIT_PAGE];
    
    
    NSMutableURLRequest *request = [ [ NSMutableURLRequest alloc ] initWithURL: [ NSURL URLWithString:  URLString] ];
    [ request setHTTPMethod: @"POST" ];
    [ request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"content-type"];
    [ request setHTTPBody: myRequestData ];
    
    self.conRequest = [[NSURLConnection alloc] initWithRequest:request delegate:self] ;
    
    self.HUD = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    
    self.HUD.dimBackground = YES;
    if(self.isEdit==TRUE)
    {
        self.HUD.labelText=PROGRESS_EDIT_ITEM_MESSAGE;
    }
    else
    {
        self.HUD.labelText=PROGRESS_ADD_ITEM_MESSAGE;
    }
    self.HUD.delegate=self;
    

    
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
    [self.HUD hide:YES afterDelay:HUD_HIDE_DELAY_DEFAULT];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // Once this method is invoked, "responseData" contains the complete result
    [self.HUD hide:YES afterDelay:HUD_HIDE_DELAY_SHORT];
#ifdef FC_DO_LOG
    
    NSString* newStr = [[NSString alloc] initWithData:self.responseData
                                             encoding:NSUTF8StringEncoding];
    FC_Log(@"%@",newStr);
#endif
    
    if(self.isEdit==TRUE)
    {
        // Editing
        NSString *WelcomeString = [NSString stringWithFormat:@"%@",
                                   @"Build your drink"];
        self.title=WelcomeString;

    }
    else
    {
        NSString *WelcomeString = [NSString stringWithFormat:@"%@",
                                   @"Build your drink"];
        self.title=WelcomeString;
    }
    fcXMLParserUserItems *itemsParser =nil;
    XMLParserMenu *menuParser=nil;
    NSXMLParser *parser = [[NSXMLParser alloc] initWithData:self.responseData];
    [parser setShouldProcessNamespaces:YES];
    if(self.downloadingMenu==TRUE)
    {
        
        menuParser = [[XMLParserMenu alloc]init];
        [parser setDelegate: menuParser];
    }
    else
    {
        if(self.isEdit)
        {
            itemsParser = [[fcXMLParserUserItems alloc]initWithProcessingType:UserItemsProccessingTypeEditItem];
        }
        else
        {
            itemsParser = [[fcXMLParserUserItems alloc]initWithProcessingType:UserItemsProccessingTypeAddItem];
        }
        [parser setDelegate: itemsParser];
    }

    if([parser parse]!=YES)
    {
        self.downloadingMenu=FALSE;

        return;
    }
    
    if(self.downloadingMenu==TRUE)
    {
        if(menuParser.signonError==TRUE)
        {
            [[myCommonAppDelegate navController] popToRootViewControllerAnimated:NO];
            return;
        }
        if([menuParser.serverResponse responseResult]!=EnumServerOjbectResponseResultOK)
        {
            NSString *message = [NSString stringWithFormat:@"%@: %@",NETWORK_ERROR_ALERT_MESSAGE, [parser parserError]];
            UIAlertView *alert =
            [[UIAlertView alloc] initWithTitle: NETWORK_ERROR_ALERT_TITLE
                                       message: message
                                      delegate: self
                             cancelButtonTitle: @"OK"
                             otherButtonTitles: nil];
            [alert show];
            return;
            //[[myCommonAppDelegate navController] popViewControllerAnimated:NO];
            
        }
        
        if([menuParser.serverResponse objectStatus]==EnumServerOjbectResponseObjectStatusObjectIncluded)
        {
            [[myCommonAppDelegate menus] setMenu:menuParser.menu];
        }
        
        else if([menuParser.serverResponse objectStatus]==EnumServerOjbectResponseObjectStatusObjectNotExist)
        {
            // TODO -- better job here ?
            [[myCommonAppDelegate navController] popViewControllerAnimated:NO];
            
        }
        else if([menuParser.serverResponse objectStatus]==EnumServerOjbectResponseObjectStatusNeedNew)
        {
            // TODO -- better job here ?
            [[myCommonAppDelegate navController] popViewControllerAnimated:NO];
            
        }
        else if([menuParser.serverResponse objectStatus] == EnumServerOjbectResponseObjectStatusHaveLatest)
        {
            // NOTHING (fall through)
        }
        self.menu = [myCommonAppDelegate getMenuForCurrentLocation];
        
        [self createListIndex];
    }
    else
    {
        if(itemsParser.signonError==TRUE)
        {
            [[myCommonAppDelegate navController] popToRootViewControllerAnimated:NO];
            return;
        }
        //[self CreateListIndex];
        if(itemsParser.parseSuccessful)
        {
            if([itemsParser.objectResponse responseResult]!=EnumServerOjbectResponseResultOK)
            {
                if( ([itemsParser.objectResponse objectStatus]==EnumServerOjbectResponseObjectStatusUnknown) ||
                   ([itemsParser.objectResponse objectStatus]==EnumServerOjbectResponseObjectStatusNeedNew) ||
                   ([itemsParser.objectResponse objectStatus]==EnumServerOjbectResponseObjectStatusObjectNotExist))
                {
                    // Menu needs reload

                
                    UIAlertView *alert =
                    [[UIAlertView alloc] initWithTitle: @"Error"
                                           message: @"Your menu is out of date. Please try again"
                                          delegate: self
                                 cancelButtonTitle: @"OK"
                                 otherButtonTitles: nil];
                    [alert show];
                    [myCommonAppDelegate ClearCurrentMenu];
                    [self doEndSuccessfully];
                    
                    return;
                
                }
                else
                {
                    ITToastMessage *Toast = [[ITToastMessage alloc]
                                             initWithDuration:ITToastMessageDefaultDuration andText:@"An error occured. Please try again"]  ;
                    
                    [Toast displayInView:[self view]];
                }
                
            }
            if(self.isEdit)
            {
                ITToastMessage *Toast = [[ITToastMessage alloc]
                                         initWithDuration:ITToastMessageDefaultDuration andText:@"Item Edited Successfully"]  ;
            
                // Since it is a Dictionary by ID, it will overwrite the prior one.
                [[myCommonAppDelegate userItems] addUserItem:itemsParser.addOrEditedItem];
                [Toast displayInView:[self view]];
                            [self doEndSuccessfully];
                self.downloadingMenu=FALSE;
                return;
            }
            else
            {
                // Its an add
            
                [[myCommonAppDelegate userItems] addUserItem:itemsParser.addOrEditedItem];
                if(itemsParser.addOrEditedItem.includeDefault==TRUE)
                {
                    [[myCommonAppDelegate getCurrentOrder] addItemToOrder:itemsParser.addOrEditedItem.userItemID];
                }
                ITToastMessage *Toast = [[ITToastMessage alloc]
                                         initWithDuration:ITToastMessageDefaultDuration andText:@"Item Added Successfully"]  ;
            
                [Toast displayInView:[self view]];
                [self doEndSuccessfully];
                self.downloadingMenu=FALSE;
                return;
            }
        }
        else
        {
            ITToastMessage *Toast = [[ITToastMessage alloc]
                                     initWithDuration:ITToastMessageDefaultDuration andText:@"An error occured. Please try again"]  ;
            
            [Toast displayInView:[self view]];
        }
    }
    
    self.downloadingMenu=FALSE;
    
}


- (BOOL) areAllMandatoryOptionsSelected
{
    for(int index=0;index<[self.listIndex count];index++)
    {
        fcAddEditItemListIndexDataItem *item = [self.listIndex objectAtIndex:index];
        
        if((item.isMandatory==TRUE) && (item.isNone==TRUE))
        {
            return FALSE;
        }
        
    }
    return TRUE;
}

- (NSInteger) getIndexOfFirstOptionToFix
{
    for(NSInteger index=0;index<[self.listIndex count];index++)
    {
        fcAddEditItemListIndexDataItem *item = [self.listIndex objectAtIndex:index];
        
        if((item.isMandatory==TRUE) && (item.isNone==TRUE))
        {
            return index;
        }
        
    }
    return -1;
}



- (void) createListIndex
{
    if(nil==self.itemType)
    {
        self.itemType = [self.menu findItemTypeByID:_itemTypeID];
    }
    self.itemTypeLabel.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    if(nil!=self.itemType)
    {
        [self.itemTypeLabel setText: [self.itemType itemTypeName ]];
    }
    
    [self.listIndex removeAllObjects];
    
    fcAddEditItemListIndexDataItem *indexEntry=nil;
    
    NSMutableDictionary *optionGroups = [self.menu optionGroups];
    
    for(NSNumber *aKey in [ optionGroups allKeys] )
    {
       MenuItemOptionGroup *group = [optionGroups objectForKey:aKey];
    
       if([self.itemType areAnyOptionsValidForDrinkOptionGroup:[group GroupID]])
       {
           indexEntry = [self createIndexEntry:group];
           [self.listIndex addObject:indexEntry];
       }
    }
    
    // Sort first, then add Notes because it doesnt really have a Sort Order (maybe I should pick IntMax for it ?)
    // THis is a little lazy
    self.listIndex = [[self.listIndex sortedArrayUsingSelector:@selector(compare:)] mutableCopy];
    
    // Now add an entry for Notes
    indexEntry = [[fcAddEditItemListIndexDataItem alloc]init];
    indexEntry.entryType=OPTION_TYPE_EXTRA_OPTIONS; // Renamed to Notes in GUI
    indexEntry.optionValue = self.itemNotes;
    if(nil==indexEntry.optionValue)
    {
        indexEntry.optionValue =NONE_TEXT;
    }
    [self.listIndex addObject:indexEntry];
    
    
    [self.itemsList reloadData];
    
}

- (void) tableView: (UITableView*)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    fcAddEditItemListIndexDataItem *itemData = [self.listIndex objectAtIndex:[indexPath row]];
    if(nil==itemData)
    {
        NSString *Log = [NSString stringWithFormat:@"AddEdit: didSelectRowAtIndexPath Item: %i, Not found In Index (bad)",indexPath.row];
        FC_ALog(@"%@",Log);
        return ;
    }
    
    
    if(itemData.entryType==OPTION_TYPE_FOOD_DRINK_OPTION)
    {
        fcItemOptionValuePickerViewController *optionsView = [[fcItemOptionValuePickerViewController alloc]
                                                              initWithNibName:@"fcItemOptionValuePickerViewController"
                                                              userItem:self.itemInProgress andOptionGroup:itemData.optionGroupRef
                                                              andItemType:self.itemType];
    
        optionsView.delegate=self;
        [ [myCommonAppDelegate navController] pushViewController:optionsView animated:NO];
    }
    else if(itemData.entryType==OPTION_TYPE_EXTRA_OPTIONS)
    {
        fcItemNotesViewController *notesView = [[fcItemNotesViewController alloc] initWithNibName:@"fcItemNotesViewController"
                                                                                         andNotes:self.itemNotes];
        notesView.delegate=self;
        [ [myCommonAppDelegate navController] pushViewController:notesView animated:NO];
    }
    
    
}

- (fcAddEditItemListIndexDataItem*) createIndexEntry:(MenuItemOptionGroup*)group
{
    fcAddEditItemListIndexDataItem *indexEntry= [[fcAddEditItemListIndexDataItem alloc]init];
    
    indexEntry.optionGroupRef=group;
    indexEntry.optionGroupID=[group GroupID];
    indexEntry.optionDescr = [group GroupName];
    indexEntry.pickType= [group SelectionType];
    indexEntry.sortOrder = [group SortOrder];
    indexEntry.entryType = OPTION_TYPE_FOOD_DRINK_OPTION;
    
    indexEntry.optionValue = [self.itemInProgress makeOptionValueStringForOptionGroup:indexEntry.optionGroupID andItemType: self.itemTypeID];
    indexEntry.optionPrice = [self.itemInProgress makeOptionCostForOptionGroup:indexEntry.optionGroupID andItemType: self.itemTypeID];
    
    if( (nil==indexEntry.optionValue) || ([indexEntry.optionValue length]==0) )
    {
        indexEntry.optionValue = NONE_TEXT;
        indexEntry.isNone=TRUE;
    }
    else
    {
        indexEntry.isNone=FALSE;
    }
    
    if( [self.menu isOptionGroupMandatoryForItemType:self.itemTypeID andOptionGroup:indexEntry.optionGroupID] )
    {
        indexEntry.isMandatory=TRUE;
    }
    else
    {
        indexEntry.isMandatory=FALSE;
    }
    
    return indexEntry;
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (UITableViewCell*)tableView:(UITableView*)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    // DO NOT CHANGE STRING CellIdentifier w/out Changing reuse ID in the NIB !!!
    static NSString *CellIdentifier = @"AddEditItemTableCell";
    UITableViewCell *Cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if(nil==Cell)
    {
        // Got to load one
        UINib *theCellNib = [UINib nibWithNibName:@"fcAddEditItemTableCell" bundle:nil];
        [theCellNib instantiateWithOwner:self options:nil];
        Cell = [self tableCell];
        
    }
    else
    {
        // Test to make sure reuse happens sometimes at leaste.
    }
    
    UILabel *mainText = (UILabel*)[Cell viewWithTag:1];
    UILabel *costText = (UILabel*)[Cell viewWithTag:2];
    
    [self UpdateTableRow:mainText andCost:costText withIndex:indexPath];
    
    
    return Cell;
}


-(void) UpdateTableRow:(UILabel*)mainText andCost:(UILabel*)costText withIndex:(NSIndexPath*)indexPath;

{
    fcAddEditItemListIndexDataItem *itemEntry = [self.listIndex objectAtIndex:indexPath.row];
    
    // TODO Check range but we should be good as its a delegate method...
    if(nil==itemEntry)
    {
        return ;
    }
    
    if(itemEntry.entryType==OPTION_TYPE_FOOD_DRINK_OPTION)
    {
        if( (nil!=itemEntry.optionPrice) &&
            ([itemEntry.optionPrice length]!=0) &&
            (![itemEntry.optionPrice isEqualToString:@"0.00"]))
        {
            [costText setText:[NSString stringWithFormat:@"$%@",itemEntry.optionPrice]];
        }
        else
        {
            [costText setText:@""];
        }
        
        NSString *descrText = [NSString stringWithFormat:@"%@: %@",itemEntry.optionDescr,itemEntry.optionValue];
        [mainText setText:descrText];
        if( (itemEntry.isMandatory==TRUE) && (itemEntry.isNone==TRUE))
        {
            // Mandatory and Set to none = bad
            [mainText setTextColor:[UIColor redColor]];
            
        }
        else
        {
            [mainText setTextColor:[UIColor blackColor]];
            
        }
        
    }
    else if(itemEntry.entryType==OPTION_TYPE_EXTRA_OPTIONS)
    {
        if([itemEntry.optionValue length]>0)
        {
            NSString *descrText = [NSString stringWithFormat:@"Notes: %@",itemEntry.optionValue];
            [mainText setText:descrText];
        }
        else
        {
            NSString *descrText = [NSString stringWithFormat:@"Notes: %@",NONE_TEXT];
            [mainText setText:descrText];
        }
        [costText setText:@""];
        [mainText setTextColor:[UIColor blackColor]];
    }
    
}

// Make sure we de-select our row when we appear otherwise it stays there selected when you come back to the ItemsList
- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.itemsList deselectRowAtIndexPath:[self.itemsList indexPathForSelectedRow] animated:YES];
}

- (NSInteger) numberOfSectionsInTableView:(UITableView*)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView*)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.listIndex count];
}
- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    CGFloat optionsHeight = [self getOptionsHeightForRowAtIndexPath:indexPath];
    CGRect f = cell.frame;
    if(optionsHeight+20<40)
    {
        f.size.height =40;
    }
    else
    {
        f.size.height = optionsHeight+20;
    }
    UILabel* optionsLabel = (UILabel*)[cell viewWithTag:1];
    
    if(nil!=optionsLabel)
    {
        CGRect optionsRect = optionsLabel.frame;
        optionsRect.size.height=optionsHeight;
        [cell viewWithTag:1].frame=optionsRect;
        //[cell viewWithTag:3]
    }
}
- (CGFloat) getOptionsHeightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    fcAddEditItemListIndexDataItem *listItem = [self.listIndex objectAtIndex:indexPath.row];
    
    NSString *text=nil;
    if(nil==listItem)
    {
        text = NONE_TEXT;
        //return 40.0; // YUCK
    }
    else if( (nil==listItem.optionValue) || ([listItem.optionValue length]==0) || ([listItem.optionValue isEqualToString:NONE_TEXT]))
    {
         text = NONE_TEXT;
    }
    else
    {
        text = [NSString stringWithFormat:@"%@: %@",listItem.optionDescr,listItem.optionValue];
    }
    UIFont *font = [UIFont systemFontOfSize: 17.0];
    
    CGSize optionsSize = [text sizeWithFont:font constrainedToSize:CGSizeMake(250,4000)];
    return optionsSize.height;
}

-(CGFloat) tableView:(UITableView*)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    
    CGFloat optionsHeight = [self getOptionsHeightForRowAtIndexPath:indexPath];
    if(optionsHeight+20<40)
    {
        return 40;
    }
    else
    {
        return optionsHeight+20;
    }
    //return optionsHeight+20.0;
}



- (void) AddHeader
{

    [[NSBundle mainBundle] loadNibNamed:@"fcAddEditItemTableHeader" owner:self options:nil];
    
    CGRect frame = self.TableHeader.frame;
    
    
    if([myCommonAppDelegate isRetina4Display])
    {
        frame.size.height-=40;
    }
    else
    {
        frame.size.height +=40;
    }
    
    
    
    //footerView.userInteractionEnabled = YES;
    self.itemsList.tableHeaderView = self.TableHeader;
    self.itemsList.tableHeaderView.userInteractionEnabled = YES;
    self.itemsList.tableHeaderView.frame=frame;
    //[ItemsList.tableFooterView setFrame:ViewSize];
    
}

- (void) AddFooter
{
    
    
    [[NSBundle mainBundle] loadNibNamed:@"fcAddEditItemTableFooter" owner:self options:nil];
    
    if(self.isEdit)
    {
        [self.addEditButton setTitle:@"Save Drink" forState:UIControlStateNormal];
    }
    else
    {
        [self.addEditButton setTitle:@"Add Drink" forState:UIControlStateNormal];
    }
   
    [self.addEditButton addTarget:self action:@selector(doAddEditDrink:) forControlEvents:UIControlEventTouchUpInside];
    
    
    for(UIView* subView in self.addEditButton.subviews)
        if([subView isKindOfClass:NSClassFromString(@"UIGroupTableViewCellBackground")])
            [subView setHidden:YES];
    
    CAGradientLayer *orderGrad =  [myCommonAppDelegate makeGreenGradient];
    orderGrad.frame = self.addEditButton.bounds;
    [self.addEditButton.layer insertSublayer:orderGrad atIndex:0];
    
    
    CGRect frame = self.TableFooter.frame;
    frame.size.height = frame.size.height+15;
    //footerView.userInteractionEnabled = YES;
    self.itemsList.tableFooterView = self.TableFooter;
    self.itemsList.tableFooterView.userInteractionEnabled = YES;
    self.itemsList.tableFooterView.frame=frame;
    
    //[ItemsList.tableFooterView setFrame:ViewSize];
    
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
- (void) doBack:(id) sender;
{
    [self doEndSuccessfully];
}

- (void) doEndSuccessfully
{
    // Go right back to order Now page
    //if(self.popTop==TRUE)
    //{
        NSArray *array = [self.navigationController viewControllers];
        
        fcItemsListViewController *orderPage = (fcItemsListViewController*)[array objectAtIndex:1];
        if([orderPage respondsToSelector:@selector(childFinishedCommit)])
        {
            [orderPage childFinishedCommit];
        }
        [self.navigationController popToViewController:[array objectAtIndex:1] animated:NO];
        return;
    //}
    /*
    else
    {
        [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
        if([self.delegate respondsToSelector:@selector(childFinishedCommit)])
        {
            [self.delegate childFinishedCommit];
        }
    }
    
    */
    
}
- (void) childFinished
{
    [self createListIndex];
   // [self.itemsList reloadData];
}
- (void) childFinishedWithNotes:(NSString*)notes
{
    self.itemNotes = [notes copy];
    [self createListIndex]; // Could avoid this if I dont put The actual string in Index, just a flag. Then can just reloadData (for another day) 
}
@end
