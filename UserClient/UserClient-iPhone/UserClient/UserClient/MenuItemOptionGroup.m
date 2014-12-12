//
//  ItemOptionGroup.m
//  UserClient
//
//  Created by Nick Ambrose on 9/3/12.
//  Copyright (c) 2012 Immerse Images. All rights reserved.
//

#import "MenuItemOptionGroup.h"
#import "MenuItemOption.h"
#import "Constants.h"
#import "XMLConstants.h"

@implementation MenuItemOptionGroup

@synthesize GroupID=_groupID;
@synthesize GroupName=_groupName;
@synthesize PartName=_partName;
@synthesize SortOrder=_sortOrder;
@synthesize SelectionType=_selectionType;
@synthesize ItemGroupItemOptions=_itemGroupItemOptions;
@synthesize menuID=_menuID;
- (id)init
{
    self = [super init];
    if (self)
    {
        _itemGroupItemOptions = [[NSMutableArray alloc] init];
        _menuID = OBJECT_NONE_ID;
    }
    return self;
}

-(void) addMenuItemOption:(MenuItemOption*)option
{
    [[self ItemGroupItemOptions] addObject:option];
}
-(MenuItemOption*) getItemOptionByID:(NSNumber*)optionID
{
    int index=0;
    MenuItemOption *option=nil;
    for(index=0;index < [self.ItemGroupItemOptions count];index++)
    {
        option = [self.ItemGroupItemOptions objectAtIndex:index];
        if(option==nil)
        {
            return nil;
        }
        if([option.OptionID isEqualToNumber:optionID])
        {
            return option;
        }
    }
    return nil;
}
@end
