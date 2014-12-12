//
//  fcLastOrderItem.h
//  UserClient
//
//  Created by Nick Ambrose on 9/16/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface fcLastOrderItem : NSObject

@property (nonatomic,copy) NSString *orderItemID;
@property (nonatomic,copy) NSString *orderItemDescription;
@property (nonatomic,copy) NSString *orderItemCost;
@end
