package com.freewaycoffee.client;

import java.util.*;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.InputFilter;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import 	java.io.IOException;
import java.io.UnsupportedEncodingException;
import 	java.net.MalformedURLException;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.cookie.Cookie;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import java.security.KeyStore;
import org.apache.http.conn.ssl.SSLSocketFactory;

import com.freewaycoffee.clientobjlib.FCAppSettingTable;
import com.freewaycoffee.clientobjlib.FCXMLHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import android.text.Spanned;

public class FreewayCoffeeApp extends Application {
	private String LoginEmail;
	//private String LoginNickname;
	private String LoginPW;
	private boolean AutoLogin;
	
	public enum FoodDrink
	{
		Food,
		Drink
	}
	
	// PROD
	public static final String BASE_URL="https://freecoffapp.com/fc/";
	// DEV
	//public static final String BASE_URL="https://freecoffapp.com/fc_dev/";

		
	
	// RELEASE
	public static final String HOST_NAME="freecoffapp.com";
			
	// PREFERENCES
	
	private SharedPreferences AppPrefs;
	// App Key
	private static final String FC_APP_PREFS="freeway_coffee_prefs";
	
	// Pref Name Keys
	private static final String FC_PREF_NETWORK_CONNECT_TIMEOUT_KEY="network_connect_timeout";
	private static final String FC_PREF_NETWORK_DATA_TIMEOUT_KEY="network_data_timeout";
	private static final String FC_PREF_MAX_TIME_TO_LOCATION_KEY="max_time_to_location";
	private static final String FC_PREF_MINS_BETWEEN_ORDERS="mind_between_orders";
	
	// Pref default Values
	private static final Integer FC_PREF_NETWORK_CONNECT_TIMEOUT_DEFAULT=30;
	private static final Integer FC_PREF_NETWORK_DATA_TIMEOUT_DEFAULT=30;
	private static final Integer FC_PREF_MAX_TIME_TO_LOCATION_DEFAULT=30;
	private static final Integer FC_PREF_MINS_BETWEEN_ORDERS_DEFAULT=5;
		
	/// END PREFERENCES

	// General commands to server
	public static final String COMMAND_STRING="user_command";
	
	// Pages
	public static final String ORDER_PAGE="fc_orders.php";
	private static final String SIGNUP_PAGE = "fc_signup.php";
	private static final String SIGNON_PAGE="fc_signon.php";
	private static final String USER_PAGE="fc_user.php";
	private static final String DRINK_PICK_PAGE="fc_drink_pick.php";
	private static final String DRINK_ADD_EDIT_PAGE="fc_drink_add_edit.php";
	private static final String FOOD_ADD_EDIT_PAGE="fc_food_add_edit.php";
	private static final String REPORT_PAGE="fc_reports.php";
	
	private static final String FOOD_DRINK_PICK_PAGE="fc_food_drink_pick.php";
	private static final String FOOD_PICK_PAGE="fc_food_pick.php";
	
	private static final String APP_CLIENT_TYPE="app_client";
	private static final String APP_CLIENT_VALUE="android";
	
	private static final String PHONE_MODEL="phone_model";
	private static final String ANDROID_VERSION="android_version";
	private static final String PHONE_MANUF="phone_manuf";
	private static final String PHONE_PRODUCT="phone_product";
	
	private static final String FC_VERSION_NAME="version_name";
	private static final String FC_VERSION_NUM="version_num";
	
	
	private static final String COMMAND_REPORT="get_report";
	private static final String REPORT_NUM_DAYS="report_num_days";
	
	private static final String GET_FOOD_DRINK_PICK_LIST="get_food_drink_picker";
	private static final String GET_FOOD_PICK_LIST="get_food_picker";
	
	private static final String FOOD_ADD_EDIT_COMMAND_ADD="user_add_food";
	private static final String FOOD_ADD_EDIT_COMMAND_EDIT="user_edit_food";
	
	private static final String USER_FOOD_ID="user_food_id";
	
	private static final String USER_FOOD_FOODS_ID="user_food_foods_id";
	private static final String USER_FOOD_OPTIONS="user_food_options";
		
	
	private static final String USER_FOOD_FOOD_TYPE="user_food_food_type";
	
	private static final String FOOD_INCLUDE_DEFAULT="user_food_include_default";
		
	private static final String DELETE_FOOD_COMMAND="delete_food";
	
	// FEEDBACK
	private static final String SEND_FEEDBACK_CMD="user_feedback";
	private static final String FEEDBACK_USER_EMAIL="feedback_user_email";
	private static final String FEEDBACK_CODE="feedback_code";
	private static final String FEEDBACK_FEEDBACK="feedback";
	private static final String FEEDBACK_HAPPINESS="feedback_happiness";

    
	// CAR DATA
	private static final String CAR_DATA_PAGE ="fc_make_model_color.php";
	private static final String CAR_PAGE_COMMAND_STRING ="user_command"; 
	private static final String GET_CAR_DATA_CMD="get_all_car_make_models";
	
	private static final String  UPDATE_TAG_AND_CAR ="update_tag_and_car";
	
	
	private static final String  USER_CAR_MAKE_ID="user_car_make_id"; 
	private static final String  USER_CAR_MODEL_ID ="user_car_model_id";
	private static final String  USER_CAR_COLOR_ID ="user_car_color_id";
	
	// UPDATE TAG
	private static final String UPDATE_TAG="update_tag";
	private static final String USER_TAG="user_tag";
	
	private static final String UPDATE_CREDIT_CARD_COMMAND="update_credit_card";

	// ADD EDIT DRINK SCHEMA
	private static final String  DRINK_ADD_EDIT_SCHEMA_STRING="ae_schema";
	private static final String  DRINK_ADD_EDIT_SCHEMA="1";
	
	// DELETE DRINK
	private static final String DELETE_DRINK_COMMAND="delete_drink";
	private static final String DRINK_ID="drink_id";
	
	// EDIT DRINK
	private static final String USER_DRINK_DRINK_ID="user_drink_id";
	
	// Orders
	private static final String MAKE_ORDER_COMMAND="make_order";
	private static final String ORDER_DRINKS_LIST="drinks_list";
	private static final String ORDER_FOOD_LIST="foods_list";
	
	public static final String ORDER_TIME_HERE_COMMAND="time_here";
	public static final String ORDER_ID="order_id";
	
	
	// Credit Cards
	private static final String CREDIT_CARD_NUMBER="credit_card_number";
	private static final String CREDIT_CARD_EXP_MONTH="credit_card_exp_month";
	private static final String CREDIT_CARD_EXP_YEAR="credit_card_exp_year";
	private static final String CREDIT_CARD_ZIP="credit_card_zip";
	

	
	// s_ I guess now indicates going up to server so we dont get mixed up with down ? UGly
	public static final String S_USER_DRINK_OPTIONS="s_udos";	
	
	// SIGNUP
	private static final String SIGNUP_EMAIL="user_email";
	private static final String SIGNUP_NAME="user_name";
	private static final String SIGNUP_PW="user_password";
	
	// SIGNON
	private static final String SIGNON_EMAIL="signon_user_email";
	private static final String SIGNON_PW="signon_user_password";
	private static final String GET_USER_ITEMS_CMD="get_user_items";
	
	private static final String USER_PAGE_COMMAND_STRING="user_command";
	private static final String DRINK_PICK_COMMAND_STRING="user_command";
	private static final String GET_DRINK_PICK_LIST="get_drink_picker";
	private static final String DRINK_ADD_EDIT_COMMAND_STRING="user_command";
	
	private static final String UPDATE_TIME_TO_LOCATION="update_time_to_location";
	
	
	private static final String DRINK_ADD_EDIT_COMMAND_ADD="user_add_drink";
	private static final String DRINK_ADD_EDIT_COMMAND_EDIT="user_edit_drink";
	
	
	//private DefaultHttpClient httpClient;
	private FreewayCoffeeHTTPClient httpClient;
	private FreewayCoffeeHTTP FCHTTP;
	
