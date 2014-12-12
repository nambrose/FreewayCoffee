//
//  fcMenu.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@class MenuItemOptionGroup;

@class fcItemTypeTable;
@class fcItemType;

@class fcMenuItemMandatoryOptionTable;
@class fcMenuItemMandatoryOption;

@class fcMenuItemDefaultOptionTable;
@class fcMenuItemDefaultOption;
@class fcUserItem;
@class fcItemTypeOption;
@class fcItemOption;
@class MenuItemOption;

@interface fcMenu : NSObject

@property (nonatomic,strong) NSNumber *menuID;
@property (nonatomic,strong) NSString *menuName;
@property (nonatomic,strong) NSNumber *menuCompatVersion; // Obsolete ?
@property (nonatomic,strong) NSNumber *menuVersion;

@property (nonatomic,strong) NSMutableDictionary *optionGroups;
@property (nonatomic,strong) fcItemTypeTable *itemTypeTable;

@property (nonatomic,strong) fcMenuItemMandatoryOptionTable *mandatoryOptions;
@property (nonatomic,strong) fcMenuItemDefaultOptionTable *defaultOptions;

-(id) init;


- (void)clearAll;
- (void)clearMenu;

- (BOOL)isMenuLoaded;
- (void) clearAllDownloadedData;

+(fcMenu*) parseFromAttributes:(NSDictionary*)attributes;
+(BOOL) isMenuIDNone:(NSNumber*)menuID;



- (void) addMenuOptionGroup:(MenuItemOptionGroup*)group;
- (void) addMenuItemType:(fcItemType*)itemType;
- (void) addMenuMandatoryOption:(fcMenuItemMandatoryOption*)mandatoryOption;
- (void) addMenuDefaultOption:(fcMenuItemDefaultOption*)defaultOption;

- (MenuItemOptionGroup*) findMenuOptionGroupByID:(NSNumber*)groupID;
- (fcItemType*) findItemTypeByID:(NSNumber*)itemTypeID;
- (fcUserItem*) makeDefaultItem: (NSNumber*)itemType;
- (void) addDefaultOptionsToItem:(fcUserItem*)item;

- (fcItemTypeOption*) findItemTypeOptionBy:(NSNumber*)ItemOptionGroupID
            andItemType:(NSNumber*)ItemTypeID andItemOptionID: (NSNumber*)ItemOptionID;

- (MenuItemOption*) findItemOptionByOptionGroupID:(NSNumber*)itemOptionGroupID
            andOptionID: (NSNumber*)itemOptionID;

- (MenuItemOptionGroup*)findItemOptionGroupByID:(NSNumber*)itemOptionGroupID;

- (BOOL) isOptionGroupMandatoryForItemType:(NSNumber*) itemTypeID andOptionGroup:(NSNumber*)optionGroupID;


@end
