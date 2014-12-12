//
//  fcItemsListViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012,2013 Freeway Coffee. All rights reserved.
//

#import "fcItemsListViewController.h"
#import "fcTimeToLocationViewController.h"
#import "fcCreditCardViewController.h"
#import "fcItemPickerViewController.h"
#import "fcCarDataViewController.h"
#import "fcAddItemViewController.h"
#import "fcRootViewController.h"
#import "fcAddEditItemViewController.h"
#import "fcOrderResponseViewController.h"
#import "fcUserTipsViewController.h"
#import "fcLocationDetailViewController.h"
#import "fcPaymentMethodViewController.h"
#import "fcLocationAllowedArrivalMethod.h"
#import "fcServerObjectResponse.h"
#import "WEPopoverController.h"
#import "fcItemOrderListTable.h"
#import "fcOrderPriceBreakdownViewController.h"

#import "fcLeftMenuViewController.h"

#import "fcLocation.h"

#import "fcXMLParserUserItems.h"
#import "fcTableCellButton.h"
#import "fcUserItemTable.h"
#import "fcUserItem.h"
#import "fcMenu.h"
#import "fcAppDelegate.h"
#import "ItemListIndexData.h"
#import "MBProgressHUD.h"
#import "ITToastMessage.h"
#import "XMLHelper.h"
#import "Constants.h"
#import "fcLastOrderItem.h"
#import "fcLastOrder.h"
#import "fcAppSetting.h"
#import "fcAppSettingsTable.h"
#import "fcUserTip.h"
#import "fcUserTipTable.h"
#import "fcUserInfo.h"
#import "fcItemOrderList.h"
#import "fcLeftMenuViewController.h"
#import "fcLocationAllowedPaymentMethod.h"
#import "fcLocationAllowedArrivalMethod.h"
#import "fcXMLParserItemsList.h"

#define ITEM_LIST_USER_LOCATION_TYPE 1
#define ITEM_LIST_USER_CREDIT_CARD_TYPE 2
#define ITEM_LIST_USER_FOOD_TYPE 3
#define ITEM_LIST_USER_DRINK_TYPE 4
#define ITEM_LIST_CHOOSE_ITEM_TYPE 5
#define ITEM_LIST_USER_TAG_TYPE 6
#define ITEM_LIST_USER_TIME_TO_LOCATION_TYPE 7
#define ITEM_LIST_ORDER_TOTAL_TYPE 8

#define ITEMS_LIST_ALERT_TAG_DO_REFRESH 1
#define ITEMS_LIST_ALERT_TAG_DO_NOTHING 2

// Table rows
#define ITEMS_LIST_DEFAULT_ROW_HEIGHT 50.0
#define ITEMS_LIST_DRINK_NO_OPTIONS_ROW_HEIGHT 15.0

//#define ITEMS_LIST_DRINK_ROW_HEIGHT 70.0


@interface ItemsListTableCellHolder : NSObject
{
    UIImageView *LeftImage;
    UILabel *MainText;
    UILabel *SmallText;
    UILabel *PriceText;
    fcTableCellButton *RemoveButton;
    
}
@property (nonatomic, strong) UIImageView *LeftImage;
@property (nonatomic, strong) UILabel *MainText;
@property (nonatomic, strong) UILabel *SmallText;
@property (nonatomic, strong) UILabel *PriceText;
@property (nonatomic, strong) fcTableCellButton *RemoveButton;
@end

@implementation ItemsListTableCellHolder
@synthesize LeftImage;
@synthesize MainText;
@synthesize SmallText;
@synthesize PriceText;
@synthesize RemoveButton;

@end

@interface fcItemsListViewController ()

@end


@implementation fcItemsListViewController


@synthesize ItemsList=_itemsList;;
@synthesize ItemListTableCell;

@synthesize ListIndex=_listIndex;
@synthesize processingOrder=_processingOrder;
//@synthesize TableFooter;

@synthesize conRequest=_conRequest;

@synthesize orderBut=_orderBut;
@synthesize feedbackBut=_feedbackBut;
@synthesize itemsListXMLParser=_itemsListXMLParser;
@synthesize menu=_menu;
@synthesize leftMenuViewController=_leftMenuViewController;
@synthesize showLastOrder=_showLastOrder;
@synthesize orderTax=_orderTax;
@synthesize orderTotal=_orderTotal;
@synthesize orderItemsTotal=_orderItemsTotal;
@synthesize orderDiscount=_orderDiscount;
@synthesize orderTip=_orderTip;
@synthesize orderConvenienceFee=_orderConvenienceFee;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        // Custom initialization
        _menu = [myCommonAppDelegate getMenuForCurrentLocation];
        _itemsListXMLParser=nil;
        _showLastOrder=TRUE;
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    responseData = [[NSMutableData alloc] init ];
    UIColor *background = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
    
   // UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@"Order" style:UIBarButtonItemStyleBordered target:self action:nil];
   // [self.navigationItem setBackBarButtonItem: backButton];
    
    //UIButton *infoButton = [UIButton buttonWithType:UIButtonTypeInfoLight];
    UIBarButtonItem *barButton = [[UIBarButtonItem alloc]
                                   initWithTitle:@"Order"
                                   style:UIBarButtonItemStyleBordered
                                   target:self
                                   action:@selector(CreateListIndexedCommit)];
   
    [self.navigationItem setBackBarButtonItem: barButton];
    // Set custom image here
    //backButton.image = [UIImage imageNamed:@"customImage.png"];
    //self.navigationItem.backBarButtonItem = backButton;
    
    /*
    UIImage *image1 = [UIImage imageNamed:@"fc_icon.png"];
    
    UIButton *backButton = [UIButton buttonWithType:UIButtonTypeCustom];
    backButton.frame = CGRectMake(0.0f, 0.0f, 36.0f, 36.0f);
    [backButton setBackgroundImage:image1 forState:UIControlStateNormal] ;
   // [backButton addTarget:self action:@selector(didTapBackButton:) forControlEvents:UIControlEventTouchUpInside];

    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    
    self.navigationItem.leftBarButtonItem = backButtonItem;

    //[[UIBarButtonItem appearance] setBackButtonBackgroundImage:image1 forState:UIControlStateNormal barMetrics:UIBarMetricsDefault];
    */
    
    //UIImage *image1 = [UIImage imageNamed:@"fc_icon.png"];
    /*
    UIImage *image1 = [UIImage imageNamed:@"menu-icon.png"];
    UIButton *LeftMenuButton = [UIButton buttonWithType:UIButtonTypeCustom];
    LeftMenuButton.frame = CGRectMake(0.0f, 0.0f, 36.0f, 36.0f);
    [LeftMenuButton setBackgroundImage:image1 forState:UIControlStateNormal] ;
    [LeftMenuButton addTarget:self action:@selector(doLeftMenu:) forControlEvents:UIControlEventTouchUpInside];
    self.navigationItem.hidesBackButton = YES;
    UIBarButtonItem *LeftMenuBarBUtton = [[UIBarButtonItem alloc] initWithCustomView:LeftMenuButton];
     self.navigationItem.leftBarButtonItem=LeftMenuBarBUtton;
    */
    
    


    /*
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"menu-icon.png"] style:UIBarButtonItemStyleBordered
                            target:self
                            action:@selector(doLeftMenu:)];
     */
    
    UIBarButtonItem *leftMenuBar =[[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"menu-icon.png"] style:UIBarButtonItemStyleBordered
                                                                  target:self
                                                                  action:@selector(doLeftMenu:)];
    self.navigationItem.leftBarButtonItem =leftMenuBar;
    
    
    /*
    UIBarButtonItem *rightMenuBar =[[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"menu-icon.png"] style:UIBarButtonItemStyleBordered
                                                                  target:self
                                                                  action:@selector(doLeftMenu:)];

    
    UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithTitle:@"Save" style:UIBarButtonItemStylePlain
                                                                   target:self action:@selector(doAddEditDrink:)];
     */
    
    UIBarButtonItem *refreshButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(doRefresh)];
    self.navigationItem.rightBarButtonItem = refreshButton;

    
    self.leftMenuViewController = [[fcLeftMenuViewController alloc] init];
    
    self.leftMenuPopover = [[WEPopoverController alloc]initWithContentViewController:self.leftMenuViewController];
    
    [self.leftMenuPopover setPopoverContentSize:CGSizeMake(200, 200)];
    
    self.leftMenuViewController.controller=self.leftMenuPopover;
        
    // Gross ios6 Hack ?
    [myCommonAppDelegate prepareButtonForGradient:self.orderBut];
    [myCommonAppDelegate prepareButtonForGradient:self.feedbackBut];
    
    CAGradientLayer *orderGrad =  [myCommonAppDelegate makeGreenGradient];
    orderGrad.frame = self.orderBut.bounds;
    [self.orderBut.layer insertSublayer:orderGrad atIndex:0];
    
    CAGradientLayer *feedbackGrad =  [myCommonAppDelegate makeGreyGradient];
    feedbackGrad.frame = self.feedbackBut.bounds;
    [self.feedbackBut.layer insertSublayer:feedbackGrad atIndex:0];
    
    
    self.title = @"Loading Order...";
    self.ItemsList.backgroundColor = [UIColor clearColor];
    self.ItemsList.opaque = NO;
    self.ItemsList.backgroundView = nil;
    [ [myCommonAppDelegate navController] setNavigationBarHidden:FALSE];
    
    self.ListIndex = [[NSMutableArray alloc] init];
    /*
    if (!ItemsList.tableFooterView)
    {
        [self AddItemsListFooter ];
    }
     */
    self.ItemsList.layer.cornerRadius = DEFAULT_CORNER_RADIUS;

    [self doGetItemList:TRUE];


}

