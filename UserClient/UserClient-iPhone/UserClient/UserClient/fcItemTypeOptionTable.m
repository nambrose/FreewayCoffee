//
//  fcItemTypeOptionTable.m
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcItemTypeOptionTable.h"
#import "fcItemTypeOption.h"

@implementation fcItemTypeOptionTable
@synthesize itemTypeOptions=_itemTypeOptions;

-(id)init
{
    self = [super init];
    if (self)
    {
        _itemTypeOptions = [[NSMutableDictionary alloc] init];
    }
    return self;
}
-(void)clear
{
    [self.itemTypeOptions removeAllObjects ];
    
}
-(void) addItemTypeOption:(fcItemTypeOption*)itemTypeOption
{
    NSMutableArray *optionList = [self.itemTypeOptions objectForKey:[ itemTypeOption itemTypeGroupID]];
    if(optionList)
    {
        [optionList addObject:itemTypeOption];
    }
    else
    {
        NSMutableArray *newList = [[NSMutableArray alloc]init];
        [newList addObject:itemTypeOption];
        [self.itemTypeOptions setObject:newList forKey:[ itemTypeOption itemTypeGroupID]];
        
    }
}

-(fcItemTypeOption*) getItemTypeByID:(NSNumber*)itemTypeID
{
    return [[self itemTypeOptions] objectForKey:itemTypeID];
}
- (fcItemTypeOption*) findItemTypeOptionByItemOptionID:(NSNumber*)ItemOptionID
                                      andOptionGroupID:(NSNumber*)ItemOptionGroupID
{
    int index=0;
    NSMutableArray *optionList = [self.itemTypeOptions objectForKey:ItemOptionGroupID];
    if(optionList==nil)
    {
        return nil;
    }
    for(index=0;index< [optionList count];index++)
    {
        fcItemTypeOption *option = [optionList objectAtIndex:index];
        if(option!=nil)
        {
            // We rely here on the fact that we cannot add each optionID more than once for each Item Type
            // in the database
            if([option.itemOptionID isEqualToNumber:ItemOptionID])
            {
                return option;
            }
        }
    }
    return nil;
    
}
- (BOOL) areAnyOptionsValidForDrinkOptionGroup:(NSNumber*) groupID
{
    NSMutableArray *optionList = [self.itemTypeOptions objectForKey:groupID];
    if(optionList==nil)
    {
        return FALSE;
    }
    return TRUE;
}


@end
