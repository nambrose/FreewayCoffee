package com.freewaycoffee.client;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Vector;

import org.xml.sax.Attributes;

import android.util.Log;

public class FreewayCoffeeXMLHelper
{
	
	// Error codes
	// Major
	static public final Integer FIRST_APP_ERR_MAJOR=1000;
	static public final Integer APP_ERROR_MAJOR_INTERNAL=FIRST_APP_ERR_MAJOR;
	static public final Integer APP_ERROR_MAJOR_NETWORK=FIRST_APP_ERR_MAJOR+1;
	
	// Minor (APP_ERROR_MAJOR_INTERNAL)
	static public final Integer APP_ERROR_INTERNAL_MINOR_NUMBER_EXCEPTION=0;
	
	// Minor (APP_ERROR_MAJOR_NETWORK)
	static public final Integer APP_ERROR_NETWORK_RESULT_NOT_OK=0;
	
	
	static public final String REGISTER_RESPONSE_TAG = "register_response";
	static public final String SIGNON_RESPONSE_TAG = "signon_response";
	static public final String USER_ITEMS_TAG="user_items";
	static public final String USER_INFO_TAG="user_info";
	static public final String USER_LOCATION_TAG="user_location";
	static public final String USER_CREDIT_CARDS_TAG="user_credit_card";
	static public final String USER_FOODS_TAG="user_foods";
	static public final String USER_FOOD_TAG="user_food";
	static public final String USER_DRINKS_TAG="u_ds";
	static public final String USER_DRINK_TAG="u_d";
	
	//OLDstatic public final String USER_DRINK_NAME="user_drink_name";
	static public final String USER_ADD_DRINK_TAG="user_add_drink";
	static public final String UPDATE_TIME_TO_LOCATION_TAG="updated_time_to_location_result";
	
	static public final String RESULT_ATTR="result";
	static public final String DELETED_DRINK_ID_ATTR="deleted_drink_id";


	// DRINK TYPES
	static public final String DRINK_TYPES_LIST_TAG="d_t_l";
	static public final String DRINK_TYPE_TAG="d_t";
	static public final String DRINK_TYPE_NAME="d_t_ld";
	static public final String DRINK_TYPE_TEXT="d_t_t";
	static public final String DRINK_TYPE_INFO_TAG="drink_type_info";
	
	// DRINK OPTION GROUPS
	static public final String DRINK_OPTION_GROUPS_LIST_TAG="d_o_g_l";
	static public final String DRINK_OPTION_GROUP_TAG="d_o_g";
	static public final String DRINK_OPTION_GROUP_PART_NAME_ATTR="d_o_g_pn";
	static public final String DRINK_OPTION_GROUP_LONG_NAME_ATTR="d_o_g_ln";
	static public final String DRINK_OPTION_GROUP_MULTISELECT_ATTR="d_o_g_ms";
   	
   	// DRINK OPTIONS
	static public final String DRINK_OPTIONS_LIST_TAG="d_o_l";
	static public final String DRINK_OPTION_TAG="d_o";
	static public final String DRINK_OPTION_NAME_ATTR="d_on";
	static public final String DRINK_OPTION_GROUP_ID="d_ogr";
   	
    // DRINK TYPE OPTIONS LIST
	
   	
	static public final String DRINK_TYPE_OPTION_TAG="X";
	static public final String DRINK_TYPES_OPTION_DRINK_TYPE_ID_ATTR="Xa";
	static public final String DRINK_TYPES_OPTION_DRINK_OPTION_ID_ATTR="Xb";
	static public final String DRINK_TYPES_OPTION_DRINK_OPTION_GROUP_ID_ATTR="Xc";
	static public final String DRINK_TYPES_OPTION_RANGE_MIN="Xd";
	static public final String DRINK_TYPES_OPTION_RANGE_MAX="Xe";
	static public final String DRINK_TYPES_OPTION_COST="Xf";
	static public final String DRINK_TYPES_OPTION_CHARGE_EACH ="Xg";
   	
	// DRINK DELETED
	static public final String DRINK_DELETED_TAG="drink_delete_response";
	
	// EDIT DRINK
	static public final String USER_EDIT_DRINK_TAG="user_edit_drink";
	
	// SORT ORDER (generic field)
	static public final String SORT_ORDER_ATTR="sort_order";
	static public final String SORT_ORDER_SHORT_ATTR="so";
	
	// Real Order Response
	static public final String ORDER_ID_ATTR = "order_id";
	static public final String USER_ORDER_HERE_RESPONSE_TAG="user_order_here_response";
	static public final String ORDER_TAG="o";
	
