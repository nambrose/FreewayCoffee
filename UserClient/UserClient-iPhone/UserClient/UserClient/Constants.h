//
//  Constants.h
//  UserClient
//
//  Created by Nick Ambrose on 1/15/12.
//  Copyright 2012 Immerse Images. All rights reserved.
//

#ifndef UserClient_Constants_h
#define UserClient_Constants_h

#import "XMLConstants.h"

#define HOST_NAME @"www.freecoffapp.com"

// DEV
#define BASE_URL @"https://freecoffapp.com/fc_dev2/"

// PROD
//#define BASE_URL @"https://freecoffapp.com/fc/"

#define MAIN_COMPAT_LEVEL 10


// LOGGING
#define FC_DO_LOG
#ifdef FC_DO_LOG
#define FC_Log(fmt, ...) NSLog((@"%s [Line %d] " fmt), __PRETTY_FUNCTION__, __LINE__, ##__VA_ARGS__)
#else
#define FC_Log(...)
#endif
#define FC_ALog(fmt, ...) NSLog((@"%s [Line %d] " fmt), __PRETTY_FUNCTION__, __LINE__, ##__VA_ARGS__)

#define DEFAULT_CORNER_RADIUS 10
// It's 1 free drink, *or* this amount
// This is also set in GLOBAL_APP_SETTINGS from the server (this is last resort)
#define FREE_DRINK_ALT_AMOUNT @"5.00" 

// ERROR CODES
#define ERROR_TAG @"error"
#define ERROR_CODE_MAJOR @"error_code_major"
#define ERROR_CODE_MINOR @"error_code_minor"
#define ERROR_DISPLAY_TEXT @"error_display_text"
#define ERROR_LONG_TEXT @"error_long_text"

#define INTERNAL_ERROR @"Internal Error"

#define HUD_HIDE_DELAY_SHORT 0.1
#define HUD_HIDE_DELAY_DEFAULT 0.5


#define NONE_TEXT @"None"
#define APP_CLIENT_TYPE @"app_client"
#define APP_CLIENT_VALUE_IOS @"apple_ios"

#define IOS_DEVICE_NAME @"ios_device_name"
#define IOS_DEVICE_SYS_NAME @"ios_device_sys_name"
#define IOS_DEVICE_SYS_VER @"ios_device_sys_ver"
#define IOS_DEVICE_MODEL @"iod_device_model"

#define IOS_APP_VER_NAME_STRING @"ios_app_ver_name"
#define IOS_APP_BUILD_STRING @"ios_app_build"

#define SELECT_A_MAKE_TEXT @"Select A Make"
#define FIRST_SELECT_A_MAKE_TEXT @"First, Select A Make"
#define SELECT_A_MODEL_TEXT @"Select A Model"
#define SELECT_A_COLOR_TEXT @"Select A Color"
#define ENTER_A_LICENSE_TEXT @"Enter A License \n                (Optional)"
#define MAKE_CANNOT_HAVE_MODELS @"Make Has No Models"


// USER COMMANDS
#define USER_COMMAND_CMD @"user_command"
#define USER_COMMAND_GET_ITEM_LIST @"get_user_items"
#define USER_COMMAND_UPDATE_TIME_TO_LOCATION @"update_time_to_location"

#define USER_COMMAND_UPDATE_TIME_TO_LOCATION_CMD @"update_time_to_location"
// TOP LEVEL
#define USER_ITEMS_TAG @"user_items"

#define RESULT_ATTR @"result"
#define RESULT_OK @"ok"
// TAGS
#define REGISTER_RESPONSE_TAG @"register_response"
#define SIGNON_RESPONSE_TAG @"signon_response"


// ALERT DIALOGS
#define SIGNUP_FAILED_ALERT_TITLE @"Signup Failed"
#define SIGNUP_FAILED_ALERT_MESSAGE @"You could not be signed up. Please Try Again."

#define SIGNON_FAILED_ALERT_TITLE @"Signon Failed"

#define NETWORK_ERROR_ALERT_TITLE @"Network Error."
#define NETWORK_ERROR_ALERT_MESSAGE @"A Network Error Occurred. Please Try Again."

