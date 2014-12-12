//
//  fcOptionValuePickerDataItem.m
//  UserClient
//
//  Created by Nick Ambrose on 9/9/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcOptionValuePickerDataItem.h"
#import "fcUserItemOption.h"
#import "MenuItemOption.h"
#import "fcItemTypeOption.h"

@implementation fcOptionValuePickerDataItem
@synthesize optionID=_optionID;
@synthesize optionText=_optionText;
@synthesize option=_option;
@synthesize itemTypeOption=_itemTypeOption;
@synthesize optionGroup=_optionGroup;
@synthesize userOption=_userOption;
@synthesize optionCount=_optionCount;
@synthesize isNone=_isNone;
@synthesize sortOrder=_sortOrder;

- (NSComparisonResult)compare:(fcOptionValuePickerDataItem *)otherObject
{
    if(self.sortOrder > [otherObject sortOrder])
    {
        return NSOrderedDescending;
    }
    else if(self.sortOrder < [otherObject sortOrder])
    {
        return NSOrderedAscending;
    }
    else
    {
        return NSOrderedSame;
    }
    
}


- (fcUserItemOption*) createUserItemOption
{
    fcUserItemOption *newOption = [[fcUserItemOption alloc]init];

    newOption.itemOptionGroupID = [self.option OptionGroupID];
    
    // Not in the table -- bit scary, should we add it ? UserOption.SetDrinkOptionID(Option.GetOptionValue());
    newOption.itemOptionCount = self.optionCount;
    newOption.itemOptionID = self.optionID;
    newOption.itemTypesOptionID = self.itemTypeOption.itemTypeOptionID;
    newOption.sortOrder = self.sortOrder;
    return newOption;    
}

- (void) incrementOptionCount
{
    self.optionCount+=1;
}
- (void) decrementOptionCount
{
    self.optionCount-=1;
}

@end