	private FreewayCoffeeDB DB;
	
	// The userinfo portion of the ItemsInfo XML
	private HashMap<String,String> UserItemsInfo;
	
	
	private HashMap<String,String> UserInfoData;
	private HashMap<String,String> LocationData;
	private HashMap<String,String > CreditCardsData;
	private HashMap<String,String >  UserCarData;
	
	private FreewayCoffeeLastOrder m_LastOrder; // Should I just make a list here ? How we gonna handle this ?
	
	
	private FreewayCoffeeCarMakeModelData CarMakeModelData;
	
	private FreewayCoffeeUserDrink m_DrinkInProgress;
	private HashMap<Integer, HashMap<String, String > > UserFoodData;
	private Map<Integer,FreewayCoffeeUserDrink> m_UserDrinksData;
	
	private HashMap<Integer,FreewayCoffeeFoodDrinkType> DrinkTypes;
	private Map<Integer,FreewayCoffeeFoodDrinkOptionGroup> m_DrinkOptionGroups;
	
	private HashMap<String,FreewayCoffeeFoodDrinkOption> FoodOptions;
	private HashMap<Integer,FreewayCoffeeFoodDrinkType> FoodTypes;
	private HashMap<Integer, HashMap<String,String>> FoodCategories; // THis is actually FoodTypes in the database. Unfortunate a bit. Crap
	private HashMap<String,String> LastError;
	
	private ArrayList<Integer> CurrentDrinksOrder;
	private ArrayList<Integer> CurrentFoodOrder;
	
	private FreewayCoffeeCarMakeModelColorTagHolder CarDataBeingEdited;
	
	private ArrayList<FreewayCoffeeFoodDrinkTypeDefaultOption> DrinkTypesDefaultOptions;
	private ArrayList<FreewayCoffeeDrinkTypeMandatoryOption> DrinkTypesMandatoryOptions;
	
	private ArrayList<FreewayCoffeeFoodDrinkTypeDefaultOption> FoodTypesDefaultOptions;
	
	private FCAppSettingTable m_AppSettings;
	
	public  boolean isAppInstalled(String uri) 
	{
	    PackageManager pm = getBaseContext().getPackageManager();
	    boolean app_installed = false;
	    try 
	    {
	        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
	        app_installed = true;
	    } 
	    catch (PackageManager.NameNotFoundException e) 
	    {
	        app_installed = false;
	    }
	    return app_installed;
	}

	public void onCreate()
	{
		
		DB = new FreewayCoffeeDB(this);
		DB.open();
		
		AppPrefs = getSharedPreferences(FC_APP_PREFS,Activity.MODE_PRIVATE);
	
			
		
		LoginEmail = DB.getLoginEmail();
		
		LoginPW = DB.getPassword();
		AutoLogin = DB.getAutoLogin();
		InitializeHttpClient();
		FCHTTP = new FreewayCoffeeHTTP(this);
		UserItemsInfo = new HashMap<String,String>();
		CreditCardsData=new HashMap<String, String > ();
		
		UserFoodData=new HashMap<Integer, HashMap<String, String > >();
		
		m_UserDrinksData=new HashMap<Integer, FreewayCoffeeUserDrink>();
		UserInfoData=new HashMap<String,String> ();
		LocationData=new HashMap<String,String> ();
		
		CurrentDrinksOrder = new ArrayList<Integer>();
		CurrentFoodOrder = new ArrayList<Integer>();
		
		m_LastOrder = new FreewayCoffeeLastOrder(this);
		
		
		//DrinkOptions = new HashMap<String,FreewayCoffeeFoodDrinkOption> ();
		DrinkTypes = new HashMap<Integer,FreewayCoffeeFoodDrinkType>();
		m_DrinkOptionGroups = new HashMap<Integer,FreewayCoffeeFoodDrinkOptionGroup>();
		
		FoodOptions= new HashMap<String,FreewayCoffeeFoodDrinkOption> ();
		FoodTypes = new HashMap<Integer,FreewayCoffeeFoodDrinkType>();
		FoodCategories=new HashMap<Integer, HashMap<String, String > >();
		
		UserCarData = new HashMap<String,String> ();
		CarMakeModelData = new FreewayCoffeeCarMakeModelData();
		
		DrinkTypesDefaultOptions = new ArrayList<FreewayCoffeeFoodDrinkTypeDefaultOption>();
		
		FoodTypesDefaultOptions = new ArrayList<FreewayCoffeeFoodDrinkTypeDefaultOption>();
		DrinkTypesMandatoryOptions = new  ArrayList<FreewayCoffeeDrinkTypeMandatoryOption>() ;
		
		LastError = new HashMap<String,String>();
		m_DrinkInProgress=null;
		CarDataBeingEdited=null;
		
		m_AppSettings = new FCAppSettingTable();
	}
	
	public void ClearAllDownloadedData()
	{
		UserItemsInfo.clear();
		CreditCardsData.clear();
		UserFoodData.clear();
		m_UserDrinksData.clear();
		UserInfoData.clear();
		LocationData.clear();
		
		//DrinkOptions.clear();
		DrinkTypes.clear();
		m_DrinkOptionGroups.clear();
		
		FoodOptions.clear();
		FoodTypes.clear();
		FoodCategories.clear();
		
		UserCarData.clear();
		CarMakeModelData.Clear();
		CarDataBeingEdited=null;
		DrinkTypesDefaultOptions.clear();
		DrinkTypesMandatoryOptions.clear();
		
		FoodTypesDefaultOptions.clear();
		
		if(m_LastOrder!=null)
		{
			m_LastOrder.Clear();
		}
		CurrentDrinksOrder.clear();
		CurrentFoodOrder.clear();
		
		LastError.clear();
		
		m_DrinkInProgress=null;
		
		m_AppSettings.Clear();
		
	}
	/*
	InputFilter GetEditTextInputFilter()
	{
		InputFilter filter = new InputFilter() 
		{ 
	        public CharSequence filter(CharSequence source, int start, int end, 
	        			Spanned dest, int dstart, int dend) 
	        { 
	        	String Result="";
	        	for (int i = start; i < end; i++) 
	        	{ 
	        		if (Character.isLetterOrDigit(source.charAt(i))) 
	                { 
	        			Result+= source.charAt(i);  
	                }
	        		else if(source.charAt(i)=='#')
	        		{
	        			Result+= source.charAt(i);
	        		}
	        		else if(source.charAt(i)==' ')
	        		{
	        			Result+= source.charAt(i);
	        		}
	        	}
	        	return Result; 
	        }
		}; 
		return filter;
	
	}
	
	InputFilter GetEditTextFreeformInputFilter()
	{
		InputFilter filter = new InputFilter() 
		{ 
	        public CharSequence filter(CharSequence source, int start, int end, 
	        			Spanned dest, int dstart, int dend) 
	        { 
	        	String Result="";
	        	for (int i = start; i < end; i++) 
	        	{ 
	        		if(source.charAt(i)!='&')
	        		{
	        			Result+= source.charAt(i);
	        		}
	        	}
	        	return Result; 
	        }
		}; 
		return filter;
	
	}
	
	InputFilter GetEditTextEmailInputFilter()
	{
		InputFilter filter = new InputFilter() 
		{ 
	        public CharSequence filter(CharSequence source, int start, int end, 
	        			Spanned dest, int dstart, int dend) 
	        { 
	        
	        	String Result="";
	        	for (int i = start; i < end; i++) 
	        	{ 
	        		if (Character.isLetterOrDigit(source.charAt(i))) 
	                {
	        			Result+= source.charAt(i);
	                }
	        		if( (source.charAt(i)=='@' ||  (source.charAt(i)=='.') || (source.charAt(i)=='+')))
	        		{
	        			Result+= source.charAt(i);
	        		}     
	        	}
	        	return Result; 
	        }
		}; 
		return filter;
	
	}
	*/
	
	public FCAppSettingTable GetAppSettingsTable()
	{
		return m_AppSettings;
	}
	
