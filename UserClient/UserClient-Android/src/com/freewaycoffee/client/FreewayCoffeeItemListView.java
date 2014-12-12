package com.freewaycoffee.client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.freewaycoffee.clientobjlib.FCUserData;
import com.freewaycoffee.clientobjlib.FCXMLHelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;


public class FreewayCoffeeItemListView extends Activity implements View.OnClickListener
{
	static final private int MENU_ABOUT = Menu.FIRST;
	static final private int MENU_FEEDBACK = Menu.FIRST+1;
	static final private int MENU_VIEW_ORDER=Menu.FIRST+2;
	static final private int MENU_LAST_ERROR=Menu.FIRST+3;
	static final private int MENU_REPORT=Menu.FIRST+4;
	
	
	// Alert Dialog IDs. Eventually port this to Fragments I suppose.
	
	
	// Used to pass drink ID to drink picker activity
	public static String USER_ITEM_SELECTED_DRINK_ID="user_item_selected_drink_id";
	public static String USER_ITEM_SELECTED_FOOD_DRINK_POS="user_selected_food_drink_pos";
	public static String FC_ITEM_LIST_VIEW_SUB_AC_TYPE="fc_item_list_view_sub_ac_type";
	public static String USER_ADD_DRINK_TYPE="user_add_drink_type";
	public static String USER_EDIT_DRINK_ID="user_edit_drink_id";
	
	public static String USER_SELECT_DRINK_OPTION_ID="user_select_drink_option";
	public static String USER_SELECT_FOOD_OPTION_ID="user_select_food_option";
	
	public static String ERROR_ORDER_INTENT_ID="error_order_string";
	
	public static String PARENT_LIST_POSITION="parent_list_position";
	public static String USER_SELECTED_DRINK_OPTION_DATA_ID="user_selected_drink_option_data_id";
	public static String USER_SELECTED_FOOD_OPTION_DATA_ID="user_selected_food_option_data_id";
	public static String DRINK_TYPE="drink_type";
	public static String FOOD_TYPE="food_type";
	
	public static String INTENT_KEY_USER_LOCATION_STRING="intent_key_user_location_string";
	public static String INTENT_KEY_TIME_TO_LOCATION="intent_key_time_to_location";
	
	public static String ACTIVITY_COMMAND_CODE="activity_command";
	public static String ACTIVITY_FOOD_CATEGORY="food_category";
	
	
	public static Integer RESULT_CODE_NOT_LOGGED_IN = RESULT_FIRST_USER+1;
	
	public enum SubActivityTypes
	{
		FC_SUB_AC_CHOOSE_FOOD_DRINK,
		FC_SUB_AC_CHOOSE_DRINK_BYPASS_DRINK_PICK, // Used for when user has no drinks and we go straight to the AddDrink window
		FC_SUB_AC_CHOOSE_CARD,
		FC_SUB_AC_CHOOSE_LOCATION,
		FC_SUB_AC_CHOOSE_TAG,
		FC_SUB_AC_CHOOSE_TIME_TO_LOCATION,
		FC_SUB_AC_SHOW_ORDER,
		FC_SUB_ACTIVITY_EDIT_DRINK,
		FC_SUB_ACTIVITY_ADD_DRINK,
		FC_SUB_ACTIVITY_ADD_FOOD,
		FC_SUB_ACTIVITY_EDIT_FOOD
	}
	
	
	// Dialogs
	private static final int ORDER_DIALOG=1;
	static final private int ALERT_DIALOG_NO_CREDIT_CARD=2;
	static final private int ALERT_DIALOG_NO_ITEMS=3;
	static final private int ALERT_DIALOG_ORDER_TOO_SOON=4;
	static final private int ALERT_DIALOG_NO_COST=5;
	static final private int ALERT_DIALOG_NO_CAR=6;
	
	private ArrayList<FreewayCoffeeItemListIndexPair> ListIndex;
	
	
	
	public static final String USER_INFO_NAME_KEY="user_name";
	
	public static final String TYPE_STRING="type";
	public static final String USER_TIME_TO_LOCATION="user_time_to_location";

	public static final String USER_TIME_TO_LOCATION_TYPE="user_time_to_location";
	
	// Types for list elements. For now not "android" list types as they all use same layot. May need to change this. TODO
	public static final String USER_LOCATION_TYPE="user_location";
	public static final String USER_CREDIT_CARD_TYPE="user_credit_card";
	public static final String USER_FOOD_TYPE="user_food";
	public static final String USER_DRINK_TYPE="user_drink";
	
	    
	public static final String ITEM_SEPARATOR_TYPE="separator";
	public static final String CHOOSE_ITEM_TYPE="choose_item";
	public static final String USER_TAG_TYPE="user_tag";
	public static String CARD_CHARGED_AMOUNT_TYPE="card_charged_amount";
	
	
	public static final String USER_TAG_ATTR="user_tag";
	
	public static final String USER_CHOSE_DRINK_ID="user_chose_drink_id";
	public static final String USER_CHOSE_FOOD_ID="user_chose_food_id";
	
	public static final String USER_ADD_FOOD_TYPE="user_add_food_type";
	public static final String USER_EDIT_FOOD_ID="user_edit_food_type";
	
	
	public static final String CREDIT_CARD_NONE="NONE";
	public static final String LOCATION_NONE="NONE";
	
	private FreewayCoffeeApp appState;
	private FreewayCoffeeXMLHandler ListViewXMLHandler;
	private ListView listView;
	private FCListAdapter myAdapter;
	
	private FreewayCoffeeMakeOrderAsyncGet OrderAsyncGet;
	private FreewayCoffeeItemListAsyncGet AsyncGet;
	
	private ProgressDialog ItemsListProgress;
	private ProgressDialog MakeOrderProgress;
	
	private BigDecimal m_OrderItemsTotal;
	private BigDecimal m_OrderTotal;
	private BigDecimal m_OrderDiscount;
	private boolean m_IsOrderFreeDueToDiscounts;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		appState = ((FreewayCoffeeApp)getApplicationContext());
		setContentView(R.layout.itemlist);
		ItemsListProgress=null;
		Object retained = getLastNonConfigurationInstance();
		
		listView = (ListView)findViewById(R.id.fc_item_list);
	     