- (void)doRefresh
{
    self.showLastOrder=FALSE;
    [self doGetItemList:FALSE];
}


-(void) doLeftMenu:(id)sender
{

    [self.leftMenuPopover presentPopoverFromBarButtonItem:self.navigationItem.leftBarButtonItem
                                 permittedArrowDirections:UIPopoverArrowDirectionUp
                                                 animated:YES];

}
- (void)viewWillAppear:(BOOL)animated
{
    [ [myCommonAppDelegate navController] setNavigationBarHidden:FALSE];
    [super viewWillAppear:animated];
    
}

- (void) AddItemsListFooter
{
    [[NSBundle mainBundle] loadNibNamed:@"ItemsListTableFooter" owner:self options:nil];
    UIButton *But = (UIButton*)[[self TableFooter] viewWithTag:1];
    [But addTarget:self action:@selector(doBack:) forControlEvents:UIControlEventTouchUpInside];
        
    But = (UIButton*)[[self TableFooter] viewWithTag:2];
    [But addTarget:self action:@selector(doUpdateCarData:) forControlEvents:UIControlEventTouchUpInside];
        
        
    //footerView.userInteractionEnabled = YES;
    self.ItemsList.tableFooterView = self.TableFooter;
    self.ItemsList.tableFooterView.userInteractionEnabled = YES;
    //[ItemsList.tableFooterView setFrame:ViewSize];
        
    
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.ItemsList deselectRowAtIndexPath:[self.ItemsList indexPathForSelectedRow] animated:YES];
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
// TABLE View Delegates

- (void) tableView: (UITableView*)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    ItemListIndexData *Item = [self.ListIndex objectAtIndex:indexPath.row];
    if(nil==Item)
    {
        NSString *Log = [NSString stringWithFormat:@"ItemsList: didSelectRowAtIndexPath Item: %i, Not found In Index (bad)",indexPath.row];
        FC_ALog(@"%@",Log);
        return ;
    }
    if(Item.ItemType==ITEM_LIST_USER_TAG_TYPE)
    {
        fcCarDataViewController *CarView=nil;
        // Let them pick their car Info !!
        if([myCommonAppDelegate isRetina4Display])
        {
            CarView = [[ fcCarDataViewController alloc] initWithNibName:@"fcCarDataViewController_ret4" bundle:nil];
        }
        else
        {
            CarView = [[ fcCarDataViewController alloc] initWithNibName:@"fcCarDataViewController" bundle:nil];
        }
        
        CarView.delegate=self;
        
        [ [myCommonAppDelegate navController] pushViewController:CarView animated:NO];
    }
    else if(Item.ItemType==ITEM_LIST_USER_CREDIT_CARD_TYPE)
    {
        
        fcPaymentMethodViewController *payView=nil;
        
        
        if([myCommonAppDelegate isRetina4Display])
        {
            payView = [[fcPaymentMethodViewController alloc] initWithNibName:@"fcPaymentMethodViewController_ret4" bundle:nil];
        }
        else
        {
            payView = [[fcPaymentMethodViewController alloc] initWithNibName:@"fcPaymentMethodViewController" bundle:nil];
        }
        payView.delegate=self;
        [ [myCommonAppDelegate navController] pushViewController:payView animated:NO];

    }
    else if(Item.ItemType==ITEM_LIST_USER_TIME_TO_LOCATION_TYPE)
    {
        /// WARNING FIXME NO ERROR CHECKS HERE THIS DOES NOT LOOK THAT GREAT !!!!!!
        int TimeTo = [[[myCommonAppDelegate UserInfo] userTimeToLocation] intValue];
        
        fcTimeToLocationViewController *timeView=nil;
        if([myCommonAppDelegate isRetina4Display])
        {
            timeView = [[fcTimeToLocationViewController alloc]
                        initWithNibName:@"fcTimeToLocationViewController_ret4" andInitialTime:TimeTo];

        }
        else
        {
            timeView = [[fcTimeToLocationViewController alloc]
                                                        initWithNibName:@"fcTimeToLocationViewController" andInitialTime:TimeTo];
        }
        
        timeView.delegate=self;
        [ [myCommonAppDelegate navController] pushViewController:timeView animated:NO];
        
    }
    else if(Item.ItemType==ITEM_LIST_CHOOSE_ITEM_TYPE)
    {
        
        // If Order is empty, go right to add
        if([myCommonAppDelegate getCurrentOrder] ==nil)
        {
            // GO right to the Adding page
            fcAddItemViewController *addItemView = [[fcAddItemViewController alloc]
                                                    initWithNibName:@"fcAddItemViewController" bundle:nil];
            
            [ [myCommonAppDelegate navController] pushViewController:addItemView animated:NO];
            addItemView.delegate=self;
        }
        else
        {
            // Item Picker
            [self.navigationItem setBackBarButtonItem: nil]; // He uses his own Left bar button ? THis is ridic
            fcItemPickerViewController *itemView = [[fcItemPickerViewController alloc]
                                                    initWithNibName:@"fcItemPickerViewController" bundle:nil];
            
            [ [myCommonAppDelegate navController] pushViewController:itemView animated:NO];
            itemView.delegate=self;
        }
        
        
    }
    else if(Item.ItemType==ITEM_LIST_USER_DRINK_TYPE)
    {
        NSNumber *itemID = [NSNumber numberWithInt:Item.ItemID];
        
        fcUserItem *userItem = [[myCommonAppDelegate userItems] getUserItemForID:itemID];
        if(nil==userItem)
        {
            return;
        }

        fcAddEditItemViewController *addEditView = [[fcAddEditItemViewController alloc]
                                                    initWithNibName:[myCommonAppDelegate getRet4ControllerName:@"fcAddEditItemViewController"]
                                                    andItemType: [userItem ItemTypeID]
                                                    andUserItemId: itemID
                                                    andIsEdit:TRUE andPopTop: TRUE];
        
        addEditView.delegate=self;
        [ [myCommonAppDelegate navController] pushViewController:addEditView animated:NO];
    }
    else if(Item.ItemType==ITEM_LIST_USER_LOCATION_TYPE)
    {
        fcLocationDetailViewController *locationDetailView = nil;
        if([myCommonAppDelegate isRetina4Display])
        {
                locationDetailView = [[fcLocationDetailViewController alloc]initWithNibName:@"fcLocationDetailViewController_ret4" bundle:nil];
        }
        else
        {
            locationDetailView = [[fcLocationDetailViewController alloc]initWithNibName:@"fcLocationDetailViewController" bundle:nil];
        }
        
                
        [ [myCommonAppDelegate navController] pushViewController:locationDetailView animated:NO];
    }
    else if(Item.ItemType==ITEM_LIST_ORDER_TOTAL_TYPE )
    {
        fcLocation *location = [myCommonAppDelegate getCurrentLocation];
        if(location==nil)
        {
            return;
        }
        fcOrderPriceBreakdownViewController *breakDownController = [[fcOrderPriceBreakdownViewController alloc]
                                                                    initWithNibName:@"fcOrderPriceBreakdownViewController" bundle:nil];
        
        breakDownController.orderTotal=self.orderTotal;
        breakDownController.orderTip=self.orderTip;
        breakDownController.orderTaxableAmount = self.orderTaxableAmount;
        breakDownController.orderItemsTotal=self.orderItemsTotal;
        breakDownController.orderDiscount=self.orderDiscount;
        breakDownController.orderTaxable=self.orderTaxable;
        breakDownController.orderTax = self.orderTax;
        breakDownController.orderConvenienceFee=self.orderConvenienceFee;
        
        // So we can clear our "Table Selection" When the popover is dismissed, viewDidAppear did not seem to be called
        [self.ItemsList reloadData];
        
        WEPopoverController *breakPopover = [[WEPopoverController alloc]initWithContentViewController:breakDownController];
        [breakPopover setPopoverContentSize:CGSizeMake(220, 300)];
        breakDownController.WEcontroller=breakPopover;
        
        [breakPopover presentPopoverFromRect:[self.orderBut frame]
                                             inView:self.view
                                            permittedArrowDirections:UIPopoverArrowDirectionAny 
                                           animated:YES];
        
    }
    
    self.leftMenuViewController = [[fcLeftMenuViewController alloc] init];
    
    self.leftMenuPopover = [[WEPopoverController alloc]initWithContentViewController:self.leftMenuViewController];
    
    [self.leftMenuPopover setPopoverContentSize:CGSizeMake(200, 200)];
    
    self.leftMenuViewController.controller=self.leftMenuPopover;

}




