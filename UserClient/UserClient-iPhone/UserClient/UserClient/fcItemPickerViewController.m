//
//  fcItemPickerViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>
#import "fcItemPickerViewController.h"
#import "fcItemsListViewController.h"
#import "fcAddEditItemListIndexDataItem.h"
#import "fcAddEditItemViewController.h"
#import "fcRootViewController.h"
#import "fcAddItemViewController.h"
#import "fcAppDelegate.h"
#import "fcMenu.h"
#import "fcItemPickerDataListItem.h"
#import "fcUserItemTable.h"
#import "fcUserItem.h"
#import "fcTableCellButton.h"
#import "fcTableCellActionSheet.h"
#import "XMLParserMenu.h"
#import "fcXMLParserUserItems.h"
#import "Constants.h"
#import "ITToastMessage.h"
#import "fcMenuTable.h"
#import "fcServerObjectResponse.h"
#import "fcItemOrderList.h"


@interface ItemsPickerTableCellHolder : NSObject
{
    //UIImageView *LeftImage;
    UILabel *MainText;
    UILabel *SmallText;
    UILabel *PriceText;
    fcTableCellButton *actionButton;
    
}
//@property (nonatomic, strong) UIImageView *LeftImage;
@property (nonatomic, strong) UILabel *MainText;
@property (nonatomic, strong) UILabel *SmallText;
@property (nonatomic, strong) UILabel *PriceText;
@property (nonatomic, strong) fcTableCellButton *actionButton;

@end
@implementation ItemsPickerTableCellHolder

//@synthesize LeftImage;
@synthesize MainText=_mainText;
@synthesize SmallText=_smallText;
@synthesize PriceText=_priceText;
@synthesize actionButton=_actionButton;
@end

@interface fcItemPickerViewController ()

@end

@implementation fcItemPickerViewController
//@synthesize WelcomeUserLabel;
@synthesize itemsList=_itemsList;
@synthesize listIndex=_listIndex;
@synthesize TableFooter;
@synthesize delegate=_delegate;
@synthesize processingDelete=_processingDelete;
@synthesize conRequest=_conRequest;
@synthesize menu=_menu;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        // Custom initialization
        _menu = [myCommonAppDelegate getMenuForCurrentLocation];
        _processingDelete=FALSE;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    responseData = [[NSMutableData alloc] init ];
    NSString *WelcomeString = [NSString stringWithFormat:@"%@",@"Favorites"];
    
    self.title = WelcomeString;
    
    UIColor *background = [[UIColor alloc] initWithPatternImage:
                           [UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
        
    //[WelcomeUserLabel setText:WelcomeString];
    
    self.ListIndex = [[NSMutableArray alloc] init];
    
    self.itemsList.backgroundColor = [UIColor clearColor];
    self.itemsList.opaque = NO;
    self.itemsList.backgroundView = nil;

    
    self.navigationItem.hidesBackButton = YES;
    UIBarButtonItem *barButton = [[UIBarButtonItem alloc]
                                  initWithTitle:@"Order"
                                  style:UIBarButtonItemStyleBordered
                                  target:self
                                  action:@selector(exitCommitted)];
    
    [self.navigationItem setLeftBarButtonItem: barButton];

    
    //self.itemsList.tableFooterView=[[UIView alloc] init];
    
     
    if (!self.itemsList.tableFooterView)
    {
        [self AddItemsPickListFooter ];
    }
    
    self.itemsList.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithTitle:@"New Drink" style:UIBarButtonItemStylePlain
                                                                   target:self action:@selector(doNewDrink:)];
    self.navigationItem.rightBarButtonItem = rightButton;

    self.menu = [myCommonAppDelegate getMenuForCurrentLocation];
    if(self.menu==nil)
    {
        self.title = CHECK_MENU_MESSAGE;
        [self doDownloadItemData];
    }
    else
    {
        [self CreateListIndex];
    }

}
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

