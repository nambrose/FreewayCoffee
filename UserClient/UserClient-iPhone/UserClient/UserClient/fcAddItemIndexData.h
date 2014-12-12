//
//  fcAddItemIndexData.h
//  UserClient
//
//  Created by Nick Ambrose on 9/8/12.
//  Copyright (c) 2012 Nick Ambrose. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface fcAddItemIndexData : NSObject

@property (nonatomic,strong) NSNumber *itemID;
@property (nonatomic,assign) NSInteger sortOrder;

- (NSComparisonResult)compare:(fcAddItemIndexData *)otherObject;

@end
