//
//  fcAddEditItemViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "fcItemsListViewController.h"
#import "MBProgressHUD.h"

@class fcItemType;
@class fcUserItem;
@class fcAddEditItemListIndexDataItem;
@class MenuItemOptionGroup;
@class fcMenu;
@class UICheckbox;

@protocol ItemAddEditChildFinished  <NSObject>
- (void) childFinishedWithNotes:(NSString*)notes;
@end

@interface fcAddEditItemViewController : UIViewController <ItemListChildFinished,ItemAddEditChildFinished,NSXMLParserDelegate,MBProgressHUDDelegate>

@property (nonatomic, strong) MBProgressHUD *HUD;
@property (nonatomic, strong) NSMutableData *responseData;
@property (nonatomic, weak) IBOutlet UIView *TableFooter;
@property (nonatomic, weak) IBOutlet UIView *TableHeader;

// Table Cell
@property (nonatomic, weak) IBOutlet UITableViewCell *tableCell;

// These three from Header View
@property (nonatomic, weak) IBOutlet UILabel *itemTypeLabel;
//@property (nonatomic, weak) IBOutlet UITextField *itemNameEdit;
@property (nonatomic, weak) IBOutlet UICheckbox *itemIncludeDefaultCheck;

// From Footer
@property (nonatomic, weak) IBOutlet UIButton *addEditButton;
@property (nonatomic, weak) IBOutlet UITableView *itemsList;

@property (nonatomic, strong) NSMutableArray *listIndex;
@property (nonatomic, weak) id<ItemListChildFinished> delegate;

// Vars
@property (nonatomic, strong) NSNumber *itemTypeID;
@property (nonatomic,strong) NSNumber *userItemID; // -1 for Add (yeah, uglified!)
@property (nonatomic,strong) fcItemType *itemType;
@property (nonatomic,strong) fcUserItem *itemInProgress;
@property (nonatomic,assign) BOOL isEdit;
@property (nonatomic,strong) NSString *itemNotes;
@property (nonatomic,assign) BOOL popTop; // Pop to Order now
@property (nonatomic,assign) BOOL downloadingMenu;
@property (nonatomic,strong) NSURLConnection *conRequest;
@property (nonatomic,strong) fcMenu *menu;


- (id) initWithNibName:(NSString *)nibNameOrNil andItemType: (NSNumber*)type andUserItemId:(NSNumber*) userItemID
             andIsEdit: (BOOL)isEdit andPopTop: (BOOL) popTop;


- (IBAction) doBack:(id) sender;
- (void) createListIndex;
- (IBAction) doAddEditDrink:(id) sender;
- (void) AddHeader;
- (void) AddFooter;
- (void) childFinished;
- (fcAddEditItemListIndexDataItem*) createIndexEntry:(MenuItemOptionGroup*)group;
- (void) UpdateTableRow:(UILabel*)mainText andCost:(UILabel*)costText withIndex:(NSIndexPath*)indexPath;
- (void) childFinishedWithNotes:(NSString*)notes;
- (BOOL) areAllMandatoryOptionsSelected;
- (NSInteger) getIndexOfFirstOptionToFix;
- (void) doEndSuccessfully;
- (void) setHeaderItems;
- (void)doDownloadItemData;

// URL Connection
- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response;
- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data;
- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error;
- (void)connectionDidFinishLoading:(NSURLConnection *)connection;
- (NSMutableString*)makeAddEditPost;
@end
