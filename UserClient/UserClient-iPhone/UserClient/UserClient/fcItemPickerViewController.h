//
//  fcItemPickerViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <UIKit/UIKit.h>
#include "MBProgressHUD.h"
#import "fcItemsListViewController.h"

@class XMLParserMenu;
@class ItemsPickerTableCellHolder;
@class fcMenu;

@interface fcItemPickerViewController : UIViewController <MBProgressHUDDelegate,ItemListChildFinished,UITableViewDelegate,UIActionSheetDelegate>
{

MBProgressHUD *HUD;
@private

NSMutableData *responseData;
//NSMutableArray *ListIndex;


}
@property (nonatomic, weak) IBOutlet UIView *TableFooter;
//@property (nonatomic, weak) IBOutlet UILabel *WelcomeUserLabel;
@property (nonatomic, strong) IBOutlet UITableView *itemsList;
@property (nonatomic, strong) NSMutableArray *listIndex;
@property (nonatomic, weak) id<ItemListChildFinished> delegate;
@property (nonatomic,weak) IBOutlet UITableViewCell *tableCell;
@property (nonatomic,assign) BOOL processingDelete;
@property (nonatomic,strong) NSURLConnection *conRequest;
@property (nonatomic,strong) fcMenu *menu;

- (CGFloat) getOptionsHeightForRowAtIndexPath:(NSIndexPath *)indexPath;
- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath;
- (void) UpdateTableRowFromHolder: (ItemsPickerTableCellHolder*)holder withIndex: (NSIndexPath*) index;
- (void) AddItemsPickListFooter;
- (IBAction) doBack: (id) sender;
- (IBAction) doNewDrink: (id) sender;
- (void) CreateListIndex;
- (void) doDownloadItemData;
- (void) childFinished;
- (void) childFinishedCommit;
- (void) exitCommitted;
- (void) doActions:(id)sender;
- (void) doActionsWork:(NSIndexPath *)cellIndex;
- (void) doDeleteItem:(NSNumber*)itemID;
@end
