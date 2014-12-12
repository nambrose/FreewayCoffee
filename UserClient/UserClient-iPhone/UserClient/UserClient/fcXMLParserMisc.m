//
//  fcXMLParserMisc.m
//  UserClient
//
//  Created by Nick Ambrose on 2/10/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import "fcXMLParserMisc.h"
#import "Constants.h"
#import "XMLConstants.h"
#import "XMLHelper.h"
#import "fcError.h"

@implementation fcXMLParserMisc

@synthesize requestType=_requestType;
@synthesize error=_error;
@synthesize signonError=_signonError;
@synthesize responseOK=_responseOK ;
@synthesize deletedCreditCardID=_deletedCreditCardID;

- (id) initWithRequestType:(XMLParserMiscRequestType)requestType
{
    self = [super init];
    if (self)
    {
        _requestType = requestType;
        _error=nil;
        _signonError    =FALSE;
        _responseOK=FALSE;
        _deletedCreditCardID = nil;
        
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
    else if([elementName isEqualToString:UPDATE_PAYMENT_METHOD_COMMAND_RESPONSE_TAG])
    {
        NSString *attrValue = [NSString stringWithString:[attributeDict valueForKey:RESULT_ATTR] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        self.responseOK = [XMLHelper parseResultString:attrValue];
    }
    else if([elementName isEqualToString:DELETE_CREDIT_CARD_CMD_AND_RESP])
    {
        NSString *attrValue = [NSString stringWithString:[attributeDict valueForKey:RESULT_ATTR] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        self.responseOK = [XMLHelper parseResultString:attrValue];
        
        attrValue =[NSString stringWithString:[attributeDict valueForKey:CREDIT_CARD_ID_CMD_PARAM]];
        if( (attrValue!=nil) && ([attrValue length]>0))
        {
            attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
            self.deletedCreditCardID = [NSNumber numberWithInteger:[attrValue integerValue]];
        }
        
    }
    
    else if ([elementName isEqualToString:ERROR_TAG])
    {
        
        self.responseOK=FALSE;
        self.error = [fcError parseFromAttributes:attributeDict];
    }
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
    
}

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
    
}




@end
