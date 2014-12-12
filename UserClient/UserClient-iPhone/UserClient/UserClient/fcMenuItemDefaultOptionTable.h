//
//  fcMenuItemDefaultOptionTable.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcMenuItemDefaultOption;

@interface fcMenuItemDefaultOptionTable : NSObject

@property (nonatomic,strong) NSMutableArray *defaultOptions;

-(void)clear;
-(NSUInteger) size;
-(void)addDefaultOption:(fcMenuItemDefaultOption*)defaultOption;
-(fcMenuItemDefaultOption*)getOptionWithIndex:(NSInteger)index;

@end
