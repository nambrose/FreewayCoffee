//
//  fcLocationAllowedArrivalMethod.h
//  UserClient
//
//  Created by Nick Ambrose on 2/9/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

// XML
#define LOCATION_ARRIVE_MODE_TAG @"l_a_m"
// END XML


#define ARRIVAL_MODE_NOT_ALLOWED_STRING @"Not Allowed At Location"

@interface fcLocationAllowedArrivalMethod : NSObject

@property (nonatomic,strong) NSNumber *modeAllowed;
@property (nonatomic,strong) NSString *modeName;

+(fcLocationAllowedArrivalMethod*) parseFromXMLAttributes:(NSDictionary*)attributes;

-(BOOL) doesMatchMode:(NSNumber*)incomingMode;

@end
