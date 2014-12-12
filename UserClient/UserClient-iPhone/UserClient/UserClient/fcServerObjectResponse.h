//
//  fcServerObjectResponse.h
//  UserClient
//
//  Created by Nick Ambrose on 1/20/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum EnumServerObjectResponseResult_t
{
    EnumServerOjbectResponseResultOK,
    EnumServerOjbectResponseResultFailed
}EnumServerObjectResponseResult;

typedef enum EnumServerOjbectResponseObjectStatus_t
{
    EnumServerOjbectResponseObjectStatusUnknown,
    EnumServerOjbectResponseObjectStatusObjectIncluded,
    EnumServerOjbectResponseObjectStatusObjectNotExist,
    EnumServerOjbectResponseObjectStatusNeedNew,
    EnumServerOjbectResponseObjectStatusHaveLatest
    
}EnumServerOjbectResponseObjectStatus;

@interface fcServerObjectResponse : NSObject



@property (nonatomic,assign) EnumServerObjectResponseResult responseResult;
@property (nonatomic,assign) EnumServerOjbectResponseObjectStatus objectStatus;
@property (nonatomic,assign) BOOL objectVerIncluded;
@property (nonatomic,strong) NSNumber *objectVersion;

+(fcServerObjectResponse*) parseFromXMLAttributes:(NSDictionary*)attributes;

@end
