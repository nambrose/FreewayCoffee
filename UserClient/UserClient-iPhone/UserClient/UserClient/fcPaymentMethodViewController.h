//
//  fcPaymentMethodViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 2/9/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"

@class UICheckbox;
@class fcXMLParserMisc;


@protocol ItemListChildFinished;


@interface fcPaymentMethodViewController : UIViewController <MBProgressHUDDelegate>

@property (nonatomic,weak) IBOutlet UIView *backView;
@property (nonatomic,weak) IBOutlet UILabel *titleLabel;

@property (nonatomic,weak) IBOutlet UIView *atStoreBackView;
@property (nonatomic,weak) IBOutlet UICheckbox *atStoreCheck;

@property (nonatomic,weak) IBOutlet UIView *cardBackView;
@property (nonatomic,weak) IBOutlet UICheckbox *cardCheck;
@property (nonatomic,weak) IBOutlet UIButton *cardDeleteButton;


@property (nonatomic,weak) IBOutlet UIButton *backButton;
@property (nonatomic,weak) IBOutlet UIButton *addCardButton;
@property (nonatomic,weak) IBOutlet UIButton *updateButton;

@property (nonatomic,strong) NSMutableData *responseData;
@property (nonatomic,strong) MBProgressHUD *HUD;
@property (nonatomic,strong) NSURLConnection *conRequest;

@property (nonatomic,strong) NSNumber *currentPayMethod;

@property (nonatomic, strong) id<ItemListChildFinished> delegate;

@property (nonatomic, strong) fcXMLParserMisc *responseXMLParser;


-(IBAction)doBack:(id)sender;
-(void) doGoBack;


-(IBAction)doAddCard:(id)sender;
-(IBAction)doUpdate:(id)sender;
-(IBAction)doDeleteCard:(id)sender;

-(void)updateState;

-(IBAction)payAtStoreChecked:(id)sender;
-(IBAction)payInAppChecked:(id)sender;

@end
