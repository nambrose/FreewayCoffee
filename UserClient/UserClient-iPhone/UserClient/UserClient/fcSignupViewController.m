//
//  fcSignupViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcSignupViewController.h"
#import "ITToastMessage.h"
#import "MBProgressHUD.h"
#import "Constants.h"
#import "fcAppDelegate.h"
#import "fcRootViewController.h"
#import "fcItemsListViewController.h"
#import "XMLHelper.h"

@implementation fcSignupViewController

@synthesize usernameField;
@synthesize nameField;
@synthesize passwordField;
@synthesize passwordAgainField;
@synthesize errorField;
@synthesize signupButton;
@synthesize goSignonButton;
@synthesize signupIndicator;
@synthesize scrollView;
@synthesize groupView;
@synthesize signupLabel;
@synthesize goSignonLabel;

@synthesize responseData=_responseData;
@synthesize usernameString=_usernameString;
@synthesize nameString=_nameString;
@synthesize passwordString=_passwordString;
@synthesize passwordAgainString=_passwordAgainString;
@synthesize signupSuccess=_signupSuccess;
@synthesize conRequest=_conRequest;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        _signupSuccess=FALSE;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self.view addSubview:scrollView];
    self.title = @"Signup View";
    responseData = [[NSMutableData alloc] init ];
    
    
    UIColor *background = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:[myCommonAppDelegate getBackgroundImageName]]];
    self.view.backgroundColor = background;
    [ [myCommonAppDelegate navController] setNavigationBarHidden:TRUE];
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@"Sign Out" style:UIBarButtonItemStyleBordered target:self action:nil];
    [self.navigationItem setBackBarButtonItem: backButton];
    
    [self.navigationItem setBackBarButtonItem: backButton];
    self.groupView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    self.groupView.layer.masksToBounds = YES;
    
    self.signupLabel.layer.cornerRadius=DEFAULT_CORNER_RADIUS;
    self.goSignonLabel.layer.cornerRadius=DEFAULT_CORNER_RADIUS;
    
}
- (BOOL)textFieldShouldReturn:(UITextField *)textBoxName {
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
- (IBAction) doSignup: (id) sender
{
    [self.usernameField resignFirstResponder];
    [self.nameField resignFirstResponder];
    [self.passwordField resignFirstResponder];
    [self.passwordAgainField resignFirstResponder];
    
    if([myCommonAppDelegate networkUp]!=TRUE)
    {
        signupIndicator.hidden = TRUE;
        [signupIndicator stopAnimating];
        [[myCommonAppDelegate topController] showNetworkDownToast:self.view];
                
        return;
    }
    signupIndicator.hidden = FALSE;
    [signupIndicator startAnimating];
    
    NSString *Temp = [[self usernameField] text];
    usernameString = [[NSString alloc] initWithString:Temp];
    
    if((usernameString==nil) || ([usernameString length] ==0))
    {
        
        errorField.text =ERROR_USERNAME_NOT_SET;
        errorField.hidden=FALSE;
        signupIndicator.hidden = TRUE;
        [signupIndicator stopAnimating];
        return;
    }
    
    if(![myCommonAppDelegate validateEmail:usernameString])
    {
        errorField.text=ERROR_NAME_NOT_VALID;
        errorField.hidden=FALSE;
        signupIndicator.hidden = TRUE;
        [signupIndicator stopAnimating];
        return;
    }
    Temp = [[self nameField] text];
    nameString = [[NSString alloc] initWithString:Temp];
    
    if( (nameString==nil) || ( [nameString length]==0))
    {
        errorField.text=ERROR_NAME_NOT_SET;
        errorField.hidden=FALSE;
        signupIndicator.hidden = TRUE;
        [signupIndicator stopAnimating];
        return;
    }
    Temp = [[self passwordField] text];
    passwordString = [[NSString alloc] initWithString:Temp];
    if( (passwordString==nil) || ( [passwordString length]==0))
    {
        errorField.text=ERROR_PASSWORD_NOT_SET;
        errorField.hidden=FALSE;
        signupIndicator.hidden = TRUE;
        [signupIndicator stopAnimating];
        return;
    }
    if( (passwordString==nil) || ( [passwordString length]<PASSWORD_MIN_LENGTH))
    {
        errorField.text=[NSString stringWithFormat:ERROR_PASSWORD_TOO_SHORT,PASSWORD_MIN_LENGTH];
        errorField.hidden=FALSE;
        signupIndicator.hidden = TRUE;
        [signupIndicator stopAnimating];
        return;
    }
    
    Temp = [[self passwordAgainField] text];
    
    passwordAgainString=Temp;
    
    if( (passwordAgainString==nil) || ( [passwordAgainString length]==0))
    {
        errorField.text=ERROR_PASSWORD_AGAIN_NOT_SET;
        errorField.hidden=FALSE;
        signupIndicator.hidden = TRUE;
        [signupIndicator stopAnimating];
        return;
    }
    
    if( [passwordString isEqualToString:passwordAgainString] != TRUE)
    {
        errorField.text=ERROR_PASSWORD_NOMATCH;
        errorField.hidden=FALSE;
        signupIndicator.hidden = TRUE;
        [signupIndicator stopAnimating];
        return;
    }
    
    self.signupSuccess=FALSE;
    UIDevice *myDevice = [UIDevice currentDevice];
    
    NSString *name = [myDevice name];
    NSString *sysName = [myDevice systemName];
    NSString *sysVer = [myDevice systemVersion];
    NSString *model = [myDevice model];
    
    NSDictionary* infoDict = [[NSBundle mainBundle] infoDictionary];
    
    NSString *build = [infoDict objectForKey:@"CFBundleVersion"];
    NSString *appVerName = [infoDict objectForKey:@"CFBundleShortVersionString"];
    
    NSString *myRequestString = [NSString stringWithFormat:@"user_email=%@&user_password=%@&user_name=%@&%@=%d&%@=%@&%@=%@&%@=%@&%@=%@&%@=%@&%@=%@&%@=%@",
                                 [XMLHelper URLEncodedString:usernameString],
                                 [XMLHelper URLEncodedString:passwordString],
                                 [XMLHelper URLEncodedString:nameString],
                                 MAIN_SCHEMA_COMPAT_LEVEL_ATTR,MAIN_COMPAT_LEVEL,
                                 APP_CLIENT_TYPE,APP_CLIENT_VALUE_IOS,
                                 IOS_DEVICE_NAME,[XMLHelper URLEncodedString:name],
                                 IOS_DEVICE_SYS_NAME,[XMLHelper URLEncodedString:sysName],
                                 IOS_DEVICE_SYS_VER,[XMLHelper URLEncodedString:sysVer],
                                 IOS_DEVICE_MODEL,[XMLHelper URLEncodedString:model],
                                 IOS_APP_VER_NAME_STRING,[XMLHelper URLEncodedString:appVerName],
                                 IOS_APP_BUILD_STRING,[XMLHelper URLEncodedString:build]];
    
   // NSString *myRequestStringEscaped = [myRequestString stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    
    NSData *myRequestData = [ NSData dataWithBytes: [ myRequestString UTF8String ] length: [ myRequestString length ] ];
    
    NSString *URLString = [NSString stringWithFormat:@"%@%@",BASE_URL,SIGNUP_PAGE];
    
    NSMutableURLRequest *request = [ [ NSMutableURLRequest alloc ] initWithURL: [ NSURL URLWithString:  URLString] ];
    [ request setHTTPMethod: @"POST" ];
    [ request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"content-type"];
    [ request setHTTPBody: myRequestData ];
    [ request setTimeoutInterval:[myCommonAppDelegate getRequestShortTimeout]];
    
    self.conRequest=[[NSURLConnection alloc] initWithRequest:request delegate:self] ;
    
    HUD = [MBProgressHUD showHUDAddedTo:self.view animated:NO];
    
    HUD.dimBackground = YES;
    HUD.labelText=PROGRESS_SIGNUP_MESSAGE;
    HUD.delegate=self;
    
    
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    if( [elementName isEqualToString:REGISTER_RESPONSE_TAG])
    {
        NSString * Result = (NSString*)[attributeDict objectForKey:RESULT_ATTR];
        if( [Result isEqualToString:SUCCESS_REGISTER_SIGNIN])
        {
            self.signupSuccess=TRUE;
                        
        }
        else
        {
            self.signupSuccess=FALSE;
        }
    }
    else if ([elementName isEqualToString:ERROR_TAG])
    {
        [myCommonAppDelegate setLastError:[XMLHelper convertStringsFromWeb:attributeDict]];
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
    errorField.hidden=TRUE;
    
    NSXMLParser *parser = [[NSXMLParser alloc] initWithData:responseData];
    [parser setDelegate: self];
    
#ifdef FC_DO_LOG
    
    [parser setShouldProcessNamespaces:YES];
     NSString* newStr = [[NSString alloc] initWithData:responseData encoding:NSUTF8StringEncoding];
     FC_Log(@"%@",newStr);
#endif
    
    if([parser parse]!=YES)
    {
        // TODO ERROR
    }
    if(self.signupSuccess==TRUE)
    {
        //[[iToast makeText:SIGNUP_SUCCESS_TOAST_MESSAGE] show];
        ITToastMessage *Toast = [[ITToastMessage alloc] initWithDuration:ITToastMessageDefaultDuration andText:SIGNUP_SUCCESS_TOAST_MESSAGE]  ;
        
        [Toast displayInView:[self view]];
        
        
        // Now save email, name and password to the prefs for next time
        [myCommonAppDelegate storeUsername:usernameString];
        [myCommonAppDelegate storeName:nameString];
        [myCommonAppDelegate storePassword:passwordString];
        [myCommonAppDelegate ClearAllDownloadedData:TRUE];
        
        
        fcItemsListViewController *itemsList = [[ fcItemsListViewController alloc] initWithNibName:@"fcItemsListViewController" bundle:nil];
        [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
        [ [myCommonAppDelegate navController] pushViewController:itemsList animated:NO];

    }
    else
    {
        UIAlertView *alert =
        [[UIAlertView alloc] initWithTitle: SIGNUP_FAILED_ALERT_TITLE
                                   message: [myCommonAppDelegate makeLastErrorText]
                                  delegate: self
                         cancelButtonTitle: @"OK"
                         otherButtonTitles: nil];
        [alert show];
    }
}

- (void)viewWillAppear:(BOOL)animated
{
    [ [myCommonAppDelegate navController] setNavigationBarHidden:TRUE];
    [super viewWillAppear:animated];
    
}
- (void) viewDidAppear:(BOOL)animated
{
   // [ [myCommonAppDelegate navController] setNavigationBarHidden:TRUE];
    [super viewDidAppear:animated];
}
- (IBAction) goSignon: (id) sender
{
    /*
    fcRootViewController *signonView = nil;
    if([myCommonAppDelegate isRetina4Display])
    {
        signonView = [[fcRootViewController alloc] initWithNibName:@"fcRootViewController_ret4" bundle:nil];
    }
    else
    {
        signonView = [[fcRootViewController alloc] initWithNibName:@"fcRootViewController" bundle:nil];
    }
     */
    // Remove ourselves from the stack
    [[myCommonAppDelegate navController] popViewControllerAnimated:NO];
    
    // Add the Signon Controller
    //[ [myCommonAppDelegate navController] pushViewController:signonView animated:NO];
    
}

#pragma mark -
#pragma mark MBProgressHUDDelegate methods

- (void)hudWasHidden:(MBProgressHUD *)hud
{
    // Remove HUD from screen when the HUD was hidded
    [HUD removeFromSuperview];
	HUD = nil;
}


@end
