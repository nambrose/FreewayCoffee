//
//  fcXMLParserUserItems.h
//  UserClient
//
//  Created by Nick Ambrose on 9/15/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcUserItem;
@class fcUserItemOption;
@class fcServerObjectResponse;


typedef enum
{
    UserItemsProccessingTypeDownloadUserItems,
    UserItemsProccessingTypeAddItem,
    UserItemsProccessingTypeEditItem,
    UserItemsProccessingTypeDeleteItem
}UserItemsProccessingType;


@interface fcXMLParserUserItems : NSObject  <NSXMLParserDelegate>

@property (nonatomic,strong) fcUserItem *currentItem; // Temp, used until we get a whole one
@property (nonatomic,assign) BOOL parseSuccessful;
@property (nonatomic,assign) BOOL wasServerResponseGood;
@property (nonatomic,strong) fcUserItem *addOrEditedItem;
@property (nonatomic,strong) NSNumber *deletedItemID;
@property (nonatomic,assign) UserItemsProccessingType processingType;
@property (nonatomic,assign) BOOL signonError;
@property (nonatomic,strong) fcServerObjectResponse *objectResponse;
// Required ones
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName;
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict;
- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string;

- (id) initWithProcessingType:(UserItemsProccessingType)processType;

// These are mostly for downloading initial order items
+ (fcUserItem*) parseUserItem:(NSDictionary *)attributeDict; // Not the Options, JUST the toplevel
+ (fcUserItemOption*) parseUserItemOption:(NSDictionary *)attributeDict;
@end
