//
//  fcXMLParserLocations.m
//  UserClient
//
//  Created by Nick Ambrose on 1/17/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import "fcXMLParserLocations.h"
#import "fcLocation.h"
#import "fcLocationTable.h"
#import "Constants.h"
#import "XMLConstants.h"
#import "XMLHelper.h"
#import "fcAppDelegate.h"
#import "fcError.h"
#import "fcLocationAllowedPaymentMethod.h"
#import "fcLocationAllowedArrivalMethod.h"
#import "fcLastOrder.h"
#import "fcXMLParserItemsList.h"

@implementation fcXMLParserLocations

@synthesize locations=_locations;
@synthesize signonError=_signonError;
@synthesize parseSuccessful=_parseSuccessful;
@synthesize parseType=_parseType;
@synthesize error = _error;
@synthesize responseSuccess=_responseSuccess;
@synthesize updatedLocationID = _updatedLocationID;
@synthesize currentLocation=_currentLocation;
@synthesize lastOrder=_lastOrder;

- (id) init
{
    self = [super init];
    if (self)
    {
        _signonError=FALSE;
        _parseSuccessful=TRUE;
        _locations = [[fcLocationTable alloc]init];
        _parseType = XML_PARSER_LOCATIONS_PARSE_TYPE_UNKNOWN;
        _responseSuccess=FALSE;
        _updatedLocationID= [NSNumber numberWithInt:LOCATION_NONE_ID];
        _currentLocation=nil;
        _lastOrder=nil;
    }
    
    return self;
}


- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    if([elementName isEqualToString:SIGNON_RESPONSE_TAG])
    {
        NSString *attrValue = [NSString stringWithString:[attributeDict valueForKey:RESULT_ATTR] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        if([attrValue isEqualToString:OK_ATTR_VALUE]!=TRUE)
        {
            self.signonError=TRUE;
        }
    }
        
    else if([elementName isEqualToString:USER_LOCATION_TAG])
    {
        self.currentLocation = [fcLocation parseFromXML:attributeDict];
    }
    else if([elementName isEqualToString:LOCATION_ARRIVE_MODE_TAG])
    {
        fcLocationAllowedArrivalMethod *method = [fcLocationAllowedArrivalMethod parseFromXMLAttributes:attributeDict];
        if(method!=nil && self.currentLocation!=nil)
        {
            [self.currentLocation addArrivalMethod:method];
        }
    }
    else if([elementName isEqualToString:LOCATION_PAY_METHOD_TAG])
    {
        fcLocationAllowedPaymentMethod *method = [fcLocationAllowedPaymentMethod parseFromXMLAttributes:attributeDict];
        if(method!=nil && self.currentLocation!=nil)
        {
            [self.currentLocation addPaymentMethod:method];
        }
    }
    else if([elementName isEqualToString:UPDATED_USER_LOCATION_TAG])
    {
        NSString *attrValue = [NSString stringWithString:[attributeDict valueForKey:RESULT_ATTR] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        self.responseSuccess = [XMLHelper parseResultString:attrValue];
        if(self.responseSuccess==TRUE)
        {
            attrValue = [NSString stringWithString:[attributeDict valueForKey:LOCATION_ID_ATTR]];
            attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
            self.updatedLocationID=[NSNumber numberWithInteger:[attrValue integerValue]];
        }
    
    }
    else if ([elementName isEqualToString:ERROR_TAG])
    {
        self.parseSuccessful=FALSE;
        self.error = [fcError parseFromAttributes:attributeDict];
    }
    /////// WARNING, LAST ORDER STUFF IS ALSO PASTED INTO LOCATIONS .... Ucky I KNOW BUT OH WELL. MAKE SURE TO CHANGE BOTH .....
    
    else if ([elementName isEqualToString:OM_ORDER_ITEM_TAG])
    {
        fcLastOrderItem *item = [fcXMLParserItemsList parseLastOrderItem:attributeDict];
        if(nil!=item)
        {
            [self.lastOrder addLastOrderItem:item];
        }
    }
    else if ([elementName isEqualToString:ORDER_CREDIT_CARD_TAG])
    {
        self.lastOrder.orderCreditCard = [XMLHelper convertStringsFromWeb:attributeDict];
    }
    else if ([elementName isEqualToString:ORDER_LOCATION_TAG])
    {
        self.lastOrder.lastOrderLocation = [XMLHelper convertStringsFromWeb:attributeDict];
    }
    else if ([elementName isEqualToString:ORDER_TAG])
    {
        self.lastOrder.lastOrder = [XMLHelper convertStringsFromWeb:attributeDict];
    }

}


- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI
 qualifiedName:(NSString *)qName
{
    if([elementName isEqualToString:USER_LOCATION_TAG])
    {
        if(self.currentLocation!=nil)
        {
            [self.locations setLocation:self.currentLocation];
            self.currentLocation=nil;
        }
    }
    
}

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
    
}

@end