- (void) tableView: (UITableView*)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    fcItemPickerDataListItem *item = [self.listIndex objectAtIndex:indexPath.row];
    if(nil==item)
    {
        NSString *Log = [NSString stringWithFormat:@"ItemPicker: didSelectRowAtIndexPath Item: %i, Not found In Index (bad)",indexPath.row];
        FC_ALog(@"%@",Log);
        return ;
    }
    
    [self doActionsWork:indexPath];
    
    //[self exitCommitted];

}

- (UITableViewCell*)tableView:(UITableView*)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"ItemPickerTableCell"; // MUST Match the NIB !!!
    UITableViewCell *Cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if(nil==Cell)
    {
        // Got to load one
        //[[NSBundle mainBundle] loadNibNamed:@"ItemsListTableCell" owner:self options:nil];
        UINib *theCellNib = [UINib nibWithNibName:@"fcItemPickerTableCell" bundle:nil];
        [theCellNib instantiateWithOwner:self options:nil];
        Cell = [self tableCell];
        
    }
    else
    {
        // Test to make sure reuse happens sometimes at leaste.
    }
    
    ItemsPickerTableCellHolder *Holder = [[ItemsPickerTableCellHolder alloc] init];
    
    //Holder.LeftImage = (UIImageView*)[Cell viewWithTag:1];
    Holder.MainText = (UILabel*) [Cell viewWithTag:2];
    Holder.SmallText= (UILabel*)[Cell viewWithTag:3];
    Holder.PriceText=(UILabel*)[Cell viewWithTag:4];
    Holder.actionButton=(fcTableCellButton *)[Cell viewWithTag:5];
    Holder.actionButton.cellIndex=indexPath;
    Holder.actionButton.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    
    [Holder.actionButton addTarget:self action:@selector(doActions:) forControlEvents:UIControlEventTouchUpInside];
    for(UIView* subView in Holder.actionButton.subviews)
        if([subView isKindOfClass:NSClassFromString(@"UIGroupTableViewCellBackground")])
            [subView setHidden:YES];
    
    CAGradientLayer *orderGrad =  [myCommonAppDelegate makeGreyGradient];
    orderGrad.frame = Holder.actionButton.bounds;
    [Holder.actionButton.layer insertSublayer:orderGrad atIndex:0];

    
    [self UpdateTableRowFromHolder:Holder withIndex:indexPath];
    
    return Cell;
}

- (void) doActionsWork:(NSIndexPath *)index
{
    fcTableCellActionSheet *actionSheet = [[fcTableCellActionSheet alloc]
                                           initWithTitle:@"Actions" delegate:self
                                           cancelButtonTitle:@"Cancel"
                                           destructiveButtonTitle:@"Delete Item"
                                           otherButtonTitles:@"Edit Item", @"Add to order",nil
                                           ];
    actionSheet.cellIndex=index;
    [actionSheet showInView:self.view] ;
}
- (void) doActions:(id)sender
{
    fcTableCellButton *but = (fcTableCellButton*)(sender);
    
   // NSIndexPath *index = but.cellIndex;
    
    [self doActionsWork:but.cellIndex];
    
}

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
       
}

- (void)doGoEdit:(NSNumber*)itemID
{
    fcUserItem *item = [[myCommonAppDelegate userItems] getUserItemForID:itemID];
    if(nil==item)
    {
        return;
    }
        
    fcAddEditItemViewController *addEditView = [[fcAddEditItemViewController alloc]
                                                
                                                initWithNibName:[myCommonAppDelegate getRet4ControllerName:@"fcAddEditItemViewController"]
                                                andItemType: [item ItemTypeID]
                                                andUserItemId: itemID
                                                andIsEdit:TRUE andPopTop: FALSE];
    
    addEditView.delegate=self;
    [ [myCommonAppDelegate navController] pushViewController:addEditView animated:YES];
    

}
- (void)actionSheet:(UIActionSheet *)actionSheet didDismissWithButtonIndex:(NSInteger)buttonIndex
{
    
    fcTableCellActionSheet *mySheet = (fcTableCellActionSheet*)actionSheet;
    
    NSIndexPath *index =mySheet.cellIndex;
    FC_Log(@"Clicked Index:%d",buttonIndex);
    
    fcItemPickerDataListItem *item = [self.listIndex objectAtIndex:index.row];
    
    switch(buttonIndex)
    {
             case 2:
             // Now I really hate myself
             // Add to order
             [[myCommonAppDelegate getCurrentOrder] addItemToOrder:item.itemID];
             [self exitCommitted];
             break;
             
        case 0:
            // Delete item
            if([myCommonAppDelegate networkUp]!=TRUE)
            {
                [[myCommonAppDelegate topController] showNetworkDownToast:self.view];
                return;
            }
            
            [self doDeleteItem:item.itemID];
            break;
        case 1:
            // Edit Item
            [self doGoEdit:item.itemID];
            
            
            
            break;
    }

}

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex
{
    //[actionSheet dismissWithClickedButtonIndex:buttonIndex animated:NO];
}

