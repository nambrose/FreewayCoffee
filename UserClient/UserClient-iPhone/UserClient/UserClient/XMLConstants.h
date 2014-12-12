#ifndef XML_CONSTANTS_H
#define XML_CONSTANTS_H

// XML ATTRIBUTES

// PAGES
#define SIGNUP_PAGE @"fc_signup.php"
#define SIGNON_PAGE @"fc_signon.php"
#define ORDER_PAGE @"fc_orders.php"
#define SIGNUP_PAGE @"fc_signup.php"
#define SIGNON_PAGE @"fc_signon.php"
#define USER_PAGE @"fc_user.php"
#define DRINK_ADD_EDIT_PAGE @"fc_drink_add_edit.php"
#define CAR_DATA_PAGE @"fc_make_model_color.php"
#define MENU_PAGE @"fc_fe_menu.php"

#define LOCATIONS_PAGE @"fc_fe_location.php"


#define USER_COMMAND @"user_command"

// COMMANDS
#define CAR_PAGE_COMMAND_STRING @"user_command"
#define GET_CAR_DATA_CMD @"get_all_car_make_models"
#define UPDATE_TAG_AND_CAR_CMD @"update_tag_and_car"
#define UPDATE_CREDIT_CARD_CMD @"update_credit_card"
#define UPDATE_CREDIT_CARD_USE_FOR_PAY_CMD @"update_credit_card_use_pay"

#define GET_USER_ITEMS_CMD @"get_user_items"
#define GET_MENU_ITEMS_CMD @"get_drink_picker"
#define DELETE_ITEM_COMMAND @"delete_drink"
#define MAKE_ORDER_COMMAND @"make_order"
#define GET_ALL_LOCATIONS_CMD @"get_all_locations"
#define SET_USER_LOCATION_CMD @"set_user_location"
#define GET_MENU_CMD @"get_menu"
#define USER_ID_ATTR @"user_id"
#define LOCATION_ID_ATTR @"location_id"
#define UPDATE_PAYMENT_METHOD_COMMAND @"update_pay_method"

#define UPDATE_PAYMENT_METHOD_COMMAND_RESPONSE_TAG @"update_pay_method_resp"

// Always gets
#define GET_MENU_CMD @"get_menu"
// If menu version is included, will only get if out of date
#define GET_MENU_IF_NEED_CMD @"get_menu_if_needed"

// Always gets
#define GET_MENU_FOR_USER_CMD @"get_menu_for_user"
// If menu version is included, will only get if out of date
#define GET_MENU_FOR_USER_IF_NEED_CMD @"get_menu_for_user_if_needed"

#define USER_ARRIVE_MODE_CMD_ARG @"user_arrive_mode"
#define MENU_ID_CMD_ARG @"menu_id"
#define MENU_HAVE_VERSION_CMD_ARG @"menu_have_version"

// Inherent type (Drink, Food, Candy etc.)
#define ITEM_TYPE_TYPE_ID_CMD_PARAM @"d_t_it" 

// MENU

// Object Responses

// No object can have this version
#define OBJECT_NONE_VERSION 0
#define OBJECT_NONE_ID 0

#define OBJECT_RESPONSE_ATTR @"object_response"

// The last version number (used with object need new but not included)
#define OBJECT_RESPONSE_LATEST_VER_ATTR @"object_latest_ver"
// You need a new one but its not included
#define OBJECT_RESPONSE_NEED_NEW @"object_need_new"
// You need a new one and its included
#define OBJECT_RESPONSE_OBJ_INCLUDED @"object_included"
// Object does not exist
#define OBJECT_RESPONSE_NOT_EXIST @"object_not_exist"
// You already have the latest
#define OBJECT_RESPONSE_HAVE_LATEST @"object_have_latest"


// Delete drink
#define ITEM_ID @"drink_id"

// COMMAND RESPONSE
#define CREDIT_CARD_UPDATE_RESPONSE_TAG @"update_credit_card_response"

// COMMAND PARAMS


// ITEM ADD EDIT
#define ITEM_ADD_EDIT_COMMAND_ADD @"user_add_drink"
#define ITEM_ADD_EDIT_COMMAND_EDIT @"user_edit_drink"

// EDIT ITEM
#define USER_ITEM_ITEM_ID @"user_drink_id"

// DELETE
#define ITEM_DELETED_RESPONSE_TAG @"drink_delete_response"

#define USER_ITEM_TAG @"u_d"
#define USER_ITEM_OPTION_TAG @"udo"

