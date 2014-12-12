//
//  fcItemTypeTable.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcItemType;

@interface fcItemTypeTable : NSObject

@property (nonatomic,strong) NSMutableDictionary *itemTypes;

-(id)init;
-(void) addItemType:(fcItemType*)itemType;
-(fcItemType*) getItemTypeByID:(NSNumber*)itemTypeID;
-(void)clear;
-(NSUInteger) size;

@end