        View footerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fc_itemlist_footer, null, false);
        listView.addFooterView(footerView);
        
        m_IsOrderFreeDueToDiscounts = false;
        
        Button OrderBut = (Button)findViewById(R.id.itemlist_make_order);
        if(OrderBut!=null)
        {
        	if(!FreewayCoffeeApp.BASE_URL.equals("https://freecoffapp.com/fc/"))
        	{
        		OrderBut.setText("Tst Order");
        		OrderBut.setBackgroundDrawable(getResources().getDrawable(R.drawable.fc_feedback_button_bg));
        		
        	}
        }
		if(retained instanceof FreewayCoffeeItemListAsyncGet)
		{
			// Get the saved state -- the Async Get with the XML and the ListIndex.
			// WHen we set the new activity, it will call Process XML for us
			OrderAsyncGet=null;
			AsyncGet = (FreewayCoffeeItemListAsyncGet)retained;
			ListIndex=AsyncGet.SavedListIndex;
			ResetList();
			
			showProgressDialog();// This must be before setActivity as that calls ProcessXML which destroys the dialog --- so we dont want to just re-create it again
			AsyncGet.SetActivity(this);
		}
		else if (retained instanceof FreewayCoffeeMakeOrderAsyncGet)
		{
			// Get the saved state -- the Async Get with the XML and the ListIndex.
			// WHen we set the new activity, it will call Process XML for us
			AsyncGet=null;
			OrderAsyncGet = (FreewayCoffeeMakeOrderAsyncGet)retained;
			ListIndex=OrderAsyncGet.SavedListIndex;
			ResetList();
			showMakeOrderProgressDialog();// This must be before setActivity as that calls ProcessXML which destroys the dialog --- so we dont want to just re-create it again
			OrderAsyncGet.SetActivity(this);
		}
		else if (retained instanceof ArrayList<?>)
		{
			ListIndex = (ArrayList<FreewayCoffeeItemListIndexPair>)(retained);
			OrderAsyncGet=null;
			AsyncGet=null;
			ResetList();
			
		}
		else
		{
			// If no saved state, go off and request it as normal.
			AsyncGet=null;	
			ListIndex=null;
			CreateAndDisplay();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		
		
		MenuItem item = menu.add(0,MENU_ABOUT,Menu.NONE,R.string.fc_about);
		item.setIcon(R.drawable.fc_about);
		//item.setIcon(R.drawable.menu_about);
		item = menu.add(0,MENU_FEEDBACK,Menu.NONE,R.string.fc_feedback);
		item.setIcon(R.drawable.fc_contact_us);
		//item.setIcon(R.drawable.menu_send_diag);
		item = menu.add(0,MENU_VIEW_ORDER,Menu.NONE,R.string.fc_view_order);
		item.setIcon(R.drawable.fc_show_last_order);
		//item.setIcon(R.drawable.menu_view_order);
		//MENU_LAST_ERROR
		if(appState.IsSuperUser())
		{
			item = menu.add(0,MENU_REPORT,Menu.NONE,R.string.fc_reports);
			item.setIcon(R.drawable.fc_about);
		}
		return true;
	}
	
	@Override 
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch(item.getItemId())
		{
		case MENU_ABOUT:
			DoAboutMenu(item);
			return true;
			
		case MENU_FEEDBACK:
			DoFeedbackMenu(item);
			return true;
			
		case MENU_VIEW_ORDER:
			DoShowPreviousOrderMenu(item);
			return true;
		case MENU_LAST_ERROR:
			DoShowLastError(item);
			return true;
		case MENU_REPORT:
			DoReports();
			return true;
		}
		return false;
	}
	
	public void DoShowLastError(MenuItem item)
	{
		/*
		Intent intent = new Intent();
		intent.setClassName(this, FreewayCoffeeShowLastErrorDialogActivity.class.getName());
		startActivity(intent);
		*/
	}
	
	public void DoAboutMenu(MenuItem item)
	{
		Intent intent = new Intent();
		intent.setClassName(this, FreewayCoffeeAboutDialogActivity.class.getName());
		startActivity(intent);
	}
	
	public void DoFeedback()
	{
		Intent intent = new Intent();
		intent.setClassName(this, FreewayCoffeeFeedbackActivity.class.getName());
		startActivity(intent);
	}
	
	public void DoReports()
	{
		Intent intent = new Intent();
		intent.setClassName(this, FreewayCoffeeReportsActivity.class.getName());
		startActivity(intent);
	}
	public void DoButtonFeedback(View V)
	{
		DoFeedback();
	}
	public void DoFeedbackMenu(MenuItem item)
	{
		DoFeedback();
		
	}
	
	public void DoShowPreviousOrderMenu(MenuItem item)
	{
		DoShowPreviousOrderMenuLogic();
	}
	private void DoShowPreviousOrderMenuLogic()
	{
	
		Intent intent = new Intent();
		intent.setClassName(this, FreewayCoffeeOrderResultActivity.class.getName());
		startActivity(intent);
	}
	
	protected Dialog onCreateDialog (int id, Bundle args)
	{
		/*
		case ORDER_DIALOG:
			String OrderText = appState.MakeLastOrderResponseText();
			return new AlertDialog.Builder(this)
            .setIcon(R.drawable.fc_drink)
            .setTitle(R.string.fc_your_order)
            .setMessage(OrderText)
            .setPositiveButton(R.string.fc_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    // User clicked OK so do some stuff 
                	dismissDialog(ORDER_DIALOG);
                }
            })
            .create();
		*/
		
		AlertDialog.Builder AlertBuilder;
		switch(id)
		{
		
		case ALERT_DIALOG_NO_CREDIT_CARD:
			AlertBuilder = new AlertDialog.Builder(this);
			AlertBuilder.setMessage(R.string.fc_must_add_credit_card)
            .setCancelable(false)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) 
                {
                    dialog.dismiss();
                }
            });
			return AlertBuilder.create();
			
			
		case ALERT_DIALOG_NO_ITEMS:
			AlertBuilder = new AlertDialog.Builder(this);
			AlertBuilder.setMessage(R.string.fc_must_add_item_to_order)
            .setCancelable(false)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) 
                {
                    dialog.dismiss();
                }
            });
			return AlertBuilder.create();
		case ALERT_DIALOG_NO_COST:
			AlertBuilder = new AlertDialog.Builder(this);
			AlertBuilder.setMessage(R.string.fc_order_no_zero_cost)
            .setCancelable(false)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) 
                {
                    dialog.dismiss();
                }
            });
			return AlertBuilder.create();
		case ALERT_DIALOG_NO_CAR:
			AlertBuilder = new AlertDialog.Builder(this);
			AlertBuilder.setMessage(R.string.fc_order_no_car)
            .setCancelable(false)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) 
                {
                    dialog.dismiss();
                }
            });
			return AlertBuilder.create();
			
			/*
		case ALERT_DIALOG_TOO_SOON:
			String TooSoonText = getString(R.string.fc_order_too_soon) + appState.GetPreferenceMinsBetweenOrders() + " minutes";
			AlertBuilder = new AlertDialog.Builder(this);
			AlertBuilder.setMessage(R.string.fc_order_too_soon)
            .setCancelable(false)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) 
                {
                    dialog.dismiss();
                }
            });
			*/
		}
			
		return null;
		
	}
	
	
	private void DismissAllProgress()
	{
		if(ItemsListProgress!=null)
		{
			ItemsListProgress.dismiss();
			ItemsListProgress=null;
		}
		if(MakeOrderProgress!=null)
		{
			MakeOrderProgress.dismiss();
			MakeOrderProgress=null;
		}
	}
	private void DisplayNetworkError()
    {
		DismissAllProgress();
		DisplayError(R.string.fc_network_error);
    }
	
	private void DisplayError(Integer ErrorCode)
	{
		DismissAllProgress();
		Toast Err = Toast.makeText(appState.getApplicationContext(),
				getString(ErrorCode),
				Toast.LENGTH_SHORT);
    	Err.show();
	}
	
	public void showMakeOrderProgressDialog()
	{
		MakeOrderProgress = ProgressDialog.show(this, "",
				 getString(R.string.fc_making_order),
				 true);
	}
	
	public void showProgressDialog()
	{
		 ItemsListProgress = ProgressDialog.show(this, "",
				 getString(R.string.fc_retrieving_items_list),
				 true);
	}
	
	
	boolean ValidateOrder()
	{
		if( appState.IsCurrentOrderEmpty())
		{
			showDialog(ALERT_DIALOG_NO_ITEMS);
			return false;
		}
		else if(appState.IsCreditCardPresent()==false)
		{
			showDialog(ALERT_DIALOG_NO_CREDIT_CARD);
			return false;
		}
		BigDecimal TotalCost = appState.GetTotalOrderCost();
		TotalCost.setScale(2); // hopefully format like money
		if( (TotalCost.equals(BigDecimal.ZERO)) && (!m_IsOrderFreeDueToDiscounts) )
		{
			showDialog(ALERT_DIALOG_NO_COST);
			return false;
		}
		
		 
		boolean CarOK=false;
		
		if(appState.IsUserWalkup()==true)
		{
			CarOK=true;
		}
		if(CarOK!=true)
		{
			String MakeModelColorLong = appState.GetUserCarData().get(FreewayCoffeeXMLHelper.USER_CAR_DESCR_LONG_ATTR);
			if( (MakeModelColorLong==null) || (MakeModelColorLong.length()==0))
			{
				showDialog(ALERT_DIALOG_NO_CAR);
				return false;
			}
		  
			/* Removed -- license plate no longer mandatory
			String ExtraTag=appState.GetUserInfoData().get(USER_TAG_ATTR);
			if(ExtraTag==null || (ExtraTag.length()==0))
			{
				showDialog(ALERT_DIALOG_NO_CAR);
				return false;
			}
			*/
		}
		/*
		if(appState.IsOrderTooSoonAfterLast())
		{
			showDialog(ALERT_DIALOG_ORDER_TOO_SOON);
			return false;
		}
		*/
		return true;
		
	}
	
	public void MakeOrder(View v)
	{
		if(!ValidateOrder())
		{
			return;
		}
		
		try
		{
			String URL = appState.MakeCurrentOrderURL();
			OrderAsyncGet = new FreewayCoffeeMakeOrderAsyncGet(this,appState);
			OrderAsyncGet.execute(URL);
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO
		}
	}
	
	public void CreateAndDisplay()
	{	
		AsyncGet = new FreewayCoffeeItemListAsyncGet(this,appState);
		AsyncGet.execute(appState.MakeGetItemListURL());
	}
	
	@Override
   	public void onConfigurationChanged(Configuration newConfig) 
   	{
   	    super.onConfigurationChanged(newConfig);
   	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   	}
	   
	 public void ProcessXMLResult(String XML)
	 {
		 //Log.w("FreewayCoffeeDB",XML);
		 /* Get a SAXParser from the SAXPArserFactory. */
		 UnlinkAllAsync();
		 SAXParserFactory spf = SAXParserFactory.newInstance();
		 try
		 {
			 SAXParser sp = spf.newSAXParser();
			 /* Get the XMLReader of the SAXParser we created. */
			 XMLReader xr = sp.getXMLReader();
			 /* Create a new ContentHandler and apply it to the XML-Reader*/
			 ListViewXMLHandler = new FreewayCoffeeXMLHandler(appState);
			 xr.setContentHandler(ListViewXMLHandler);

	    	//Log.w("FCItemListView",XML);
	    	/* Parse the xml-data from our URL. */
	    	InputSource is = new InputSource(new StringReader(XML));
			//is.setEncoding("UTF-8");
				
	    	xr.parse(is);
	    	/* Parsing has finished. */ 
	    	DismissAllProgress();
	    		
	    	if(ListViewXMLHandler.NetworkError==true)
	    	{	    			
	    		DisplayNetworkError();
		    	return;
	    	}
	    		
	    	// TODO remove hardcoded string
	    	if( (ListViewXMLHandler.signonResponse!=null) && ListViewXMLHandler.signonResponse.equals("signon_failed"))	   
	    	{	
	    		// Start the Signon activity again.
	    		setResult(RESULT_CODE_NOT_LOGGED_IN,null);	
	    		this.finish();
	    	}
	    	if(ListViewXMLHandler.orderResponse!=null)
	    	{
	    		// Set the state
	    		appState.GetLastOrder().Clear();
	    		Intent intent = new Intent();
	    		if(ListViewXMLHandler.orderResponse.equals("ok"))
	    		{
	    			appState.GetLastOrder().SetOrderStatus(FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_SUBMITTED);
	    			appState.GetLastOrder().SetOrderLocation(ListViewXMLHandler.TheOrderLocation);
	    			appState.GetLastOrder().SetOrderID(ListViewXMLHandler.m_OrderID);
	    			intent.setClassName(this, FreewayCoffeeOrderResultActivity.class.getName());
		    		startActivityForResult(intent,SubActivityTypes.FC_SUB_AC_SHOW_ORDER.ordinal());
	    		}
	    		else
	    		{
	    			// Pop an error but do not overwrite a possible good order with a bad.
	    			appState.GetLastOrder().SetOrderStatus(FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_FAILED);
	    			intent.putExtra(FreewayCoffeeItemListView.ERROR_ORDER_INTENT_ID,appState.GetLastOrder().MakeFailedOrderResponseText());
	    			intent.setClassName(this, FreewayCoffeeOrderResultErrorActivity.class.getName());
	    			startActivity(intent);
	    		}
	    		//FreewayCoffeeItemListView.ERROR_ORDER_INTENT_ID);
	    		//showDialog(ORDER_DIALOG);
	    	}
	    	else
	    	{
	    		appState.GetLastOrder().SetOrderStatus(FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_SUBMITTED);
    			appState.GetLastOrder().SetOrderLocation(ListViewXMLHandler.TheOrderLocation);
    			appState.GetLastOrder().SetOrderID(ListViewXMLHandler.m_OrderID);
    			appState.GetLastOrder().SetOrderData(ListViewXMLHandler.TheOrder);
    			appState.GetLastOrder().SetOrderItems(ListViewXMLHandler.m_OrderItems);
    			appState.GetLastOrder().SetOrderCreditCard(ListViewXMLHandler.m_OrderCreditCard);
    			// Updates OrderStatus based on USER_HERE data in the Order. I dont like this that much. REALLY FIXME
    			appState.GetLastOrder().SetImHereStatus();
    			ResetList();
    			if( (appState.GetLastOrder().OrderExists()) && 
    				(appState.GetLastOrder().GetOrderStatus()!=FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_HERE_OK))
    			{
    				DoShowPreviousOrderMenuLogic();
    			}
	    	} 
	     		
	    }
	    catch(SAXException e)
	    {
	    	//Log.w("FCItemListView",e.getMessage());
	    	DisplayNetworkError();
	    	return;
	    }
	    catch (ParserConfigurationException pe)
	    {
	    	DisplayNetworkError();
	    	return;
	    }
	    catch (IOException io)
	    {
	    	DisplayNetworkError();
	    	return;
	    }	
	}

	 private void ResetList()
	 {

         TextView UsernameView = (TextView)findViewById(R.id.fc_banner_text);
         
         String OrderString = getString(R.string.fc_order);
         UsernameView.setText( appState.GetUserInfoData().get(USER_INFO_NAME_KEY) + "'s " + OrderString);
         
         
         
         CreateListIndex();
         myAdapter = new FCListAdapter(ListIndex,this);
         listView.setAdapter(myAdapter);
         listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	 }
	 
	 protected void onDestroy ()
	 {
		 super.onDestroy();
		 UnlinkAllAsync();
	 }
	 
	 private void UnlinkAllAsync()
	 {
		 if(AsyncGet!=null)
		 {
			 AsyncGet.UnlinkActivity();
			 AsyncGet=null;
		 }
		 if(OrderAsyncGet!=null)
		 {
			 OrderAsyncGet.UnlinkActivity();
			 OrderAsyncGet=null;
		 }
	 }
	 
	 private void ReconcileIndex()
	 {
		 if(ListIndex==null)
		 {
			 // Probably should not happen
			 return;
		 }
		 for(Iterator<FreewayCoffeeItemListIndexPair> it = ListIndex.iterator();it.hasNext();)
		 {
			 // Get the DRINK ID if its a drink. Then look up that drink in the main state.
			 // IF it does not exist, assume it was erased and remove from the List Index
			 FreewayCoffeeItemListIndexPair Item = it.next();
			 if(Item.ItemType.equals(USER_DRINK_TYPE))
			 {
				 if(appState.GetUserDrink(Item.ItemID)==null)
				 {
					 it.remove();
				 }
			 }
			 if(Item.ItemType.equals(USER_FOOD_TYPE))
			 {
				 if(appState.GetFoodFromID(Item.ItemID)==null)
				 {
					 it.remove();
				 }
			 }
		 }
	 }
	 
	 @Override
	 public Object onRetainNonConfigurationInstance()
	 {
		 // Only one of these cases can exist. 
		 // Either we have
		 // 1) GetItems Async in progress/just completed (then it gets nulled)
		 // 2) Order Async in progress/just completed (then it gets nulled)
		 // 3) Neither in progredd
		 // 4) Something horrid, and we return null;
		 
		 if(AsyncGet!=null)
		 {
			 AsyncGet.UnlinkActivity();
			 AsyncGet.SavedListIndex=ListIndex;
			 return AsyncGet;
		 }
		 if(OrderAsyncGet!=null)
		 {
			 OrderAsyncGet.UnlinkActivity();
			 OrderAsyncGet.SavedListIndex=ListIndex;
			 return OrderAsyncGet;
		 }
		 if(ListIndex!=null)
		 {
			 return ListIndex;
		 }
		 return null;
	 }
	 
	 private void UpdateOrderTotalAndDiscountAndTip()
	 {
	     
	     m_OrderTotal=BigDecimal.ZERO;
	     //self.orderTip=[NSDecimalNumber zero];
	     m_OrderDiscount=BigDecimal.ZERO;
	     m_IsOrderFreeDueToDiscounts=false;
	     
	     
	     m_OrderItemsTotal = appState.GetTotalOrderCost();
	     
	     // Check for Free Drinks
	     String UserFreeDrinkStr = appState.GetUserInfoData().get(FCUserData.USER_INFO_USER_FREE_DRINKS);     
	     
	     if(UserFreeDrinkStr==null)
	     {
	         m_OrderTotal= m_OrderItemsTotal; // Immutable so assignment is OK

	         m_OrderDiscount=BigDecimal.ZERO;
	         m_IsOrderFreeDueToDiscounts=false;
	     }
	     else
	     {
	    	 Integer UserFreeDrinkCount = 0;
	    	 try
	    	 {
	    		 UserFreeDrinkCount = Integer.parseInt(UserFreeDrinkStr);
	    	 }
	    	 catch (NumberFormatException e)
	    	 {
	    		 
	    	 }
	    	 
	    	 if(UserFreeDrinkCount.compareTo(0)==1)
	    	 {
	    		 // NumFree Drinks is bigger than zero !
	    		 // Check App settings first for discount
	             BigDecimal AmountFree=null;
	             String AppSettingOrderDiscountAmt = 
	            		 appState.GetAppSettingsTable().tryGetSettingValueAsString(FCXMLHelper.APP_SETTING_DEFAULT_FREE_DRINK_DISCOUNT_AMT);
	             
	             if(null!=AppSettingOrderDiscountAmt)
	             {
	             	 AmountFree = new BigDecimal(AppSettingOrderDiscountAmt);
	             }
	             else
	             {
	             	 AmountFree = new BigDecimal(FCXMLHelper.FREE_DRINK_ALT_AMOUNT);
	             }         
	             if(m_OrderItemsTotal.compareTo(AmountFree)==1)
	             {
	                 // Order Total greater than the free amount
	              	 m_OrderDiscount = AmountFree;
	                 m_OrderTotal = m_OrderItemsTotal.subtract(AmountFree);
	                 m_IsOrderFreeDueToDiscounts=false;
	             }
	             else
	             {
	             	 m_OrderDiscount = m_OrderItemsTotal;
	                 m_OrderTotal = BigDecimal.ZERO;
	                 m_IsOrderFreeDueToDiscounts=true;
	                 
	             }
	    	 }
	         else
	         {
	        	 m_OrderTotal=m_OrderItemsTotal;
	             m_OrderDiscount=BigDecimal.ZERO;
	             m_IsOrderFreeDueToDiscounts=false;
	          }
	     }
	     /*
	      * TIPS 
	      */
	     /*
	     NSString *LocationID = [[myCommonAppDelegate UserLocationInfo] objectForKey:ID_ATTR];
	     
	     // Calc tip
	     self.orderTip=[NSDecimalNumber zero];
	     if([[NSScanner scannerWithString:LocationID] scanInt:nil])
	     {
	         NSNumber *locationID = [NSNumber numberWithInt:[LocationID integerValue]];
	         
	         fcUserTip *tip = [[myCommonAppDelegate userTips] getTipForLocation:locationID];
	         // Tip is before discounts
	         self.orderTip = [tip calculateTipDollarAmount:self.orderItemsTotal];
	         if(tip)
	         {
	             self.orderTotal =[self.orderTotal decimalNumberByAdding:self.orderTip];
	         }
	             
	     }
	     else
	     {
	         NSLog(@"%@",[NSString stringWithFormat:@"Location[%@] is not an Integer.",LocationID] );
	     }
	     */

	 }

	 private void CreateListIndex()
	 {

		 // Only create the first time. Otherwise clear current pointer. This is because this pointer is set into the Adapter
		 // If we re-do this pointer, we have to re-set it into the Adapter each time
		 
		// If there was a saved ListIndex in AsyncGet, then we just use it. This *should* be safe
		// because we just re-parsed existing XML above so all the IDs ought to match up.
		// If not, then we are in a certain kind of trouble!
		if(AsyncGet!=null)
		{
			if(AsyncGet.SavedListIndex!=null) 
			 {
				ListIndex = new ArrayList<FreewayCoffeeItemListIndexPair>(AsyncGet.SavedListIndex);
				UpdateOrderTotalAndDiscountAndTip();
				return; // Dont re-create the index.
			 }
		 }
		 if(OrderAsyncGet!=null)
		 {
			 if(OrderAsyncGet.SavedListIndex!=null)			 
			 {
				 ListIndex = new ArrayList<FreewayCoffeeItemListIndexPair>(OrderAsyncGet.SavedListIndex);
				 UpdateOrderTotalAndDiscountAndTip();
				 return; // Dont re-create the index.
			 }
		 }
		 
		 if(ListIndex==null)
		 {
			 ListIndex = new ArrayList<FreewayCoffeeItemListIndexPair>();
		 }
		 else
		 {
			 ListIndex.clear();
		 }
		 
		 FreewayCoffeeItemListIndexPair IndexPair=null;
		 
 		// Populate drinks from CurrentDrinksOrder
		for(int index=0;index<appState.GetCurrentDrinksOrder().size();index++)
		{
			IndexPair=new FreewayCoffeeItemListIndexPair();
			IndexPair.ItemID=appState.GetCurrentDrinksOrder().get(index);
			IndexPair.ItemType=USER_DRINK_TYPE;
			ListIndex.add(IndexPair);
		}
		
		for(int index=0;index<appState.GetCurrentFoodOrder().size();index++)
		{
			IndexPair=new FreewayCoffeeItemListIndexPair();
			IndexPair.ItemID=appState.GetCurrentFoodOrder().get(index);
			IndexPair.ItemType=USER_FOOD_TYPE;
			ListIndex.add(IndexPair);
		}
		
 		// Location
 		
 		IndexPair=new FreewayCoffeeItemListIndexPair();
 		
 		
 		IndexPair.ItemID=-1;
 		IndexPair.ItemType=CHOOSE_ITEM_TYPE;
 		ListIndex.add(IndexPair);
 		
 		
 		IndexPair=new FreewayCoffeeItemListIndexPair();
 		if(appState.GetLocationData().get("id")!=null)
 		{
 			try
 			{
 				IndexPair.ItemID=Integer.parseInt(appState.GetLocationData().get("id"));
 				IndexPair.ItemType=USER_LOCATION_TYPE;
 			}
 			catch (NumberFormatException e)
 			{
 				// ItemID already =-1
 			}
 			
 			ListIndex.add(IndexPair);
 		}
 			
 		
 		// Populate Card
 		
 		// No Cards
 		if(appState.GetCreditCardsData().size()==0)
 		{
 			IndexPair=new FreewayCoffeeItemListIndexPair();
 			IndexPair.ItemID = -1;
			IndexPair.ItemType=USER_CREDIT_CARD_TYPE;
			ListIndex.add(IndexPair);
 		}
 		else
 		{
 			IndexPair=new FreewayCoffeeItemListIndexPair();
 			if( (appState.GetCreditCardsData()!=null) && 
 					appState.GetCreditCardsData().get("id")!=null)
 			{
 				try
 				{
 			
 					IndexPair.ItemID = Integer.parseInt(appState.GetCreditCardsData().get("id"));
 				}
 				catch (NumberFormatException e)
 				{
 					IndexPair.ItemID=-1;
 				}
 			}
 			IndexPair.ItemType=USER_CREDIT_CARD_TYPE;
 			ListIndex.add(IndexPair);
 		}
 		
 		// Populate TAG
 		IndexPair=new FreewayCoffeeItemListIndexPair();
 		IndexPair.ItemType = USER_TAG_TYPE;
 		IndexPair.ItemID =0; // Does not have its own ID
 		ListIndex.add(IndexPair);
 		
 		
 		// Time to Location
 		IndexPair=new FreewayCoffeeItemListIndexPair();
 		IndexPair.ItemType = USER_TIME_TO_LOCATION_TYPE;
 		IndexPair.ItemID =0; // Does not have its own ID
 		ListIndex.add(IndexPair);
 		
 		IndexPair = new FreewayCoffeeItemListIndexPair();
 		IndexPair.ItemType = CARD_CHARGED_AMOUNT_TYPE;
 		IndexPair.ItemID =0; // Does not have its own ID
 		ListIndex.add(IndexPair);
 		UpdateOrderTotalAndDiscountAndTip();
	 }
	 
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		//CreateAndDisplay();
		if(resultCode==RESULT_CODE_NOT_LOGGED_IN)
		{
			// Forced logout by admin, or some other weird error
			setResult(RESULT_CODE_NOT_LOGGED_IN);
			finish();
			
		}
		else if(resultCode==RESULT_OK)
		{
			
			if( (requestCode==SubActivityTypes.FC_SUB_AC_CHOOSE_FOOD_DRINK.ordinal()) ||
			   (requestCode==SubActivityTypes.FC_SUB_AC_CHOOSE_DRINK_BYPASS_DRINK_PICK.ordinal()))
			{
				DoChooseFoodDrink(data);
			}
			else if(requestCode==SubActivityTypes.FC_SUB_AC_CHOOSE_TIME_TO_LOCATION.ordinal())
			{
				DoUpdateTimeToLocation(data);
			}
			else if(requestCode==SubActivityTypes.FC_SUB_AC_CHOOSE_CARD.ordinal())
			{
				DoUpdateCreditCard(data);
			}
			else if(requestCode==SubActivityTypes.FC_SUB_AC_CHOOSE_TAG.ordinal())
			{
				DoUpdateTag(data);
			}
			else if(requestCode == SubActivityTypes.FC_SUB_ACTIVITY_EDIT_DRINK.ordinal())
			{
				// All we need here is to redraw, which ReconcileIndex/notify Data Changed does. This is just a placeholder.
			}
			
		}
		// Since Drinks may have been deleted (or food eventually), check to make sure all the IDs are still valid
		ReconcileIndex();
		UpdateOrderTotalAndDiscountAndTip();
		myAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onClick(View v)
	{
		Integer Pos = (Integer)(v.getTag());
		int Position = (int)(Pos); // Get the row# that we put there in getView in the adapter. This will be the remove_button
		
		if(ListIndex.get(Position).ItemType==USER_DRINK_TYPE)
		{
			appState.RemoveDrinkIDFromCurrentDrinksOrder(ListIndex.get(Position).ItemID, false);
		}
		else if (ListIndex.get(Position).ItemType==USER_FOOD_TYPE)
		{
			appState.RemoveFoodIDFromCurrentFoodOrder(ListIndex.get(Position).ItemID, false);
		}
		ListIndex.remove(Position);
		
		UpdateOrderTotalAndDiscountAndTip();
		
		myAdapter.notifyDataSetChanged();
	}
	
	private void DoUpdateTimeToLocation(Intent intent)
	{	
		// TTL has already added it to the database and appState
	}
	
	private void DoUpdateCreditCard(Intent data)
	{
		// The Credit Card Activity will have already updated the Global App State. 
		
	}
	
	private void DoUpdateTag(Intent data)
	{
		// The User Tag Activity will have already updated the Global App State. 
		
	}
	
	private void DoChooseFoodDrink(Intent data)
	{
			
		// OK, the user selected a drink. Could be an existing drink, a new drink (which will have already been loaded in by this point
		// Or a NONE (delete)
		// The selected row should still be set here so we know where to put it.
		// Cases:
		// Drink->None (remove from Index)
		// Drink->Drink (Update index)
		// Add Item->Drink Insert drink (keeping add item)
		// Add Item->NONE. Do nothing
		Integer SelectedID = data.getIntExtra(USER_CHOSE_DRINK_ID, -1);
		
		Integer ListPosition=data.getIntExtra(FreewayCoffeeItemListView.USER_ITEM_SELECTED_FOOD_DRINK_POS,-1);
		FreewayCoffeeItemListIndexPair NewItem = new FreewayCoffeeItemListIndexPair();
		
		if(SelectedID!=-1)
		{
			// Drink
			NewItem.ItemType = USER_DRINK_TYPE;
			appState.AddDrinkIDToCurrentDrinksOrder(SelectedID);
			NewItem.ItemID=SelectedID;
		}
		else
		{
			SelectedID = data.getIntExtra(USER_CHOSE_FOOD_ID, -1);
			if(SelectedID<=0)
			{
				return; // ?? TODO FIXME
			}
			// Food
			NewItem.ItemType = USER_FOOD_TYPE;
			NewItem.ItemID=SelectedID;
			appState.AddFoodIDToCurrentFoodOrder(SelectedID);
		}		
		
		if(ListIndex.get(ListPosition).ItemType==CHOOSE_ITEM_TYPE)
		{
			// Insert this one, leaving still a "Choose Item" in the list (no point over-writing it then adding it)				
			ListIndex.add(ListPosition,NewItem);
		}
		else if(ListIndex.get(ListPosition).ItemType==USER_DRINK_TYPE)
		{
			// Replace existing drink
			ListIndex.set(ListPosition,NewItem);		
		}
		else if(ListIndex.get(ListPosition).ItemType==USER_FOOD_TYPE)
		{
			// Replace existing food
			ListIndex.set(ListPosition,NewItem);	
		}
		else
		{
				// Bad. I was really hoping not to get here.
		}

	}

	private class FCListAdapter extends BaseAdapter
	{
		private ArrayList<FreewayCoffeeItemListIndexPair> Items;
		private LayoutInflater mInflater;
		
		public FCListAdapter(ArrayList<FreewayCoffeeItemListIndexPair> items, Context context)
		{
				             
			Items = items;
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount()
		{
			return Items.size();
		}
		  	 
		@Override
		public Object getItem(int position)
		{
			return Items.get(position);
		}
		  
		@Override
		public long getItemId(int position)
		{
			return position;
		}
		  
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
		  
			// A FCListViewHolder keeps references to children views to avoid unneccessary calls
			// to findViewById() on each row.
			FCListViewHolder holder;
			  
			// When convertView is not null, we can reuse it directly, there is no need
			// to reinflate it. We only inflate a new View when the convertView supplied
			// by ListView is null
			if (convertView == null)
			{
				convertView = mInflater.inflate(R.layout.listbox, null);
				// Creates a ViewHolder and store references to the two children views
				// we want to bind data to.
				holder = new FCListViewHolder();
				holder.item_descr = (TextView) convertView.findViewById(R.id.fc_list_item_text);
				holder.item_small_text = (TextView) convertView.findViewById(R.id.fc_item_list_small_text);
				holder.item_cost = (TextView) convertView.findViewById(R.id.fc_item_list_cost);
				
				holder.item_icon = (ImageView) convertView.findViewById(R.id.fc_list_img);
				holder.remove_button = (FreewayCoffeeNoParentPressImageButtonView) convertView.findViewById(R.id.fc_item_list_row_remove_button);
				holder.remove_button.setOnClickListener(FreewayCoffeeItemListView.this);
				holder.remove_button.setTag(position); // So we can get the offending line later when we want to 86 it
				convertView.setTag(holder);
				convertView.setOnClickListener(new OnItemClickListener(position)); 
			                     
			}
			else
			{
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (FCListViewHolder) convertView.getTag();
				// Make sure we set On-click here because when a view is re-used when removing, it may have a different Position so we need to update that.
				convertView.setOnClickListener(new OnItemClickListener(position)); 
				holder.remove_button.setTag(position); // So we can get the offending line later when we want to 86 it
			}
			  	              
			UpdateItem(position,holder);
			 
			  
			  return convertView;

		  }
		  
		/*
		private void CleanHolderState(FCListViewHolder holder)
		{
			holder.remove_button.setVisibility(Button.INVISIBLE); // we only want the remove button for food/drink
			holder.item_icon.setImageResource(R.drawable.unknown);
			holder.item_descr.setText("");
			holder.item_small_text.setText("");
			holder.item_cost.setText("");
		  
		}
		*/
		  private void UpdateItem(Integer position,FCListViewHolder holder)
		  {  
			  Integer ItemID = ListIndex.get(position).ItemID;
			  //CleanHolderState(holder);
			  
			  if(ListIndex.get(position).ItemType.equals(USER_DRINK_TYPE))
			  {
				  //String ItemDescr;
				  holder.remove_button.setVisibility(Button.VISIBLE);
				  FreewayCoffeeUserDrink TheDrink = appState.GetUserDrink(ItemID);
				  if(TheDrink==null)
				  {
					  
					  holder.item_small_text.setText("Internal Error. No DrinkID(" + ItemID + " Found");
					  return;
				  }
				  
				  String ItemDescr = TheDrink.GetDrinkTypeLongDescr();
				  
				  if(!TheDrink.GetUserDrinkName().equals(""))
				  {
				  
					  ItemDescr +=" (" + TheDrink.GetUserDrinkName() + ")";
				  }
				  
				  holder.item_descr.setText(ItemDescr);
				  String ItemSmallText = TheDrink.GetUserDrinkOptionsText();
				  
				  holder.item_small_text.setText(ItemSmallText);
				  holder.item_cost.setText("$" + TheDrink.GetUserDrinkCost());
				  holder.item_cost.setTextColor(getResources().getColor(R.color.Black));
				  holder.item_small_text.setTextColor(getResources().getColor(R.color.Black));
				  
				  //holder.item_icon.setImageResource(R.drawable.drink);
				  holder.item_icon.setImageResource(R.drawable.fc_drink);
			  }
			  else if(ListIndex.get(position).ItemType.equals(USER_FOOD_TYPE))
			  {
				  holder.remove_button.setVisibility(Button.VISIBLE);
				  String ItemDescr = (appState.GetFoodData().get(ItemID).get(FreewayCoffeeXMLHelper.USER_FOOD_LONG_DESCR_ATTR));
				  holder.item_descr.setText(ItemDescr);
				  holder.item_small_text.setText(appState.GetFoodData().get(ItemID).get(FreewayCoffeeXMLHelper.USER_FOOD_OPTIONS_TEXT));
				  holder.item_cost.setText("$" + appState.GetFoodData().get(ItemID).get(FreewayCoffeeXMLHelper.USER_FOOD_COST_ATTR));
				  //holder.item_icon.setImageResource(R.drawable.food);
				  holder.item_icon.setImageResource(R.drawable.fc_food);
			  }
			  else if(ListIndex.get(position).ItemType.equals(USER_LOCATION_TYPE))
			  {
				  if(appState.GetLocationData().get(FreewayCoffeeXMLHelper.USER_LOCATION_DESCR_ATTR).equals(LOCATION_NONE))
				  {
					  holder.item_descr.setText(R.string.location_none);
				  }
				  else
				  {
					  holder.item_descr.setText(appState.GetLocationData().get(FreewayCoffeeXMLHelper.USER_LOCATION_DESCR_ATTR));
				  }
				
				  //holder.item_icon.setImageResource(R.drawable.location);
				  holder.item_icon.setImageResource(R.drawable.fc_location);
				  holder.item_small_text.setText("");
				  holder.item_cost.setText("");
				  holder.remove_button.setVisibility(Button.INVISIBLE);
				  
			  }
			  else if(ListIndex.get(position).ItemType.equals(USER_CREDIT_CARD_TYPE))
			  {
				  // TODO: Revisit this
				  if(appState.IsCreditCardPresent()==false)
				  {
					  holder.item_descr.setText(R.string.credit_card_none);
				  }
				  else
				  {
					  holder.item_descr.setText(appState.GetCreditCardsData().get(FreewayCoffeeXMLHelper.USER_CREDT_CARD_DESCR_ATTR) + "...(" + 
							  					appState.GetCreditCardsData().get(FreewayCoffeeXMLHelper.USER_CREDIT_CARD_LAST4_ATTR) + ")");
				  }
				  //holder.item_icon.setImageResource(R.drawable.ccard);
				  holder.item_icon.setImageResource(R.drawable.fc_credit_card);
				  holder.item_small_text.setText("");
				  holder.item_cost.setText("");
				  holder.remove_button.setVisibility(Button.INVISIBLE);
			  }
			  else if(ListIndex.get(position).ItemType.equals(USER_TIME_TO_LOCATION_TYPE))
			  {
				  holder.item_descr.setText(getString(R.string.fc_arriving_in) +": " + appState.GetUserInfoData().get(USER_TIME_TO_LOCATION) + " Mins");
				  //holder.item_icon.setImageResource(R.drawable.arrive);
				  holder.item_icon.setImageResource(R.drawable.fc_ready_in);
				  holder.item_small_text.setText("");
				  holder.item_cost.setText("");
				  holder.remove_button.setVisibility(Button.INVISIBLE);
			  }
			  else if(ListIndex.get(position).ItemType.equals(CHOOSE_ITEM_TYPE))
			  {
				  
				  holder.item_descr.setText(R.string.add_an_item);
				  //holder.item_icon.setImageResource(R.drawable.unknown);
				  holder.item_icon.setImageResource(R.drawable.fc_add);
				  holder.item_small_text.setText("");
				  holder.item_cost.setText("");
				  holder.remove_button.setVisibility(Button.INVISIBLE);
			  }
			  else if(ListIndex.get(position).ItemType.equals(ITEM_SEPARATOR_TYPE))
			  {
				  holder.item_descr.setText("");
				  holder.item_small_text.setText("");
				  holder.item_cost.setText("");
				  holder.remove_button.setVisibility(Button.INVISIBLE);
				  //holder.item_icon.setImageResource(R.drawable.unknown);
			  }
			  else if(ListIndex.get(position).ItemType.equals(USER_TAG_TYPE))
			  {
				  if(appState.IsUserWalkup()==true)
				  {
					  holder.item_descr.setText("Walkup");
					  holder.item_icon.setImageResource(R.drawable.fc_man);
				  }
				  else
				  {
					  // There must be a more elegant way to do this. Oh well.
					  String MakeModelColorLong = appState.GetUserCarData().get(FreewayCoffeeXMLHelper.USER_CAR_DESCR_LONG_ATTR);
					  String ExtraTag="";
				
					  if(MakeModelColorLong==null)
					  {
						  MakeModelColorLong="";
					  }
				  
					  ExtraTag=appState.GetUserInfoData().get(USER_TAG_ATTR);
					  if(ExtraTag==null || (ExtraTag.length()==0))
					  {
						  // Only add " - None" if there was no make/model/color info (since license is optional)
						  if(MakeModelColorLong.length()==0)
						  {
							  ExtraTag = " - " + getString(R.string.none_text); 
						  }
						  else
						  {
							  ExtraTag="";
						  }
					  }
					  else
					  {
						  // Make sure the " - " is in front.
						  ExtraTag = " - " + ExtraTag;
					  }
				  
					  holder.item_descr.setText(MakeModelColorLong  + ExtraTag);
					  holder.item_icon.setImageResource(R.drawable.fc_car);
				  }
				  //holder.item_icon.setImageResource(R.drawable.fc_user_tag);
				  
				  holder.item_small_text.setText("");
				  holder.item_cost.setText("");
				  holder.remove_button.setVisibility(Button.INVISIBLE);
			  }
			  else if(ListIndex.get(position).ItemType.equals(CARD_CHARGED_AMOUNT_TYPE))	 
			  {
				  BigDecimal TotalCost = m_OrderTotal;
				  TotalCost.setScale(2); // hopefully format like money
				  
				  String DisplayStr ="";
				  if(FreewayCoffeeApp.BASE_URL.equals("https://freecoffapp.com/fc/"))
				  {
					  String IsDemo = appState.GetUserInfoData().get(FCXMLHelper.USER_IS_DEMO_ATTR);
					  
					  if(IsDemo!=null &&  (IsDemo.equals(FCXMLHelper.DEMO_USER)) )
					  {
						  DisplayStr = getString(R.string.fc_demo);
					  }
					  else
					  {
						  DisplayStr = getString(R.string.fc_your_card_charged);
					  }
				  }
				  else
				  {
					  DisplayStr = "TEST APP: ";
				  }
				  holder.item_descr.setText(DisplayStr + " $" + TotalCost.toPlainString());
				  
				  //holder.item_icon.setImageResource(R.drawable.fc_money);
				  holder.item_icon.setImageResource(R.drawable.fc_order_total);
				  holder.item_cost.setText("");
				  
				  if(m_OrderDiscount.compareTo(BigDecimal.ZERO)==1)
				  {
					  // Discount > 0
					  BigDecimal TempDiscount = m_OrderDiscount;
					  TempDiscount.setScale(2);
					  holder.item_small_text.setText("Discount:$" + TempDiscount.toPlainString());
					  holder.item_small_text.setTextColor(getResources().getColor(R.color.Green));
				  }
				  else
				  {
					  holder.item_small_text.setText("");
				  }
				  
				  holder.remove_button.setVisibility(Button.INVISIBLE);
			  }
				  
 
		  }
		  
		  
		 // listView.setOnItemClickListener(new OnItemClickListener()
		  private class OnItemClickListener implements View.OnClickListener
		  {           
			  
		        private int mPosition;
		        OnItemClickListener(int position)
		        {
		                mPosition = position;
		        }
		        @Override
		        public void onClick(View arg0)
		        {
                  
		        	// TODO handle Food here too.
		        	if(ListIndex.get(mPosition).ItemType.equals(CHOOSE_ITEM_TYPE))
		        	{
		        		Intent intent = new Intent();
		        	
		        		intent.putExtra(USER_ITEM_SELECTED_DRINK_ID,-1);
		        		intent.putExtra(USER_ITEM_SELECTED_FOOD_DRINK_POS,mPosition);

		        		
		        		intent.putExtra(ACTIVITY_COMMAND_CODE,SubActivityTypes.FC_SUB_AC_CHOOSE_FOOD_DRINK.ordinal());
		        		intent.setClassName(FreewayCoffeeItemListView.this, FreewayCoffeeDrinkPickerActivity.class.getName());
		        		startActivityForResult(intent,SubActivityTypes.FC_SUB_AC_CHOOSE_FOOD_DRINK.ordinal());
		        	}          		
		        	else if(ListIndex.get(mPosition).ItemType.equals(USER_DRINK_TYPE))
		        	{
		        		// Bypass Drink Picker and go directly to edit.
		        		DoStartEditDrink(mPosition); 
		        	}
		        	else if(ListIndex.get(mPosition).ItemType.equals(USER_TIME_TO_LOCATION_TYPE))
		        	{
		        		DoStartTimeToLocation(mPosition);
		        	}	
		        	else if(ListIndex.get(mPosition).ItemType.equals(USER_CREDIT_CARD_TYPE))
		        	{
		        		Intent intent = new Intent();
		        		intent.setClassName(FreewayCoffeeItemListView.this, FreewayCoffeeCreditCardActivity.class.getName());
		        		startActivityForResult(intent,SubActivityTypes.FC_SUB_AC_CHOOSE_CARD.ordinal());
		        	}
		        	else if(ListIndex.get(mPosition).ItemType.equals(USER_TAG_TYPE))
		        	{
		        		Intent intent = new Intent();
		        		intent.setClassName(FreewayCoffeeItemListView.this, FreewayCoffeeUserTagActivity.class.getName());
		        		startActivityForResult(intent,SubActivityTypes.FC_SUB_AC_CHOOSE_TAG.ordinal());
		        	}
		        	else if(ListIndex.get(mPosition).ItemType.equals(USER_LOCATION_TYPE))
		        	{
		        		// NOTE: TODO: Since we have one location, just bring up the detail page. Eventually need to let user select a location
		        		// here and do a startActivityForResult
		        		Intent intent = new Intent();
		        		intent.setClassName(FreewayCoffeeItemListView.this, FreewayCoffeeLocationDetailActivity.class.getName());
		        		startActivity(intent);
		        	}
		        }
		        
		        public void DoStartTimeToLocation(int Position)
		        {
		        	Intent intent = new Intent();
		        	intent.setClassName(FreewayCoffeeItemListView.this, FreewayCoffeeTimeToLocationActivity.class.getName());
		        	
		        	int TimeToLoc=0;
		        	try
		        	{
		        		TimeToLoc = Integer.parseInt(appState.GetUserInfoData().get(USER_TIME_TO_LOCATION));
		        	}
		        	catch(NumberFormatException e)
		        	{
		        		// TODO -- just leave it for now. Need to report later.
		        	}
		        	intent.putExtra(INTENT_KEY_USER_LOCATION_STRING, appState.GetLocationData().get(FreewayCoffeeXMLHelper.USER_LOCATION_DESCR_ATTR));
		        	intent.putExtra(INTENT_KEY_TIME_TO_LOCATION, TimeToLoc);
		        	
		        	startActivityForResult(intent,SubActivityTypes.FC_SUB_AC_CHOOSE_TIME_TO_LOCATION.ordinal());
		        	
		        }
		        
		        public void DoStartEditDrink(int Position)
		        {
		        	Intent intent = new Intent();
		        	intent.putExtra(FreewayCoffeeItemListView.USER_EDIT_DRINK_ID,ListIndex.get(Position).ItemID);
		        	
					intent.setClassName(FreewayCoffeeItemListView.this, FreewayCoffeeDrinkAddEditActivity.class.getName());
					startActivityForResult(intent,SubActivityTypes.FC_SUB_ACTIVITY_EDIT_DRINK.ordinal());
					
		        }
		        /*
		        public void DoStartChooseDrink(int Position)
		        {
		        	Intent intent = new Intent();
		        	intent.setClassName(FreewayCoffeeItemListView.this, FreewayCoffeeDrinkPickerActivity.class.getName());
		        		// We know we are guaranteed to be a drink here.
		        	try
		        	{
		        		intent.putExtra(USER_ITEM_SELECTED_DRINK_ID,ListIndex.get(Position).ItemID);
		        		intent.putExtra(USER_ITEM_SELECTED_FOOD_DRINK_POS,Position);
                	
		        	}
		        	catch (NumberFormatException e)
		        	{
		        		// Nicka TODO --- this should of course never happen
		        		intent.putExtra(USER_ITEM_SELECTED_DRINK_ID,-1);
		        	}
                
		        	startActivityForResult(intent,SubActivityTypes.FC_SUB_AC_CHOOSE_FOOD_DRINK.ordinal());
		        }
		        */
          }
		  
			       
		  class FCListViewHolder
		  {
			  TextView item_descr;
			  TextView item_small_text;
			  TextView item_cost;
			  ImageView item_icon;
			  FreewayCoffeeNoParentPressImageButtonView remove_button;
		  }
	};
};