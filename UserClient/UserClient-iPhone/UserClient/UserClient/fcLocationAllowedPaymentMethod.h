//
//  fcLocationAllowedPaymentMethod.h
//  UserClient
//
//  Created by Nick Ambrose on 2/9/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

// I hate myself
#define LOCATION_PAY_METHOD_UNKNOWN 0

#define LOCATION_PAY_METHOD_IN_APP 1
#define LOCATION_PAY_METHOD_IN_STORE 2

// TODO HORRID FIXME NOW THIS NEEDS TO COME FROM SERVER OR SOMEWHERE ELSE AT LEAST
#define LOCATION_PAY_IN_STORE_STRING @"Pay at Location"
#define LOCATION_PAY_IN_APP_STRING @"In-App"
// XML

#define LOCATION_PAY_METHOD_TAG @"l_p_m"
#define LOCATION_PAY_METHOD_ATTR @"l_p_m_pm"

// END XML

@interface fcLocationAllowedPaymentMethod : NSObject

@property (nonatomic,strong) NSNumber *paymentMethod;
@property (nonatomic,strong) NSString *paymentMethodText;

+(fcLocationAllowedPaymentMethod*) parseFromXMLAttributes:(NSDictionary*)attributes;

-(BOOL) doesMatchMode:(NSNumber*)incomingMode;
+(BOOL) isPayInStore:(NSNumber*)method;


@end
