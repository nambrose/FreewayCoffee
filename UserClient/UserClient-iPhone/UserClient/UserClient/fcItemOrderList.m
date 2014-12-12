//
//  fcItemOrderList.m
//  UserClient
//
//  Created by Nick Ambrose on 1/27/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

// A list of Item ID's that the user will order (for a given menuID only)
#import "fcItemOrderList.h"
#import "fcAppDelegate.h"
#import "fcUserItem.h"
#import "fcUserItemTable.h"

@implementation fcItemOrderList
@synthesize orderIDList=_orderIDList;

-(id)init
{
    self = [super init];
    if (self)
    {
        // For now, add all these to clearAll too (GROSS)
        _orderIDList = [[NSMutableArray alloc] init];
    }
    return self;
}


- (void) clearAllDownloadedData
{
    [self.orderIDList removeAllObjects];
}

// Order Related
- (BOOL) isOrderEmpty
{
    return [self.orderIDList count]==0;
}

- (void) clearOrder
{
    [self.orderIDList removeAllObjects];
}

- (int) count
{
    return [self.orderIDList count];
}
- (NSNumber*)getItemIDForIndex:(NSInteger) index
{
    if(index < [self count])
    {
        return [self.orderIDList objectAtIndex:index];
    }
    return nil;
}

- (void)generateDefaultOrderIfEmpty:(NSNumber*)menuID;
{
    if([self isOrderEmpty] !=TRUE)
    {
        return;
    }
    
    [self generateDefaultOrder:menuID];
}

- (void) generateDefaultOrder:(NSNumber*)menuID;
{
    [self clearOrder];
    
    fcUserItemTable *table = [myCommonAppDelegate userItems];
    
    for( NSNumber *aKey in [ [table userItems ] allKeys] )
    {
        // do something
        fcUserItem *theItem = [[table userItems] objectForKey:aKey] ;
        if(nil==theItem)
        {
            continue; // Not good
        }
        if([theItem.menuID isEqualToNumber:menuID]!=TRUE)
        {
            continue; // Not for this menu
        }
        if(theItem.includeDefault==TRUE)
        {
            [self addItemToOrder:theItem.userItemID];
        }
    }
    
}
- (NSDecimalNumber*) getOrderTotalCost
{
    NSDecimalNumber *CurrentCost = [NSDecimalNumber decimalNumberWithString:@"0.00" ];
    
    // CurrentCost = [CurrentCost decimalNumberByAdding:subCost];
    // return [NSString stringWithFormat:@"%.2f",[CurrentCost doubleValue]];
    
    for(int index=0;index<[self.orderIDList count];index++)
    {
        NSNumber *itemID = [self.orderIDList objectAtIndex:index];
        
        fcUserItem *item = [[myCommonAppDelegate userItems] getUserItemForID:itemID];
        if(nil==item)
        {
            continue; // Thats not good
        }
        NSDecimalNumber *itemCost = [NSDecimalNumber decimalNumberWithString:item.userItemCost];
        CurrentCost = [CurrentCost decimalNumberByAdding:itemCost];
    }
    
    return CurrentCost;
    
}
- (void) addItemToOrder:(NSNumber*)itemID
{
    // Really should check its a valid item !!
    [self.orderIDList addObject:itemID];
}

- (void) removeItemFromOrder:(NSNumber*)itemID andRemoveAll:(BOOL)removeAll
{
    [self removeIDFromIntArray:self.orderIDList withItem:itemID andRemoveAll:removeAll];
}


- (void) removeIDFromIntArray:(NSMutableArray*)theArray withItem:(NSNumber*)itemID andRemoveAll:(BOOL)removeAll
{
    if([theArray count]==0)
    {
        return;
    }
    for(int index=[theArray count]-1;index>=0;index--)
    {
        NSNumber *item = [theArray objectAtIndex:index];
        if([item isEqualToNumber:itemID])
        {
            [theArray removeObjectAtIndex:index];
            
            if(removeAll==FALSE)
            {
                return;
            }
        }
    }
    
    
}

- (void) reconcile
{
    if([self.orderIDList count]==0)
    {
        return;
    }
    for(int index=[self.orderIDList count];index>0;index--)
    {
        fcUserItem  *item = [[myCommonAppDelegate userItems] getUserItemForID:[self.orderIDList objectAtIndex:index-1]];
        if(item==nil)
        {
            [self.orderIDList removeObjectAtIndex:index];
        }
    }
}

- (void) removeItemFromOrder:(NSNumber*) itemID atIndex:(int)index
{
    if(index<[self.orderIDList count])
    {
        NSNumber *listItem = [self.orderIDList objectAtIndex:index];
        
        if([listItem isEqualToNumber:itemID])
        {
            [self.orderIDList removeObjectAtIndex:index];
        }
    }
}


// Must be an array of NSNumbers
- (NSString*) getIDListAsString:(NSArray*)theList
{
    NSMutableString *result = [NSMutableString stringWithString:@""];
    
    BOOL firstTime=TRUE;
    
    for(int index=0;index < [self.orderIDList count];index++)
    {
        NSNumber *item = [self.orderIDList objectAtIndex:index];
        if(nil!=item)
        {
            if(firstTime==TRUE)
            {
                firstTime=FALSE;
            }
            else
            {
                [result appendFormat:@","];
            }
            [result appendFormat:@"%@",item ];
        }
    }
    return [result copy];
}

- (NSString*) getOrderAsIDList
{
    return [self getIDListAsString: self.orderIDList];
}



@end