- (NSInteger) numberOfSectionsInTableView:(UITableView*)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView*)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.ListIndex count];
}


- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
    CGFloat optionsHeight =0;
    ItemListIndexData *listItem = [self.ListIndex objectAtIndex:indexPath.row];
    if(listItem.ItemType!=ITEM_LIST_USER_DRINK_TYPE)
    {
        optionsHeight  = ITEMS_LIST_DEFAULT_ROW_HEIGHT;
    }
    else
    {
        optionsHeight = [self getOptionsHeightForRowAtIndexPath:indexPath];
    }
    
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


- (CGFloat) getOptionsHeightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if([self.ListIndex count]==0)
    {
        return ITEMS_LIST_DEFAULT_ROW_HEIGHT;
    }
    ItemListIndexData *listItem = [self.ListIndex objectAtIndex:indexPath.row];
    
    
    if(nil==listItem)
    {
        return ITEMS_LIST_DRINK_NO_OPTIONS_ROW_HEIGHT; // YUCK
    }
   
    NSNumber *userItemId = [NSNumber numberWithInt:listItem.ItemID];
    fcUserItem *userItem = [[myCommonAppDelegate userItems ] getUserItemForID:userItemId];
    
    
    if(nil==userItem)
    {
        return ITEMS_LIST_DRINK_NO_OPTIONS_ROW_HEIGHT; // YUCK
    }
    
    NSMutableString *itemOptions = [NSMutableString stringWithString:userItem.userItemOptionsText];
    
    if( (itemOptions==nil) || ([itemOptions length]==0))
    {
        //return 20;
        return ITEMS_LIST_DRINK_NO_OPTIONS_ROW_HEIGHT;
    }
    
    UIFont *font = [UIFont fontWithName:@"Helvetica" size:10.0];
    
    CGSize optionsSize = [itemOptions sizeWithFont:font constrainedToSize:CGSizeMake(160,4000)];
    if(optionsSize.height>ITEMS_LIST_DEFAULT_ROW_HEIGHT)
    {
        return ITEMS_LIST_DEFAULT_ROW_HEIGHT;
    }
    return optionsSize.height;
}

-(CGFloat) tableView:(UITableView*)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if([self.ListIndex count]==0)
    {
        return ITEMS_LIST_DEFAULT_ROW_HEIGHT;
    }
                           
    ItemListIndexData *Item = [self.ListIndex objectAtIndex:indexPath.row];
    if(nil==Item)
    {
        //NSString *Log = [NSString stringWithFormat:@"ItemsList: heightForRowAtIndexPath Item: %i, Not found In Index (bad)",indexPath.row];
        //FC_Log(@"%@",Log);
        return ITEMS_LIST_DEFAULT_ROW_HEIGHT;
    }
    
    if(Item.ItemType!=ITEM_LIST_USER_DRINK_TYPE)
    {
        return ITEMS_LIST_DEFAULT_ROW_HEIGHT;
    }
    

    
    CGFloat optionsHeight = [self getOptionsHeightForRowAtIndexPath:indexPath];
    
    return optionsHeight+40.0;
}

- (void) UpdateItemsListRowHolderForLocation: (ItemsListTableCellHolder*)Holder withIndex: (NSIndexPath *)indexPath andItem:(ItemListIndexData*) Item
{
    [Holder.LeftImage setImage:[UIImage imageNamed:LOCATION_TABLE_ROW_IMAGE_NAME] ];
    if(Item.ItemID<1)
    {
        [Holder.MainText setText:LOCATION_NONE_TEXT];
        Holder.SmallText.hidden=YES;
        [Holder.MainText setTextColor:[myCommonAppDelegate getNotPopulatedItemColor]];
    }
    else
    {
        
        fcLocation *location = [myCommonAppDelegate getCurrentLocation];
        
        if(location==nil)
        {
            [Holder.MainText setText:LOCATION_NONE_TEXT];
            Holder.SmallText.hidden=YES;
            [Holder.MainText setTextColor:[myCommonAppDelegate getNotPopulatedItemColor]];
        }
        else
        {
            [Holder.MainText setText:[location LocationDescription]];
            [Holder.MainText setTextColor:[myCommonAppDelegate getNormalItemColor]];
            if([location isLongDescrPopulated]==TRUE)
            {
                Holder.SmallText.hidden=NO;
                [Holder.SmallText setText:location.LocationLongDescr];
            }
        }
    
    }
}