- (void)actionSheetCancel:(UIActionSheet *)actionSheet
{
    
}
- (void) UpdateTableRowFromHolder: (ItemsPickerTableCellHolder*)holder withIndex: (NSIndexPath*) index
{
    // Technically should check the array range here ..... next release
    fcItemPickerDataListItem *listItem = [self.listIndex objectAtIndex:index.row];
    if(nil==listItem)
    {
        return;
    }
    fcUserItem *userItem = [[myCommonAppDelegate userItems ] getUserItemForID:listItem.itemID];
    
    if(nil==userItem)
    {
        return; // FIXME
    }
    
    NSMutableString *itemDescr = [NSMutableString stringWithString:userItem.userItemItemTypeLongDescr];
    
    
    
    if( (nil!=userItem.userItemName) &&   ([userItem.userItemName length]>0) )
    {
        [itemDescr appendFormat:@" (%@)",userItem.userItemName];
    }
    
    [holder.MainText setText:itemDescr];
    

    NSString *costStr = [NSString stringWithFormat:@"$%@",userItem.userItemCost];
    
    [holder.PriceText setText:costStr ];
    
    
    [holder.SmallText setText:userItem.userItemOptionsText];
}


- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    CGFloat optionsHeight = [self getOptionsHeightForRowAtIndexPath:indexPath];
    //CGRect f = cell.frame;
    
    //f.size.height = optionsHeight+40.0;
    
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
- (CGFloat) getOptionsHeightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    fcItemPickerDataListItem *listItem = [self.listIndex objectAtIndex:indexPath.row];
    
    
    if(nil==listItem)
    {
        return 50.0; // YUCK
    }
    fcUserItem *userItem = [[myCommonAppDelegate userItems ] getUserItemForID:listItem.itemID];
    
    if(nil==userItem)
    {
        return 50.0; // YUCK
    }
    
    return 50.0;
    /*
    NSMutableString *itemOptions = [NSMutableString stringWithString:userItem.userItemOptionsText];
    
    
    UIFont *font = [UIFont fontWithName:@"Helvetica" size:10.0];
    
    CGSize optionsSize = [itemOptions sizeWithFont:font constrainedToSize:CGSizeMake(160,4000)];
    if(optionsSize.height<50.0)
    {
        return 50.0;
    }
    return optionsSize.height;
     */
}

-(CGFloat) tableView:(UITableView*)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    
    CGFloat optionsHeight = [self getOptionsHeightForRowAtIndexPath:indexPath];
    
    return optionsHeight+40.0;
    /*
    ItemListIndexData *Item = [ListIndex objectAtIndex:indexPath.row];
    if(nil==Item)
    {
        NSString *Log = [NSString stringWithFormat:@"ItemsList: heightForRowAtIndexPath Item: %i, Not found In Index (bad)",indexPath.row];
        FC_Log(@"%@",Log);
        return ITEMS_LIST_DEFAULT_ROW_HEIGHT;
    }
    
    if(Item.ItemType==ITEM_LIST_USER_DRINK_TYPE)
    {
        return ITEMS_LIST_DRINK_ROW_HEIGHT;
    }
    return ITEMS_LIST_DEFAULT_ROW_HEIGHT;
    */
   // return 50;
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


