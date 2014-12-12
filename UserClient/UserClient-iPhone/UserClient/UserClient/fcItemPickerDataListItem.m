//
//  fcItemPickerDataListItem.m
//  UserClient
//
//  Created by Nick Ambrose on 9/15/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcItemPickerDataListItem.h"

@implementation fcItemPickerDataListItem

@synthesize itemID=_itemID;
@synthesize Descr=_Descr;
@synthesize sortOrder=_sortOrder;
@synthesize itemType=_itemType;

- (NSComparisonResult)compare:(fcItemPickerDataListItem *)otherObject
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
