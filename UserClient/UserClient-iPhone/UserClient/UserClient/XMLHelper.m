//
//  XMLHelper.m
//  UserClient
//
//  Created by Nick Ambrose on 1/20/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#import "XMLHelper.h"
#import "fcAppSetting.h"
#import "fcUserTip.h"
#import "XMLConstants.h"

@implementation XMLHelper

- (id)init
{
    self = [super init];
    if (self) {
        // Initialization code here.
    }
    
    return self;
}
+ (NSMutableDictionary *) convertStringsFromWeb:(NSDictionary*)theDict;
{
    NSMutableDictionary *newDict = [theDict mutableCopy];
    for(NSString *aKey in [ newDict allKeys] )
    {
        // do something
        NSString *theText = [newDict valueForKey:aKey] ;
        //FC_Log(@"convertStringsFromWeb Key: %@, Value: %@",aKey,theText);
        [newDict setValue:[XMLHelper stringByDecodingURLFormat:theText] forKey:aKey];
    }
    return newDict;

}
 
+ (NSString *)stringByDecodingURLFormat:(NSString *)InputString
{

    NSString *result = [InputString stringByReplacingOccurrencesOfString:@"+" withString:@" "];
    result = [result stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    return result;
}
+ (BOOL) parseResultString:(NSString*)InputString
{
    if(InputString==nil)
    {
        return FALSE;
    }
    if([InputString isEqualToString:OK_ATTR_VALUE])
    {
        return TRUE;
    }
    return FALSE;
}
    
+ (NSString *) URLEncodedString:(NSString*)stringInput;
{
    /* COnsider this ?
     NSString * encodedString = (NSString *)CFURLCreateStringByAddingPercentEscapes(
     NULL,
     (CFStringRef)unencodedString,
     NULL,
     (CFStringRef)@"!*'();:@&=+$,/?%#[]",
     kCFStringEncodingUTF8 );
     */
    
    if(stringInput==nil)
    {
        return nil;
    }
    NSMutableString * output = [NSMutableString string];
    const unsigned char * source = (const unsigned char *)[stringInput UTF8String];
    int sourceLen = strlen((const char *)source);
    for (int i = 0; i < sourceLen; ++i)
    {
        const unsigned char thisChar = source[i];
        if (thisChar == ' ')
        {
            [output appendString:@"+"];
        }
        else if (thisChar == '.' || thisChar == '-' || thisChar == '_' || thisChar == '~' || 
                       (thisChar >= 'a' && thisChar <= 'z') ||
                       (thisChar >= 'A' && thisChar <= 'Z') ||
                       (thisChar >= '0' && thisChar <= '9')) 
        {
            [output appendFormat:@"%c", thisChar];
        } else
        {
                [output appendFormat:@"%%%02X", thisChar];
        }
    }
    return output;
}

+ (NSInteger) parseStringToInt:(NSString *)InputString
{
    return [InputString integerValue];
}
+ (BOOL) parseBoolFromString:(NSString *)InputString
{
    if( ([InputString isEqualToString:@"1"]) || ([InputString isEqualToString:@"true"]) || ([InputString isEqualToString:@"TRUE"]) )
    {
        return TRUE;
    }
    return FALSE;
}

+ (fcAppSetting*) parseAppSetting:(NSDictionary*)atts
{
    fcAppSetting *setting = [[fcAppSetting alloc]init];
    
    for(NSString *aKey in [ atts allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[atts valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"parseAppSetting: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:APP_SETTING_NAME_ATTR])
        {
            setting.appSettingName=attrValue;
        }
        else if([aKey isEqualToString:APP_SETTING_VALUE_ATTR])
        {
            setting.appSettingValue=attrValue;
        }
    }
    return setting;

}
+ (fcUserTip*) parseUserTip:(NSDictionary*)atts
{
    fcUserTip *tip = [[fcUserTip alloc]init];
    for(NSString *aKey in [ atts allKeys] )
    {
        NSString *attrValue = [NSString stringWithString:[atts valueForKey:aKey] ] ;
        attrValue = [XMLHelper stringByDecodingURLFormat:attrValue];
        
        //FC_Log(@"parseAppSetting: Attr: %@ Value: %@",aKey,attrValue);
        if([aKey isEqualToString:USER_ID_ATTR])
        {
           tip.userID = [NSNumber numberWithInteger:[attrValue integerValue]] ;
        }
        else if([aKey isEqualToString:LOCATION_ID_ATTR])
        {
            tip.locationID = [NSNumber numberWithInteger:[attrValue integerValue]] ;
        }
        else if([aKey isEqualToString:USER_TIP_TYPE_ATTR])
        {
            tip.tipType = [attrValue integerValue] ;
        }
        else if([aKey isEqualToString:USER_TIP_AMOUNT_ATTR])
        {
            tip.tipAmount = attrValue;
        }
        else if([aKey isEqualToString:USER_TIP_ROUND_UP_ATTR])
        {
            tip.roundUp = [XMLHelper parseBoolFromString:attrValue];
        }
        
    }
    return tip;
        
}

    
@end
