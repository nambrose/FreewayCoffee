//
//  fcMenuItemDefaultOption.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface fcMenuItemDefaultOption : NSObject

//@property (nonatomic,strong) NSNumber *defaultOptionID;
@property (nonatomic,strong) NSNumber *itemTypeID;
@property (nonatomic,strong) NSNumber *groupID;
@property (nonatomic,strong) NSNumber *itemOptionID;
@property (nonatomic,assign) NSInteger itemOptionCount;
- (id)init;
@end
