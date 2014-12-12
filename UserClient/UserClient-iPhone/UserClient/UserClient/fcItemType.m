//
//  fcItemType.m
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcItemType.h"
#import "fcItemTypeOptionTable.h"
#import "Constants.h"
#import "XMLConstants.h"

@implementation fcItemType
@synthesize itemTypeID;
@synthesize itemTypeName;
@synthesize itemTypeText;
@synthesize sortOrder;
@synthesize menuID=_menuID;
@synthesize itemGroupID=_itemGroupID;
@synthesize itemTypeTypeID=_itemTypeTypeID;

@synthesize itemTypeOptions=_itemTypeOptions;
-(id)init
{
    self = [super init];
    if (self)
    {
        _itemTypeOptions = [[fcItemTypeOptionTable alloc]init];
        _menuID = OBJECT_NONE_ID;
    }
    return self;
}


-(void) clearOptions
{
    [self.itemTypeOptions clear];
}

-(void) addItemTypeOption:(fcItemTypeOption*)itemTypeOption
{
    [self.itemTypeOptions addItemTypeOption:itemTypeOption];
    
}
- (fcItemTypeOption*) findItemTypeOptionByItemOptionID:(NSNumber*)ItemOptionID
                                      andOptionGroupID:(NSNumber*)ItemOptionGroupID
{
    return [self.itemTypeOptions findItemTypeOptionByItemOptionID:ItemOptionID
                                                 andOptionGroupID: ItemOptionGroupID  ];
}
- (BOOL) areAnyOptionsValidForDrinkOptionGroup:(NSNumber*) groupID
{
    return [self.itemTypeOptions areAnyOptionsValidForDrinkOptionGroup:groupID];
}

- (BOOL) isOptionValidForItemType:(NSNumber*)optionID andGroup:(NSNumber*) optionGroupID
{
    fcItemTypeOption *option = [self findItemTypeOptionByItemOptionID:optionID andOptionGroupID:optionGroupID];
    if(nil==option)
    {
        return FALSE;
    }
    return TRUE;
    
}
@end
