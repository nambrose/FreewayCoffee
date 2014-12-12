//
//  fcOrderFinishedViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 2/17/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import "fcOrderFinishedViewController.h"

@interface fcOrderFinishedViewController ()

@end



#import "fcOrderResponseViewController.h"
#import "fcAppDelegate.h"
#import "fcLastOrder.h"
#import "ITToastMessage.h"
#import "XMLHelper.h"
#import "fcRootViewController.h"
#import "fcLastOrderItem.h"
#import "fcItemsListViewController.h"
#import "fcUserInfo.h"
#import "fcLocationAllowedArrivalMethod.h"
#import "fcLocation.h"


@implementation fcOrderFinishedViewController
@synthesize actionButton=_actionButton;
@synthesize mainText=_mainText;
@synthesize groupView=_groupView;
@synthesize finishedGradient=_finishedGradient;

//@synthesize delegate=_delegate;
/////////
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    UIColor *background = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
    
    self.navigationItem.hidesBackButton = YES;
    UIBarButtonItem *barButton = [[UIBarButtonItem alloc]
                                  initWithTitle:@"Order"
                                  style:UIBarButtonItemStyleBordered
                                  target:self
                                  action:@selector(exitCommitted)];
    
       
    self.finishedGradient = [myCommonAppDelegate makeGreyGradient];
    self.finishedGradient.frame = self.actionButton.bounds;
        [self.navigationItem setLeftBarButtonItem: barButton];
    
    [myCommonAppDelegate prepareButtonForGradient:self.actionButton];
    
    
    self.mainText.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    [self.mainText setClipsToBounds:YES];
    
    self.groupView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    [self doSetDisplay];
}

- (IBAction) doAction:(id)sender
{
    [self exitCommitted];
}

 
- (void) doSetDisplay
{
  
    [self.actionButton.layer insertSublayer:self.finishedGradient atIndex:0];
        
    [self.actionButton setTitle:@"Finished" forState:UIControlStateNormal];
    self.title = @"Order Confirmation";
        
    NSString *htmlContentString = [[myCommonAppDelegate lastOrder] makeOrderResponseText];
    [self.mainText loadHTMLString:htmlContentString baseURL:nil];
    
    self.groupView.backgroundColor = [UIColor clearColor];

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
- (void) exitCommitted
{
 
    NSArray *array = [self.navigationController viewControllers];
    
    fcItemsListViewController *orderPage = (fcItemsListViewController*)[array objectAtIndex:1];
    if([orderPage respondsToSelector:@selector(childFinishedCommit)])
    {
        [orderPage childFinishedCommit];
    }
    [self.navigationController popToViewController:[array objectAtIndex:1] animated:NO];
    
}


@end
