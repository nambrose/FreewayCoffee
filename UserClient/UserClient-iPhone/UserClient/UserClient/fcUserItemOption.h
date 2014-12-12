//
//  fcUserItemOption.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>
@class fcMenu;

@interface fcUserItemOption : NSObject

@property(nonatomic,strong) NSNumber *userItemOptionID;
@property(nonatomic,strong) NSNumber *itemOptionID;
@property(nonatomic,strong) NSNumber *itemTypesOptionID;
@property(nonatomic,strong) NSNumber *itemOptionGroupID;
@property(nonatomic,assign) NSInteger itemOptionCount;

@property(nonatomic,assign) NSInteger sortOrder;

- (id)init;
- (NSComparisonResult)compare:(fcUserItemOption *)otherObject;
- (NSString*) makeOptionValueString:(NSNumber*)itemType withMenu:(fcMenu*)menu;
- (NSDecimalNumber*) getTotalCost:(NSNumber*)itemType withMenu:(fcMenu*)menu;
- (fcUserItemOption*)clone;
- (void) decrementCount;
@end
