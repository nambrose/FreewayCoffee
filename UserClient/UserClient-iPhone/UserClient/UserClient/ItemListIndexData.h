//
//  ItemListIndexData.h
//  UserClient
//
//  Created by Nick Ambrose on 1/20/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ItemListIndexData : NSObject
{
    int ItemType;
    int ItemID;
}

@property(nonatomic, assign) int ItemType;
@property(nonatomic, assign) int ItemID;


@end
