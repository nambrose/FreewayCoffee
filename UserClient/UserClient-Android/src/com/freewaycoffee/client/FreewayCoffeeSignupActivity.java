package com.freewaycoffee.client;

import java.io.BufferedReader;

import android.text.InputFilter;
import android.util.Log;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler; 
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringReader;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;


public class FreewayCoffeeSignupActivity extends Activity
{

	static final private int MENU_ABOUT = Menu.FIRST;
	static final private int MENU_FEEDBACK = Menu.FIRST+1;
	
	private FreewayCoffeeApp appState;
   
	private String EmailAddress;
	private String Nickname;
	private String Password;
	private boolean LoginAutomatically;
	static final private int PASSWORD_NOT_STRONG_DIALOG=1;
	//FreewayCoffeeSignupAsyncGet AsyncGet;
	FreewayCoffeeSignupAsyncPost AsyncPost;
	ProgressDialog SignupProgress;
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	
        super.onCreate(savedInstanceState);
        
        
        appState = ((FreewayCoffeeApp)getApplicationContext());        
        setContentView(R.layout.signup);
        
        //FILTInputFilter[] NameFilts = new InputFilter[]{appState.GetEditTextInputFilter()};
        
        EditText NicknameView = (EditText)findViewById(R.id.signupnickname_edit);
        //FILTNicknameView.setFilters(NameFilts); 
        
        EditText EmailView = (EditText)findViewById(R.id.signupemail_edit);
        
	    //FILTInputFilter[] Emailfilts = new InputFilter[]{appState.GetEditTextEmailInputFilter()};
	     
