package com.freeewaycoffee.ordermanager;

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

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.text.InputFilter;
import android.text.Spanned;

public class FCOrderManagerApp extends Application 
{
	public static final String HOST_NAME_DEBUG="nickambr.ipower.com";
	
	private static final String BASE_URL="https://nickambr.ipower.com/fc/";
	//private static final String BASE_URL="https://secure107.inmotionhosting.com/~freewa7/fc/";

	// RELEASE
	public static final String HOST_NAME_RELEASE="secure107.inmotionhosting.com";
	
	// App Key
	private static final String FC_OM_APP_PREFS="fc_om_prefs";
		
	// Pref Name Keys
	private static final String FC_PREF_NETWORK_CONNECT_TIMEOUT_KEY="fc_om_network_connect_timeout";
	private static final String FC_PREF_NETWORK_DATA_TIMEOUT_KEY="fc_om_network_data_timeout";
	private static final String FC_PREF_LOGIN_NAME="fc_om_login_name";
	private static final String FC_PREF_USER_PASSWORD="fc_om_user_password";
	private static final String FC_PREF_ORDER_UPDATE_INTERVAL_KEY="fc_om_order_update_interval";
	private static final String FC_PREF_HIDE_COMPLETED_ORDERS_KEY="fc_om_hide_completed_orders";
	private static final String FC_PREF_GUI_UPDATE_INTERVAL_KEY="fc_om_gui_update_interval";
	
	// Pref default Values
	private static final Integer FC_PREF_NETWORK_CONNECT_TIMEOUT_DEFAULT=30;
	private static final Integer FC_PREF_NETWORK_DATA_TIMEOUT_DEFAULT=30;
	private static final Integer FC_PREF_ORDER_UPDATE_INTERVAL_DEFAULT=2;
	private static final boolean  FC_PREF_HIDE_COMPLETED_ORDERS_DEFAULT=true;
	private static final Integer FC_PREF_GUI_UPDATE_INTERVAL_DEFAULT=5;
	
	
	// PAGES
	private static final String OM_SIGNON_PAGE="fc_om_signon.php";
	
	private static final String OM_ORDERS_PAGE="fc_om_orders.php";
	
	private static final String OM_USER_COMMAND="om_user_command";
	
	private static final String USER_COMMAND_OM_GET_TODAYS_ORDERS_FOR_LOG="get_todays_orders_for_loc";
	private static final String USER_COMMAND_OM_GET_ORDERS_AFTER_ORDER_ID_FOR_LOC="get_orders_after_id_for_loc";
	
	private static final String USER_COMMAND_OM_GET_TODAYS_ORDERS_AND_HERE_DATA="o_loc_and_here";
	private static final String USER_COMMAND_OM_GET_ORDERS_FOR_LOC_AFTER_ORDER_AND_HERE_DATA="o_loc_ord_and_here";
	
    // COMMAND PARAMS
    
	private static final String OM_LOCATION_ID_CMD_PARAM="location_id";
	private static final String OM_ORDER_ID_CMD_PARAM="order_id";
	private Integer m_HighestOrderID;
	
	// SIGNON
	private static final String SIGNON_EMAIL="signon_user_email";
	private static final String SIGNON_PW="signon_user_password";
		
	//private DefaultHttpClient httpClient;
	private FCOrderManagerHTTPClient httpClient;
	private FCOrderManagerHTTP FCHTTP;
	private SharedPreferences AppPrefs;
	private HashMap<Integer,FCOrderManagerOrder> m_OrderList;
	private HashMap<String,String> m_UserInfoData;
	private HashMap<String,String> m_LocationData;
	
	private FCOrderManagerError LastError;
	
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
		m_OrderList = new HashMap<Integer,FCOrderManagerOrder>();
		m_UserInfoData = new HashMap<String,String>();
		m_LocationData = new HashMap<String,String>();
		m_HighestOrderID=-1;
	}
	
	public void ClearAllDownloadedData()
	{
		m_OrderList.clear();
		m_HighestOrderID=-1;
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
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair(SIGNON_EMAIL,Email));
		nameValuePairs.add(new BasicNameValuePair(SIGNON_PW,Password));
	
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		return httppost;
		
	}
	
	// PREFERENCES
	
	
	
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

	public HashMap<String,String> GetUserInfoData()
	{
		return m_UserInfoData;
	}
	
	public HashMap<String,String> GetUserLocationData()
	{
		return m_LocationData;
	}
	
	public HashMap<Integer,FCOrderManagerOrder> GetOrderList()
	{
		return m_OrderList;
	}
	
	
	public String MakeGetTodaysOrdersForLocationAndHere(String Location)
	{
		String URL = new String(BASE_URL + OM_ORDERS_PAGE  + "?" + OM_USER_COMMAND + "=" + USER_COMMAND_OM_GET_TODAYS_ORDERS_AND_HERE_DATA + "&" +
				OM_LOCATION_ID_CMD_PARAM + "=" + Location);
		return URL;
	}
	public String MakeGetTodaysOrdersForLocation(String Location)
	{
		String URL = new String(BASE_URL + OM_ORDERS_PAGE  + "?" + OM_USER_COMMAND + "=" + USER_COMMAND_OM_GET_TODAYS_ORDERS_FOR_LOG + "&" +
				OM_LOCATION_ID_CMD_PARAM + "=" + Location);
		return URL;
	}
	
	public String MakeGetOrdersAfterOrderIDForLocation(String Location, Integer OrderID)
	{
		String URL = new String(BASE_URL + OM_ORDERS_PAGE  + "?" + OM_USER_COMMAND + "=" + USER_COMMAND_OM_GET_ORDERS_AFTER_ORDER_ID_FOR_LOC + "&" +
				OM_LOCATION_ID_CMD_PARAM + "=" + Location + "&" +OM_ORDER_ID_CMD_PARAM + "=" + OrderID);
		return URL;
	}
	
	public String MakeGetOrdersAfterOrderIDForLocationAndHere(String Location, Integer OrderID)
	{
		String URL = new String(BASE_URL + OM_ORDERS_PAGE  + "?" + OM_USER_COMMAND + "=" + USER_COMMAND_OM_GET_ORDERS_FOR_LOC_AFTER_ORDER_AND_HERE_DATA + "&" +
				OM_LOCATION_ID_CMD_PARAM + "=" + Location + "&" +OM_ORDER_ID_CMD_PARAM + "=" + OrderID);
		return URL;
	}
	
	public void AddOrder(FCOrderManagerOrder Order)
	{
		m_OrderList.put(Order.GetOrderID(),Order);
		if(Order.GetOrderID().compareTo(m_HighestOrderID)>0)
		{
			m_HighestOrderID = Order.GetOrderID();
		}
	}
	
	public void ProcessCustomerHereItem(FCOrderManagerCustomerHereItem Item)
	{
		FCOrderManagerOrder Order= m_OrderList.get(Item.GetOrderID());
		
		if(Order!=null)
		{
			Order.SetTimeUserHere(Item.GetTimeHere());
		}
	}
	
	public Integer GetHighestOrderID()
	{
		return m_HighestOrderID;
	}
	
	public String GetUserLocationID()
	{
		return m_LocationData.get(FCOrderManagerXMLHelper.ID_ATTR);
	}
	
	boolean NeedDownloadAll()
	{
		if(m_HighestOrderID==-1)
		{
			return true;
		}
		return false;
	}

}
