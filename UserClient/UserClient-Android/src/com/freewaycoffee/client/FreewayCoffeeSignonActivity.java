package com.freewaycoffee.client;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class FreewayCoffeeSignonActivity extends Activity 
{
	static final private int MENU_ABOUT = Menu.FIRST;
	static final private int MENU_FEEDBACK = Menu.FIRST+1;
	
	static final private int SCHEMA_ERROR_DIALOG=1;
	static final private int LOGIN_FAIL_DIALOG=2;
	static final private int NETWORK_ERROR_DIALOG=3;
	static final private int PASSWORD_NOT_STRONG_DIALOG=4;
	
	private FreewayCoffeeApp appState;
	private String EmailAddress;
	private String Password;
	private boolean LoginAutomatically;
	private  ProgressDialog SignonProgress;
	//private FreewayCoffeeSignonAsyncGet AsyncGet;
	private String SchemaError;
	
	private FreewayCoffeeSignonAsyncPost AsyncPost;

	 @Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
		 super.onCreate(savedInstanceState);
		 appState = ((FreewayCoffeeApp)getApplicationContext());        
	     setContentView(R.layout.signon);
	     Object retained = getLastNonConfigurationInstance(); 
	    
	     
	     EditText EmailView = (EditText)findViewById(R.id.signonemail_edit);
	     /*
	     InputFilter[] filts = new InputFilter[]{appState.GetEditTextEmailInputFilter()};
	     
	     EmailView.setFilters(filts); 
	     */
	     
	        
	     //FILTInputFilter[] Emailfilts = new InputFilter[]{appState.GetEditTextEmailInputFilter()};
		     
	     //FILTEmailView.setFilters(Emailfilts); 
		     
	     
	     EditText PasswordView = (EditText)findViewById(R.id.signonpassword_edit);
	     
	     
	     // Eventually, just skip the display if this one is set, and get to the action!
	     
	     //CheckBox AutoSigninView = (CheckBox)findViewById(R.id.signon_loginautomatically);
	     
	     //if(retained instanceof FreewayCoffeeSignonAsyncGet)
	     if(retained instanceof FreewayCoffeeSignonAsyncPost)
	     {
	    	 
	    	// AsyncGet = (FreewayCoffeeSignonAsyncGet)retained;
	    	 
	    	 
	    	 //EmailAddress=AsyncGet.EmailAddressSaved;
	    	 //Password=AsyncGet.PasswordSaved;
	    	 //LoginAutomatically=AsyncGet.LoginAutomaticallySaved;
	    	  AsyncPost = (FreewayCoffeeSignonAsyncPost)retained;
	    	 
	    	 
	    	 EmailAddress=AsyncPost.EmailAddressSaved;
	    	 Password=AsyncPost.PasswordSaved;
	    	 LoginAutomatically=AsyncPost.LoginAutomaticallySaved;
	    	 
	    	 // Get the saved state -- the Async Get with the XML and the ListIndex.
	    	 // WHen we set the new activity, it will call Process XML for us
	    	 EmailView.setText(EmailAddress);
	    	 PasswordView.setText(Password);
	    	 //AutoSigninView.setChecked(LoginAutomatically);
	    	 showProgressDialog();// This must be before setActivity as that calls ProcessXML which destroys the dialog --- so we dont want to just re-create it again
	    	 //AsyncGet.SetActivity(this);
	    	 
	    	 
	    	 AsyncPost.SetActivity(this);
	     }
	     else
	     {	     
	    	 SignonProgress=null;
	    	 //AsyncGet=null;
	    	 AsyncPost=null;
	    	 EmailView.setText(appState.getLoginEmail());
	    	 EmailAddress = appState.getLoginEmail();
	    	 PasswordView.setText(appState.GetPassword());
	    	 Password=appState.GetPassword();
	    	 //AutoSigninView.setChecked(appState.GetLoginAutomatically());
	     }
	     if(appState.IsUserNameSet() && appState.IsPasswordSet())
    	 {
	    	 DoLogin();
    	 }
	 }
     
	 protected Dialog onCreateDialog (int id, Bundle args)
	 {

		 switch(id)
		 {
		 case SCHEMA_ERROR_DIALOG:

			 return new AlertDialog.Builder(this)
			 .setIcon(R.drawable.fc_error)
			 .setTitle(R.string.fc_schema_error)
			 .setMessage(SchemaError)
			 .setPositiveButton(R.string.fc_ok, new DialogInterface.OnClickListener() {
				 public void onClick(DialogInterface dialog, int whichButton) {

					 /* User clicked OK so do some stuff */
					 dismissDialog(SCHEMA_ERROR_DIALOG);
				 }
			 })
			 .create();


		 case LOGIN_FAIL_DIALOG:

			 return new AlertDialog.Builder(this)
			 .setIcon(R.drawable.fc_error)
			 .setTitle(R.string.fc_signon_error)
			 .setMessage(R.string.fc_login_not_successful)
			 .setPositiveButton(R.string.fc_ok, new DialogInterface.OnClickListener() {
				 public void onClick(DialogInterface dialog, int whichButton) {

					 /* User clicked OK so do some stuff */
					 dismissDialog(LOGIN_FAIL_DIALOG);
				 }

			 })
			 .create();

		 case NETWORK_ERROR_DIALOG:

			 return new AlertDialog.Builder(this)
			 .setIcon(R.drawable.fc_error)
			 .setTitle(R.string.fc_network_error)
			 .setMessage(R.string.fc_network_error)
			 .setPositiveButton(R.string.fc_ok, new DialogInterface.OnClickListener() {
				 public void onClick(DialogInterface dialog, int whichButton) {

					 /* User clicked OK so do some stuff */
					 dismissDialog(NETWORK_ERROR_DIALOG);
				 }

			 })
			 .create();
		 
		 case PASSWORD_NOT_STRONG_DIALOG:
			 return new AlertDialog.Builder(this)
			 .setIcon(R.drawable.fc_error)
			 .setTitle(R.string.fc_pw_weak_error)
			 .setMessage("Your password must be at least 6 characters long")
			 .setPositiveButton(R.string.fc_ok, new DialogInterface.OnClickListener() {
				 public void onClick(DialogInterface dialog, int whichButton) {

					 /* User clicked OK so do some stuff */
					 dismissDialog(PASSWORD_NOT_STRONG_DIALOG);
				 }

			 })
			 .create();
	 
		 }
		 
		 return null;
	 }
	 
	 @Override
		public boolean onCreateOptionsMenu(Menu menu)
		{
			super.onCreateOptionsMenu(menu);
			
			
			MenuItem item = menu.add(0,MENU_ABOUT,Menu.NONE,R.string.fc_about);
			item.setIcon(R.drawable.fc_about);
			
			item = menu.add(0,MENU_FEEDBACK,Menu.NONE,R.string.fc_feedback);
			item.setIcon(R.drawable.fc_contact_us);
			
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

		 }
		 return false;
	 }
		
		public void DoAboutMenu(MenuItem item)
		{
			Intent intent = new Intent();
			intent.setClassName(this, FreewayCoffeeAboutDialogActivity.class.getName());
			startActivity(intent);
		}
		
		public void DoFeedbackMenu(MenuItem item)
		{
			Intent intent = new Intent();
			intent.setClassName(this, FreewayCoffeeFeedbackActivity.class.getName());
			startActivity(intent);
		}
		
	 public void showProgressDialog()
	 {
		 SignonProgress = ProgressDialog.show(this, "",
				 			getString(R.string.fc_signing_in),
				 			true);
	 }
	 private void DisplayError(Integer ErrorViewID, Integer ErrorStringID)
	    {
	    	TextView EmailError = (TextView)findViewById(ErrorViewID);
			EmailError.setText(ErrorStringID);
			EmailError.invalidate();
	    }
	    
	    private void ClearError(Integer ErrorViewID)
	    {
	    	TextView EmailError = (TextView)findViewById(ErrorViewID);
			EmailError.setText("");
			EmailError.invalidate();
	    }
	    
	    private void UnlinkAsync()
	    {
	    	if(AsyncPost!=null)
	    	{
	    		AsyncPost.UnlinkActivity();
	    		AsyncPost=null;
	    	}
	    }
	    
	    private void ClearOldErrors()
	    {
	    	ClearError(R.id.signonemail_error);	    	
	    	ClearError(R.id.signonpassword_error);  	
	    }
       
	    public void AttemptSignon(View v)
	    {
	    	DoAttemptSignon(v);
	    }
	    
	    
	    protected void onDestroy ()
		{
			 super.onDestroy();
			 //AsyncGet.cancel(true);
			 /*
			 if(AsyncGet!=null)
			 {
				 AsyncGet.UnlinkActivity();
			 }*/
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
				 AsyncGet.EmailAddressSaved =EmailAddress;
				 AsyncGet.PasswordSaved=Password;
				 AsyncGet.LoginAutomaticallySaved=LoginAutomatically;
				 return AsyncGet;
			 }*/
			 if(AsyncPost!=null)
			 {
				 AsyncPost.UnlinkActivity();
				 AsyncPost.EmailAddressSaved =EmailAddress;
				 AsyncPost.PasswordSaved=Password;
				 AsyncPost.LoginAutomaticallySaved=LoginAutomatically;
				 return AsyncPost;
			 }
			 return null;
		 }
		 
		 public void GoToSignup(View v)
		 {
			 // Start the Signup activity
			 // We finish() here to avoid a trigger-happy user making an infinite number of activities
 			Intent intent = new Intent();
			intent.setClassName(this, FreewayCoffeeSignupActivity.class.getName());
			startActivity(intent);
			finish();
		 }
		 
	    // Separate method so we can call from onCreate w/out necessarily worrying about passing a View
	    private void DoAttemptSignon(View SignonButton)
	    {
	    	
	    	ClearOldErrors();
	    	
	    	// Validate Email address
	    	EditText EmailView = (EditText)findViewById(R.id.signonemail_edit);
	    	EmailAddress = EmailView.getText().toString();
	    	
	    	if(EmailAddress.length()==0)
	    	{
	    		DisplayError(R.id.signonemail_error,R.string.error_email_cannot_be_empty);
	    		return;
	    	}
	    	
	    	// Validate password non-empty
	    	EditText PasswordView = (EditText)findViewById(R.id.signonpassword_edit);
	    	Password = PasswordView.getText().toString();
	    	if(Password.length()==0)
	    	{
	    		DisplayError(R.id.signonpassword_error,R.string.error_password_cannot_be_empty);
	    		return;
	    	}
	    	
	    	if(FreewayCoffeeXMLHelper.CheckPasswordStrength(Password)!=true)
	    	{
	    		showDialog(PASSWORD_NOT_STRONG_DIALOG);
	    		return;
	    	}
	    	// Auto signin
	    	//CheckBox AutoSigninView = (CheckBox)findViewById(R.id.signon_loginautomatically);
	    	//LoginAutomatically = AutoSigninView.isChecked();
	    	LoginAutomatically = true;
	    	DoLogin();
	    	
	   	
	    }
	    
	    private void DoLogin()
	    {
	    	// Make a Get
	    	try
	    	{
	    		//AsyncGet = new FreewayCoffeeSignonAsyncGet(this,appState);
	    		//AsyncGet.execute(appState.MakeSignonURL(EmailAddress,Password));
	    		AsyncPost = new FreewayCoffeeSignonAsyncPost(this,appState);
	    		AsyncPost.execute(appState.MakeSignonHTTPPost(EmailAddress, Password));
	    	}
	    	catch (UnsupportedEncodingException e)
			{
				// TODO
				DisplayNetworkError();
				return;
			}
	    }
	    public void ProcessXMLResult(String XML)
		{
	    	//Log.w("FreewayCoffeeDB",XML);
	    	/* Get a SAXParser from the SAXPArserFactory. */
	    	//AsyncGet=null;
	    	UnlinkAsync();
	    	if(SignonProgress!=null)
    		{
    			SignonProgress.dismiss();
    			SignonProgress=null; // So we dont try to dismiss twice if we sign on, sign off then change orientation during next signon ?
    		}
	    	
	    	SAXParserFactory spf = SAXParserFactory.newInstance();
	    	try
	    	{
	    		SAXParser sp = spf.newSAXParser();

	    		/* Get the XMLReader of the SAXParser we created. */
	    		XMLReader xr = sp.getXMLReader();
	    		/* Create a new ContentHandler and apply it to the XML-Reader*/
	    		FreewayCoffeeXMLHandler myExampleHandler = new FreewayCoffeeXMLHandler(appState);
	    		xr.setContentHandler(myExampleHandler);

	    		/* Parse the xml-data from our URL. */
	    		//String test = "<register_response result=\"signupOK\"></register_response>";
	    		InputSource is = new InputSource(new StringReader(XML));
				//is.setEncoding("UTF-8");
				
	    		xr.parse(is);
	    		/* Parsing has finished. */
	    		
	    		
	    		if(myExampleHandler.NetworkError==true)
	    		{
	    			
	    			DisplayNetworkError();
		    		return;
	    		}
	    		
	    		if(myExampleHandler.IsSchemaError())
	    		{
	    			DisplaySchemaError(myExampleHandler.CompatReleaseRequired);
	    			return;
	    		}
	    		if(myExampleHandler.signonResponse.equals("signon_ok"))
	    	    {	
	    			/*
	    			Toast Err = Toast.makeText(appState.getApplicationContext(),
	    					getString(R.string.fc_logged_in_ok),
							Toast.LENGTH_SHORT);
	    			Err.show();
	    			*/
	    			// Save the users login state to the database (in case they switched user)
	    			// Since we were having some issues here, only save them if they have some data ... sadly cannot do for auto login
	    			if(EmailAddress.length()>0)
	    			{
	    				appState.SetLoginEmail(EmailAddress);
	    			}
	    			if(Password.length()>0)
	    			{
	    				appState.SetPassword(Password);
	    			}
	    			// Cant fix this one (Login Automatically)
	    			appState.SetLoginAutomatically(LoginAutomatically);
	    			appState.ClearAllDownloadedData(); // This is our chance to make sure a complete refresh is done
	    			appState.ClearCurrentDrinksOrder(); // Also done in ClearAllDownloadedData
	    			// Start the ItemsList activity
	    			Intent intent = new Intent();
	    			intent.setClassName(this, FreewayCoffeeItemListView.class.getName());
	    			startActivity(intent);
	    	    }
	    		else
	    		{
	    			showDialog(LOGIN_FAIL_DIALOG);
	    			/*
	    			Toast Err = Toast.makeText(appState.getApplicationContext(),
	    										getString(R.string.fc_login_not_successful),	    							
	    										Toast.LENGTH_SHORT);
	    			Err.show();
	    			*/
	    			
	    		}
	    	}
	    	catch(SAXException e)
	    	{

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
	    
	    @Override
    	public void onConfigurationChanged(Configuration newConfig) 
    	{
    	    super.onConfigurationChanged(newConfig);
    	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	}
	    
	    private void DisplaySchemaError(String CompatReleaseRequired)
	    {
	    	String this_app_ver="";
	    	try
	    	{
	    	    this_app_ver = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
	    	}
	    	catch (NameNotFoundException e)
	    	{
	    	    //Log.v(tag, e.getMessage());
	    	}
	    	SchemaError = getString(R.string.fc_version_error_occurred) + CompatReleaseRequired + " " +
            getString(R.string.fc_version_you_have) + this_app_ver + " " + getString(R.string.fc_please_download_release);
	    	
	    	showDialog(SCHEMA_ERROR_DIALOG);
	    
	    }
	    
	    private void DismissProgress()
	    {
	    	if(SignonProgress!=null)
    		{
    			SignonProgress.dismiss();
    			SignonProgress=null; // So we dont try to dismiss twice if we sign on, sign off then change orientation during next signon ?
    		}
	    }
	    private void DisplayNetworkError()
	    {
	    	DismissProgress();
	    	showDialog(NETWORK_ERROR_DIALOG);
	    	/*
	    	Toast Err = Toast.makeText(appState.getApplicationContext(),
					getString(R.string.fc_network_error),
					Toast.LENGTH_SHORT);
	    	Err.show();
	    	*/
	    }
}
