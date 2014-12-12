//
//  CarModelList.m
//  UserClient
//
//  Created by Nick Ambrose on 1/22/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#import "CarModelList.h"
#import "CarModel.h"

@implementation CarModelList

@synthesize Models;

- (id)init
{
    self = [super init];
    if (self) {
        // Initialization code here.
        Models = [[NSMutableDictionary alloc] init];

    }
    
        return self;
}

- (void) AddModel:(CarModel *)Model
{
    NSNumber *Key = [NSNumber numberWithInt:[Model ModelID]];
                     
    [Models setObject:Model forKey:Key ];
    
}

- (void) Clear
{
    [[self Models] removeAllObjects];
}

- (CarModel*) GetModel:(NSInteger) ModelID
{
    NSNumber *Key = [NSNumber numberWithInt:ModelID];
    return [Models objectForKey:Key];
}


- (NSUInteger) Size
{
    return [Models count];
}

/*
public Set<Map.Entry<Integer,FreewayCoffeeCarModel>> GetModelEntrySet()
{
    return Models.entrySet();
}
*/

- (void)dealloc 
{
    self.Models=nil;
    //[super dealloc];
}

@end
