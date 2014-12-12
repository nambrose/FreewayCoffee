//
//  fcError.m
//  UserClient
//
//  Created by Nick Ambrose on 1/19/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import "fcError.h"
#import "XMLHelper.h"
#import "XMLConstants.h"
#import "Constants.h"

@implementation fcError

@synthesize errorMajorCode=_errorMajorCode;
@synthesize errorMinorCode=_errorMinorCode;

@synthesize errorDisplayText=_errorDisplayText;
@synthesize errorLongText=_errorLongText;

-(id)init
{
    self = [super init];
    if (self)
    {
        _errorMajorCode=0;
        _errorMinorCode=0;
        _errorDisplayText=NONE_TEXT;
        _errorLongText=@"";
    }
    return self;
}

#define ERROR_CODE_MAJOR @"error_code_major"
#define ERROR_CODE_MINOR @"error_code_minor"
#define ERROR_DISPLAY_TEXT @"error_display_text"
#define ERROR_LONG_TEXT @"error_long_text"


+ (fcError*) parseFromAttributes:(NSDictionary*)attributes
{
    fcError *error = [[fcError alloc]init];
    
    for(NSString *aKey in [ attributes allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[attributes valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"parseError: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:ERROR_CODE_MAJOR])
        {
            [error setErrorMajorCode: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:ERROR_CODE_MINOR])
        {
            [error setErrorMinorCode: [NSNumber numberWithInteger:[attrValue integerValue]]] ;
        }
        else if([aKey isEqualToString:ERROR_DISPLAY_TEXT])
        {
            [error setErrorDisplayText:attrValue] ;
        }
        else if([aKey isEqualToString:ERROR_LONG_TEXT])
        {
            [error setErrorLongText:attrValue] ;
        }
    }

    return error;
    
}

- (NSString*) makeErrorText
{
    
    NSString *result = [NSString stringWithFormat:@"Error: Code[%@]\n\n%@",
                        [self errorMajorCode ],
                        [self errorLongText ] ];
    return result;

}


@end
