//
//  fcError.h
//  UserClient
//
//  Created by Nick Ambrose on 1/19/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

#define ERROR_TAG @"error"
#define ERROR_CODE_MAJOR @"error_code_major"
#define ERROR_CODE_MINOR @"error_code_minor"
#define ERROR_DISPLAY_TEXT @"error_display_text"
#define ERROR_LONG_TEXT @"error_long_text"

@interface fcError : NSObject

@property (nonatomic,strong) NSNumber *errorMajorCode;
@property (nonatomic,strong) NSNumber *errorMinorCode;

@property (nonatomic,strong) NSString *errorDisplayText;
@property (nonatomic,strong) NSString *errorLongText;

+ (fcError*) parseFromAttributes:(NSDictionary*)attributes;

- (NSString*) makeErrorText;

@end