- (void) UpdateItemsListRowHolderForCreditCard:(ItemsListTableCellHolder*)Holder withIndex: (NSIndexPath *)indexPath
                                       andItem:(ItemListIndexData*) Item
{
   // [Holder.SmallText setText:@""];
    //[Holder.MainText setTextColor:[UIColor blackColor]];
    //[Holder.SmallText setTextColor:[UIColor blackColor]];
    
    [Holder.LeftImage setImage:[UIImage imageNamed:CREDIT_CARD_TABLE_ROW_IMAGE_NAME]];
    
    NSNumber *currentPayMethod = [[myCommonAppDelegate UserInfo] userPayMethod];
    fcLocation *location = [myCommonAppDelegate getCurrentLocation];

    if([currentPayMethod isEqualToNumber:[NSNumber numberWithInteger:LOCATION_PAY_METHOD_IN_STORE]])
    {
        [Holder.MainText setText:LOCATION_PAY_IN_STORE_STRING];
    }
    else if([currentPayMethod isEqualToNumber:[NSNumber numberWithInteger:LOCATION_PAY_METHOD_IN_APP]])
    {
        NSString *CardID = [[myCommonAppDelegate UserCreditCardInfo] valueForKey:ID_ATTR];
        if( (nil==CardID)  || ([CardID isEqualToString:@""]) || ([CardID isEqualToString:@"0"]))
        {
            [Holder.MainText setText:ADD_A_CREDIT_CARD];
            [Holder.MainText setTextColor:[UIColor redColor]];
        
        }
        else
        {
            NSString *CardDescr = [NSString stringWithFormat:@"%@ - %@ ...(%@)",
                                   LOCATION_PAY_IN_APP_STRING,
                                   [[myCommonAppDelegate UserCreditCardInfo] valueForKey:USER_CREDIT_CARD_DESCR_ATTR],
                                   [[myCommonAppDelegate UserCreditCardInfo] valueForKey:USER_CREDIT_CARD_LAST_FOUR_ATTR]];
            [Holder.MainText setText:CardDescr];
        }
    }
    else
    {
        [Holder.MainText setText:ADD_A_CREDIT_CARD];
        [Holder.MainText setTextColor:[UIColor redColor]];

    }
    if(location!=nil)
    {
        if([location isPaymentMethodAllowed:currentPayMethod]!=TRUE)
        {
            [Holder.MainText setTextColor:[UIColor redColor]];
            Holder.SmallText.hidden=NO;
            [Holder.SmallText setText:LOCATION_DOES_NOT_SUPPORT_THIS_PAYMENT];
        }
        else
        {
            [Holder.MainText setTextColor:[UIColor blackColor]];
            Holder.SmallText.hidden=YES;
        }
    }
}
- (void) doRemoveItemFromOrder:(id)sender
{
    fcTableCellButton *but = (fcTableCellButton*)sender;
    if(nil==sender)
    {
        return;
    }
    
    NSIndexPath *index = but.cellIndex;
    
    ItemListIndexData *item = [self.ListIndex objectAtIndex:index.row];
    
    if(nil==item)
    {
        return;
    }
    
    if(item.ItemType==ITEM_LIST_USER_DRINK_TYPE)
    {
        NSNumber *itemID = [NSNumber numberWithInt:item.ItemID];
        
        [[myCommonAppDelegate getCurrentOrder] removeItemFromOrder:itemID andRemoveAll:FALSE];
    }
    [self CreateListIndex];
    
}
- (void) UpdateItemsListRowHolderForDrink : (ItemsListTableCellHolder*)Holder withIndex: (NSIndexPath *)indexPath
                                   andItem:(ItemListIndexData*) Item
{
    [Holder.LeftImage setImage:[UIImage imageNamed:DRINK_TABLE_ROW_IMAGE_NAME]];
    
    UIImage *RemoveImage = [UIImage imageNamed:REMOVE_BUTTON_TABLE_ROW_IMAGE_NAME];
    
    [Holder.RemoveButton setImage:RemoveImage forState:UIControlStateNormal];
    [Holder.RemoveButton setCellIndex:indexPath];
    [Holder.RemoveButton addTarget:self action:@selector(doRemoveItemFromOrder:)
                 forControlEvents:UIControlEventTouchUpInside];

    
    [Holder.SmallText setHidden:NO];
    [Holder.PriceText setHidden:NO];
    [Holder.RemoveButton setHidden:NO];
    
    // First, look up the drink
    
    NSNumber *itemID = [NSNumber numberWithInt:[Item ItemID] ];
    
    fcUserItem *theItem = [ [myCommonAppDelegate userItems] getUserItemForID: itemID];
    
    if(nil==theItem)
    {
        NSString *Log = [NSString stringWithFormat:@"ItemList: UpdateTableRowFromHolder: Could not find item for ID:%d",
                         [Item ItemID]];
        // Oops TODO
        FC_ALog(@"%@",Log);
        [Holder.MainText setText:INTERNAL_ERROR];
        return;
    }
    
    if( (nil==[theItem userItemName]) || ( [[theItem userItemName] length]==0))
    {
        [Holder.MainText setText: [theItem userItemItemTypeLongDescr]];
    }
    else
    {
        NSString *ItemMainText = [NSString stringWithFormat:@"%@ (%@)",
                                   [theItem userItemItemTypeLongDescr],[theItem userItemName]];
        [Holder.MainText setText:ItemMainText];
    }
    [Holder.SmallText setText: [theItem userItemOptionsText]];
    
    if( (nil==[theItem userItemCost]) || ( [[theItem userItemCost]length]==0))
    {
        [Holder.PriceText setText:@"$0.00"];
    }
    else
    {
        NSString *PriceText = [NSString stringWithFormat:@"$%@",[theItem userItemCost]];
        [Holder.PriceText setText: PriceText];
    }
                                         
}

- (void) UpdateItemsListRowHolderForUserTag: (ItemsListTableCellHolder*)Holder withIndex: (NSIndexPath *)indexPath
                                    andItem:(ItemListIndexData*) Item
{
    
    if([myCommonAppDelegate IsUserWalkup]==TRUE)
    {
        [Holder.MainText setText:@"Walkup"];
        [Holder.LeftImage setImage:[UIImage imageNamed:USER_TAG_ROW_IMAGE_WALKUP_NAME]];
    }
    else
    {
        [Holder.LeftImage setImage:[UIImage imageNamed:USER_TAG_ROW_IMAGE_CAR_NAME] ];
        NSString *MakeModelColorLong = [[myCommonAppDelegate UserCarInfo] valueForKey:USER_CAR_DESCR_LONG_ATTR];
        if(nil==MakeModelColorLong)
        {
            MakeModelColorLong=@"";
        }
    
        
        NSString *ExtraTag=[[myCommonAppDelegate UserInfo] userTag];
        /*
        if( (nil==ExtraTag)|| ( [ExtraTag length]==0))
        {
            ExtraTag = NONE_TEXT;
        }
         */
        NSString *TagStr = nil;
        if ( (nil==MakeModelColorLong) || ([MakeModelColorLong length]==0) )
        {
            if( (nil==ExtraTag) || ([ExtraTag length]==0))
            {
                // Both strings empty
                TagStr = SET_ARRIVAL_MODE_TEXT;
                
            }
            else
            {
                TagStr = ExtraTag;
            }
        }
        else
        {
            // No Extra
            if( (nil==ExtraTag) || ([ExtraTag length]==0))
            {
                
                TagStr = MakeModelColorLong;
            }
            else
            {
                // Both set
                TagStr = [NSString stringWithFormat:@"%@ - %@",MakeModelColorLong,ExtraTag];
            }
            
        }
        [Holder.MainText setText:TagStr];
    }
    
    NSNumber *arrivalMode = [[myCommonAppDelegate UserInfo] userArriveMode];
    fcLocation *location = [myCommonAppDelegate getCurrentLocation];
        
    if(location!=nil)
    {
        if([location isArrivalMethodAllowed:arrivalMode]!=TRUE)
        {
            [Holder.MainText setTextColor:[UIColor redColor]];
            [Holder.SmallText setHidden:NO];
            [Holder.SmallText setText:ARRIVAL_MODE_NOT_ALLOWED_STRING];
        }
    }
}

