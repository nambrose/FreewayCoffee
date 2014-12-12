package com.freewaycoffee.client;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class FreewayCoffeeCreditCardActivity extends Activity 
{
	private FreewayCoffeeApp appState;
	private FCCreditCardHolder Holder;
	private FCCreditCardData CardData;
	private ProgressDialog CreditCardUpdateProgress;
	//private FreewayCoffeeCreditCardAsyncGet AsyncGet;
	private FreewayCoffeeCreditCardAsyncPost AsyncPost;
	private FreewayCoffeeCreditCardXMLHandler CreditCardXMLHandler;
	
	static final private int MENU_LAST_ERROR = Menu.FIRST;
	
	@Override
   	public void onConfigurationChanged(Configuration newConfig) 
   	{
   	    super.onConfigurationChanged(newConfig);
   	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   	}
	   
	@Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
		super.onCreate(savedInstanceState);
	 	appState = ((FreewayCoffeeApp)getApplicationContext());        
    	setContentView(R.layout.fc_credit_card);
    	
    	TextView Banner = (TextView)findViewById(R.id.fc_banner_text);
    	Banner.setText(appState.GetUserInfoData().get(FreewayCoffeeItemListView.USER_INFO_NAME_KEY) + ", " + getString(R.string.fc_add_your_card));
    	
    	// Fill in the holder of the (possibly re-created) views
    	Holder=new FCCreditCardHolder();
    	
    	FillCardHolder();
    	
    	// Only updated when the user clicks.
    	CardData = new FCCreditCardData(); 
    	
    	Object retained = getLastNonConfigurationInstance(); 
    	//if(retained instanceof FreewayCoffeeCreditCardAsyncGet)
    	if(retained instanceof FreewayCoffeeCreditCardAsyncPost)
		{
    		//AsyncGet = (FreewayCoffeeCreditCardAsyncGet)retained;
    		AsyncPost = (FreewayCoffeeCreditCardAsyncPost)retained;
    		showProgressDialog();// This must be before setActivity as that calls ProcessXML which destroys the dialog --- so we dont want to just re-create it again
    		//AsyncGet.SetActivity(this);
    		AsyncPost.SetActivity(this);
    		
		}
    	else
    	{
    		//AsyncGet = null;
    		AsyncPost=null;
    	}
	 }

    protected void onDestroy ()
	{
		 super.onDestroy();
		
		 /*
		 if(AsyncGet!=null)
		 {
			 AsyncGet.UnlinkActivity();
		 }
		 */
		 if(AsyncPost!=null)
		 {
			 AsyncPost.UnlinkActivity();
		 }
	}
	 
	 @Override
	 public Object onRetainNonConfigurationInstance()
	 {
		 /*
		 if(AsyncGet!=null)
		 {
			 AsyncGet.UnlinkActivity();
			 return AsyncGet;
		 }
		 */
		 if(AsyncPost!=null)
		 {
			 AsyncPost.UnlinkActivity();
			 return AsyncPost;
		 }
		 return null;
	 }
	 
	 public void showProgressDialog()
	 {
		 CreditCardUpdateProgress = ProgressDialog.show(this, "",
				 			getString(R.string.fc_updating_credit_card_info),
				 			true);
	 }
	 
	public void UserCanceled(View V)
	{
		setResult(RESULT_CANCELED);
		finish();
	}
	
	public void DoUpdateCard(View V)
	{
		ClearAllErrors();
		if(ValidateAndGetFields()!=true)
		{
			return;
		}
		
		//AsyncGet = new FreewayCoffeeCreditCardAsyncGet(this,appState);
		try
		{
			//String CommandStr = appState.MakeUpdateCreditCardURL(CardData.CardNumber,CardData.ExpMonth,CardData.ExpYear,CardData.ZIP);			
			//AsyncGet.execute(CommandStr);
		
			AsyncPost = new FreewayCoffeeCreditCardAsyncPost(this,appState);
    		AsyncPost.execute(appState.MakeUpdateCreditCardHTTPPost(CardData.CardNumber,CardData.ExpMonth,CardData.ExpYear,CardData.ZIP));
		}
		
		
		catch (UnsupportedEncodingException e)
		{
			// TODO
			DisplayNetworkError();
			return;
		}
		
	}
	
	private String MakeFailedResponseText()
	{
		String ErrorText = appState.MakeLastErrorText();
	
		String Result = appState.getString(R.string.fc_sorry) + ", " + appState.getLoginNickname() + "\n" +  "Your card could not be added or updated" + "\n\n";
		Result += appState.getString(R.string.fc_reason) +  "\n" + ErrorText;
		return Result;
	}
	public void ProcessXMLResult(String XML)
    {
		//Log.w("FreewayCoffeeDB",XML);
		/* Get a SAXParser from the SAXPArserFactory. */
		SAXParserFactory spf = SAXParserFactory.newInstance();

		try
		{
			SAXParser sp = spf.newSAXParser();
			//AsyncGet=null;
			AsyncPost=null;
            /* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();
            /* Create a new ContentHandler and apply it to the XML-Reader*/
			CreditCardXMLHandler = new FreewayCoffeeCreditCardXMLHandler(appState);
			xr.setContentHandler(CreditCardXMLHandler);

           // Parse the xml-data from our URL.
			InputSource is = new InputSource(new StringReader(XML));
			//is.setEncoding("UTF-8");
			
    		xr.parse(is);
           /* Parsing has finished. */
          
          if(CreditCardUpdateProgress!=null)
          {
        	  CreditCardUpdateProgress.dismiss();
        	  CreditCardUpdateProgress=null;
          }
          if(CreditCardXMLHandler.NetworkError==true)
  		  {
        	  DisplayNetworkError();
        	  return;
  		  }
          if(CreditCardXMLHandler.signonResponse!=null)
          {
        	   // TODO -=- in general, what do we do for these sub-views if we get say a SIGNON_OK.
        	   // Cannot just reload indefinitely. Can I even call reload here ?
        	   // Return to the parent view and try to let that one refresh ? Lets punt it for now.

               // Tell the parent view that some critical login-type thing has happened!
        	  setResult(FreewayCoffeeItemListView.RESULT_CODE_NOT_LOGGED_IN);
        	   this.finish();
           }
          if(CreditCardXMLHandler.updateCardResponse.equals("ok"))
          {
        	  Toast SuccessToast = Toast.makeText(appState.getApplicationContext(),
        			  							  getString(R.string.fc_card_updated),
        			  							  Toast.LENGTH_SHORT);
        	  SuccessToast.show();
        	  try
        	  {
        		  
        		  appState.SetCreditCardsData(CreditCardXMLHandler.TheCard);
        		  EndActivity();
        	  }
        	  catch (NumberFormatException e)
        	  {
        		  // TODO sub optional
        		  Toast ErrorToast = Toast.makeText(appState.getApplicationContext(),"An Error occured updating your card info. Please try again.",Toast.LENGTH_SHORT);
            	  ErrorToast.show();
            	  return;
        	  }
        	  
        	  
          }
          else
          {
        	  Intent intent = new Intent();
        	  intent.putExtra(FreewayCoffeeItemListView.ERROR_ORDER_INTENT_ID,MakeFailedResponseText());
  			  intent.setClassName(this, FreewayCoffeeOrderResultErrorActivity.class.getName());
  		      startActivity(intent);
  			
        	  //Toast ErrorToast = Toast.makeText(appState.getApplicationContext(),"An Error occured updating your card info. Please try again.",Toast.LENGTH_SHORT);
        	  //ErrorToast.show();
        	  return;
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
	
	private void DisplayNetworkError()
    {
		if(CreditCardUpdateProgress!=null)
		{
	      	  CreditCardUpdateProgress.dismiss();
	      	  CreditCardUpdateProgress=null;
	    }
    	Toast Err = Toast.makeText(appState.getApplicationContext(),
				getString(R.string.fc_network_error),
				Toast.LENGTH_SHORT);
    	Err.show();
    	
        
    }
	
	private void EndActivity()
	{
		setResult(RESULT_OK);
		finish();
	}
	
	private boolean ValidateAndGetFields()
	{
		CardData.CardNumber = Holder.CardNumberView.getText().toString();
		CardData.CardNumber = getDigitsOnly(CardData.CardNumber);
		
		
		if((CardData.CardNumber.length()==0)|| !isCreditCardValid(CardData.CardNumber))
		{
			SetError(getString(R.string.fc_credit_card_number_invalid),Holder.CardNumberView);
			return false;
		}
		
		
		Date d = new Date(System.currentTimeMillis());
		
		// TODO -- deal with possible NumberFormatExceptions ??
		// Get Current Year
		CharSequence s  = DateFormat.format("yyyy", d.getTime());
		Integer CurrentYear = Integer.parseInt(s.toString());
	
		// Get Current Month
		s  = DateFormat.format("MM", d.getTime());
		Integer CurrentMonth = Integer.parseInt(s.toString());
		
		// Validate Year
		Integer ExpYear;
		CardData.ExpYear = Holder.ExpYearView.getText().toString();
		try
		{
			ExpYear = Integer.parseInt(CardData.ExpYear);
			if( (ExpYear.compareTo(CurrentYear)<0))
			{
				SetError(getString(R.string.fc_credit_card_exp_year_invalid),Holder.ExpYearView);
				return false;
			}
		}
		catch(NumberFormatException e)
		{
			SetError(getString(R.string.fc_credit_card_exp_year_invalid),Holder.ExpYearView);
			return false;
		}
		
		CardData.ExpMonth = Holder.ExpMonthView.getText().toString();
		try
		{
			Integer ExpMonth = Integer.parseInt(CardData.ExpMonth);
			if( (ExpMonth<1) || (ExpMonth>12) )
			{
				SetError(getString(R.string.fc_credit_card_exp_month_invalid),Holder.ExpMonthView);
				return false;
			}
			if( (ExpYear.compareTo(CurrentYear)==0) && (ExpMonth.compareTo(CurrentMonth)<0))
			{
				SetError(getString(R.string.fc_credit_card_exp_month_invalid),Holder.ExpMonthView);
				return false;
			}
		}
		catch(NumberFormatException e)
		{
			SetError(getString(R.string.fc_credit_card_exp_month_invalid),Holder.ExpMonthView);
			return false;
		}
		
		/*
		CardData.CCV = Holder.CCVView.getText().toString();
		
		if( (CardData.CCV.length()!=3) || (CardData.CCV.length()!=4))
		{
			SetError(getString(R.string.fc_credit_card_exp_ccv_invalid),Holder.CCVView);
			return false;
		}
		try
		{
			Integer CCV = Integer.parseInt(CardData.CCV);
			if(CCV<0)
			{
				SetError(getString(R.string.fc_credit_card_exp_ccv_invalid),Holder.CCVView);
				return false;
			}
		}
		catch(NumberFormatException e)
		{
			SetError(getString(R.string.fc_credit_card_exp_ccv_invalid),Holder.CCVView);
			return false;
		}
		*/
		
		CardData.ZIP = Holder.ZIPView.getText().toString();
		if(CardData.ZIP.length()<5)
		{
			SetError(getString(R.string.fc_credit_card_exp_zip_invalid),Holder.ZIPView);
			return false;
		}
		try
		{
			Integer ZIP = Integer.parseInt(CardData.ExpMonth);
			if(ZIP<1)
			{
				SetError(getString(R.string.fc_credit_card_exp_zip_invalid),Holder.ZIPView);
				return false;
			}
		}
		catch(NumberFormatException e)
		{
			SetError(getString(R.string.fc_credit_card_exp_zip_invalid),Holder.ZIPView);
			return false;
		}
		
		return true;
		
	}
	
	private void SetError(String ErrorText, EditText E)
	{
		Toast Err = Toast.makeText(appState.getApplicationContext(),
							ErrorText,
							Toast.LENGTH_SHORT);
		Err.show();
		E.setTextColor(getResources().getColor(R.color.Red));
	}
	private void ClearAllErrors()
	{
		Holder.CardNumberView.setTextColor(getResources().getColor(R.color.Black));
		Holder.ExpMonthView.setTextColor(getResources().getColor(R.color.Black));
		Holder.ExpYearView.setTextColor(getResources().getColor(R.color.Black));
		Holder.ZIPView.setTextColor(getResources().getColor(R.color.Black));
	//	Holder.CCVView.setTextColor(getResources().getColor(R.color.Black));
	}
	
	private void FillCardHolder()
	{
		Holder.CardNumberView=(EditText)findViewById(R.id.fc_credit_card_card_number);
		Holder.ExpMonthView=(EditText)findViewById(R.id.fc_credit_card_exp_month);
		Holder.ExpYearView=(EditText)findViewById(R.id.fc_credit_card_exp_year);
		//Holder.CCVView=(EditText)findViewById(R.id.fc_credit_card_ccv);
		Holder.ZIPView=(EditText)findViewById(R.id.fc_credit_card_zip);
	}
	public class FCCreditCardData
	{
		String CardNumber;
		String ExpMonth;
		String ExpYear;
		//String CCV;
		String ZIP;
		
	}
	private class FCCreditCardHolder
	{
		EditText CardNumberView;
		EditText ExpMonthView;
		EditText ExpYearView;
		//EditText CCVView;
		EditText ZIPView;
	}
	
	// Credit card validation code thanks to: http://www.merriampark.com/anatomycc.htm (Free license)
	private static String getDigitsOnly (String s)
	{
	    StringBuffer digitsOnly = new StringBuffer ();
	    char c;
	    for (int i = 0; i < s.length (); i++) {
	      c = s.charAt (i);
	      if (Character.isDigit (c)) {
	        digitsOnly.append (c);
	      }
	    }
	    return digitsOnly.toString ();
	  }

	  //-------------------
	  // Perform Luhn check
	  //-------------------

	  private static boolean isCreditCardValid (String cardNumber) 
	  {
	    String digitsOnly = getDigitsOnly (cardNumber);
	    int sum = 0;
	    int digit = 0;
	    int addend = 0;
	    boolean timesTwo = false;

	    for (int i = digitsOnly.length () - 1; i >= 0; i--) {
	      digit = Integer.parseInt (digitsOnly.substring (i, i + 1));
	      if (timesTwo) {
	        addend = digit * 2;
	        if (addend > 9) {
	          addend -= 9;
	        }
	      }
	      else {
	        addend = digit;
	      }
	      sum += addend;
	      timesTwo = !timesTwo;
	    }

	    int modulus = sum % 10;
	    return modulus == 0;

	  }
}
