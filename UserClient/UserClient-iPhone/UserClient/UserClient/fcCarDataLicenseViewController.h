//
//  fcCarDataLicenseViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol CarLicenseViewProtocol <NSObject>
-(void) setLicenseText:(NSString*)License;
@end

@interface fcCarDataLicenseViewController : UIViewController <CarLicenseViewProtocol>
{

@private
NSString *m_InitialText;

}

//@property (nonatomic, weak) IBOutlet UILabel *WelcomeUserLabel;
@property (nonatomic, weak) IBOutlet UITextField *LicenseText;
@property (nonatomic, weak) id<CarLicenseViewProtocol> delegate;
@property (nonatomic, strong) UIScrollView *scrollView;
@property (nonatomic,weak)  IBOutlet UIView *groupView;
@property (nonatomic,weak)  IBOutlet UIButton *cancelBut;
@property (nonatomic,weak)  IBOutlet UIButton *okBut;
- (IBAction) doCancel: (id) sender;
- (IBAction) doOK: (id) sender;
- (id)initWithNibName:(NSString *)nibNameOrNil andText:(NSString*)licenseText;

@end
