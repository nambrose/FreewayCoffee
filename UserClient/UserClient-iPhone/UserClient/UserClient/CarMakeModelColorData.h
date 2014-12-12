//
//  CarMakeModelColorData.h
//  UserClient
//
//  Created by Nick Ambrose on 1/22/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "CarColorList.h"
#import "CarMake.h"

@interface CarMakeModelColorData : NSObject
{
    // Map of NSNumber->CarMake which has a ModelList in it 
    NSMutableDictionary *CarMakesAndModels;
    CarColorList *CarColors;
}

@property (nonatomic, strong) NSMutableDictionary *CarMakesAndModels;
@property (nonatomic, strong) CarColorList *CarColors;

- (void) Clear;
- (BOOL) IsDataPopulated;
- (void) AddCarColor:(CarColor *)Color forID:(NSInteger) ID;
- (void) AddCarMake:(CarMake*)Make forID:(NSInteger) ID;
- (int) GetCarMakeCount;
- (int) GetCarColorCount;
- (CarMake*) GetCarMake:(NSInteger) MakeID;
- (CarColor*) GetCarColor:(NSInteger) ID;



@end