- (void) UpdateItemsListRowHolderForTimeToLocation: (ItemsListTableCellHolder*)Holder withIndex: (NSIndexPath *)indexPath
                                           andItem:(ItemListIndexData*) Item
{
    [Holder.LeftImage setImage:[UIImage imageNamed:TIME_TO_ARRIVE_TABLE_ROW_IMAGE_NAME] ];
    
    NSString *TimeToLocation = SET_TIME_TO_ARRIVE;
    
    NSNumber *timeToLocation = [[myCommonAppDelegate UserInfo] userTimeToLocation];
    if(timeToLocation==nil)
    {
        [Holder.MainText setTextColor:[myCommonAppDelegate getNotPopulatedItemColor]];
    }
    else
    {
        TimeToLocation = [NSString stringWithFormat:@"%@ %@ mins",READY_IN_TEXT,timeToLocation];
        [Holder.MainText setTextColor:[myCommonAppDelegate getNormalItemColor]];
    }
    [Holder.MainText setText:TimeToLocation];
}

- (void) updateOrderTotalAndDiscountAndTipAndTax
{
    
    self.orderTotal=[NSDecimalNumber zero];
    self.orderTip=[NSDecimalNumber zero];
    self.orderDiscount=[NSDecimalNumber zero];
    self.orderFreeDueToDiscounts=FALSE;
    self.orderTax=[NSDecimalNumber zero];
    self.orderTaxable=FALSE;
    self.orderTaxableAmount=[NSDecimalNumber zero];
    
    self.orderItemsTotal = [[myCommonAppDelegate getCurrentOrder] getOrderTotalCost];
    // Check for Free Drinks
    NSNumber *userFreeDrinks = [[myCommonAppDelegate UserInfo] userFreeDrinksCount];
    
    fcLocation *location = [myCommonAppDelegate getCurrentLocation];
    if(location==nil)
    {
        self.orderTotal=self.orderItemsTotal;
        self.orderDiscount=[NSDecimalNumber zero];
        self.orderFreeDueToDiscounts=FALSE;

        return;
    }
    if([location isLocationNone]==TRUE)
    {
        self.orderTotal=self.orderItemsTotal;
        self.orderDiscount=[NSDecimalNumber zero];
        self.orderFreeDueToDiscounts=FALSE;

        return;
    }

    if(nil==userFreeDrinks)
    {
        self.orderTotal=self.orderItemsTotal;
        self.orderDiscount=[NSDecimalNumber zero];
        self.orderFreeDueToDiscounts=FALSE;
    }
    else
    {
        
        if([userFreeDrinks compare:[NSNumber numberWithInt:0]]==NSOrderedDescending)
        {
            // NumFree Drinks is bigger than zero !
            // Check App settings first for discount
            NSDecimalNumber *amountFree=nil;
            NSString *appSettingOrderDiscountAmt = [[myCommonAppDelegate appSettings] tryGetsettingAsString:APP_SETTING_DEFAULT_FREE_DRINK_DISCOUNT_AMT];
            if(nil!=appSettingOrderDiscountAmt)
            {
                amountFree = [NSDecimalNumber decimalNumberWithString:appSettingOrderDiscountAmt];
            }
            else
            {
                amountFree = [NSDecimalNumber decimalNumberWithString:FREE_DRINK_ALT_AMOUNT];
            }
            if([self.orderItemsTotal compare:amountFree ]==NSOrderedDescending)
            {
                // Order Total greater than the free amount
                self.orderDiscount = amountFree;
                self.orderTotal = [self.orderItemsTotal decimalNumberBySubtracting:amountFree];
            }
            else
            {
                self.orderDiscount = self.orderItemsTotal;
                self.orderTotal = [NSDecimalNumber zero];
                self.orderFreeDueToDiscounts=TRUE;
            }
        }
        else
        {
            self.orderTotal=self.orderItemsTotal;
            self.orderDiscount=[NSDecimalNumber zero];
            self.orderFreeDueToDiscounts=FALSE;
        }

    }
    //NSString *LocationID = [[myCommonAppDelegate UserLocationInfo] objectForKey:ID_ATTR];
    
    
    
    // NOTE: TIPS will be based on Menu eventually !
    // Calc tip
    
    fcUserTip *tip = [[myCommonAppDelegate userTips] getTipForLocation:location.LocationID];
    // Tip is before discounts
            
    if(tip!=nil)
    {
        self.orderTip = [tip calculateTipDollarAmount:self.orderItemsTotal];
        self.orderTotal =[self.orderTotal decimalNumberByAdding:self.orderTip];
    }
    self.orderTaxable= [location IsOrderTaxable];
    
    self.orderTaxableAmount= [self.orderItemsTotal decimalNumberBySubtracting:self.orderDiscount];
    
    FC_Log(@"Taxable Amt1: %@\n",self.orderTaxableAmount);
    
    self.orderConvenienceFee = [location CalculateConvenienceFeeForPayMethod:[[myCommonAppDelegate UserInfo] userPayMethod ]];
    
    self.orderTaxableAmount= [self.orderTaxableAmount  decimalNumberByAdding:self.orderConvenienceFee];
    self.orderTotal = [self.orderTotal decimalNumberByAdding:self.orderConvenienceFee];
    
    FC_Log(@"Taxable Amt2: %@\n",self.orderTaxableAmount);
    
    self.orderTax=[location CalculateSalesTax:self.orderTaxableAmount];
    
    FC_Log(@"Tax: %@\n",self.orderTax);

    self.orderTotal = [self.orderTotal decimalNumberByAdding:self.orderTax];
}

