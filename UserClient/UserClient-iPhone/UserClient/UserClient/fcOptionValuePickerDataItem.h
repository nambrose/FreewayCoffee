//
//  fcOptionValuePickerDataItem.h
//  UserClient
//
//  Created by Nick Ambrose on 9/9/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@class MenuItemOption;
@class MenuItemOptionGroup;
@class fcItemTypeOption;
@class fcUserItemOption;


@interface fcOptionValuePickerDataItem : NSObject

@property (nonatomic,strong) NSNumber *optionID;
@property (nonatomic,strong) NSString *optionText;
@property (nonatomic,strong) MenuItemOption *option;
@property (nonatomic,strong) fcItemTypeOption *itemTypeOption;
@property (nonatomic,strong) MenuItemOptionGroup *optionGroup;
@property (nonatomic,strong) fcUserItemOption *userOption;
@property (nonatomic,assign) NSInteger optionCount;
@property (nonatomic,assign) BOOL isNone;
@property (nonatomic,assign) NSInteger sortOrder;

- (NSComparisonResult)compare:(fcOptionValuePickerDataItem *)otherObject;
- (fcUserItemOption*) createUserItemOption;
- (void) incrementOptionCount;
- (void) decrementOptionCount;
@end
