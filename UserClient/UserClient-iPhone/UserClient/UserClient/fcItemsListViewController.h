//
//  fcItemsListViewController.h
//  UserClient
//
//  Created by Nick Ambrose on 9/6/12.
//  Copyright (c) 2012,2013 Freeway Coffee. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MBProgressHUD.h"
#import "fcUserItem.h"


@protocol ItemListChildFinished <NSObject, NSXMLParserDelegate>
- (void) childFinished;
- (void) childFinishedCommit;// Child actually did something ... s
//- (void) childFinishedCloseParent; //some Controllers care (like Add Drink needs to re-pop to get back two+levels)
@end

@class fcXMLParserItemsList;
@class fcMenu;
@class fcLeftMenuViewController;
@class WEPopoverController;


@interface fcItemsListViewController : UIViewController <NSXMLParserDelegate,MBProgressHUDDelegate,ItemListChildFinished>

{

@private
    MBProgressHUD *HUD;
    NSMutableData *responseData;
}


@property (nonatomic, strong) NSMutableArray *ListIndex;

@property (nonatomic,strong) fcLeftMenuViewController *leftMenuViewController;
@property (nonatomic,strong) WEPopoverController *leftMenuPopover;

@property (nonatomic, strong) IBOutlet UITableView *ItemsList;
@property (nonatomic, weak) IBOutlet UITableViewCell *ItemListTableCell;
@property (nonatomic, weak) IBOutlet UIView *TableFooter;
@property (nonatomic,assign) BOOL processingOrder;

@property (nonatomic,strong) NSURLConnection *conRequest;

// XML Parsers
@property (nonatomic,strong) fcXMLParserItemsList *itemsListXMLParser;

@property (nonatomic,weak) IBOutlet UIButton *orderBut;
@property (nonatomic,weak) IBOutlet UIButton *feedbackBut;

@property (nonatomic,strong) NSDecimalNumber *orderTotal;
@property (nonatomic,strong) NSDecimalNumber *orderItemsTotal;
@property (nonatomic,strong) NSDecimalNumber *orderDiscount;
@property (nonatomic,strong) NSDecimalNumber *orderTip;
@property (nonatomic,strong) NSDecimalNumber *orderTax;
@property (nonatomic,strong) NSDecimalNumber *orderTaxableAmount;
@property (nonatomic,strong) NSDecimalNumber *orderConvenienceFee;
@property (nonatomic,assign) BOOL orderTaxable;

@property (nonatomic,assign) BOOL orderFreeDueToDiscounts;
@property (nonatomic,strong) fcMenu *menu;

// Used because when we "refresh" from the main window, we dont want to display the previous order "I'm here" status even if it exists
// This would be super-confusing to the poor user
@property (nonatomic,assign) BOOL showLastOrder; 

- (void) doRefresh;
- (void) doGetItemList:(BOOL)showLast;
- (void) AddItemsListFooter;
- (void) CreateListIndex;
- (void) childFinished;
- (IBAction) doFeedback: (id) sender;
- (IBAction) doMakeOrder: (id) sender;
- (void)viewWillAppear:(BOOL)animated;
- (void) childFinishedCommit;
- (void) doRemoveItemFromOrder:(id)sender;
- (BOOL) validateOrder;
- (void) doSetUserTip:(id) sender;
- (void) updateOrderTotalAndDiscountAndTipAndTax;
// Update global state based on downloaded data from ItemsList (locations etc)
// Only partial for now as some state updated as it is downloaded
- (void) updateGlobalStateFromGetItemsList;
@end
