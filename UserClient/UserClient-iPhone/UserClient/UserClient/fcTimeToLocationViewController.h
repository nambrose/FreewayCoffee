//
//  fcTimeToLocationViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"

@protocol ItemListChildFinished;

@interface fcTimeToLocationViewController : UIViewController <NSXMLParserDelegate,MBProgressHUDDelegate>
{
    

IBOutlet UILabel *WelcomeUserLabel;
IBOutlet UILabel *LocationLabel;
IBOutlet UILabel *TimeLabel;
IBOutlet UISlider *TimeSlider;
IBOutlet UIScrollView *scrollView;
@private

int m_InitialTime; // The initial time (for the Reset Button);
int m_CurrentTime;
MBProgressHUD *HUD;
NSMutableData *responseData;
NSMutableDictionary *DownloadedErrorData;
BOOL TimeUpdatedSuccessfully;


}
//@property (nonatomic, strong) UILabel *WelcomeUserLabel;
@property (nonatomic, strong) UILabel *LocationLabel;
@property (nonatomic, strong) UILabel *TimeLabel;
@property (nonatomic, strong) UISlider *TimeSlider;
//@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic, weak) IBOutlet UIView *groupView;
@property (nonatomic, weak) id<ItemListChildFinished> delegate;
@property (nonatomic,assign) BOOL signonError;
@property (nonatomic,strong) NSURLConnection *conRequest;
@property (nonatomic, weak) IBOutlet UIButton *actionBut;
@property (nonatomic, weak) IBOutlet UIButton *resetBut;
- (id)initWithNibName:(NSString *)nibNameOrNil andInitialTime:(int)Time;
- (IBAction) doCancel: (id) sender;
- (IBAction) doReset: (id) sender;
- (IBAction) doUpdate: (id) sender;
- (void) updateTimeLabel;
- (void) timeSliderTapped: (UITapGestureRecognizer*) g;
- (void) updateTimeSlider;
- (void) updateTimeSliderAndLabel;
- (void) goBack;
@end