- (void) UpdateItemsUpdateItemsListRowHolderForOrderTotal: (ItemsListTableCellHolder*)Holder withIndex: (NSIndexPath *)indexPath
                                           andItem:(ItemListIndexData*) Item
{
    
    
    NSMutableString *displayStr = [NSMutableString stringWithString:@""];
    
    // SOOPER UGLY FIXME YESTERDAY
    if([BASE_URL isEqualToString:@"https://freecoffapp.com/fc/"])
    {
        BOOL isDemo = [[myCommonAppDelegate UserInfo] userIsDemo];
        
        if( isDemo )
        {
            [displayStr appendFormat:@"Demo Acct:"];
        }
        else
        {
            [displayStr appendFormat:@"Total Charge:"];
            
        }
    }
    else
    {
        [displayStr appendFormat:@"TEST APP: "];
    }
    
    
    [Holder.RemoveButton setCellIndex:indexPath];
    [Holder.RemoveButton addTarget:self action:@selector(doSetUserTip:)
                  forControlEvents:UIControlEventTouchUpInside];
    [Holder.SmallText setHidden:NO];
    [Holder.PriceText setHidden:NO];
    
    [Holder.RemoveButton setHidden:NO];
    UIImage *RemoveImage=nil;
    
    BOOL showRedTipIconIfNoTip = [[myCommonAppDelegate appSettings] tryGetSettingAsBOOL:APP_SETTING_NAME_ZERO_TIP_SHOW_RED_ICON];
    /*
    BOOL showTipTextItemsList = [[myCommonAppDelegate appSettings] tryGetSettingAsBOOL:APP_SETTING_NAME_SHOW_TIP_TEXT_ITEMS_LIST];
    
    
    NSString *tipString = [NSString stringWithFormat:@"%.2f",[self.orderTip doubleValue]];
      */      
    
    /*
    if(showTipTextItemsList)
    {
        [Holder.SmallText setText: [NSString stringWithFormat:@"Incl Tip: $%@",tipString]];
    }
    else
    {
        [Holder.SmallText setHidden:YES];
    }
     */
    
    Holder.SmallText.text=@"Touch for details";
    
    // if zero is "less than" tipAmount
    if([self.orderTip compare: [NSDecimalNumber zero] ]==NSOrderedDescending)
    {
        // Tip Greater than zero
        RemoveImage = [UIImage imageNamed:TIP_IMAGE_NAME];
        //[Holder.RemoveButton setImage:RemoveImage forState:UIControlStateNormal];
    }
    else
    {
        if(showRedTipIconIfNoTip==TRUE)
        {
            RemoveImage = [UIImage imageNamed:TIP_NONE_IMAGE_NAME];
        }
        else
        {
            RemoveImage = [UIImage imageNamed:TIP_IMAGE_NAME];
        }
    }
    
    if(RemoveImage!=nil)
    {
        [Holder.RemoveButton setImage:RemoveImage forState:UIControlStateNormal];
    }
    
    
    if([self.orderDiscount compare:[NSDecimalNumber zero]]!=NSOrderedSame)
    {
        [Holder.PriceText setTextColor:[UIColor greenColor]];
        
        [Holder.PriceText setText:[NSString stringWithFormat:@"Discount: %@",
                                    [[myCommonAppDelegate getCurrencyFormatter] stringFromNumber:self.orderDiscount]]];
    }
    else
    {
        [Holder.PriceText setText:@""];
    }
    
    
    
    //NSString *orderTotalString = [NSString stringWithFormat:@"%.2f",[self.orderTotal doubleValue]];
    [displayStr appendFormat:@"%@",[ [myCommonAppDelegate getCurrencyFormatter] stringFromNumber:self.orderTotal ]];
    [Holder.MainText setText:displayStr];
    [Holder.LeftImage setImage:[UIImage imageNamed:ORDER_TOTAL_TABLE_ROW_IMAGE_NAME] ];
    
    
}
- (void) UpdateTableRowFromHolder:(ItemsListTableCellHolder*)Holder withIndex:(NSIndexPath *)indexPath
{
    ItemListIndexData *Item = [self.ListIndex objectAtIndex:indexPath.row];
    if(nil==Item)
    {
        NSString *Log = [NSString stringWithFormat:@"ItemsList: UpdateTableRowFromHolder Item: %i, Not found In Index (bad)",indexPath.row];
        FC_ALog(@"%@",Log);
        return;
    }
    NSString *Log=nil;
    
    // For all non-drink, hide the price, small text and remove button items (drink will change the settings to show)
    [Holder.SmallText setHidden:YES];
    [Holder.PriceText setHidden:YES];
    [Holder.RemoveButton setHidden:YES];
    [Holder.MainText setTextColor:[UIColor blackColor]];
    [Holder.PriceText setTextColor:[UIColor blackColor]];
    [Holder.SmallText setTextColor:[UIColor blackColor]];
    switch(Item.ItemType)
    {
        case ITEM_LIST_USER_LOCATION_TYPE:
            [self UpdateItemsListRowHolderForLocation: Holder withIndex:indexPath andItem:Item];
            break;
        case ITEM_LIST_USER_CREDIT_CARD_TYPE:
            [self UpdateItemsListRowHolderForCreditCard: Holder withIndex:indexPath andItem:Item];
            
            break;
        case ITEM_LIST_USER_FOOD_TYPE:
            [Holder.LeftImage setImage:[UIImage imageNamed:FOOD_TABLE_ROW_IMAGE_NAME] ];
            break;
        case ITEM_LIST_USER_DRINK_TYPE:
            [self UpdateItemsListRowHolderForDrink : Holder withIndex:indexPath andItem:Item];
            break;
        case ITEM_LIST_CHOOSE_ITEM_TYPE:
            [Holder.LeftImage setImage:[UIImage imageNamed:ADD_EDIT_ITEMS_ROW_IMAGE_NAME]];
            [Holder.MainText setText:CHOOSE_AN_ITEM_TEXT];
            break;
        case ITEM_LIST_USER_TAG_TYPE:
            [self UpdateItemsListRowHolderForUserTag: Holder withIndex:indexPath andItem:Item];
            break;
        case ITEM_LIST_USER_TIME_TO_LOCATION_TYPE:
            [self UpdateItemsListRowHolderForTimeToLocation: Holder withIndex:indexPath andItem:Item];
            break;
        case  ITEM_LIST_ORDER_TOTAL_TYPE:
            [self UpdateItemsUpdateItemsListRowHolderForOrderTotal: Holder withIndex:indexPath andItem:Item];
            break;
        default:
            // TODO
            Log = [NSString stringWithFormat:@"ItemList: UpdateTableRowFromHolder Fell through case with Item Type: %i",Item.ItemType];
            FC_Log(@"%@",Log);
            
            [Holder.MainText setText:@"Bad"];
            break;
    }
    
}

- (UITableViewCell*)tableView:(UITableView*)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"ItemListCell"; // MUST Match the NIB !!!
    static NSString *CellIdentifierNonDrink = @"ItemsListTableCellNonDrink";
    
    ItemListIndexData *Item = [self.ListIndex objectAtIndex:indexPath.row];
    if(nil==Item)
    {
        NSString *Log = [NSString stringWithFormat:@"ItemsList: UpdateTableRowFromHolder Item: %i, Not found In Index (bad)",indexPath.row];
        FC_ALog(@"%@",Log);
        return nil;
    }

    UITableViewCell *Cell = nil;
    if(Item.ItemType==ITEM_LIST_USER_DRINK_TYPE)
    {
       Cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
        if(nil==Cell)
        {
            // Got to load one
            UINib *theCellNib = [UINib nibWithNibName:@"ItemsListTableCell" bundle:nil];
            [theCellNib instantiateWithOwner:self options:nil];
            Cell = [self ItemListTableCell];
        
        }
        else
        {
            // Test to make sure reuse happens sometimes at least.
        }
    }
    else
    {
        Cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifierNonDrink];
        if(nil==Cell)
        {
            // Got to load one
            UINib *theCellNib = [UINib nibWithNibName:@"ItemsListTableCellNonDrink" bundle:nil];
            [theCellNib instantiateWithOwner:self options:nil];
            Cell = [self ItemListTableCell];
            
        }
        else
        {
            // Test to make sure reuse happens sometimes at leaste.
        }

    }
    ItemsListTableCellHolder *Holder = [[ItemsListTableCellHolder alloc] init];
    
    Holder.LeftImage = (UIImageView*)[Cell viewWithTag:1];
    Holder.MainText = (UILabel*) [Cell viewWithTag:2];
    Holder.SmallText= (UILabel*)[Cell viewWithTag:3];
    Holder.PriceText=(UILabel*)[Cell viewWithTag:4];
    Holder.RemoveButton=(fcTableCellButton*)[Cell viewWithTag:5];
    
    [self UpdateTableRowFromHolder:Holder withIndex:indexPath];
    
    return Cell;
}



