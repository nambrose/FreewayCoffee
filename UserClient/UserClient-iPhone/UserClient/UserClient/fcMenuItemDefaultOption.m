//
//  fcMenuItemDefaultOption.m
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcMenuItemDefaultOption.h"

@implementation fcMenuItemDefaultOption

@synthesize itemTypeID=_itemTypeID;
@synthesize groupID=_groupID;
@synthesize itemOptionID=_itemOptionID;
@synthesize itemOptionCount=_itemOptionCount;

-(id)init
{
    self = [super init];
    if (self)
    {
        _itemOptionCount=1; // This is only sent by server if !=1 so we need to default it here.
    }
    return self;
}

@end
