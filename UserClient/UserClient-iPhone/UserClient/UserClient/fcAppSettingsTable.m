//
//  fcAppSettingsTable.m
//  UserClient
//
//  Created by Nick Ambrose on 9/29/12.
//  Copyright (c) 2012 Freeway Coffee. All rights reserved.
//

#import "fcAppSettingsTable.h"
#import "fcAppSetting.h"
#import "XMLHelper.h"

@implementation fcAppSettingsTable
@synthesize settingsTable=_settingsTable;

-(id)init
{
    self = [super init];
    if (self)
    {
        _settingsTable  = [[NSMutableDictionary alloc]init];
    }
    return self;
}
- (void) clear
{
    [self.settingsTable removeAllObjects];
}
- (void) addAppSetting:(fcAppSetting*)setting
{
    [self.settingsTable setValue:setting forKey:setting.appSettingName];
}
- (BOOL) tryGetSettingAsBOOL:(NSString*)settingName
{
    fcAppSetting *value = [self.settingsTable valueForKey:settingName];
    if(nil==value)
    {
        return FALSE;
    }
    return [XMLHelper parseBoolFromString:value.appSettingValue];
}
- (NSString*) tryGetsettingAsString:(NSString*)settingName
{
 
    fcAppSetting *value = [self.settingsTable valueForKey:settingName];
    if(nil==value)
    {
        return nil;
    }
    return [value appSettingValue];
}

@end
