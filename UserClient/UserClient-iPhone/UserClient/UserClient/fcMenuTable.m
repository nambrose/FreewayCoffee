//
//  fcMenuTable.m
//  UserClient
//
//  Created by Nick Ambrose on 1/20/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import "fcMenuTable.h"
#import "fcMenu.h"


@implementation fcMenuTable

@synthesize menus=_menus;
- (id)init
{
    self = [super init];
    if (self)
    {
        _menus  = [[NSMutableDictionary alloc]init];
    }
    return self;
    
}
- (void)clear
{
    [self.menus removeAllObjects];
}
- (NSUInteger)size
{
    return [self.menus count];
}

- (void) deleteMenu:(NSNumber *)menuID
{
    [self.menus removeObjectForKey:menuID];
}

- (fcMenu*) getMenu: (NSNumber *)menuId
{
    return [self.menus objectForKey:menuId];
}
- (void) setMenu:(fcMenu*)menu
{
    if(menu!=nil)
    {
        [self.menus setObject:menu forKey:[menu menuID]];
    }
}

- (void) setMenuTable:(fcMenuTable*)table
{
    for(NSNumber *aKey in [ table.menus allKeys] )
    {
        [self setMenu: [table.menus objectForKey:aKey]];
    }
}

- (void) clearAllDownloadedData
{
    
    for(NSNumber *aKey in [ self.menus allKeys] )
    {
        [[self.menus objectForKey:aKey] clearAllDownloadedData];
    }
    [self.menus removeAllObjects];

}
@end

