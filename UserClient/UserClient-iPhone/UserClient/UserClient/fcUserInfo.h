//
//  fcUserInfo.h
//  UserClient
//
//  Created by Nick Ambrose on 1/12/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

#define USER_DEMO_VALUE_DEMO @"1"
#define USER_DEMO_VALUE_NON_DEMO @"0"
#define ARRIVE_MODE_CAR_NUM 0

#define ARRIVE_MODE_CAR_STR @"0"
#define ARRIVE_MODE_WALKUP_STR @"1"
#define ARRIVE_MODE_WALKUP_NUM 1

#define USER_INFO_TAG @"user_info"

#define USER_INFO_USER_NAME @"user_name"
#define USER_INFO_USER_EMAIL @"user_email"
#define USER_INFO_USER_TAG @"user_tag"
#define USER_INFO_USER_FREE_DRINKS @"user_free_drinks"
#define USER_INFO_USER_IS_DEMO_ATTR @"user_is_demo"
#define USER_INFO_USER_IS_LOCKED_ATTR @"user_locked"
#define USER_INFO_USER_TYPE_ATTR @"user_type"
#define USER_INFO_TIME_TO_LOCATION_ATTR @"user_time_to_location"
//#define USER_INFO_TZ_ATTR @"user_tz"
#define USER_INFO_ARRIVE_MODE_ATTR @"user_arrive_mode"
#define USER_INFO_LOCATION_ID @"user_location_id"
#define USER_INFO_PAY_METHOD_ATTR @"user_pay_method"

enum UserTypeEnum
{
    UserTypeNormal=0,
    UserTypeAdmin=1,
    UserTypeSuper=2
};

@interface fcUserInfo : NSObject

@property(nonatomic,strong) NSNumber *userID;
@property(nonatomic,strong) NSString *userName;
@property(nonatomic,strong) NSString *userEmail;
@property(nonatomic,strong) NSString *userTag;
@property(nonatomic,strong) NSNumber *userFreeDrinksCount;
@property(nonatomic,strong) NSNumber *userTimeToLocation;
@property(nonatomic,assign) BOOL userIsDemo;
@property(nonatomic,assign) BOOL userIsLocked;
@property(nonatomic,strong) NSNumber *userType; // Admin, Super etc -- see enum
@property(nonatomic,strong) NSNumber *userArriveMode; // Walkup, Car etc.
@property(nonatomic,strong) NSNumber *userLocationID; // Current Location
//@property(nonatomic,strong) NSString *userTZ;
@property(nonatomic,strong) NSNumber *userPayMethod; // InStore,InApp etc
@property(nonatomic,strong) NSNumber *userIncarnation;

+(fcUserInfo*)parseFromXML:(NSDictionary *)attributeDict;

-(BOOL) isLocationSet;
//-(BOOL) isTimeToLocationSetAndValid; // Checks that its not nil and >0

@end
