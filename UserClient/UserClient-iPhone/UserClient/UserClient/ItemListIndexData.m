//
//  ItemListIndexData.m
//  UserClient
//
//  Created by Nick Ambrose on 1/20/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#import "ItemListIndexData.h"

@implementation ItemListIndexData

@synthesize ItemType;
@synthesize ItemID;


- (id)init
{
    self = [super init];
    if (self)
    {
        ItemType=-1;
        ItemID=-1;
    }
    
    
    return self;
}

@end
