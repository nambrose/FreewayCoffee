//
//  fcAddItemIndexData.m
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcAddItemIndexData.h"

@implementation fcAddItemIndexData
@synthesize itemID=_itemID;
@synthesize sortOrder=_sortOrder;

- (NSComparisonResult)compare:(fcAddItemIndexData *)otherObject
{
    if(self.sortOrder > [otherObject sortOrder])
    {
        return NSOrderedDescending;
    }
    else if(self.sortOrder < [otherObject sortOrder])
    {
        return NSOrderedAscending;
    }
    else
    {
        return NSOrderedSame;
    }
    
}

@end
