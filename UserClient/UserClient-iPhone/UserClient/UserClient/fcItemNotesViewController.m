//
//  fcItemNotesViewController.m
//  UserClient
//
//  Created by Nick Ambrose on 9/15/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>
#import "fcItemNotesViewController.h"
#import "fcAddEditItemViewController.h"
#import "Constants.h"
#import "fcAppDelegate.h"

@interface fcItemNotesViewController ()

@end

@implementation fcItemNotesViewController
@synthesize notesTextView=_notesTextView;
@synthesize notes=_notes;

- (id) initWithNibName:(NSString *)nibNameOrNil andNotes: (NSString*)notes
{
    self = [super initWithNibName:nibNameOrNil bundle:nil];
    if (self)
    {
        // Custom initialization
        _notes = [notes copy];
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    UIColor *background = [[UIColor alloc] initWithPatternImage:
                           [UIImage imageNamed:IMG_BACKGROUND_IMAGE_NAME]];
    self.view.backgroundColor = background;
    
    self.notesTextView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    self.groupView.layer.cornerRadius = DEFAULT_CORNER_RADIUS;
    
    self.title = @"Notes";
    
    //UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithTitle:@"Cancel" style:UIBarButtonItemStyleBordered target:self
    //                                                                  action:nil];
    //    [self.navigationItem setBackBarButtonItem: backButton];
    UIBarButtonItem *rightButton = [[UIBarButtonItem alloc] initWithTitle:@"Save" style:UIBarButtonItemStylePlain
                                                                       target:self action:@selector(doneCommit)];
    self.navigationItem.rightBarButtonItem = rightButton;
        
    [self.notesTextView setText:self.notes];
    
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
- (void) doneCommit
{
    if([self.delegate respondsToSelector:@selector(childFinishedWithNotes:)])
    {
        [self.delegate childFinishedWithNotes:self.notesTextView.text];
    }
    
    [ [myCommonAppDelegate navController] popViewControllerAnimated:NO];
}
@end
