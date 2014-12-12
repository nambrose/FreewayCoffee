//
//  fcItemOptionValuePickerViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 9/9/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <UIKit/UIKit.h>

@class fcUserItem;
@class fcUserItemOptionTable;
@class MenuItemOptionGroup;
@class fcItemType;
@class OptionValuePickerCellHolder;
@class fcMenu;

@protocol ItemListChildFinished;

@interface fcItemOptionValuePickerViewController : UIViewController
@property (nonatomic, weak) IBOutlet UIView *TableFooter;
//@property (nonatomic, weak) IBOutlet UIView *TableHeader;

// Table Cell
@property (nonatomic, weak) IBOutlet UITableViewCell *tableCell;
@property (nonatomic, weak) IBOutlet UITableView *itemsList;
//@property (nonatomic, weak) IBOutlet UILabel

// Vars
@property (nonatomic, strong) fcUserItem *itemBeingEdited;
@property (nonatomic, strong) fcUserItemOptionTable *itemOptionsBeingEdited; // Starts off as a copy of the Items and gets comitted or not
@property (nonatomic, strong) MenuItemOptionGroup *itemOptionGroup; // This is the kind of option we are talking about
@property (nonatomic,strong) fcItemType *itemType;
@property (nonatomic,assign) BOOL doesAnyOptionHaveMaxCountMoreThanOne;
@property (nonatomic, strong) NSMutableArray *listIndex;
@property (nonatomic, weak) id<ItemListChildFinished> delegate;
@property (nonatomic,strong) fcMenu *menu;

- (id) initWithNibName:(NSString *)nibNameOrNil userItem:(fcUserItem*)item andOptionGroup:(MenuItemOptionGroup*)group
            andItemType:(fcItemType*)itemType;
- (void) AddFooter;
- (void) createListIndex;
- (void) updateTableRow:(OptionValuePickerCellHolder*)holder withIndex:(NSIndexPath*)indexPath;
- (void) doneCommit; // Does the action

- (void) doMinus:(id)sender;
- (void) doPlus:(id)sender;


@end
