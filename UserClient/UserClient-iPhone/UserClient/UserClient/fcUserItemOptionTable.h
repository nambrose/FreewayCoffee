//
//  fcUserDrinkOptionTable.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcUserItemOption;
@class fcMenu;

@interface fcUserItemOptionTable : NSObject

@property (nonatomic,strong) NSMutableArray *userOptions;

- (id)init;
- (void)clear;
- (void)addUserItemOption:(fcUserItemOption*)option;
- (void)sort;
- (NSString*)makeOptionValueStringForOptionGroup:(NSNumber*)optionGroupID andItemType: (NSNumber*)itemType andMenu:(fcMenu*)menu;
- (NSString*)makeOptionCostForOptionGroup:(NSNumber*)optionGroupID andItemType: (NSNumber*)itemType andMenu:(fcMenu*)menu;
- (fcUserItemOptionTable*) cloneOptions;
- (fcUserItemOption*) findUserItemOptionByItemOptionID:(NSNumber*) optionID;
- (void) removeAllOptionsForOptionGroup: (NSNumber*)groupID;
- (void) removeOptionByOptionID:(NSNumber*)optionID;
- (void) removeAllOptionsForAnyOtherOptionInGroup:(NSNumber*) groupID withOptionID:(NSNumber*) optionID;
- (void) addOptionsListToPost:(NSMutableString*)postString;

@end
