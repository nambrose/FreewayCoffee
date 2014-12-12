//
//  fcMenuItemDefaultOptionTable.m
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcMenuItemDefaultOptionTable.h"
#import "fcMenuItemDefaultOption.h"


@implementation fcMenuItemDefaultOptionTable
@synthesize defaultOptions=_defaultOptions;
-(id)init
{
    self = [super init];
    if (self)
    {
        _defaultOptions = [[NSMutableArray alloc] init];
    }
    return self;
}

-(NSUInteger) size
{
    return [self.defaultOptions count];
}
-(void)clear
{
    [self.defaultOptions removeAllObjects];
}

-(void)addDefaultOption:(fcMenuItemDefaultOption*)defaultOption
{
    [self.defaultOptions addObject:defaultOption ];
                                   
}
-(fcMenuItemDefaultOption*)getOptionWithIndex:(NSInteger)index
{
    if([self.defaultOptions count]==0)
    {
        return nil;
    }
    
    if(index<[self.defaultOptions count])
    {
        return [self.defaultOptions objectAtIndex:index];
    }
    return nil;
}
@end
