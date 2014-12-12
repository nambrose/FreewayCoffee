//
//  CarDateIndexItem.h
//  UserClient
//
//  Created by Nick Ambrose on 9/2/12.
//  Copyright (c) 2012 Freeway Coffee. All rights reserved.
//

#ifndef UserClient_CarDateIndexItem_h
#define UserClient_CarDateIndexItem_h

#import <Foundation/Foundation.h>

#define CAR_DATA_ITEM_TYPE_MAKE 1
#define CAR_DATA_ITEM_TYPE_MODEL 2
#define CAR_DATA_ITEM_TYPE_COLOR 3
#define CAR_DATA_ITEM_TYPE_TAG 4

@interface CarDataIndexItem : NSObject
{
    int ItemType;
    //int ItemID;
}

@property(nonatomic, assign) int ItemType;
//@property(nonatomic, assign) int ItemID;


@end


#endif
