//
//  fcItemTypeOption.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface fcItemTypeOption : NSObject

@property (nonatomic,strong) NSNumber *itemTypeOptionID;
@property (nonatomic,strong) NSNumber *itemTypeID;
@property (nonatomic,strong) NSNumber *itemOptionID;
@property (nonatomic,strong) NSNumber *itemTypeGroupID;
@property (nonatomic,assign) NSInteger itemTypeRangeMin;
@property (nonatomic,assign) NSInteger itemTypeRangeMax;
@property (nonatomic,strong) NSString *itemTypeCost;
@property (nonatomic,assign) NSInteger itemTypeChargePerCount;

-(id)init;

@end
