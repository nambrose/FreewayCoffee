//
//  fcOrderFinishedViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 2/17/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>

@interface fcOrderFinishedViewController : UIViewController

@property(nonatomic, weak) IBOutlet UIButton *actionButton;
@property(nonatomic, weak) IBOutlet UIWebView *mainText;
@property(nonatomic, weak) IBOutlet UIView *groupView;


@property (nonatomic,strong) CAGradientLayer *finishedGradient;
//@property (nonatomic, strong) id<ItemListChildFinished> delegate;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil;
- (void) doSetDisplay;
- (IBAction) doAction:(id)sender;
- (void) exitCommitted;


@end
