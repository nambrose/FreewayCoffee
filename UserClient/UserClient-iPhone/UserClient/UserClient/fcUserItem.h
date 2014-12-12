//
//  fcUserItem.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcUserItemOptionTable;
@class fcUserItemOption;
@class fcMenu;

@interface fcUserItem : NSObject

@property (nonatomic,strong) NSNumber *userItemID;
@property (nonatomic,strong) NSNumber *ItemTypeID;
@property (nonatomic,strong) NSNumber *menuID;

@property (nonatomic,copy) NSString *userItemItemTypeLongDescr;
@property (nonatomic,copy) NSString *userItemCost;
@property (nonatomic,copy) NSString *userItemName;
@property (nonatomic,copy) NSString *userItemExtra;
@property (nonatomic,assign) BOOL includeDefault;

// Problem arises if we add a Item option. Then this becomes invalid. Can't really happen as we "reget" the Item on add/edit
@property (nonatomic,copy) NSString *userItemOptionsText;

@property (nonatomic,strong)  fcUserItemOptionTable *userItemOptions;

- (id)init;
- (void)clearUserItemOptions;

- (fcMenu*) getMenu;

- (void)addUserItemOption:(fcUserItemOption*)option;
- (void)sortUserItemOptions;
- (NSString*)makeOptionValueStringForOptionGroup:(NSNumber*)optionGroupID andItemType: (NSNumber*)itemType;
- (NSString*)makeOptionCostForOptionGroup:(NSNumber*)optionGroupID andItemType: (NSNumber*)itemType;
- (fcUserItemOptionTable*) cloneOptions;
- (void) replaceUserItemOptions:(fcUserItemOptionTable*)newOptions; // Replaces completely
- (void) removeOptionByOptionID:(NSNumber*)optionID;
- (void) addOptionsListToPost:(NSMutableString*)postString;
- (fcUserItem*)clone;
@end
