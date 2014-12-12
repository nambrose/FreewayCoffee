package com.freewaycoffee.client;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.freewaycoffee.client.FreewayCoffeeItemListView.SubActivityTypes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;


public class FreewayCoffeeOrderResultActivity extends Activity 
{
	private FreewayCoffeeApp appState;
	private Button m_MainBut;
	private Button m_FeedbackBut;
	private TextView UsernameView;
	private TextView MainText;
	FreewayCoffeeLastOrderAsyncGet m_Async;
	private ProgressDialog m_ArrivedProgress;
	private FreewayCoffeeXMLHandler m_OrderResultXMLHandler;
	private CheckBox m_WalkupCheck;
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
		 super.onCreate(savedInstanceState);
		 appState = ((FreewayCoffeeApp)getApplicationContext());        
     
		 setContentView(R.layout.fc_order_response);
		 
		  UsernameView = (TextView)findViewById(R.id.fc_banner_text);
              
		  MainText = (TextView)findViewById(R.id.fc_order_response_text);

          m_MainBut = (Button)findViewById(R.id.fc_order_response_button);
          
          m_FeedbackBut = (Button)findViewById(R.id.fc_order_response_feedback_button);
          m_WalkupCheck = (CheckBox)findViewById(R.id.fc_order_response_walkup);
          
          DoDisplay();
          
          
          
