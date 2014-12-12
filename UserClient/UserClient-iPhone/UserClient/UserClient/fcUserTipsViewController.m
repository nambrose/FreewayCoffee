//
//  fcUserTipsViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 9/29/12.
//  Copyright (c) 2012 Freeway Coffee. All rights reserved.
//

#import <QuartzCore/QuartzCore.h>
#import "fcUserTipsViewController.h"
#import "Constants.h"
#import "fcAppDelegate.h"
#import "fcUserTip.h"
#import "fcUserTipTable.h"
#import "fcAppSetting.h"
#import "fcAppSettingsTable.h"
#import "fcRootViewController.h"
#import "XMLHelper.h"
#import "fcItemsListViewController.h"
#import "fcUserInfo.h"
#import "fcLocation.h"

@interface fcUserTipsViewController ()

@end

@implementation fcUserTipsViewController
@synthesize roundUpView=_roundUpView;
@synthesize tipValueView=_tipValueView;
@synthesize roundUpCheck=_roundUpCheck;
@synthesize minusButton=_minusButton;
@synthesize plusButton=_plusButton;
@synthesize tipValueLabel=_tipValueLabel;
@synthesize userTip=_userTip;
@synthesize tipAmount=_tipAmount;
@synthesize delegate=_delegate;
@synthesize tipIncrement = _tipIncrement;
@synthesize signonError=_signonError;
@synthesize conRequest=_conRequest;
@synthesize tipFromServer=_tipFromServer;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        // Custom initialization
        _tipUpdatedSuccessfully=FALSE;
        _signonError=FALSE;
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
    responseData = [[NSMutableData alloc] init ];
    UIColor *background = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:IMG_BACKGROUND_IMAGE_NAME]];
    self.view.backgroundColor = background;
    
    // UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@"Order" style:UIBarButtonItemStyleBordered target:self action:nil];
    // [self.navigationItem setBackBarButtonItem: backButton];
    
    //UIButton *infoButton = [UIButton buttonWithType:UIButtonTypeInfoLight];
    /*
    UIBarButtonItem *barButton = [[UIBarButtonItem alloc]
                                  initWithTitle:@"Order"
                                  style:UIBarButtonItemStyleBordered
                                  target:self
                                  action:@selector(childFinishedCommit)];
    
    [self.navigationItem setBackBarButtonItem: barButton];
     */
    self.title = @"Tips...";
    
    UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithTitle:@"Update" style:UIBarButtonItemStylePlain
                                                                   target:self action:@selector(updateTip:)];
    self.navigationItem.rightBarButtonItem = rightButton;
    
    self.roundUpView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    self.tipValueView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    
    
    NSNumber *locationID = [[myCommonAppDelegate UserInfo] userLocationID];
    
    if(locationID!=nil)
    {
        // Tip can be on locationID=0 -- ugh
        self.userTip = [[myCommonAppDelegate userTips] getTipForLocation:[NSNumber numberWithInt:0]];
    
    }
    self.tipValueView.hidden=FALSE;
    
    self.roundUpCheck.text = @"Round up to next dollar";

    NSString *tipIncrStr = [[myCommonAppDelegate appSettings] tryGetsettingAsString:APP_SETTING_TIPS_INCREMENT_FACTOR];
    if(nil==tipIncrStr)
    {
        tipIncrStr = DEFAULT_APP_TIP_INCREMENT;
    }
    
    self.tipIncrement = [NSDecimalNumber decimalNumberWithString:tipIncrStr];
    
    if(nil!=self.userTip)
    {
        
        self.tipAmount =[NSDecimalNumber decimalNumberWithString:self.userTip.tipAmount];
        
        self.roundUpCheck.checked=self.userTip.roundUp;
        
    }
    else
    {
            // No Tip.
        self.tipAmount=[NSDecimalNumber decimalNumberWithString:@"0.00"];
        self.roundUpCheck.checked=FALSE;
    }
    
    [self updateTipLabel];
        
}


