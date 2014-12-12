//
//  fcRootViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"


@interface fcRootViewController : UIViewController <NSXMLParserDelegate,UITextFieldDelegate,MBProgressHUDDelegate>
{
@private
    NSMutableData *responseData;
    NSString *usernameString;
    NSString *passwordString;
    MBProgressHUD *HUD;
    
    
}
@property (weak, nonatomic) IBOutlet UIView *groupView;
@property (nonatomic, weak) IBOutlet UIScrollView *scrollView;
@property (nonatomic, weak) IBOutlet UITextField *usernameField;
@property (nonatomic, weak) IBOutlet UITextField *passwordField;
@property (nonatomic, weak) IBOutlet UIButton *loginButton;
@property (nonatomic, weak) IBOutlet UIButton *goSignupButton;
@property (nonatomic,weak) IBOutlet UILabel *existingUserLabel;
@property (nonatomic,strong) NSURLConnection *conRequest;

@property (nonatomic, weak) UIActivityIndicatorView *loginIndicator;
@property (nonatomic, assign) BOOL signonSuccess;

- (IBAction) login: (id) sender;
- (IBAction) goSignup: (id) sender;
- (void) doGoSignup;

- (void) tryLogin;
- (void) populateFields;
- (void) viewDidAppear:(BOOL)animated;
- (void) viewWillAppear:(BOOL)animated;
- (void) showNetworkDownToast:(UIView*)v;
@end
