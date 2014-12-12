//
//  fcOrderResponseViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 9/16/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>

#import "fcOrderResponseViewController.h"
#import "fcAppDelegate.h"
#import "fcLastOrder.h"
#import "ITToastMessage.h"
#import "XMLHelper.h"
#import "fcRootViewController.h"
#import "fcLastOrderItem.h"
#import "fcItemsListViewController.h"
#import "fcUserInfo.h"
#import "fcLocationAllowedArrivalMethod.h"
#import "fcLocation.h"
#import "fcOrderFinishedViewController.h"


@interface fcOrderResponseViewController ()

@end

@implementation fcOrderResponseViewController
@synthesize checkbox=_checkbox;
@synthesize actionButton=_actionButton;
@synthesize mainText=_mainText;
@synthesize groupView=_groupView;
@synthesize conRequest=_conRequest;

@synthesize parseSuccessful=_parseSuccessful;
@synthesize wasServerResponseGood=_wasServerResponseGood;
@synthesize wasOrderHereResponse=_wasOrderHereResponse;
@synthesize signonError=_signonError;
@synthesize gradient=_gradient;
@synthesize delegate=_delegate;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        _parseSuccessful=FALSE;
        _wasServerResponseGood=FALSE;
        _parseSuccessful=FALSE;
        _wasOrderHereResponse=FALSE;
        _signonError=FALSE;
        
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    UIColor *background = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
    
    self.navigationItem.hidesBackButton = YES;
    UIBarButtonItem *barButton = [[UIBarButtonItem alloc]
                                  initWithTitle:@"Order"
                                  style:UIBarButtonItemStyleBordered
                                  target:self
                                  action:@selector(exitCommitted)];
    
    self.gradient = [myCommonAppDelegate makeGreenGradient];
    self.gradient.frame = self.actionButton.bounds;
    
    // #90EE90
    
   /* UIColor *lightGreen = [UIColor colorWithRed:0.0 green:0.6 blue:0.0 alpha:0.8];
    
    self.gradient.colors = [NSArray arrayWithObjects:(id)[lightGreen CGColor], (id)[[UIColor greenColor] CGColor], nil];
    self.gradient.cornerRadius = DEFAULT_CORNER_RADIUS;

    */
    [self.navigationItem setLeftBarButtonItem: barButton];
    
    [myCommonAppDelegate prepareButtonForGradient:self.actionButton];
    
    
    self.mainText.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    [self.mainText setClipsToBounds:YES];
    
    self.groupView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    [self doSetDisplay];
}

