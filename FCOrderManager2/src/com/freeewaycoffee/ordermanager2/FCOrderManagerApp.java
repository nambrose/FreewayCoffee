package com.freeewaycoffee.ordermanager2;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.freewaycoffee.clientobjlib.*;


public class FCOrderManagerApp extends Application 
{
	// PROD
	//private static final String BASE_URL="https://freecoffapp.com/fc/";
	
	private static final String BASE_URL_PROD="https://freecoffapp.com/fc/";

	// DEV
	private static final String BASE_URL="https://freecoffapp.com/fc_dev2/";

	// RELEASE
	
	public static final String HOST_NAME="freecoffapp.com";
	
	// App Key
	private static final String FC_OM_APP_PREFS="fc_om_prefs";
		
	private static final String APP_CLIENT_TYPE="app_client";
	private static final String APP_CLIENT_VALUE="android";
	
	private static final String PHONE_MODEL="phone_model";
	private static final String ANDROID_VERSION="android_version";
	private static final String PHONE_MANUF="phone_manuf";
	private static final String PHONE_PRODUCT="phone_product";
	
	private static final String FC_VERSION_NAME="version_name";
	private static final String FC_VERSION_NUM="version_num";
	
	// Pref Name Keys
	private static final String FC_PREF_NETWORK_CONNECT_TIMEOUT_KEY="fc_om_network_connect_timeout";
	private static final String FC_PREF_NETWORK_DATA_TIMEOUT_KEY="fc_om_network_data_timeout";
	private static final String FC_PREF_LOGIN_NAME="fc_om_login_name";
	private static final String FC_PREF_USER_PASSWORD="fc_om_user_password";
	private static final String FC_PREF_ORDER_UPDATE_INTERVAL_KEY="fc_om_order_update_interval";
	private static final String FC_PREF_HIDE_COMPLETED_ORDERS_KEY="fc_om_hide_completed_orders";
	private static final String FC_PREF_HIDE_TEST_ORDERS_KEY="fc_om_hide_test_orders";
	private static final String FC_PREF_VIBRATE_ON_ORDERS="fc_om_vibrate_orders";
	private static final String FC_PREF_SOUND_ON_ORDERS="fc_om_sound_orders";
	
	
	
	private static final String FC_PREF_GUI_UPDATE_INTERVAL_KEY="fc_om_gui_update_interval";
	
	// Pref default Values
	private static final Integer FC_PREF_NETWORK_CONNECT_TIMEOUT_DEFAULT=30;
	private static final Integer FC_PREF_NETWORK_DATA_TIMEOUT_DEFAULT=30;
	private static final Integer FC_PREF_ORDER_UPDATE_INTERVAL_DEFAULT=2;
	private static final boolean  FC_PREF_HIDE_COMPLETED_ORDERS_DEFAULT=true;
	private static final Integer FC_PREF_GUI_UPDATE_INTERVAL_DEFAULT=5;
	private static boolean FC_PREF_VIBRATE_ON_ORDERS_DEFAULT=true;
	private static boolean FC_PREF_SOUND_ON_ORDERS_DEFAULT=true;
	private static boolean FC_PREF_HIDE_TEST_ORDERS_DEFAULT=true;
	
	// PAGES
	private static final String OM_SIGNON_PAGE="fc_om_signon.php";
	
	private static final String OM_ORDERS_PAGE="fc_om_orders.php";
	
	private static final String OM_USER_COMMAND="om_user_command";
	
	//private static final String USER_COMMAND_OM_GET_ORDERS_SINCE_TIMESTAMP="o_loc_after_ts";
	private static final String USER_COMMAND_OM_GET_ORDERS_SINCE_TAG="o_loc_after_tag";
	
	private static final String USER_COMMAND_OM_GET_TODAYS_FOR_LOC="o_loc_today";
	
	private static final String USER_COMMAND_UPDATE_LOCATION_OPEN_MODE = "update_loc_open_mode";
	
    // COMMAND PARAMS
    
	private static final String OM_LOCATION_ID_CMD_PARAM="location_id";
	private static final String OM_ORDER_ID_CMD_PARAM="order_id";
	//private static final String OM_TIMESTAMP_CMD_PARAM="ts";
	private static final String OM_GLOBAL_ORDER_TAG_CMD_PARAM="o_tag";

