//
//  fcCarDataLicenseViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>
#import "fcCarDataLicenseViewController.h"
#import "fcAppDelegate.h"
#import "Constants.h"

@interface fcCarDataLicenseViewController ()

@end

@implementation fcCarDataLicenseViewController
//@synthesize WelcomeUserLabel;
@synthesize LicenseText;
@synthesize delegate=_delegate;
@synthesize scrollView;
@synthesize groupView;
@synthesize cancelBut=_cancelBut;
@synthesize okBut=_okBut;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textBoxName
{
	[textBoxName resignFirstResponder];
	return YES;
}

- (id)initWithNibName:(NSString *)nibNameOrNil andText:(NSString*)licenseText
{
    self = [super initWithNibName:nibNameOrNil bundle:nil];
    if (self)
    {
        m_InitialText = licenseText;
    }
    return self;
    
}
- (void)viewDidLoad
{
    [super viewDidLoad];
    UIColor *background = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
    
    NSString *WelcomeString = [NSString stringWithFormat:@"%@", @"License Plate"];
    //[WelcomeUserLabel setText: WelcomeString];
    
    UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithTitle:@"Update" style:UIBarButtonItemStylePlain
                                                                   target:self action:@selector(doOK:)];
    self.navigationItem.rightBarButtonItem = rightButton;
    self.groupView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    self.groupView.layer.masksToBounds = YES;

    self.title = WelcomeString;
    for(UIView* subView in self.cancelBut.subviews)
        if([subView isKindOfClass:NSClassFromString(@"UIGroupTableViewCellBackground")])
            [subView setHidden:YES];
    
    for(UIView* subView in self.okBut.subviews)
        if([subView isKindOfClass:NSClassFromString(@"UIGroupTableViewCellBackground")])
            [subView setHidden:YES];
    
    CAGradientLayer *okGrad =  [myCommonAppDelegate makeGreenGradient];
    okGrad.frame = self.okBut.bounds;
    [self.okBut.layer insertSublayer:okGrad atIndex:0];
    
    CAGradientLayer *cancelGrad =  [myCommonAppDelegate makeGreyGradient];
    cancelGrad.frame = self.cancelBut.bounds;
    [self.cancelBut.layer insertSublayer:cancelGrad atIndex:0];
    

    
    
    [self.view addSubview:scrollView];
    [LicenseText setText:m_InitialText];}

- (IBAction) doCancel: (id) sender
{
    [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
}
- (IBAction) doOK: (id) sender
{
    NSString *LicenseString = [[self LicenseText] text];
    if( (LicenseString==nil) || ( [LicenseString length]==0))
    {
        LicenseString=@"";
    }
    
    if([self.delegate respondsToSelector:@selector(setLicenseText:)])
    {
        [self.delegate setLicenseText: LicenseString];
    }
    
    [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
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
