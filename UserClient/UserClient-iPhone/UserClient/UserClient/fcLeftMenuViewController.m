//
//  fcLeftMenuViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 2/9/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import "fcLeftMenuViewController.h"
#import <UIKit/UIKit.h>
#import "WEPopoverController.h"
#import "fcAppDelegate.h"
#import "fcLeftMenuIndexItem.h"
#import "fcLastOrder.h"
#import "fcOrderResponseViewController.h"
#import "fcOrderFinishedViewController.h"
#import "fcAboutViewController.h"

@interface fcLeftMenuViewController ()

@end

@implementation fcLeftMenuViewController

@synthesize controller=_controller;
@synthesize ItemsList=_ItemsList;

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
        _ItemsList = [[NSMutableArray alloc]init];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];

    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
 
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
    [ [myCommonAppDelegate navController] setNavigationBarHidden:FALSE];
    
    [self CreateListIndex];
}

- (void) CreateListIndex
{
    [self.ItemsList removeAllObjects];
    
    fcLeftMenuIndexItem *item = [[fcLeftMenuIndexItem alloc]init];
    item.ItemType = LEFT_MENU_TYPE_ABOUT;
    item.ItemText = LEFT_MENU_TYPE_ABOUT_TEXT;
    [self.ItemsList addObject:item];
    
    item = [[fcLeftMenuIndexItem alloc]init];
    item.ItemType = LEFT_MENU_TYPE_FEEDBACK;
    item.ItemText = LEFT_MENU_TYPE_FEEDBACK_TEXT;
    [self.ItemsList addObject:item];
    
    item = [[fcLeftMenuIndexItem alloc]init];
    item.ItemType = LEFT_MENU_TYPE_VIEW_LAST_ORDER;
    item.ItemText = LEFT_MENU_TYPE_VIEW_LAST_ORDER_TEXT;
    [self.ItemsList addObject:item];
    
    item = [[fcLeftMenuIndexItem alloc]init];
    item.ItemType = LEFT_MENU_TYPE_SIGN_OUT;
    item.ItemText = LEFT_MENU_TYPE_SIGN_OUT_TEXT;
    [self.ItemsList addObject:item];
    
    [self.tableView reloadData];
    
}
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    return [NSString stringWithFormat:@"Section %d", section];
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{

    // Return the number of sections.
    return 1;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 0.0f;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{

    // Return the number of rows in the section.
    return [self.ItemsList count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell2";
  //  UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier ];
    
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    
    fcLeftMenuIndexItem *item = [self.ItemsList objectAtIndex:indexPath.row];
    
    if(item==nil)
    {
        cell.textLabel.text=NONE_TEXT;
    }
    else
    {
        cell.textLabel.text=item.ItemText;
    }
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
   
    fcLeftMenuIndexItem *item = [self.ItemsList objectAtIndex:indexPath.row];
   [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
    if(item==nil)
    {
        return;
    }
    if(self.controller)
    {
        [self.controller dismissPopoverAnimated:NO];
    }
    switch(item.ItemType)
    {
        case LEFT_MENU_TYPE_SIGN_OUT:
            [[myCommonAppDelegate navController ]popToRootViewControllerAnimated:NO];
        break;
        case LEFT_MENU_TYPE_FEEDBACK:
            [myCommonAppDelegate openFeedbackEmailClient];
        break;
        case LEFT_MENU_TYPE_VIEW_LAST_ORDER:
            [self openLastOrderController];
        break;
        case LEFT_MENU_TYPE_ABOUT:
            [self openAboutController];
        break;
        break;
            
            
    }
}

-(void) openAboutController
{
    NSString *ControllerName =[myCommonAppDelegate getRet4ControllerName:@"fcAboutViewController"];
    fcAboutViewController *aboutView = [[fcAboutViewController alloc]
                                                initWithNibName:ControllerName bundle:nil];
    [ [myCommonAppDelegate navController] pushViewController:aboutView animated:NO];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
}

-(void) openLastOrderController
{
    if([[myCommonAppDelegate lastOrder] doesOrderExist]!=TRUE)
    {
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: @"Error"
                                   message: @"No Order to show"
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];

        return;
    }


    // If user said they were here, open the "Order Finished" page
    
    if( [[myCommonAppDelegate lastOrder] orderStatus]==ORDER_HERE_OK)
    {
        NSString *ControllerName =[myCommonAppDelegate getRet4ControllerName:@"fcOrderFinishedViewController"];
        fcOrderFinishedViewController *orderView = [[fcOrderFinishedViewController alloc]
                                                    initWithNibName:ControllerName bundle:nil];
        [ [myCommonAppDelegate navController] pushViewController:orderView animated:NO];
        
    }
    else
    {
        NSString *ControllerName =[myCommonAppDelegate getRet4ControllerName:@"fcOrderResponseViewController"];
        fcOrderResponseViewController *orderView = [[fcOrderResponseViewController alloc]
                                                    initWithNibName:ControllerName bundle:nil];
        orderView.delegate=nil;
        [ [myCommonAppDelegate navController] pushViewController:orderView animated:NO];
    }

}
/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }   
    else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/


@end
