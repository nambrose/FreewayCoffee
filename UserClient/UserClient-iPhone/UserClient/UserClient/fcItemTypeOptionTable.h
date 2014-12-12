//
//  fcItemTypeOptionTable.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcItemTypeOption;

@interface fcItemTypeOptionTable : NSObject

@property (nonatomic,strong) NSMutableDictionary *itemTypeOptions;

-(id)init;
-(void)clear;
-(void) addItemTypeOption:(fcItemTypeOption*)itemTypeOption;
-(fcItemTypeOption*) getItemTypeByID:(NSNumber*)itemTypeID;
- (fcItemTypeOption*) findItemTypeOptionByItemOptionID:(NSNumber*)ItemOptionID
                                      andOptionGroupID:(NSNumber*)ItemOptionGroupID;
- (BOOL) areAnyOptionsValidForDrinkOptionGroup:(NSNumber*) groupID;

@end
