//
//  fcPaymentMethodViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 2/9/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>
#import "fcPaymentMethodViewController.h"
#import "UICheckbox.h"
#import "fcAppDelegate.h"
#import "Constants.h"
#import "fcUserInfo.h"
#import "fcLocation.h"
#import "fcLocationAllowedPaymentMethod.h"
#import "fcItemsListViewController.h"
#import "MBProgressHUD.h"
#import "fcXMLParserMisc.h"
#import "fcError.h"
#import "fcCreditCardViewController.h"


@interface fcPaymentMethodViewController ()

@end

@implementation fcPaymentMethodViewController


@synthesize backView=_backView;

@synthesize atStoreBackView=_atStoreBackView;
@synthesize atStoreCheck=_atStoreCheck;


@synthesize cardBackView=_cardBackView;
@synthesize cardCheck=_cardCheck;
@synthesize cardDeleteButton=_cardDeleteButton;

@synthesize backButton=_backButton;
@synthesize addCardButton=_addCardButton;
@synthesize responseData=_responseData;
@synthesize HUD=_HUD;

@synthesize conRequest=_conRequest;
@synthesize updateButton=_updateButton;
@synthesize currentPayMethod=_currentPayMethod;
@synthesize delegate=_delegate;
@synthesize titleLabel=_titleLabel;
@synthesize responseXMLParser=_responseXMLParser;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        _HUD=nil;
        //DownloadedErrorData=nil;
        //DownloadedCreditCardData=nil;
        //_cardUpdatedSuccessfully=FALSE;
        _responseData=[[NSMutableData alloc]init];
        _conRequest=nil;
        _responseXMLParser=nil;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    //self.responseData = [[NSMutableData alloc] init ];
    NSString *WelcomeString = [NSString stringWithFormat:@"%@",@"Payment Types"];
    
    UIColor *background = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
    self.title=WelcomeString;
    
    
    self.backView.layer.masksToBounds = YES;
    self.backView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    
    self.atStoreBackView.layer.masksToBounds = YES;
    self.atStoreBackView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    
    self.cardBackView.layer.masksToBounds = YES;
    self.cardBackView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    
    self.backButton.layer.masksToBounds = YES;
    self.backButton.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    
    self.titleLabel.layer.masksToBounds = YES;
    self.titleLabel.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    
    
    // Button gradients
    [myCommonAppDelegate prepareButtonForGradient:self.cardDeleteButton];
    [myCommonAppDelegate prepareButtonForGradient:self.backButton];
    [myCommonAppDelegate prepareButtonForGradient:self.addCardButton];
    [myCommonAppDelegate prepareButtonForGradient:self.updateButton];
    
    CAGradientLayer *cardDelGrad =  [myCommonAppDelegate makeRedGradient];
    //CAGradientLayer *cardDelGrad =  [myCommonAppDelegate makeGreyGradient];
    cardDelGrad.frame = self.cardDeleteButton.bounds;
    [self.cardDeleteButton.layer insertSublayer:cardDelGrad atIndex:0];
    
    CAGradientLayer *backGrad =  [myCommonAppDelegate makeGreyGradient];
    backGrad.frame = self.backButton.bounds;
    [self.backButton.layer insertSublayer:backGrad atIndex:0];
    
    CAGradientLayer *addCardGrad =  [myCommonAppDelegate makeGreenGradient];
    addCardGrad.frame = self.addCardButton.bounds;
    [self.addCardButton.layer insertSublayer:addCardGrad atIndex:0];

    
    CAGradientLayer *updateGrad =  [myCommonAppDelegate makeGreenGradient];
    updateGrad.frame = self.updateButton.bounds;
    [self.updateButton.layer insertSublayer:updateGrad atIndex:0];

    UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithTitle:@"Update" style:UIBarButtonItemStylePlain
                                                                   target:self action:@selector(doUpdate:)];
    self.navigationItem.rightBarButtonItem = rightButton;
    
    self.currentPayMethod = [[myCommonAppDelegate UserInfo] userPayMethod];
    [self.atStoreCheck setText:LOCATION_PAY_IN_STORE_STRING];
    
    // Now set up some state and hide stuff if necessary.
    [self updateState];
    
}