	static public final String ORDER_USER_ID_ATTR="o_u_id"; 
	static public final String ORDER_START_TIME_ATTR="o_st";
	static public final String ORDER_TIME_TO_LOCATION_ATTR="o_ttl";
	static public final String ORDER_END_TIME_ATTR="o_et"; 
	static public final String ORDER_DISPOSITION_ATTR="o_d";
	static public final String ORDER_DISPOSITION_TEXT_ATTR="o_dt";
	static public final String ORDER_TOTAL_COST_ATTR="o_tc";
	static public final String ORDER_USER_EMAIL_ATTR="o_ue";
	static public final String ORDER_USER_NAME_ATTR="o_un";
	static public final String ORDER_USER_TIME_HERE_ATTR="o_uth"; 
	static public final String ORDER_LOCATION_ID_ATTR="o_li";
	static public final String ORDER_TIME_NEEDED_ATTR="o_tn";
	static public final String ORDER_USER_CAR_INFO="u_uc";
	     
	static public final String OM_ORDER_ITEM_TAG="om_oi";
	static public final String OM_ORDER_ITEM_DESCR_ATTR="om_oi_d";
	static public final String OM_ORDER_ITEM_COST_ATTR="om_oi_c";
 
	// Order Credit Cards
	static public final String ORDER_CREDIT_CARD_TAG="o_cc";
	static public final String ORDER_CREDIT_CARD_PROFILE_ID_ATTR="o_ccpf";
	static public final String ORDER_CREDIT_CARD_PROVIDER_ID_ATTR="o_ccpo";
	static public final String ORDER_CREDIT_CARD_AUTH_CODE="o_cca";
	static public final String ORDER_CREDIT_CARD_REFUND_AUTH_CODE="o_ccra";
	static public final String ORDER_CREDIT_CARD_CARD_TYPE="o_cct";
	static public final String ORDER_CREDIT_CARD_CARD_LAST4="o_cc4";
	static public final String ORDER_CREDIT_CARD_DESCR="o_ccd";
   	
   	
	// Order response
/*
	
	static public final String ORDER_RESPONSE_RESULT_ATTR="result";
	
	static public final String USER_ORDER_RESPONSE_TOTAL_COST_ATTR = "total_cost";
	static public final String USER_ORDER_RESPONSE_CREDIT_CARD_LAST4_ATTR = "credit_card_last4";
	static public final String USER_ORDER_RESPONSE_CREDIT_CARD_NAME_ATTR="credit_card_name";
	static public final String USER_ORDER_RESPONSE_TIME_READY_ATTR="time_ready";
	static public final String USER_ORDER_RESPONSE_LOCATION_ATTR="location";
	static public final String USER_ORDER_RESPONSE_ERROR_ATTR="error_string";
	static public final String USER_ORDER_RESPONSE_LOCATION_DESCRIPTION_ATTR="location_description";
	static public final String USER_ORDER_RESPONSE_LOCATION_ADDRESS_ATTR="location_address";
	static public final String USER_ORDER_RESPONSE_LOCATION_PHONE_ATTR="location_phone";
	static public final String USER_ORDER_RESPONSE_LOCATION_EMAIL_ATTR="location_email";
	*/
	static public final String USER_ORDER_RESPONSE_TAG="user_order_response";
	static public final String ATTR_MAIN_SCHEMA_COMPAT_RELEASE_NEEDED="compat_release_needed";
	static public final String ATTR_MAIN_SCHEMA_COMPAT_LEVEL="compat_level";
	
	static public final String UPDATE_TIME_TO_LOCATION_RESULT_ATTR="result";
	
	// ERROR CODES
	static public final String ERROR_TAG="error";
	static public final String ERROR_CODE_MAJOR="error_code_major";
	static public final String ERROR_CODE_MINOR="error_code_minor";
	static public final String ERROR_DISPLAY_TEXT="error_display_text";
	static public final String ERROR_LONG_TEXT="error_long_text";
	
	// ORDER RESPONSE
	
	static public final String ORDER_DRINK_DESCR_ATTR_PREFIX="order_drink_descr";
	static public final String ORDER_DRINK_COST_ATTR_PREFIX="order_drink_cost";
	
	static public final String ORDER_FOOD_DESCR_ATTR_PREFIX="order_food_descr";
	static public final String ORDER_FOOD_COST_ATTR_PREFIX="order_food_cost";
	
	// CAR MAKE MODEL DATA
	
	// Car data
	static public final String USER_CAR_DATA_TAG = "user_car_data";
	static public final String USER_CAR_DESCR_LONG_ATTR = "user_car_info_long_string";
	
	// Car Color
	
	
	static public final String CAR_COLOR_TAG ="car_color";
	static public final String CAR_COLOR_ID_ATTR="id";
	static public final String CAR_COLOR_LONG_DESCR_ATTR="c_co_l";
	static public final String CAR_COLOR_SHORT_DESCR_ATTR="c_co_s";
	
