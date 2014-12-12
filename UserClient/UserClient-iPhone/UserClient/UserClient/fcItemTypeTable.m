//
//  fcItemTypeTable.m
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcItemTypeTable.h"
#import "fcItemType.h"


@implementation fcItemTypeTable
@synthesize itemTypes=_itemTypes;

-(id)init
{
    self = [super init];
    if (self)
    {
        _itemTypes = [[NSMutableDictionary alloc] init];
    }
    return self;
}
-(NSUInteger) size
{
    return [self.itemTypes count];
    
}
-(void)clear
{
    [self.itemTypes removeAllObjects];
}

-(void) addItemType:(fcItemType*)itemType
{
    [[self itemTypes] setObject:itemType forKey:[itemType itemTypeID]];

}

-(fcItemType*) getItemTypeByID:(NSNumber*)itemTypeID
{
    return [[self itemTypes] objectForKey:itemTypeID];
}
@end