-(IBAction)payAtStoreChecked:(id)sender
{
    if([self.atStoreCheck checked]==TRUE)
    {
        self.currentPayMethod=[NSNumber numberWithInteger:LOCATION_PAY_METHOD_IN_STORE];
    }
    else
    {
        self.currentPayMethod=[NSNumber numberWithInteger:LOCATION_PAY_METHOD_UNKNOWN];
    }
   [self updateState];
}

-(IBAction)payInAppChecked:(id)sender
{
    if([self.cardCheck checked]==TRUE)
    {
        self.currentPayMethod=[NSNumber numberWithInteger:LOCATION_PAY_METHOD_IN_APP];
    }
    else
    {
        self.currentPayMethod=[NSNumber numberWithInteger:LOCATION_PAY_METHOD_UNKNOWN];
    }
    [self updateState];
}

-(void)updateState
{

    if([ self.currentPayMethod isEqualToNumber: [NSNumber numberWithInteger:LOCATION_PAY_METHOD_IN_STORE]])
    {
        // Disable the credit card check box because we can only have one selected.
        [self.cardCheck setChecked:FALSE];
        [self.atStoreCheck setChecked:TRUE];
    }
     
    
    // If no credit card, hide the credit card line and show the add card button
    NSString *CardID = [[myCommonAppDelegate UserCreditCardInfo] valueForKey:ID_ATTR];
    if( (nil==CardID)  || ([CardID isEqualToString:@""]) || ([CardID isEqualToString:@"0"]))
    {
        // No Card;
        [self.addCardButton setHidden:NO];
        [self.cardBackView setHidden:YES];
        
    }
    else
    {
        // Has Card
        NSString *CardDescr = [NSString stringWithFormat:@"%@ ...(%@)",
                               [[myCommonAppDelegate UserCreditCardInfo] valueForKey:USER_CREDIT_CARD_DESCR_ATTR],
                               [[myCommonAppDelegate UserCreditCardInfo] valueForKey:USER_CREDIT_CARD_LAST_FOUR_ATTR]];
        [self.cardCheck setText:CardDescr];
        if([self.currentPayMethod isEqualToNumber: [NSNumber numberWithInteger:LOCATION_PAY_METHOD_IN_APP]])
        {
            [self.cardCheck setChecked:TRUE];
            [self.atStoreCheck setChecked:FALSE];
            [self.cardBackView setHidden:NO];
        }
        [self.addCardButton setHidden:YES];
    }

}

