//
//  fcUserItemOption.m
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcUserItemOption.h"
#import "fcAppDelegate.h"
#import "MenuItemOption.h"
#import "MenuItemOptionGroup.h"
#import "fcItemTypeOption.h"

#import "fcMenu.h"

@implementation fcUserItemOption
@synthesize itemOptionID=_itemOptionID;
@synthesize itemTypesOptionID=_itemTypesOptionID;
@synthesize itemOptionGroupID=_itemOptionGroupID;
@synthesize itemOptionCount=_itemOptionCount;
@synthesize sortOrder=_sortOrder;
//@synthesize menu=_menu;

- (id)init
{
    self = [super init];
    if (self)
    {
        _itemOptionCount=1; // Not sent by server unless its different than 1
        
    }
    return self;
}

- (NSComparisonResult)compare:(fcUserItemOption *)otherObject
{
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
}
- (fcUserItemOption*)clone
{
    fcUserItemOption *newOption = [[fcUserItemOption alloc]init];
    
    newOption.itemOptionID = self.itemOptionID;
    newOption.itemOptionID=self.itemOptionID;
    newOption.itemTypesOptionID=self.itemTypesOptionID;
    newOption.itemOptionGroupID=self.itemOptionGroupID;
    newOption.itemOptionCount=self.itemOptionCount;
    newOption.sortOrder=self.sortOrder;
    
    return newOption;
}
- (NSString*) makeOptionValueString:(NSNumber*)itemType withMenu:(fcMenu*)menu
{
    
    if(menu==nil)
    {
        return nil;
    }
    
    MenuItemOption *option= [menu findItemOptionByOptionGroupID:self.itemOptionGroupID  andOptionID:self.itemOptionID];
    MenuItemOptionGroup *optionGroup = [menu findItemOptionGroupByID:self.itemOptionGroupID];
    

    // Get FoodDrinkOptionType, check if Max>1 and then add (1), (2) if count >0
    if(nil==option)
    {
        return nil;
    }
    
    NSMutableString *Result = [NSMutableString stringWithString:[option OptionName]];
    
    fcItemTypeOption *typeOption = [menu findItemTypeOptionBy:self.itemOptionGroupID andItemType:itemType
                                                                    andItemOptionID:self.itemOptionID];
    
    
    if(optionGroup.SelectionType == ItemOptionGroupSelectMulti)
    {
        if(self.itemOptionCount>0)
        {
            [Result appendFormat:@" (%d)",self.itemOptionCount];
        }
    }
    else if( (self.itemOptionCount>0) && ([typeOption itemTypeRangeMax]>1) )
    {
        [Result appendFormat:@" (%d)",self.itemOptionCount];
    }
    return Result;
}
 

- (NSDecimalNumber*) getTotalCost:(NSNumber*)itemType withMenu:(fcMenu*)menu
{
    if(menu==nil)
    {
        return nil;
    }
    // NOTE:Could also use a method here like FindDrinkTypeOptionByDrinkTypeOptionID as I think both are unique in that list.
    fcItemTypeOption *typeOption = [menu findItemTypeOptionBy:self.itemOptionGroupID andItemType:itemType
                                                                    andItemOptionID:self.itemOptionID];
    
    if(nil==typeOption)
    {
        return nil;
    }
    
    NSDecimalNumber *Result  = [NSDecimalNumber decimalNumberWithString:typeOption.itemTypeCost ];
    
    // If Charge per count, then Result = Cost * Count. Else dont multiply it.
    if([typeOption itemTypeChargePerCount]==1)
    {
        NSString *strCount = [NSString stringWithFormat:@"%d",self.itemOptionCount];
    
        NSDecimalNumber *count = [NSDecimalNumber decimalNumberWithString:strCount];
    
        Result = [Result decimalNumberByMultiplyingBy:count];
    }
    return Result;
}

- (void) decrementCount
{
    if(self.itemOptionCount>0)
    {
        self.itemOptionCount-=1;
    }
}
@end