// USER ITEM ATTRS
#define USER_ITEM_OPTIONS_TEXT_ATTR @"u_d_o"
#define USER_ITEM_LONG_DESCR_ATTR @"u_d_ld"
#define USER_ITEM_TYPE_ID @"u_d_dt"
#define USER_ITEM_NAME_ATTR @"u_d_dn"
#define USER_ITEM_TYPE_NAME_ATTR @"u_d_dtn"
#define USER_ITEM_INCLUDE_DEFAULT_ATTR @"u_d_id"
#define USER_ITEM_COST_ATTR @"u_d_c"

#define USER_ITEM_EXTRA_OPTIONS_ATTR @"u_d_e"

// USER DRINK OPTIONS
#define USER_ITEM_OPTION_USER_DRINK_ID_ATTR @"udo_udi"
#define USER_ITEM_OPTION_ITEM_TYPES_OPTION_ID_ATTR @"udo_dtoi"
#define USER_ITEM_OPTION_ITEM_OPTION_ID_ATTR @"udo_doi"
#define USER_ITEM_OPTION_ITEM_OPTION_GROUP_ID_ATTR @"udo_dogi"
#define USER_ITEM_OPTION_COUNT_ATTR @"udo_c"

// s_ I guess now indicates going up to server so we dont get mixed up with down ? UGly
#define S_USER_ITEM_OPTIONS @"s_udos"

#define IS_ACTIVE_ATTR @"is_act"

// ADD EDIT ITEM SCHEMA
#define ITEM_ADD_EDIT_SCHEMA_STRING @"ae_schema"
#define ITEM_ADD_EDIT_SCHEMA @"1"

// CAR COMMAND PARAMS
#define USER_CAR_MAKE_ID @"user_car_make_id"
#define USER_CAR_MODEL_ID @"user_car_model_id"
#define USER_CAR_COLOR_ID @"user_car_color_id"
#define USER_TAG @"user_tag"

// CREDIT CARD COMMANDS

// Delete CREDIT CARD
#define DELETE_CREDIT_CARD_CMD_AND_RESP @"d_c_c"
#define CREDIT_CARD_ID_CMD_PARAM @"d_c_c_id"


// CREDIT CARD COMMAND PARAMS
#define CREDIT_CARD_NUMBER_CMD_PARAM @"credit_card_number"
#define CREDIT_CARD_EXP_MONTH_CMD_PARAM @"credit_card_exp_month"
#define CREDIT_CARD_EXP_YEAR_CMD_PARAM @"credit_card_exp_year"
#define CREDIT_CARD_ZIP_CMD_PARAM @"credit_card_zip"

// GENERAL ATTRIBUTES
#define MAIN_SCHEMA_COMPAT_RELEASE_NEEDED_ATTR @"compat_release_needed"
#define MAIN_SCHEMA_COMPAT_LEVEL_ATTR @"compat_level"

#define ID_ATTR @"id"
#define ORDER_ID_ATTR  @"order_id"
#define ORDER_ID @"order_id"
#define RESULT_ATTR @"result"
#define SORT_ORDER_ATTR @"sort_order"
#define SORT_ORDER_SHORT_ATTR @"so"
#define OK_ATTR_VALUE @"ok"

// SIGNON or UP
#define SIGNON_RESPONSE_FAIL @"signon_failed"
#define SUCCESS_REGISTER_SIGNIN @"success_register_signin"
#define SIGNON_OK @"signon_ok"

// ITEMS LIST

#define ORDER_NOW_TEXT @"Order Now"

#define USER_CREDIT_CARDS_TAG @"user_credit_card"
#define USER_FOODS_TAG @"user_foods"
#define USER_FOOD_TAG @"user_food"
#define USER_DRINKS_TAG @"user_drinks"
#define USER_DRINK_TAG @"user_drink"

#define DRINK_TYPES_TAG @"drink_types"
#define DRINK_TYPE_TAG @"drink_type"

#define USER_ADD_ITEM_TAG @"user_add_drink"
#define DELETED_ITEM_ID_ATTR @"deleted_drink_id"
#define USER_EDIT_ITEM_TAG @"user_edit_drink"

// DRINKS
#define DRINK_TYPE_NAME_ATTR @"user_drink_drink_type_name"
#define DRINK_NAME_ATTR @"user_drink_drink_name"
#define DRINK_LONG_DESCR_ATTR @"user_drink_long_descr"
#define DRINK_SYRUP_STRINGS_ATTR @"user_drink_syrup_strings"
#define SYRUPS_STRING @"Syrups"
#define DRINK_COST_ATTR @"user_drink_cost"