	static public final String ALL_CAR_MAKES_AND_MODELS_TAG="all_car_makes_and_models";
	
	static public final String CAR_MAKE_TAG="c_ma";
	static public final String CAR_MAKE_ID_ATTR="id";
	static public final String CAR_MAKE_LONG_DESCR_ATTR="c_ma_l";
	static public final String CAR_MAKE_SHORT_DESCR_ATTR="c_ma_s";
	static public final String CAR_MAKE_CAN_HAVE_MODELS="c_ma_h";
	
	static public final String CAR_MODEL_TAG="c_mo";
	static public final String CAR_MODEL_ID_ATTR="id";
	static public final String CAR_MODEL_MAKE_ID_ATTR="c_mo_ma";
	static public final String CAR_MODEL_LONG_DESCR="c_mo_l";
	static public final String CAR_MODEL_SHORT_DESCR="c_mo_s";
	
	// END CAR MAKE MODEL DATA
	
	static public final String UPDATE_CREDIT_CARD_RESULT_ATTR="result";
	static public final String CREDIT_CARD_UPDATE_RESPONSE_TAG="update_credit_card_response";
	
	static public final String ADD_DRINK_RESULT_ATTR="result";
	static public final String SUCCESS_REGISTER_SIGNIN="success_register_signin";
	
	static public final String FOOD_INCLUDE_DEFAULT="food_include_default";
	
	// USER TAG
	static public final String USER_UPDATE_TAG_TAG="updated_user_tag_response";
	static public final String USER_TAG_ATTR="user_tag";
	static public final String USER_TAG_TAG="user_tag";
	static public final String USER_TAG_UPDATE_RESULT_ATTR="result";
	// END USER TAG
	
	static public final String PART_ID_PREFIX="partid_";
	
	// DRINK TYPES MANDATORY OPTIONS
	// DRINK TYPED MAND OPTIONS
	static public final String DRINK_TYPES_MAND_OPTION_LIST_TAG="dt_mol";
	static public final String DRINK_TYPES_MAND_OPTION="dt_mo";
   	
	static public final String DRINK_TYPES_MAND_OPTION_DT_ID="dt_mo_dt";
	static public final String DRINK_TYPES_MAND_OPTION_DOG_ID="dt_mo_dog";
   	
	
	// DRINK TYPES DEFAULT OPTIONS
	static public final String DRINK_TYPES_DEFAULT_OPTION_LIST_TAG="dt_dol";
	static public final String DRINK_TYPES_DEFAULT_OPTION_TAG="dt_do";
	static public final String DRINK_TYPES_DEFAULT_OPTION_DT_ID="dt_do_dt";
	static public final String DRINK_TYPES_DEFAULT_OPTION_OPT_GROUP="dt_do_og";
	static public final String DRINK_TYPES_DEFAULT_OPTION_OPT_VALUE_ID="dt_do_ov";
	static public final String DRINK_TYPES_DEFAULT_OPTION_COUNT="dt_do_oc";
   	
	// ORDER LOCATION
	// ORDER LOCATION
	public static final String ORDER_LOCATION_TAG="o_location";
	public static final String ORDER_LOCATION_DESCRIPTION_ATTR="order_location_description";
	public static final String ORDER_LOCATION_ADDRESS_ATTR="order_location_address";
	public static final String ORDER_LOCATION_HOURS_ATTR="order_location_hours";
	public static final String ORDER_LOCATION_GPS_LAT_ATTR="order_location_gps_lat";
	public static final String ORDER_LOCATION_GPS_LONG_ATTR="order_location_gps_long";
	public static final String ORDER_LOCATION_PHONE_ATTR="order_location_phone";
	public static final String ORDER_LOCATION_EMAIL_ATTR="order_location_email";
	public static final String ORDER_LOCATION_INSTRUCTIONS_ATTR="order_location_instructions";
    
	// LOCATION
	public static final String USER_LOCATION_DESCR_ATTR="user_location_description";
	public static final String USER_LOCATION_GPS_LAT_ATTR="user_location_gps_lat";
	public static final String USER_LOCATION_GPS_LONG_ATTR="user_location_gps_long";
	public static final String USER_LOCATION_ADDRESS="user_location_address";
	public static final String USER_LOCATION_HOURS="user_location_hours";
	public static final String USER_LOCATION_PHONE_ATTR="l_p";
	public static final String USER_LOCATION_EMAIL_ATTR="l_e";
	public static final String USER_LOCATION_INSTRUCTIONS="l_i";
	
	public static final String USER_CREDT_CARD_DESCR_ATTR="user_credit_card_descr";
	public static final String USER_CREDIT_CARD_LAST4_ATTR="user_credit_card_last4";
	