- (IBAction) doAction:(id)sender
{
    if([[myCommonAppDelegate lastOrder] orderStatus] ==ORDER_HERE_SENT)
    {
        ITToastMessage *Toast = [[ITToastMessage alloc] initWithDuration:ITToastMessageDefaultDuration
                                                                 andText:@"I've arrived already sent. Awaiting response"]  ;
        
        [Toast displayInView:[self view]];
        return;
    }
    
    else if([[myCommonAppDelegate lastOrder] orderStatus]==ORDER_SUBMITTED)
    {
        if([myCommonAppDelegate networkUp]!=TRUE)
        {
            [[myCommonAppDelegate topController] showNetworkDownToast:self.view];
            
            return;
        }
        [[myCommonAppDelegate lastOrder] setOrderStatus:ORDER_HERE_SENT];
        
        
        NSNumber *orderID = [[myCommonAppDelegate lastOrder] orderID];
        NSString *arriveMode=nil;
        responseData =[[NSMutableData alloc]init];
        
        // This works even if the checkbox is "hidden" because Walkup is not supported because I still set the checkbox state in that case
        if([self.checkbox checked])
        {
            arriveMode = ARRIVE_MODE_WALKUP_STR;
        }
        else
        {
            arriveMode = ARRIVE_MODE_CAR_STR;
        }

        self.signonError=FALSE;
        
        NSString *URLString = [NSString stringWithFormat:@"%@%@?%@=%@&%@=%@&%@=%@",
                               BASE_URL,ORDER_PAGE,
                               USER_COMMAND,ORDER_TIME_HERE_COMMAND,
                               USER_ARRIVE_MODE_CMD_ARG,arriveMode,
                               ORDER_ID,orderID];
        
        
       // FC_Log(@"%@",URLString);
        
        NSURLRequest *theRequest=[NSURLRequest requestWithURL:[NSURL URLWithString:URLString]
                                  
                                                  cachePolicy:NSURLRequestUseProtocolCachePolicy
                                  
                                              timeoutInterval:60.0];
        self.conRequest = [[NSURLConnection alloc] initWithRequest:theRequest delegate:self];
        
        HUD = [MBProgressHUD showHUDAddedTo:self.view animated:YES] ;
        HUD.dimBackground = YES;
        HUD.labelText=PROGRESS_IVE_ARRIVED_MESSAGE;
        HUD.delegate=self;

    }
    
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    if ([elementName isEqualToString:USER_ORDER_HERE_RESPONSE_TAG])
    {
        self.wasOrderHereResponse=TRUE;
        NSString *response = [attributeDict valueForKey:RESULT_ATTR];
        
        if(nil==response)
        {
            return;
        }
        
        response = [XMLHelper stringByDecodingURLFormat:response];
        if([response isEqualToString:OK_ATTR_VALUE])
        {
            self.wasServerResponseGood=TRUE;
            self.parseSuccessful=TRUE;
        }
        
    }
    else if ([elementName isEqualToString:OM_ORDER_ITEM_TAG])
    {
        fcLastOrderItem *item = [self parseLastOrderItem:attributeDict];
        if(nil!=item)
        {
            [[myCommonAppDelegate lastOrder] addLastOrderItem:item];
        }
    }
    else if ([elementName isEqualToString:ORDER_CREDIT_CARD_TAG])
    {
        [[myCommonAppDelegate lastOrder] setOrderCreditCard:[XMLHelper convertStringsFromWeb:attributeDict]];
    }

    else if ([elementName isEqualToString:ORDER_TAG])
    {
        [[myCommonAppDelegate lastOrder] setLastOrder:[XMLHelper convertStringsFromWeb:attributeDict]];
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
    [HUD hide:YES afterDelay:HUD_HIDE_DELAY_DEFAULT];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{

    self.conRequest=nil;
    
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

    
    if( (self.parseSuccessful) && (self.wasServerResponseGood) && (self.wasOrderHereResponse) )
    {
        
        //TODO NOTE: Parsing actually updates last order structures. Ideally we do that in 2-phases in case an error happens. Later. TODO 
        [[myCommonAppDelegate lastOrder] setOrderStatus:ORDER_HERE_OK];
        
        [self goToOrderFinished];
        return;
    }
    else
    {
        if([[myCommonAppDelegate lastOrder] orderStatus]!=ORDER_HERE_OK)
        {
            [[myCommonAppDelegate lastOrder] setOrderStatus:ORDER_SUBMITTED];
            
             ITToastMessage *Toast = [[ITToastMessage alloc] initWithDuration:ITToastMessageDefaultDuration
                                                                      andText:@"I've arrived failed. Please try again"]  ;
             
             [Toast displayInView:[self view]];
             return;
        }
    }
             /*
    else
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
              */
}

- (void) goToOrderFinished
{
    
    [[myCommonAppDelegate navController] popViewControllerAnimated:NO];
    fcOrderFinishedViewController *finishedView=nil;
    
    if([myCommonAppDelegate isRetina4Display])
    {
        finishedView = [[ fcOrderFinishedViewController alloc] initWithNibName:@"fcOrderFinishedViewController_ret4" bundle:nil];
    }
    else
    {
        finishedView = [[ fcOrderFinishedViewController alloc] initWithNibName:@"fcOrderFinishedViewController" bundle:nil];
    }
    [ [myCommonAppDelegate navController] pushViewController:finishedView animated:NO];


}
- (void) doSetDisplay
{
    if( ([[myCommonAppDelegate lastOrder] orderStatus] ==ORDER_SUBMITTED) ||
        ([[myCommonAppDelegate lastOrder] orderStatus] ==ORDER_HERE_SENT))
    {
            
    
        //[myButton setTitle:@"my text" forState:UIControlStateNormal];

        [self.actionButton setTitle:@"I've Arrived" forState:UIControlStateNormal];
    
        self.title=@"Order Submitted";
    
    
        //UIView *view = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 248, 37)] ;
        

        
        //self.finishedGradient=nil;
        [self.actionButton.layer insertSublayer:self.gradient atIndex:0];

        
        NSString *htmlContentString = [[myCommonAppDelegate lastOrder] makeOrderSubmittedText];
        
        [self.mainText loadHTMLString:htmlContentString baseURL:nil];
        //self.groupView.hidden=NO;
        self.checkbox.text = @"Walkup";
        
        fcLocation *location = [myCommonAppDelegate getCurrentLocation];
        if(location!=nil)
        {
            if([location isArrivalMethodAllowed:[NSNumber numberWithInt:ARRIVE_MODE_WALKUP_NUM]]!=TRUE)
            {
                self.checkbox.hidden=YES;
            }
            else
            {
                self.checkbox.hidden=NO;
            }
        }
        else
        {
            self.checkbox.hidden=NO;
        }
        self.checkbox.checked = [myCommonAppDelegate IsUserWalkup];
        self.groupView.backgroundColor = [UIColor whiteColor];
    }
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
////////// LastOrder XML
- (fcLastOrderItem*) parseLastOrderItem:(NSDictionary *)attributeDict
{
    fcLastOrderItem *item = [[fcLastOrderItem alloc]init];
    
    for(NSString *aKey in [ attributeDict allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attributeDict valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        /*
         orderItemID;
         @property (nonatomic,copy) NSString *orderItemDescription;
         @property (nonatomic,copy) NSString *orderItemCost;
         */
        //FC_Log(@"parseUserItem: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:ID_ATTR])
        {
            //[item setOrderItemID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
            [item setOrderItemID:attrValue];
        }
        else if([aKey isEqualToString:OM_ORDER_ITEM_DESCR_ATTR])
        {
            [item setOrderItemDescription:attrValue];
        }
        else if([aKey isEqualToString:OM_ORDER_ITEM_COST_ATTR])
        {
            [item setOrderItemCost:attrValue];
        }
    }
    
    return item;
    
}
- (void) exitCommitted
{
    
    if([self.delegate respondsToSelector:@selector(childFinishedCommit)])
    {
        [self.delegate childFinishedCommit];
    }
    
    [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
}
@end
