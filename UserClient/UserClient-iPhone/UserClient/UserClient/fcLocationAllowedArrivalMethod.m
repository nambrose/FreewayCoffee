//
//  fcLocationAllowedArrivalMethod.m
//  UserClient
//
//  Created by Nick Ambrose on 2/9/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

// XML

#define LOCATION_ARRIVE_MODE_ATTR @"l_a_m_am"
#define LOCATION_ARRIVE_MODE_STRING_ATTR @"l_a_m_sam"

// END XML

#import "fcLocationAllowedArrivalMethod.h"
#import "XMLHelper.h"


@implementation fcLocationAllowedArrivalMethod
@synthesize modeAllowed=_modeAllowed;
@synthesize modeName=_modeName;

+(fcLocationAllowedArrivalMethod*) parseFromXMLAttributes:(NSDictionary*)attributes
{
    fcLocationAllowedArrivalMethod *method = [[fcLocationAllowedArrivalMethod alloc]init];
    
    for(NSString *aKey in [ attributes allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attributes valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"fcLocationAllowedArrivalMethod: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:LOCATION_ARRIVE_MODE_ATTR])
        {
            [method setModeAllowed:[NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:LOCATION_ARRIVE_MODE_STRING_ATTR])
        {
            [method setModeName:attrValue];
        }
    }
    return method;

}

-(BOOL) doesMatchMode:(NSNumber*)incomingMode
{
    return [self.modeAllowed isEqualToNumber:incomingMode];
}

@end