	// USER TAG
	public static final String USER_CAR_COLOR_ID_ATTR="car_color_id";
	public static final String USER_CAR_MAKE_ID_ATTR="car_make_id";
	public static final String USER_CAR_MODEL_ID_ATTR="car_model_id";
	
	
	// USER DRINK TAGS
	public static final String USER_DRINK_OPTION_TAG="udo";
	
	// USER DRINK ATTRS
	static public final String USER_DRINK_OPTIONS_TEXT_ATTR="u_d_o";
	public static final String USER_DRINK_LONG_DESCR_ATTR="u_d_ld";
	static public final String USER_DRINK_TYPE_ID="u_d_dt";
	public static final String USER_DRINK_NAME_ATTR="u_d_dn";
	public static final String USER_DRINK_TYPE_NAME_ATTR="u_d_dtn";
	static public final String USER_DRINK_INCLUDE_DEFAULT_ATTR="u_d_id";
	public static final String USER_DRINK_COST_ATTR="u_d_c";
	
	public static final String USER_DRINK_EXTRA_OPTIONS_ATTR="u_d_e";
	
	// USER DRINK OPTIONS
	public static final String  USER_DRINK_OPTION_USER_DRINK_ID_ATTR="udo_udi";
	public static final String  USER_DRINK_OPTION_DRINK_TYPES_OPTION_ID_ATTR="udo_dtoi";
	public static final String  USER_DRINK_OPTION_DRINK_OPTION_ID_ATTR="udo_doi";
	public static final String  USER_DRINK_OPTION_DRINK_OPTION_GROUP_ID_ATTR="udo_dogi";
	public static final String  USER_DRINK_OPTION_COUNT_ATTR="udo_c";
    

	// FOOD
	public static final String USER_FOOD_DELETE_RESPONSE_TAG="food_delete_response";
	
	public static final String FOOD_TYPE_TAG="food_type";
	public static final String FOOD_TYPES_TAG="food_types";
	public static final String FOOD_LONG_DESCR="food_long_descr";
	public static final String FOOD_TAG="food";
	public static final String FOODS_TAG="foods";
	public static final String FOOD_TYPE_ID_ATTR="food_type_id";
	public static final String FOODS_SHORT_DESCR ="food_type_short_descr";
	public static final String FOODS_LONG_DESCR="food_type_long_descr";
	public static final String FOODS_COST="foods_cost";
	
	public static final String  USER_FOOD_FOOD_TYPE="user_food_food_type";
	public static final String  USER_FOOD_LONG_DESCR="user_food_long_descr";
	public static final String  DELETED_FOOD_ID_ATTR="deleted_food_id";
	public static final String  FOOD_OPTION_INFO_TAG="food_option_info";
	public static final String  USER_FOOD_OPTIONS_TEXT="user_food_options_text";
	public static final String  USER_FOOD_ID_ATTR="user_food_id";
	
	public static final String USER_FOOD_FOODS_ID_ATTR="user_food_foods_id";
	
	public static final String FOOD_COST_ATTR="user_food_cost";
	public static final String FOOD_OPTION_TAG="food_option";
	public static final String FOOD_OPTIONS_TAG="food_options";
	static public final String FOOD_OPTION_TYPE="type";
	static public final String FOOD_OPTION_PICK_TYPE="pick";
	static public final String FOOD_OPTION_MANDATORY="mandatory";
	static public final String FOOD_OPTION_LABEL="label";
	static public final String FOOD_OPTION_DESCR="long_descr";
	
	static public final String FOOD_OPTION_DATA="food_option_data";
	
	static public final String FOOD_DEFAULT_OPTIONS="food_default_options";
	static public final String FOOD_DEFAULT_OPTION="food_default_option";
	static public final String FOOD_OPTION_TYPE_ATTR="food_option_type";
	static public final String FOOD_OPTION_VALUE_ATTR="food_option_value";
	
	// USER FOOD
	static public final String USER_FOOD_LONG_DESCR_ATTR="user_food_long_descr";
	static public final String USER_FOOD_SHORT_DESCR_ATTR="user_food_short_descr";
	static public final String USER_FOOD_COST_ATTR="user_food_cost";
	static public final String USER_FOOD_INCLUDE_DEFAULT_ATTR="include_default";
    
	static public final String USER_ADD_FOOD_TAG="user_add_food";
	static public final String USER_EDIT_FOOD_TAG="user_edit_food";
    

	static public final String DRINK_OPTION_INFO_TAG="drink_option_info";
	
	
	
	// DRINK OPTIONS
	static public final String DRINK_OPTION_TYPE="type";
	static public final String DRINK_OPTION_PICK_TYPE="pick";
	static public final String DRINK_OPTION_MANDATORY="mandatory";
	static public final String DRINK_OPTION_LABEL="label";
	static public final String DRINK_OPTION_DESCR="long_descr";

	
	static public final String OPTION_DATA_NAME="option_data";
	
	
	