#define USER_INCARNATION_CMD_ARG @"u_inc"
#define USER_LOCATION_INCARNATION_CMD_ARG @"u_loc_inc"

// LOCATION
#define USER_LOCATION_VALUE @"None"
#define LOCATION_NONE_TEXT @"Choose a location..."

// Choose/Add an Item


#define CHOOSE_AN_ITEM_TEXT @"Add/Edit items"

// USER TAG
#define USER_CAR_DESCR_LONG_ATTR @"user_car_info_long_string"





////// GLOBAL APP SETTINGS
#define APP_SETTING_LIST_TAG @"app_setting_list"
#define APP_SETTING_TAG @"app_setting"
#define APP_SETTING_NAME_ATTR @"app_set_name"
#define APP_SETTING_VALUE_ATTR @"app_set_val"

// APP SETTING VALUES
#define APP_SETTING_NAME_ZERO_TIP_SHOW_RED_ICON @"zero_tip_show_red_icon"
#define APP_SETTING_NAME_SHOW_TIP_TEXT_ITEMS_LIST @"show_tip_text_items_list"
#define APP_SETTING_NAME_SHOW_TIPS_BY_PERCENTAGE @"show_tips_by_precentage"
#define APP_SETTING_TIPS_INCREMENT_FACTOR @"tips_increment_factor"
#define APP_SETTING_DEFAULT_FREE_DRINK_DISCOUNT_AMT @"default_free_drink_discount_amt"

// TIPS
// TIPS COMMANDS
#define SET_USER_TIP_CMD @"set_user_tip"
#define DELETE_USER_TIP_CMD @"delete_user_tip"

// TIPS ATTRS/TAGS
#define USER_TIP_TYPE_NONE 0
#define USER_TIP_TYPE_AMOUNT 1
#define USER_TIP_TYPE_PERCENT 2

#define USER_TIP_LIST_TAG @"user_tip_list"
#define USER_TIP_TAG @"user_tip"
#define USER_TIP_TYPE_ATTR @"tip_type"
#define USER_TIP_AMOUNT_ATTR @"tip_amt"
#define USER_TIP_ROUND_UP_ATTR @"tip_rnd"

// TIME TO LOCATION

#define UPDATE_TIME_TO_LOCATION_TAG @"updated_time_to_location_result"

// CREDIT CARD

#define USER_CREDIT_CARD_DESCR_ATTR @"user_credit_card_descr"
#define USER_CREDIT_CARD_LAST_FOUR_ATTR @"user_credit_card_last4"

// USER CAR DATA
#define USER_CAR_DATA_TAG @"user_car_data"

#define USER_CAR_MAKE_ID_ATTR @"car_make_id"
#define USER_CAR_MAKE_LONG_DESCR_ATTR @"car_make_long_descr"
#define USER_CAR_MAKE_SHORT_DESCR_ATTR @"car_make_short_descr"
#define USER_CAR_MAKE_HAS_MODELS_ATTR @"car_make_has_models"
#define USER_CAR_MODEL_ID_ATTR @"car_model_id"
#define USER_CAR_MODEL_LONG_DESCR_ATTR @"car_model_long_descr"
#define USER_CAR_MODEL_SHORT_DESCR_ATTR @"car_model_short_descr"
#define USER_CAR_COLOR_ID_ATTR @"car_color_id"
#define USER_CAR_COLOR_LONG_DESCR_ATTR @"car_color_long_descr"
#define USER_CAR_COLOR_SHORT_DESCR_ATTR @"car_color_short_descr"
#define USER_CAR_DESCR_LONG_ATTR @"user_car_info_long_string"


// END USER CAR DATA

// For car list
#define CAR_MAKE_TEXT @"Make"
#define CAR_MODEL_TEXT @"Model"
#define CAR_COLOR_TEXT @"Color"
#define CAR_LICENSE_TEXT @"License"

// Car Color
#define CAR_COLOR_TAG @"car_color"
#define CAR_COLOR_ID_ATTR @"id"
#define CAR_COLOR_LONG_DESCR_ATTR @"c_co_l"
#define CAR_COLOR_SHORT_DESCR_ATTR @"c_co_s"

#define ALL_CAR_MAKES_AND_MODELS_TAG @"all_car_makes_and_models"

#define CAR_MAKE_TAG @"c_ma"
#define CAR_MAKE_ID_ATTR @"id"
#define CAR_MAKE_LONG_DESCR_ATTR @"c_ma_l"
#define CAR_MAKE_SHORT_DESCR_ATTR @"c_ma_s"
#define CAR_MAKE_CAN_HAVE_MODELS @"c_ma_h"

