//
//  fcUserInfo.m
//  UserClient
//
//  Created by Nick Ambrose on 1/12/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import "fcUserInfo.h"
#import "XMLHelper.h"
#import "XMLConstants.h"
#import "fcLocation.h"

@implementation fcUserInfo

@synthesize userID=_userID;
@synthesize userName=_userName;
@synthesize userEmail=_userEmail;
@synthesize userTag=_userTag;
@synthesize userFreeDrinksCount=_userFreeDrinksCount;
@synthesize userTimeToLocation=_userTimeToLocation;
@synthesize userIsDemo=_userIsDemo;
@synthesize userIsLocked=_userIsLocked;
@synthesize userType=_userType;
@synthesize userArriveMode=_userArriveMode;
@synthesize userLocationID=_userLocationID;
//@synthesize userTZ=_userTZ;
@synthesize userPayMethod=_userPayMethod;
@synthesize userIncarnation=_userIncarnation;

+(fcUserInfo*)parseFromXML:(NSDictionary *)attributeDict
{
    fcUserInfo *user = [[fcUserInfo alloc]init];
    
    for(NSString *aKey in [ attributeDict allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attributeDict valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        
        //FC_Log(@"parseUserInfo: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:ID_ATTR])
        {
            [user setUserID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:USER_INFO_USER_NAME])
        {
            [user setUserName:attrValue];
        }
        else if([aKey isEqualToString:USER_INFO_USER_EMAIL])
        {
            [user setUserEmail:attrValue];
        }
        else if([aKey isEqualToString:USER_INFO_USER_TAG])
        {
            [user setUserTag:attrValue];
        }
        else if([aKey isEqualToString:USER_INFO_USER_FREE_DRINKS])
        {
            [user setUserFreeDrinksCount:[NSNumber numberWithInteger:[attrValue integerValue]]];
        }
        else if([aKey isEqualToString:USER_INFO_TIME_TO_LOCATION_ATTR])
        {
            [user setUserTimeToLocation:[NSNumber numberWithInteger:[attrValue integerValue]]];
        }
        else if([aKey isEqualToString:USER_INFO_USER_IS_DEMO_ATTR])
        {
            [user setUserIsDemo:[XMLHelper parseBoolFromString:attrValue]];
        }
        else if([aKey isEqualToString:USER_INFO_USER_IS_LOCKED_ATTR])
        {
            [user setUserIsLocked:[XMLHelper parseBoolFromString:attrValue]];
        }
        else if([aKey isEqualToString:USER_INFO_USER_TYPE_ATTR])
        {
            [user setUserType:[NSNumber numberWithInteger:[attrValue integerValue]]];
        }
        else if([aKey isEqualToString:USER_INFO_TIME_TO_LOCATION_ATTR])
        {
            [user setUserTimeToLocation:[NSNumber numberWithInteger:[attrValue integerValue]]];
        }
        /*
        else if([aKey isEqualToString:USER_INFO_TZ_ATTR])
        {
            [user setUserTZ:attrValue];
        }
         */
        else if([aKey isEqualToString:USER_INFO_ARRIVE_MODE_ATTR])
        {
            [user setUserArriveMode:[NSNumber numberWithInteger:[attrValue integerValue]]];
        }
        else if([aKey isEqualToString:USER_INFO_LOCATION_ID])
        {
            [user setUserLocationID:[NSNumber numberWithInteger:[attrValue integerValue]]];
        }
        else if([aKey isEqualToString:USER_INFO_PAY_METHOD_ATTR])
        {
            [user setUserPayMethod:[NSNumber numberWithInteger:[attrValue integerValue]]];
        }
        else if([aKey isEqualToString:INCARNATION_ATTR])
        {
            [user setUserIncarnation:[NSNumber numberWithInteger:[attrValue integerValue]]];
        }
        
        
    }
    return user;
    

}
-(BOOL) isLocationSet
{
    if(self.userLocationID==nil)
    {
        return FALSE;
    }

    if([fcLocation isLocationIDNone:self.userLocationID])
    {
        return FALSE;
    }
    return TRUE;

}

@end
