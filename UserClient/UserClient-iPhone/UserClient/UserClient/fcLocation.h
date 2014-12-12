//
//  fcLocation.h
//  UserClient
//
//  Created by Nick Ambrose on 12/24/12.
//  Copyright (c) 2012 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

// XML
#define LOCATIONS_LIST_TAG @"locations"
#define UPDATED_USER_LOCATION_TAG @"updated_location"
#define USER_LOCATION_TAG @"user_location"
// END XML

#define LOCATION_DOES_NOT_SUPPORT_THIS_PAYMENT @"Not supported at Location"


#define LOCATION_NONE_ID 0

@class fcLocationAllowedArrivalMethod;
@class fcLocationAllowedPaymentMethod;

@interface fcLocation : NSObject

@property (nonatomic,strong) NSNumber *LocationID;
@property (nonatomic,strong) NSString *LocationDescription;
@property (nonatomic,strong) NSString *LocationAddress;
@property (nonatomic,strong) NSString *LocationHours;
@property (nonatomic,strong) NSString *LocationGPSLat;
@property (nonatomic,strong) NSString *LocationGPSLon;
@property (nonatomic,strong) NSString *LocationEmail;
@property (nonatomic,strong) NSString *LocationTZ;
@property (nonatomic,strong) NSString *LocationPhone;
@property (nonatomic,assign) NSInteger LocationOpenMode;
@property (nonatomic,strong) NSString *LocationLongDescr;
@property (nonatomic,strong) NSString *LocationInstructions;
@property (nonatomic,strong) NSNumber *LocationMenuID;
@property (nonatomic,assign) BOOL      LocationIsActive;
@property (nonatomic,strong) NSNumber *incarnation;
@property (nonatomic,strong) NSMutableArray *allowedArrivalModes;
@property (nonatomic,strong) NSMutableArray *allowedPaymentMethods;
@property (nonatomic,assign) BOOL showPhone;
@property (nonatomic,assign) BOOL showEmail;
@property (nonatomic,assign) NSString *LocationTalkToTheManagerNumber;
@property (nonatomic,strong) NSDecimalNumber *LocationSalesTaxRate;
@property (nonatomic,strong) NSDecimalNumber *LocationConvenienceFee;

- (id)init;

+(fcLocation*)parseFromXML:(NSDictionary *)attributeDict;
+(BOOL) isLocationIDNone:(NSNumber*)locationID;

-(BOOL) isLocationNone;
-(BOOL) isLongDescrPopulated;
- (NSString*) makeDetailText;
- (NSString*) makeLocationMapsURL;
-(BOOL) IsOrderTaxable;
-(NSDecimalNumber*) CalculateSalesTax:(NSDecimalNumber*) amount;
-(NSDecimalNumber*) CalculateConvenienceFeeForPayMethod:(NSNumber*)payMethod;

-(void) addPaymentMethod:(fcLocationAllowedPaymentMethod*)incomingMethod;
-(void) clearAllPaymentMethods;
-(BOOL) isPaymentMethodAllowed:(NSNumber*)method;
-(NSString*) makePaymentMethodStringForAllMethods;
-(NSUInteger) getCountOfPaymentMethods;


-(void) addArrivalMethod:(fcLocationAllowedArrivalMethod*)incomingMethod;
-(void) clearAllArrivalMethods;
-(BOOL) isArrivalMethodAllowed:(NSNumber*)method;
-(NSString*) makeArrivalMethodStringForAllMethods;
-(NSUInteger) getCountOfArrivalMethods;
- (BOOL) IsTalkToManagerSet;

@end
