//
//  fcLocationTable.h
//  UserClient
//
//  Created by Nick Ambrose on 12/24/12.
//  Copyright (c) 2012 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcLocation;

@interface fcLocationTable : NSObject

@property (nonatomic,strong) NSMutableDictionary *LocationData;

- (id)init;
- (void)clear;
- (NSUInteger)size;

- (fcLocation*) getLocation:(NSNumber *)locationId;
- (void) setLocation:(fcLocation*)location; // Overwrites any existing location
- (void) setLocationTable:(fcLocationTable*)table;
@end
