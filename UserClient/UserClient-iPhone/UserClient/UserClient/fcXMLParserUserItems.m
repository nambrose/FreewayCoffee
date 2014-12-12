//
//  fcXMLParserUserItems.m
//  UserClient
//
//  Created by Nick Ambrose on 9/15/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import "fcXMLParserUserItems.h"
#import "fcUserItem.h"
#import "fcUserItemOption.h"
#import "fcServerObjectResponse.h"

#import "XMLHelper.h"
#import "Constants.h"

@implementation fcXMLParserUserItems
@synthesize currentItem=_currentItem;
@synthesize parseSuccessful=_parseSuccessful;
@synthesize addOrEditedItem=_addOrEditedItem;
@synthesize processingType=_processingType;
@synthesize wasServerResponseGood=_wasServerResponseGood;
@synthesize deletedItemID=_deletedItemID;
@synthesize signonError=_signonError;
@synthesize objectResponse=_objectResponse;

- (id) initWithProcessingType:(UserItemsProccessingType)processType
{
    self = [super init];
    if (self)
    {
        _processingType=processType;
        _parseSuccessful=FALSE;
        _wasServerResponseGood=FALSE;
        //_menuItemOptionGroups = [[NSMutableDictionary alloc]init];
        //_currentItemType=nil;
        _signonError=FALSE;
        _objectResponse=nil;
        
    }
    
    return self;
}
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
    if([elementName isEqualToString:USER_ITEM_TAG])
    {
        self.addOrEditedItem = self.currentItem;
        self.currentItem=nil;
    }
    
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    if ([elementName isEqualToString:USER_ADD_ITEM_TAG])
    {
        if(self.processingType!=UserItemsProccessingTypeAddItem)
        {
            return;
        }
        NSString *response =[attributeDict valueForKey:RESULT_ATTR]  ;
        
        if(nil==response)
        {
            return; // not good
        }
        response = [XMLHelper stringByDecodingURLFormat:response];
        self.wasServerResponseGood=TRUE;
        self.parseSuccessful=TRUE;
        
        self.objectResponse= [fcServerObjectResponse parseFromXMLAttributes:attributeDict];
        
    }
    else if([elementName isEqualToString:USER_EDIT_ITEM_TAG])
    {
        if(self.processingType!=UserItemsProccessingTypeEditItem)
        {
            return;
        }
        NSString *response =[attributeDict valueForKey:RESULT_ATTR]  ;
        
        if(nil==response)
        {
            return; // not good
        }
        response = [XMLHelper stringByDecodingURLFormat:response];
        self.wasServerResponseGood=TRUE;
        self.parseSuccessful=TRUE;
        
        self.objectResponse= [fcServerObjectResponse parseFromXMLAttributes:attributeDict];
    }
    else if([elementName isEqualToString:ITEM_DELETED_RESPONSE_TAG])
    {
        if(self.processingType!=UserItemsProccessingTypeDeleteItem)
        {
            return;
        }
        NSString *response =[attributeDict valueForKey:RESULT_ATTR]  ;
        
        if(nil==response)
        {
            return; // not good
        }
        response = [XMLHelper stringByDecodingURLFormat:response];
        if([response isEqualToString:OK_ATTR_VALUE])
        {
            self.wasServerResponseGood=TRUE;
        }
        //[defaultOption setGroupID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        NSString *deletedIDStr = [attributeDict valueForKey:DELETED_ITEM_ID_ATTR]  ;
        if(nil==deletedIDStr)
        {
            self.parseSuccessful=FALSE;
            return;
        }
        deletedIDStr = [XMLHelper stringByDecodingURLFormat:deletedIDStr];
        self.deletedItemID = [NSNumber numberWithInteger:[deletedIDStr integerValue]] ;
        self.parseSuccessful=TRUE;
    }
    else if([elementName isEqualToString:USER_ITEM_TAG])
    {
        self.currentItem = [fcXMLParserUserItems parseUserItem:attributeDict];
    }
    else if([elementName isEqualToString:USER_ITEM_OPTION_TAG])
    {
        if(nil!=self.currentItem)
        {
            
            fcUserItemOption *option = [fcXMLParserUserItems parseUserItemOption:attributeDict];
            [self.currentItem addUserItemOption:option];
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
- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
    
}
// Not the Options, JUST the toplevel
+ (fcUserItem*) parseUserItem:(NSDictionary *)attributeDict
{
    fcUserItem *item = [[fcUserItem alloc]init];
    
    for(NSString *aKey in [ attributeDict allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attributeDict valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"parseUserItem: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:ID_ATTR])
        {
            [item setUserItemID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:USER_ITEM_OPTIONS_TEXT_ATTR])
        {
            [item setUserItemOptionsText:attrValue];
        }
        else if([aKey isEqualToString:USER_ITEM_TYPE_NAME_ATTR])
        {
            [item setUserItemItemTypeLongDescr:attrValue];
        }
        else if([aKey isEqualToString:USER_ITEM_NAME_ATTR])
        {
            [item setUserItemName:attrValue];
        }
        else if([aKey isEqualToString:USER_ITEM_EXTRA_OPTIONS_ATTR])
        {
            [item setUserItemExtra:attrValue];
        }
        else if([aKey isEqualToString:USER_ITEM_TYPE_ID])
        {
            [item setItemTypeID:[NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:USER_ITEM_INCLUDE_DEFAULT_ATTR])
        {
            [item setIncludeDefault: [XMLHelper parseBoolFromString:attrValue] ];
        }
        else if([aKey isEqualToString:USER_ITEM_COST_ATTR])
        {
            [item setUserItemCost:attrValue];
        }
        else if([aKey isEqualToString:MENU_ID_ATTR])
        {
            [item setMenuID:[NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
            
        
    }
    
    return item;

}

+ (fcUserItemOption*) parseUserItemOption:(NSDictionary *)attributeDict
{
    fcUserItemOption *option = [[fcUserItemOption alloc]init];
    
    for(NSString *aKey in [ attributeDict allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attributeDict valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"parseUserItem: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:ID_ATTR])
        {
            [option setUserItemOptionID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:USER_ITEM_OPTION_ITEM_TYPES_OPTION_ID_ATTR])
        {
            [option setItemTypesOptionID:  [NSNumber numberWithInteger:[attrValue integerValue]] ];
        }
        else if([aKey isEqualToString:USER_ITEM_OPTION_ITEM_OPTION_ID_ATTR])
        {
            [option setItemOptionID: [NSNumber numberWithInteger:[attrValue integerValue]]];
        }
        else if([aKey isEqualToString:USER_ITEM_OPTION_ITEM_OPTION_GROUP_ID_ATTR])
        {
            [option setItemOptionGroupID:[NSNumber numberWithInteger:[attrValue integerValue]]];
        }
        else if([aKey isEqualToString:USER_ITEM_OPTION_COUNT_ATTR])
        {
            [option setItemOptionCount: [attrValue integerValue]];
        }
        else if([aKey isEqualToString:SORT_ORDER_SHORT_ATTR])
        {
            [option setSortOrder:[attrValue integerValue]];
        }
        
    }
    return option;
}

@end
