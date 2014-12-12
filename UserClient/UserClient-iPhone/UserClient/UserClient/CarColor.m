//
//  CarColor.m
//  UserClient
//
//  Created by Nick Ambrose on 1/22/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#import "CarColor.h"

#define CAR_COLOR_NONE_ID 0

@implementation CarColor

@synthesize CarColorID;
@synthesize CarColorLongDescr;
@synthesize CarColorShortDescr;
@synthesize SortOrder;

- (id)init
{
    self = [super init];
    if (self) {
        // Initialization code here.
    }
    
    self.CarColorLongDescr = [[NSString alloc] init];
    self.CarColorShortDescr = [[NSString alloc] init];
    return self;
}

- (BOOL) IsNone
{
    if(CarColorID==CAR_COLOR_NONE_ID)
    {
        return TRUE;
    }
    return FALSE;
}

- (void)dealloc 
{
    self.CarColorLongDescr=nil;
    self.CarColorShortDescr=nil;
    //[super dealloc];
}
@end
