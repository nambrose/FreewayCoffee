//
//  fcAddItemViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>
#import "fcAddItemViewController.h"
#import "fcAddItemIndexData.h"
#import "fcItemPickerViewController.h"
#import "fcAddEditItemViewController.h"
#import "XMLHelper.h"
#import "XMLParserMenu.h"
#import "fcItemTypeTable.h"
#import "fcAppDelegate.h"
#import "fcItemType.h"
#import "fcMenu.h"
#import "fcRootViewController.h"
#import "fcMenu.h"
#import "fcMenuTable.h"
#import "fcServerObjectResponse.h"

@interface fcAddItemViewController ()

@end

@implementation fcAddItemViewController

@synthesize itemsList=_itemsList;
@synthesize listIndex=_listIndex;
@synthesize delegate=_delegate;
@synthesize conRequest=_conRequest;
@synthesize menu=_menu;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        // Custom initialization
        _listIndex = [[NSMutableArray alloc]init];
        _menu = [myCommonAppDelegate getMenuForCurrentLocation];

    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    /*NSString *WelcomeString = [NSString stringWithFormat:@"%@",
                               @"Select a drink type"];
     */
    responseData = [[NSMutableData alloc] init ];
    
    UIColor *background = [[UIColor alloc] initWithPatternImage:
                           [UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
    /*
    if (!self.itemsList.tableFooterView)
    {
        [self AddItemsPickListFooter ];
    }
    */
    
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@"Drinks" style:UIBarButtonItemStyleBordered target:self action:nil];
    [self.navigationItem setBackBarButtonItem: backButton];
     self.itemsList.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    
    self.menu= [myCommonAppDelegate getMenuForCurrentLocation];
    if(self.menu==nil)
    {
        self.title = CHECK_MENU_MESSAGE;
        [self doDownloadItemData];
    }
    else
    {
        [self createListIndex];
    }
    
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
    
    [HUD hide:YES afterDelay:HUD_HIDE_DELAY_DEFAULT];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    // Once this method is invoked, "responseData" contains the complete result
    [HUD hide:YES afterDelay:HUD_HIDE_DELAY_SHORT];
    
    NSXMLParser *parser = [[NSXMLParser alloc] initWithData:responseData];
    //NSString *strData = [[NSString alloc]initWithData:responseData encoding:NSUTF8StringEncoding];
    
    XMLParserMenu *menuXMLParser = [[XMLParserMenu alloc] init];
    
    [parser setDelegate: menuXMLParser];
    
    [parser setShouldProcessNamespaces:YES];
#ifdef FC_DO_LOG
    NSString* newStr = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
    FC_Log(@"Menu: %@",newStr);
#endif
    
    if([parser parse]!=YES)
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
    }
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
    [self createListIndex];
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
    fcAddItemIndexData *itemData = [self.listIndex objectAtIndex:[indexPath row]];
    if(nil==itemData)
    {
        NSString *Log = [NSString stringWithFormat:@"CarData: didSelectRowAtIndexPath Item: %i, Not found In Index (bad)",indexPath.row];
        FC_ALog(@"%@",Log);
        return ;
    }
    fcAddEditItemViewController *addEditView = [[fcAddEditItemViewController alloc]
                                                initWithNibName:[myCommonAppDelegate getRet4ControllerName:@"fcAddEditItemViewController"]
                                                andItemType: [itemData itemID]
                                                andUserItemId: [NSNumber numberWithInt:-1]
                                                andIsEdit:FALSE andPopTop: TRUE];
    
    addEditView.delegate=self;
    [ [myCommonAppDelegate navController] pushViewController:addEditView animated:NO];
    

    
}

- (UITableViewCell*)tableView:(UITableView*)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"fcAddItemTableCell";
    
    fcAddItemIndexData *itemData = [self.listIndex objectAtIndex:[indexPath row]];
    
    UITableViewCell *Cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if(nil==Cell)
    {
        Cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:CellIdentifier] ;
    }
    else
    {
        // Test to make sure reuse happens sometimes at leaste.
    }
    if(nil==itemData)
    {
        [Cell.textLabel setText:@"Unknown"];
        [Cell.detailTextLabel setText:@""];
        Cell.accessoryType=UITableViewCellAccessoryNone;
    }
    else
    {
        fcItemType *itemType = [self.menu findItemTypeByID:[itemData itemID]];
        [Cell.textLabel setText: [itemType itemTypeName]];
        [Cell.detailTextLabel setText:[itemType itemTypeText]];
        [Cell.detailTextLabel setFont:[UIFont systemFontOfSize:10]];
        
        Cell.accessoryType=UITableViewCellAccessoryDisclosureIndicator;
    }
        
    return Cell;
 
}


- (void) createListIndex
{
    self.title=@"Select a drink type";
    fcAddItemIndexData *listItem  = nil;
    [self.listIndex removeAllObjects];
    
    fcItemTypeTable *typeTable = [self.menu itemTypeTable];
    
    for( NSNumber *aKey in [ [typeTable  itemTypes] allKeys] )
    {
        // do something
        fcItemType *item = [[typeTable  itemTypes] objectForKey:aKey] ;
        if(nil!=item)
        {
            listItem = [[fcAddItemIndexData alloc]init];
            listItem.itemID = [item itemTypeID];
            listItem.sortOrder = [item sortOrder];
            [self.listIndex addObject:listItem];
        }
    }
    
    self.listIndex = [[self.listIndex sortedArrayUsingSelector:@selector(compare:)] mutableCopy];
    
    [self.itemsList reloadData];
}
- (void) AddItemsPickListFooter
{
    [[NSBundle mainBundle] loadNibNamed:@"fcAddItemTableFooter" owner:self options:nil];
    UIButton *But = (UIButton*)[[self TableFooter] viewWithTag:1];
    [But addTarget:self action:@selector(doBack:) forControlEvents:UIControlEventTouchUpInside];
    
    //footerView.userInteractionEnabled = YES;
    self.itemsList.tableFooterView = self.TableFooter;
    self.itemsList.tableFooterView.userInteractionEnabled = YES;
    //[ItemsList.tableFooterView setFrame:ViewSize];
    
}
// Make sure we de-select our row when we appear otherwise it stays there selected when you come back to the ItemsList
- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.itemsList deselectRowAtIndexPath:[self.itemsList indexPathForSelectedRow] animated:NO];
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
- (void) doBack:(id) sender;
{
    [self doBackCommit ];
    
}
- (void) doBackCommit
{
    [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
    if([self.delegate respondsToSelector:@selector(childFinishedCommit)])
    {
        [self.delegate childFinishedCommit];
    }
}

- (void) childFinishedCommit
{
    [self doBackCommit ];
}
- (void) childFinished
{

}
@end
