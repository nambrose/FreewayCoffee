//
//  CarModelList.h
//  UserClient
//
//  Created by Nick Ambrose on 1/22/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "Constants.h"

@class CarModel;

@interface CarModelList : NSObject
{
    NSMutableDictionary *Models;
}

@property (nonatomic, strong) NSMutableDictionary *Models;

- (void) AddModel:(CarModel *)Model;
- (void) Clear;
- (CarModel*) GetModel:(NSInteger) ModelID;
- (NSUInteger) Size;


@end