	private static final String OM_ORDER_DELIVER_CMD_PARAM="order_delivered";	
	private static final String OM_ORDER_NOSHOW_CMD_PARAM="order_noshow";
	private static final String OM_ORDER_REFUND_CMD_PARAM="order_refund";
	
	private static final String OM_INCARNATION_CMD_PARAM="incarnation";
	private static final String OM_LOCATION_OPEN_MODE_CMD_PARAM=FCLocation.USER_LOCATION_OPEN_MODE_ATTR;
	
	private Long m_HighestGlobalOrderTag;
	
	//private Integer m_HighestOrderID;
	
	// SIGNON
	private static final String SIGNON_EMAIL="signon_user_email";
	private static final String SIGNON_PW="signon_user_password";
		
	//private DefaultHttpClient httpClient;
	private FCOrderManagerHTTPClient httpClient;
	private FCOrderManagerHTTP FCHTTP;
	private SharedPreferences AppPrefs;
	private HashMap<Integer,FCOrder> m_OrderList;
	
	private FCUserData m_UserData;
	private FCLocation m_LocationData;
	
	//private HashMap<String,String> m_UserInfoData;
	//private HashMap<String,String> m_LocationData;
	
	private FCOrderManagerError LastError;
	private ArrayList<FCOrderManagerRequest> m_RequestQueue;
	//private String m_HighestOrderTimestamp;
	
	public void onCreate()
	{
		Initialize();
	}
	private void Initialize()
	{
		AppPrefs = getSharedPreferences(FC_OM_APP_PREFS,Activity.MODE_PRIVATE);
		InitializeHttpClient();
		FCHTTP = new FCOrderManagerHTTP(this);
		LastError=null;
		m_OrderList = new HashMap<Integer,FCOrder>();
		m_UserData = null;
		//m_UserInfoData = new HashMap<String,String>();
		//m_LocationData = new HashMap<String,String>();
		m_LocationData=null;
		ClearHighestGlobalOrderTag();
		
		m_RequestQueue  = new ArrayList<FCOrderManagerRequest>();
	}
	
	public void ClearAllDownloadedData()
	{
		m_OrderList.clear();
		ClearHighestGlobalOrderTag();
		m_UserData=null;
		m_LocationData=null;
		//m_UserInfoData.clear();
		//m_LocationData.clear();
	}
	
	public FCOrderManagerError GetLastError()
	{
		return LastError;
	}
	
	public void AddError(FCOrderManagerError Error)
	{
		LastError=Error;
	}
	
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
	
	
	public FCOrderManagerHTTPClient GetHttpClient()
	{
		return httpClient;
	}
		
	public FCOrderManagerHTTP GetHTTP()
	{
		return FCHTTP;
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
		httpClient = new  FCOrderManagerHTTPClient(this,httpParameters);
		httpClient.getParams().setParameter("http.protocol.content-charset", "UTF-8");

		
	}

	public HttpPost MakeSignonHTTPPost(String Email, String Password) throws UnsupportedEncodingException
	{
		HttpPost httppost = new HttpPost(BASE_URL + OM_SIGNON_PAGE);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(SIGNON_EMAIL,Email));
		nameValuePairs.add(new BasicNameValuePair(SIGNON_PW,Password));
		
		nameValuePairs.add(new BasicNameValuePair(FCXMLHelper.ATTR_MAIN_SCHEMA_COMPAT_LEVEL,
				String.valueOf(FCOrderManagerXMLHandler.MAIN_COMPAT_SCHEMA_REV)) );
		
