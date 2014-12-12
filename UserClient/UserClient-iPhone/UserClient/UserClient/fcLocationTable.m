//
//  fcLocationTable.m
//  UserClient
//
//  Created by Nick Ambrose on 12/24/12.
//  Copyright (c) 2012 Freeway Coffee. All rights reserved.
//

#import "fcLocationTable.h"
#import "fcLocation.h"

@implementation fcLocationTable

@synthesize LocationData=_LocationData;

- (id)init
{
    self = [super init];
    if (self)
    {
        _LocationData  = [[NSMutableDictionary alloc]init];
    }
    return self;
    
}
- (void)clear
{
    [self.LocationData removeAllObjects];
}
- (NSUInteger)size
{
    return [self.LocationData count];
}

- (fcLocation*) getLocation: (NSNumber *)locationId
{
    return [self.LocationData objectForKey:locationId];
}
- (void) setLocation:(fcLocation*)location
{
    if(location!=nil)
    {
        [self.LocationData setObject:location forKey:[location LocationID]];
    }
}

- (void) setLocationTable:(fcLocationTable*)table
{
    for(NSNumber *aKey in [ table.LocationData allKeys] )
    {
        [self setLocation: [table.LocationData objectForKey:aKey]];
    }
}
@end