- (void) updateTipLabel
{
    NSString *tipString = [NSString stringWithFormat:@"%.2f",[self.tipAmount doubleValue]];
    [self.tipValueLabel setText: [NSString stringWithFormat:@"Tip: $%@",tipString]];
    [self.view setNeedsDisplay];
}
/*
- (void)viewWillAppear:(BOOL)animated
{
    [ [myCommonAppDelegate navController] setNavigationBarHidden:FALSE];
    [super viewWillAppear:animated];
    
}
*/
- (IBAction) plusTip:(id)sender
{
    self.tipAmount = [self.tipAmount decimalNumberByAdding:self.tipIncrement];
    [self updateTipLabel];
    
}
- (IBAction) minusTip:(id)sender
{
    
    if ( ([self.tipAmount compare:self.tipIncrement]==NSOrderedAscending) ||
         ([self.tipAmount compare:self.tipIncrement]==NSOrderedSame))
    {
        // Value is smaller than Increment
        self.tipAmount = [NSDecimalNumber zero];
    }
    else
    {
        self.tipAmount = [self.tipAmount decimalNumberBySubtracting:self.tipIncrement];
    }
     [self updateTipLabel];
}
- (IBAction) updateTip:(id)sender
{
    if([myCommonAppDelegate networkUp]!=TRUE)
    {
        [[myCommonAppDelegate topController] showNetworkDownToast:self.view];
        return;
    }
    
    self.title = @"Updating...";
    self.tipUpdatedSuccessfully=FALSE;
    responseData = [[NSMutableData alloc]init];
    // Data all validated as best we can !
    
    NSString *tipTypeStr=[NSString stringWithFormat:@"%d",USER_TIP_TYPE_AMOUNT];
    
    
    NSString *roundUp=@"";
    if(self.roundUpCheck.checked)
    {
        roundUp = [NSString stringWithFormat:@"1"];
    }
    else
    {
        roundUp = [NSString stringWithFormat:@"0"];
    }
    
    NSString *tipString = [NSString stringWithFormat:@"%.2f",[self.tipAmount doubleValue]];
    
    NSString *myRequestString = [NSString stringWithFormat:@"%@=%@&%@=%@&%@=%@&%@=%@&%@=%@",
                                 USER_COMMAND,SET_USER_TIP_CMD,
                                 LOCATION_ID_ATTR,@"0",
                                 USER_TIP_TYPE_ATTR,tipTypeStr,
                                 USER_TIP_AMOUNT_ATTR, tipString,
                                 USER_TIP_ROUND_UP_ATTR,roundUp];
    
    self.signonError=FALSE;
    NSString *myRequestStringEscaped = [myRequestString stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    
    NSData *myRequestData = [ NSData dataWithBytes: [ myRequestStringEscaped UTF8String ] length: [ myRequestStringEscaped length ] ];
    
    NSString *URLString = [NSString stringWithFormat:@"%@%@",BASE_URL,USER_PAGE];
    
    NSMutableURLRequest *request = [ [ NSMutableURLRequest alloc ] initWithURL: [ NSURL URLWithString:  URLString] ];
    [ request setHTTPMethod: @"POST" ];
    [ request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"content-type"];
    [ request setHTTPBody: myRequestData ];
    
    self.conRequest = [[NSURLConnection alloc] initWithRequest:request delegate:self] ;
    
    HUD = [MBProgressHUD showHUDAddedTo:self.view animated:YES] ;
    HUD.dimBackground = YES;
    HUD.labelText=PROGRESS_UPDATE_TIP_MESSAGE;
    HUD.delegate=self;
    
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

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    
    if( [elementName isEqualToString:SET_USER_TIP_CMD])
    {
        NSString *resultString = [attributeDict valueForKey:RESULT_ATTR];
        resultString = [XMLHelper stringByDecodingURLFormat:resultString];
        
        if([resultString isEqualToString:RESULT_OK])
        {
           self.tipUpdatedSuccessfully=TRUE;
        }
        
    }
    else if([elementName isEqualToString:USER_TIP_TAG])
    {
        self.tipFromServer = [XMLHelper parseUserTip:attributeDict];
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

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    self.title = @"Tips...";
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
        responseData=nil;
        return;
    }
    
    responseData=nil;
    
    if(self.signonError==TRUE)
    {
        [[myCommonAppDelegate navController] popToRootViewControllerAnimated:NO];
        return;
        
    }
    
    if(self.tipUpdatedSuccessfully==TRUE)
    {
        if(nil!=self.tipFromServer)
        {
            [[myCommonAppDelegate userTips] addTip:self.tipFromServer];
        }
        //[myCommonAppDelegate setUserCreditCardInfo:[XMLHelper convertStringsFromWeb:DownloadedCreditCardData]  ];
        if([self.delegate respondsToSelector:@selector(childFinished)])
        {
            [self.delegate childFinished];
        }
        [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
        return;
    }
    else
    {
       // if(DownloadedErrorData==nil)
       // {
            UIAlertView *alert =
            [[UIAlertView alloc] initWithTitle: NETWORK_ERROR_ALERT_TITLE
                                       message: NETWORK_ERROR_ALERT_MESSAGE
                                      delegate: self
                             cancelButtonTitle: @"OK"
                             otherButtonTitles: nil];
            [alert show];
            
            return;
       // }
    return;
        }
    }
    


- (IBAction)roundupChecked:(id)sender
{
    /*
    if(self.roundUpCheck.checked==TRUE)
    {
        self.tipValueView.hidden=TRUE;
        
    }
    else
    {
        self.tipValueView.hidden=FALSE;
    }
    [self.view setNeedsDisplay];
     */
}


@end
