//
//  fcUserTipTable.h
//  UserClient
//
//  Created by Nick Ambrose on 9/29/12.
//  Copyright (c) 2012 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcUserTip;

@interface fcUserTipTable : NSObject


@property (nonatomic,strong) NSMutableDictionary *userTips; // Indexed by LocationID

- (id)init;
- (void) clear;
- (void) addTip:(fcUserTip*)tip;
- (fcUserTip*) getTipForLocation:(NSNumber*)locationID; 
@end
