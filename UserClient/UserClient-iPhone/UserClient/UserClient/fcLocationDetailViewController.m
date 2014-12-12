//
//  fcLocationDetailViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 1/17/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import "fcLocationDetailViewController.h"
#import "fcLocationListViewController.h"
#import "fcLocation.h"

#import "fcUserInfo.h"

#import "Constants.h"
#import "fcAppDelegate.h"

@interface fcLocationDetailViewController ()

@end

@implementation fcLocationDetailViewController
@synthesize mainText=_mainText;
@synthesize backButton=_backButton;
@synthesize mapButton=_mapButton;
@synthesize chooseLocationButton=_chooseLocationButton;
@synthesize bgView=_bgView;
@synthesize gradientBack=_gradientBack;
@synthesize gradientMap=_gradientMap;
@synthesize gradientChoose=_gradientChoose;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        // Custom initialization
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    UIColor *background = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:IMG_BACKGROUND_IMAGE_NAME]];
    self.view.backgroundColor = background;
    
    /*
    self.navigationItem.hidesBackButton = YES;
    UIBarButtonItem *barButton = [[UIBarButtonItem alloc]
                                  initWithTitle:@"Order"
                                  style:UIBarButtonItemStyleBordered
                                  target:self
                                  action:@selector(exitCommitted)];
     [self.navigationItem setLeftBarButtonItem: barButton];
    */
    
    self.gradientBack = [myCommonAppDelegate makeGreyGradient];
    self.gradientBack.frame = self.backButton.bounds;
    
    self.gradientMap = [myCommonAppDelegate makeGreyGradient];
    self.gradientMap.frame = self.mapButton.bounds;
    
    self.gradientChoose = [myCommonAppDelegate makeGreenGradient];
    self.gradientChoose.frame = self.chooseLocationButton.bounds;
    
    // Not good self.mainText.dataDetectorTypes = UIDataDetectorTypeAll;
    
    // Gross ios6 Hack ?
    [myCommonAppDelegate prepareButtonForGradient:self.backButton];
    [myCommonAppDelegate prepareButtonForGradient:self.mapButton];
    [myCommonAppDelegate prepareButtonForGradient:self.chooseLocationButton];
    
    [self.backButton.layer insertSublayer:self.gradientBack atIndex:0];
    [self.mapButton.layer insertSublayer:self.gradientMap atIndex:0];
    [self.chooseLocationButton.layer insertSublayer:self.gradientChoose atIndex:0];
        
    self.mainText.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    [self.mainText setClipsToBounds:YES];
    
    self.bgView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    
    if([[myCommonAppDelegate UserInfo] isLocationSet]!=TRUE)
        
    {
        fcLocationListViewController *locationListView = [[fcLocationListViewController alloc]
                                                    initWithNibName:@"fcLocationListViewController" bundle:nil];
        
        [ [myCommonAppDelegate navController] pushViewController:locationListView animated:NO];

        
    }
    else
    {
        [self doSetDisplay];
    }
}

- (void) doSetDisplay
{
    [self.mainText setText:@""];
    self.title=@"Unknown";
    
    fcLocation *location = [myCommonAppDelegate getCurrentLocation];
    if(location!=nil)
    {
        if([location isLocationNone]!=TRUE)
        {
            self.title = [location LocationDescription];
            [self.mainText setText:[location makeDetailText]];
        }
        
    }
    
    
}
- (IBAction) doBack:(id)sender
{
    [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
}
- (IBAction) doMap:(id)sender
{
    fcLocation *location = [myCommonAppDelegate getCurrentLocation];
    if(location!=nil)
    {
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:[location makeLocationMapsURL]]];
       // - (NSString*) makeLocationMapsURL
    }
}
- (IBAction) doChooseLocation:(id)sender
{
    fcLocationListViewController *locationListView = [[fcLocationListViewController alloc]
                                                      initWithNibName:@"fcLocationListViewController" bundle:nil];
    
    [ [myCommonAppDelegate navController] pushViewController:locationListView animated:NO];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