- (void) CreateListIndex
{
    [self setListIndex:[[NSMutableArray alloc] init]];
    
    [self.ListIndex removeAllObjects];
    
    ItemListIndexData *ListItem  = nil;
    // Add drinks first
    //fcUserItemTable *userItemTable =
    
    fcItemOrderList *currentOrder = [myCommonAppDelegate getCurrentOrder];
    
    for(int index=0;index < [currentOrder count];index++)
    {
        // Should probably check it's a real item by looking it up. Later
        NSNumber *item = [currentOrder getItemIDForIndex:index];
        if(nil!=item)
        {
            ListItem  = [[ItemListIndexData alloc] init];
            ListItem.ItemType=ITEM_LIST_USER_DRINK_TYPE;
            ListItem.ItemID=[item integerValue];
            [self.ListIndex addObject:ListItem];
        }
    }
      
    // the "Add an Item" entry
    ListItem  = [[ItemListIndexData alloc] init];
    ListItem.ItemType=ITEM_LIST_CHOOSE_ITEM_TYPE;
    ListItem.ItemID=-1;
    [self.ListIndex addObject:ListItem];
   
    
    // Location
    ListItem  = [[ItemListIndexData alloc] init];
    ListItem.ItemType = ITEM_LIST_USER_LOCATION_TYPE;
    

    
    NSNumber *userLocationID = [[myCommonAppDelegate UserInfo] userLocationID];
    ListItem.ItemID = [userLocationID integerValue];
                           
    [self.ListIndex addObject:ListItem];
    
    // Credit Card
    ListItem  = [[ItemListIndexData alloc] init];
    ListItem.ItemType = ITEM_LIST_USER_CREDIT_CARD_TYPE;
    ListItem.ItemID=-1;
    // See if there is a card
    NSDictionary *CardData = [myCommonAppDelegate UserCreditCardInfo];
    
    if( [CardData count]!=0)
    {
        NSString *CardID = [CardData objectForKey:ID_ATTR];
        if([[NSScanner scannerWithString:CardID] scanInt:nil])
        {
            ListItem.ItemID = [CardID integerValue];
            
        }
        else
        {
            // TODO
            FC_Log(@"%@",[NSString stringWithFormat:@"CardID[%@] is not an Integer.",CardID] );
        }
        
    }
    // Always add the credit card even if it was not in the data (so the user can press the item to add a card later)
    [self.ListIndex addObject:ListItem];
   
    
    // Car Data
    ListItem  = [[ItemListIndexData alloc] init];
    ListItem.ItemType = ITEM_LIST_USER_TAG_TYPE;
    ListItem.ItemID=-1;
    [self.ListIndex addObject:ListItem];
    
    
    
    // Time to Location
    ListItem  = [[ItemListIndexData alloc] init];
    ListItem.ItemType=ITEM_LIST_USER_TIME_TO_LOCATION_TYPE;
    ListItem.ItemID=-1;
    [self.ListIndex addObject:ListItem];
  
    // Order Total
    
    ListItem  = [[ItemListIndexData alloc] init];
    ListItem.ItemType=ITEM_LIST_ORDER_TOTAL_TYPE;
    ListItem.ItemID=-1;
    [self.ListIndex addObject:ListItem];
    
    //NSString *WelcomeString = [NSString stringWithFormat:@"%@'s Order",[myCommonAppDelegate getName]];
    self.title = @"Your order";
   
    [self updateOrderTotalAndDiscountAndTipAndTax];
    [self.ItemsList reloadData];
}

- (BOOL) validateOrder
{
   
        fcLocation *location = [myCommonAppDelegate getCurrentLocation];
    
    if( ([[myCommonAppDelegate UserInfo ] isLocationSet]!=TRUE) || (location==nil))
    {
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: @"Error"
                                   message: @"You must set a location before you order."
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        alert.tag=ITEMS_LIST_ALERT_TAG_DO_NOTHING;
        [alert show];
        
        return FALSE;
    }
    
    if( ([myCommonAppDelegate getCurrentOrder]==nil) || [[myCommonAppDelegate getCurrentOrder] isOrderEmpty])
    {
     
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: @"Error"
                                   message: @"You must add an item to your order."
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        alert.tag=ITEMS_LIST_ALERT_TAG_DO_NOTHING;
        [alert show];
        
        return FALSE;

    }
    
    
    NSNumber *arrivalMode = [[myCommonAppDelegate UserInfo] userArriveMode];
    
    NSNumber *payMethod = [[myCommonAppDelegate UserInfo] userPayMethod];
    
    
    // Check method of payment is supported.
    
    if([location isPaymentMethodAllowed:payMethod]!=TRUE)
    {
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: @"Error"
                                   message: @"Your method of payment is not supported at this location, pick another."
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        alert.tag=ITEMS_LIST_ALERT_TAG_DO_NOTHING;
        [alert show];
        
        return FALSE;
    }
    
    
    if([location isArrivalMethodAllowed:arrivalMode]!=TRUE)
    {
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: @"Error"
                                   message: @"Your mode of arrival is not supported at this location, pick another."
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        alert.tag=ITEMS_LIST_ALERT_TAG_DO_NOTHING;
        [alert show];
        
        return FALSE;
    }

    
    if ( ([self.orderTotal compare:[NSNumber numberWithDouble:0.00]]== NSOrderedSame) && (!self.orderFreeDueToDiscounts))
    {
		UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: @"Error"
                                   message: @"You cannot have a zero value order."
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        alert.tag=ITEMS_LIST_ALERT_TAG_DO_NOTHING;
        [alert show];
    
        return FALSE;
    }

    return TRUE;
    
}

- (void) doMakeOrder:(id)sender
{
    
    self.showLastOrder=TRUE;
    
    if([myCommonAppDelegate networkUp]!=TRUE)
    {
        [[myCommonAppDelegate topController] showNetworkDownToast:self.view];
        return;
    }
    
    if([self validateOrder]!=TRUE)
    {
        return;
    }
    
    
    
    NSString *itemIDList = [XMLHelper URLEncodedString:[[myCommonAppDelegate getCurrentOrder] getOrderAsIDList]];
    
    NSString *arriveMode=nil;
    
    if([myCommonAppDelegate IsUserWalkup]==TRUE)
    {
        arriveMode = ARRIVE_MODE_WALKUP_STR;
    }
    else
    {
        arriveMode = ARRIVE_MODE_CAR_STR;
    }
    self.processingOrder=TRUE;
    
    NSNumber *locationIncarnation=nil;
    fcLocation *location = [myCommonAppDelegate getCurrentLocation];
    if(location!=nil)
    {
        locationIncarnation = [location incarnation];
    }
    
    NSMutableString *URLString = [NSMutableString stringWithFormat:@"%@%@?%@=%@&%@=%@&%@=%@&%@=%@&%@=%@",
                           BASE_URL,ORDER_PAGE,
                           USER_COMMAND_CMD,MAKE_ORDER_COMMAND,
                           APP_CLIENT_TYPE,[XMLHelper URLEncodedString:APP_CLIENT_VALUE_IOS ],
                           USER_ARRIVE_MODE_CMD_ARG,arriveMode,
                           ORDER_ITEMS_LIST,itemIDList,
                           USER_INCARNATION_CMD_ARG,[[myCommonAppDelegate UserInfo] userIncarnation]];
    
    if(locationIncarnation!=nil)
    {
        [URLString appendFormat:@"&%@=%@",USER_LOCATION_INCARNATION_CMD_ARG,locationIncarnation];
    }
    
    FC_Log(@"%@",URLString);
    
    NSURLRequest *theRequest=[NSURLRequest requestWithURL:[NSURL URLWithString:URLString]
                              
                                              cachePolicy:NSURLRequestUseProtocolCachePolicy
                              
                                          timeoutInterval:60.0];
    self.conRequest = [[NSURLConnection alloc] initWithRequest:theRequest delegate:self];
    
    HUD = [MBProgressHUD showHUDAddedTo:self.view animated:NO] ;
    HUD.dimBackground = YES;
    HUD.labelText=PROGRESS_MAKE_ORDER_MESSAGE;
    HUD.delegate=self;
    
}

