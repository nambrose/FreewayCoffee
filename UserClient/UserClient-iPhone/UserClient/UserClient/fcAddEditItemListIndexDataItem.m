//
//  fcAddEditItemListIndexDataItem.m
//  UserClient
//
//  Created by Nick Ambrose on 9/9/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcAddEditItemListIndexDataItem.h"

@implementation fcAddEditItemListIndexDataItem

@synthesize optionGroupRef=_optionGroupRef;
@synthesize optionDescr=_optionDescr;
@synthesize optionPrice=_optionPrice;
@synthesize pickType=_pickType;
@synthesize optionValue=_optionValue;
@synthesize optionGroupID=_optionGroupID;
@synthesize isMandatory=_isMandatory;
@synthesize sortOrder=_sortOrder;

@synthesize entryType; //(OPTION_TYPE_FOOD_DRINK_OPTION or OPTION_TYPE_EXTRA_OPTIONS)
@synthesize isNone;

-(id)init
{
    self = [super init];
    if (self)
    {
    }
    return self;
}

- (NSComparisonResult)compare:(fcAddEditItemListIndexDataItem *)otherObject
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
