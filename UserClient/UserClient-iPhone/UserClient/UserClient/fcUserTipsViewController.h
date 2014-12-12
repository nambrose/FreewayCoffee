//
//  fcUserTipsViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 9/29/12.
//  Copyright (c) 2012 Freeway Coffee. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UICheckbox.h"
#import "MBProgressHUD.h"

@protocol ItemListChildFinished;

@class fcUserTip;

@interface fcUserTipsViewController : UIViewController  <NSXMLParserDelegate,MBProgressHUDDelegate>
{
@private
MBProgressHUD *HUD;
NSMutableData *responseData;

}

@property (nonatomic,weak) IBOutlet UIView *roundUpView;
@property (nonatomic,weak) IBOutlet UIView *tipValueView;
@property (nonatomic,weak) IBOutlet UICheckbox *roundUpCheck;
@property (nonatomic,weak) IBOutlet UIButton *minusButton;
@property (nonatomic,weak) IBOutlet UIButton *plusButton;
@property (nonatomic,weak) IBOutlet UILabel *tipValueLabel;
@property (nonatomic, strong) id<ItemListChildFinished> delegate;
@property (nonatomic,weak) fcUserTip *userTip;
@property (nonatomic,strong) NSDecimalNumber *tipAmount;
@property (nonatomic,strong) NSDecimalNumber *tipIncrement;
@property (nonatomic,assign) BOOL signonError;
@property (nonatomic,strong) NSURLConnection *conRequest;
@property (nonatomic,assign) BOOL tipUpdatedSuccessfully;
@property (nonatomic,strong) fcUserTip *tipFromServer;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil;
- (void)viewDidLoad;
- (void)updateTipLabel;
- (IBAction) plusTip:(id)sender;
- (IBAction) minusTip:(id)sender;
- (IBAction) updateTip:(id)sender;
//- (void)viewWillAppear:(BOOL)animated;
@end