-(IBAction)doDeleteCard:(id)sender
{
    NSString *cardIDAsStr = [[myCommonAppDelegate UserCreditCardInfo] valueForKey:ID_ATTR];
    
    if( (cardIDAsStr ==nil) || ([cardIDAsStr length]==0))
    {
        // TODO FIX THIS AT SOME POINT
        return;
    }
       
    NSString *myRequestString = [NSString stringWithFormat:@"%@=%@&%@=%@",
                                 USER_COMMAND,DELETE_CREDIT_CARD_CMD_AND_RESP,
                                 CREDIT_CARD_ID_CMD_PARAM,cardIDAsStr];
    
    
    NSString *myRequestStringEscaped = [myRequestString stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    
    NSData *myRequestData = [ NSData dataWithBytes: [ myRequestStringEscaped UTF8String ] length: [ myRequestStringEscaped length ] ];
    
    NSString *URLString = [NSString stringWithFormat:@"%@%@",BASE_URL,USER_PAGE];
    
    NSMutableURLRequest *request = [ [ NSMutableURLRequest alloc ] initWithURL: [ NSURL URLWithString:  URLString] ];
    [ request setHTTPMethod: @"POST" ];
    [ request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"content-type"];
    [ request setHTTPBody: myRequestData ];
    
    
    self.responseXMLParser = [[fcXMLParserMisc alloc] initWithRequestType:XMLParserMiscRequestTypeDeleteCreditCard];
    
    self.conRequest = [[NSURLConnection alloc] initWithRequest:request delegate:self] ;
    
    self.HUD = [MBProgressHUD showHUDAddedTo:self.view animated:YES] ;
    self.HUD.dimBackground = YES;
    self.HUD.labelText=PROGRESS_DELETE_CREDIT_CARD_MESSAGE;
    self.HUD.delegate=self;
    
    
}
-(IBAction)doUpdate:(id)sender
{
    
    if( ([self.atStoreCheck checked]!=TRUE) && ([self.cardCheck checked]!=TRUE))
    {
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: PAY_METHOD_ALERT_TITLE
                                   message: PAY_METHOD_ALERT_MUST_CHOOSE_ONE_ALERT_MESSAGE
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
        
        return;
    }
    
    
    NSString *URLString = [NSString stringWithFormat:@"%@%@?%@=%@&%@=%@",
                           BASE_URL,USER_PAGE,
                           USER_COMMAND,UPDATE_PAYMENT_METHOD_COMMAND,
                           LOCATION_PAY_METHOD_ATTR,self.currentPayMethod];
    
    //FC_Log(@"%@",URLString);
    
    self.responseXMLParser = [[fcXMLParserMisc alloc] initWithRequestType:XMLParserMiscRequestTypeUpdatePayMethod];
    
    NSURLRequest *theRequest=[NSURLRequest requestWithURL:[NSURL URLWithString:URLString]
                              
                                              cachePolicy:NSURLRequestUseProtocolCachePolicy
                              
                                          timeoutInterval:60.0];
    self.conRequest = [[NSURLConnection alloc] initWithRequest:theRequest delegate:self];
    
    self.HUD = [MBProgressHUD showHUDAddedTo:self.view animated:NO] ;
    self.HUD.dimBackground = YES;
    self.HUD.labelText=PROGRESS_UPDATE_PAY_METHOD_MESSAGE;
    self.HUD.delegate=self;
    

}

-(IBAction)doBack:(id)sender
{
    [self doGoBack];
}

-(void) doGoBack
{
    if([self.delegate respondsToSelector:@selector(childFinished)])
    {
        [self.delegate childFinished];
    }
    
    [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    [self.responseData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    [self.responseData appendData:data];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    //[[NSAlert alertWithError:error] runModal];
    
    [self.HUD hide:YES afterDelay:HUD_HIDE_DELAY_DEFAULT];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    
    self.conRequest=nil;
    // Once this method is invoked, "responseData" contains the complete result
    [self.HUD hide:YES afterDelay:HUD_HIDE_DELAY_DEFAULT];
    
    NSXMLParser *parser = [[NSXMLParser alloc] initWithData:self.responseData];
    
    [parser setDelegate: self.responseXMLParser];
    
    [parser setShouldProcessNamespaces:YES];
#ifdef FC_DO_LOG
     NSString* newStr = [[NSString alloc] initWithData:self.responseData encoding:NSUTF8StringEncoding];
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
    
    if(self.responseXMLParser.signonError==TRUE)
    {
        [[myCommonAppDelegate navController] popToRootViewControllerAnimated:NO];
        self.responseXMLParser=nil;
        return;
        
    }
    if(self.responseXMLParser.responseOK!=TRUE)
    {
    
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: RESPONSE_ERROR_ALERT_TITLE
                                   message: [self.responseXMLParser.error  makeErrorText]
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
        self.responseXMLParser=nil;
        return;
    }
    else
    {
        // Success !
        if([self.responseXMLParser requestType]==XMLParserMiscRequestTypeDeleteCreditCard)
        {
            //TODO WE NEED TO FIX CREDIT CARD STORAGES !
            [[myCommonAppDelegate UserCreditCardInfo] removeAllObjects];
            self.responseXMLParser=nil;
            [self updateState];
            return;
            
        }
        else if([self.responseXMLParser requestType]==XMLParserMiscRequestTypeUpdatePayMethod)
        {
            self.responseXMLParser=nil;
            [[myCommonAppDelegate UserInfo] setUserPayMethod:self.currentPayMethod];
            [myCommonAppDelegate goToTopPageComitted];
        }
        return;
    }
    
}


-(IBAction)doAddCard:(id)sender
{
    
     fcCreditCardViewController *cardView =nil;
     if([myCommonAppDelegate isRetina4Display])
     {
     cardView = [[fcCreditCardViewController alloc] initWithNibName:@"fcCreditCardViewController_ret4" bundle:nil];
     }
     else
     {
     cardView = [[fcCreditCardViewController alloc] initWithNibName:@"fcCreditCardViewController" bundle:nil];
     }
     [ [myCommonAppDelegate navController] pushViewController:cardView animated:NO];
     
     
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
