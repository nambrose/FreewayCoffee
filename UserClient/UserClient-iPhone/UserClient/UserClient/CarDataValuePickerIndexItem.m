//
//  CarDataValuePickerIndexItem.m
//  UserClient
//
//  Created by Nick Ambrose on 9/2/12.
//  Copyright (c) 2012 Immerse Images. All rights reserved.
//

#import "CarDataValuePickerIndexItem.h"

@implementation CarDataValuePickerIndexItem

@synthesize ItemText;
@synthesize SortOrder;
@synthesize m_HasModels;
@synthesize m_ItemID;

- (NSComparisonResult)compare:(CarDataValuePickerIndexItem *)otherObject
{
    if(self.SortOrder > [otherObject SortOrder])
    {
        return NSOrderedDescending;
    }
    else if(self.SortOrder < [otherObject SortOrder])
    {
        return NSOrderedAscending;
    }
    else
    {
        return NSOrderedSame;
    }
    
}
@end