- (void) CreateListIndex
{
    self.title = @"Favorites";
    
    [self.listIndex removeAllObjects];
    
    if(self.menu==nil)
    {
        return; // should not happen
        [self.itemsList reloadData];
    }
    fcItemPickerDataListItem *item=nil;
    
    fcUserItemTable *itemsTable = [myCommonAppDelegate userItems];
    
    // Populate drinks
    for(NSNumber *aKey in [ itemsTable userItems] )
    {
        fcUserItem *userItem = [[itemsTable userItems] objectForKey:aKey];

        // Only add it to the list if its for the current menu
        if([self.menu.menuID isEqualToNumber:userItem.menuID])
        {
        
            item = [[fcItemPickerDataListItem alloc]init];
            item.itemID = userItem.userItemID;
            [self.listIndex addObject:item];
        }
    }
    
    [self.itemsList reloadData];
}

- (void) AddItemsPickListFooter
{
    
    
    [[NSBundle mainBundle] loadNibNamed:@"ItemPickerTableFooter" owner:self options:nil];
    //UIButton *But = (UIButton*)[[self TableFooter] viewWithTag:1];
    //[But addTarget:self action:@selector(doBack:) forControlEvents:UIControlEventTouchUpInside];
    
    UIButton *addBut = (UIButton*)[[self TableFooter] viewWithTag:2];
    [addBut addTarget:self action:@selector(doNewDrink:) forControlEvents:UIControlEventTouchUpInside];
    
    for(UIView* subView in addBut.subviews)
        if([subView isKindOfClass:NSClassFromString(@"UIGroupTableViewCellBackground")])
            [subView setHidden:YES];
    
    CAGradientLayer *orderGrad =  [myCommonAppDelegate makeGreenGradient];
    orderGrad.frame = addBut.bounds;
    [addBut.layer insertSublayer:orderGrad atIndex:0];
    
    
    //footerView.userInteractionEnabled = YES;
    self.itemsList.tableFooterView = self.TableFooter;
    self.itemsList.tableFooterView.userInteractionEnabled = YES;
    //[ItemsList.tableFooterView setFrame:ViewSize];
    
}

- (void) doDeleteItem:(NSNumber*)itemID
{
    if([myCommonAppDelegate networkUp]!=TRUE)
    {
        [[myCommonAppDelegate topController] showNetworkDownToast:self.view];
        return;
    }
    
    NSString *URLString = [NSString stringWithFormat:@"%@%@?%@=%@&%@=%@",
                           BASE_URL,DRINK_ADD_EDIT_PAGE,
                           USER_COMMAND,DELETE_ITEM_COMMAND,
                           ITEM_ID,itemID];
    
    self.processingDelete=TRUE;
    NSURLRequest *theRequest=[NSURLRequest requestWithURL:[NSURL URLWithString:URLString]
                                              cachePolicy:NSURLRequestUseProtocolCachePolicy
                              
                                          timeoutInterval:60.0];
    FC_Log(@"%@",URLString);
    self.conRequest = [[NSURLConnection alloc] initWithRequest:theRequest delegate:self];
    
    HUD = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    HUD.dimBackground = YES;
    HUD.labelText=@"Deleting Drink";
    HUD.delegate=self;

}
-(void)doDownloadItemData