	// Hokey Network Error business!
	static public final String NETWORK_ERROR_XML="<network_error></network_error>";
	static public final String NETWORK_ERROR_TAG="network_error";
	
	static public final String SIGNON_RESPONSE_FAIL="signon_failed";
	
	static public final String USER_ITEMS_COMPAT_ATTR="";
	
	static public final String DRINK_TYPE_ID="id";
	
	// FOOD TYPES DEFAULT OPTIONS
	public static FreewayCoffeeFoodDrinkTypeDefaultOption ParseFoodTypesDefaultOption(Attributes atts)
	{
		FreewayCoffeeFoodDrinkTypeDefaultOption Option = new FreewayCoffeeFoodDrinkTypeDefaultOption();
			
		// ID -- NOT SENT NOW
		/*
		String ParsedAttr = atts.getValue("id");
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		
		try
		{
			Option.SetID(Integer.parseInt(ParsedAttr));
		}
		catch(NumberFormatException e)
		{
			// TODO Log
			return null;
		}	
		*/
		String ParsedAttr;
		// DRINK TYPE
		ParsedAttr = atts.getValue(FOOD_TYPE_ID_ATTR);
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		try
		{
			Option.SetDrinkType(Integer.parseInt(ParsedAttr));
		}
		catch(NumberFormatException e)
		{
			// TODO Log
			return null;
		}
		
		// OPTION TYPE (milks, sizes etc)
		ParsedAttr = atts.getValue(FOOD_OPTION_TYPE_ATTR);
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		
		//Option.SetOptionName(ParsedAttr);
		
		// OPTION VALUE
		ParsedAttr = atts.getValue(FOOD_OPTION_VALUE_ATTR);
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		try
		{
			Option.SetOptionValue(Integer.parseInt(ParsedAttr));
		}
		catch(NumberFormatException e)
		{
			// TODO Log
			return null;
		}
		
		return Option;
		
	}
	
	
	// DRINK TYPES DEFAULT OPTIONS
	public static FreewayCoffeeFoodDrinkTypeDefaultOption ParseDrinkTypesDefaultOption(Attributes atts)
	{
		FreewayCoffeeFoodDrinkTypeDefaultOption Option = new FreewayCoffeeFoodDrinkTypeDefaultOption();
			
		// ID -- not sent
		//String ParsedAttr = atts.getValue("id");
		//ParsedAttr = URLDecoder.decode(ParsedAttr);
		/*
		try
		{
			Option.SetID(Integer.parseInt(ParsedAttr));
		}
		catch(NumberFormatException e)
		{
			// TODO Log
			return null;
		}	
		*/
		String ParsedAttr;
		
		// DRINK TYPE
		ParsedAttr = atts.getValue(DRINK_TYPES_DEFAULT_OPTION_DT_ID);
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		try
		{
			Option.SetDrinkType(Integer.parseInt(ParsedAttr));
		}
		catch(NumberFormatException e)
		{
			// TODO Log
			return null;
		}
		
		// OPTION TYPE (milks, sizes etc)
		ParsedAttr = atts.getValue(DRINK_TYPES_DEFAULT_OPTION_OPT_GROUP);
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		
		try
		{
			Option.SetOptionGroup(Integer.parseInt(ParsedAttr));
		}
		catch(NumberFormatException e)
		{
			// TODO Log
			return null;
		}
		
		
		// OPTION VALUE
		ParsedAttr = atts.getValue(DRINK_TYPES_DEFAULT_OPTION_OPT_VALUE_ID);
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		try
		{
			Option.SetOptionValue(Integer.parseInt(ParsedAttr));
		}
		catch(NumberFormatException e)
		{
			// TODO Log
			return null;
		}
		
		// OPTION COUNT
		ParsedAttr = atts.getValue(DRINK_TYPES_DEFAULT_OPTION_COUNT);
		
		try
		{
			// Server does not send if it is the default (1)
			if( (ParsedAttr!=null) && (ParsedAttr.length()>0))
			{
				ParsedAttr = URLDecoder.decode(ParsedAttr);
				Option.SetOptionCount(Integer.parseInt(ParsedAttr));
			}
		}
		catch(NumberFormatException e)
		{
			// TODO Log
			return null;
		}
		return Option;
		
	}
	