	// Default if no data is NO (Car)
	boolean IsUserWalkup()
	{
		String UserArriveMode = GetUserInfoData().get(FCXMLHelper.USER_ARRIVE_MODE_ATTR);
		if(UserArriveMode==null)
		{
			return false;
		}
		else if(UserArriveMode.equals(FCXMLHelper.ARRIVE_MODE_WALKUP_STR))
		{
			return true;
		}
		return false;
	}
		
	public void SetUserArriveMode(String Mode)
	{
		// Just shove it in. No checking. // TODO CHECK ME FIXME
		GetUserInfoData().put(FCXMLHelper.USER_ARRIVE_MODE_ATTR,Mode);
	}
	public FreewayCoffeeFoodDrinkOption FindDrinkOption(Integer DrinkOptionGroupID,Integer DrinkOptionID)
	{
		FreewayCoffeeFoodDrinkOptionGroup Group = m_DrinkOptionGroups.get(DrinkOptionGroupID);
		if(Group==null)
		{
			return null;
		}
		return Group.FindDrinkOption(DrinkOptionID);
		
	}
	
	public FreewayCoffeeFoodDrinkTypeOption FindDrinkTypeOption(Integer DrinkOptionGroup,Integer DrinkTypeID, Integer DrinkTypeOption)
	{
		FreewayCoffeeFoodDrinkType DrinkType = DrinkTypes.get(DrinkTypeID);
		if(DrinkType==null)
		{
			return null;
		}
		return DrinkType.FindDrinkTypeOptionByDrinkOptionID( DrinkOptionGroup,  DrinkTypeOption);
	}
	
