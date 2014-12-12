//
//  CarMakeModelColorData.m
//  UserClient
//
//  Created by Nick Ambrose on 1/22/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#import "CarMakeModelColorData.h"

@implementation CarMakeModelColorData

@synthesize CarMakesAndModels;
@synthesize CarColors;


- (id)init
{
    self = [super init];
    if (self) {
        // Initialization code here.
        CarMakesAndModels = [[NSMutableDictionary alloc] init];
        CarColors= [[CarColorList alloc] init];
    }
   


    return self;
}

- (int) GetCarMakeCount
{
    return [CarMakesAndModels count];
}
- (int) GetCarColorCount
{
    return [CarColors Size];
}
- (void) Clear
{
    [CarMakesAndModels removeAllObjects];
    [CarColors Clear];
}

- (BOOL) IsDataPopulated
{
    if( ([CarColors Size]>0) && ( [CarMakesAndModels count]>0))
    {
        return TRUE;
    }
    else
    {
        // Since we only want all the data, if one was empty and one populated, something went wrong so we just clear it and re-get it.
        [CarColors Clear];
        [CarMakesAndModels removeAllObjects];
        return FALSE;
    }
}

- (void) AddCarColor:(CarColor *)Color forID:(NSInteger) ID
{
    [CarColors AddCarColor:Color forID:ID];
}

-(void) AddCarMake:(CarMake*)Make forID:(NSInteger) ID
{
    NSNumber *Key = [NSNumber numberWithInt:ID];
    [CarMakesAndModels setObject: Make forKey:Key];
}

- (CarMake*) GetCarMake:(NSInteger) MakeID
{
    NSNumber *Key = [NSNumber numberWithInt:MakeID];
    return [CarMakesAndModels objectForKey:Key];
    
}

- (CarColor*) GetCarColor:(NSInteger) ID
{
    return [CarColors GetCarColor:ID];
}

- (void) dealloc
{
    self.CarMakesAndModels=nil;
    self.CarColors=nil;
    //[super dealloc];
}

@end
