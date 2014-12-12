//
//  fcItemPickerDataListItem.h
//  UserClient
//
//  Created by Nick Ambrose on 9/15/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcItemType;

@interface fcItemPickerDataListItem : NSObject

//public FreewayCoffeeApp.FoodDrink Type;
@property (nonatomic,assign) NSNumber *itemID;
@property (nonatomic,copy) NSString *Descr;
@property (nonatomic,assign) NSInteger sortOrder;
@property (nonatomic,strong)fcItemType *itemType;

- (NSComparisonResult)compare:(fcItemPickerDataListItem *)otherObject;
@end
