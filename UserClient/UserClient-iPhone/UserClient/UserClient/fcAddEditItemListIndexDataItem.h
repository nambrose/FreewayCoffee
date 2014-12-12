//
//  fcAddEditItemListIndexDataItem.h
//  UserClient
//
//  Created by Nick Ambrose on 9/9/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

#define OPTION_TYPE_FOOD_DRINK_OPTION 0
#define OPTION_TYPE_EXTRA_OPTIONS 1

#import "MenuItemOptionGroup.h"

@interface fcAddEditItemListIndexDataItem : NSObject

@property (nonatomic,weak) MenuItemOptionGroup *optionGroupRef;
@property (nonatomic,strong) NSString *optionDescr;
@property (nonatomic,strong) NSString *optionPrice;
@property (nonatomic,assign) ItemOptionGroupSelectionType pickType;
@property (nonatomic,strong) NSString *optionValue;
@property (nonatomic,strong) NSNumber *optionGroupID;
@property (nonatomic,assign) BOOL isMandatory;
@property (nonatomic,assign) NSInteger sortOrder;

@property (nonatomic,assign) NSInteger entryType; //(OPTION_TYPE_FOOD_DRINK_OPTION or OPTION_TYPE_EXTRA_OPTIONS)
@property (nonatomic,assign) BOOL isNone;
-(id) init;
- (NSComparisonResult)compare:(fcAddEditItemListIndexDataItem *)otherObject;

@end