	// DRINK TYPES MANDATORY OPTIONS
	public static FreewayCoffeeDrinkTypeMandatoryOption ParseDrinkTypesMandatoryOption(Attributes atts)
	{
		FreewayCoffeeDrinkTypeMandatoryOption Option = new FreewayCoffeeDrinkTypeMandatoryOption();
		
		/*
		// ID
		String ParsedAttr = atts.getValue("id");
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		
		try
		{
			Option.SetItemID(Integer.parseInt(ParsedAttr));
		}
		catch(NumberFormatException e)
		{
			// TODO Log
			return null;
		}	
		*/
		String ParsedAttr;
	
		// DRINK TYPE
		ParsedAttr = atts.getValue(DRINK_TYPES_MAND_OPTION_DT_ID);
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		try
		{
			Option.SetDrinkType(Integer.parseInt(ParsedAttr));
		}
		catch(NumberFormatException e)
		{
			// TODO Log
			return null;
		}
		
		// Option Name
		ParsedAttr = atts.getValue(DRINK_TYPES_MAND_OPTION_DOG_ID);
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		
		
		try
		{
			Option.SetOptionGroup(Integer.parseInt(ParsedAttr));
		}
		catch(NumberFormatException e)
		{
			// TODO Log
			return null;
		}
		
		return Option;
		
	}
	
	
	public static void DoConvertAttrsToStringMap(Attributes atts, HashMap<String,String> MapDest)
	{
		for(int attrsIndex=0; attrsIndex<atts.getLength(); attrsIndex++)
	   	{
	   		String AttrName = atts.getLocalName(attrsIndex);
	   		String AttrValue = atts.getValue(attrsIndex);
	   		AttrValue = URLDecoder.decode(AttrValue);
	   		MapDest.put(AttrName,AttrValue );
	   		//Log.w("FCXML", "DoConvertAttrsToStringMap: Index: " + attrsIndex + " Name: " + AttrName + " Value " + AttrValue );
	   	}
	}
	
    // Takes Atts which is a list of Attributes that must have an "id=<int>" member and shoves them into the HashMap<Int, HashMap<String,String> > Object
    public static void ProcessListOfItems(Attributes atts,HashMap<Integer, HashMap<String,String > > Container)
    { 
    	HashMap<String,String> ObjectData = new HashMap<String,String>();
    	Integer ObjectID = Integer.parseInt(atts.getValue("id"));
    	FreewayCoffeeXMLHelper.DoConvertAttrsToStringMap(atts,ObjectData);
    	Container.put(ObjectID,ObjectData);
    }
    
    

