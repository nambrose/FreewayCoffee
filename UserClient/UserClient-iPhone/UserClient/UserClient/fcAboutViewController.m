//
//  fcAboutViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 2/22/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <QuartzCore/QuartzCore.h>
#import "fcAboutViewController.h"
#import "fcAppDelegate.h"

@interface fcAboutViewController ()

@end

@implementation fcAboutViewController
@synthesize backBut=_backBut;
@synthesize mainText=_mainText;

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
    UIColor *background = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
    
    [myCommonAppDelegate prepareButtonForGradient:self.backBut];
    CAGradientLayer *backGrad =  [myCommonAppDelegate makeGreenGradient];
    backGrad.frame = self.backBut.bounds;
    [self.backBut.layer insertSublayer:backGrad atIndex:0];
    
    
    NSString *htmlContentString = [self makeAboutText];
    self.mainText.layer.masksToBounds = YES;

    self.mainText.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    [self.mainText loadHTMLString:htmlContentString baseURL:nil];

    
 
}

-(NSString*)makeAboutText
{
    NSDictionary* infoDict = [[NSBundle mainBundle] infoDictionary];
    
    NSString *build = [infoDict objectForKey:@"CFBundleVersion"];
    NSString *appVerName = [infoDict objectForKey:@"CFBundleShortVersionString"];
    
    NSMutableString *result = [NSMutableString stringWithFormat:@"<html><style type=\"text/css\"> \n"
                               "body {font-family: \"helvetica\";}\n"
                               "</style> \n<body><p>"];
    
    [result appendFormat:@"Freeway Coffee (Version: %@)<br><br> Build:(%@)<br><br>",appVerName,build];
    
    [result appendFormat:@"Fresh coffee with no lines<br><br>Drinking your favorite coffee just became faster and easier<br><br>"];
    
    
    //[result appendFormat:@"<a href=\"http://www.reewaycoffee.com/
    [result appendFormat:@"<a href=\"http://www.freewaycoffee.com/privacy-policy/\">Privacy Policy</a><br><br>"];
    
     
    [result appendFormat:@"</p></body</html>"];
    return result;

}

-(IBAction) doBack:(id)sender
{
    [myCommonAppDelegate goToTopPage];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