#define CAR_MODEL_TAG @"c_mo"
#define CAR_MODEL_ID_ATTR @"id"
#define CAR_MODEL_MAKE_ID_ATTR @"c_mo_ma"
#define CAR_MODEL_LONG_DESCR @"c_mo_l"
#define CAR_MODEL_SHORT_DESCR @"c_mo_s"

// END CAR MAKE MODEL DATA


// MENU STUFF

#define GET_MENU_RESPONSE_TAG @"get_menu_response"

#define MENU_TAG @"menu"

// MENU_ID_ATTR:Used for when menu ID is embedded in a non-menu (when you get a menu its just id=)
#define MENU_ID_ATTR @"m_id"
#define MENU_REV_ATTR @"m_rev"
#define MENU_NAME_ATTR @"m_name"
#define MENU_COMPAT_LEVEL_ATTR @"compat_level"


// MENU ITEM TYPES
#define MENU_ITEM_TYPES_LIST_TAG @"d_t_l"
#define MENU_ITEM_TYPE_TAG @"d_t"
#define MENU_ITEM_TYPE_NAME @"d_t_ld"
#define MENU_ITEM_TYPE_TEXT @"d_t_t"
#define MENU_ITEM_TYPE_INFO_TAG @"drink_type_info"
// Logical grouping (Organics, Drink, Entree, Dinner, Pizza etc)
#define DRINK_TYPE_ITEM_GROUP_ATTR @"d_t_ig"
// Inherent type (Drink, Food etc)
#define DRINK_TYPE_TYPE_ID_ATTR @"d_t_it"

// MENU ITEM OPTION GROUPS
#define MENU_ITEM_OPTION_GROUPS_LIST_TAG @"d_o_g_l"
#define MENU_ITEM_OPTION_GROUP_TAG @"d_o_g"
#define MENU_ITEM_OPTION_GROUP_PART_NAME_ATTR @"d_o_g_pn"
#define MENU_ITEM_OPTION_GROUP_LONG_NAME_ATTR @"d_o_g_ln"
#define MENU_ITEM_OPTION_GROUP_MULTISELECT_ATTR @"d_o_g_ms"

// MENU ITEM OPTIONS
#define MENU_ITEM_OPTIONS_LIST_TAG @"d_o_l"
#define MENU_ITEM_OPTION_TAG @"d_o"
#define MENU_ITEM_OPTION_NAME_ATTR @"d_on"
#define MENU_ITEM_OPTION_GROUP_ID @"d_ogr"

// MENU ITEM TYPE OPTIONS LIST
#define MENU_ITEM_TYPE_OPTIONS_LIST_TAG @"d_t_o_l"
#define MENU_ITEM_TYPE_OPTION_TAG @"X"
#define MENU_ITEM_TYPE_TYPES_OPTION_DRINK_TYPE_ID_ATTR @"Xa"
#define MENU_ITEM_TYPE_TYPES_OPTION_DRINK_OPTION_ID_ATTR @"Xb"
#define MENU_ITEM_TYPE_TYPES_OPTION_DRINK_OPTION_GROUP_ID_ATTR @"Xc"
#define MENU_ITEM_TYPE_TYPES_OPTION_RANGE_MIN @"Xd"
#define MENU_ITEM_TYPE_TYPES_OPTION_RANGE_MAX @"Xe"
#define MENU_ITEM_TYPE_TYPES_OPTION_COST @"Xf"
#define MENU_ITEM_TYPES_OPTION_CHARGE_EACH @"Xg"

// MENU ITEM TYPE

// MENU ITEM TYPES DEFAULT OPTIONS
#define MENU_ITEM_TYPES_DEFAULT_OPTION_LIST_TAG @"dt_dol"
#define MENU_ITEM_TYPES_DEFAULT_OPTION_TAG @"dt_do"
#define MENU_ITEM_TYPES_DEFAULT_OPTION_DT_ID @"dt_do_dt"
#define MENU_ITEM_TYPES_DEFAULT_OPTION_OPT_GROUP @"dt_do_og"
#define MENU_ITEM_TYPES_DEFAULT_OPTION_OPT_VALUE_ID @"dt_do_ov"
#define MENU_ITEM_TYPES_DEFAULT_OPTION_COUNT @"dt_do_oc"



// MENU_ITEM TYPES MAND OPTIONS
#define MENU_ITEM_TYPES_MAND_OPTION_LIST_TAG @"dt_mol"
#define MENU_ITEM_TYPES_MAND_OPTION @"dt_mo"

