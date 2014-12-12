//
//  fcAppSettingsTable.h
//  UserClient
//
//  Created by Nick Ambrose on 9/29/12.
//  Copyright (c) 2012 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

@class fcAppSetting;

@interface fcAppSettingsTable : NSObject

@property (nonatomic,strong) NSMutableDictionary *settingsTable;

- (id)init;
- (void) clear;
- (void) addAppSetting:(fcAppSetting*)setting;
- (BOOL) tryGetSettingAsBOOL:(NSString*)settingName; // Bit ugly. Returns false if setting does not exist, is false or is el garbage
- (NSString*) tryGetsettingAsString:(NSString*)settingName;
@end
