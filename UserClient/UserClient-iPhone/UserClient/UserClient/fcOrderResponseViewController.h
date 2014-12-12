//
//  fcOrderResponseViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 9/16/12.
//  Copyright (c) 2012,2013 Nick Ambrose. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>
#import <UIKit/UIKit.h>
#import "UICheckBox.h"
#import "MBProgressHUD.h"

@protocol ItemListChildFinished;

@interface fcOrderResponseViewController : UIViewController <NSXMLParserDelegate,MBProgressHUDDelegate>
{
    MBProgressHUD *HUD;
    NSMutableData *responseData;
}
@property(nonatomic, weak) IBOutlet UICheckbox *checkbox;
@property(nonatomic, weak) IBOutlet UIButton *actionButton;
@property(nonatomic, weak) IBOutlet UIWebView *mainText;
@property(nonatomic, weak) IBOutlet UIView *groupView;
@property (nonatomic,assign) BOOL parseSuccessful;
@property (nonatomic,assign) BOOL wasServerResponseGood;
@property (nonatomic,assign) BOOL wasOrderHereResponse;
@property (nonatomic,assign) BOOL signonError;
@property (nonatomic,strong) NSURLConnection *conRequest;
@property (nonatomic,strong) CAGradientLayer *gradient;

@property (nonatomic, strong) id<ItemListChildFinished> delegate;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil;
- (void) doSetDisplay;
- (IBAction) doAction:(id)sender;
- (void) exitCommitted;
- (void) goToOrderFinished;
@end