{
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

    
    HUD = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
    HUD.dimBackground = YES;
    HUD.labelText=CHECK_MENU_HUD_MESSAGE;
    HUD.delegate=self;
    
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    [responseData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    [responseData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    //[[NSAlert alertWithError:error] runModal];
    
    [HUD hide:YES afterDelay:HUD_HIDE_DELAY_SHORT];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // Once this method is invoked, "responseData" contains the complete result
    [HUD hide:YES afterDelay:HUD_HIDE_DELAY_SHORT];
    
    NSXMLParser *parser = [[NSXMLParser alloc] initWithData:responseData];
    //NSString *strData = [[NSString alloc]initWithData:responseData encoding:NSUTF8StringEncoding];
    
    [parser setShouldProcessNamespaces:YES];
    
    fcXMLParserUserItems *itemsXMLParser=nil;
    XMLParserMenu *menuXMLParser=nil;
    if(self.processingDelete==TRUE)
    {
        itemsXMLParser = [[fcXMLParserUserItems alloc]initWithProcessingType:UserItemsProccessingTypeDeleteItem];
        [parser setDelegate:itemsXMLParser];
    }
    else
    {
        menuXMLParser = [[XMLParserMenu alloc] init];
    
        [parser setDelegate: menuXMLParser];
        
    }
#ifdef FC_DO_LOG
    NSString* newStr = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
    FC_Log(@"Menu:%@",newStr);
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
    
    if(self.processingDelete==TRUE)
    {
        if(itemsXMLParser.signonError==TRUE)
        {
            [[myCommonAppDelegate navController] popToRootViewControllerAnimated:NO];
            return;
        }
        if((itemsXMLParser.wasServerResponseGood) && (itemsXMLParser.parseSuccessful))
        {
            [[myCommonAppDelegate getCurrentOrder] removeItemFromOrder:itemsXMLParser.deletedItemID andRemoveAll:TRUE];
            [[myCommonAppDelegate userItems] removeUserItem:itemsXMLParser.deletedItemID];
            //ITToastMessage *Toast = [[ITToastMessage alloc] initWithDuration:ITToastMessageDefaultDuration andText:@"Drink Deleted Successfully"]  ;
            
            //[Toast displayInView:[self view]];
            
        }
    }
    else
    {
        if(menuXMLParser.signonError==TRUE)
        {
            [[myCommonAppDelegate navController] popToRootViewControllerAnimated:NO];
            return;
        }
        if([menuXMLParser.serverResponse responseResult]!=EnumServerOjbectResponseResultOK)
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
        
        if([menuXMLParser.serverResponse objectStatus]==EnumServerOjbectResponseObjectStatusObjectIncluded)
        {
            [[myCommonAppDelegate menus] setMenu:menuXMLParser.menu];
        }
        
        else if([menuXMLParser.serverResponse objectStatus]==EnumServerOjbectResponseObjectStatusObjectNotExist)
        {
            // TODO -- better job here ?
            [[myCommonAppDelegate navController] popViewControllerAnimated:NO];
            
        }
        else if([menuXMLParser.serverResponse objectStatus]==EnumServerOjbectResponseObjectStatusNeedNew)
        {
            // TODO -- better job here ?
            [[myCommonAppDelegate navController] popViewControllerAnimated:NO];
            
        }
        else if([menuXMLParser.serverResponse objectStatus] == EnumServerOjbectResponseObjectStatusHaveLatest)
        {
            // NOTHING (fall through)
        }
        self.menu = [myCommonAppDelegate getMenuForCurrentLocation];
    }
    
    
    [self CreateListIndex];
    //[self.itemsList reloadData];
}

- (IBAction) doBack: (id) sender
{
    if([self.delegate respondsToSelector:@selector(childFinished)])
    {
        [self.delegate childFinished];
    }
    
    [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
    
}

- (IBAction) doNewDrink: (id) sender
{
    fcAddItemViewController *addView =
        [[ fcAddItemViewController alloc] initWithNibName:@"fcAddItemViewController" bundle:nil];
    addView.delegate=self;
    
    [ [myCommonAppDelegate navController] pushViewController:addView animated:NO];

}

- (void)dealloc
{
    [HUD removeFromSuperview];
    
}
- (void) childFinishedCommit
{
    [self exitCommitted];
    //[self CreateListIndex];
    //[self.itemsList reloadData];
}
- (void) childFinished
{
    [self CreateListIndex];
}
- (void) exitCommitted
{
    if([self.delegate respondsToSelector:@selector(childFinishedCommit)])
    {
        [self.delegate childFinishedCommit];
    }
    
    [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
}
@end
