//
//  fcXMLParserLocations.h
//  UserClient
//
//  Created by Nick Ambrose on 1/17/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

#define XML_PARSER_LOCATIONS_PARSE_TYPE_UNKNOWN 0
#define XML_PARSER_LOCATIONS_PARSE_TYPE_GET_LOCATIONS 1
#define XML_PARSER_LOCATIONS_PARSE_TYPE_SET_USER_LOCATION 2


@class fcLocationTable;
@class fcLocation;
@class fcError;
@class fcLastOrder;


@interface fcXMLParserLocations : NSObject <NSXMLParserDelegate>


// NOTE WHEN YOU UPDATE THIS, CHECK IF YOU NEED TO UPDATE THE LOCATION PARSING IN ITEMS_LIST PARSER.
// THIS IS UGLY AND I THOUGHT I GOT AWAY FROM THAT CRAP !!!!

@property (nonatomic,strong) fcLocationTable *locations;
@property (nonatomic,assign) BOOL signonError;
@property (nonatomic,assign) BOOL parseSuccessful;
@property (nonatomic,assign) NSInteger parseType; // Get Locations, Set Location etc
@property (nonatomic,assign) BOOL responseSuccess;
@property (nonatomic,strong) fcError *error;
@property (nonatomic,strong) NSNumber *updatedLocationID;
@property (nonatomic,strong) fcLocation *currentLocation;

@property (nonatomic,strong) fcLastOrder *lastOrder;

- (id) init;

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict;
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName;

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string;



@end
