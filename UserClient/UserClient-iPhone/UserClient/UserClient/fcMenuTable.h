//
//  fcMenuTable.h
//  UserClient
//
//  Created by Nick Ambrose on 1/20/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcMenu;

@interface fcMenuTable : NSObject

@property (nonatomic,strong) NSMutableDictionary *menus;
- (id)init;
- (void)clear;
- (NSUInteger)size;

- (fcMenu*) getMenu:(NSNumber *)menuId;
- (void) setMenu:(fcMenu*)menu; // Overwrites any existing location
- (void) setMenuTable:(fcMenuTable*)table;
- (void) clearAllDownloadedData;
- (void) deleteMenu:(NSNumber *)menuID;
@end
