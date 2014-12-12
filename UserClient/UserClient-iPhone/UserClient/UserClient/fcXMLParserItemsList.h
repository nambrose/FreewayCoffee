//
//  fcXMLParserItemsList.h
//  UserClient
//
//  Created by Nick Ambrose on 12/24/12.
//  Copyright (c) 2012,2013 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcUserItem;
@class fcLastOrder;
@class fcLastOrderItem;
@class fcLocation;
@class fcLocationTable;
@class fcUserInfo;
@class fcServerObjectResponse;

@interface fcXMLParserItemsList : NSObject <NSXMLParserDelegate>

// NOTE WHEN YOU UPDATE THIS, CHECK IF YOU NEED TO UPDATE THE LOCATION PARSING IN  PARSER_LOCATIONS.
// THIS IS UGLY AND I THOUGHT I GOT AWAY FROM THAT CRAP !!!!


@property (nonatomic,strong) fcUserItem *currentItem; // Temp, used until we get a whole one
@property (nonatomic,assign) BOOL signonError;
@property (nonatomic,strong) fcLastOrder *lastOrder;
@property (nonatomic,copy) NSString *updatedFreeDrinks;
@property (nonatomic,assign) BOOL parseSuccessful;
@property (nonatomic,assign) BOOL wasServerResponseGood;
@property (nonatomic,assign) BOOL wasOrderHereResponse;

@property (nonatomic,strong) fcLocationTable *locations;
@property (nonatomic,strong) fcUserInfo *userInfo;
@property (nonatomic,strong) fcLocation *currentLocation;
@property (nonatomic,strong) fcServerObjectResponse *orderObjResponse;
- (id) init;

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict;
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName;

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string;

+ (fcLastOrderItem*) parseLastOrderItem:(NSDictionary *)attributeDict;
@end
