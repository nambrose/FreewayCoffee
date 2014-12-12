package com.freewaycoffee.clientobjlib;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import org.xml.sax.Attributes;

import android.util.Log;

public class FCXMLHelper 
{
	public enum ResponseTypeEnum
	{
		UNKNOWN,
		SIGNON,
		ORDER_DOWNLOAD,
		ORDER_UPDATED,
		LOCATION_UPDATED,
		/* ORDERS_SINCE_TS, OBSOLETE */
		
		ORDERS_SINCE_TAG
	};
	
	public enum ResponseEnum
	{
		NONE,
		OK,
		ERROR
	};
	
	// ERROR CODES
		
	// NOTE: Many of these shared with UserClient --- NEED TO RESOLVE NICK TODO FIXME
	static public final String SIGNON_RESPONSE_TAG = "signon_response";
	
	static public final String RESULT_ATTR="result";
	
	static public final String ATTR_MAIN_SCHEMA_COMPAT_RELEASE_NEEDED="compat_release_needed";
	static public final String ATTR_MAIN_SCHEMA_COMPAT_LEVEL="compat_level";
	
	static public final String UPDATE_TIME_TO_LOCATION_RESULT_ATTR="result";
	
	static public final String FREE_DRINK_ALT_AMOUNT = "5.00";
	// APP SETTINGS
	//////GLOBAL APP SETTINGS
	static public final String  APP_SETTING_LIST_TAG ="app_setting_list";
	static public final String  APP_SETTING_TAG ="app_setting";
	static public final String  APP_SETTING_NAME_ATTR ="app_set_name";
	static public final String  APP_SETTING_VALUE_ATTR ="app_set_val";

	//APP SETTING VALUES
	static public final String  APP_SETTING_NAME_ZERO_TIP_SHOW_RED_ICON = "zero_tip_show_red_icon";
	static public final String  APP_SETTING_NAME_SHOW_TIP_TEXT_ITEMS_LIST = "show_tip_text_items_list";
	static public final String  APP_SETTING_NAME_SHOW_TIPS_BY_PERCENTAGE = "show_tips_by_precentage";
	static public final String  APP_SETTING_TIPS_INCREMENT_FACTOR = "tips_increment_factor";
	static public final String  APP_SETTING_DEFAULT_FREE_DRINK_DISCOUNT_AMT  ="default_free_drink_discount_amt";

	// USER STUFF -- SLOWLY IMPORTING from FC APP
	
	public static final String NON_DEMO_USER="0";
	public static final String DEMO_USER="1";
	
	// ARRIVE MODE (User Part)
	public static final Integer ARRIVE_MODE_CAR_INT=0;
	public static final Integer ARRIVE_MODE_WALKUP_INT=1;
	public static final String ARRIVE_MODE_CAR_STR="0";
	public static final String ARRIVE_MODE_WALKUP_STR="1";
	
	public static final String USER_ARRIVE_MODE_ATTR="user_arrive_mode";
	
	
	public static final String USER_ARRIVE_MODE_CMD_ARG="user_arrive_mode";
	
	public static final String USER_IS_DEMO_ATTR="user_is_demo";
	public static final String USER_IS_LOCKED_ATTR="user_is_locked";
	public static final String USER_TYPE_ATTR="user_type";
	
	public static final String USER_TYPE_NORMAL="0";
	public static final String USER_TYPE_ADMINL="1";
	public static final String USER_TYPE_SUPER="2";
	
	// Order stuff
	static public final String ORDER_LIST_TAG="o_l";
	
	static public final String OM_ORDERS_SINCE_TIMESTAMP_TAG="o_ts_l";

	static public final String OM_ORDER_FOOD_ITEM_TAG="om_oofi";
	static public final String ORDER_TAG="o";
	static public final String ORDER_CREDIT_CARD_TAG="o_cc";

	static public final String ORDER_UPDATED_TAG="order_updated";
	static public final String OM_ORDER_ITEM_LIST_TAG="om_ois";
	
	static public final String OM_ORDER_ITEM_TAG="om_oi";
	static public final String INCARNATION_ATTR="i_n";

	static public final String OM_ORDER_ITEM_DESCR_ATTR="om_oi_d";
	static public final String OM_ORDER_ITEM_COST_ATTR="om_oi_c";
	
	 
	static public final String OM_ORDER_HIGHEST_TIMESTAMP_ATTR="ou_hts";
	
	
	//static public final String  OM_ORDER_FOOD_ITEM_DESCR_ATTR="om_ofi_d";
	//static public final String  OM_ORDER_FOOD_ITEM_COST_ATTR="om_ofi_c";
	
	
    
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
	static public final String ORDER_USER_TAG="o_ut";
	static public final String ORDER_USER_IS_DEMO_ATTR="o_ud";
	static public final String ORDER_USER_CLIENT_TYPE_ATTR="o_ct";
	static public final String ORDER_ARRIVE_MODE_ATTR="o_am";
	
	static public final String ORDER_ITEMS_TOTAL_ATTR="o_it";
	static public final String ORDER_DISCOUNT_ATTR = "o_disc";
	static public final String ORDER_TIP_ATTR = "o_tip";
	static public final String ORDER_PAY_METHOD_ATTR ="o_pm";
	static public final String ORDER_GLOBAL_ORDER_TAG_ATTR="o_gt";
	static public final String ORDER_IS_TAXABLE_ATTR = "o_tx";
	static public final String ORDER_TAXABALE_AMOUNT_ATTR  ="o_tx_amt";
	static public final String ORDER_TAX_ATTR  ="o_tx_tax";
	static public final String ORDER_TAX_RATE_ATTR ="o_tx_rt";
	static public final String ORDER_CONV_FEE_ATTR ="o_conv";

	
	static public final String OM_USER_TIME_HERE_TAG="o_th";
	static public final String OM_USER_TIME_HERE_LOC_ID_ATTR="o_th_l";
	static public final String OM_USER_TIME_HERE_ORDER_ID_ATTR="o_th_o";
	static public final String OM_USER_TIME_HERE_TIME_HERE_ATTR="o_th_t";			
	    
