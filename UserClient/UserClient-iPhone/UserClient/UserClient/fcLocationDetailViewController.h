//
//  fcLocationDetailViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 1/17/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>
#import <UIKit/UIKit.h>

@interface fcLocationDetailViewController : UIViewController

@property (nonatomic,weak) IBOutlet UITextView *mainText;
@property (nonatomic,weak) IBOutlet UIButton *backButton;
@property (nonatomic,weak) IBOutlet UIButton *mapButton;
@property (nonatomic,weak) IBOutlet UIButton *chooseLocationButton;
@property (nonatomic,weak) IBOutlet UIView *bgView;
@property (nonatomic,strong) CAGradientLayer *gradientBack;
@property (nonatomic,strong) CAGradientLayer *gradientMap;

@property (nonatomic,strong) CAGradientLayer *gradientChoose;

- (IBAction) doBack:(id)sender;
- (IBAction) doMap:(id)sender;
- (IBAction) doChooseLocation:(id)sender;
- (void) doSetDisplay;

@end