#define CREDIT_CARD_UPDATE_FAIL_ALERT_TITLE @"Card Update Failed"

#define UPDATE_CAR_DATA_FAILED_ALERT_TITLE @"Update Car Data Failed"
#define UPDATE_CAR_DATA_FAILED_ALERT_MESSAGE @"Your Car Data Could not be updated. Please Try Again"

#define XML_PARSE_ALERT_TITLE @"Messaging Error"
#define XML_PARSE_ALERT_MESSAGE @"An error processing the response occurred"

#define RESPONSE_ERROR_ALERT_TITLE @"Response Error"

#define UNKNOWN_ALERT_TITLE @"Unknown"
#define UNKNOWN_ALERT_MESSAGE @"Unknown"

#define DOWNLOAD_LOCATIONS_ALERT_TITLE @"Download Error"

#define SET_USER_LOCATION_ALERT_TITLE @"Set Location Error"

// CREDIT CARDS
#define ADD_A_CREDIT_CARD @"Add a payment method..."
#define CREDIT_CARD_INPUT_FAILED_ALERT_TITLE @"Validation Failed"
#define CREDIT_CARD_NO_CARD_NUMBER_ALERT_MESSAGE @"Card Number Cannot Be Empty"
#define CREDIT_CARD_INVALID_CARD_NUMBER_ALERT_MESSAGE @"That Card Number Is Not Valid"
#define CREDIT_CARD_NO_EXP_MONTH_ALERT_MESSAGE @"Expiration Month Cannot Be Empty"
#define CREDIT_CARD_EXP_MONTH_LENGTH_ALERT_MESSAGE @"Expiration Month Must Be 1 or 2 Digits"
#define CREDIT_CARD_NO_EXP_YEAR_ALERT_MESSAGE @"Expiration Year Cannot Be Empty"
#define CREDIT_CARD_EXP_YEAR_LENGTH_ALERT_MESSAGE @"Expiration Year Must Be "

#define CREDIT_CARD_NO_BILLING_ZIP_ALERT_MESSAGE @"Billing ZIP Cannot Be Empty"
#define CREDIT_CARD_BILLING_ZIP_LENGTH_ALERT_MESSAGE @"Billing ZIP Must Be "

#define CREDIT_CARD_EXP_YEAR_LENGTH 4
#define CREDIT_CARD_BILLING_ZIP_LENGTH 5

// PAY METHOD
#define PAY_METHOD_ALERT_TITLE @"Pay Method"
#define PAY_METHOD_ALERT_MUST_CHOOSE_ONE_ALERT_MESSAGE @"You must chose a method"


// TIME TO LOCATION
#define UPDATE_TIME_TO_LOCATION_FAILED_ALERT_TITLE @"Update Failed"
#define UPDATE_TIME_TO_LOCATION_FAILED_ALERT_MESSAGE @"Failed to Update your Time To Location, please try again"



// TOASTS /////////

#define SIGNUP_SUCCESS_TOAST_MESSAGE @"Signed up successfully, Signing you in..."
#define SIGNON_SUCCESS_TOAST_MESSAGE @"Signed on successfully"
#define SIGNON_FAILED_TOAST_MESSAGE @"Signon failed. Please Check your usernname and password are entered correctly. Sign up if you do not have an account."


// PREFERENCE KEYS
#define FC_USERNAME_PREF_KEY @"FC_USERNAME_PREF_KEY"
#define FC_NAME_PREF_KEY @"FC_NAME_PREF_KEY"
#define FC_PASSWORD_PREF_KEY @"FC_PASSWORD_PREF_KEY" // Remove to Keychain

// GENERAL /////
#define ERROR_USERNAME_NOT_SET @"Username must be set"
#define ERROR_PASSWORD_NOT_SET @"Password must be set"
#define ERROR_PASSWORD_TOO_SHORT @"Password is Too short. Must be at least %d chars"
#define PASSWORD_MIN_LENGTH 6

#define WELCOME_USER_STRING @"Welcome, "

#define SET_ARRIVAL_MODE_TEXT @"Set your arrival mode..."
// HUD/Progress Messages

