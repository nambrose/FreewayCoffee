//
//  fcCreditCardViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <QuartzCore/QuartzCore.h>

#import "fcCreditCardViewController.h"
#import "fcItemsListViewController.h"
#import "fcLocationAllowedPaymentMethod.h"
#import "fcRootViewController.h"
#import "fcAppDelegate.h"
#import "Constants.h"
#import "XMLHelper.h"
#import "fcUserInfo.h"


@interface fcCreditCardViewController ()

@end

@implementation fcCreditCardViewController
//@synthesize delegate=_delegate;
//@synthesize WelcomeUserLabel;
@synthesize cardNumber;
@synthesize expMonth;
@synthesize expYear;
@synthesize billingZIP;
@synthesize scrollView;
@synthesize groupScroll;
@synthesize groupView;
@synthesize signonError=_signonError;
@synthesize conRequest=_conRequest;
@synthesize CardUpdatedSuccessfully=_cardUpdatedSuccessfully;
@synthesize updateBut=_updateBut;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        HUD=nil;
        DownloadedErrorData=nil;
        DownloadedCreditCardData=nil;
        _cardUpdatedSuccessfully=FALSE;
        responseData=nil;
        _signonError=FALSE;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    responseData = [[NSMutableData alloc] init ];
    NSString *WelcomeString = [NSString stringWithFormat:@"%@",@"Add your card"];
    
    UIColor *background = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
    self.title=WelcomeString;
    //[WelcomeUserLabel setText:WelcomeString];
    self.groupScroll.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    self.groupScroll.layer.masksToBounds = YES;
    
    self.groupView.layer.masksToBounds = YES;
    self.groupView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    
    // Update But BG
    for(UIView* subView in self.updateBut.subviews)
        if([subView isKindOfClass:NSClassFromString(@"UIGroupTableViewCellBackground")])
            [subView setHidden:YES];
    
    CAGradientLayer *updateGrad =  [myCommonAppDelegate makeGreenGradient];
    updateGrad.frame = self.updateBut.bounds;
    [self.updateBut.layer insertSublayer:updateGrad atIndex:0];


    UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithTitle:@"Update" style:UIBarButtonItemStylePlain
                                                                     target:self action:@selector(doUpdate:)];
    /*
    UIButton *rightNewButton = [UIButton buttonWithType:UIButtonTypeCustom];
    
    //backButton.frame = CGRectMake(0.0f, 0.0f, 36.0f, 36.0f);
    //[backButton setBackgroundImage:image1 forState:UIControlStateNormal] ;
    
    [rightNewButton addTarget:self action:@selector(doUpdate:) forControlEvents:UIControlEventTouchUpInside];
   
    for(UIView* subView in rightNewButton.subviews)
    if([subView isKindOfClass:NSClassFromString(@"UIGroupTableViewCellBackground")])
        [subView setHidden:YES];
    
    CAGradientLayer *orderGrad =  [myCommonAppDelegate makeGreenGradient];
    orderGrad.frame = rightNewButton.frame;
    [rightNewButton.layer insertSublayer:orderGrad atIndex:0];
    [rightNewButton setTitle:@"Update" forState:UIControlStateNormal];
    
    rightNewButton.frame = CGRectMake(0.0f, 0.0f, 36.0f, 36.0f);
    
    UIBarButtonItem *rightButtonItem = [[UIBarButtonItem alloc] initWithCustomView:rightNewButton];
    self.navigationItem.rightBarButtonItem = rightButtonItem;
    */
    self.navigationItem.rightBarButtonItem = rightButton;

    
    [self.view addSubview:scrollView];
}

