//
//  fcMenu.m
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcMenu.h"
#import "MenuItemOptionGroup.h"
#import "fcItemTypeTable.h"
#import "fcItemType.h"
#import "fcMenuItemMandatoryOptionTable.h"
#import "fcMenuItemDefaultOptionTable.h"
#import "fcMenuItemDefaultOption.h"
#import "fcUserItem.h"
#import "fcUserItemOption.h"
#import "fcUserItemTable.h"
#import "fcMenuItemDefaultOption.h"
#import "fcItemTypeOption.h"
#import "MenuItemOptionGroup.h"
#import "fcItemTypeTable.h"
#import "MenuItemOption.h"
#import "fcAppDelegate.h"
#import "XMLConstants.h"
#import "XMLHelper.h"


@implementation fcMenu

@synthesize optionGroups=_optionGroups;
@synthesize itemTypeTable=_itemTypeTable;
@synthesize mandatoryOptions=_mandatoryOptions;
@synthesize defaultOptions=_defaultOptions;
@synthesize menuID=_menuID;
@synthesize menuName=_menuName;
@synthesize menuVersion=_menuVersion; 
@synthesize menuCompatVersion=_menuCompatVersion;

-(id)init
{
    self = [super init];
    if (self)
    {
        // For now, add all these to clearAll too (GROSS)
        _menuID = [NSNumber numberWithInt:OBJECT_NONE_VERSION];
        _menuName=@"";
        _menuCompatVersion=[NSNumber numberWithInt:0]; // Obsolete ?
        _menuVersion=[NSNumber numberWithInt:OBJECT_NONE_ID];
        
        _optionGroups = [[NSMutableDictionary alloc] init];
        _mandatoryOptions = [[fcMenuItemMandatoryOptionTable alloc]init];
        _defaultOptions = [[fcMenuItemDefaultOptionTable alloc]init];
        _itemTypeTable  = [[fcItemTypeTable alloc]init];
        
    }
    return self;
}
+(BOOL) isMenuIDNone:(NSNumber*)menuID
{
    if(menuID==nil)
    {
        return TRUE;
    }
    return [menuID isEqualToNumber:OBJECT_NONE_ID];
}
+(fcMenu*) parseFromAttributes:(NSDictionary*)attributes
{
    fcMenu *menu = [[fcMenu alloc] init];
    for(NSString *aKey in [ attributes allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attributes valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
     
        if([aKey isEqualToString:ID_ATTR])
        {
            menu.menuID = [NSNumber numberWithInt:[attrValue intValue] ];
        }
        else if([aKey isEqualToString:MENU_REV_ATTR])
        {
            menu.menuVersion = [NSNumber numberWithInt:[attrValue intValue] ];
        }
        else if([aKey isEqualToString:MENU_NAME_ATTR])
        {
            menu.menuName = attrValue;
        }
        else if([aKey isEqualToString:MENU_COMPAT_LEVEL_ATTR])
        {
            menu.menuCompatVersion = [NSNumber numberWithInt:[attrValue intValue] ];
        }
    }
    return menu;
}

-(void)clearMenu
{
    [self.optionGroups removeAllObjects];
    [self.mandatoryOptions clear];
    [self.defaultOptions clear];
    [self.itemTypeTable clear];
}

- (void)clearAll
{
    [self clearMenu];
    self.menuID = [NSNumber numberWithInt:OBJECT_NONE_VERSION];
    self.menuName=@"";
    self.menuCompatVersion=[NSNumber numberWithInt:0]; // Obsolete ?
    self.menuVersion=[NSNumber numberWithInt:OBJECT_NONE_ID];
}
- (BOOL)isMenuLoaded
{
        if( ([self.optionGroups count]>0) &&
            ([self.mandatoryOptions size]>0) &&
            ([self.defaultOptions size]>0) &&
            ([self.itemTypeTable size]>0))
        {
            return TRUE;
        }
    [self clearMenu]; // just in case one has el garbage in it
    return FALSE;
}
- (void) clearAllDownloadedData
{
    [self clearAll];
}
-(void) addMenuOptionGroup:(MenuItemOptionGroup*)group
{
    [self.optionGroups setObject:group forKey:[group GroupID] ];
    
}

-(void) addMenuItemType:(fcItemType*)itemType
{
    [self.itemTypeTable addItemType:itemType];
    
}

-(MenuItemOptionGroup*) findMenuOptionGroupByID:(NSNumber*)groupID
{
    return [self.optionGroups objectForKey:groupID];
}

-(void) addMenuMandatoryOption:(fcMenuItemMandatoryOption*)mandatoryOption
{
    [self.mandatoryOptions addMandatoryOption:mandatoryOption];
}
-(void) addMenuDefaultOption:(fcMenuItemDefaultOption*)defaultOption
{
    [self.defaultOptions addDefaultOption:defaultOption];
}

- (fcItemType*) findItemTypeByID:(NSNumber*)itemTypeID
{
    return [self.itemTypeTable getItemTypeByID:itemTypeID];
    
}

- (fcUserItem*) makeDefaultItem:(NSNumber*)itemType;
{
    fcUserItem *newItem = [[fcUserItem alloc]init];
    [newItem setItemTypeID:itemType];
    newItem.menuID=self.menuID;
    [self addDefaultOptionsToItem:newItem];
    return newItem;
    
}
- (fcItemTypeOption*) findItemTypeOptionBy:(NSNumber*)ItemOptionGroupID
                               andItemType:(NSNumber*)ItemTypeID andItemOptionID: (NSNumber*)ItemOptionID
{
    fcItemType *itemType = [self.itemTypeTable getItemTypeByID:ItemTypeID];
    
    if(nil==itemType)
    {
        return nil;
    }
    
    return [itemType findItemTypeOptionByItemOptionID:ItemOptionID andOptionGroupID:ItemOptionGroupID];
}

- (MenuItemOptionGroup*)findItemOptionGroupByID:(NSNumber*)itemOptionGroupID
{
    
    return [self.optionGroups objectForKey:itemOptionGroupID];
}

- (MenuItemOption*) findItemOptionByOptionGroupID:(NSNumber*)itemOptionGroupID
                                     andOptionID: (NSNumber*)itemOptionID
{
    MenuItemOptionGroup *group = [self findItemOptionGroupByID:itemOptionGroupID];
    if(group==nil)
    {
        return nil;
    }
    
    return [group getItemOptionByID:itemOptionID];
    
                                  
}

- (void) addDefaultOptionsToItem:(fcUserItem*)item
{
    fcMenuItemDefaultOption *defaultOption = nil;
    //fcItemType *itemType = [self.itemTypeTable getItemTypeByID:[item ItemTypeID]];
    //if(nil==itemType)
    //{
    //    return;
    //}
    for(int index=0;index < [self.defaultOptions size];index++)
    {
        
        defaultOption = [self.defaultOptions  getOptionWithIndex:index];
        
        if( [defaultOption.itemTypeID isEqualToNumber:[item ItemTypeID]])
        {
            // This default option is for our item type
            fcUserItemOption *option = [[fcUserItemOption alloc]init];
            [option setItemOptionGroupID:[defaultOption groupID]];
            // NOTE: NO m_UserDrinkOptionID because this is not in the database yet (Default drink)
            [option setItemOptionCount: [defaultOption itemOptionCount]];
            [option setItemOptionID: [defaultOption itemOptionID]];
            
            fcItemTypeOption *itemTypeOption = [self findItemTypeOptionBy:[option itemOptionGroupID]
                            andItemType:[item ItemTypeID] andItemOptionID: [option itemOptionID]];
            
            if(itemTypeOption==nil)
            {
                // Really bad -- database error
                return;
            }
            [option setItemTypesOptionID:[itemTypeOption itemTypeOptionID]];
            MenuItemOption *itemOption = [self findItemOptionByOptionGroupID:[option itemOptionGroupID]
                                                                andOptionID: [option itemOptionID]];
            [option setSortOrder:[itemOption SortOrder]];
            [item addUserItemOption:option];
            [item sortUserItemOptions];
        }
    }
}
- (BOOL) isOptionGroupMandatoryForItemType:(NSNumber*) itemTypeID andOptionGroup:(NSNumber*)optionGroupID
{
    return [self.mandatoryOptions isOptionGroupMandatoryForItemType:itemTypeID andOptionGroup:optionGroupID];
}

@end