#define SET_TIME_TO_ARRIVE @"Set your arrival time..."
#define READY_IN_TEXT @"Arriving in"
#define PROGRESS_SIGNUP_MESSAGE @"Signing you up..."
#define PROGRESS_SIGNIN_MESSAGE @"Signing you in..."
#define PROGRESS_DOWNLOAD_ITEMS_MESSAGE @"Retrieving your items list..."
#define PROGRESS_MAKE_ORDER_MESSAGE @"Making order"
#define PROGRESS_DOWNLOAD_CAR_DATA_MESSAGE @"Downloading Car Make/Model/Color data"
#define PROGRESS_UPDATE_CAR_DATA_MESSAGE @"Updating Car Info..."
#define PROGRESS_UPDATE_CREDIT_CARD_MESSAGE @"Updating Your Card Info..."
#define PROGRESS_UPDATE_TIME_TO_LOCATION_MESSAGE @"Updating your arrival time"
#define PROGRESS_EDIT_ITEM_MESSAGE @"Editing drink"
#define PROGRESS_ADD_ITEM_MESSAGE @"Adding drink"
#define PROGRESS_IVE_ARRIVED_MESSAGE @"I've Arrived"
#define PROGRESS_UPDATE_TIP_MESSAGE @"Updating tips"
#define PROGRESS_DOWNLOAD_LOCATIONS_MESSAGE @"Retrieving Locations..."
#define PROGRESS_SET_USER_LOCATION_MESSAGE @"Setting Location..."
#define PROGRESS_UPDATE_PAY_METHOD_MESSAGE @"Updating Method..."
#define PROGRESS_DELETE_CREDIT_CARD_MESSAGE @"Deleting Card..."
#define CHECK_MENU_HUD_MESSAGE @"Check Menu"

#define CHECK_MENU_MESSAGE @"Check Menu"

// IMAGES

#define FC_LOGO_IMAGE_NAME @"fc_logo.png"
#define IMG_BACKGROUND_IMAGE_NAME @"Default.png"
#define IMG_BACKGROUND_IMAGE_NAME_RET4 @"Default-568h.png"



#define DEFAULT_APP_TIP_INCREMENT @"0.25"
// Table row images

#define TIME_TO_ARRIVE_TABLE_ROW_IMAGE_NAME @"fc_ready_in.png"
#define CREDIT_CARD_TABLE_ROW_IMAGE_NAME @"fc_credit_card.png"
#define DRINK_TABLE_ROW_IMAGE_NAME @"fc_drink.png"
#define USER_TAG_ROW_IMAGE_CAR_NAME @"fc_car.png"
#define USER_TAG_ROW_IMAGE_WALKUP_NAME @"fc_man.png"

#define FOOD_TABLE_ROW_IMAGE_NAME @"fc_food.png"
#define LOCATION_TABLE_ROW_IMAGE_NAME @"fc_location.png"
#define UNKNOWN_TABLE_ROW_IMAGE_NAME @"unknown.png"
#define ADD_EDIT_ITEMS_ROW_IMAGE_NAME @"fc_add.png"
#define ORDER_TOTAL_TABLE_ROW_IMAGE_NAME @"fc_order_total.png"


#define TIP_NONE_IMAGE_NAME @"fc_tip_none.png"
#define TIP_IMAGE_NAME @"fc_tip.png"
// Table row button images

#define ADD_BUTTON_TABLE_ROW_IMAGE_NAME @"fc_add_button_image.png"
#define EDIT_BUTTON_TABLE_ROW_IMAGE_NAME @"fc_edit_button_image.png"
#define REMOVE_BUTTON_TABLE_ROW_IMAGE_NAME @"fc_remove_button_image.png"

#define CREDIT_CARDS_ACCEPTED_IMAGE @"fc_credit_card_images.png"

#define ERROR_USERNAME_MUST_BE_EMAIL @"Username must be an email address"
#define ERROR_NAME_NOT_SET @"Name must be set"
#define ERROR_NAME_NOT_VALID @"Not an acceptable email address"

#define ERROR_PASSWORD_AGAIN_NOT_SET @"Password (again) must be set"
#define ERROR_PASSWORD_NOMATCH @"Passwords do not match"

#endif
