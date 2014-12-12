//
//  fcCreditCardViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <UIKit/UIKit.h>
#include "MBProgressHUD.h"
#import "TPKeyboardAvoidingScrollView.h"

@protocol  ItemListChildFinished;


@interface fcCreditCardViewController : UIViewController <NSXMLParserDelegate,MBProgressHUDDelegate>
{
IBOutlet UILabel *WelcomeUserLabel;

IBOutlet UITextField *cardNumber;
IBOutlet UITextField *expMonth;
IBOutlet UITextField *expYear;
IBOutlet UITextField *billingZIP;
IBOutlet UIScrollView *scrollView;
@private

MBProgressHUD *HUD;
NSMutableData *responseData;
NSMutableDictionary *DownloadedErrorData;
NSMutableDictionary *DownloadedCreditCardData;
//BOOL CardUpdatedSuccessfully;

}
//@property (nonatomic, strong) UILabel *WelcomeUserLabel;
@property (nonatomic, strong) UITextField *cardNumber;
@property (nonatomic, strong) UITextField *expMonth;
@property (nonatomic, strong) UITextField *expYear;
@property (nonatomic, strong) UITextField *billingZIP;
@property (nonatomic, strong) UIScrollView *scrollView;
//@property (nonatomic, strong) id<ItemListChildFinished> delegate;
@property (nonatomic, assign) BOOL CardUpdatedSuccessfully;
@property (nonatomic, weak) IBOutlet TPKeyboardAvoidingScrollView *groupScroll;
@property (nonatomic, weak) IBOutlet UIView *groupView;
@property (nonatomic,assign) BOOL signonError;
@property (nonatomic,strong) NSURLConnection *conRequest;
@property (nonatomic, weak) IBOutlet UIButton *updateBut;

- (IBAction) doCancel: (id) sender;
- (IBAction) doUpdate: (id) sender;

- (NSMutableArray *) toCharArray:(NSString *)stringToConvert;
- (BOOL) checkCardNumber:(NSString *)stringToTest;

@end
