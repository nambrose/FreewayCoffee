//
//  fcOrderPriceBreakdownViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 3/30/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <UIKit/UIKit.h>

@class WEPopoverController;

@interface fcOrderPriceBreakdownViewController : UIViewController

@property (nonatomic,weak) IBOutlet UITextView *mainText;
@property (nonatomic,weak) IBOutlet UIButton *doneBut;
@property (nonatomic,strong) WEPopoverController *WEcontroller;
@property (nonatomic,strong) NSDecimalNumber *orderTotal;
@property (nonatomic,strong) NSDecimalNumber *orderItemsTotal;
@property (nonatomic,strong) NSDecimalNumber *orderDiscount;
@property (nonatomic,strong) NSDecimalNumber *orderTip;
@property (nonatomic,strong) NSDecimalNumber *orderTax;
@property (nonatomic,strong) NSDecimalNumber *orderTaxableAmount;
@property (nonatomic,strong) NSDecimalNumber *orderConvenienceFee;
@property (nonatomic,assign) BOOL orderTaxable;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil;
-(IBAction) doDone:(id)sender;

@end
