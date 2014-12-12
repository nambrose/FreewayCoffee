//
//  fcSignupViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"

@interface fcSignupViewController : UIViewController <NSXMLParserDelegate,MBProgressHUDDelegate,UITextFieldDelegate>
{

    
@private
    NSMutableData *responseData;
    NSString *usernameString;
    NSString *nameString;
    NSString *passwordString;
    NSString *passwordAgainString;
    
    MBProgressHUD *HUD;

}
@property (nonatomic, weak) IBOutlet UITextField *usernameField;
@property (nonatomic, weak) IBOutlet UITextField *nameField;
@property (nonatomic, weak) IBOutlet UITextField *passwordField;
@property (nonatomic, weak) IBOutlet UITextField *passwordAgainField;
@property (nonatomic, weak) IBOutlet UILabel *errorField;
@property (nonatomic, weak) IBOutlet UIButton *signupButton;
@property (nonatomic, weak) IBOutlet UIButton *goSignonButton;
@property (nonatomic, weak) IBOutlet UIActivityIndicatorView *signupIndicator;
@property (nonatomic, weak) IBOutlet UIScrollView *scrollView;
@property (nonatomic, strong) NSMutableData *responseData;
@property (nonatomic, strong) NSString *usernameString;
@property (nonatomic, strong) NSString *nameString;
@property (nonatomic, strong) NSString *passwordString;
@property (nonatomic, strong) NSString *passwordAgainString;
@property (nonatomic, weak) IBOutlet UIView *groupView;
@property (nonatomic, weak) IBOutlet UILabel *signupLabel;
@property (nonatomic, weak) IBOutlet UILabel *goSignonLabel;
@property (nonatomic,strong) NSURLConnection *conRequest;
@property (nonatomic,assign) BOOL signupSuccess;

- (IBAction) doSignup: (id) sender;
- (IBAction) goSignon: (id) sender;
- (void) viewDidAppear:(BOOL)animated;
- (void)viewWillAppear:(BOOL)animated;
@end