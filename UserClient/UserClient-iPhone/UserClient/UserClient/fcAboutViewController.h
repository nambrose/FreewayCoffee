//
//  fcAboutViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 2/22/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
@interface fcAboutViewController : UIViewController

@property (nonatomic,weak) IBOutlet UIWebView *mainText;
@property (nonatomic,weak) IBOutlet UIButton *backBut;

-(IBAction) doBack:(id)sender;
-(NSString*)makeAboutText;
@end
