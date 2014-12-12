//
//  fcItemOrderList.h
//  UserClient
//
//  Created by Nick Ambrose on 1/27/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface fcItemOrderList : NSObject

@property (nonatomic,strong) NSMutableArray *orderIDList;

-(id) init;

- (int) count;
- (NSNumber*)getItemIDForIndex:(NSInteger) index;

- (void) clearAllDownloadedData;

- (BOOL) isOrderEmpty;
- (void) clearOrder;
- (void) generateDefaultOrder:(NSNumber*)menuID;;
- (void) generateDefaultOrderIfEmpty:(NSNumber*)menuID;

- (NSDecimalNumber*) getOrderTotalCost;
- (void) addItemToOrder:(NSNumber*)itemID;
- (void) removeItemFromOrder:(NSNumber*)itemID andRemoveAll:(BOOL)removeAll;
- (void) removeIDFromIntArray:(NSMutableArray*)theArray withItem:(NSNumber*)itemID andRemoveAll:(BOOL)removeAll;
- (NSString*) getOrderAsIDList;
// Must be an array of NSNumbers
// Private (use anon category I know, I know)
- (NSString*) getIDListAsString:(NSArray*)theList;
- (void) reconcile;
@end