    // Order Credit Cards
	
	static public final String ORDER_CREDIT_CARD_PROFILE_ID_ATTR="o_ccpf";
	static public final String ORDER_CREDIT_CARD_PROVIDER_ID_ATTR="o_ccpo";
	static public final String ORDER_CREDIT_CARD_AUTH_CODE="o_cca";
	static public final String ORDER_CREDIT_CARD_REFUND_AUTH_CODE="ORDER_CREDIT_CARD_REFUND_AUTH_CODE";
	static public final String ORDER_CREDIT_CARD_CARD_TYPE= "o_cct";
	static public final String ORDER_CREDIT_CARD_CARD_LAST4= "o_cc4";
	static public final String ORDER_CREDIT_CARD_DESCR= "o_ccd";
   	
	
	
	static public final String RESPONSE_OK_ATTR="ok";
	static public final String SIGNON_RESPONSE_OK_ATTR="signon_ok";
	
	// ERROR CODES
	static public final String ERROR_TAG="error";
	static public final String ERROR_CODE_MAJOR="error_code_major";
	static public final String ERROR_CODE_MINOR="error_code_minor";
	static public final String ERROR_DISPLAY_TEXT="error_display_text";
	static public final String ERROR_LONG_TEXT="error_long_text";
	
	// Hokey Network Error business!
	static public final String NETWORK_ERROR_XML="<network_error></network_error>";
	static public final String NETWORK_ERROR_TAG="network_error";
		
	static public final String SIGNON_RESPONSE_FAIL="signon_failed";
	
	static public final String URL_DECODE_TYPE="UTF-8";
	static public final String ID_ATTR="id";
	
	static public final String USER_LOCATION_TAG="user_location";
	
	// Location
	// LOCATION
	
	public static ResponseEnum ParseResultCodeFromAttributes(Attributes atts)
	{
		ResponseEnum Response = ResponseEnum.NONE;
		
		String ResponseStr = atts.getValue(FCXMLHelper.RESULT_ATTR);
		try
		{
			ResponseStr=URLDecoder.decode(ResponseStr,FCXMLHelper.URL_DECODE_TYPE);
			if(ResponseStr.equals(FCXMLHelper.RESPONSE_OK_ATTR))
			{
				Response=FCXMLHelper.ResponseEnum.OK;
			}
			else if(ResponseStr.equals(FCXMLHelper.SIGNON_RESPONSE_OK_ATTR))
			{
				Response=FCXMLHelper.ResponseEnum.OK;
			}
			else
			{
				Response=FCXMLHelper.ResponseEnum.ERROR;
			}
		}
		catch(UnsupportedEncodingException e)
		{
			
		}
		return Response;

	}
	
	public static void DoConvertAttrsToStringMap(Attributes atts, HashMap<String,String> MapDest)
	{
		for(int attrsIndex=0; attrsIndex<atts.getLength(); attrsIndex++)
	   	{
	   		String AttrName = atts.getLocalName(attrsIndex);
	   		String AttrValue = atts.getValue(attrsIndex);
	   		try
	   		{
	   			AttrValue = URLDecoder.decode(AttrValue,URL_DECODE_TYPE);
	   			MapDest.put(AttrName,AttrValue );
	   		}
	   		catch(UnsupportedEncodingException e)
	   		{
	   			// TODO
	   		}
	   		
	   		//Log.w("FCXML", "DoConvertAttrsToStringMap: Index: " + attrsIndex + " Name: " + AttrName + " Value " + AttrValue );
	   	}
	}
	
	public static boolean ParseAppSetting(Attributes atts,FCAppSetting Setting)
	{
		String ParsedAttr = atts.getValue(APP_SETTING_NAME_ATTR);
		if(ParsedAttr==null)
		{
			return false;
		}
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	
    	
    	Setting.SetSettingName(ParsedAttr);
    	
    	ParsedAttr = atts.getValue(APP_SETTING_VALUE_ATTR);
		if(ParsedAttr==null)
		{
			return false;
		}
    	ParsedAttr = URLDecoder.decode(ParsedAttr);
    	
    	Setting.SetSettingValue(ParsedAttr);
    	return true;
    	
	}
    // Takes Atts which is a list of Attributes that must have an "id=<int>" member and shoves them into the HashMap<Int, HashMap<String,String> > Object
    public static void ProcessListOfItems(Attributes atts,HashMap<Integer, HashMap<String,String > > Container)
    { 
    	HashMap<String,String> ObjectData = new HashMap<String,String>();
    	Integer ObjectID = ParseIntStringOrMinusOne(atts.getValue(ID_ATTR));
    	FCXMLHelper.DoConvertAttrsToStringMap(atts,ObjectData);
    	Container.put(ObjectID,ObjectData);
    }
    
    public static boolean parseStringValueAsBoolean(String Value)
    {
    	if(Value==null)
    	{
    		return false;
    	}
    	if(  (Value.equals("1")) || (Value.equalsIgnoreCase("true")) )
    	{
    		return true;
    	}
    	return false;
    			
    }
		
			
    public static Integer ParseIntStringOrMinusOne(String Input)
    {
    	try
    	{
    		Integer Result = Integer.parseInt(Input);
    		return Result;
    	}
    	catch (NumberFormatException e)
    	{
    		return -1;
    	}
    	
    }
}
