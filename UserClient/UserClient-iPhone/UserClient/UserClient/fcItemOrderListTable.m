//
//  fcItemOrderListTable.m
//  UserClient
//
//  Created by Nick Ambrose on 1/27/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import "fcItemOrderListTable.h"

#import "fcItemOrderList.h"


@implementation fcItemOrderListTable

@synthesize orderListTable=_orderListTable;

-(id)init
{
    self = [super init];
    if (self)
    {
        // For now, add all these to clearAll too (GROSS)
        _orderListTable = [[NSMutableDictionary alloc] init];
    }
    return self;
}


- (void) clearAllDownloadedData
{
    [self.orderListTable removeAllObjects];
}
- (void) generateDefaultOrderIfEmpty:(NSNumber*)menuID 
{
    fcItemOrderList *orderList =[ self getOrderListForMenuID:menuID];
    
    if(orderList==nil)
    {
        orderList = [[fcItemOrderList alloc] init];
        [self.orderListTable setObject:orderList forKey:menuID];
        [orderList generateDefaultOrder:menuID];
    }
    else
    {
        [orderList generateDefaultOrderIfEmpty:menuID];
    }
}

-(fcItemOrderList*) getOrderListForMenuID:(NSNumber*) menuID
{
    return [self.orderListTable objectForKey:menuID];
}

- (void) generateDefaultOrderListForMenuID:(NSNumber *)menuID
{
    fcItemOrderList *orderList = [self.orderListTable objectForKey:menuID];
    if(orderList!=nil)
    {
        [orderList generateDefaultOrder:menuID];
    }
}

- (void) removeItemFromOrder:(NSNumber*)itemID fromMenuID:(NSNumber*)menuID andRemoveAll:(BOOL)removeAll
{
    fcItemOrderList *orderList = [self.orderListTable objectForKey:menuID];
    if(orderList!=nil)
    {
        [orderList removeItemFromOrder:menuID andRemoveAll:removeAll];
    }
    
}

- (void) reconcile
{
    for(NSNumber *menuID in [self.orderListTable allKeys])
    {
        fcItemOrderList *orderList = [self.orderListTable objectForKey:menuID];
        [orderList reconcile];
    }
}
@end
