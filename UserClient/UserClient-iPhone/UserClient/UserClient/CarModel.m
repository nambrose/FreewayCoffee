//
//  CarModel.m
//  UserClient
//
//  Created by Nick Ambrose on 1/22/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#import "CarModel.h"

@implementation CarModel

@synthesize ModelID;
@synthesize MakeID;
@synthesize ModelLongDescr;
@synthesize ModelShortDescr;
@synthesize SortOrder;
- (id)init
{
    self = [super init];
    if (self) {
        // Initialization code here.
    }
    
   
    return self;
}


-(BOOL) IsNone
{
    if(ModelID==CAR_MODEL_NONE_ID)
    {
        return TRUE;
    }
    return TRUE;
}


@end
