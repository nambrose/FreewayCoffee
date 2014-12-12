package com.freeewaycoffee.ordermanager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

import org.xml.sax.Attributes;

public class FCOrderManagerXMLHelper 
{
	public enum ResponseTypeEnum
	{
		UNKNOWN,
		SIGNON,
		ORDER_DOWNLOAD,
		ORDER_AND_TIME_HERE_DOWNLOAD
	};
	
	public enum SignonResponseEnum
	{
		NONE,
		OK,
		ERROR
	};
	// NOTE: Many of these shared with UserClient --- NEED TO RESOLVE NICK TODO FIXME
	static public final String SIGNON_RESPONSE_TAG = "signon_response";
	
	static public final String RESULT_ATTR="result";
	
	static public final String ATTR_MAIN_SCHEMA_COMPAT_RELEASE_NEEDED="compat_release_needed";
	static public final String ATTR_MAIN_SCHEMA_COMPAT_LEVEL="compat_level";
	
	static public final String UPDATE_TIME_TO_LOCATION_RESULT_ATTR="result";
	
	// Order stuff
	static public final String ORDER_LIST_TAG="o_l";
	static public final String OM_ORDER_AND_TIME_HERE_LIST_TAG="o_a_th_l";
	static public final String OM_USER_TIME_HERE_LIST_TAG="o_thl";
	static public final String  OM_ORDER_FOOD_ITEM_TAG="om_oofi";
	static public final String ORDER_TAG="o";
	static public final String ORDER_CREDIT_CARD_TAG="o_cc";
	static public final String  OM_ORDER_DRINK_ITEM_LIST_TAG="om_odis";
	static public final String  OM_ORDER_FOOD_ITEM_LIST_TAG="om_ofis";
	static public final String  OM_ORDER_DRINK_ITEM_TAG="om_odi";
	
	
	static public final String INCARNATION_ATTR="incarnation";

	
	    
	static public final String  OM_ORDER_DRINK_ITEM_DESCR_ATTR="om_odi_d";
	static public final String  OM_ORDER_DRINK_ITEM_COST_ATTR="om_odi_c";
	    		
	
	static public final String  OM_ORDER_FOOD_ITEM_DESCR_ATTR="om_ofi_d";
	static public final String  OM_ORDER_FOOD_ITEM_COST_ATTR="om_ofi_c";
	
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
	static public final String USER_INFO_TAG="user_info";
	static public final String USER_LOCATION_TAG="user_location";
	
	// Location
	// LOCATION
	public static final String USER_LOCATION_DESCR_ATTR="user_location_description";
	public static final String USER_LOCATION_GPS_LAT_ATTR="user_location_gps_lat";
	public static final String USER_LOCATION_GPS_LONG_ATTR="user_location_gps_long";
	public static final String USER_LOCATION_ADDRESS="user_location_address";
	public static final String USER_LOCATION_HOURS="user_location_hours";

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
	
    // Takes Atts which is a list of Attributes that must have an "id=<int>" member and shoves them into the HashMap<Int, HashMap<String,String> > Object
    public static void ProcessListOfItems(Attributes atts,HashMap<Integer, HashMap<String,String > > Container)
    { 
    	HashMap<String,String> ObjectData = new HashMap<String,String>();
    	Integer ObjectID = ParseIntStringOrMinusOne(atts.getValue(ID_ATTR));
    	FCOrderManagerXMLHelper.DoConvertAttrsToStringMap(atts,ObjectData);
    	Container.put(ObjectID,ObjectData);
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
