//
//  fcUserDrinkTable.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcUserItem;

@interface fcUserItemTable : NSObject

@property (nonatomic,strong) NSMutableDictionary *userItems;

- (id)init;
- (void) clear;
- (void) addUserItem:(fcUserItem*)item;
- (fcUserItem*)getUserItemForID:(NSNumber*)itemID;
- (void) removeUserItem:(NSNumber*)itemID;
- (BOOL) isEmpty;
@end
