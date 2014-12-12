//
//  fcXMLParserMisc.h
//  UserClient
//
//  Created by Nick Ambrose on 2/10/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//


// Intended for small "misc" commands like update Pay Method, Credit Card etc. Not sure if this is going to be a good idea. Likely not.
#import <Foundation/Foundation.h>

@class fcError;

typedef enum XMLParserMiscRequestType_t
{
    XMLParserMiscRequestTypeUpdatePayMethod,
    XMLParserMiscRequestTypeDeleteCreditCard
}XMLParserMiscRequestType;


@interface fcXMLParserMisc : NSObject <NSXMLParserDelegate>

@property (nonatomic,assign) XMLParserMiscRequestType requestType;
@property (nonatomic,strong) fcError *error;
@property (nonatomic,assign) BOOL signonError;
@property (nonatomic,assign) BOOL responseOK;
@property (nonatomic,strong) NSNumber *deletedCreditCardID;

- (id) initWithRequestType:(XMLParserMiscRequestType)requestType;

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict;
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName;

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string;



@end
