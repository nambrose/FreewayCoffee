//
//  fcLocation.m
//  UserClient
//
//  Created by Nick Ambrose on 12/24/12.
//  Copyright (c) 2012 Freeway Coffee. All rights reserved.
//

#import "fcLocation.h"
#import "XMLHelper.h"
#import "Constants.h"
#import "XMLConstants.h"
#import "fcLocationAllowedArrivalMethod.h"
#import "fcLocationAllowedPaymentMethod.h"


// XML
#define USER_LOCATION_PHONE_ATTR @"l_p"
#define USER_LOCATION_EMAIL_ATTR @"l_e"
#define USER_LOCATION_DESCRIPTION_ATTR @"l_d"
#define USER_LOCATION_ADDRESS_ATTR @"l_a"
#define USER_LOCATION_OPEN_MODE_ATTR @"l_om"
#define USER_LOCATION_HOURS_ATTR @"l_h"
#define USER_LOCATION_GPS_LAT_ATTR @"lg_lat"
#define USER_LOCATION_GPS_LON_ATTR @"l_glon"
#define USER_LOCATION_TZ_ATTR @"l_tz"
#define USER_LOCATION_INSTRUCTIONS_ATTR @"l_i"
#define USER_LOCATION_LONG_DESCR_ATTR @"l_lo_d"
#define USER_LOCATION_MENU_ID_ATTR @"l_m_id"
#define USER_LOCATION_SHOW_PHONE_ATTR @"l_sp"
#define USER_LOCATION_SHOW_EMAIL_ATTR @"l_se"
#define USER_LOCATION_TTTM_ATTR @"l_tttm"
#define USER_LOCATION_SALES_TAX_RATE_ATTR @"l_str"
#define USER_LOCATION_CONV_FEE @"l_cf"

// XML


@implementation fcLocation
@synthesize LocationID=_LocationID;
@synthesize LocationDescription=_LocationDescription;
@synthesize LocationAddress=_LocationAddress;
@synthesize LocationHours=_LocationHours;
@synthesize LocationGPSLat=_LocationGPSLat;
@synthesize LocationGPSLon=_LocationGPSLon;
@synthesize LocationEmail=_LocationEmail;
@synthesize LocationTZ=_LocationTZ;
@synthesize LocationPhone=_LocationPhone;
@synthesize LocationOpenMode=_LocationOpenMode;
@synthesize LocationLongDescr=_LocationLongDescr;
@synthesize LocationMenuID=_LocationMenuID;
@synthesize LocationIsActive=_LocationIsActive;
@synthesize LocationInstructions=_LocationInstructions;
@synthesize allowedArrivalModes=_allowedArrivalModes;
@synthesize allowedPaymentMethods=_allowedPaymentMethods;
@synthesize showEmail=_showEmail;
@synthesize showPhone=_showPhone;
@synthesize LocationTalkToTheManagerNumber=_LocationTalkToTheManagerNumber;
@synthesize LocationSalesTaxRate=_LocationSalesTaxRate;
@synthesize LocationConvenienceFee=_LocationConvenienceFee;

- (id)init
{
    self = [super init];
    if (self)
    {
        _allowedArrivalModes  = [[NSMutableArray alloc]init];
        _allowedPaymentMethods = [[NSMutableArray alloc]init];
        _showPhone=TRUE; // Only sent by server if false
        _showEmail=TRUE; // Only sent by server if false;
        _LocationSalesTaxRate = [NSDecimalNumber decimalNumberWithString:@"0.00"];
        _LocationConvenienceFee = [NSDecimalNumber decimalNumberWithString:@"0.00"];
    }
    return self;
    
}

-(BOOL) isLocationNone
{
    return [fcLocation isLocationIDNone:self.LocationID];
}

-(BOOL) isLongDescrPopulated
{
    if(self.LocationLongDescr==nil)
    {
        return FALSE;
    }
    if([self.LocationLongDescr length]==0)
    {
        return FALSE;
    }
    return TRUE;
}

+(BOOL) isLocationIDNone:(NSNumber*)locationID
{
    if(locationID==nil)
    {
        return TRUE;
    }
    return [locationID isEqualToNumber:[NSNumber numberWithInt:LOCATION_NONE_ID]];
}

