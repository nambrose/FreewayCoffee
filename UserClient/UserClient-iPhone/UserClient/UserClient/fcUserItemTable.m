//
//  fcUserDrinkTable.m
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcUserItemTable.h"
#import "fcUserItem.h"

@implementation fcUserItemTable
@synthesize userItems=_userItems;


- (id)init
{
    self = [super init];
    if (self)
    {
        _userItems = [[NSMutableDictionary alloc] init];
    }
    return self;
}
-(void)clear
{
    [self.userItems removeAllObjects];
}
-(void)addUserItem:(fcUserItem*)item
{
    [self.userItems setObject:item forKey:[item userItemID]];
}

- (fcUserItem*)getUserItemForID:(NSNumber*)itemID
{
    return [self.userItems objectForKey:itemID];
}

- (void) removeUserItem:(NSNumber*)itemID
{
    [self.userItems removeObjectForKey:itemID];
}
- (BOOL) isEmpty
{
    return [self.userItems count]==0;
}

/*
-(BOOL) anyUserItemsForMenuID:(NSNumber *)menuID
{
    BOOL result=false;
    [self.userItems enumerateKeysAndObjectsUsingBlock:^(id key, id obj, BOOL *stop)
    {
        fcUserItem *item = (fcUserItem*)obj;
        if(item)
        FC_Log(@"%@ => %@", key, obj);
    }];
    
    return result;
}
*/
@end