	public boolean IsOptionMandatoryForDrinkType(Integer DrinkTypeID,Integer OptionGroup)
	{
		FreewayCoffeeDrinkTypeMandatoryOption Option=null;
		for(int index=0;index<DrinkTypesMandatoryOptions.size();index++)
		{
			Option = DrinkTypesMandatoryOptions.get(index);
			if( (Option.GetDrinkType().equals(DrinkTypeID) ) && OptionGroup.equals(Option.GetOptionGroup()))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean IsOptionMandatoryForFoodType(Integer FoodTypeID, String OptionType)
	{
		// NOTE FIXME TODO -- all FoodOptions are currently mandatory
		return true;
	}
	
	public FreewayCoffeeFoodDrinkTypeDefaultOption FindFoodDefaultOption(Integer FoodTypeID, Integer OptionGroup)
	{
		return FindFoodDrinkDefaultOption(FoodTypesDefaultOptions,FoodTypeID,OptionGroup);
	}
	
	public FreewayCoffeeFoodDrinkTypeDefaultOption FindDrinkDefaultOption(Integer DrinkTypeID, Integer OptionGroup)
	{
		return FindFoodDrinkDefaultOption(DrinkTypesDefaultOptions,DrinkTypeID,OptionGroup);
	}
	
	public FreewayCoffeeFoodDrinkTypeDefaultOption FindFoodDrinkDefaultOption(ArrayList<FreewayCoffeeFoodDrinkTypeDefaultOption> DefaultOptionList,
													Integer TypeID, Integer OptionGroup)
	{
		FreewayCoffeeFoodDrinkTypeDefaultOption Option=null;
		
		for(int index=0;index<DefaultOptionList.size();index++)
		{
			Option = DefaultOptionList.get(index);
			if(Option.GetType().equals(TypeID))
			{
				if(Option.GetOptionGroup().equals(OptionGroup))
				{
					return Option;
				}
			}
		}
		return null;
	}
	
	
	
	public HashMap<String,String> MakeNewEmptyFood(String FoodCategoryID,String FoodTypeID)
	{
	
		HashMap<String,String> NewFood = new HashMap<String,String>();
		
		NewFood.put(FreewayCoffeeXMLHelper.USER_FOOD_FOOD_TYPE,FoodCategoryID);
		NewFood.put(FreewayCoffeeXMLHelper.USER_FOOD_FOODS_ID_ATTR,FoodTypeID); //  TODO GROSS
		
		// FIXME FOOD AddDefaultPartIDs(NewFood, FoodTypeID,FoodDrink.Food);
		return NewFood;
	}
			
	
	public FreewayCoffeeUserDrink GetDrinkInProgress()
	{
		return m_DrinkInProgress;
	}
	
	public void ClearDrinkInProgress()
	{
		m_DrinkInProgress=null;
	}
	
	public FreewayCoffeeUserDrink SetDrinkInProgress(Integer UserDrinkID)
	{
		FreewayCoffeeUserDrink TheDrink = GetUserDrink(UserDrinkID);
		if(TheDrink!=null)
		{
			m_DrinkInProgress = FreewayCoffeeUserDrink.CloneUserDrink(TheDrink);
		}
		else
		{
			ClearDrinkInProgress();
		}
		
		return m_DrinkInProgress;
		
	}
	public FreewayCoffeeUserDrink MakeAndSetNewEmptyDrink(Integer DrinkTypeID)
	{
		m_DrinkInProgress = new FreewayCoffeeUserDrink();
		m_DrinkInProgress.SetDrinkTypeID(DrinkTypeID);
	
	//	NewDrink.put(FreewayCoffeeXMLHelper.USER_DRINK_TYPE_ID, DrinkTypeID);
		UserDrinkAddDefaultOptions(m_DrinkInProgress);
		//AddDefaultPartIDs(NewDrink,DrinkTypeID,FoodDrink.Drink);
		//return NewDrink;
		return m_DrinkInProgress;
	}
		
	private void UserDrinkAddDefaultOptions(FreewayCoffeeUserDrink TheDrink)
	{
		
		for( FreewayCoffeeFoodDrinkTypeDefaultOption Option : DrinkTypesDefaultOptions )
		{
			if(Option.GetType().equals(TheDrink.GetDrinkTypeID()))
			{
				FreewayCoffeeFoodDrinkUserOption UserOption = new FreewayCoffeeFoodDrinkUserOption();
				// NOTE: NO m_UserDrinkOptionID because this is not in the database yet (Default drink)
				UserOption.SetDrinkOptionGroupID(Option.GetOptionGroup());
				// Not in the table -- bit scary, should we add it ? UserOption.SetDrinkOptionID(Option.GetOptionValue());
				UserOption.SetOptionCount(Option.GetOptionCount());
				UserOption.SetDrinkOptionID(Option.GetOptionValue()); // Default Option points just to FoodDrinkOption, NOT FoodDrinkTypeOption
				
				
				FreewayCoffeeFoodDrinkTypeOption DTOption = FindDrinkTypeOption(Option.GetOptionGroup(),
						TheDrink.GetDrinkTypeID(), Option.GetOptionValue());
				if(DTOption==null)
				{
					// Really bad -- database error
					return;
				}
				UserOption.SetDrinkTypesOptionID(DTOption.GetFoodDrinkTypeOptionID());			
				FreewayCoffeeFoodDrinkOption Op= FindDrinkOption(Option.GetOptionGroup(),Option.GetOptionValue());
				if(Op == null)
				{
					// terrible
					return;
				}
				UserOption.SetSortOrder(Op.GetSortOrder());
	
				TheDrink.AddUserDrinkOption(UserOption);
			}
		}
		TheDrink.SortUserDrinkOptions();
	
	}
	
	public String GetFoodOptionValue(String OptionType,String OptionID)
	{
		FreewayCoffeeFoodDrinkOption Option= GetFoodOptions().get(OptionType);
		//return GetFoodDrinkOptionValue(Option,OptionID);
		return ("Erk");
	}
	
	/*
	public String GetDrinkOptionValue(Integer OptionGroup,String OptionID)
	{
		FreewayCoffeeFoodDrinkOption Option= GetDrinkOptions().get(OptionType);
		return GetFoodDrinkOptionValue(Option,OptionID);
	}
	
	public String GetFoodDrinkOptionValue(FreewayCoffeeFoodDrinkOption Option, String OptionID)
	{
		if(Option==null)
		{
			return getString(R.string.none_text);
		}
		else
		{
			String Ret = Option.GetOptionValueFromID(OptionID);
			if(Ret==null)
			{
				return getString(R.string.none_text);
			}
			else
			{
				return Ret;
			}
		}
	}
	*/
	
	boolean IsUserNameSet()
	{
		if( (getLoginEmail()==null) || (getLoginEmail().length()==0))
		{
			return false;
		}
		return true;
	}
	
	boolean IsPasswordSet()
	{
		if( (GetPassword()==null) || (GetPassword().length()==0))
		{
			return false;
		}
		return true;
	}
	
	
	boolean IsDrinksMenuLoaded()
	{
		
		if( (m_DrinkOptionGroups.size()>0) && (DrinkTypes.size()>0) && 
			(DrinkTypesDefaultOptions.size()>0)  && (DrinkTypesMandatoryOptions.size()>0))
		{
			return true;
		}
		else
		{
			m_DrinkOptionGroups.clear();
			DrinkTypes.clear();
			DrinkTypesDefaultOptions.clear();
			DrinkTypesMandatoryOptions.clear();
			return false;
		}

	}
	
	boolean IsFoodMenuLoaded()
	{
		return true;
		/*
		if( (FoodOptions.size()>0) && (FoodTypes.size()>0) && (FoodCategories.size()>0) && FoodTypesDefaultOptions.size()>0)
		{
			return true;
		}
		
		FoodOptions.clear();
		FoodTypes.clear();
		FoodCategories.clear();
		FoodTypesDefaultOptions.clear();
		return false;
		*/
		
	}
	
	public ArrayList<FreewayCoffeeFoodDrinkTypeDefaultOption> GetDrinkTypesDefaultOptionsData()
	{
		return DrinkTypesDefaultOptions;
	}
	
	public ArrayList<FreewayCoffeeFoodDrinkTypeDefaultOption> GetFoodTypesDefaultOptionsData()
	{
		return FoodTypesDefaultOptions;
	}
	
	public ArrayList<FreewayCoffeeDrinkTypeMandatoryOption> GetDrinkTypesMandatoryOptionsData()
	{
		return DrinkTypesMandatoryOptions;
	}
	
	
	public FreewayCoffeeCarMakeModelColorTagHolder GetCarDataBeingEdited()
	{
		return CarDataBeingEdited;
	}
	
	public void SetCarDataBeingEdited(FreewayCoffeeCarMakeModelColorTagHolder Data)
	{
		CarDataBeingEdited=Data;
	}
	
	public FreewayCoffeeCarMakeModelData GetCarMakeModelData()
	{
		return CarMakeModelData;
	}
	
	public void ClearUserCarData()
	{
		UserCarData.clear();
	}
	
	public HashMap<String,String> GetUserCarData()
	{
		return UserCarData;
	}
	
	public HashMap<String,String> GetLastError()
	{
		return LastError;
	}
	
	/*
	public void SetLastError(String ErrorText)
	{
		LastError=ErrorText;
	}
	*/
	
	
	boolean IsCreditCardPresent()
	{
		if(GetCreditCardsData().size()==0) 
		{
			return false;
		}
		else if( (GetCreditCardsData().get("id")==null) ||
				 (GetCreditCardsData().get("id").equals("")) ||
				 (GetCreditCardsData().get("id").equals("0")) )
		{
			return false;
		}
		return true;
	}
	
	// General order stuff
	
	boolean IsCurrentOrderEmpty()
	{
		if( IsCurrentDrinksOrderEmpty() && IsCurrentFoodOrderEmpty())
		{
			return true;
		}
		return false;
		
	}
	void ClearCurrentOrder()
	{
		ClearCurrentFoodOrder();
		ClearCurrentDrinksOrder();
	}
	
	void GenerateDefaultOrder()
	{
		GenerateDefaultDrinksOrder();
		GenerateDefaultFoodOrder();
	}
	
	BigDecimal GetTotalOrderCost()
	{
		BigDecimal DrinksCost = GetTotalCostOfCurrentDrinksOrder();
		BigDecimal FoodCost = GetTotalCostOfCurrentFoodOrder();
		return DrinksCost.add(FoodCost);
		
	}
	// Current Food Order
	
	boolean IsCurrentFoodOrderEmpty()
	{
		if(CurrentFoodOrder.size()==0)
		{
			return true;
		}
		return false;
	}
	
	ArrayList<Integer> GetCurrentFoodOrder()
	{
		return CurrentFoodOrder;
	}
	
	void ClearCurrentFoodOrder()
	{
		CurrentFoodOrder.clear();
	}
	
	void AddFoodIDToCurrentFoodOrder(Integer FoodID)
	{
		CurrentFoodOrder.add(FoodID);
	}
	
	void RemoveFoodIDFromCurrentFoodOrder(Integer FoodID, boolean RemoveAll)
	{
		RemoveIDFromIntArrayList(CurrentFoodOrder,FoodID,RemoveAll);
	}
	
	BigDecimal GetTotalCostOfCurrentFoodOrder()
	{
		BigDecimal Total=new BigDecimal("0.00");
		
		for(int index=0;index<CurrentFoodOrder.size();index++)
		{
			Integer CurrentDrinkID = CurrentFoodOrder.get(index);
			HashMap<String,String> CurrentFood = UserFoodData.get(CurrentDrinkID);
			
			String FoodCost = CurrentFood.get(FreewayCoffeeXMLHelper.FOOD_COST_ATTR);
			if(FoodCost==null)
			{
				// NOTE: LOG
				continue;
			}
			BigDecimal CurrentCostBigDec = new BigDecimal(FoodCost);
			Total = Total.add(CurrentCostBigDec);
		}
		return Total;
	}
	
	void GenerateDefaultFoodOrder()
	{
		ClearCurrentFoodOrder();
		
		for (HashMap<String, String > Value : GetFoodData().values())
		{	
			try
			{
				Integer FoodID = Integer.parseInt(Value.get("id"));
 				CurrentFoodOrder.add(FoodID);
 			}
 			catch (NumberFormatException e)
 			{
 				// TODO LOG:
 						
 			}		
 		}
	}
	
	
	void RemoveFoodIDFromCurrentFoodOrderAtIndex(Integer FoodID, int index)
	{
		if(CurrentFoodOrder.get(index).equals(FoodID))
		{
			CurrentFoodOrder.remove(index);
		}
	}
	
	
	// Current Drink Order
	
	boolean IsCurrentDrinksOrderEmpty()
	{
		if(CurrentDrinksOrder.size()==0)
		{
			return true;
		}
		return false;
	}
	
	
	
	ArrayList<Integer> GetCurrentDrinksOrder()
	{
		return CurrentDrinksOrder;
	}
	
	void ClearCurrentDrinksOrder()
	{
		CurrentDrinksOrder.clear();
	}
	
	// NOTE TODO: No sanity check here
	void AddDrinkIDToCurrentDrinksOrder(Integer DrinkID)
	{
		CurrentDrinksOrder.add(DrinkID);
	}
	
	void RemoveDrinkIDFromCurrentDrinksOrder(Integer DrinkID, boolean RemoveAll)
	{
		RemoveIDFromIntArrayList(CurrentDrinksOrder,DrinkID,RemoveAll);
	}
	
	void RemoveIDFromIntArrayList(ArrayList<Integer> TheList, Integer TheId, boolean RemoveAll)
	{
		// Go from the back or RemoveAll will have incorrect indexes. 
		if(TheList.size()==0)
		{
			return;
		}
		
		for(int index=TheList.size()-1;index>=0;index--)
		{
			
			if(TheList.get(index).equals(TheId))
			{
				TheList.remove(index);
				if(RemoveAll==false)
				{
					return;
				}
			}
		}
	}
	
	public void AddUserDrink(FreewayCoffeeUserDrink TheDrink)
	{
		if(TheDrink==null)
		{
			return; // LOG
		}
		m_UserDrinksData.put(TheDrink.GetUserDrinkID(),TheDrink);
	}
	public Map<Integer,FreewayCoffeeUserDrink> GetUserDrinksData()
	{
		return m_UserDrinksData;
	}
	public FreewayCoffeeUserDrink GetUserDrink(Integer CurrentDrinkID)
	{
		return m_UserDrinksData.get(CurrentDrinkID);
	}
	
	public BigDecimal GetTotalCostOfCurrentDrinksOrder()
	{
		BigDecimal Total=new BigDecimal("0.00");
		
		for(int index=0;index<CurrentDrinksOrder.size();index++)
		{
			Integer CurrentDrinkID = CurrentDrinksOrder.get(index);
			FreewayCoffeeUserDrink CurrentDrink = GetUserDrink(CurrentDrinkID);
			if(CurrentDrink == null)
			{
				return new BigDecimal("0.00");
			}
			
			BigDecimal CurrentCostBigDec = new BigDecimal(CurrentDrink.GetUserDrinkCost());
			Total = Total.add(CurrentCostBigDec);
		}
		return Total;
	}
	
	void GenerateDefaultDrinksOrder()
	{
		ClearCurrentDrinksOrder();
		for (FreewayCoffeeUserDrink CurrentDrink : GetUserDrinksData().values())
 		{
			if(CurrentDrink.GetIncludeDefault())
			{
				CurrentDrinksOrder.add(CurrentDrink.GetUserDrinkID());
 			}	
 		}
	}
	
	void RemoveDrinkIDFromCurrentDrinksOrderAtIndex(Integer DrinkID, int index)
	{
		if(CurrentDrinksOrder.get(index).equals(DrinkID))
		{
			CurrentDrinksOrder.remove(index);
		}
	}
	
	public FreewayCoffeeLastOrder GetLastOrder()
	{
		return m_LastOrder;
	}
	
	/*
	public boolean IsOrderTooSoonAfterLast()
	{
		Calendar NextOrderTime = Calendar.getInstance();
		NextOrderTime.setTime(LastOrderTime);
		NextOrderTime.add(Calendar.MINUTE,GetPreferenceMinsBetweenOrders());
		
		Calendar Now = Calendar.getInstance();;
		
		if(Now.after(NextOrderTime))
		{
			return true;
		}
		return false;
	}
	*/
	
	
	public Map<Integer,FreewayCoffeeFoodDrinkOptionGroup> GetDrinkOptionGroups()
	{
		return m_DrinkOptionGroups;
	}
	
	public HashMap<String,FreewayCoffeeFoodDrinkOption> GetFoodOptions()
	{
		return FoodOptions;
	}
	
	public HashMap<String, String > GetFoodFromID(Integer FoodID)
	{
		return UserFoodData.get(FoodID);
	}
	
	
	public void DeleteFoodDrinkID(FoodDrink Type, Integer ID)
	{
		if(Type.equals(FoodDrink.Drink))
		{
			DeleteDrinkID(ID);
		}
		else if(Type.equals(FoodDrink.Food))
		{
			DeleteFoodID(ID);
		}
	}
	
	public void DeleteDrinkID(Integer DrinkID)
	{
		m_UserDrinksData.remove(DrinkID);
		// Remove all occurrences of this DrinkID from the current order
		RemoveDrinkIDFromCurrentDrinksOrder(DrinkID,true);
	}

	
	public void DeleteFoodID(Integer FoodID)
	{
		UserFoodData.remove(FoodID);
		// Remove all occurrences of this DrinkID from the current order
		RemoveFoodIDFromCurrentFoodOrder(FoodID,true);
	}

	public HashMap<Integer,FreewayCoffeeFoodDrinkType> GetDrinkTypes()
	{
		return DrinkTypes;
	}
	
	public HashMap<Integer,FreewayCoffeeFoodDrinkType> GetFoodTypes()
	{
		return FoodTypes;
	}
	
	public void AddFoodCategory(Integer CategoryID, HashMap<String,String> FoodCategoryData)
	{
		FoodCategories.put(CategoryID,FoodCategoryData);
	}
	
	public HashMap<Integer, HashMap<String,String>> GetFoodCategories()
	{
		return FoodCategories;
	}
	
	public void ClearFoodCategories()
	{
		FoodCategories.clear();
	}
	
	// DRINK OPTION GROUPS START
	public void AddDrinkOptionGroup(FreewayCoffeeFoodDrinkOptionGroup Group)
	{
		m_DrinkOptionGroups.put(Group.GetGroupID(), Group);
	}
	
	// DRINK OPTION GROUPS END
	
	public String GetDrinkCostFromDrinkID(Integer DrinkID)
	{
		FreewayCoffeeUserDrink TheDrink = GetUserDrink(DrinkID);
		if(TheDrink==null)
		{
			return "oops";
		}
		return TheDrink.GetUserDrinkCost();
		
	}
	public String GetDrinkTypeNameFromDrinkID(Integer DrinkID)
	{
		FreewayCoffeeUserDrink TheDrink = GetUserDrink(DrinkID);
		if(TheDrink==null)
		{
			return "oops";
		}
		
		return GetDrinkTypeNameFromDrinkTypeID(TheDrink.GetDrinkTypeID());
	}
	public String GetDrinkNameFromDrinkID(Integer DrinkID)
	{
		FreewayCoffeeUserDrink TheDrink = GetUserDrink(DrinkID);
		if(TheDrink==null)
		{
			return "oops";
		}
		return TheDrink.GetUserDrinkName();
		
		
	}
	public String GetDrinkTypeNameFromDrinkTypeID(Integer DrinkID)
	{
		return DrinkTypes.get(DrinkID).GetTypeName();
	}
	
	
	public boolean IsSuperUser()
	{
		String UserType = GetUserInfoData().get(FCXMLHelper.USER_TYPE_ATTR);
		if(UserType==null)
		{
			return false;
		}
		if(UserType.equals(FCXMLHelper.USER_TYPE_SUPER))
		{
			return true;
		}
		return false;
		
	}
	public HashMap<String,String> GetUserInfoData()
	{
		return UserInfoData;
	}
	
	public HashMap<String,String>  GetLocationData()
	{
		return LocationData;
	}
	
	public void SetCreditCardsData(HashMap<String, String > TheCard)
	{
		CreditCardsData.clear();
		CreditCardsData = new HashMap<String,String>(TheCard);
		
	}
	
	public HashMap<String, String > GetCreditCardsData()
	{
		return CreditCardsData;
	}
	
	public HashMap<Integer, HashMap<String, String > > GetFoodData()
	{
		return UserFoodData;
	}
	
	
	public HashMap<String,String> GetUserItemsInfo()
	{
		return UserItemsInfo;
	}
	
	public void AddToUserItemsInfo(String Key, String Value)
	{
		UserItemsInfo.put(Key,Value);
	}
	
	public FreewayCoffeeHTTP GetHTTP()
	{
		return FCHTTP;
	}
	
	
	public Integer GetPreferenceMinsBetweenOrders()
	{
		Integer MinsBetween = AppPrefs.getInt(FC_PREF_MINS_BETWEEN_ORDERS,FC_PREF_MINS_BETWEEN_ORDERS_DEFAULT);
		return MinsBetween;
	}
	public Integer GetPreferenceMaxTimeToLocation()
	{
		Integer MaxTimeTo = AppPrefs.getInt(FC_PREF_MAX_TIME_TO_LOCATION_KEY,FC_PREF_MAX_TIME_TO_LOCATION_DEFAULT);
		return MaxTimeTo;
	}
	
	// The timeout in milliseconds until a connection is established.
	public Integer GetPreferenceConnectTimeoutInMillisec()
	{
		Integer TimeoutInSec = AppPrefs.getInt(FC_PREF_NETWORK_CONNECT_TIMEOUT_KEY,FC_PREF_NETWORK_CONNECT_TIMEOUT_DEFAULT);
		return TimeoutInSec*1000;
	}
	
	public Integer GetPreferenceNetworkTimeoutInMillisec()
	{
		Integer TimeoutInSec = AppPrefs.getInt(FC_PREF_NETWORK_DATA_TIMEOUT_KEY,FC_PREF_NETWORK_DATA_TIMEOUT_DEFAULT);
		return TimeoutInSec*1000;
	}
	
	// The timeout inseconds until a connection is established.
	public Integer GetPreferenceConnectTimeoutInSeconds()
	{
		Integer TimeoutInSec = AppPrefs.getInt(FC_PREF_NETWORK_CONNECT_TIMEOUT_KEY,FC_PREF_NETWORK_CONNECT_TIMEOUT_DEFAULT);
		return TimeoutInSec;
	}
	
	// The timeout for waiting for data inn milliseconds
	public Integer GetPreferenceNetworkTimeoutInSeconds()
	{
		Integer TimeoutInSec = AppPrefs.getInt(FC_PREF_NETWORK_DATA_TIMEOUT_KEY,FC_PREF_NETWORK_DATA_TIMEOUT_DEFAULT);
		return TimeoutInSec;
	}
	
	// The timeout in seconds until a connection is established.
	public void SetPreferenceConnectTimeoutInSeconds(Integer Timeout)
	{
		SharedPreferences.Editor editor = AppPrefs.edit();
		editor.putInt(FC_PREF_NETWORK_DATA_TIMEOUT_KEY, Timeout);
		editor.commit();
	}
	// The timeout for waiting for data inn milliseconds
	public void SetPreferenceNetworkTimeoutInSeconds(Integer Timeout)
	{
		SharedPreferences.Editor editor = AppPrefs.edit();
		editor.putInt(FC_PREF_NETWORK_DATA_TIMEOUT_KEY, Timeout);
		editor.commit();
	}
	
	
	private void InitializeHttpClient()
	{
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = GetPreferenceConnectTimeoutInMillisec();
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = GetPreferenceNetworkTimeoutInMillisec();
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		//httpClient = new DefaultHttpClient(httpParameters);
		httpClient = new  FreewayCoffeeHTTPClient(this,httpParameters);
		httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");

		
	}
	
	
	FreewayCoffeeHTTPClient GetHttpClient()
	{
		return httpClient;
	}
	
	public String getLoginEmail()
	{
		return LoginEmail;
	}
	
	public String getLoginNickname()
	{
		if(UserInfoData==null)
		{
			return null;
		}
		return UserInfoData.get(SIGNUP_NAME);
		
	}
	
	public String GetPassword()
	{
		return LoginPW;		
	}
	public boolean GetLoginAutomatically()
	{
		return AutoLogin;
	}
	
	public void SetLoginEmail(String email)
	{
		// TODO: Could be an issue if DB update fails ? (worse case user has to retype)
		LoginEmail = email;
		DB.UpdateLoginEmail(email);
	}

	public void SetPassword(String password)
	{
		// TODO: Could be an issue if DB update fails ? (worse case user has to retype)
		LoginPW=password;
		DB.UpdatePassword(password);
	}
	
	public void SetLoginAutomatically(boolean auto)
	{
		// TODO: Could be an issue if DB update fails ? (worse case user has to retype)
		AutoLogin=auto;
		DB.UpdateAutoLogin(auto);
	}
	
	public String MakeSiteURL()
	{
		return "http://freewaycoffee.com";
	}
	
	public boolean IsOrderSuccessful(HashMap<String,String> TheOrder)
	{
		return false;
		/*
		String OrderResultString = TheOrder.get(FreewayCoffeeXMLHelper.ORDER_RESPONSE_RESULT_ATTR);
		if(OrderResultString==null)
		{
			return false;
		}
		else if(OrderResultString.equals("ok"))
		{
			return true;
		}
		return false;
		*/
	}
	
	public HttpPost MakeReportPost(String NumDays) throws UnsupportedEncodingException
	{
		HttpPost httppost = new HttpPost(BASE_URL + REPORT_PAGE);
		httppost.setHeader("Accept-Encoding", "UTF-8");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(COMMAND_STRING,COMMAND_REPORT));
		nameValuePairs.add(new BasicNameValuePair(REPORT_NUM_DAYS,NumDays));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
		return httppost;
	}

	public HttpPost MakeSignonHTTPPost(String Email, String Password) throws UnsupportedEncodingException
	{
		HttpPost httppost = new HttpPost(BASE_URL + SIGNON_PAGE);
		httppost.setHeader("Accept-Encoding", "UTF-8");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(9);
		nameValuePairs.add(new BasicNameValuePair(SIGNON_EMAIL,Email));
		nameValuePairs.add(new BasicNameValuePair(SIGNON_PW,Password));
		nameValuePairs.add(new BasicNameValuePair(FreewayCoffeeXMLHelper.ATTR_MAIN_SCHEMA_COMPAT_LEVEL,
				String.valueOf(FreewayCoffeeXMLHandler.MAIN_COMPAT_SCHEMA_REV)) );
		
		nameValuePairs.add(new BasicNameValuePair(APP_CLIENT_TYPE,APP_CLIENT_VALUE));
		nameValuePairs.add(new BasicNameValuePair(PHONE_MODEL,android.os.Build.MODEL));
		nameValuePairs.add(new BasicNameValuePair(ANDROID_VERSION,android.os.Build.VERSION.RELEASE));
		nameValuePairs.add(new BasicNameValuePair(PHONE_MANUF,android.os.Build.MANUFACTURER));
		nameValuePairs.add(new BasicNameValuePair(PHONE_PRODUCT,android.os.Build.PRODUCT));
		try
		{
			nameValuePairs.add(new BasicNameValuePair(FC_VERSION_NAME,this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName));
			nameValuePairs.add(new BasicNameValuePair(FC_VERSION_NUM, 
					String.valueOf(this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode)) );
		}
		catch (NameNotFoundException e)
		{
			nameValuePairs.add(new BasicNameValuePair(FC_VERSION_NAME,"Unknown"));
			nameValuePairs.add(new BasicNameValuePair(FC_VERSION_NAME,"0"));
		}
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
		return httppost;
		
	}
	
	
	
	String GetIDListAsString(ArrayList<Integer> TheList)
	{
		String Result="";
		boolean FirstTime=true;
	
		for(int Index=0; Index<TheList.size(); Index++)
		{
		
			if(FirstTime==true)
			{
				FirstTime=false;
			}
			else
			{
				Result +=",";
			}
			
			Result+=TheList.get(Index);
		}
		return Result;
	}

	String GetCurrentDrinkOrderIDList()
	{
		return GetIDListAsString(CurrentDrinksOrder);
	}
	
	String GetCurrentFoodOrderIDList()
	{
		return GetIDListAsString(CurrentFoodOrder);
	}
	
	public String MakeCurrentOrderURL()  throws UnsupportedEncodingException
	{
		String DrinksList = GetCurrentDrinkOrderIDList();
		

		String UserArriveMode = FCXMLHelper.ARRIVE_MODE_CAR_STR;
		if(IsUserWalkup())
		{
			UserArriveMode = FCXMLHelper.ARRIVE_MODE_WALKUP_STR;
		}
		String MakeOrderURL = new String(BASE_URL + ORDER_PAGE + "?" + COMMAND_STRING + "=" + MAKE_ORDER_COMMAND  + "&" +
				APP_CLIENT_TYPE + "=" + URLEncoder.encode(APP_CLIENT_VALUE ,"utf-8") + "&" +
				FCXMLHelper.USER_ARRIVE_MODE_CMD_ARG  +"=" + UserArriveMode +"&" +
				ORDER_DRINKS_LIST + "=" + URLEncoder.encode(DrinksList,"utf-8"));
			
		return MakeOrderURL;
	}
	
	public String MakeSignonURL(String Email, String Password) throws UnsupportedEncodingException
	{
		//URLEncoder.encode(url, "utf-8");

		String SignonURL= new String(BASE_URL + SIGNON_PAGE + "?" + SIGNON_EMAIL + "=" + 
				URLEncoder.encode(Email,"utf-8") + "&" + SIGNON_PW + "=" + URLEncoder.encode(Password,"utf-8"));
		return SignonURL;
	}

	public HttpPost MakeSignupHTTPPost(String Email, String Name, String Password) throws UnsupportedEncodingException
	{
		HttpPost httppost = new HttpPost(BASE_URL + SIGNUP_PAGE);
		httppost.setHeader("Accept-Encoding", "UTF-8");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(10);
		nameValuePairs.add(new BasicNameValuePair(SIGNUP_EMAIL,Email));
		nameValuePairs.add(new BasicNameValuePair(SIGNUP_NAME,Name));
		nameValuePairs.add(new BasicNameValuePair(SIGNUP_PW,Password));
		nameValuePairs.add(new BasicNameValuePair(APP_CLIENT_TYPE,APP_CLIENT_VALUE));
		nameValuePairs.add(new BasicNameValuePair(PHONE_MODEL,android.os.Build.MODEL));
		nameValuePairs.add(new BasicNameValuePair(ANDROID_VERSION,android.os.Build.VERSION.RELEASE));
		nameValuePairs.add(new BasicNameValuePair(PHONE_MANUF,android.os.Build.MANUFACTURER));
		nameValuePairs.add(new BasicNameValuePair(PHONE_PRODUCT,android.os.Build.PRODUCT));
		try
		{
			nameValuePairs.add(new BasicNameValuePair(FC_VERSION_NAME,this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName));
			nameValuePairs.add(new BasicNameValuePair(FC_VERSION_NUM, 
					String.valueOf(this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode)) );
		}
		catch (NameNotFoundException e)
		{
			nameValuePairs.add(new BasicNameValuePair(FC_VERSION_NAME,"Unknown"));
			nameValuePairs.add(new BasicNameValuePair(FC_VERSION_NAME,"0"));
		}
		
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
		return httppost;
		
	}
	
	public String MakeSignupURL(String Email, String Name, String Password) throws UnsupportedEncodingException
	{
		String SignupURL= new String(BASE_URL + SIGNUP_PAGE +"?" + SIGNUP_EMAIL + "=" + 
				URLEncoder.encode(Email,"utf-8") + "&" + SIGNUP_NAME + "=" + 
				URLEncoder.encode(Name,"utf-8") + "&" + SIGNUP_PW + "=" + 
				URLEncoder.encode(Password,"utf-8"));
		
		return SignupURL;
	}
	
	public String MakeGetCarDataURL()
	{
		String GetCarDataURL = new String(BASE_URL + CAR_DATA_PAGE + "?" + CAR_PAGE_COMMAND_STRING + "=" + GET_CAR_DATA_CMD);
		return GetCarDataURL;
	}
	
	public String MakeGetItemListURL()
	{
		String GetItemListURL = new String(BASE_URL +USER_PAGE + "?" + USER_PAGE_COMMAND_STRING + "=" + GET_USER_ITEMS_CMD );
		return GetItemListURL;
		
	}
	
	public String MakeSendFeedbackURL(int HappinessIndicator, String Comments, String UserEmail ) throws UnsupportedEncodingException
	{
		String FeedbackURL = new String(BASE_URL +USER_PAGE + "?" +   USER_PAGE_COMMAND_STRING + "=" + SEND_FEEDBACK_CMD + "&" +
				                           FEEDBACK_USER_EMAIL + "=" + URLEncoder.encode(UserEmail,"utf-8") + "&" +
				                           FEEDBACK_CODE + "=0" + "&" +
				                           FEEDBACK_FEEDBACK + "=" + URLEncoder.encode(Comments,"utf-8") + "&" +
				                           FEEDBACK_HAPPINESS + "=" + HappinessIndicator);
		return FeedbackURL;
		
	}
	
	public String MakeGetFoodDrinkPickListURL()
	{
		String GetFoodDrinkPickList = new String(BASE_URL + FOOD_DRINK_PICK_PAGE + "?" + COMMAND_STRING +"=" + GET_FOOD_DRINK_PICK_LIST);
		
		return GetFoodDrinkPickList;
	}
	public String MakeGetFoodPickListURL()
	{
		String GetFoodPickList = new String(BASE_URL + FOOD_PICK_PAGE + "?" + COMMAND_STRING +"=" + GET_FOOD_PICK_LIST);
		
		return GetFoodPickList;
	}
	public HttpPost MakeGetDrinkPickListURL() 
	{
		HttpPost httppost = new HttpPost(BASE_URL + DRINK_PICK_PAGE);
		httppost.setHeader("Accept-Encoding", "UTF-8");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		nameValuePairs.add(new BasicNameValuePair(USER_PAGE_COMMAND_STRING,GET_DRINK_PICK_LIST));
		
		try
		{
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
		}
		catch (UnsupportedEncodingException e)
		{
			return null;
		}
		return httppost;
	}
	
	public String MakeUpdateTagAndCarDataURL(boolean Walkup)  throws UnsupportedEncodingException
	{
		String UserArriveMode = FCXMLHelper.ARRIVE_MODE_CAR_STR;
		if(Walkup==true)
		{
			UserArriveMode = FCXMLHelper.ARRIVE_MODE_WALKUP_STR;
		}
		
		String UpdateTag = new String(BASE_URL + USER_PAGE + "?" + 
				USER_PAGE_COMMAND_STRING + "=" + UPDATE_TAG_AND_CAR + "&" +
				USER_TAG + "=" + URLEncoder.encode(CarDataBeingEdited.Tag,"utf-8") + "&" +
				FCXMLHelper.USER_ARRIVE_MODE_CMD_ARG  +"=" + UserArriveMode +"&" +
				USER_CAR_MAKE_ID + "=" + CarDataBeingEdited.MakeID + "&" +
				USER_CAR_MODEL_ID + "=" + CarDataBeingEdited.ModelID + "&" +
				USER_CAR_COLOR_ID + "=" + CarDataBeingEdited.ColorID);
		
		return UpdateTag;
	}
	
	public String MakeUpdateTagURL(String TagStr) throws UnsupportedEncodingException
	{
		
		String UpdateTag = new String(BASE_URL + USER_PAGE + "?" + 
								USER_PAGE_COMMAND_STRING + "=" + UPDATE_TAG + "&" +
								USER_TAG + "=" + URLEncoder.encode(TagStr,"utf-8"));
		return UpdateTag;
		
	}
	
	public String MakeUpdateTimeToLocationURL(Integer CurrentTimeToLocation)  throws UnsupportedEncodingException
	{
		String UpdateTimeToLocation = new String(BASE_URL + USER_PAGE + "?" + 
								USER_PAGE_COMMAND_STRING + "=" + UPDATE_TIME_TO_LOCATION + "&" +
								UPDATE_TIME_TO_LOCATION + "=" + 
								URLEncoder.encode(String.valueOf(CurrentTimeToLocation),"utf-8"));
		
		return UpdateTimeToLocation;
	}
	
	public HttpPost MakeUpdateCreditCardHTTPPost(String CardNumber, String ExpMonth,String ExpYear,String ZIP) throws UnsupportedEncodingException
	{
		
		HttpPost httppost = new HttpPost(BASE_URL + USER_PAGE);
		httppost.setHeader("Accept-Encoding", "UTF-8");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
		
		nameValuePairs.add(new BasicNameValuePair(USER_PAGE_COMMAND_STRING,UPDATE_CREDIT_CARD_COMMAND));
		
		nameValuePairs.add(new BasicNameValuePair(CREDIT_CARD_NUMBER,CardNumber));
		nameValuePairs.add(new BasicNameValuePair(CREDIT_CARD_EXP_MONTH,ExpMonth));
		nameValuePairs.add(new BasicNameValuePair(CREDIT_CARD_EXP_YEAR,ExpYear));
		nameValuePairs.add(new BasicNameValuePair(CREDIT_CARD_ZIP,ZIP));
		
		
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
		return httppost;
		
	}
	
	public String GetFoodDrinkOptionsCommandStringFromFoodDrink(HashMap<String,String> FoodDrink) 
	{
		String Result="";
		

		boolean FirstLoop=true;
		for (Map.Entry<String, String> entry : FoodDrink.entrySet())
		{
		
			if(entry.getKey().startsWith(FreewayCoffeeXMLHelper.PART_ID_PREFIX,0))
			{
				if(!FirstLoop)
				{
					// Use a dash to separate each set of options so we can use a , for multiple of each option
					Result = Result + "-";
				}
				// Thats kind of ugly!
				String OptionName = entry.getKey().substring(FreewayCoffeeXMLHelper.PART_ID_PREFIX.length());
			
				Result = Result + OptionName + "*" + entry.getValue();
				
				FirstLoop=false;
			}
		}
		return Result;
	}
		
	/* This will launch the Google navigation app, but lets the user select the transportation means before starting the navigation:

Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
Uri.parse("http://maps.google.com/maps/?daddr=my+street+address");
intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
startActivity(intent);

*/
	/*
	 * Start on the Google map:

Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
Uri.parse("geo:0,0?q=my+street+address");
intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
startActivity(intent);


	 */
	public String MakeLocationGoogleMapsURL()
	{
		//http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
		return "http://maps.google.com/maps?daddr=" + LocationData.get(FreewayCoffeeXMLHelper.USER_LOCATION_GPS_LAT_ATTR) + "," +
				LocationData.get(FreewayCoffeeXMLHelper.USER_LOCATION_GPS_LONG_ATTR);
	}
	public String MakeLoctionDetailText()
	{
		String Result = LocationData.get(FreewayCoffeeXMLHelper.USER_LOCATION_DESCR_ATTR) + "\n\n\n" + 
		                getString(R.string.fc_address) + "\n" + LocationData.get(FreewayCoffeeXMLHelper.USER_LOCATION_ADDRESS) + "\n\n" +
		                getString(R.string.fc_phone) +"\n" + LocationData.get(FreewayCoffeeXMLHelper.USER_LOCATION_PHONE_ATTR) + "\n\n" +
		                getString(R.string.fc_hours) + "\n" + LocationData.get(FreewayCoffeeXMLHelper.USER_LOCATION_HOURS) + "\n\n" +
		                getString(R.string.fc_email) + "\n" + LocationData.get(FreewayCoffeeXMLHelper.USER_LOCATION_EMAIL_ATTR) + "\n\n" +
		                getString(R.string.fc_gps) + ": " +  LocationData.get(FreewayCoffeeXMLHelper.USER_LOCATION_GPS_LAT_ATTR) + "/" +
		                LocationData.get(FreewayCoffeeXMLHelper.USER_LOCATION_GPS_LONG_ATTR);
		return Result;
		
	}
	
	public String MakeDeleteFoodDrinkURL(FoodDrink Type, Integer ID) throws UnsupportedEncodingException
	{
		if(Type.equals(FoodDrink.Drink))
		{
			return MakeDeleteDrinkURL(ID);
		}
		else
		{
			return "Crap";
		}
	}
	
	public String MakeDeleteDrinkURL(Integer DrinkID) throws UnsupportedEncodingException
	{
		String DrinkIDAsString = URLEncoder.encode(String.valueOf(DrinkID),"utf-8");
		
		String Delete;
		Delete = new String(BASE_URL + DRINK_ADD_EDIT_PAGE  + "?" + DRINK_ADD_EDIT_COMMAND_STRING + "=" + DELETE_DRINK_COMMAND + "&" +
				            DRINK_ID + "=" + DrinkIDAsString);
		return Delete;
		
	}
	

	
	
	public HttpPost MakeAddEditDrinkURL(FreewayCoffeeUserDrink Drink)  throws UnsupportedEncodingException
	{
		// Base URL + Command (add_drink or edit_drink)
		// TODO Fix hardcoded "id"
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		HttpPost httppost = new HttpPost(BASE_URL + DRINK_ADD_EDIT_PAGE);
		httppost.setHeader("Accept-Encoding", "UTF-8");

		//String AddEdit;
		if(Drink.GetUserDrinkID()==null)
		{
			// No Drink ID, It must be new
			nameValuePairs.add(new BasicNameValuePair(DRINK_ADD_EDIT_COMMAND_STRING,DRINK_ADD_EDIT_COMMAND_ADD ));
					
		}
		else
		{
			nameValuePairs.add(new BasicNameValuePair(DRINK_ADD_EDIT_COMMAND_STRING,DRINK_ADD_EDIT_COMMAND_EDIT));
			nameValuePairs.add(new BasicNameValuePair(USER_DRINK_DRINK_ID, String.valueOf(Drink.GetUserDrinkID()) ));
			
		}
		nameValuePairs.add(new BasicNameValuePair(DRINK_ADD_EDIT_SCHEMA_STRING,DRINK_ADD_EDIT_SCHEMA));
		

		// Drink Type
		// TODO Error check might well be nice here.
		nameValuePairs.add(new BasicNameValuePair(FreewayCoffeeXMLHelper.USER_DRINK_TYPE_ID, String.valueOf(Drink.GetDrinkTypeID()) ));
		
		if( (Drink.GetUserDrinkExtra()!=null) && (!Drink.GetUserDrinkExtra().equals("")))
		{
			nameValuePairs.add(new BasicNameValuePair(FreewayCoffeeXMLHelper.USER_DRINK_EXTRA_OPTIONS_ATTR,Drink.GetUserDrinkExtra()));
		}

		// Drink Name
		
		if( (Drink.GetUserDrinkName()!=null) && (!Drink.GetUserDrinkName().equals("")) )
		{
			nameValuePairs.add(new BasicNameValuePair(FreewayCoffeeXMLHelper.USER_DRINK_NAME_ATTR,Drink.GetUserDrinkName()));
		}
		
		
		// Always Include
		if(Drink.GetIncludeDefault()==true)
		{
			nameValuePairs.add(new BasicNameValuePair(FreewayCoffeeXMLHelper.USER_DRINK_INCLUDE_DEFAULT_ATTR,"1"));
		}
		else
		{
			nameValuePairs.add(new BasicNameValuePair(FreewayCoffeeXMLHelper.USER_DRINK_INCLUDE_DEFAULT_ATTR,"0"));
		}
		
		Drink.AddOptionsListToPost(nameValuePairs);
		
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
		return httppost;
		
	}
	
	public String MakeLastErrorText() 
	{
		if(LastError.size()==0)
		{
			return getString(R.string.none_text);
		}
		
		String Result = getString(R.string.error_code_major) +  LastError.get(FreewayCoffeeXMLHelper.ERROR_CODE_MAJOR) +
						"\n" + getString(R.string.error_code_minor) + LastError.get(FreewayCoffeeXMLHelper.ERROR_CODE_MINOR) +
						"\n\n" + getString(R.string.error_message) + LastError.get(FreewayCoffeeXMLHelper.ERROR_LONG_TEXT);
		
		
		return Result;
	}
	
	public void SetLastError(Integer CodeMajor, Integer CodeMinor,String Message,String DisplayString)
	{
		LastError.clear();
		LastError.put(FreewayCoffeeXMLHelper.ERROR_CODE_MAJOR, String.valueOf(CodeMajor));
		LastError.put(FreewayCoffeeXMLHelper.ERROR_CODE_MINOR,String.valueOf(CodeMinor));
		LastError.put(FreewayCoffeeXMLHelper.ERROR_LONG_TEXT,Message);
		LastError.put(FreewayCoffeeXMLHelper.ERROR_DISPLAY_TEXT, DisplayString);
		
	}
	
	public String GetLastErrorDisplayString()
	{
		return LastError.get(FreewayCoffeeXMLHelper.ERROR_DISPLAY_TEXT);
	}
	
	public String MakeLastErrorEncodedText()  throws UnsupportedEncodingException
	{
		if(LastError.size()==0)
		{
			return getString(R.string.none_text);
		}
		
		String Result ="";
		for(Map.Entry<String, String> Entry : LastError.entrySet())
		{
			Result += Entry.getKey() + ": " + Entry.getValue() + " ";
		}
		
		return URLEncoder.encode(Result,"utf-8");
	}
}