    public static boolean ParseDrinkOption(Attributes atts,FreewayCoffeeFoodDrinkOption Option)
    {
    	String ParsedAttr = atts.getValue("id");
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	try
    	{
    		Option.SetOptionID(Integer.parseInt(ParsedAttr));
    		//Integer.parseInt(ParsedAttr));
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    		
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.DRINK_OPTION_NAME_ATTR);
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	
    	
    	Option.SetOptionName(ParsedAttr);
    	
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.DRINK_OPTION_GROUP_ID);
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	try
    	{
    		Option.SetGroupOptionID(Integer.parseInt(ParsedAttr));
    		
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	try
       	{
       		String SortOrderStr = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.SORT_ORDER_SHORT_ATTR));
       		Integer SortOrder = Integer.parseInt(SortOrderStr);
       		Option.SetSortOrder(SortOrder);
    	}
       	catch(NumberFormatException e)
       	{
       		return false;
    	}    
    	return true;
    }
    
    public static boolean ParseDrinkOptionGroup(Attributes atts,FreewayCoffeeFoodDrinkOptionGroup Group)
    {
    	
    	String ParsedAttr = atts.getValue("id");
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	try
    	{
    		Group.SetGroupID(Integer.parseInt(ParsedAttr));
    		//Integer.parseInt(ParsedAttr));
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.DRINK_OPTION_GROUP_PART_NAME_ATTR);
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	
    	Group.SetPartName(ParsedAttr);
    	
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.DRINK_OPTION_GROUP_LONG_NAME_ATTR);
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	
    	Group.SetGroupName(ParsedAttr);
    	
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.DRINK_OPTION_GROUP_MULTISELECT_ATTR);
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	try
    	{
    		Integer Method = Integer.parseInt(ParsedAttr);
    		if(Method==0)
    		{
    			Group.SetSelectionType(FreewayCoffeeFoodDrinkOptionGroup.SelectionType.SelectOne);
    		}
    		else if(Method==1)
    		{
    			Group.SetSelectionType(FreewayCoffeeFoodDrinkOptionGroup.SelectionType.SelectMulti);
    		}
    		else
    		{
    			return false;
    		}
    		
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	try
       	{
       		String SortOrderStr = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.SORT_ORDER_SHORT_ATTR));
       		Integer SortOrder = Integer.parseInt(SortOrderStr);
       		Group.SetSortOrder(SortOrder);
    	}
       	catch(NumberFormatException e)
       	{
       		return false;
    	}    
    	return true;
    }
    
    public static boolean ParseUserDrinkAttrs(FreewayCoffeeUserDrink TheDrink, Attributes atts)
    {
    	if( (TheDrink==null) || (atts==null))
    	{
    		return false;
    	}
    	String ParsedAttr = atts.getValue("id");
    			
    			//FreewayCoffeeXMLHelper.DRINK_TYPES_MANDATORY_OPTION_DRINK_TYPE_ID_ATTR);
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		try
		{
			TheDrink.SetUserDrinkID(Integer.parseInt(ParsedAttr));
		}
		catch(NumberFormatException e)
		{
			// TODO Log
			return false;
		}
		ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.USER_DRINK_OPTIONS_TEXT_ATTR);
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		TheDrink.SetUserDrinkOptionsText(ParsedAttr);
		
		ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.USER_DRINK_TYPE_NAME_ATTR);
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		TheDrink.SetDrinkTypeLongDescr(ParsedAttr);
		
		ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.USER_DRINK_NAME_ATTR);
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		TheDrink.SetUserDrinkName(ParsedAttr);
		
		ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.USER_DRINK_EXTRA_OPTIONS_ATTR);
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		TheDrink.SetUserDrinkExtra(ParsedAttr);
		
		ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.USER_DRINK_TYPE_ID);
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		
		try
		{
			TheDrink.SetDrinkTypeID(Integer.parseInt(ParsedAttr));
		}
		catch(NumberFormatException e)
		{
			// TODO Log
			return false;
		}
		
		ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.USER_DRINK_INCLUDE_DEFAULT_ATTR);
		try
		{
			Integer Value = Integer.parseInt(ParsedAttr);
			if(Value>0)
			{
				TheDrink.SetIncludeDefault(true);
			}
			else
			{
				TheDrink.SetIncludeDefault(false);
			}
			
		}
		catch(NumberFormatException e)
		{
			// TODO Log
			return false;
			
		}
		
		ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.USER_DRINK_COST_ATTR);
		ParsedAttr = URLDecoder.decode(ParsedAttr);
		TheDrink.SetUserDrinkCost(ParsedAttr);
		
		
		return true;
		
    }
    
    public static boolean ParseUserDrinkOptions(FreewayCoffeeApp appState, FreewayCoffeeFoodDrinkUserOption TheOption, Attributes atts)
    {
    	if( (TheOption==null) || (atts==null))
    	{
    		return false;
    
    	}
  	
    	String ParsedAttr = atts.getValue("id");
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	try
    	{
    		TheOption.SetID(Integer.parseInt(ParsedAttr));
    		//Integer.parseInt(ParsedAttr));
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	
    	// USER_DRINK_OPTION_USER_DRINK_ID_ATTR -- not parsed (as it is attached to that drink already)
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.USER_DRINK_OPTION_DRINK_TYPES_OPTION_ID_ATTR);
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	try
    	{
    		TheOption.SetDrinkTypesOptionID(Integer.parseInt(ParsedAttr));
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.USER_DRINK_OPTION_DRINK_OPTION_ID_ATTR);
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	try
    	{
    		TheOption.SetDrinkOptionID(Integer.parseInt(ParsedAttr));
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.USER_DRINK_OPTION_DRINK_OPTION_ID_ATTR);
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	try
    	{
    		TheOption.SetDrinkOptionID(Integer.parseInt(ParsedAttr));
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.USER_DRINK_OPTION_DRINK_OPTION_GROUP_ID_ATTR);
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	try
    	{
    		TheOption.SetDrinkOptionGroupID(Integer.parseInt(ParsedAttr));
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.USER_DRINK_OPTION_COUNT_ATTR);
    	
    	try
    	{
    		// May not be set. If so, Default = 1
    		if(ParsedAttr!=null)
    		{
    			ParsedAttr = URLDecoder.decode(ParsedAttr);
    			TheOption.SetOptionCount(Integer.parseInt(ParsedAttr));
    		}
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	
    	try
    	{
    		String SortOrderStr = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.SORT_ORDER_SHORT_ATTR));
    		Integer SortOrder = Integer.parseInt(SortOrderStr);
    		TheOption.SetSortOrder(SortOrder);
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO LOG FIXME
    		//CurrentDrinkType.SetSortOrder(-1);
    	}
    	
    	if( (TheOption.GetSortOrder()==null) || (TheOption.GetSortOrder()==-1))
    	{
    		FreewayCoffeeFoodDrinkOption Op= appState.FindDrinkOption(TheOption.GetDrinkOptionGroupID(),TheOption.GetDrinkOptionID());
    		if(Op!=null)
    		{
    			// May not be present if menu is not downloaded
    			TheOption.SetSortOrder(Op.GetSortOrder());
    		}
    		else
    		{
    			TheOption.SetSortOrder(-1);
    			
    		}
    	}
    	return true;
    }
    
    public static void ParseDrinkType(Attributes atts,FreewayCoffeeFoodDrinkType CurrentDrinkType )
    {
    	try
    	{
    		Integer ID = Integer.parseInt(URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.DRINK_TYPE_ID)));
    		CurrentDrinkType.SetTypeID(ID);
    	}
    	catch (NumberFormatException ne)
    	{
    		CurrentDrinkType.SetTypeID(-1);
    	}

    	CurrentDrinkType.SetTypeName(URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.DRINK_TYPE_NAME)));
    	String ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.DRINK_TYPE_TEXT);
    	if(ParsedAttr!=null)
    	{
    		ParsedAttr = URLDecoder.decode(ParsedAttr);
    		CurrentDrinkType.SetFoodDrinkTypeText(ParsedAttr);
    	}
    	try
    	{
    		String SortOrderStr = URLDecoder.decode(atts.getValue(FreewayCoffeeXMLHelper.SORT_ORDER_SHORT_ATTR));
    		Integer SortOrder = Integer.parseInt(SortOrderStr);
    		CurrentDrinkType.SetSortOrder(SortOrder);
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO LOG FIXME
    		CurrentDrinkType.SetSortOrder(-1);
    	}
    }
    
    public static boolean ParseDrinkTypeOption(Attributes atts, FreewayCoffeeFoodDrinkTypeOption Option)
    {
    	String ParsedAttr = atts.getValue("id");
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	try
    	{
    		Option.SetFoodDrinkTypeOptionID(Integer.parseInt(ParsedAttr));
    		//Integer.parseInt(ParsedAttr));
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.DRINK_TYPES_OPTION_DRINK_OPTION_ID_ATTR);
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	try
    	{
    		Option.SetFoodDrinkOptionID(Integer.parseInt(ParsedAttr));
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.DRINK_TYPES_OPTION_DRINK_OPTION_GROUP_ID_ATTR);
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	try
    	{
    		Option.SetFoodDrinkOptionGroupID(Integer.parseInt(ParsedAttr));
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.DRINK_TYPES_OPTION_DRINK_TYPE_ID_ATTR);
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	try
    	{
    		Option.SetFoodDrinkTypeID(Integer.parseInt(ParsedAttr));
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	
    	
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.DRINK_TYPES_OPTION_RANGE_MIN);
    	
    	try
    	{
    		// This one is optional from the server (and defaulted here)
    		if( (ParsedAttr!=null) && (ParsedAttr.length()>0))
    		{
    			ParsedAttr = URLDecoder.decode(ParsedAttr);
    			Option.SetMinCount(Integer.parseInt(ParsedAttr));
    		}
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.DRINK_TYPES_OPTION_RANGE_MAX);
    	
    	try
    	{
    		// This one is optional from the server (and defaulted here)
    		if((ParsedAttr!=null) && (ParsedAttr.length()>0))
    		{
    			ParsedAttr = URLDecoder.decode(ParsedAttr);
    			Option.SetMaxCount(Integer.parseInt(ParsedAttr));
    		}
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.DRINK_TYPES_OPTION_COST);
    	
    	
    	// This one is optional from the server (and defaulted here)
    	if((ParsedAttr!=null) && (ParsedAttr.length()>0))
    	{
    		ParsedAttr = URLDecoder.decode(ParsedAttr);
    		Option.SetCostPer(ParsedAttr);
    	}
    	
    	ParsedAttr = atts.getValue(FreewayCoffeeXMLHelper.DRINK_TYPES_OPTION_CHARGE_EACH);
    	
    	try
    	{
    		// This one is optional from the server (and defaulted here)
    		if((ParsedAttr!=null) && (ParsedAttr.length()>0))
    		{
    			ParsedAttr = URLDecoder.decode(ParsedAttr);
    			Option.SetChargeEach(Integer.parseInt(ParsedAttr));
    		}
    	}
    	catch(NumberFormatException e)
    	{
    		// TODO Log
    		return false;
    	}
    	
    	return true;
    	
    }
    public static boolean CheckPasswordStrength(String Password)
    {
    	boolean ContainsUpper=false;
    	boolean ContainsNumber=false;
    	boolean ContainsChar=false;
    	if(Password.length()<6)
    	{
    		return false;
    	}
    	
    	return true;
    	/*
    	if(Password==null)
    	{
    		return false;
    	}
    	if(Password.length()<8)
    	{
    		return false;
    	}
    	for(int index=0;index<Password.length();index++)
    	{
    		if(Character.isDigit(Password.charAt(index)))
    		{
    			ContainsNumber=true;
    		}
    		if(Character.isUpperCase(Password.charAt(index)))
    		{
    			ContainsUpper=true;
    		}
    		if(Character.isLetter(Password.charAt(index)))
    		{
    			ContainsChar=true;
    		}
    		if( (ContainsUpper==true) &&
    			(ContainsNumber==true) &&
    			(ContainsChar==true) )
    		{
    			return true;
    		}
    		
    	}
    	return false;
    	*/
    }
}
