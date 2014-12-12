//
//  CarColorList.m
//  UserClient
//
//  Created by Nick Ambrose on 1/22/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#import "CarColorList.h"
#import "CarColor.h"

@implementation CarColorList

@synthesize CarColors;

- (id)init
{
    self = [super init];
    if (self) {
        // Initialization code here.
         CarColors = [[NSMutableDictionary alloc] init];
    }
    
   
    
    return self;
}

- (void) AddCarColor:(CarColor*) Color
{
    NSNumber *Key = [NSNumber numberWithInt:[Color CarColorID]];
                    
    [CarColors setObject:Color forKey:Key];

}

- (void) Clear
{
    [[self CarColors] removeAllObjects];
    
}

- (NSUInteger) Size
{
    return [CarColors count];
}

-(void) AddCarColor:(CarColor*)Color forID:(NSInteger) ID
{
    NSNumber *Key = [NSNumber numberWithInt:ID];
    [CarColors setObject:Color forKey:Key];
}

- (CarColor*) GetCarColor:(NSInteger) ID
{
    NSNumber *Key = [NSNumber numberWithInt:ID];
    return [CarColors objectForKey:Key];
 
}



@end
