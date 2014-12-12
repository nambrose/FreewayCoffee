//
//  fcLocationAllowedPaymentMethod.m
//  UserClient
//
//  Created by Nick Ambrose on 2/9/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import "Constants.h"
#import "fcLocationAllowedPaymentMethod.h"
#import "XMLHelper.h"
#import "XMLConstants.h"

// XML

#define LOCATION_PAY_METHOD_DESCR_ATTR @"l_p_m_pmd"

// END XML
@implementation fcLocationAllowedPaymentMethod

+(BOOL) isPayInStore:(NSNumber*)method
{
    if([method isEqualToNumber:[NSNumber numberWithInteger:LOCATION_PAY_METHOD_IN_STORE]])
    {
        return true;
    }
    return false;
    
}

+(fcLocationAllowedPaymentMethod*) parseFromXMLAttributes:(NSDictionary*)attributes
{
    fcLocationAllowedPaymentMethod *method = [[fcLocationAllowedPaymentMethod alloc]init];
    
    for(NSString *aKey in [ attributes allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attributes valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        FC_Log(@"fcLocationAllowedPaymentMethod: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:LOCATION_PAY_METHOD_ATTR])
        {
            [method setPaymentMethod:[NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:LOCATION_PAY_METHOD_DESCR_ATTR])
        {
            [method setPaymentMethodText:attrValue];
        }
    }
    return method;
    
}

-(BOOL) doesMatchMode:(NSNumber*)incomingMode
{
    return [self.paymentMethod isEqualToNumber:incomingMode];
}


@end
