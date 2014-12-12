//
//  fcItemOrderListTable.h
//  UserClient
//
//  Created by Nick Ambrose on 1/27/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcItemOrderList;

@interface fcItemOrderListTable : NSObject

@property (nonatomic,strong) NSMutableDictionary *orderListTable;

-(id) init;

-(void) clearAllDownloadedData;

-(fcItemOrderList*) getOrderListForMenuID:(NSNumber*) menuID;
//-(void) setOrderListForMenuID:( NSNumber *)menudID;
- (void) generateDefaultOrderListForMenuID:(NSNumber *)menuID;
- (void) removeItemFromOrder:(NSNumber*)itemID fromMenuID:(NSNumber*)menuID andRemoveAll:(BOOL)removeAll;
- (void) generateDefaultOrderIfEmpty:(NSNumber*)menuID;
- (void) reconcile; // Go through each order list and check that the corresponding User Item actually exists
@end
