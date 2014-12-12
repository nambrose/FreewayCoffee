//
//  fcItemTypeOption.m
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcItemTypeOption.h"

@implementation fcItemTypeOption
@synthesize itemTypeOptionID=_itemTypeOptionID;
@synthesize itemTypeID=_itemTypeID;
@synthesize itemOptionID=_itemOptionID;
@synthesize itemTypeGroupID=_itemTypeGroupID;
@synthesize itemTypeRangeMin=_itemTypeRangeMin;
@synthesize itemTypeRangeMax=_itemTypeRangeMax;
@synthesize itemTypeCost=_itemTypeCost;
@synthesize itemTypeChargePerCount=_itemTypeChargePerCount;
-(id)init
{
    self = [super init];
    if (self)
    {
        _itemTypeRangeMin=0;
		_itemTypeRangeMax=1;
		_itemTypeCost=@"0.00";
        _itemTypeChargePerCount=1; // Default is to always charge per.
    }
    return self;
}
@end