		 //MainText.setText(appState.MakeLastOrderResponseText());
		 //SetArrivedButtonStateAndText();
		 
	 }
	 
	 private void DismissAllProgress()
	 {
		if(m_ArrivedProgress!=null)
		{
			m_ArrivedProgress.dismiss();
			m_ArrivedProgress=null;
		}
	}

	public void DoFeedback()
	{
		Intent intent = new Intent();
		intent.setClassName(this, FreewayCoffeeFeedbackActivity.class.getName());
		startActivity(intent);
	}
	
	public void DoButtonFeedback(View V)
	{
		DoFeedback();
	}
		
	public void showArrivedProgressDialog()
	{
		 m_ArrivedProgress = ProgressDialog.show(this, "",
					 getString(R.string.fc_ive_arrived),
					 true);
	}
	 
	 private void DoDisplay()
	 {
		 if( (appState.GetLastOrder().GetOrderStatus()==FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_SUBMITTED) ||
       		  ( appState.GetLastOrder().GetOrderStatus()==FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_HERE_SENT))
         {
       	  m_MainBut.setText(getString(R.string.fc_ive_arrived));
       	  UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + 
       			  	getString(R.string.fc_order_submitted));
       	  MainText.setText(appState.GetLastOrder().MakeOrderSubmittedText(),BufferType.SPANNABLE);
       	m_WalkupCheck.setVisibility(CheckBox.VISIBLE);
   		m_WalkupCheck.setChecked(appState.IsUserWalkup());
       	
       	  
         }
         else if (( appState.GetLastOrder().GetOrderStatus()==FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_HERE_OK))
         {
       	  	String ResponseString = getString(R.string.fc_order_response);
       	  	UsernameView.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + ResponseString);
       	  	m_MainBut.setText(getString(R.string.fc_finished));
       	  	MainText.setText(appState.GetLastOrder().MakeOrderResponseText(),BufferType.SPANNABLE);
       		m_FeedbackBut.setVisibility(Button.VISIBLE);
       		
       		m_FeedbackBut.setVisibility(Button.GONE);
           	m_WalkupCheck.setVisibility(CheckBox.GONE);
       	
       	
         }
	 }
	 public void DoAction(View v)
	 {
		 if(appState.GetLastOrder().GetOrderStatus()==FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_HERE_SENT)
		 {
			 Toast SuccessToast = Toast.makeText(appState.getApplicationContext(),
					  getString(R.string.fc_im_here_sent_already),
					  Toast.LENGTH_SHORT);
			SuccessToast.show();
			return;
		 }
		 else if( appState.GetLastOrder().GetOrderStatus()==FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_SUBMITTED)
		 {
			 appState.GetLastOrder().SetOrderStatus(FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_HERE_SENT);
			 String ImHereURL = appState.GetLastOrder().MakeImHereURL(m_WalkupCheck.isChecked());
			  m_Async= new FreewayCoffeeLastOrderAsyncGet(this,appState);
			 m_Async.execute(ImHereURL);
			
		 }
		 else if( appState.GetLastOrder().GetOrderStatus()==FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_HERE_OK)
		 {
			 DoBack(null);
		 }
			 
	 }
	 public void DoBack(View v)
	 {
		 setResult(RESULT_OK);
		 finish();
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
			 m_OrderResultXMLHandler = new FreewayCoffeeXMLHandler(appState);
			 xr.setContentHandler(m_OrderResultXMLHandler);

	    	//Log.w("FCItemListView",XML);
	    	/* Parse the xml-data from our URL. */
	    	InputSource is = new InputSource(new StringReader(XML));
			//is.setEncoding("UTF-8");
				
	    	xr.parse(is);
	    	/* Parsing has finished. */ 
	    	DismissAllProgress();
	    		
	    	if(m_OrderResultXMLHandler.NetworkError==true)
	    	{	    			
	    		DisplayNetworkError();
	    		if(appState.GetLastOrder().GetOrderStatus()!=FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_HERE_OK)
	    		{
	    			appState.GetLastOrder().SetOrderStatus(FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_SUBMITTED);
	    		}
		    	return;
	    	}
	    		
	    	// TODO remove hardcoded string
	    	if( (m_OrderResultXMLHandler.signonResponse!=null) && m_OrderResultXMLHandler.signonResponse.equals("signon_failed"))	   
	    	{	
	    		// Start the Signon activity again.
	    		setResult(FreewayCoffeeItemListView.RESULT_CODE_NOT_LOGGED_IN,null);	
	    		this.finish();
	    	}
	    	if(m_OrderResultXMLHandler.orderHereResponse!=null)
	    	{
	    		// Set the state
	    		appState.GetLastOrder().Clear();
	    		
	    		if( (m_OrderResultXMLHandler.orderHereResponse!=null) && m_OrderResultXMLHandler.orderHereResponse.equals("ok"))
	    		{
	    			appState.GetLastOrder().SetOrderStatus(FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_HERE_OK);
	    			appState.GetLastOrder().SetOrderLocation(m_OrderResultXMLHandler.TheOrderLocation);
	    			appState.GetLastOrder().SetOrderID(m_OrderResultXMLHandler.m_OrderID);
	    			appState.GetLastOrder().SetOrderData(m_OrderResultXMLHandler.TheOrder);
	    			appState.GetLastOrder().SetOrderItems(m_OrderResultXMLHandler.m_OrderItems);
	    			appState.GetLastOrder().SetOrderCreditCard(m_OrderResultXMLHandler.m_OrderCreditCard);
	    			
	    			DoDisplay();
	    		}
	    		else
	    		{
	    			if(appState.GetLastOrder().GetOrderStatus()!=FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_HERE_OK)
		    		{
	    				appState.GetLastOrder().SetOrderStatus(FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_SUBMITTED);
	    				Toast ErrToast = Toast.makeText(appState.getApplicationContext(),
	  	  					  getString(R.string.fc_im_here_failed),
	  	  					  Toast.LENGTH_SHORT);
	  	    			ErrToast.show();
		    		}
	    			
    		}
	    		
	    	}
	    	
	     		
	    }
	    catch(SAXException e)
	    {
	    	if(appState.GetLastOrder().GetOrderStatus()!=FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_HERE_OK)
    		{
	    		appState.GetLastOrder().SetOrderStatus(FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_SUBMITTED);
    		}
	    	//Log.w("FCItemListView",e.getMessage());
	    	DisplayNetworkError();
	    	return;
	    }
	    catch (ParserConfigurationException pe)
	    {
	    	if(appState.GetLastOrder().GetOrderStatus()!=FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_HERE_OK)
    		{
	    		appState.GetLastOrder().SetOrderStatus(FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_SUBMITTED);
    		}
	    	DisplayNetworkError();
	    	return;
	    }
	    catch (IOException io)
	    {
	    	if(appState.GetLastOrder().GetOrderStatus()!=FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_HERE_OK)
    		{
	    		appState.GetLastOrder().SetOrderStatus(FreewayCoffeeLastOrder.OrderSubmittedStatus.ORDER_SUBMITTED);
    		}
	    	DisplayNetworkError();
	    	return;
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
	
	 @Override
   	public void onConfigurationChanged(Configuration newConfig) 
   	{
   	    super.onConfigurationChanged(newConfig);
   	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   	}
	 
	 protected void onDestroy ()
	 {
		 super.onDestroy();
		 UnlinkAllAsync();
	 }
	 
	 private void UnlinkAllAsync()
	 {
		 if(m_Async!=null)
		 {
			 m_Async.UnlinkActivity();
			 m_Async=null;
		 }
	 }
	 
	 public void DoHere(View v)
	 {
		 // Dont reward impatient users by sending over and over
		 /*
		 if(appState.GetLastOrderSentImHere()==true)
		 {
			 Toast SuccessToast = Toast.makeText(appState.getApplicationContext(),
					  getString(R.string.fc_im_here_sent_already),
					  Toast.LENGTH_SHORT);
			SuccessToast.show();		 
		 }
		 */
		 // Maybe not quite true if it doesn't complete/times out, but I dont want to block the user (for now)
		 /*
		 appState.SetLastOrderSentImHere(true);
		 FreewayCoffeeLastOrderAsyncGet Async= new FreewayCoffeeLastOrderAsyncGet(this,appState);
		 String ImHereURL = appState.MakeImHereURL();
		 Async.execute(ImHereURL);
		 /*
		 Toast SuccessToast = Toast.makeText(appState.getApplicationContext(),
					  getString(R.string.fc_im_here_sent),
					  Toast.LENGTH_SHORT);
		 SuccessToast.show();
		 
		 SetArrivedButtonStateAndText();
		 
		 */
	 }
}
