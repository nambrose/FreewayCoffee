//
//  fcUserItem.m
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcUserItem.h"
#import "fcUserItemOptionTable.h"
#import "fcAppDelegate.h"
#import "fcMenuTable.h"


@implementation fcUserItem

@synthesize userItemID=_userItemID;
@synthesize ItemTypeID=_ItemtypeID;

@synthesize userItemItemTypeLongDescr=_userItemItemTypeLongDescr;
@synthesize userItemCost=_userItemCost;
@synthesize userItemName=_userItemName;
@synthesize userItemExtra=_userItemExtra;
@synthesize includeDefault=_includeDefault ;
@synthesize menuID=_menuID;

// Problem arises if we add a Item option. Then this becomes invalid. Can't really happen as we "reget" the Item on add/edit
@synthesize userItemOptionsText=_userItemOptionsText;

@synthesize userItemOptions=_userItemOptions;


-(id)init
{
    self = [super init];
    if (self)
    {
        _userItemOptions = [[fcUserItemOptionTable alloc] init];
        _includeDefault=TRUE;
    }
    return self;
}
- (void)sortUserItemOptions
{
    [self.userItemOptions sort];
    
}
- (fcMenu*) getMenu
{
    return [[myCommonAppDelegate menus] getMenu:self.menuID];
     
}
- (fcUserItem*)clone
{
    fcUserItem *newItem = [[fcUserItem alloc]init];
    
    newItem.userItemID = self.userItemID;
    newItem.ItemTypeID  = self.ItemTypeID;
    newItem.userItemItemTypeLongDescr =[self.userItemItemTypeLongDescr copy];
    newItem.userItemCost = [self.userItemCost copy];
    newItem.userItemName = [self.userItemName copy];
    newItem.userItemExtra = [self.userItemExtra copy];
    newItem.menuID = self.menuID;
    
    //self.includeDefault=TRUE;
    if(self.includeDefault==TRUE)
    {
        newItem.includeDefault=TRUE;
    }
    else
    {
        newItem.includeDefault=FALSE;
    }
    
    //newItem.includeDefault = [self.includeDefault ];
    newItem.userItemOptionsText = [self.userItemOptionsText copy];
    
    newItem.userItemOptions = [self.userItemOptions cloneOptions];
    return newItem;
    
}
-(void)clearUserItemOptions
{
    [self.userItemOptions clear];
}

-(void)addUserItemOption:(fcUserItemOption*)option
{
    [self.userItemOptions addUserItemOption:option];
    [self sortUserItemOptions]; // Slow but it guarantees we display thing in the correct order when we do makeOptionValueStringForOptionGroup
}

- (NSString*)makeOptionValueStringForOptionGroup:(NSNumber*)optionGroupID andItemType: (NSNumber*)itemType
{
    fcMenu *menu = [self getMenu];
    if(menu==nil)
    {
        return nil;
    }
    return [self.userItemOptions makeOptionValueStringForOptionGroup:optionGroupID andItemType:itemType andMenu:menu];
}
- (NSString*)makeOptionCostForOptionGroup:(NSNumber*)optionGroupID andItemType: (NSNumber*)itemType
{
    fcMenu *menu = [self getMenu];
    if(menu==nil)
    {
        return nil;
    }
    return [self.userItemOptions makeOptionCostForOptionGroup:optionGroupID andItemType:itemType andMenu:menu];
}
- (fcUserItemOptionTable*) cloneOptions
{
    return [self.userItemOptions cloneOptions];
}
- (void) replaceUserItemOptions:(fcUserItemOptionTable*)newOptions
{
    self.userItemOptions = newOptions; // We will let "ARC" Figure that one out !!!
}

- (void) removeOptionByOptionID:(NSNumber*)optionID
{
    [self.userItemOptions removeOptionByOptionID:optionID];
}

- (void) addOptionsListToPost:(NSMutableString*)postString
{
    [self.userItemOptions addOptionsListToPost:postString ];
}

@end
