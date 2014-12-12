//
//  CarDataIndexItem.m
//  UserClient
//
//  Created by Nick Ambrose on 9/2/12.
//  Copyright 2012 Freeway Coffee. All rights reserved.
//

#import "CarDataIndexItem.h"

@implementation CarDataIndexItem

@synthesize ItemType;
//@synthesize ItemID;


- (id)init
{
    self = [super init];
    if (self) {
        // Initialization code here.
        ItemType=-1;
    }
    
    
 //   ItemID=-1;
    return self;
}

@end