		nameValuePairs.add(new BasicNameValuePair(APP_CLIENT_TYPE,APP_CLIENT_VALUE));
		nameValuePairs.add(new BasicNameValuePair(PHONE_MODEL,android.os.Build.MODEL));
		nameValuePairs.add(new BasicNameValuePair(ANDROID_VERSION,android.os.Build.VERSION.RELEASE));
		nameValuePairs.add(new BasicNameValuePair(PHONE_MANUF,android.os.Build.MANUFACTURER));
		nameValuePairs.add(new BasicNameValuePair(PHONE_PRODUCT,android.os.Build.PRODUCT));
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
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		return httppost;
		
	}
	
	// PREFERENCES
	
	public void ClearHighestGlobalOrderTag()
	{
		m_HighestGlobalOrderTag=Long.valueOf(0);
	}
	
	public void UpdateHighestGlobalOrderTag(Long Tag)
	{
		if(Tag.compareTo(m_HighestGlobalOrderTag)>0)
		{
			m_HighestGlobalOrderTag=Tag;
		}
	}
	
	public Long GetHighestGlobalOrderTag()
	{
		return m_HighestGlobalOrderTag;
	}
	
	public boolean GetPreferenceVibrateOnOrders()
	{
		boolean Return = AppPrefs.getBoolean(FC_PREF_VIBRATE_ON_ORDERS, FC_PREF_VIBRATE_ON_ORDERS_DEFAULT);
		return Return;
	}
	
	public boolean GetPreferenceSoundOnOrders()
	{
		boolean Return = AppPrefs.getBoolean(FC_PREF_SOUND_ON_ORDERS, FC_PREF_SOUND_ON_ORDERS_DEFAULT);
		return Return;
	}
	public void SetPreferenceVibrateOnOrders(boolean VibrateFlag)
	{
		SharedPreferences.Editor editor = AppPrefs.edit();
		editor.putBoolean(FC_PREF_VIBRATE_ON_ORDERS, VibrateFlag);
		editor.commit();
	}
	public void SetPreferenceSoundOnOrders(boolean SoundFlag)
	{
		SharedPreferences.Editor editor = AppPrefs.edit();
		editor.putBoolean(FC_PREF_SOUND_ON_ORDERS, SoundFlag);
		editor.commit();
	}
	
	public boolean GetPreferenceHideCompletedOrders()
	{
		boolean Return = AppPrefs.getBoolean(FC_PREF_HIDE_COMPLETED_ORDERS_KEY, FC_PREF_HIDE_COMPLETED_ORDERS_DEFAULT);
		return Return;
	}
	
	public void SetPreferenceHideCompletedOrders(boolean HideFlag)
	{
		SharedPreferences.Editor editor = AppPrefs.edit();
		editor.putBoolean(FC_PREF_HIDE_COMPLETED_ORDERS_KEY, HideFlag);
		editor.commit();
	}
	
	public boolean GetPreferenceHideTestOrders()
	{
		boolean Return = AppPrefs.getBoolean(FC_PREF_HIDE_TEST_ORDERS_KEY, FC_PREF_HIDE_TEST_ORDERS_DEFAULT);
		return Return;
	}
	
	
	public void SetPreferenceHideTestOrders(boolean HideFlag)
	{
		SharedPreferences.Editor editor = AppPrefs.edit();
		editor.putBoolean(FC_PREF_HIDE_TEST_ORDERS_KEY, HideFlag);
		editor.commit();
	}
	
	public String GetUserLoginName()
	{
		return AppPrefs.getString(FC_PREF_LOGIN_NAME,"");
	}
	
	public String GetUserPassword()
	{
		return AppPrefs.getString(FC_PREF_USER_PASSWORD,"");
	}
	
	public void SetUserLoginName(String Login)
	{
		SharedPreferences.Editor editor = AppPrefs.edit();
		editor.putString(FC_PREF_LOGIN_NAME, Login);
		editor.commit();
	}
	public void SetUserPassword(String Password)
	{
		SharedPreferences.Editor editor = AppPrefs.edit();
		editor.putString(FC_PREF_USER_PASSWORD, Password);
		editor.commit();
	}
	
	public Integer GetPreferenceOrderUpdateIntervalInMillisec()
	{
		Integer TimeoutInSec = AppPrefs.getInt(FC_PREF_ORDER_UPDATE_INTERVAL_KEY,FC_PREF_ORDER_UPDATE_INTERVAL_DEFAULT);
		return TimeoutInSec*1000;
	}
	
	public void SetPreferenceOrderUpdateIntervalInSeconds(Integer Timeout)
	{
		SharedPreferences.Editor editor = AppPrefs.edit();
		editor.putInt(FC_PREF_ORDER_UPDATE_INTERVAL_KEY, Timeout);
		editor.commit();
	}
	
	public Integer GetPreferenceGUIUpdateIntervalInMillisec()
	{
		Integer TimeoutInSec = AppPrefs.getInt(FC_PREF_GUI_UPDATE_INTERVAL_KEY,FC_PREF_GUI_UPDATE_INTERVAL_DEFAULT);
		return TimeoutInSec*1000;
	}
	public void SetPreferenceGUIUpdateIntervalInSeconds(Integer Interval)
	{
		SharedPreferences.Editor editor = AppPrefs.edit();
		editor.putInt(FC_PREF_GUI_UPDATE_INTERVAL_KEY, Interval);
		
		editor.commit();
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
	*/
	
/*
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
*/
	/*
	InputFilter GetEditTextEmailInputFilter()
	{
		InputFilter filter = new InputFilter() 
		{ 
			public CharSequence filter(CharSequence source, int start, int end, 
					Spanned dest, int dstart, int dend) 
			{ 

				//String Result="";
				int i;
		        for (i = start; i < end; i++)
		        {
		        	if (Character.isLetterOrDigit(source.charAt(i))) 
					{
		        		//Result+= source.charAt(i);
		        		return null;
					}
		        	
					if (source.charAt(i)=='@')
					{
						return null;
						//Result+= source.charAt(i);
					}
					
		            if(source.charAt(i)=='.')
		            {
		            	return null;
		            }
		            if(source.charAt(i)=='+')
		            {
		            	return null;
		            }
		            
		        }

		        return "";
		        
				
			}
		}; 
		return filter;

	}
*/
	public FCUserData GetUserData()
	{
		return m_UserData;
	}
	
	public void SetUserData(FCUserData Data)
	{
		
		m_UserData = Data; // Careful, this is a reference any changes to orignal and whammo !
		
	}
	/*
	public HashMap<String,String> GetUserInfoData()
	{
		return m_UserInfoData;
	}
	*/
	
	public void SetUserLocationData(FCLocation Location)
	{
		m_LocationData = Location; // Careful, they both point to the same data
	}
	public FCLocation GetUserLocationData()
	{
		return m_LocationData;
	}
	/*
	public HashMap<String,String> GetUserLocationData()
	{
		return m_LocationData;
	}
	
	*/
	public HashMap<Integer,FCOrder> GetOrderList()
	{
		return m_OrderList;
	}
	
	public boolean IsDemoSystem()
	{
		if(BASE_URL.equalsIgnoreCase(BASE_URL_PROD))
		{
			return false;
		}
		return true;
	}
	public HttpPost MakeSetOrderStatusRequest(FCOrder Order,Integer Status)throws UnsupportedEncodingException
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		HttpPost httppost = new HttpPost(BASE_URL + OM_ORDERS_PAGE);
		switch(Status)
		{
		case FCOrder.ORDER_DELIVERED:
			nameValuePairs.add(new BasicNameValuePair(OM_USER_COMMAND,OM_ORDER_DELIVER_CMD_PARAM));
			break;
		case FCOrder.ORDER_NOSHOW:
			nameValuePairs.add(new BasicNameValuePair(OM_USER_COMMAND,OM_ORDER_NOSHOW_CMD_PARAM));
			break;
		case FCOrder.ORDER_REFUNDED:
			nameValuePairs.add(new BasicNameValuePair(OM_USER_COMMAND,OM_ORDER_REFUND_CMD_PARAM));
			break;
			default:
				return null;
		}
		nameValuePairs.add(new BasicNameValuePair(OM_ORDER_ID_CMD_PARAM,String.valueOf(Order.GetOrderID())));
		nameValuePairs.add(new BasicNameValuePair(OM_INCARNATION_CMD_PARAM,String.valueOf(Order.GetIncarnation())));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
		return httppost;
		
		
	}
	
	public HttpPost MakeGetTodaysOrdersForLocation(String Location) throws UnsupportedEncodingException
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		HttpPost httppost = new HttpPost(BASE_URL + OM_ORDERS_PAGE);
		nameValuePairs.add(new BasicNameValuePair(OM_USER_COMMAND,USER_COMMAND_OM_GET_TODAYS_FOR_LOC));
		nameValuePairs.add(new BasicNameValuePair(OM_LOCATION_ID_CMD_PARAM,Location));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
		return httppost;
		
	}
		
	/*
	public HttpPost MakeGetOrdersAfterTimestampForLocation(String Location) throws UnsupportedEncodingException
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		HttpPost httppost = new HttpPost(BASE_URL + OM_ORDERS_PAGE);
		nameValuePairs.add(new BasicNameValuePair(OM_USER_COMMAND,USER_COMMAND_OM_GET_ORDERS_SINCE_TIMESTAMP));
		nameValuePairs.add(new BasicNameValuePair(OM_LOCATION_ID_CMD_PARAM,Location));
		nameValuePairs.add(new BasicNameValuePair(OM_TIMESTAMP_CMD_PARAM,String.valueOf(GetHighestOrderTimestamp())));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
		return httppost;
		
	}
	*/
	
	public HttpPost MakeUpdateLocationOpenMode(Integer LocationID,Integer Mode) throws UnsupportedEncodingException
	{
		String Location = String.valueOf(LocationID);
		String ModeStr = String.valueOf(Mode);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		HttpPost httppost = new HttpPost(BASE_URL + OM_ORDERS_PAGE);
		nameValuePairs.add(new BasicNameValuePair(OM_USER_COMMAND,USER_COMMAND_UPDATE_LOCATION_OPEN_MODE));
		nameValuePairs.add(new BasicNameValuePair(OM_LOCATION_OPEN_MODE_CMD_PARAM,ModeStr));
		nameValuePairs.add(new BasicNameValuePair(OM_LOCATION_ID_CMD_PARAM,Location));
		
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
		return httppost;
		
	}
	
	public HttpPost MakeGetOrdersAfterTagForLocation(String Location) throws UnsupportedEncodingException
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		HttpPost httppost = new HttpPost(BASE_URL + OM_ORDERS_PAGE);
		nameValuePairs.add(new BasicNameValuePair(OM_USER_COMMAND,USER_COMMAND_OM_GET_ORDERS_SINCE_TAG));
		nameValuePairs.add(new BasicNameValuePair(OM_LOCATION_ID_CMD_PARAM,Location));
		nameValuePairs.add(new BasicNameValuePair(OM_GLOBAL_ORDER_TAG_CMD_PARAM,String.valueOf(GetHighestGlobalOrderTag())));
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
		return httppost;
		
	}
	
	public void DeleteOrderByID(Integer OrderID)
	{
		m_OrderList.remove(OrderID);
	}
	
	public void AddOrder(FCOrder Order)
	{
		m_OrderList.put(Order.GetOrderID(),Order);
	}
	
	public void ProcessCustomerHereItem(FCOrderManagerCustomerHereItem Item)
	{
		FCOrder Order= m_OrderList.get(Item.GetOrderID());
		
		if(Order!=null)
		{
			Order.SetTimeUserHere(Item.GetTimeHere());
		}
	}
	
	public Integer GetUserLocationID()
	{
		if(m_LocationData==null)
		{
			return null;
		}
		return m_LocationData.GetLocationID();
	}
	
	boolean NeedDownloadAll()
	{
		if(m_OrderList.size()==0)
		{
			return true;
		}
		// Just in case we get some orders but for some reason no tag ... just re-download and hope we get it.
		if(m_HighestGlobalOrderTag==null)
		{
			return true;
		}
		if (m_HighestGlobalOrderTag.compareTo(Long.valueOf(0))<=0)
		{
			return true;
		}
		 
		return false;
	}

	//m_RequestQueue  = new ArrayList<FCOrderManagerRequest>();
	public void AddRequest(FCOrderManagerRequest Request)
	{
		m_RequestQueue.add(Request);
	}
	
	public void RemoveFirstRequest()
	{
		if(m_RequestQueue.size()>0)
		{
			m_RequestQueue.remove(0);
		}
	}
	
	public int GetRequestQueueSize()
	{
		return m_RequestQueue.size();
	}
	
	public FCOrderManagerRequest GetFirstRequestAndMarkInProgress()
	{
		if(m_RequestQueue.size()>0)
		{
			FCOrderManagerRequest Request= m_RequestQueue.get(0);
			Request.SetInProgress();
			return Request;
		}
		return null;
	}
}