+(fcLocation*)parseFromXML:(NSDictionary *)attributeDict
{
    fcLocation *location = [[fcLocation alloc]init];

    for(NSString *aKey in [ attributeDict allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attributeDict valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"parseUserItem: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:ID_ATTR])
        {
            [location setLocationID: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:USER_LOCATION_PHONE_ATTR])
        {
            [location setLocationPhone:attrValue];
        }
        else if([aKey isEqualToString:USER_LOCATION_EMAIL_ATTR])
        {
            [location setLocationEmail:attrValue];
        }
        else if([aKey isEqualToString:USER_LOCATION_DESCRIPTION_ATTR])
        {
            [location setLocationDescription:attrValue];
        }
        else if([aKey isEqualToString:USER_LOCATION_ADDRESS_ATTR])
        {
            [location setLocationAddress:attrValue];
        }
        else if([aKey isEqualToString:USER_LOCATION_OPEN_MODE_ATTR])
        {
            [location setLocationOpenMode:[attrValue integerValue]];
        }
        else if([aKey isEqualToString:USER_LOCATION_HOURS_ATTR])
        {
            [location setLocationHours:attrValue];
        }
        else if([aKey isEqualToString:USER_LOCATION_GPS_LAT_ATTR])
        {
            [location setLocationGPSLat:attrValue];
        }
        else if([aKey isEqualToString:USER_LOCATION_GPS_LON_ATTR])
        {
            [location setLocationGPSLon:attrValue];
        }
        else if([aKey isEqualToString:USER_LOCATION_TZ_ATTR])
        {
            [location setLocationTZ:attrValue];
        }
        else if([aKey isEqualToString:USER_LOCATION_INSTRUCTIONS_ATTR])
        {
            [location setLocationInstructions:attrValue];
        }
        else if([aKey isEqualToString:USER_LOCATION_LONG_DESCR_ATTR])
        {
            [location setLocationLongDescr:attrValue];
        }
        else if([aKey isEqualToString:USER_LOCATION_MENU_ID_ATTR])
        {
            [location setLocationMenuID:[NSNumber numberWithInteger:[attrValue integerValue]]];
        }
        else if([aKey isEqualToString:INCARNATION_ATTR])
        {
            [location setIncarnation:[NSNumber numberWithInteger:[attrValue integerValue]]];
        }
        else if([aKey isEqualToString:IS_ACTIVE_ATTR])
        {
            [location setLocationIsActive:[XMLHelper parseBoolFromString:attrValue]];
        }
        else if([aKey isEqualToString:USER_LOCATION_SHOW_PHONE_ATTR])
        {
            [location setShowPhone:[XMLHelper parseBoolFromString:attrValue]];
        }
        else if([aKey isEqualToString:USER_LOCATION_SHOW_EMAIL_ATTR])
        {
            [location setShowEmail:[XMLHelper parseBoolFromString:attrValue]];
        }
        else if([aKey isEqualToString:USER_LOCATION_TTTM_ATTR])
        {
            [location setLocationTalkToTheManagerNumber:attrValue];
        }
        else if([aKey isEqualToString:USER_LOCATION_SALES_TAX_RATE_ATTR])
        {
            NSDecimalNumber *TaxRate = [NSDecimalNumber decimalNumberWithString:attrValue];
            [location setLocationSalesTaxRate:TaxRate];
        }
        else if([aKey isEqualToString:USER_LOCATION_CONV_FEE])
        {
            NSDecimalNumber *ConvFee = [NSDecimalNumber decimalNumberWithString:attrValue];
            [location setLocationConvenienceFee:ConvFee];
        }
        
    }
    
    return location;
}

-(BOOL) IsOrderTaxable
{
    return TRUE;
}
-(NSDecimalNumber*) CalculateConvenienceFeeForPayMethod:(NSNumber*)payMethod
{
    if([payMethod isEqualToNumber:[NSNumber numberWithInteger:LOCATION_PAY_METHOD_IN_STORE]])
    {
        return [NSDecimalNumber zero];
    }
    else
    {
        return self.LocationConvenienceFee;
    }
}
-(NSDecimalNumber*) CalculateSalesTax:(NSDecimalNumber*) amount
{
    /*
    if( [self IsOrderTaxable:arrive_mode]!=true)
    {
        return [NSDecimalNumber zero];
    }
     */
    if([self.LocationSalesTaxRate compare:[NSDecimalNumber zero]]==NSOrderedSame)
    {
        return [NSDecimalNumber zero];
    }
    
    NSDecimalNumber *tax = [amount decimalNumberByMultiplyingBy:self.LocationSalesTaxRate];
    
    FC_Log(@"Initial Tax: %@",tax);
    
    //$tax = bcmul($amount,$this->LocationSalesTaxRate,10);
    
    // NICKA - cloning the exact logic on server so we hopefully get same results, even though I think with "round up and scale:2, this is a 1-liner!
    NSDecimalNumberHandler *trunc_three_handler =
    [NSDecimalNumberHandler decimalNumberHandlerWithRoundingMode:NSRoundDown scale:3 raiseOnExactness:NO raiseOnOverflow:NO raiseOnUnderflow:NO raiseOnDivideByZero:NO];
    
    NSDecimalNumberHandler *trunc_two_handler =
    [NSDecimalNumberHandler decimalNumberHandlerWithRoundingMode:NSRoundDown scale:2 raiseOnExactness:NO raiseOnOverflow:NO raiseOnUnderflow:NO raiseOnDivideByZero:NO];
    
    NSDecimalNumber *tax_trunc_three = [tax decimalNumberByRoundingAccordingToBehavior:trunc_three_handler];
    NSDecimalNumber *tax_trunc_two = [tax decimalNumberByRoundingAccordingToBehavior:trunc_two_handler];

    NSDecimalNumber *compare_amount = [tax_trunc_three decimalNumberBySubtracting:tax_trunc_two];
    
    FC_Log(@"Calc Tax: Trunc3: %@",tax_trunc_three);
    FC_Log(@"Calc Tax: Trunc2: %@",tax_trunc_two);
    FC_Log(@"Calc Tax: Compare Amount:%@",compare_amount);
    
    if([compare_amount compare:[NSDecimalNumber zero]] == NSOrderedDescending)
    {
        //there was something in that third decimal, so roung "up"
        tax = [tax decimalNumberByAdding:[ NSDecimalNumber decimalNumberWithString:@"0.01"] withBehavior:trunc_two_handler];
    }
    else
    {
        tax = [tax decimalNumberByAdding:[ NSDecimalNumber decimalNumberWithString:@"0.00"] withBehavior:trunc_two_handler];
    }
    
    return tax;
    
}

- (NSString*) makeLocationMapsURL
{
    //http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
    
    NSString *result = [NSString stringWithFormat:@"http://maps.google.com/maps?daddr=%@,%@",
                        self.LocationGPSLat,
                        self.LocationGPSLon];
    return result;
    
}


- (NSString*) makeDetailText
{
    NSMutableString *result = [NSMutableString stringWithFormat:@"%@\n\nAddress:%@\n\nHours:\n%@\n\n",
                    self.LocationDescription,
                    self.LocationAddress,
                               self.LocationHours];
    
    if(self.showPhone==TRUE)
    {
        [result appendFormat:@"Phone:%@\n",self.LocationPhone];
    }
    
    if(self.showEmail==TRUE)
    {
        [result appendFormat:@"Email:%@\n",self.LocationEmail];
    }
    
    
    NSString *arrivalModes = self.makeArrivalMethodStringForAllMethods;
    if( (arrivalModes!=nil) && [arrivalModes length]>0)
    {
        [result appendFormat:@"\nArrive By: %@\n\n",arrivalModes];
    }
    
    NSString *paymentMethods = self.makePaymentMethodStringForAllMethods ;
    
    if( (paymentMethods!=nil) && [paymentMethods length] >0)
    {
        [result appendFormat:@"Payment Methods:\n%@",paymentMethods];
    }
    
                        
    return result;
}

-(void) addPaymentMethod:(fcLocationAllowedPaymentMethod*)method
{
    [self.allowedPaymentMethods addObject:method];
}

-(NSUInteger) getCountOfPaymentMethods
{
    return [self.allowedPaymentMethods count];
}

-(void) clearAllPaymentMethods
{
    [self.allowedPaymentMethods removeAllObjects];
}
-(BOOL) isPaymentMethodAllowed:(NSNumber*)incomingMethod
{
    for(int index=0;index<[self.allowedPaymentMethods count];index++)
    {
        fcLocationAllowedPaymentMethod *method = [self.allowedPaymentMethods objectAtIndex:index];
        if([method doesMatchMode:incomingMethod])
        {
            return TRUE;
        }
    }
    return FALSE;
}

-(NSString*) makePaymentMethodStringForAllMethods
{
    BOOL firstTime=TRUE;
    NSMutableString *result=[[NSMutableString alloc]init];
    
    for(int index=0;index<[self.allowedPaymentMethods count];index++)
    {
        fcLocationAllowedPaymentMethod *method = [self.allowedPaymentMethods objectAtIndex:index];
        if(firstTime!=TRUE)
        {
            [result appendFormat:@", "];
        }
        firstTime=FALSE;
        [result appendFormat:@"%@",[method paymentMethodText]];
    }
    return result;
}


-(void) addArrivalMethod:(fcLocationAllowedArrivalMethod*)incomingMethod
{
    [self.allowedArrivalModes addObject:incomingMethod];
}
-(void) clearAllArrivalMethods
{
    [self.allowedArrivalModes removeAllObjects];
}
-(BOOL) isArrivalMethodAllowed:(NSNumber*)incomingMethod
{
    for(int index=0;index<[self.allowedArrivalModes count];index++)
    {
        fcLocationAllowedArrivalMethod *method = [self.allowedArrivalModes objectAtIndex:index];
        if([method doesMatchMode:incomingMethod])
        {
            return TRUE;
        }
    }
    return FALSE;
}
-(NSString*) makeArrivalMethodStringForAllMethods
{
    BOOL firstTime=TRUE;
    NSMutableString *result=[[NSMutableString alloc]init];
    
    for(int index=0;index<[self.allowedArrivalModes count];index++)
    {
        fcLocationAllowedArrivalMethod *method = [self.allowedArrivalModes objectAtIndex:index];
        if(firstTime!=TRUE)
        {
            [result appendFormat:@", "];
        }
        firstTime=FALSE;
        [result appendFormat:@"%@",[method modeName]];
    }
    return result;

}

-(NSUInteger) getCountOfArrivalMethods
{
    return [self.allowedArrivalModes count];
}

- (BOOL) IsTalkToManagerSet
{
    if(self.LocationTalkToTheManagerNumber==nil)
    {
        return false;
    }
    if([self.LocationTalkToTheManagerNumber length]==0)
    {
        return false;
    }

    return true;
}
@end
