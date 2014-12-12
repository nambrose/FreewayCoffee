//
//  XMLParserMenu.m
//  UserClient
//
//  Created by Nick Ambrose on 9/3/12.
//  Copyright (c) 2012 Immerse Images. All rights reserved.
//

#import "XMLParserMenu.h"
#import "fcAppDelegate.h"

#import "fcMenu.h"
#import "MenuItemOptionGroup.h"
#import "MenuItemOption.h"

#import "fcItemType.h"
#import "fcItemTypeTable.h"
#import "fcItemTypeOption.h"
#import "fcItemTypeOptionTable.h"
#import "fcMenuItemDefaultOption.h"
#import "fcMenuItemMandatoryOption.h"
#import "fcServerObjectResponse.h"
#import "fcMenu.h"


#import "XMLHelper.h"
#import "Constants.h"

@implementation XMLParserMenu

@synthesize signonError=_signonError;
- (id)init
{
    self = [super init];
    if (self)
    {
        _currentItemType=nil;
        _signonError=FALSE;
        _serverResponse = [[fcServerObjectResponse alloc ] init];
        _menu = nil; // Gets allocated later in parse

    }
    
    return self;
}


- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    if( [elementName isEqualToString:MENU_ITEM_OPTION_GROUP_TAG])
    {
        MenuItemOptionGroup *group = [self parseMenuItemOptionsGroup:attributeDict] ;
        [self.menu addMenuOptionGroup:group];
    }
    else if( [elementName isEqualToString:GET_MENU_RESPONSE_TAG])
    {
        self.serverResponse = [fcServerObjectResponse parseFromXMLAttributes:attributeDict];
    }
    else if( [elementName isEqualToString:MENU_TAG])
    {
        self.menu = [fcMenu parseFromAttributes:attributeDict];
    }
    else if( [elementName isEqualToString:MENU_ITEM_OPTION_TAG])
    {
        MenuItemOption *option = [self parseMenuItemOption:attributeDict] ;
        if(nil!=option)
        {
            MenuItemOptionGroup *group =[self.menu findMenuOptionGroupByID:[option OptionGroupID]];
            if(nil!=group)
            {
                //FC_Log(@"Found Group: %@ For Option: %@",[group GroupName],[option OptionName]);
                [group addMenuItemOption:option];
            }
        }
    }
    else if( [elementName isEqualToString:MENU_ITEM_TYPE_TAG])
    {
        _currentItemType = [self parseItemType:attributeDict];
    }
    else if( [elementName isEqualToString:MENU_ITEM_TYPE_OPTION_TAG])
    {
        fcItemTypeOption *itemTypeOption = [self parseItemTypeOption:attributeDict];
        if(nil!=_currentItemType)
        {
            [_currentItemType addItemTypeOption:itemTypeOption];
        }
    }
    else if( [elementName isEqualToString:MENU_ITEM_TYPES_DEFAULT_OPTION_TAG])
    {
        fcMenuItemDefaultOption *defOption = [self parseDefaultOption:attributeDict];
        if(nil!=defOption)
        {
            [self.menu addMenuDefaultOption:defOption];
        }
    }
    else if( [elementName isEqualToString:MENU_ITEM_TYPES_MAND_OPTION])
    {
        fcMenuItemMandatoryOption *mandOption = [self parseMandatoryOption:attributeDict];
        if(nil!=mandOption)
        {
            [self.menu addMenuMandatoryOption:mandOption];
        }
    }
    else if([elementName isEqualToString:SIGNON_RESPONSE_TAG])
    {
        NSString *attrValue = [NSString stringWithString:[attributeDict valueForKey:RESULT_ATTR] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        if([attrValue isEqualToString:OK_ATTR_VALUE]!=TRUE)
        {
            self.signonError=TRUE;
        }
    }
    
    
}
- (fcMenuItemDefaultOption*)parseDefaultOption:(NSDictionary*)attrs
{
    fcMenuItemDefaultOption *defaultOption = [[fcMenuItemDefaultOption alloc] init];
    for(NSString *aKey in [ attrs allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attrs valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"parseDefaultOption: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:MENU_ITEM_TYPES_DEFAULT_OPTION_DT_ID])
        {
            [defaultOption setItemTypeID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:MENU_ITEM_TYPES_DEFAULT_OPTION_OPT_GROUP])
        {
            [defaultOption setGroupID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:MENU_ITEM_TYPES_DEFAULT_OPTION_OPT_VALUE_ID])
        {
            [defaultOption setItemOptionID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:MENU_ITEM_TYPES_DEFAULT_OPTION_COUNT])
        {
            [defaultOption setItemOptionCount: [attrValue integerValue]];
        }
    }
    
    return defaultOption;
            
        
}
- (fcMenuItemMandatoryOption*)parseMandatoryOption:(NSDictionary*)attrs
{
    fcMenuItemMandatoryOption *mandOption = [[fcMenuItemMandatoryOption alloc] init];
    for(NSString *aKey in [ attrs allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attrs valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"parseMandatoryOption: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:MENU_ITEM_TYPES_MAND_OPTION_DT_ID])
        {
            [mandOption setItemTypeID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:MENU_ITEM_TYPES_MAND_OPTION_DOG_ID])
        {
            [mandOption setItemGroupID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
    }
    return mandOption;
}
- (fcItemTypeOption*) parseItemTypeOption:(NSDictionary*)attrs
{
    fcItemTypeOption *itemTypeOption = [[fcItemTypeOption alloc] init];
    for(NSString *aKey in [ attrs allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attrs valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"parseMenuItemOptionsGroup: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:ID_ATTR])
        {
            [itemTypeOption setItemTypeOptionID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:MENU_ITEM_TYPE_TYPES_OPTION_DRINK_TYPE_ID_ATTR])
        {
            [itemTypeOption setItemTypeID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:MENU_ITEM_TYPE_TYPES_OPTION_DRINK_OPTION_ID_ATTR])
        {
            [itemTypeOption setItemOptionID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:MENU_ITEM_TYPE_TYPES_OPTION_DRINK_OPTION_GROUP_ID_ATTR])
        {
            [itemTypeOption setItemTypeGroupID:[NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        
        else if([aKey isEqualToString:MENU_ITEM_TYPE_TYPES_OPTION_RANGE_MIN])
        {
            [itemTypeOption setItemTypeRangeMin: [attrValue integerValue]];
        }
        else if([aKey isEqualToString:MENU_ITEM_TYPE_TYPES_OPTION_RANGE_MAX])
        {
            [itemTypeOption setItemTypeRangeMax: [attrValue integerValue]];
        }
        else if([aKey isEqualToString:MENU_ITEM_TYPE_TYPES_OPTION_COST])
        {
            [itemTypeOption setItemTypeCost: attrValue] ;
        }
        else if([aKey isEqualToString:MENU_ITEM_TYPES_OPTION_CHARGE_EACH])
        {
            [itemTypeOption setItemTypeChargePerCount: [attrValue integerValue]] ;
        }
        
    }
    return itemTypeOption;
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
    if( [elementName isEqualToString:MENU_ITEM_TYPE_TAG])
    {
        if(nil!=_currentItemType)
        {
            [self.menu addMenuItemType:_currentItemType];
            _currentItemType=nil;
        }
    }
    
}

-(fcItemType*) parseItemType:(NSDictionary *)attrs
{
    fcItemType *item = [[fcItemType alloc] init];
    for(NSString *aKey in [ attrs allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attrs valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"parseItemType: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:ID_ATTR])
        {
            [item setItemTypeID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:MENU_ITEM_TYPE_NAME])
        {
            [item setItemTypeName:attrValue];
        }
        else if([aKey isEqualToString:MENU_ITEM_TYPE_TEXT])
        {
            [item setItemTypeText:attrValue];
        }
        else if([aKey isEqualToString:SORT_ORDER_SHORT_ATTR])
        {
            [item setSortOrder:[attrValue integerValue]];
        }
        else if([aKey isEqualToString:MENU_ID_ATTR])
        {
            [item setMenuID:[NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:DRINK_TYPE_ITEM_GROUP_ATTR])
        {
            [item setItemGroupID:[NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:DRINK_TYPE_TYPE_ID_ATTR])
        {
            [item setItemTypeTypeID:[NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }

    }
    return item;

}

- (MenuItemOption*) parseMenuItemOption:(NSDictionary*)attrs
{
    MenuItemOption *option = [[MenuItemOption alloc] init];
    for(NSString *aKey in [ attrs allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attrs valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"parseMenuItemOption: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:ID_ATTR])
        {
            [option setOptionID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:MENU_ITEM_OPTION_NAME_ATTR])
        {
            [option setOptionName:attrValue];
        }
        else if([aKey isEqualToString:MENU_ITEM_OPTION_GROUP_ID])
        {
            [option setOptionGroupID:[NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:SORT_ORDER_SHORT_ATTR])
        {
            [option setSortOrder:[attrValue integerValue]];
        }
        
    }
    return option;
}

- (MenuItemOptionGroup*) parseMenuItemOptionsGroup:(NSDictionary*)attrs
{
    MenuItemOptionGroup *group =[[MenuItemOptionGroup alloc]init];
    
    for(NSString *aKey in [ attrs allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attrs valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"parseMenuItemOptionsGroup: Attr: %@ Value: %@",aKey,attrValue);
        
        if([aKey isEqualToString:ID_ATTR])
        {
            [group setGroupID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:MENU_ITEM_OPTION_GROUP_LONG_NAME_ATTR])
        {
            [group setGroupName:attrValue];
        }
        else if([aKey isEqualToString:MENU_ITEM_OPTION_GROUP_PART_NAME_ATTR])
        {
            [group setPartName:attrValue];
        }
        else if([aKey isEqualToString:SORT_ORDER_SHORT_ATTR])
        {
            [group setSortOrder:[attrValue integerValue]];
        }
        else if([aKey isEqualToString:MENU_ITEM_OPTION_GROUP_MULTISELECT_ATTR])
        {
            int multiSelect = [attrValue intValue];
            if(multiSelect == 0)
            {
                [group setSelectionType:ItemOptionGroupSelectOne];
            }
            else if(multiSelect==1)
            {
                [group setSelectionType:ItemOptionGroupSelectMulti];
            }
        }

    }
    return group;
    
}



- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
    
}

@end
