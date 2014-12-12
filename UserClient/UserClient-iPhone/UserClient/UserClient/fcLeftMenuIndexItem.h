//
//  fcLeftMenuIndexItem.h
//  UserClient
//
//  Created by Nick Ambrose on 2/17/13.
//  Copyright (c) 2013 Freeway Coffee. All rights reserved.
//

#import <Foundation/Foundation.h>

#define LEFT_MENU_TYPE_ABOUT 0
#define LEFT_MENU_TYPE_FEEDBACK 1
#define LEFT_MENU_TYPE_VIEW_LAST_ORDER 2
#define LEFT_MENU_TYPE_SIGN_OUT 3

#define LEFT_MENU_TYPE_ABOUT_TEXT @"About"
#define LEFT_MENU_TYPE_FEEDBACK_TEXT @"Feedback"
#define LEFT_MENU_TYPE_VIEW_LAST_ORDER_TEXT @"Last Order"
#define LEFT_MENU_TYPE_SIGN_OUT_TEXT @"Signout"

@interface fcLeftMenuIndexItem : NSObject

@property (nonatomic,assign) NSUInteger ItemType;
@property (nonatomic,strong) NSString *ItemText;

@end
