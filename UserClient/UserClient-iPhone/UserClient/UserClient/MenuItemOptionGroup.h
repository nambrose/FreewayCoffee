//
//  ItemOptionGroup.h
//  UserClient
//
//  Created by Nick Ambrose on 9/3/12.
//  Copyright (c) 2012 Immerse Images. All rights reserved.
//

#import <Foundation/Foundation.h>

@class MenuItemOption;

typedef enum
{
    ItemOptionGroupSelectOne,
    ItemOptionGroupSelectMulti
}ItemOptionGroupSelectionType;


@interface MenuItemOptionGroup : NSObject
{
    
}

@property (nonatomic, strong) NSNumber *GroupID;
@property (nonatomic, strong) NSString *GroupName;
@property (nonatomic, strong) NSString *PartName;
@property (nonatomic, assign) NSInteger SortOrder;
@property (nonatomic, assign) ItemOptionGroupSelectionType SelectionType;
@property (nonatomic, assign) NSNumber *menuID;
@property (nonatomic, copy) NSMutableArray *ItemGroupItemOptions;

-(void) addMenuItemOption:(MenuItemOption*)option;
-(MenuItemOption*) getItemOptionByID:(NSNumber*)optionID;
@end
