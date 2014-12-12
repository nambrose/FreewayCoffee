//
//  XMLParserMenu.h
//  UserClient
//
//  Created by Nick Ambrose on 9/3/12.
//  Copyright (c) 2012 Immerse Images. All rights reserved.
//

#import <Foundation/Foundation.h>

@class MenuItemOptionGroup;
@class MenuItemOption;
@class fcItemType;
@class fcItemTypeOptionTable;
@class fcItemTypeOption;
@class fcMenuItemDefaultOption;
@class fcMenuItemMandatoryOption;
@class fcServerObjectResponse;
@class fcMenu;

@interface XMLParserMenu : NSObject <NSXMLParserDelegate>

{
@private
    fcItemType *_currentItemType;
   
}

@property (nonatomic,assign) BOOL signonError;
@property (nonatomic,strong) fcServerObjectResponse *serverResponse;
@property (nonatomic,strong) fcMenu *menu;

// Required ones
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName;
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict;
- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string;


- (MenuItemOptionGroup*) parseMenuItemOptionsGroup:(NSDictionary*)attrs;
- (MenuItemOption*) parseMenuItemOption:(NSDictionary*)attrs;
- (fcItemType*) parseItemType:(NSDictionary*)attrs;
- (fcItemTypeOption*) parseItemTypeOption:(NSDictionary*)attrs;
- (fcMenuItemDefaultOption*)parseDefaultOption:(NSDictionary*)attrs;
- (fcMenuItemMandatoryOption*)parseMandatoryOption:(NSDictionary*)attrs;
@end