	    //FILTEmailView.setFilters(Emailfilts); 
	     
        
        Object retained = getLastNonConfigurationInstance(); 
        if(retained instanceof FreewayCoffeeSignupAsyncPost)
        //if(retained instanceof FreewayCoffeeSignupAsyncGet)
		{
	    	// Get the saved state -- the Async Get with the XML and the ListIndex.
			// WHen we set the new activity, it will call Process XML for us
			//AsyncGet = (FreewayCoffeeSignupAsyncGet)retained;
        	AsyncPost = (FreewayCoffeeSignupAsyncPost)retained;
			showProgressDialog();// This must be before setActivity as that calls ProcessXML which destroys the dialog --- so we dont want to just re-create it again
			//AsyncGet.SetActivity(this);
			AsyncPost.SetActivity(this);
				
		}
	    else
	    {
	     
	    	SignupProgress=null;
	    	//AsyncGet=null;
	    	AsyncPost=null;
	    }
    }
    protected Dialog onCreateDialog (int id, Bundle args)
	 {

		 switch(id)
		 {
		 
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
    	 SignupProgress = ProgressDialog.show(this, "",
    			 getString(R.string.fc_signing_up),
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
    
    private void ClearOldErrors()
    {
    	ClearError(R.id.signupemail_error);
    	ClearError(R.id.signupnickname_error);
    	ClearError(R.id.signuppassword_error);
    	ClearError(R.id.signuppasswordagain_error);
    	
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
	 
	 public void GoToSignon(View v)
	 {
		// Start the Signon activity
		 // We finish() here to avoid a trigger-happy user making an infinite number of activities
		Intent intent = new Intent();
		intent.setClassName(this, FreewayCoffeeSignonActivity.class.getName());
		startActivity(intent);
		finish();
	 }
	 
    public void AttemptSignup(View v)
    {
    	ClearOldErrors();
    	
    	// Validate Email address
    	EditText EmailView = (EditText)findViewById(R.id.signupemail_edit);
    	EmailAddress = EmailView.getText().toString();
    	
    	if(EmailAddress.length()==0)
    	{
    		DisplayError(R.id.signupemail_error,R.string.error_email_cannot_be_empty);
    		return;
    	}
    	
    	// Make sure Nickname is at least not empty
    	EditText NicknameView = (EditText)findViewById(R.id.signupnickname_edit);
    	Nickname = NicknameView.getText().toString();
    	
    	if(Nickname.length()==0)
    	{
    		DisplayError(R.id.signupnickname_error,R.string.error_nickname_cannot_be_empty);
    		return;
    	}
    	
    	// Validate both passwords are non-empty and match
    	EditText PasswordView = (EditText)findViewById(R.id.signuppassword_edit);
    	Password = PasswordView.getText().toString();
    	if(Password.length()==0)
    	{
    		DisplayError(R.id.signuppassword_error,R.string.error_password_cannot_be_empty);
    		return;
    	}
    	
    	EditText PasswordAgainView = (EditText)findViewById(R.id.signuppassword_again_edit);
    	String PasswordAgain = PasswordAgainView.getText().toString();
    	if(PasswordAgain.length()==0)
    	{
    		DisplayError(R.id.signuppasswordagain_error,R.string.error_password_cannot_be_empty);
    		return;
    	}
    	
    	if(FreewayCoffeeXMLHelper.CheckPasswordStrength(Password)!=true)
    	{
    		showDialog(PASSWORD_NOT_STRONG_DIALOG);
    		return;
    	}
    	
    	if(!Password.equals(PasswordAgain))
    	{
    		DisplayError(R.id.signuppasswordagain_error,R.string.error_passwords_do_not_match);
    		return;
    	}
    	
    	
    	LoginAutomatically = true;
    	

    	try
    	{
    		// Make a Get
    		//AsyncGet = new FreewayCoffeeSignupAsyncGet(this,appState);
    		//AsyncGet.execute(appState.MakeSignupURL(EmailAddress,Nickname,Password));
    		AsyncPost = new FreewayCoffeeSignupAsyncPost(this,appState);
    		AsyncPost.execute(appState.MakeSignupHTTPPost(EmailAddress, Nickname,Password));
    		
    	}
    	catch (UnsupportedEncodingException e)
		{
			// TODO
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
    private void UnlinkAsync()
    {
    	if(AsyncPost!=null)
    	{
    		AsyncPost.UnlinkActivity();
    		AsyncPost=null;
    	}
    }
    public void ProcessXMLResult(String XML)
	{
    	//Log.w("FreewayCoffeeDB",XML);
    	/* Get a SAXParser from the SAXPArserFactory. */
    	//AsyncGet=null;
    	if(SignupProgress!=null)
		{
			SignupProgress.dismiss();
			SignupProgress=null; // So we dont try to dismiss twice if we sign on, sign off then change orientation during next signon ?
		}
    	UnlinkAsync();
    	
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
    			finish();	    			
    		}
    		
    		if(myExampleHandler.signupResponse.equals(FreewayCoffeeXMLHelper.SUCCESS_REGISTER_SIGNIN))
    	    {
    			Toast ToastSuc = Toast.makeText(appState.getApplicationContext(),
    					getString(R.string.fc_signed_up_ok),
						Toast.LENGTH_SHORT);
    			ToastSuc.show();
    			// Success. Save the login info in the Local SQLite DB for next signin
    			if(EmailAddress.length()>0)
    			{
    				appState.SetLoginEmail(EmailAddress);
    			}
    			if(Password.length()>0)
    			{
    				appState.SetPassword(Password);
    			}
    			
    			appState.SetLoginAutomatically(LoginAutomatically);
    			appState.ClearAllDownloadedData(); // This is our chance to make sure a complete refresh is done
    			appState.ClearCurrentOrder(); // Also done in ClearAllDownloadedData
    			// Start the ItemsList activity
    			Intent intent = new Intent();
    			intent.setClassName(this, FreewayCoffeeItemListView.class.getName());
    			startActivity(intent);
    			this.finish();
    	    }
    		else
    		{
    			Toast ToastErr = Toast.makeText(appState.getApplicationContext(),
    					getString(R.string.fc_signup_error),
						Toast.LENGTH_SHORT);
    			ToastErr.show();
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
    private void DisplaySchemaError(String CompatReleaseRequired)
    {
    	if(SignupProgress!=null)
		{
			SignupProgress.dismiss();
			SignupProgress=null; // So we dont try to dismiss twice if we sign on, sign off then change orientation during next signon ?
		}
    	String this_app_ver="";
    	try
    	{
    	    this_app_ver = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
    	}
    	catch (NameNotFoundException e)
    	{
    	    //Log.v(tag, e.getMessage());
    	}
    	String Message = getString(R.string.fc_version_error_occurred) + CompatReleaseRequired + " " +
    	                           getString(R.string.fc_version_you_have) + " " +  this_app_ver + getString(R.string.fc_please_download_release);
    	                           
    	Toast Err = Toast.makeText(appState.getApplicationContext(), Message, Toast.LENGTH_LONG);
    	Err.show();
    }
    
    private void DisplayNetworkError()
    {
    	if(SignupProgress!=null)
		{
			SignupProgress.dismiss();
			SignupProgress=null; // So we dont try to dismiss twice if we sign on, sign off then change orientation during next signon ?
		}
    	Toast Err = Toast.makeText(appState.getApplicationContext(),
				getString(R.string.fc_network_error),
				Toast.LENGTH_SHORT);
				Err.show();
    }
}