- (IBAction) doCancel: (id) sender
{
    /*
    if([self.delegate respondsToSelector:@selector(childFinished)])
    {
        [self.delegate childFinished];
    }
     */
    
    [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
}
- (IBAction) doUpdate: (id) sender
{
    [self.cardNumber resignFirstResponder];
    [self.expMonth resignFirstResponder];
    [self.expYear resignFirstResponder];
    [self.billingZIP resignFirstResponder];
    
    if([myCommonAppDelegate networkUp]!=TRUE)
    {
        [[myCommonAppDelegate topController] showNetworkDownToast:self.view];
        return;
    }

    NSString *cardNumberStr = [[self cardNumber] text];
    
    if((nil==cardNumberStr) || ([cardNumberStr length]==0))
    {
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: CREDIT_CARD_INPUT_FAILED_ALERT_TITLE
                                   message: CREDIT_CARD_NO_CARD_NUMBER_ALERT_MESSAGE
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
        
        return;
    }
    
    if([self checkCardNumber:cardNumberStr]!=TRUE)
    {
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: CREDIT_CARD_INPUT_FAILED_ALERT_TITLE
                                   message: CREDIT_CARD_INVALID_CARD_NUMBER_ALERT_MESSAGE
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
       
        return;
    }
    
    NSString *expMonthStr = [[self expMonth] text];
    
    
    if( (nil==expMonthStr) || ([expMonthStr length]==0))
    {
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: CREDIT_CARD_INPUT_FAILED_ALERT_TITLE
                                   message: CREDIT_CARD_NO_EXP_MONTH_ALERT_MESSAGE
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
               return;
    }
    if([expMonthStr length]<1 || [expMonthStr length]>2)
    {
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: CREDIT_CARD_INPUT_FAILED_ALERT_TITLE
                                   message: CREDIT_CARD_EXP_MONTH_LENGTH_ALERT_MESSAGE
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
        
        return;
    }
    
    NSString *expYearStr = [[self expYear] text];
    if((nil==expYearStr ) || ([expYearStr length]==0))
    {
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: CREDIT_CARD_INPUT_FAILED_ALERT_TITLE
                                   message: CREDIT_CARD_NO_EXP_YEAR_ALERT_MESSAGE
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
        
        return;
        
    }
    
    if([expYearStr length]!=CREDIT_CARD_EXP_YEAR_LENGTH)
    {
        NSString *alertStr = [NSString stringWithFormat:@"%@  %d digits",CREDIT_CARD_EXP_YEAR_LENGTH_ALERT_MESSAGE,CREDIT_CARD_EXP_YEAR_LENGTH];
        
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: CREDIT_CARD_INPUT_FAILED_ALERT_TITLE
                                   message: alertStr
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
        
        return;
    }
    
    NSString *billingZIPStr = [[self billingZIP] text];
    if( (nil==billingZIPStr ) || ([billingZIPStr length]==0))
    {
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: CREDIT_CARD_INPUT_FAILED_ALERT_TITLE
                                   message: CREDIT_CARD_NO_BILLING_ZIP_ALERT_MESSAGE
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
        
        return;
        
    }
    
    if([billingZIPStr length]!=CREDIT_CARD_BILLING_ZIP_LENGTH)
    {
        NSString *alertStr = [NSString stringWithFormat:@"%@  %d digits",CREDIT_CARD_BILLING_ZIP_LENGTH_ALERT_MESSAGE,CREDIT_CARD_BILLING_ZIP_LENGTH];
        
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: CREDIT_CARD_INPUT_FAILED_ALERT_TITLE
                                   message: alertStr
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
       
        return;
    }
    
    // Data all validated as best we can !
    
    
    // UPDATE_CREDIT_CARD_USE_FOR_PAY_CMD Means to also change pay type to InApp
    // Success is only returned if BOTH operations successful and Server guarantees BOTH or NONE apply to the DB
    // TODO This needs a serious look at in the future.
    NSString *myRequestString = [NSString stringWithFormat:@"%@=%@&%@=%@&%@=%@&%@=%@&%@=%@",
                                 USER_COMMAND,UPDATE_CREDIT_CARD_USE_FOR_PAY_CMD,
                                 CREDIT_CARD_NUMBER_CMD_PARAM,cardNumberStr,
                                 CREDIT_CARD_EXP_MONTH_CMD_PARAM,expMonthStr,
                                 CREDIT_CARD_EXP_YEAR_CMD_PARAM,expYearStr,
                                 CREDIT_CARD_ZIP_CMD_PARAM,billingZIPStr ];
    
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
    HUD.labelText=PROGRESS_UPDATE_CREDIT_CARD_MESSAGE;
    HUD.delegate=self;
    
    
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
    // FC_Log(elementName);
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
    
    if( [elementName isEqualToString:CREDIT_CARD_UPDATE_RESPONSE_TAG])
    {
        NSString *resultString = [attributeDict valueForKey:RESULT_ATTR];
        resultString = [XMLHelper stringByDecodingURLFormat:resultString];
        
        if([resultString isEqualToString:RESULT_OK])
        {
            self.CardUpdatedSuccessfully=TRUE;
        }
    }
    else if ([elementName isEqualToString:USER_CREDIT_CARDS_TAG])
    {
        DownloadedCreditCardData = [XMLHelper convertStringsFromWeb:attributeDict];
        
    }
    else if ([elementName isEqualToString:ERROR_TAG])
    {
        DownloadedErrorData = [XMLHelper convertStringsFromWeb:attributeDict];
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
    if(self.CardUpdatedSuccessfully==TRUE)
    {
        [myCommonAppDelegate setUserCreditCardInfo:[XMLHelper convertStringsFromWeb:DownloadedCreditCardData]  ];
        [[myCommonAppDelegate UserInfo] setUserPayMethod:[NSNumber numberWithInteger:LOCATION_PAY_METHOD_IN_APP]]; // We assume if they took the time to enter a Card, they want to use it !
         
        [myCommonAppDelegate goToTopPageComitted];
        return;
    }
    else
    {
        if(DownloadedErrorData==nil)
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
        else
        {
            NSString *alertStr = [NSString stringWithFormat:@"Sorry, %@ Your card could not be added or updated Reason: %@\n %@\n %@\n",
                                  [myCommonAppDelegate getName],
                                  [DownloadedErrorData valueForKey: ERROR_CODE_MAJOR],
                                  [DownloadedErrorData valueForKey: ERROR_CODE_MINOR],
                                  [DownloadedErrorData valueForKey: ERROR_LONG_TEXT]];
            
            
            
            
            UIAlertView *alert =
            [[UIAlertView alloc] initWithTitle: CREDIT_CARD_UPDATE_FAIL_ALERT_TITLE
                                       message: alertStr
                                      delegate: self
                             cancelButtonTitle: @"OK"
                             otherButtonTitles: nil];
            [alert show];
            
            return;
        }
    }
    
}


- (BOOL)textFieldShouldReturn:(UITextField *)textBoxName
{
	[textBoxName resignFirstResponder];
	return YES;
}
- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (NSMutableArray *) toCharArray:(NSString *)stringToConvert
{
    
	NSMutableArray *characters = [[NSMutableArray alloc] initWithCapacity:[stringToConvert length]];
	for (int i=0; i < [stringToConvert length]; i++) {
		NSString *ichar  = [NSString stringWithFormat:@"%c", [stringToConvert characterAtIndex:i]];
		[characters addObject:ichar];
	}
    
	return characters;
}

- (BOOL) checkCardNumber:(NSString *)stringToTest
{
    
	NSMutableArray *stringAsChars = [self toCharArray:stringToTest];
    
	BOOL isOdd = YES;
	int oddSum = 0;
	int evenSum = 0;
    
	for (int i = [stringToTest length] - 1; i >= 0; i--) {
        
		int digit = [(NSString *)[stringAsChars objectAtIndex:i] intValue];
        
		if (isOdd)
			oddSum += digit;
		else
			evenSum += digit/5 + (2*digit) % 10;
        
		isOdd = !isOdd;
	}
    
	return ((oddSum + evenSum) % 10 == 0);
}



- (void)dealloc
{
    [HUD removeFromSuperview];
}



@end
