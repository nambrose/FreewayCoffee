//
//  fcMenuItemMandatoryOptionTable.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcMenuItemMandatoryOption;

@interface fcMenuItemMandatoryOptionTable : NSObject

@property (nonatomic,strong) NSMutableArray *mandatoryOptions;

-(void)clear;
-(NSUInteger) size;

-(void)addMandatoryOption:(fcMenuItemMandatoryOption*)mandatoryOption;
- (BOOL) isOptionGroupMandatoryForItemType:(NSNumber*) itemTypeID andOptionGroup:(NSNumber*)optionGroupID;
@end
