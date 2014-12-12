//
//  fcAddItemViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"

#import "fcItemsListViewController.h"

@class fcMenu;

@interface fcAddItemViewController : UIViewController <ItemListChildFinished,NSXMLParserDelegate,MBProgressHUDDelegate>
{
@private
    MBProgressHUD *HUD;
    NSMutableData *responseData;
}
@property (nonatomic, weak) IBOutlet UIView *TableFooter;

@property (nonatomic, weak) IBOutlet UITableView *itemsList;
@property (nonatomic, strong) NSMutableArray *listIndex;
@property (nonatomic, weak) id<ItemListChildFinished> delegate;
@property (nonatomic,strong) NSURLConnection *conRequest;

@property (nonatomic,strong) fcMenu *menu;

- (void) doBack:(id) sender;
- (void) createListIndex;
- (void) childFinished;
- (void) childFinishedCommit; // Add or edit was done. We just exit.
- (void)doDownloadItemData;
@end
