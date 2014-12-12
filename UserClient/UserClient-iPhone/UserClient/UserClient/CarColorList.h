//
//  CarColorList.h
//  UserClient
//
//  Created by Nick Ambrose on 1/22/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#import <Foundation/Foundation.h>

@class CarColor;

@interface CarColorList : NSObject
{
    NSMutableDictionary *CarColors;
}

@property (nonatomic, strong) NSMutableDictionary *CarColors;

- (void) AddCarColor:(CarColor*) Color;
- (void) Clear;
- (NSUInteger) Size;
-(void) AddCarColor:(CarColor*)Color forID:(NSInteger) ID;
- (CarColor*) GetCarColor:(NSInteger) ID;


@end
