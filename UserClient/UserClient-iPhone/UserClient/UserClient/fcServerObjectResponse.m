//
//  fcServerObjectResponse.m
//  UserClient
//
//  Created by Nick Ambrose on 1/20/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import "fcServerObjectResponse.h"
#import "XMLConstants.h"
#import "XMLHelper.h"

@implementation fcServerObjectResponse
/*
EnumServerObjectResponseResult_t
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
*/


@synthesize responseResult=_responseResult;
@synthesize objectStatus=_objectStatus;
@synthesize objectVerIncluded=_objectVerIncluded;
@synthesize objectVersion=_objectVersion;

- (id)init
{
    self = [super init];
    if (self)
    {
        _responseResult=EnumServerOjbectResponseResultFailed;
        _objectStatus=EnumServerOjbectResponseObjectStatusUnknown;
        _objectVerIncluded=FALSE;
        _objectVersion=[NSNumber numberWithInt:OBJECT_NONE_VERSION];
    }
    return self;
    
}
+(fcServerObjectResponse*) parseFromXMLAttributes:(NSDictionary*)attributes
{
    fcServerObjectResponse *response = [[fcServerObjectResponse alloc]init];
        
    for(NSString *aKey in [ attributes allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attributes valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
            
        //FC_Log(@"parseUserItem: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:RESULT_ATTR])
        {
            if([attrValue isEqualToString:OK_ATTR_VALUE])
            {
                response.responseResult = EnumServerOjbectResponseResultOK;
            }
            else
            {
                response.responseResult =EnumServerOjbectResponseResultFailed;
            }
        }
        else if([aKey isEqualToString:OBJECT_RESPONSE_ATTR])
        {
            if([attrValue isEqualToString:OBJECT_RESPONSE_NEED_NEW])
            {
                response.objectStatus = EnumServerOjbectResponseObjectStatusNeedNew;
            }
            else if([attrValue isEqualToString:OBJECT_RESPONSE_OBJ_INCLUDED])
            {
                response.objectStatus =EnumServerOjbectResponseObjectStatusObjectIncluded;
            }
            else if([attrValue isEqualToString:OBJECT_RESPONSE_NOT_EXIST])
            {
                response.objectStatus =EnumServerOjbectResponseObjectStatusObjectNotExist;
            }
            else if([attrValue isEqualToString:OBJECT_RESPONSE_HAVE_LATEST])
            {
                response.objectStatus =EnumServerOjbectResponseObjectStatusHaveLatest;
            }
        }
        else if([aKey isEqualToString:OBJECT_RESPONSE_LATEST_VER_ATTR])
        {
            response.objectVerIncluded=TRUE;
            response.objectVersion = [NSNumber numberWithInteger:[attrValue integerValue]];
        }
        
    }

    return response;
}



@end
