//
//  fcItemNotesViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 9/15/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ItemAddEditChildFinished;

@interface fcItemNotesViewController : UIViewController

@property (nonatomic,weak) IBOutlet UITextView *notesTextView;
@property (nonatomic, weak) IBOutlet UIView *groupView;

@property (nonatomic,strong) NSString *notes;

@property (nonatomic, weak) id<ItemAddEditChildFinished> delegate;

- (id) initWithNibName:(NSString *)nibNameOrNil andNotes: (NSString*)notes;
- (void) doneCommit;
@end