- (void) doGetItemList:(BOOL)showLast
{

    self.showLastOrder=showLast;
    NSString *URLString = [NSString stringWithFormat:@"%@%@?%@=%@",BASE_URL,USER_PAGE,USER_COMMAND_CMD,GET_USER_ITEMS_CMD];
    self.processingOrder=FALSE;
    NSURLRequest *theRequest=[NSURLRequest requestWithURL:[NSURL URLWithString:URLString]
                              
                                              cachePolicy:NSURLRequestUseProtocolCachePolicy
                              
                                          timeoutInterval:60.0];
    self.conRequest = [[NSURLConnection alloc] initWithRequest:theRequest delegate:self];
    
    HUD = [MBProgressHUD showHUDAddedTo:self.view animated:NO] ;
    HUD.dimBackground = YES;
    HUD.labelText=PROGRESS_DOWNLOAD_ITEMS_MESSAGE;
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
 
    [HUD hide:YES afterDelay:HUD_HIDE_DELAY_DEFAULT];
}


- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    self.conRequest=nil;
    
    // Once this method is invoked, "responseData" contains the complete result
    [HUD hide:YES afterDelay:HUD_HIDE_DELAY_DEFAULT];
    
    self.itemsListXMLParser = [[fcXMLParserItemsList alloc]init];
    
    NSXMLParser *parser = [[NSXMLParser alloc] initWithData:responseData];
    [parser setDelegate: self.itemsListXMLParser];
    
    [parser setShouldProcessNamespaces:YES];
#ifdef FC_DO_LOG
     NSString* newStr = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
     FC_Log(@"%@",newStr);
#endif
    
    if([parser parse]!=YES)
    {
        // TODO ERROR
    }
    
    
    if(self.processingOrder==TRUE)
    {
        if(self.itemsListXMLParser.signonError==TRUE)
        {
            [[myCommonAppDelegate navController] popToRootViewControllerAnimated:NO];
            return;
        }
        
        if( (self.itemsListXMLParser.parseSuccessful==TRUE))
        {
            if(self.itemsListXMLParser.wasServerResponseGood!=TRUE)
            {
                if( (self.itemsListXMLParser.orderObjResponse!=nil) && (self.itemsListXMLParser.orderObjResponse.objectStatus==EnumServerOjbectResponseObjectStatusNeedNew))
                {
                    NSString *errorText = [fcLastOrder makeErrorText];
                    if((nil==errorText) || ([errorText length]==0))
                    {
                    
                        errorText = @"Unknown Error";
                    }
                
                    UIAlertView *alert =
                    [[UIAlertView alloc] initWithTitle: @"Order Error"
                                        message: errorText
                                        delegate: self
                                        cancelButtonTitle: @"OK"
                                        otherButtonTitles: nil];
                                        alert.tag=ITEMS_LIST_ALERT_TAG_DO_REFRESH;
                                        [alert show];
                                        self.itemsListXMLParser=nil;
                    return;
        
                }
                else
                {
                    //Error
                    NSString *errorText = [fcLastOrder makeErrorText];
                    if((nil==errorText) || ([errorText length]==0))
                    {
                        
                        errorText = @"Unknown Error";
                    }
                    
                    UIAlertView *alert =
                    [[UIAlertView alloc] initWithTitle: @"Order Error"
                                               message: errorText
                                              delegate: self
                                     cancelButtonTitle: @"OK"
                                     otherButtonTitles: nil];
                    alert.tag = ITEMS_LIST_ALERT_TAG_DO_NOTHING;
                    [alert show];
                    self.itemsListXMLParser=nil;
                    return;

                }
            }
            
            [self updateOrderTotalAndDiscountAndTipAndTax]; // Free drinks may have changed.
            
            [myCommonAppDelegate updateLastOrderStatus:self.itemsListXMLParser.lastOrder];
            
            // Open the next page
            // Actions like "Refresh" just dont want to show the last "Not yet here" order even if it exists
            if(self.showLastOrder==TRUE)
            {
                [[myCommonAppDelegate lastOrder] setOrderStatus:ORDER_SUBMITTED];
            
                fcOrderResponseViewController *orderView = nil;
                orderView = [[fcOrderResponseViewController alloc]
                             initWithNibName:[myCommonAppDelegate getRet4ControllerName:@"fcOrderResponseViewController" ] bundle:nil];
                
                orderView.delegate=self;
                [ [myCommonAppDelegate navController] pushViewController:orderView animated:NO];
            }
            
        }
        else
        {
            
            
            //Error
            NSString *errorText = [fcLastOrder makeErrorText];
            if((nil==errorText) || ([errorText length]==0))
            {
                
                errorText = @"Unknown Error";
            }
            
            UIAlertView *alert =
            [[UIAlertView alloc] initWithTitle: @"Order Error"
                                       message: errorText
                                      delegate: self
                             cancelButtonTitle: @"OK"
                             otherButtonTitles: nil];
            alert.tag = ITEMS_LIST_ALERT_TAG_DO_NOTHING;
            [alert show];
            self.itemsListXMLParser=nil;
            return;
            
        }
    }
    else
    {
        [self updateGlobalStateFromGetItemsList];
        [myCommonAppDelegate updateLastOrderStatus:self.itemsListXMLParser.lastOrder];
        // Getting Items List
        [myCommonAppDelegate generateCurrentDefaultOrderIfEmpty];
        [[myCommonAppDelegate orderLists ] reconcile];
        [self CreateListIndex];
        
        // Actions like "Refresh" just dont want to show the last "Not yet here" order even if it exists
        if(self.showLastOrder==TRUE)
        {
            if( ([[myCommonAppDelegate lastOrder] doesOrderExist]==TRUE) &&
               ([[myCommonAppDelegate lastOrder] orderStatus]!=ORDER_HERE_OK))
            {
                fcOrderResponseViewController *orderView = nil;
            
                if([myCommonAppDelegate isRetina4Display])
                {
                    orderView = [[fcOrderResponseViewController alloc]
                                initWithNibName:@"fcOrderResponseViewController_ret4" bundle:nil];
                }
                else
                {
                    orderView = [[fcOrderResponseViewController alloc]
                                initWithNibName:@"fcOrderResponseViewController" bundle:nil];
                }
                orderView.delegate=self;
            
                [ [myCommonAppDelegate navController] pushViewController:orderView animated:NO];
            }
        }
        
    }
    
    self.itemsListXMLParser=nil;
    
}

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex
{
    if (alertView.tag ==ITEMS_LIST_ALERT_TAG_DO_REFRESH)
    {
        [self doRefresh];
    }
}

- (void) updateGlobalStateFromGetItemsList
{
    if(self.itemsListXMLParser==nil)
    {
        return;
    }
    
    [myCommonAppDelegate setUserInfo: [self.itemsListXMLParser userInfo]];
    
    [myCommonAppDelegate mergeLocationData:[self.itemsListXMLParser locations] ];


}
- (void) childFinished
{
    [self updateOrderTotalAndDiscountAndTipAndTax];
   [self.ItemsList reloadData];
    
}
- (void) childFinishedCommit
{
    [self CreateListIndex];
    
}
- (IBAction) doFeedback: (id) sender
{
    [myCommonAppDelegate openFeedbackEmailClient];
}


- (void)dealloc
{
    [HUD removeFromSuperview];
    
}
#pragma mark MBProgressHUDDelegate methods

- (void)hudWasHidden:(MBProgressHUD *)hud
{
    
    // Remove HUD from screen when the HUD was hidded
    [HUD removeFromSuperview];
	HUD = nil;
    
}

- (void) doSetUserTip:(id) sender
{
    fcUserTipsViewController *tipsView = [[ fcUserTipsViewController alloc] initWithNibName:@"fcUserTipsViewController" bundle:nil];
    tipsView.delegate=self;
    
    [ [myCommonAppDelegate navController] pushViewController:tipsView animated:NO];
    
}
@end
