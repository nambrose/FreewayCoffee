//
//  fcTimeToLocationViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <QuartzCore/QuartzCore.h>
#import "fcTimeToLocationViewController.h"
#import "fcAppDelegate.h"
#import "fcItemsListViewController.h"
#import "XMLHelper.h"
#import "Constants.h"
#import "fcRootViewController.h"
#import "fcLocation.h"
#import "fcUserInfo.h"

@interface fcTimeToLocationViewController ()

@end

@implementation fcTimeToLocationViewController
//@synthesize WelcomeUserLabel;
@synthesize LocationLabel;
@synthesize TimeSlider;
@synthesize TimeLabel;
//@synthesize scrollView;
@synthesize delegate=_delegate;
@synthesize groupView;
@synthesize signonError=_signonError;
@synthesize conRequest=_conRequest;
@synthesize actionBut=_actionBut;
@synthesize resetBut=_resetBut;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        responseData=nil;
        DownloadedErrorData=nil;
        TimeUpdatedSuccessfully=FALSE;
        m_InitialTime=0;
        m_CurrentTime=0;
        _signonError=FALSE;
    }
    return self;
}

- (id)initWithNibName:(NSString *)nibNameOrNil andInitialTime:(int)Time
{
    self = [super initWithNibName:nibNameOrNil bundle:nil];
    if (self)
    {
        responseData=nil;
        DownloadedErrorData=nil;
        TimeUpdatedSuccessfully=FALSE;
        m_InitialTime=Time;
        m_CurrentTime=Time;
    }
    return self;
}




- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}
- (void)viewDidLoad
{
    [super viewDidLoad];
    responseData = [[NSMutableData alloc] init ];
    //NSString *WelcomeString = [NSString stringWithFormat:@"%@", @", Set Your Time"];
    
    UIColor *background = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
    
    UITapGestureRecognizer *gr = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(timeSliderTapped:)];
    [TimeSlider addGestureRecognizer:gr];
    
    fcLocation *location = [myCommonAppDelegate getCurrentLocation];
    
    if(location==nil)
    {
        // Should never get here
        [LocationLabel setText:LOCATION_NONE_TEXT];
    }
    else
    {
        [LocationLabel setText:location.LocationDescription];
    }
    
    
    UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithTitle:@"Update" style:UIBarButtonItemStylePlain
                                                                   target:self action:@selector(doUpdate:)];

    self.navigationItem.rightBarButtonItem = rightButton;
    // Gross ios6 Hack ?
    for(UIView* subView in self.actionBut.subviews)
        if([subView isKindOfClass:NSClassFromString(@"UIGroupTableViewCellBackground")])
            [subView setHidden:YES];
    
    for(UIView* subView in self.resetBut.subviews)
        if([subView isKindOfClass:NSClassFromString(@"UIGroupTableViewCellBackground")])
            [subView setHidden:YES];
    
    CAGradientLayer *orderGrad =  [myCommonAppDelegate makeGreenGradient];
    orderGrad.frame = self.actionBut.bounds;
    [self.actionBut.layer insertSublayer:orderGrad atIndex:0];
    
    CAGradientLayer *feedbackGrad =  [myCommonAppDelegate makeGreyGradient];
    feedbackGrad.frame = self.resetBut.bounds;
    [self.resetBut.layer insertSublayer:feedbackGrad atIndex:0];
    

    
    [self updateTimeSliderAndLabel];
    self.title = @"Set your time";
    
    self.groupView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    self.groupView.layer.masksToBounds = YES;
    //self.scrollView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    //[WelcomeUserLabel setText:WelcomeString];
    
    //[self.view addSubview:scrollView];
    
}

- (void) updateTimeLabel
{
    [TimeLabel setText:[NSString stringWithFormat:@"Time: %d Minutes",m_CurrentTime]];
}

-(void) updateTimeSlider
{
    [TimeSlider setValue:m_CurrentTime];
}

- (void) updateTimeSliderAndLabel
{
    [self updateTimeLabel];
    [self updateTimeSlider];
}


- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    if( [elementName isEqualToString:UPDATE_TIME_TO_LOCATION_TAG])
    {
        NSString *resultString = [attributeDict valueForKey:RESULT_ATTR];
        resultString = [XMLHelper stringByDecodingURLFormat:resultString];
        
        if([resultString isEqualToString:RESULT_OK])
        {
            TimeUpdatedSuccessfully=TRUE;
        }
        
    }
    else if([elementName isEqualToString:SIGNON_RESPONSE_TAG])
    {
        NSString *attrValue = [NSString stringWithString:[attributeDict valueForKey:RESULT_ATTR] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        if([attrValue isEqualToString:OK_ATTR_VALUE]!=TRUE)
        {
            self.signonError=TRUE;
        }
    }

    
}


- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
 
}
- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
    
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    [responseData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    [responseData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    //[[NSAlert alertWithError:error] runModal];
    
    [HUD hide:YES afterDelay:HUD_HIDE_DELAY_DEFAULT];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
     
    // Once this method is invoked, "responseData" contains the complete result
    [HUD hide:YES afterDelay:HUD_HIDE_DELAY_DEFAULT];
    
    NSXMLParser *parser = [[NSXMLParser alloc] initWithData:responseData];
    [parser setDelegate: self];
    
    [parser setShouldProcessNamespaces:YES];
    
#ifdef FC_DO_LOG
     NSString* newStr = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
     FC_Log(@"%@",newStr);
#endif 
    
    if([parser parse]!=YES)
    {
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: NETWORK_ERROR_ALERT_TITLE
                                   message: NETWORK_ERROR_ALERT_MESSAGE
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
               return;
    }
    if(self.signonError==TRUE)
    {
        [[myCommonAppDelegate navController] popToRootViewControllerAnimated:NO];
        return;
        
    }
    if(TimeUpdatedSuccessfully!=TRUE)
    {
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: UPDATE_TIME_TO_LOCATION_FAILED_ALERT_TITLE
                                   message: UPDATE_TIME_TO_LOCATION_FAILED_ALERT_MESSAGE
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
        
    }
    else
    {
        [[myCommonAppDelegate UserInfo] setUserTimeToLocation:[NSNumber numberWithInt:m_CurrentTime]];
        
        [self goBack];
        return;
    }
    
}

-(void) goBack
{
    if([self.delegate respondsToSelector:@selector(childFinished)])
    {
        [self.delegate childFinished];
    }
    
    [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
    
}
- (void) timeSliderTapped: (UITapGestureRecognizer*) g
{
    UISlider *slider = (UISlider*)g.view;
    if(slider.highlighted)
    {
        return;
    }
    CGPoint pt = [g locationInView:slider];
    CGFloat percentage = pt.x / slider.bounds.size.width;
    CGFloat delta = percentage * (slider.maximumValue - slider.minimumValue);
    CGFloat value = slider.minimumValue+delta;
    [slider setValue:value animated:YES];
    m_CurrentTime =(int)(slider.value + 0.5f);
    [self updateTimeLabel];
    
};
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)dealloc
{
    [HUD removeFromSuperview];
    
    //self.WelcomeUserLabel=nil;
}

-(IBAction) sliderChanged:(id) sender
{
    UISlider *slider = (UISlider *) sender;
    m_CurrentTime =(int)(slider.value + 0.5f);
    [self updateTimeLabel];
}

- (IBAction) doCancel: (id) sender
{
    [self goBack];
}

- (IBAction) doReset: (id) sender
{
    m_CurrentTime =m_InitialTime;
    [self updateTimeSliderAndLabel];
}

- (IBAction) doUpdate: (id) sender
{
    if([myCommonAppDelegate networkUp]!=TRUE)
    {
        [[myCommonAppDelegate topController] showNetworkDownToast:self.view];
        return;
    }

    NSString *URLString = [NSString stringWithFormat:@"%@%@?%@=%@&%@=%d",
                           BASE_URL,USER_PAGE,
                           USER_COMMAND,USER_COMMAND_UPDATE_TIME_TO_LOCATION,
                           USER_COMMAND_UPDATE_TIME_TO_LOCATION_CMD,m_CurrentTime];
    
    //FC_Log(@"%@",URLString);
    self.signonError=FALSE;
    NSURLRequest *theRequest=[NSURLRequest requestWithURL:[NSURL URLWithString:URLString]
                              
                                              cachePolicy:NSURLRequestUseProtocolCachePolicy
                              
                                          timeoutInterval:[myCommonAppDelegate getRequestLongTimeout]];
    self.conRequest = [[NSURLConnection alloc] initWithRequest:theRequest delegate:self];
    
    HUD = [MBProgressHUD showHUDAddedTo:self.view animated:NO] ;
    HUD.dimBackground = YES;
    HUD.labelText=PROGRESS_UPDATE_TIME_TO_LOCATION_MESSAGE;
    HUD.delegate=self;
    
    
}


@end
