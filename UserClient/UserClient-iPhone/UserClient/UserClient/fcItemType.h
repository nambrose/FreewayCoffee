//
//  fcItemType.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcItemTypeOptionTable;
@class fcItemTypeOption;

@interface fcItemType : NSObject

@property (nonatomic,strong) NSNumber *itemTypeID;
@property (nonatomic,strong) NSString *itemTypeName;
@property (nonatomic,strong) NSString *itemTypeText;
@property (nonatomic,strong) NSNumber *menuID;
@property (nonatomic,strong) NSNumber *itemGroupID;
@property (nonatomic,strong) NSNumber *itemTypeTypeID;
@property (nonatomic,assign) NSInteger sortOrder;
@property (nonatomic,strong) fcItemTypeOptionTable *itemTypeOptions;

- (id)init;
-(void) addItemTypeOption:(fcItemTypeOption*)itemTypeOption;
-(void) clearOptions;
- (fcItemTypeOption*) findItemTypeOptionByItemOptionID:(NSNumber*)ItemOptionID
                                    andOptionGroupID:(NSNumber*)ItemOptionGroupID;
- (BOOL) areAnyOptionsValidForDrinkOptionGroup:(NSNumber*) groupID;
- (BOOL) isOptionValidForItemType:(NSNumber*)optionID andGroup:(NSNumber*) optionGroupID;


@end
