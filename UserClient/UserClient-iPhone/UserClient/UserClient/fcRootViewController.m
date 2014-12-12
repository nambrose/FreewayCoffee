//
//  fcRootViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcRootViewController.h"
#import "fcSignupViewController.h"
#import "fcItemsListViewController.h"
#import "fcAppDelegate.h"
#import "iToast.h"
#import "iTToastMessage.h"
#import "Constants.h"
#import "fcAppDelegate.h"
#import "XMLHelper.h"


@implementation fcRootViewController

@synthesize usernameField;
@synthesize passwordField;
@synthesize loginButton;
@synthesize goSignupButton;
@synthesize loginIndicator;
@synthesize groupView;
@synthesize scrollView;
@synthesize existingUserLabel;
@synthesize signonSuccess=_signonSuccess;
@synthesize conRequest=_conRequest;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        responseData = [[NSMutableData alloc] init ];
        _signonSuccess=FALSE;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.view addSubview:scrollView];
    UIColor *background = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
 
    [ [myCommonAppDelegate navController] setNavigationBarHidden:TRUE];
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@"Sign Out" style:UIBarButtonItemStyleBordered target:self action:nil];
    [self.navigationItem setBackBarButtonItem: backButton];
    self.groupView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    self.groupView.layer.masksToBounds = YES;
    
    self.existingUserLabel.layer.cornerRadius=DEFAULT_CORNER_RADIUS;
    
    if( [myCommonAppDelegate areUsernameAndPasswordSet]==TRUE)
    {
        // Try to login
        [self populateFields];
        [self tryLogin];
    }
    else
    {
        [self doGoSignup];
    }
     
}
- (void)viewWillAppear:(BOOL)animated
{
    [ [myCommonAppDelegate navController] setNavigationBarHidden:TRUE];
    [super viewWillAppear:animated];
    
}
- (void) viewDidAppear:(BOOL)animated
{
 //   [ [myCommonAppDelegate navController] setNavigationBarHidden:TRUE];
    [super viewDidAppear:animated];
}
- (void)viewDidUnload
{
    [self setGroupView:nil];
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
- (BOOL)textFieldShouldReturn:(UITextField *)textBoxName {
	[textBoxName resignFirstResponder];
	return YES;
}


- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

/*
 // Implement loadView to create a view hierarchy programmatically, without using a nib.
 - (void)loadView
 {
 }
 */

/*
 // Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
 - (void)viewDidLoad
 {
 [super viewDidLoad];
 }
 */




- (IBAction) login: (id) sender
{
    [self tryLogin];
}
- (void) populateFields
{
    usernameString = [myCommonAppDelegate getUsername];
    [usernameField setText:usernameString];
    
    passwordString = [myCommonAppDelegate getPassword];
    [passwordField setText:passwordString];
    
}
- (void) tryLogin
{
    [self.usernameField resignFirstResponder];
    [self.passwordField resignFirstResponder];
    
    
    if([myCommonAppDelegate networkUp]!=TRUE)
    {
        self.loginIndicator.hidden = TRUE;
        [self.loginIndicator stopAnimating];
        [self showNetworkDownToast:self.view];
        return;
    }

    self.loginIndicator.hidden = FALSE;
	[self.loginIndicator startAnimating];
	
	loginButton.enabled = FALSE;
    NSString *Temp = [[self usernameField] text];
    usernameString = [[NSString alloc] initWithString:Temp];
    
    if((usernameString==nil) || ([usernameString length] ==0))
    {
        
        //errorField.text =ERROR_USERNAME_NOT_SET;
        //errorField.hidden=FALSE;
        ITToastMessage *Toast = [[ITToastMessage alloc] initWithDuration:ITToastMessageDefaultDuration andText:ERROR_USERNAME_NOT_SET]  ;
        
        [Toast displayInView:[self view]];
        //[Toast release];
        
        
        loginIndicator.hidden = TRUE;
        [loginIndicator stopAnimating];
        return;
    }
    if(![myCommonAppDelegate validateEmail:usernameString])
    {
        ITToastMessage *Toast = [[ITToastMessage alloc] initWithDuration:ITToastMessageDefaultDuration andText:ERROR_NAME_NOT_VALID]  ;
        [Toast displayInView:[self view]];
        return;
    }
    
    Temp = [[self passwordField] text];
    passwordString = [[NSString alloc] initWithString:Temp];
    if( (passwordString==nil) || ( [passwordString length]==0))
    {
        //errorField.text=ERROR_PASSWORD_NOT_SET;
        //errorField.hidden=FALSE;
        ITToastMessage *Toast = [[ITToastMessage alloc] initWithDuration:ITToastMessageDefaultDuration andText:ERROR_PASSWORD_NOT_SET]  ;
        
        [Toast displayInView:[self view]];
        //[Toast release];
        
        
        loginIndicator.hidden = TRUE;
        [loginIndicator stopAnimating];
        return;
    }
    if( (passwordString==nil) || ( [passwordString length]<PASSWORD_MIN_LENGTH))
    {
        NSString *errText = [NSString stringWithFormat:ERROR_PASSWORD_TOO_SHORT,PASSWORD_MIN_LENGTH];
        
        ITToastMessage *Toast = [[ITToastMessage alloc] initWithDuration:ITToastMessageDefaultDuration andText:errText]  ;
        [Toast displayInView:[self view]];
        loginIndicator.hidden = TRUE;
        [loginIndicator stopAnimating];
        return;
    }

    self.signonSuccess=FALSE;
    UIDevice *myDevice = [UIDevice currentDevice];
    
    NSString *name = [myDevice name];
    NSString *sysName = [myDevice systemName];
    NSString *sysVer = [myDevice systemVersion];
    NSString *model = [myDevice model];
    
    NSDictionary *infoDict = [[NSBundle mainBundle] infoDictionary];
    NSString *build = [infoDict objectForKey:@"CFBundleVersion"];
    NSString *appVerName = [infoDict objectForKey:@"CFBundleShortVersionString"];
    
    
    NSString *myRequestString = [NSString stringWithFormat:@"signon_user_email=%@&signon_user_password=%@&%@=%d&%@=%@&%@=%@&%@=%@&%@=%@&%@=%@&%@=%@&%@=%@",
                                 [XMLHelper URLEncodedString:usernameString],
                                 [XMLHelper URLEncodedString:passwordString],
                                 MAIN_SCHEMA_COMPAT_LEVEL_ATTR,MAIN_COMPAT_LEVEL,
                                 APP_CLIENT_TYPE,APP_CLIENT_VALUE_IOS,
                                 IOS_DEVICE_NAME,[XMLHelper URLEncodedString:name],
                                 IOS_DEVICE_SYS_NAME,[XMLHelper URLEncodedString:sysName],
                                 IOS_DEVICE_SYS_VER,[XMLHelper URLEncodedString:sysVer],
                                 IOS_DEVICE_MODEL,[XMLHelper URLEncodedString:model],
                                 IOS_APP_VER_NAME_STRING,[XMLHelper URLEncodedString:appVerName],
                                 IOS_APP_BUILD_STRING,[XMLHelper URLEncodedString:build]];
    
    
    
    //NSString *myRequestStringEscaped = [myRequestString stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    
    NSData *myRequestData = [ NSData dataWithBytes: [ myRequestString UTF8String ] length: [ myRequestString length ] ];
    
    NSString *URLString = [NSString stringWithFormat:@"%@%@",BASE_URL,SIGNON_PAGE];
    
    NSMutableURLRequest *request = [ [ NSMutableURLRequest alloc ] initWithURL: [ NSURL URLWithString:  URLString] ];
    [ request setHTTPMethod: @"POST" ];
    [ request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"content-type"];
    [ request setHTTPBody: myRequestData ];
    [ request setTimeoutInterval:[myCommonAppDelegate getRequestShortTimeout]];    
    
    /*
    [[[NSURLConnection alloc] initWithRequest:request delegate:self] autorelease];*/
    self.conRequest = [[NSURLConnection alloc] initWithRequest:request delegate:self];
    
    //HUD = [[MBProgressHUD showHUDAddedTo:self.view animated:YES] retain];
    HUD = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
           
    HUD.dimBackground = YES;
    HUD.labelText=PROGRESS_SIGNIN_MESSAGE;
    HUD.delegate=self;
    
    
    
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    if( [elementName isEqualToString:SIGNON_RESPONSE_TAG])
    {
        NSString * Result = (NSString*)[attributeDict objectForKey:RESULT_ATTR];
        if( [Result isEqualToString:SIGNON_OK])
        {
            self.signonSuccess=TRUE;
            //[[iToast makeText:SIGNUP_SUCCESS_TOAST_MESSAGE] show];
            ITToastMessage *Toast = [[ITToastMessage alloc] initWithDuration:ITToastMessageDefaultDuration andText:SIGNON_SUCCESS_TOAST_MESSAGE]  ;
            
            [Toast display];
            //[Toast release];
            
            // Now save email, name and password to the prefs for next time
            [myCommonAppDelegate storeUsername:usernameString];
            
            [myCommonAppDelegate storePassword:passwordString];
            [myCommonAppDelegate ClearAllDownloadedData:TRUE];
            
            fcItemsListViewController *itemsList = [[ fcItemsListViewController alloc] initWithNibName:@"fcItemsListViewController" bundle:nil];
             [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
            [ [myCommonAppDelegate navController] pushViewController:itemsList animated:NO];
            
            
        }
        else
        {
            /*
             UIAlertView *alert =
             [[UIAlertView alloc] initWithTitle: SIGNUP_FAILED_ALERT_TITLE
             message: SIGNUP_FAILED_ALERT_MESSAGE
             delegate: self
             cancelButtonTitle: @"OK"
             otherButtonTitles: nil];
             [alert show];
             [alert release];
             */
            /*ITToastMessage *Toast = [[ITToastMessage alloc] initWithDuration:ITToastMessageDefaultDuration andText:SIGNON_FAILED_TOAST_MESSAGE]  ;*/
            
             ITToastMessage *Toast = [[ITToastMessage alloc] initWithDuration:ITToastMessageDefaultDuration
                                                                      andText:@"Signon Failed"];
            
            [Toast display];
            //[Toast release];
            loginIndicator.hidden = TRUE;
            [loginIndicator stopAnimating];
            
            
            
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
    NSString *strData = [[NSString alloc]initWithData:responseData encoding:NSUTF8StringEncoding];
    FC_Log(@"%@", strData);
#endif
    
    if([parser parse]!=YES)
    {
       // TODO ERROR
    }
    loginIndicator.hidden = TRUE;
    [loginIndicator stopAnimating];
    if(self.signonSuccess!=TRUE)
    {
        /*
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: SIGNON_FAILED_ALERT_TITLE
                                   message: [myCommonAppDelegate makeLastErrorText]
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
         */
        NSString *message = [NSString stringWithFormat:@"%@: %@", SIGNON_FAILED_ALERT_TITLE,[myCommonAppDelegate makeLastErrorText]];
        ITToastMessage *Toast = [[ITToastMessage alloc] initWithDuration:ITToastMessageDefaultDuration
                                                                 andText:message];
        
        [Toast display];
    }
}

- (void)doGoSignup
{
    
    
    // Remove ourselves from the stack
    //[ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
    fcSignupViewController *signupView = nil;
    if([myCommonAppDelegate isRetina4Display])
    {
        signupView = [[fcSignupViewController alloc] initWithNibName:@"fcSignupViewController_ret4" bundle:nil];
    }
    else
    {
        signupView = [[fcSignupViewController alloc] initWithNibName:@"fcSignupViewController" bundle:nil];
    }
    [ [myCommonAppDelegate navController] pushViewController:signupView animated:NO];
    

}

- (IBAction)goSignup: (id) sender
{
    // Go to signup page.
    [self doGoSignup];
    
}

#pragma mark -
#pragma mark MBProgressHUDDelegate methods

- (void)hudWasHidden:(MBProgressHUD *)hud
{
    // Remove HUD from screen when the HUD was hidded
    [HUD removeFromSuperview];
    //[HUD release];
	HUD = nil;
}
- (void) showNetworkDownToast:(UIView*)v
{
    ITToastMessage *Toast = [[ITToastMessage alloc] initWithDuration:ITToastMessageDefaultDuration
                                                             andText:@"The network is down. Please try later."];
    
    [Toast display];
}

@end
