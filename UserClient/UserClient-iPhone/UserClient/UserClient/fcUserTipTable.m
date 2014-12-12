//
//  fcUserTipTable.m
//  UserClient
//
//  Created by Nick Ambrose on 9/29/12.
//  Copyright (c) 2012 Freeway Coffee. All rights reserved.
//

#import "fcUserTipTable.h"
#import "fcUserTip.h"

@implementation fcUserTipTable
@synthesize userTips=_userTips;

-(id)init
{
    self = [super init];
    if (self)
    {
        _userTips  = [[NSMutableDictionary alloc]init];
    }
    return self;
}

- (void) clear
{
    [self.userTips removeAllObjects];
}
- (void) addTip:(fcUserTip*)tip
{
    [self.userTips setObject:tip forKey:tip.locationID] ;
    
}
- (fcUserTip*) getTipForLocation:(NSNumber*)locationID
{
    fcUserTip *tip = [self.userTips objectForKey:locationID];
    if(nil!=tip)
    {
        return tip;
    }
    
    // Global tip ....
    return [self.userTips objectForKey:[NSNumber numberWithInt:0]];
}

@end
