//
//  XMLHelper.h
//  UserClient
//
//  Created by Nick Ambrose on 1/20/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//
@class fcAppSetting;
@class fcUserTip;

#import <Foundation/Foundation.h>

@interface XMLHelper : NSObject

+ (NSMutableDictionary *) convertStringsFromWeb:(NSDictionary*)theDict;
+ (NSString *) URLEncodedString: (NSString*)stringInput;
+ (NSString *)stringByDecodingURLFormat:(NSString *)InputString;
+ (NSInteger) parseStringToInt:(NSString *)InputString;
+ (BOOL) parseBoolFromString:(NSString *)InputString;
+ (BOOL) parseResultString:(NSString*)InputString; // result=ok/failed only
// General Parsers we dont want to be in Nav Controllers etc!

+ (fcAppSetting*) parseAppSetting:(NSDictionary*)atts;
+ (fcUserTip*) parseUserTip:(NSDictionary*)atts;

@end