#define MENU_ITEM_TYPES_MAND_OPTION_DT_ID @"dt_mo_dt"
#define MENU_ITEM_TYPES_MAND_OPTION_DOG_ID @"dt_mo_dog"



// END MENU STUFF


// ORDERS & ITEMS

#define USER_ORDER_RESPONSE_TAG @"user_order_response"
#define USER_ORDER_HERE_RESPONSE_TAG @"user_order_here_response"
#define ORDER_TAG @"o"

#define OM_ORDER_ITEM_TAG @"om_oi"
#define INCARNATION_ATTR @"i_n"

#define OM_ORDER_ITEM_DESCR_ATTR @"om_oi_d"
#define OM_ORDER_ITEM_COST_ATTR @"om_oi_c"
#define  ORDER_ITEMS_LIST @"drinks_list"

#define OM_ORDER_HIGHEST_TIMESTAMP_ATTR @"ou_hts"

#define ORDER_TIME_HERE_COMMAND @"time_here"

#define ORDER_USER_ID_ATTR @"o_u_id"
#define ORDER_START_TIME_ATTR @"o_st"
#define ORDER_TIME_TO_LOCATION_ATTR @"o_ttl"
#define ORDER_END_TIME_ATTR @"o_et"
#define ORDER_DISPOSITION_ATTR @"o_d"
#define ORDER_DISPOSITION_TEXT_ATTR @"o_dt"
#define ORDER_TOTAL_COST_ATTR @"o_tc"
#define ORDER_USER_EMAIL_ATTR @"o_ue"
#define ORDER_USER_NAME_ATTR @"o_un"
#define ORDER_USER_TIME_HERE_ATTR @"o_uth"
#define ORDER_LOCATION_ID_ATTR @"o_li"
#define ORDER_TIME_NEEDED_ATTR @"o_tn"
#define ORDER_USER_CAR_INFO @"u_uc"
#define ORDER_USER_TAG @"o_ut"
#define ORDER_USER_IS_DEMO_ATTR @"o_ud"
#define ORDER_USER_CLIENT_TYPE_ATTR @"o_ct"
#define ORDER_ARRIVE_MODE_ATTR @"o_am"
#define ORDER_ITEMS_TOTAL_ATTR @"o_it"
#define ORDER_DISCOUNT_ATTR @"o_disc"
#define ORDER_TIP_ATTR @"o_tip"
#define ORDER_PAY_METHOD_ATTR @"o_pm"
#define ORDER_GLOBAL_ORDER_TAG_ATTR @"o_gt"
#define ORDER_IS_TAXABLE_ATTR @"o_tx"
#define ORDER_TAXABALE_AMOUNT_ATTR @"o_tx_amt"
#define ORDER_TAX_ATTR @"o_tx_tax"
#define ORDER_TAX_RATE_ATTR @"o_tx_rt"
#define ORDER_CONV_FEE_ATTR @"o_conv"

// Order Credit Cards
#define ORDER_CREDIT_CARD_TAG @"o_cc"
#define ORDER_CREDIT_CARD_PROFILE_ID_ATTR @"o_ccpf"
#define ORDER_CREDIT_CARD_PROVIDER_ID_ATTR @"o_ccpo"
#define ORDER_CREDIT_CARD_AUTH_CODE @"o_cca"
#define ORDER_CREDIT_CARD_REFUND_AUTH_CODE @"o_ccra"
#define ORDER_CREDIT_CARD_CARD_TYPE @"o_cct"
#define ORDER_CREDIT_CARD_CARD_LAST4 @"o_cc4"
#define ORDER_CREDIT_CARD_DESCR @"o_ccd"

#define ORDER_LOCATION_TAG @"o_location"
#define ORDER_LOCATION_DESCRIPTION_ATTR @"order_location_description"
#define ORDER_LOCATION_ADDRESS_ATTR @"order_location_address"
#define ORDER_LOCATION_HOURS_ATTR @"order_location_hours"
#define ORDER_LOCATION_GPS_LAT_ATTR @"order_location_gps_lat"
#define ORDER_LOCATION_GPS_LONG_ATTR @"order_location_gps_long"
#define ORDER_LOCATION_PHONE_ATTR @"order_location_phone"
#define ORDER_LOCATION_EMAIL_ATTR @"order_location_email"
#define ORDER_LOCATION_INSTRUCTIONS_ATTR @"order_location_instructions"

/////////// END XML STUFF //////////////

#endif /* XML_CONSTANTS_H */